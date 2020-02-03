/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services.matcher.quickmatch;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import gov.gtas.services.matcher.quickmatch.QuickMatcherConfig.AccuracyMode;
import gov.gtas.util.DateCalendarUtils;
import org.apache.commons.codec.language.DoubleMetaphone;
import org.apache.commons.codec.language.Nysiis;
import org.apache.commons.text.similarity.JaroWinklerDistance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/*
 *  Do not make this a bean without taking multithreading into account.
 * */
public class MatchingContext {

	private final Logger logger = LoggerFactory.getLogger(MatchingContext.class);

	// Set the parts of names that we should combine for full_name and metaphones
	private final String[] NAME_PARTS = { "first_name", "middle_name", "last_name" };
	private final String[] stringAttributes = { "first_name", "middle_name", "last_name", "GNDR_CD", "NATIONALITY_CD",
			"DOC_CTRY_CD", "DOC_TYP_NM", "DOC_ID" };
	static final DateTimeFormatter DATE_FORMATTER_YYYY_MM_DD = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ROOT);
	public static final int DOB_YEAR_OFFSET = 3;
	private QuickMatcherConfig config;

	public QuickMatcherConfig getConfig() {
		return config;
	}

	/*
	 * The choice of mode determines the subsets of attributes that QuickMatch uses
	 * to match travelers.
	 *
	 * HighRecall : QuickMatch prioritizes finding as many derogatory matches as
	 * possible, at the cost of more false positives. On average, QuickMatch will
	 * suggest more hits, requiring more time from GTAS users to review cases.
	 *
	 * HighPrecision : QuickMatch prioritizes suggesting derogatory matches that are
	 * more likely to be correct, at the cost of missing matches for which there is
	 * less evidence. On average, QuickMatch will suggest fewer hits, but GTAS users
	 * will spend less time on incorrect cases.
	 *
	 * Balanced : QuickMatch suggests a high fraction of the true derogatory matches
	 * while requiring a reasonable amount of GTAS users’ time to review them.
	 *
	 * BalancedWithTextDistance : In addition to the Balanced algorithm, QuickMatch
	 * combines the text distance of names with matches on other attributes. This
	 * extra algorithm finds more true hits than Balanced mode alone, for some
	 * queries where Balanced mode may struggle. On other queries, though, it simple
	 * returns hits that Balanced mode already found.
	 *
	 * gtasDefault: The default:
	 *
	 */
	private String accuracyMode;

	private List<List<String>> matchClauses;

	// Only used for hard-coded clauses in Balanced accuracyMode
	private final JaroWinklerDistance jaroWinklerDistance = new JaroWinklerDistance();

	private float jaroWinklerThreshold = 0.95f;
	private int dobYearOffset = DOB_YEAR_OFFSET;

	// Representations of derog list
	private List<HashMap<String, String>> derogList;

	@SuppressWarnings("unused")
	private Map<String, ArrayList<HashMap<String, String>>> derogListByClause;

	private Map<String, Map<String, Set<String>>> clauseValuesToDerogIds;

	public MatchingContext() {
		this.config = new QuickMatcherConfig();
	}

	public void setJaroWinklerThreshold(float jaroWinklerThreshold) {
		this.jaroWinklerThreshold = jaroWinklerThreshold;
	}

	public void setDobYearOffset(int dobYearOffset) {
		this.dobYearOffset = dobYearOffset;
	}

	public float getJaroWinklerThreshold() {
		return jaroWinklerThreshold;
	}

	public int getDobYearOffset() {
		return dobYearOffset;
	}

	// For normal operation: get accuracyMode from config
	public void initialize(final List<HashMap<String, String>> watchListItems) {
		this.initializeConfig();
		this.initialize(watchListItems, this.config.getAccuracyMode());
	}

	// For testing: force an accuracyMode
	public void initialize(final List<HashMap<String, String>> watchListItems, final String accuracyMode) {
		initializeConfig();

		this.accuracyMode = accuracyMode;
		this.matchClauses = config.getClausesForAccuracyMode(accuracyMode);

		this.derogList = watchListItems;
		this.derogList = this.prepareAttributes(this.derogList);
		/**
		 * Split the list into sets where each matchClause applies; Then, for each
		 * query, we need only apply a clause to derog records where it is valid.
		 */

		this.derogListByClause = splitBatchByClause(this.derogList, this.matchClauses);
		this.clauseValuesToDerogIds = mapClauseValuesToDerogIds(this.derogList, this.matchClauses);
	}

	private void initializeConfig() {

		ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

		try {
			this.config = mapper.readValue(
					Thread.currentThread().getContextClassLoader().getResourceAsStream("QMConfig.yaml"),
					QuickMatcherConfig.class);
		} catch (IOException e) {
			//
			logger.error(e.getMessage());
		}
	}

	private static Map<String, ArrayList<HashMap<String, String>>> splitBatchByClause(
			List<HashMap<String, String>> derogList, List<List<String>> matchClauses) {
		String clausekey;
		Boolean hasClause;
		Map<String, ArrayList<HashMap<String, String>>> batchByClause = new HashMap<>();
		for (List<String> clause : matchClauses) {
			clausekey = clause.toString();
			// Find derog records to which this clause applies
			ArrayList<HashMap<String, String>> subList = new ArrayList<>();
			for (HashMap<String, String> rec : derogList) {
				hasClause = true;
				for (String attribute : clause) {
					if ((!rec.containsKey(attribute)) || rec.get(attribute).equals("")) {
						hasClause = false;
						break;
					}
				}
				if (hasClause) {
					subList.add(rec);
				}
			}
			batchByClause.put(clausekey, subList);
		}
		return batchByClause;
	}

	private static String concatClauseValues(Map<String, String> record, List<String> clause) {
		StringBuilder builder = new StringBuilder(32);
		for (String attribute : clause) {
			if (record.containsKey(attribute)) {
				builder.append(record.get(attribute));
			}
		}
		return builder.toString();
	}

	private static Map<String, Map<String, Set<String>>> mapClauseValuesToDerogIds(
			List<HashMap<String, String>> derogList, List<List<String>> matchClauses) {
		String clauseKey;
		Map<String, Map<String, Set<String>>> clauseValuesToDerogIds = new HashMap<>();
		String concatenated;
		for (List<String> clause : matchClauses) {
			clauseKey = clause.toString();
			// Find derog records to which this clause applies
			HashMap<String, Set<String>> valuesToDerogIds = new HashMap<>();
			for (HashMap<String, String> rec : derogList) {
				concatenated = concatClauseValues(rec, clause);
				if (!concatenated.equals("")) {
					// All clause attributes had values
					if (valuesToDerogIds.containsKey(concatenated)) {
						valuesToDerogIds.get(concatenated).add(rec.get("derogId"));
					} else {
						Set<String> derogIds = new HashSet<>();
						derogIds.add(rec.get("derogId"));
						valuesToDerogIds.put(concatenated, derogIds);
					}
				}
			}
			clauseValuesToDerogIds.put(clauseKey, valuesToDerogIds);
		}
		return clauseValuesToDerogIds;
	}

	public MatchingResult match(List<HashMap<String, String>> travelers, Set<String> foundDerogIds) {

		// Rename attributes
		this.prepareAttributes(travelers);

		// Dictionary for match responses, so that each traveler has a single
		// response object that grows as hits are found from different representations
		HashMap<String, DerogResponse> responses = new HashMap<>();

		// Match each queried traveler
		// Travelers will appear once for each document and each citizenship in their
		// query record.
		// If same traveler appears again, find its original response and add
		// the new derog hits
		String gtasId;
		DerogResponse thisResponse;
		for (HashMap<String, String> trav : travelers) {
			thisResponse = matchTraveler(trav, foundDerogIds);
			gtasId = thisResponse.getGtasId();
			if (responses.containsKey(gtasId)) {
				// Add any new hits to the existing response object
				responses.get(gtasId).addDerogIds(thisResponse.getDerogIds());
			} else {
				// Create a new response object in the dictionary
				responses.put(thisResponse.getGtasId(), thisResponse);
			}
		}

		// Check that each queried traveler has a response
		for (HashMap<String, String> trav : travelers) {
			if (!responses.containsKey(trav.get("gtasId"))) {
				logger.warn("Valid traveler with gtasId {} was dropped from response list.", trav.get("gtasId"));
			}
		}

		// Count hits
		int totalHits = 0;
		for (DerogResponse resp : responses.values()) {
			totalHits += resp.getDerogIds().size();
		}

		return new MatchingResult(totalHits, responses);

	}

	private DerogResponse matchTraveler(HashMap<String, String> traveler, Set<String> foundDerogIds) {

		// DerogHits to add to returned DerogResponse
		ArrayList<DerogHit> derogHits = new ArrayList<>();
		// Use set to dedup derogIds

		// For each match clause, compare the input traveler to each derog record with
		// that clause
		String clauseAsString = "";
		String concatenated;

		Set<String> clauseHits = new HashSet<>();
		Map<String, String> clauseNameMap = new HashMap<>();
		for (List<String> clause : matchClauses) {

			// Find derog hits on this clause
			clauseAsString = clause.toString();
			Map<String, Set<String>> derogForClause = this.clauseValuesToDerogIds.get(clauseAsString);

			boolean exactlyMatched = false;
			boolean partiallyMatched = false;
			boolean jaroWinklerDistanceMatched = false;

			concatenated = concatClauseValues(traveler, clause);

			if ((!concatenated.equals("")) && derogForClause.containsKey(concatenated)) {
				// Get derogIds with same concatenated values for the clause
				clauseHits = derogForClause.get(concatenated);
				logger.info("gtasId {} matches derogIds {} on clause {}", traveler.get("gtasId"), clauseHits, clause);
				logger.debug("Matched string: {}", concatenated);
				exactlyMatched = true;
				for (String derogId : clauseHits) {
					if (!foundDerogIds.contains(derogId)) {
						clauseNameMap.put(derogId, "an exact name match");
					}
				}
			} else {
				clauseHits = new HashSet<>();
			}

			if (this.accuracyMode.equals(AccuracyMode.GTAS_DEFAULT.toString())) {

				for (HashMap<String, String> derogRecord : this.derogList) {
					if (!foundDerogIds.contains(derogRecord.get("derogId"))) {

						int offset = calculateYearOffset(traveler.get("DOB_Date"), derogRecord.get("DOB_Date"));

						if (offset <= dobYearOffset) {
							// calculate Jaro Winkler distance
							Double distance = this.goodTextDistance(traveler.get("full_name"),
									derogRecord.get("full_name"));

							if (traveler.get("metaphones").equals(derogRecord.get("metaphones"))
									&& distance >= jaroWinklerThreshold) {

								logger.debug(
										"A Double Metaphone match and a Jaro Winkler distance hit of {} for traveler={}, derog={}.",
										distance, traveler.get("full_name"), derogRecord.get("full_name"));

								if (offset <= dobYearOffset) {

									logger.debug(
											"There was a match on date of birth (YEAR only) with an offset of {}, traveler={}, derog={}",
											offset, traveler.get("DOB_Date"), derogRecord.get("DOB_Date"));

									String derogId = derogRecord.get("derogId");
									clauseHits.add(derogId);
									clauseNameMap.put(derogId, derogRecord.get("full_name"));
									partiallyMatched = true;
								}

							} else if (distance >= jaroWinklerThreshold
									&& isSameDOBYearMonthDay(traveler.get("DOB_Date"), derogRecord.get("DOB_Date"))) {

								logger.debug(
										"There was a match on exact date of {} and a Jaro Winkler distance hit of {} for traveler={}, derog={}.",
										traveler.get("DOB_Date"), distance, traveler.get("full_name"),
										derogRecord.get("full_name"));

								// It is a hit
								String derogId = derogRecord.get("derogId");
								clauseHits.add(derogId);
								clauseNameMap.put(derogId, derogRecord.get("full_name"));
								jaroWinklerDistanceMatched = true;
							}
						}
					}
				}
			}

			// Build DerogHits only for new derogIds for this traveler
			for (String thisDerogId : clauseHits) {
				if (!foundDerogIds.contains(thisDerogId)) {
					String ruleDescription = "Partial hit on " + clauseNameMap.get(thisDerogId);
					if (exactlyMatched) {
						derogHits.add(new DerogHit(thisDerogId, clauseAsString, 1f, ruleDescription));
					} else if (partiallyMatched) {
						derogHits.add(new DerogHit(thisDerogId, clauseAsString, 0.9f, ruleDescription));
					} else if (jaroWinklerDistanceMatched)
						derogHits.add(new DerogHit(thisDerogId, clauseAsString, 0.80f, ruleDescription));
					else
						derogHits.add(new DerogHit(thisDerogId, clauseAsString, 0.7f, ruleDescription));
					foundDerogIds.add(thisDerogId);
				}
			}
		}

		if (this.accuracyMode.equals("BalancedWithTextDistance")) {
			for (HashMap<String, String> derogRecord : this.derogList) {
				// Text distance is expensive, so don't do it for derogIds we've already hit
				if (!foundDerogIds.contains(derogRecord.get("derogId"))) {

					// Hard-coding these supporting attributes for now
					if (traveler.get("DOB_Date").equals(derogRecord.get("DOB_Date"))
							&& this.goodTextDistance(traveler.get("full_name"),
									derogRecord.get("full_name")) >= jaroWinklerThreshold) {

						logger.info("Text distance hit for traveler={}, derog={}, and DOB_Date.",
								traveler.get("full_name"), derogRecord.get("full_name"));
						clauseHits.add(derogRecord.get("derogId"));

					} else if (traveler.get("NATIONALITY_CD").equals(derogRecord.get("NATIONALITY_CD"))
							&& this.goodTextDistance(traveler.get("full_name"),
									derogRecord.get("full_name")) >= jaroWinklerThreshold) {

						logger.info("Text distance hit for traveler={}, derog={}, and NATIONALITY_CD.",
								traveler.get("full_name"), derogRecord.get("full_name"));
						clauseHits.add(derogRecord.get("derogId"));
					}
				}
			}

			// Dedup the hits again
			clauseAsString = "[full_name text distance, DOB_Date OR NATIONALITY_CD]";
			for (String thisDerogId : clauseHits) {
				if (!foundDerogIds.contains(thisDerogId)) {
					derogHits.add(new DerogHit(thisDerogId, clauseAsString, 1,
							this.derogList.get(0).get(DerogHit.WATCH_LIST_NAME)));
					foundDerogIds.add(thisDerogId);
				}
			}
		}

		if (derogHits.isEmpty()) {
			logger.debug("gtasId {} has no matches.", traveler.get("gtasId"));
		}

		return new DerogResponse(traveler.get("gtasId"), derogHits);
	}

	/**
	 * 
	 * @param travelerDate
	 * @param derogDate
	 * @return
	 * @throws ParseException
	 */
	private int calculateYearOffset(String travelerDate, String derogDate) {
		try {
			int travlerYear = DateCalendarUtils.getYearOfDate(travelerDate, DATE_FORMATTER_YYYY_MM_DD);
			int derogYear = DateCalendarUtils.getYearOfDate(derogDate, DATE_FORMATTER_YYYY_MM_DD);
			return Math.abs(travlerYear - derogYear);
		} catch (Exception e) {
			logger.error("QuickMatch: failed to parse date of birth {}", e.getMessage());
		}
		return Integer.MAX_VALUE;
	}

	/**
	 * 
	 * Compares the two Dates for equality
	 * 
	 * @param travelerDate
	 * @param derogDate
	 * @return
	 */
	private boolean isSameDOBYearMonthDay(String travelerDate, String derogDate) {

		try {
			LocalDate travelerDob = DateCalendarUtils.parseLocalDate(travelerDate, DATE_FORMATTER_YYYY_MM_DD);
			LocalDate derogDob = DateCalendarUtils.parseLocalDate(derogDate, DATE_FORMATTER_YYYY_MM_DD);
			return travelerDob.compareTo(derogDob) == 0;
		} catch (Exception e) {
			logger.error("QuickMatch: failed to parse date of birth");
		}
		return false;
	}

	private Double goodTextDistance(String name1, String name2) {
		return this.jaroWinklerDistance.apply(name1, name2);
	}

	// Attribute renames and cleansing
	private List<HashMap<String, String>> prepareAttributes(List<HashMap<String, String>> parsedRecords)
			throws IllegalArgumentException {

		DoubleMetaphone dmeta = new DoubleMetaphone();
		Nysiis nysiis = new Nysiis(false);

		// If gtasId and derogId are renamed to same thing, only change the appropriate
		// one.
		// but leave the original renames list untouched.
		HashMap<String, String> batchRenames = new HashMap<>(config.getAttributeRenames());
		// Begin per-record renames and pre-processing
		// Use iterator so we can remove records with critical errors
		Iterator<HashMap<String, String>> recordIterator = parsedRecords.iterator();
		Map<String, String> rec;
		while (recordIterator.hasNext()) {
			rec = recordIterator.next();
			// First rename attributes to align with internal processing
			for (String defaultAttribute : batchRenames.keySet()) {
				String renamed = batchRenames.get(defaultAttribute);
				if (rec.containsKey(renamed)) {
					String value = rec.remove(renamed);
					// Method remove returns previous value
					// All present, null atributes are set to empty string (for later hashing)
					rec.put(defaultAttribute, value == null ? "" : value);
				}
			}

			// Standardize case
			for (String attribute : stringAttributes) {
				if (rec.containsKey(attribute) && rec.get(attribute) != null) {
					rec.put(attribute, rec.get(attribute).toUpperCase());
				}
			}

			// If an attribute is missing, set its derived attributes to the empty string
			// Regex cleansing
			for (String attr : rec.keySet()) {
				if (rec.get(attr) != null)
					rec.put(attr, rec.get(attr).replaceAll(config.getDerogFilterOutRegex(), ""));
			}

			// Compute dob_year as string for exact matching
			// By now, no attribue should be null
			if (rec.containsKey("DOB_Date") && !rec.get("DOB_Date").isEmpty()) {
				String[] splitDob = rec.get("DOB_Date").split("-");
				if (splitDob.length == 3) { // Expect YYYY-MM-DD
					rec.put("dob_year", splitDob[0]);
				}
			}

			// // Gender
			// if (rec.containsKey("GNDR_CD")) {
			// String gender = rec.get("GNDR_CD");
			// if (!(gender.equals("M") || gender.equals("F"))) {
			// rec.put("GNDR_CD", "");
			// }
			// } else {
			// rec.put("GNDR_CD", "");
			// }

			// Build full_name and metaphones from name parts.
			// Method dmeta.doublemetaphone() accepts no spaces,
			// so apply it to name parts individually.
			String full_name = "";
			String partial_name_metaphones = "";
			String metaphones = "";
			for (String namePart : NAME_PARTS) {
				String name = "";
				if (rec.containsKey(namePart))
					name = rec.get(namePart);
				if (name != null && !name.equals("")) {
					full_name += name + " ";

					String metaphone = computeMetaphones(dmeta, name);
					metaphones += metaphone;
					if (namePart.equals("first_name") || namePart.equals("last_name")) {
						if (name.split("\\s+")[0].equals(name)) {
							partial_name_metaphones += metaphone;
						} else {
							partial_name_metaphones += computeMetaphones(dmeta, name.split("\\s+")[0]);
						}
					}
				} else {
					rec.put(namePart, "");
				}
			}

			full_name = full_name.trim();
			metaphones = metaphones.trim();
			rec.put("full_name", full_name);
			rec.put("metaphones", metaphones);
			rec.put("partial_metaphones", partial_name_metaphones);
			rec.put("nysiis", nysiis.encode(rec.get("full_name")));
		}
		return parsedRecords;
	}

	private static String computeMetaphones(DoubleMetaphone dmeta, String name) {
		StringBuilder builder = new StringBuilder(32);
		for (String part : name.trim().split("\\s+")) {
			builder.append(dmeta.doubleMetaphone(part));
			builder.append(" ");
		}
		return builder.toString().trim();
	}
}

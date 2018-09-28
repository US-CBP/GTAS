/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services.matcher.quickmatch;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
public class QuickMatcherConfig {

	enum AccuracyMode {

		HIGH_RECALL("HighRecall"), BALANCED("Balanced"), BALANCED_WITH_TEXT_DISTANCE(
				"BalancedWithTextDistance"), HIGH_PRECISION("HighPrecision"), GTAS_DEFAULT("GtasDefault");

		private String mode;

		private AccuracyMode(final String value) {
			this.mode = value;
		}

		@Override
		public String toString() {
			//
			return this.mode;
		}

	};

	/**
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

	/**
	 * Regular expression of patterns that QuickMatch will remove from any traveler
	 * attribute in the derogatory list or match queries. Note that by default, the
	 * expression beings with ^ and ends with $, so that QuickMatch will only remove
	 * a pattern if the entire string matches that pattern.
	 */
	private String derogFilterOutRegex;

	private List<List<String>> highRecall;
	private List<List<String>> balanced;
	private List<List<String>> highPrecision;
	private List<List<String>> gtasDefault;

	public List<List<String>> getGtasDefault() {
		return gtasDefault;
	}

	/**
	 * A dictionary of pairs “ expected_attribute_name: provided_attribute_name ”.
	 * Change the provided_attribute_name to match the attribute name that GTAS will
	 * send. Not all attributes are required, but they are highly recommended for
	 * the Accuracy Modes to function as expected.
	 */
	private HashMap<String, String> attributeRenames;

	public QuickMatcherConfig() {
	}

	public String getAccuracyMode() {
		return accuracyMode;
	}

	public String getDerogFilterOutRegex() {
		return derogFilterOutRegex;
	}

	public List<List<String>> getHighRecall() {
		return highRecall;
	}

	public List<List<String>> getBalanced() {
		return balanced;
	}

	public List<List<String>> getHighPrecision() {
		return highPrecision;
	}

	public List<List<String>> getClausesForAccuracyMode(String mode) {
		HashMap<String, List<List<String>>> modes = new HashMap<>();
		// We allow the clauses in the modes to be edited,
		// so we load their definitions from the config file
		modes.put(AccuracyMode.HIGH_RECALL.toString(), highRecall);
		modes.put(AccuracyMode.BALANCED.toString(), balanced);
		modes.put(AccuracyMode.BALANCED_WITH_TEXT_DISTANCE.toString(), balanced);
		modes.put(AccuracyMode.HIGH_PRECISION.toString(), highPrecision);
		modes.put(AccuracyMode.GTAS_DEFAULT.toString(), gtasDefault);
		return modes.get(mode);
	}

	public HashMap<String, String> getAttributeRenames() {
		// Only keep actual renames, since we apply to every traveler query record.
		HashMap<String, String> nontrivialRenames = new HashMap<>(this.attributeRenames);
		Iterator<String> iterator = nontrivialRenames.keySet().iterator();
		while (iterator.hasNext()) {
			String orig = iterator.next();
			if (orig.equals(nontrivialRenames.get(orig))) {
				iterator.remove();
			}
		}
		return nontrivialRenames;
	}
}

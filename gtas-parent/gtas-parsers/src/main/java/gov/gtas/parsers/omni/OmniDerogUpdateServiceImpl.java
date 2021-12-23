/*
 *  All Application code is Copyright 2020, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *  Please see LICENSE.txt for details.
 */
package gov.gtas.parsers.omni;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.gtas.model.HitDetail;
import gov.gtas.model.HitMaker;
import gov.gtas.model.Flight;
import gov.gtas.model.Passenger;
import gov.gtas.model.lookup.HitCategory;
import gov.gtas.parsers.omni.model.OmniMessageType;
import gov.gtas.parsers.omni.model.OmniDerogHit;
import gov.gtas.repository.HitDetailRepository;
import gov.gtas.repository.PassengerRepository;
import gov.gtas.parsers.omni.jms.OmniMessageSender;
import gov.gtas.model.Document;
import gov.gtas.model.Flight;
import gov.gtas.model.Passenger;
import gov.gtas.model.PassengerDetails;
import gov.gtas.parsers.omni.model.OmniDerogListEntry;
import gov.gtas.parsers.omni.model.OmniDocument;
import gov.gtas.parsers.omni.model.OmniPassenger;
import gov.gtas.parsers.omni.model.OmniRawProfile;
import gov.gtas.parsers.omni.model.OmniDerogPassengerUpdate;
import gov.gtas.parsers.omni.model.OmniLookoutCategory;
import gov.gtas.services.FlightService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.concurrent.ConcurrentHashMap;

@Component
@ConditionalOnProperty(prefix = "omni", name = "enabled")
public class OmniDerogUpdateServiceImpl implements OmniDerogUpdateService {
	public static final Long OMNI_LOOKOUT_CAT1_BIT_MASK = 2L;
	public static final Long OMNI_LOOKOUT_CAT2_BIT_MASK = 4L;
	public static final Long OMNI_LOOKOUT_CAT3_BIT_MASK = 8L;
	public static final Long OMNI_LOOKOUT_CAT4_BIT_MASK = 16L;
	public static final Long OMNI_LOOKOUT_CAT5_BIT_MASK = 32L;
	public static final Long OMNI_LOOKOUT_CAT6_BIT_MASK = 64L;

	public static final String GTAS_HIT_CATEGORY_GENERAL = "General";
	public static final String GTAS_HIT_CATEGORY_TERRORISM = "Terrorism";
	public static final String GTAS_HIT_CATEGORY_WORLD_HEALTH = "World Health";
	public static final String GTAS_HIT_CATEGORY_FEDERAL_LAW_ENFORCEMENT = "Federal Law Enforcement";
	public static final String GTAS_HIT_CATEGORY_LOCAL_LAW_ENFORCEMENT = "Local Law Enforcement";

	public static final String NO_PAYLOAD = "";

	private static ObjectMapper objectMapper = new ObjectMapper();

	private static final Logger logger =
			LoggerFactory.getLogger(OmniDerogUpdateServiceImpl.class);

	private final String PASSENGER_WATCHLIST_NAME = "Passenger";

	@Value("${omni.derog-replace.batchSize}")
	private Integer batchSize;

	@Autowired
	PassengerRepository passengerRepository;

	@Autowired
	HitDetailRepository hitDetailRepository;

	OmniAdapter omniAdapter;

	OmniMessageSender omniMessageSender;

	public OmniDerogUpdateServiceImpl(
			OmniMessageSender omniMessageSender,
			OmniAdapter omniAdapter,
			HitDetailRepository hitDetailRepository,
			PassengerRepository passengerRepository) {
		this.omniMessageSender = omniMessageSender;
		this.omniAdapter = omniAdapter;
		this.hitDetailRepository = hitDetailRepository;
		this.passengerRepository = passengerRepository;
	}

	@Override
	public void updateOmniDerogPassengers(Set<Long> flightIds) {
		// logger.info("=====> Entering updateOmniDerogPassengers()");
		try {
			int batchIndex = 0;
			int upperBound = batchSize.intValue();

			Pageable pageable = PageRequest.of(batchIndex, upperBound);

			Slice<HitDetail> slice = null;

			ConcurrentHashMap<String, Long> derogCategoriesMap = new ConcurrentHashMap<>();
			ConcurrentHashMap<String, OmniPassenger> omniPassengersMap = new ConcurrentHashMap<>();
			List<OmniRawProfile> omniRawProfileList = new ArrayList<>();
			List<OmniLookoutCategory> omniLookoutCategoryList = new ArrayList<>();

			Iterator<Map.Entry<String, OmniPassenger>> entryIt = null;

			// Retrieve the hit details that occured since the last run
			while (true) {

				List<HitDetail> hitDetailList = new ArrayList<>();

				slice = hitDetailRepository.findAllByFlightIds(flightIds, pageable);

				int number = slice.getNumber();
				int numberOfElements = slice.getNumberOfElements();
				int size = slice.getSize();

				hitDetailList.addAll(slice.getContent());

				// logger.info("slice description - page number {}, numberOfElements: {}, size: {}",number, numberOfElements, size);

				for (HitDetail hitDetail: hitDetailList) {
					// String jsonHitDetail = objectMapper.writer().writeValueAsString(hitDetail);
					// logger.info(" ========= Processing Hit Detail={} ========", jsonHitDetail);
					Long passengerId = hitDetail.getPassengerId();
					String passengerIdStr = Long.toString(passengerId);
					Passenger passenger = passengerRepository.getFullPassengerById(passengerId);
					Flight flight = hitDetail.getFlight();
					Long categoryBitMask = (Long) derogCategoriesMap.getOrDefault(passengerIdStr, 0L);
					HitMaker hitMaker = hitDetail.getHitMaker();
					HitCategory hitCategory = hitMaker.getHitCategory();
					String categoryName = hitCategory.getName();

					switch(categoryName) {
						case GTAS_HIT_CATEGORY_GENERAL:
							categoryBitMask |= OMNI_LOOKOUT_CAT1_BIT_MASK;
							break;

						case GTAS_HIT_CATEGORY_TERRORISM:
							categoryBitMask |= OMNI_LOOKOUT_CAT2_BIT_MASK;
							break;

						case GTAS_HIT_CATEGORY_WORLD_HEALTH:
							categoryBitMask |= OMNI_LOOKOUT_CAT3_BIT_MASK;
							break;

						case GTAS_HIT_CATEGORY_FEDERAL_LAW_ENFORCEMENT:
							categoryBitMask |= OMNI_LOOKOUT_CAT4_BIT_MASK;
							break;

						case GTAS_HIT_CATEGORY_LOCAL_LAW_ENFORCEMENT:
							categoryBitMask |= OMNI_LOOKOUT_CAT5_BIT_MASK;
							break;
						default:
							categoryBitMask |= OMNI_LOOKOUT_CAT6_BIT_MASK;
							break;
					}

					OmniPassenger omniPassenger = omniAdapter.convertPassengerToOmniRawProfile(flight, passenger);
					derogCategoriesMap.put(passengerIdStr, categoryBitMask);
					omniPassengersMap.put(passengerIdStr, omniPassenger);
				}

				if (!slice.hasNext()) {
					break;
				}

				pageable = slice.nextPageable();
			}

			entryIt = omniPassengersMap.entrySet().iterator();

			// Now, prepare the payload to send to Kaizen
			// String jsonStream = "";

			while (entryIt.hasNext()) {
				Map.Entry<String, OmniPassenger> entry = entryIt.next();

				String passengerIdStr = (String) entry.getKey();
				OmniPassenger omniPassenger = (OmniPassenger) entry.getValue();

				// jsonStream = objectMapper.writer().writeValueAsString(omniPassenger);
				// logger.info(" ========= Current OmniPassenger={}, passengerIdStr={}", jsonStream, passengerIdStr);

				omniRawProfileList.add(omniPassenger.getOmniRawProfile());

				Long lookoutCategoryBitMask = (Long) derogCategoriesMap.get(passengerIdStr);

				// logger.info(" ========= Current lookoutCategoryBitMask={}, passengerIdStr={}", lookoutCategoryBitMask, passengerIdStr);
				OmniLookoutCategory omniLookoutCategory = new OmniLookoutCategory();
				omniLookoutCategory.setPassengerNumber(Long.parseLong(passengerIdStr));
				omniLookoutCategory.setLookoutCategoryBitMask(lookoutCategoryBitMask);
				omniLookoutCategoryList.add(omniLookoutCategory);
			}

			OmniDerogPassengerUpdate omniDerogPassengerUpdate = new OmniDerogPassengerUpdate();
			omniDerogPassengerUpdate.setProfiles(omniRawProfileList);
			omniDerogPassengerUpdate.setLookoutCategories(omniLookoutCategoryList);

			// String jsonDerogCategoriesMap = objectMapper.writer().writeValueAsString(derogCategoriesMap);
			// logger.info(" ========= Passengers Derog Category Labels Map={}", jsonDerogCategoriesMap);

			// String jsonOmniPassengersMap = objectMapper.writer().writeValueAsString(omniPassengersMap);
			// logger.info(" ========= Omni Passengers Map={}", jsonOmniPassengersMap);

			// String jsonOmniDerogPassengerUpdate = objectMapper.writer().writeValueAsString(omniDerogPassengerUpdate);
			// logger.info(" ========= Omni Derog Passengers Update={}", jsonOmniDerogPassengerUpdate);

			// Send the message to Omni via Kaizen
			if (omniRawProfileList.size() > 0) {
				omniMessageSender.sendMessageToOmni(OmniMessageType.UPDATE_DEROG_CATEGORY, omniDerogPassengerUpdate);
			}

		} catch (Exception ex) {
			logger.error("updateOmniDerogPassengers() - Got an exception: ", ex);
		}
	}
}

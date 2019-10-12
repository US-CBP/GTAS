package gov.gtas.services;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import gov.gtas.config.CachingConfig;
import gov.gtas.config.CommonServicesConfig;
import gov.gtas.enumtype.EntityEnum;
import gov.gtas.enumtype.WatchlistEditEnum;
import gov.gtas.json.JsonServiceResponse;
import gov.gtas.model.watchlist.json.WatchlistItemSpec;
import gov.gtas.model.watchlist.json.WatchlistSpec;
import gov.gtas.model.watchlist.json.WatchlistTerm;
import gov.gtas.querybuilder.mappings.DocumentMapping;
import gov.gtas.querybuilder.mappings.PassengerMapping;
import gov.gtas.services.security.UserData;
import gov.gtas.services.security.UserService;
import gov.gtas.svc.WatchlistService;

public class WatchListLoaderService {
	private static final Logger logger = LoggerFactory.getLogger(WatchListLoaderService.class);
	private static String FILE_TO_PARSE = null; // "C:\\TEST\\wl-pass.csv";

	public static void main(String[] args) {
		File file = null;
		Scanner scanIn = new Scanner(System.in);
		if (args != null && args.length >= 1) {
			FILE_TO_PARSE = args[0];
		} else {
			logger.info("Enter absolute file Name to parse and load watchlist (Example:C:/TEST/wlist.csv): ");
			FILE_TO_PARSE = scanIn.nextLine();
		}
		if (StringUtils.isNoneBlank(FILE_TO_PARSE)) {

			boolean check = true;
			while (check) {
				File toParse = new File(FILE_TO_PARSE);
				boolean isFile = toParse.isFile();

				if (isFile) {
					BufferedReader br = null;
					String line = "";
					String cvsSplitBy = ",";
					file = toParse;
					try {
						br = new BufferedReader(new FileReader(toParse));
						while ((line = br.readLine()) != null) {
							// use comma as separator
							String[] tokens = line.split(cvsSplitBy);
							if (tokens.length < 10) {
								logger.info("****************************************************************");
								logger.info(" INVALID FILE EXPECTED FIELDS IN FILE = 10 ");
								logger.info("" + toParse + " Contains " + tokens.length + " Fields to parse");
								logger.info("                    EXITING     ");
								logger.info("****************************************************************");
								System.exit(-1);
							}
							break;
						}

					} catch (FileNotFoundException e) {
						logger.error("file to parse not found!", e);
					} catch (IOException e) {
						logger.error("error processing file!", e);
					} finally {
						if (br != null) {
							try {
								br.close();
							} catch (IOException e) {
								logger.error("error closing br", e);
							}
						}
					}
					check = false;
					logger.info("Valid file to parse : " + FILE_TO_PARSE);
					scanIn.close();
				} else {
					logger.info("\nPlease enter valid file name to parse and load " + FILE_TO_PARSE
							+ " is not a valid file");
					logger.info("\nPlease enter the filename again: ");
					FILE_TO_PARSE = scanIn.nextLine();
				}

			}
			ConfigurableApplicationContext ctx = new AnnotationConfigApplicationContext(CommonServicesConfig.class,
					CachingConfig.class);
			parseAndLoadWatchList(ctx, file);
			com.hazelcast.core.Hazelcast.shutdownAll();

		}
	}

	private static void parseAndLoadWatchList(ConfigurableApplicationContext ctx, File f) {
		CSVParser parser = null;

		try {
			UserService userService = (UserService) ctx.getBean(UserService.class);
			WatchlistService wlService = (WatchlistService) ctx.getBean(WatchlistService.class);
			UserData user = userService.findById("gtas");
			logger.info(user.getUserId());

			parser = new CSVParser(new FileReader(f), CSVFormat.DEFAULT.withHeader());
			if (f.toString().endsWith("xls") || f.toString().endsWith("xlsx")) {
				parser = new CSVParser(new FileReader(f), CSVFormat.EXCEL.withHeader());
			}
			WatchlistSpec ret = new WatchlistSpec(f.getName() + "-P",
					EntityEnum.PASSENGER.getEntityName().toUpperCase());
			WatchlistSpec ret_D = new WatchlistSpec(f.getName() + "-D",
					EntityEnum.DOCUMENT.getEntityName().toUpperCase());

			for (CSVRecord record : parser) {
				List<WatchlistTerm> termList = new ArrayList<>();
				List<WatchlistTerm> termList_D = new ArrayList<>();
				if (StringUtils.isNotBlank(record.get("firstname"))) {
					termList = createWatchListTermList(PassengerMapping.FIRST_NAME.getFieldName(),
							PassengerMapping.FIRST_NAME.getFieldType(), record.get("firstname"), termList);
				}
				if (StringUtils.isNotBlank(record.get("lastname"))) {
					termList = createWatchListTermList(PassengerMapping.LAST_NAME.getFieldName(),
							PassengerMapping.LAST_NAME.getFieldType(), record.get("lastname"), termList);
				}
				if (StringUtils.isNotBlank(record.get("middlename"))) {
					termList = createWatchListTermList(PassengerMapping.MIDDLE_NAME.getFieldName(),
							PassengerMapping.MIDDLE_NAME.getFieldType(), record.get("middlename"), termList);
				}
				if (StringUtils.isNotBlank(record.get("dob"))) {
					String dob_m = record.get("dob").replaceAll("/", "-");
					termList = createWatchListTermList(PassengerMapping.DOB.getFieldName(),
							PassengerMapping.DOB.getFieldType(), dob_m, termList);
				}
				if (StringUtils.isNotBlank(record.get("documentcountry"))) {
					termList_D = createWatchListTermList(DocumentMapping.ISSUANCE_COUNTRY.getFieldName(),
							DocumentMapping.ISSUANCE_COUNTRY.getFieldType(), record.get("documentcountry"), termList_D);
				}
				if (StringUtils.isNotBlank(record.get("documentnumber"))) {
					termList_D = createWatchListTermList(DocumentMapping.DOCUMENT_NUMBER.getFieldName(),
							DocumentMapping.DOCUMENT_NUMBER.getFieldType(), record.get("documentnumber"), termList_D);
				}
				if (StringUtils.isNotBlank(record.get("documenttype"))) {
					termList_D = createWatchListTermList(DocumentMapping.DOCUMENT_TYPE.getFieldName(),
							DocumentMapping.DOCUMENT_TYPE.getFieldType(), record.get("documenttype"), termList_D);
				}
				if (StringUtils.isNotBlank(record.get("coc"))) {
					termList = createWatchListTermList(PassengerMapping.NATIONALITY.getFieldName(),
							PassengerMapping.NATIONALITY.getFieldType(), record.get("coc"), termList);
				}

				// logger.info("%s\t%s\t%s\n", record.get("dob"), record.get("firstname"),
				// record.get("doc"));
				if (!termList.isEmpty()) {
					WatchlistItemSpec spec = new WatchlistItemSpec();
					spec.setAction(WatchlistEditEnum.C.getOperationName());
					spec.setTerms(termList.toArray(new WatchlistTerm[termList.size()]));
					ret.addWatchlistItem(spec);

				}
				if (!termList_D.isEmpty()) {
					WatchlistItemSpec spec_d = new WatchlistItemSpec();
					spec_d.setAction(WatchlistEditEnum.C.getOperationName());
					spec_d.setTerms(termList_D.toArray(new WatchlistTerm[termList_D.size()]));
					ret_D.addWatchlistItem(spec_d);

				}

			}
			JsonServiceResponse resp = wlService.createUpdateDeleteWatchlistItems(user.getUserId(), ret, 1L);
			logger.info("****************************************************************");
			logger.info("Passenger WatchList Saved " + resp.getMessage());
			logger.info("****************************************************************");
			JsonServiceResponse resp1 = wlService.createUpdateDeleteWatchlistItems(user.getUserId(), ret_D, 1L);
			logger.info("****************************************************************");
			logger.info("Document WatchList Saved " + resp1.getMessage());
			logger.info("****************************************************************");
			JsonServiceResponse resp2 = wlService.activateAllWatchlists();
			logger.info("****************************************************************");
			logger.info(" All WatchList items were Activated " + resp2.getMessage());
			logger.info("****************************************************************");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			logger.error("parse load watchlist error!", e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error("parse load watchlist error.", e);
		} finally {
			try {
				parser.close();
			} catch (IOException e) {
				logger.error("parse load watchlist error.", e);
			}
		}
	}

	public static List<WatchlistTerm> createWatchListTermList(String fieldName, String type, String value,
			List<WatchlistTerm> terms) {
		WatchlistTerm passengerTerm = new WatchlistTerm(fieldName, type, value);
		terms.add(passengerTerm);
		return terms;
	}

}

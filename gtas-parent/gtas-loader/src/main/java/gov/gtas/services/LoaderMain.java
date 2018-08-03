/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services;

import static gov.gtas.constant.GtasSecurityConstants.GTAS_APPLICATION_USERID;
import gov.gtas.aws.QueueService;
import gov.gtas.config.CachingConfig;
import gov.gtas.config.CommonServicesConfig;
import gov.gtas.enumtype.AuditActionType;
import gov.gtas.json.AuditActionData;
import gov.gtas.json.AuditActionTarget;
import gov.gtas.parsers.edifact.EdifactLexer;
import gov.gtas.parsers.edifact.Segment;
import gov.gtas.parsers.exception.ParseException;
import gov.gtas.parsers.redisson.model.LedgerLiveObject;
import gov.gtas.parsers.util.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.amazonaws.services.sqs.model.Message;

public class LoaderMain {
    private static Loader loader;

    private enum InputType {
        FILE_LIST, TWO_DIRS, QUEUE
    };

    public static void main(String[] args) {
        if (args.length == 0) {
            printUsage();
            System.exit(0);
        }

        InputType inputType = InputType.FILE_LIST;

        if (args.length == 1 && args[0].startsWith("http")) {
            // queue url
            inputType = InputType.QUEUE;
        } else {
            // a list of files or 2 directories
            File arg1 = new File(args[0]);
            if (arg1.isFile()) {
                inputType = InputType.FILE_LIST;
            } else if (arg1.isDirectory() && args.length == 2) {
                File arg2 = new File(args[1]);
                if (!arg2.isDirectory()) {
                    System.err.println(arg2 + " is not a directory");
                    System.exit(-1);
                }
                inputType = InputType.TWO_DIRS;
            } else {
                System.err.println("Invalid argument(s)");
                printUsage();
                System.exit(-1);
            }
        }

        try (ConfigurableApplicationContext ctx = new AnnotationConfigApplicationContext(
                CommonServicesConfig.class, CachingConfig.class)) {
            loader = ctx.getBean(Loader.class);
            LoaderStatistics stats = new LoaderStatistics();

            switch (inputType) {
            case FILE_LIST:
                processListOfFiles(args, stats);
                break;
            case TWO_DIRS:
                processInputAndOutputDirectories(args, stats);
                break;
            case QUEUE:
                processQueue(args, stats);
                break;
            }

            writeAuditLog(ctx, stats);
            com.hazelcast.core.Hazelcast.shutdownAll();
        }
    }

    private static void processListOfFiles(String[] args, LoaderStatistics stats) {
        // ignore any directories
        for (int i = 0; i < args.length; i++) {
            File f = new File(args[i]);
            if (f.isFile()) {
                processSingleFile(f, stats);
            }
        }
    }

    private static void processInputAndOutputDirectories(String[] args,
            LoaderStatistics stats) {
        File incomingDir = new File(args[0]);
        File outgoingDir = new File(args[1]);
        File[] listOfFiles = incomingDir.listFiles();
        for (int i = 0; i < listOfFiles.length; i++) {
            File f = listOfFiles[i];
            if (f.isFile()) {
                processSingleFile(f, stats);
                f.renameTo(new File(outgoingDir + File.separator + f.getName()));
            }
        }
    }

    private static void processQueue(String[] args, LoaderStatistics stats) {
        String queueName = args[0].substring(args[0].lastIndexOf('/') + 1);
        QueueService sqs = new QueueService(queueName);
        List<Message> messages = sqs.receiveMessages();
        for (Message m : messages) {
            try {
                FileUtils.writeToFile(m.getMessageId(), m.getBody());
                File f = new File(m.getMessageId());
                processSingleFile(f, stats);
                sqs.deleteMessage(m.getReceiptHandle());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Writes the audit log with run statistics.
     * 
     * @param ctx
     *            the spring application context to obtain the audit log
     *            service.
     * @param stats
     *            the statistics bean.
     */
    private static void writeAuditLog(ConfigurableApplicationContext ctx,
            LoaderStatistics stats) {
        try {
            AuditLogPersistenceService auditLogSvc = ctx
                    .getBean(AuditLogPersistenceService.class);
            AuditActionTarget target = new AuditActionTarget(
                    AuditActionType.LOADER_RUN, "GTAS Message Loader", null);
            AuditActionData actionData = new AuditActionData();
            actionData.addProperty("totalFilesProcessed",
                    String.valueOf(stats.getNumFilesProcessed()));
            actionData.addProperty("totalFilesAborted",
                    String.valueOf(stats.getNumFilesAborted()));
            actionData.addProperty("totalMessagesProcessed",
                    String.valueOf(stats.getNumMessagesProcessed()));
            actionData.addProperty("totalMessagesInError",
                    String.valueOf(stats.getNumMessagesFailed()));

            String message = "Message Loader run on " + new Date();
            auditLogSvc.create(AuditActionType.LOADER_RUN, target, actionData,
                    message, GTAS_APPLICATION_USERID);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void processSingleFile(File f, LoaderStatistics stats) {
        System.out.println(String.format("Processing %s", f.getAbsolutePath()));
        String[] primeFlightKey = new String[]{"placeHolder"};
        try {
			primeFlightKey = getPrimeFlightTvl(FileUtils.readSmallFile(f.getAbsolutePath()));
		} catch (IOException e) {
			e.printStackTrace();
		}
        int[] result = loader.processMessage(f, primeFlightKey);
        // update loader statistics.
        if (result != null) {
            stats.incrementNumFilesProcessed();
            stats.incrementNumMessagesProcessed(result[0]);
            stats.incrementNumMessagesFailed(result[1]);
        } else {
            stats.incrementNumFilesAborted();
        }
    }

    private static void printUsage() {
        System.out.println("Usage: MessageLoader [files]");
        System.out
                .println("Usage: MessageLoader [incoming dir] [outgoing dir]");
        System.out.println("Usage: MessageLoader [queue URL]");
    }
    
	private static String[] getPrimeFlightTvl(byte[] fileRaw){
		String[] primeFlightTVL = new String[4];
		primeFlightTVL[0] = "placeholder";
		Boolean foundTVL = Boolean.FALSE;
		String tmp = new String(fileRaw, StandardCharsets.US_ASCII);
		List<Segment> segments = new ArrayList<>();
        EdifactLexer lexer = new EdifactLexer(tmp);
        try {
			segments = lexer.tokenize();
		} catch (ParseException e) {
			e.printStackTrace();
		}
         for(Segment seg : segments){
             if(seg.getName().equalsIgnoreCase("TVL")){
            	primeFlightTVL[0] = seg.getComposite(1).getElement(0);
            	primeFlightTVL[1] = seg.getComposite(2).getElement(0);
 				primeFlightTVL[2] = seg.getComposite(3).getElement(0) + seg.getComposite(4).getElement(0);
                break;
             }
         }
         return primeFlightTVL;
	}
}

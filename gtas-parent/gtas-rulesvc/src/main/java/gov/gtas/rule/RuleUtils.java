/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.rule;

import gov.gtas.constant.RuleServiceConstants;
import gov.gtas.error.RuleServiceException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.kie.api.KieBase;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.api.builder.Results;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.event.rule.DefaultAgendaEventListener;
import org.kie.api.event.rule.DefaultRuleRuntimeEventListener;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A collection of utility methods for:
 * <ul>
 * <li>Knowledge management</li>
 * <li>Rule management</li>
 * </ul>
 */
public class RuleUtils {
	private static final Logger logger = LoggerFactory
			.getLogger(RuleUtils.class);

	private RuleUtils() {
	}

	/**
	 * Creates a KieSession from a DRL file.<br>
	 * (see for example
	 * http://stackoverflow.com/questions/27488034/with-drools-6
	 * -x-how-do-i-avoid-maven-and-the-compiler)
	 * 
	 * @param filePath
	 *            the input DRL file on the class path.
	 * @return the created KieBase.
	 * @throws IOException
	 *             on IO error.
	 */
	public static KieBase createKieBaseFromClasspathFile(final String filePath)
			throws IOException {
		File file = new File(filePath);
		InputStream is = Thread.currentThread().getContextClassLoader()
				.getResourceAsStream(filePath);
		String kfilepath = RuleServiceConstants.KIE_FILE_SYSTEM_ROOT
				+ file.getName();
		return createKieBase(kfilepath, is);
	}

	/**
	 * Creates a KieBase from DRL string data.
	 * 
	 * @param drlString
	 *            the DRL data as a string.
	 * @return the created KieBase.
	 * @throws IOException
	 *             on IO error.
	 */
	public static KieBase createKieBaseFromDrlString(final String drlString)
			throws IOException {
		File file = File.createTempFile("rule", "");
		ByteArrayInputStream bis = new ByteArrayInputStream(
				drlString.getBytes());
		String kfilepath = RuleServiceConstants.KIE_FILE_SYSTEM_ROOT
				+ file.getName() + ".drl";
		return createKieBase(kfilepath, bis);
	}

	/**
	 * Thread-safe creation of KieSession from a KieBase.
	 * 
	 * @param kieBase
	 *            the source KieBase
	 * @return the created KieSession
	 */
	public static synchronized KieSession createSession(KieBase kieBase) {
		return kieBase.newKieSession();
	}

	/**
	 * Converts a KieBase to compressed binary data suitable for caching or
	 * saving in a database as a BLOB.
	 * 
	 * @param kieBase
	 *            the KieBase to convert.
	 * @return compressed binary data for KieBase.
	 * @throws IOException
	 *             on IO error.
	 */
	public static byte[] convertKieBaseToBytes(final KieBase kieBase)
			throws IOException {
		final ByteArrayOutputStream bos = new ByteArrayOutputStream();
		final GZIPOutputStream gzipOutStream = new GZIPOutputStream(bos);
		final ObjectOutputStream out = new ObjectOutputStream(gzipOutStream);
		out.writeObject(kieBase);
		out.close();
		return bos.toByteArray();
	}

	/**
	 * Converts a Serializable Java object to compressed binary data suitable
	 * for caching or saving in a database as a BLOB.
	 * 
	 * @param serializable
	 *            the object to serialize.
	 * @return compressed binary data for serializable object.
	 * @throws IOException
	 *             on IO error.
	 */
	public static byte[] convertSerializableToBytes(
			final Serializable serializable) throws IOException {
		final ByteArrayOutputStream bos = new ByteArrayOutputStream();
		final GZIPOutputStream gzipOutStream = new GZIPOutputStream(bos);
		final ObjectOutputStream out = new ObjectOutputStream(gzipOutStream);
		out.writeObject(serializable);
		out.close();
		return bos.toByteArray();
	}

	/**
	 * Creates a KieBase from compressed binary data.
	 * 
	 * @param kiebaseBytes
	 *            the binary compressed data to be used for input.
	 * @return the KieBase object constructed from the input data.
	 * @throws ClassNotFoundException
	 *             if the compressed binary data includes unknown serialized
	 *             Java Class instances.
	 * @throws IOException
	 *             on IO error.
	 */
	public static KieBase convertKieBasefromBytes(final byte[] kiebaseBytes)
			throws ClassNotFoundException, IOException {
		final ByteArrayInputStream bis = new ByteArrayInputStream(kiebaseBytes);
		final GZIPInputStream gzipInStream = new GZIPInputStream(bis);
		final ObjectInputStream in = new ObjectInputStream(gzipInStream);
		KieBase kieBase = (KieBase) in.readObject();
		in.close();
		return kieBase;
	}

	/**
	 * Creates a Serializable object from compressed binary data.
	 * 
	 * @param objectBytes
	 *            the binary compressed data to be used for input.
	 * @return the Serializable object constructed from the input data.
	 * @throws ClassNotFoundException
	 *             if the compressed binary data includes unknown serialized
	 *             Java Class instances.
	 * @throws IOException
	 *             on IO error.
	 */
	public static Serializable convertSerializablefromBytes(
			final byte[] objectBytes) throws ClassNotFoundException,
			IOException {
		final ByteArrayInputStream bis = new ByteArrayInputStream(objectBytes);
		final GZIPInputStream gzipInStream = new GZIPInputStream(bis);
		final ObjectInputStream in = new ObjectInputStream(gzipInStream);
		Serializable serializable = (Serializable) in.readObject();
		in.close();
		return serializable;
	}

	/**
	 * Creates a KieBase from input stream data.
	 * 
	 * @param kfilepath
	 *            the in memory KieFileSystem name
	 * @param is
	 *            the input stream for DRL data
	 * @param errorHandler
	 *            error handler
	 * @return the created KieBase
	 */
	private static KieBase createKieBase(final String kfilepath,
			final InputStream is) {
		KieServices ks = KieServices.Factory.get();
		KieFileSystem kfs = ks.newKieFileSystem();
		kfs.write(kfilepath, ks.getResources().newInputStreamResource(is));
		KieBuilder kieBuilder = ks.newKieBuilder(kfs).buildAll();
		Results results = kieBuilder.getResults();
		if (results.hasMessages(Message.Level.ERROR)) {
			RuleServiceException ruleException = new RuleServiceException(
					RuleServiceConstants.RULE_COMPILE_ERROR_CODE,
					String.format(
							RuleServiceConstants.RULE_COMPILE_ERROR_MESSAGE,
							kfilepath));
			List<Message> errors = results.getMessages();
			for (Message msg : errors) {
				logger.error(msg.getText());
				ruleException.addRuleCompilationError(msg);
			}
			throw ruleException;
		}
		KieContainer kieContainer = ks.newKieContainer(ks.getRepository()
				.getDefaultReleaseId());

		// CEP - get the KIE related configuration container and set the
		// EventProcessing (from default cloud) to Stream
		KieBaseConfiguration config = ks.newKieBaseConfiguration();
		config.setOption(EventProcessingOption.STREAM);
		return kieContainer.newKieBase(config);
	}

	/**
	 * Creates a simple rule session from the provided session name. Note: The
	 * session name must be configured in the KieModule configuration file
	 * (META-INF/kmodule.xml).
	 * 
	 * @param sessionName
	 *            the session name.
	 * @param eventListenerList
	 *            the list of event listeners to attach to the session.
	 * @return the created session.
	 */
	public static KieSession initSessionFromClasspath(final String sessionName,
			final List<EventListener> eventListenerList) {
		// KieServices is the factory for all KIE services
		KieServices ks = KieServices.Factory.get();

		// From the KIE services, a container is created from the class-path
		KieContainer kc = ks.getKieClasspathContainer();

		// From the container, a session is created based on
		// its definition and configuration in the META-INF/kmodule.xml file
		KieSession ksession = kc.newKieSession(sessionName);

		// Once the session is created, the application can interact with it
		// In this case it is setting a global as defined in the
		// gov/gtas/rule/gtas.drl file
		ksession.setGlobal(RuleServiceConstants.RULE_RESULT_LIST_NAME,
				new ArrayList<Object>());

		// The application can also setup listeners
		if (eventListenerList != null) {
			for (EventListener el : eventListenerList) {
				if (el instanceof DefaultAgendaEventListener) {
					ksession.addEventListener((DefaultAgendaEventListener) el);
				} else if (el instanceof DefaultRuleRuntimeEventListener) {
					ksession.addEventListener((DefaultRuleRuntimeEventListener) el);
				}
			}
		}
		return ksession;
	}
}

/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.config;

import javax.naming.NamingException;

import org.apache.commons.dbcp.cpdsadapter.DriverAdapterCPDS;
import org.apache.commons.dbcp.datasources.SharedPoolDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mock.jndi.SimpleNamingContextBuilder;
import org.springframework.stereotype.Component;

@Component
public class JndiBean {

	private final Logger logger  = LoggerFactory.getLogger(JndiBean.class);

	
	public JndiBean() {
		try {
            DriverAdapterCPDS cpds = new DriverAdapterCPDS();
            cpds.setDriver("org.mariadb.jdbc.Driver");
            cpds.setUrl("jdbc:mariadb://localhost:3306/gtas");
            cpds.setUser("root");
            cpds.setPassword("admin");

            SharedPoolDataSource dataSource = new SharedPoolDataSource();
            dataSource.setConnectionPoolDataSource(cpds);
            dataSource.setMaxActive(10);
            dataSource.setMaxWait(50);

            SimpleNamingContextBuilder builder = new SimpleNamingContextBuilder();
            builder.bind("java:comp/env/jdbc/gtasDataSource", dataSource);
            builder.activate();
        } catch (NamingException | ClassNotFoundException ex) {
            logger.error(ex.getMessage() );
        }
	}
}

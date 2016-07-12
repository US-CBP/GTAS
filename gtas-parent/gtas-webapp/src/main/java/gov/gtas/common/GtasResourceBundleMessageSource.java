/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.common;

import java.util.Locale;
import java.util.Properties;

import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.stereotype.Component;

public class GtasResourceBundleMessageSource extends ReloadableResourceBundleMessageSource {

    public Properties getAllProperties(Locale locale) {
        clearCacheIncludingAncestors();
        PropertiesHolder propertiesHolder = getMergedProperties(locale);
        Properties properties = propertiesHolder.getProperties();

        return properties;
    }
}
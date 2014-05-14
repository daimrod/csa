package jgreg.internship.nii.RES;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;
import org.apache.uima.resource.DataResource;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.SharedResourceObject;

/**
 * Describe a mapping
 *
 * A mapping file is as follow:
 *
 * key1;value\nkey1;value\nkey2;value\n...
 *
 * @author
 */
public final class MappingRES implements SharedResourceObject {
	private static final Logger logger = Logger.getLogger(MappingRES.class
			.getCanonicalName());

	private Map<String, Set<String>> mapping;

	public void load(DataResource aData) throws ResourceInitializationException {
		String filename = aData.getUri().toString();
        PropertiesConfiguration config;
        try {
            config = new PropertiesConfiguration(filename);
        } catch (ConfigurationException ex) {
            throw new ResourceInitializationException(ex);
        }

		mapping = new HashMap<>();
        for (Iterator<String> iter = config.getKeys(); iter.hasNext();) {
            String key = iter.next();
            Set<String> set = new HashSet<>(Arrays.asList(config.getStringArray(key)));
            logger.debug(key + " = " + set);
            mapping.put(key, set);
        }
	}

	public boolean containsKey(Object key) {
		return mapping.containsKey(key);
	}

	public boolean containsValue(Object value) {
		return mapping.containsValue(value);
	}

	public Set<Map.Entry<String, Set<String>>> entrySet() {
		return mapping.entrySet();
	}

	public Set<String> get(Object key) {
		return mapping.get(key);
	}

	public Set<String> keySet() {
		return mapping.keySet();
	}

	public Set<String> remove(Object key) {
		return mapping.remove(key);
	}

	public int size() {
		return mapping.size();
	}

    public Collection<Set<String>> values() {
        return mapping.values();
    }
}

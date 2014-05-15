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

// TODO: Auto-generated Javadoc
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
	
	/** The Constant logger. */
	private static final Logger logger = Logger.getLogger(MappingRES.class
			.getCanonicalName());

	/** The mapping. */
	private Map<String, Set<String>> mapping;

	/* (non-Javadoc)
	 * @see org.apache.uima.resource.SharedResourceObject#load(org.apache.uima.resource.DataResource)
	 */
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

	/**
	 * Contains key.
	 *
	 * @param key the key
	 * @return true, if successful
	 */
	public boolean containsKey(Object key) {
		return mapping.containsKey(key);
	}

	/**
	 * Contains value.
	 *
	 * @param value the value
	 * @return true, if successful
	 */
	public boolean containsValue(Object value) {
		return mapping.containsValue(value);
	}

	/**
	 * Entry set.
	 *
	 * @return the sets the
	 */
	public Set<Map.Entry<String, Set<String>>> entrySet() {
		return mapping.entrySet();
	}

	/**
	 * Gets the.
	 *
	 * @param key the key
	 * @return the sets the
	 */
	public Set<String> get(Object key) {
		return mapping.get(key);
	}

	/**
	 * Key set.
	 *
	 * @return the sets the
	 */
	public Set<String> keySet() {
		return mapping.keySet();
	}

	/**
	 * Removes the.
	 *
	 * @param key the key
	 * @return the sets the
	 */
	public Set<String> remove(Object key) {
		return mapping.remove(key);
	}

	/**
	 * Size.
	 *
	 * @return the int
	 */
	public int size() {
		return mapping.size();
	}

    /**
     * Values.
     *
     * @return the collection
     */
    public Collection<Set<String>> values() {
        return mapping.values();
    }
}

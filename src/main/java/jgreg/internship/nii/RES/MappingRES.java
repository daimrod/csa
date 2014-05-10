package jgreg.internship.nii.RES;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import jgreg.internship.nii.Utils.Utils;

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
		File file = new File(filename);

		mapping = new HashMap<>();
		try {
			for (String line : Utils.readLines(file)) {
				// data[0] = class name
				// data[1] = filename
				String[] data = line.split(";");
				if (data.length != 2) {
					logger.warn("ill-formed line `" + data.toString() + "'");
					continue;
				}
				if (!mapping.containsKey(data[0])) {
					mapping.put(data[0], new HashSet<>());
				}
				mapping.get(data[0]).add(data[1]);
			}
		} catch (IOException ex) {
			throw new ResourceInitializationException(ex);
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

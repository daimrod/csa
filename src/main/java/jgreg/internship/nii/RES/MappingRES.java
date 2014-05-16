// 
// Author:: Grégoire Jadi <daimrod@gmail.com>
// Copyright:: Copyright (c) 2014, Grégoire Jadi
// All rights reserved.
// 
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are
// met:
// 
//    1. Redistributions of source code must retain the above copyright
//       notice, this list of conditions and the following disclaimer.
// 
//    2. Redistributions in binary form must reproduce the above
//       copyright notice, this list of conditions and the following
//       disclaimer in the documentation and/or other materials provided
//       with the distribution.
// 
// THIS SOFTWARE IS PROVIDED BY GRÉGOIRE JADI ``AS IS'' AND ANY
// EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
// PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL GRÉGOIRE JADI OR
// CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
// SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
// LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
// USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
// ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
// OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
// OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
// SUCH DAMAGE.
// 
// The views and conclusions contained in the software and
// documentation are those of the authors and should not be
// interpreted as representing official policies, either expressed or
// implied, of Grégoire Jadi.
// 

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

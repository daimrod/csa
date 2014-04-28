package jgreg.internship.nii.RES;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.uima.resource.DataResource;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.SharedResourceObject;

public final class MatcherRES<T> implements SharedResourceObject {
    private static final Logger logger = Logger.getLogger(MatcherRES.class.getCanonicalName());
    
    private Map<String, List<T>> matches;
    
    @Override
    public void load(DataResource aData) throws ResourceInitializationException {
        matches = new HashMap<>();
    }

    synchronized public void add(String key, T val) {
        if (matches.containsKey(key)) {
            matches.get(key).add(val);
        } else {
            List<T> list = new LinkedList<>();
            list.add(val);
            matches.put(key, list);
        }
    }

    public Map<String, List<T>> getMatches() { return matches; }
}

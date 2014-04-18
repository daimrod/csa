/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jgreg.internship.nii.AE;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import jgreg.internship.nii.types.Citation;
import jgreg.internship.nii.types.ID;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Logger;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

public class CoCitationExtractor extends org.apache.uima.fit.component.JCasAnnotator_ImplBase {
    private static final Logger logger = Logger.getLogger(CoCitationExtractor.class.getCanonicalName());
    
    public static final String OUTPUT_FILE = "outputFilePath";
    @ConfigurationParameter(name = OUTPUT_FILE, mandatory = true)
    private File outputFile;

    // references[A] -- cited by --> B, C, D, ...
    private Map<String, Set<String>> references;

    @Override
    public void initialize(UimaContext context) throws ResourceInitializationException {
        references = new HashMap<>();
        outputFile = new File((String) context.getConfigParameterValue(OUTPUT_FILE));
    }

    @Override
    public void process(JCas jCas) throws AnalysisEngineProcessException {
        String from = JCasUtil.selectSingle(jCas, ID.class).getPMID();

        for (Citation citation : JCasUtil.select(jCas, Citation.class)) {
            addCitation(from, citation.getPMID());
        }
    }

    private void addCitation(String from, String to) {
        if (references.containsKey(to)) {
            references.get(to).add(from);
        } else {
            Set<String> set = new HashSet<>();
            set.add(from);
            references.put(to, set);
        }

        logger.debug("`" + to + "' is cited by `" + from + "' (" + references.keySet().size() + ")");
    }

    private Set<Pair<String, String>> getCoCitations() {
        HashSet<Pair<String, String>> ret = new HashSet<>();

        int length = references.keySet().size();
        String[] articles = references.keySet().toArray(new String[length]);
        logger.debug("Set of articles created");

        for (int i = 0; i < length; i++) {
            String a = articles[i];
            for (int j = i + 1; j < length; j++) {
                String b = articles[j];
                // if a ∩ b ≠ ∅ ...
                if (references.get(a).stream().anyMatch((o) -> references.get(b).contains(o))) {
                    ret.add(new ImmutablePair<>(a, b));
                }
            }
        }

        return ret;
    }

    @Override
    public void collectionProcessComplete() throws AnalysisEngineProcessException {
        logger.info("Dumping cocitation information to `" + outputFile.getAbsolutePath() + "'...");
        
        // StringBuilder str = new StringBuilder();
        
        // for (Pair<String, String> pair : getCoCitations()) {
        //     str.append(pair.getLeft())
        //             .append(' ')
        //             .append(pair.getRight())
        //             .append('\n');
        // }

        try {
            FileUtils.writeLines(outputFile, getCoCitations());
        } catch (IOException ex) {
            logger.fatal(null, ex);
        }
    }
}

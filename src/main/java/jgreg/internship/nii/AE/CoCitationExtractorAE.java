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

/**
 * Extract citation and co-citation information.
 *
 * 1. Extract all citations for every articles (every CAS).
 * 2. Compute a list of co-cited articles from the citations previously extracted.
 * 3. Dump the list to @{link #OUTPUT_FILE}
 *
 * @author Grégoire Jadi
 */

public class CoCitationExtractorAE extends org.apache.uima.fit.component.JCasAnnotator_ImplBase {
    private static final Logger logger = Logger.getLogger(CoCitationExtractorAE.class.getCanonicalName());
    
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
        String citer = JCasUtil.selectSingle(jCas, ID.class).getPMID();

        for (Citation citee : JCasUtil.select(jCas, Citation.class)) {
            // <citation> -- cited by --> <current jCas>
            addCitation(citee.getPMID(), citer);
        }
    }

    private void addCitation(String citer, String citee) {
        if (references.containsKey(citee)) {
            references.get(citee).add(citer);
        } else {
            Set<String> set = new HashSet<>();
            set.add(citer);
            references.put(citee, set);
        }

        logger.debug("`" + citee + "' is cited by `" + citer + "' (" + references.keySet().size() + ")");
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
                // Looking for an intersection between references[a] and references[b]
                // if a ∩ b ≠ ∅ ...
                if (references.get(a).stream().anyMatch((o) -> references.get(b).contains(o))) {
                    ret.add(new ImmutablePair<>(a, b));
                }
            }
        }
        logger.info("CoCitation extracted");

        return ret;
    }

    @Override
    public void collectionProcessComplete() throws AnalysisEngineProcessException {
        logger.info("Dumping cocitation information to `" + outputFile.getAbsolutePath() + "'...");

        try {
            FileUtils.writeLines(outputFile, getCoCitations());
        } catch (IOException ex) {
            logger.fatal(null, ex);
        }
    }
}

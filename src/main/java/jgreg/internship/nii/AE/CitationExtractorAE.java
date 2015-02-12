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
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

/**
 * Extract citation information.
 *
 * 1. Extract all citations for every articles (every CAS).
 * 2. Dump the list to @{link #OUTPUT_FILE}
 *
 * @author Grégoire Jadi
 */

public class CitationExtractorAE extends
		org.apache.uima.fit.component.JCasAnnotator_ImplBase {
	private static final Logger logger = Logger
			.getLogger(CitationExtractorAE.class.getCanonicalName());

    public static final String OUTPUT_FILE = "outputFile";
	@ConfigurationParameter(name = OUTPUT_FILE, mandatory = true)
	private File outputFile;

    // references[A] -- cited by --> B, C, D, ...
	private Map<String, Set<String>> references;

	@Override
	public void initialize(UimaContext context)
			throws ResourceInitializationException {
		references = new HashMap<>();
		outputFile = new File(
                (String) context.getConfigParameterValue(OUTPUT_FILE));
    }

	@Override
	public void process(JCas jCas) throws AnalysisEngineProcessException {
		String citer = JCasUtil.selectSingle(jCas, ID.class).getPMID();

        for (Citation citee : JCasUtil.select(jCas, Citation.class)) {
            // Skip citation without PMID
            if (citee.getPMID() == null) {
                logger.debug("skipping citation without PMID");
                continue;
            }
			// citer = <current jCas>
			// citee = inside citer
            // <citee> -- cited by --> <citer>
            addCitation(citee.getPMID(), citer);
		}
	}

	private void addCitation(String citee, String citer) {
		if (!references.containsKey(citee)) {
			Set<String> set = new HashSet<>();
			references.put(citee, set);
		}
		references.get(citee).add(citer);

		logger.debug("`" + citee + "' is cited by `" + citer + "' ("
				+ references.keySet().size() + ")");
	}

    @Override
    public void collectionProcessComplete()
			throws AnalysisEngineProcessException {
		logger.info("Dumping citation information to `"
				+ outputFile.getAbsolutePath() + "'...");

        try {
            StringBuilder builder = new StringBuilder();
            for (String citee : references.keySet()) {
                builder.append(citee).append(" = ").append(StringUtils.join(references.get(citee), ",")).append('\n');
            }
            logger.debug(builder.toString());
            FileUtils.write(outputFile, builder.toString(), null, true);
		} catch (IOException ex) {
			logger.fatal(null, ex);
  }
    }
}

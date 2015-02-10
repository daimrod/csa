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

package jgreg.internship.nii.Utils;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import jgreg.internship.nii.RES.MappingRES;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Logger;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;

/**
 * Extract citation and co-citation information.
 *
 * 1. Extract all citations for every articles (every CAS). 2. Compute a list of
 * co-cited articles from the citations previously extracted. 3. Dump the list
 * to @{link #OUTPUT_FILE}
 *
 * Two articles are cocited, if they have a coCitationScore superior or equal to
 * @{link #COCITATION_THRESHOLD}.
 *
 * @author Grégoire Jadi
 */

public class CoCitationExtractor {
	private static final Logger logger = Logger
            .getLogger(CoCitationExtractor.class.getCanonicalName());

    private File coCitationFile;
    private Integer coCitationThreshold;

	// references[A] -- cited by --> B, C, D, ...
    private MappingRES citations;

    public CoCitationExtractor(String citationFilename, String coCitationFilename, Integer coCitationThreshold) throws Exception {
        citations = new MappingRES();
        citations.load(citationFilename);
        this.coCitationFile = new File(coCitationFilename);
        this.coCitationThreshold = coCitationThreshold;
	}

    private Set<Pair<String, String>> getCoCitations() {
		HashSet<Pair<String, String>> ret = new HashSet<>();

		int length = citations.keySet().size();
		String[] articles = citations.keySet().toArray(new String[length]);
		logger.debug("Set of articles created");

		for (int i = 0; i < length; i++) {
			String a = articles[i];
			for (int j = i + 1; j < length; j++) {
				String b = articles[j];
				// Looking for an intersection between references[a] and
				// references[b]
                // if a ∩ b ≠ ∅ ...
                if (getCoCitationScore(a, b) >= coCitationThreshold) {
					ret.add(new ImmutablePair<>(a, b));
				}
			}
		}
		logger.info("CoCitation extracted");

		return ret;
    }

    private String getCoCitationsAsString() {
        StringBuffer ret = new StringBuffer();

		int length = citations.keySet().size();
		String[] articles = citations.keySet().toArray(new String[length]);
		logger.debug("Set of articles created");

		for (int i = 0; i < length; i++) {
			String a = articles[i];
			for (int j = i + 1; j < length; j++) {
				String b = articles[j];
				// Looking for an intersection between references[a] and
				// references[b]
                // if a ∩ b ≠ ∅ ...
                if (getCoCitationScore(a, b) >= coCitationThreshold) {
                    ret.append(a).append(' ').append(b).append('\n');
                }
			}
		}
		logger.info("CoCitation extracted");

        return ret.toString();
	}

    private Integer getCoCitationScore(String a, String b) {
        Integer score;
		Set<String> citersA = citations.get(a);
        Set<String> citersB = citations.get(b);
        if (citersA == null || citersB == null) {
            score = 0;
        }

		Set<String> intersection = new HashSet<>(citersA);
        intersection.retainAll(citersB);
        score = intersection.size();

        return score;
	}

    public void write()
			throws AnalysisEngineProcessException {
		logger.info("Dumping cocitation information to `"
				+ coCitationFile.getAbsolutePath() + "'...");

		try {
            FileUtils.writeLines(coCitationFile, getCoCitationsAsString(), null, true);
		} catch (IOException ex) {
			logger.fatal(null, ex);
  }
    }
}

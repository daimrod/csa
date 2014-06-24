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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jgreg.internship.nii.types.CitationContext;
import jgreg.internship.nii.types.ID;
import jgreg.internship.nii.types.Sentiment;
import jgreg.internship.nii.types.Token;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

/**
 * TODO
 *
 * @author Grégoire Jadi
 */
public class CitationContextExtractorAE extends
		org.apache.uima.fit.component.JCasAnnotator_ImplBase {

	/** The Constant logger. */
	private static final Logger logger = Logger
			.getLogger(CitationContextExtractorAE.class.getCanonicalName());

	public static final String PARAM_CONFIGURATION_FILE = "configFilename";
	@ConfigurationParameter(name = PARAM_CONFIGURATION_FILE, mandatory = true)
	private String configFilename;

    private File outputDir;

	private PropertiesConfiguration extractorConfig;

	@Override
	public void initialize(UimaContext context)
			throws ResourceInitializationException {
		super.initialize(context);

        try {
			extractorConfig = new PropertiesConfiguration(configFilename);
		} catch (ConfigurationException ex) {
			logger.fatal(ex);
			throw new ResourceInitializationException(ex);
        }

        outputDir = new File(extractorConfig.getString("outputDirectory"));
		outputDir.mkdirs();
    }

	public void process(JCas jCas) throws AnalysisEngineProcessException {
		// extract current JCas' PMID
		ID id = JCasUtil.selectSingle(jCas, ID.class);

		// find the list of contexts to extract from the current JCas
		List<String> contexts = new ArrayList<String>(
				Arrays.asList(extractorConfig.getString(id.getPMID())
						.split(" ")));

		// if there is no contexts, leave
		if (contexts.isEmpty())
			return;

		// if not, prepare a temporary buffer
		StringBuilder buffer = new StringBuilder();

		// and iterate on all CitationContext
		for (CitationContext context : JCasUtil.select(jCas,
				CitationContext.class)) {
			String contextID = String.valueOf(context.getID());
			// find the correct one
			if (contexts.contains(contextID)) {
				// and extract them

				// The format for each context is the following (between !BEGIN
				// and !END):
				// !BEGIN
				// #<num>
				// word word word word/_/annotation word word
				// word word
				//
				//
				// !END

				buffer.append('#').append(contextID).append('\n');

				// iterate over every Token of the current CitationContext
				for (Token token : JCasUtil.selectCovered(jCas, Token.class,
						context)) {
					// append the Token text
					buffer.append(token.getCoveredText());

					// for each token, try to find its Sentiment
					List<Sentiment> sentiments = JCasUtil.selectCovered(jCas,
							Sentiment.class, token);
					// if there is any
					if (!sentiments.isEmpty()) {
						// add the annotation
						buffer.append("/_/")
								.append(sentiments.get(0).getName());
					}
					buffer.append(' ');
				}
				buffer.append('\n').append('\n');
			}

		}

		// finally dump the buffer to the output file
		try {
			FileUtils.writeStringToFile(new File(outputDir, id.getPMID()
					+ ".ctx"), buffer.toString());
		} catch (IOException ex) {
			logger.fatal(ex);
			throw new AnalysisEngineProcessException(ex);
		}

	}
}

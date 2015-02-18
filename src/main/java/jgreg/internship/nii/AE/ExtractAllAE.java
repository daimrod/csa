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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import jgreg.internship.nii.RES.MappingRES;
import jgreg.internship.nii.Utils.Utils;
import jgreg.internship.nii.types.CitationContext;
import jgreg.internship.nii.types.Filename;
import jgreg.internship.nii.types.ID;
import jgreg.internship.nii.types.Sentiment;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.Feature;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.Type;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.descriptor.ExternalResource;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.resource.ResourceInitializationException;

/**
 * Extract {@link jgreg.internship.nii.types.Sentiment} information from
 * {@link jgreg.internship.nii.types.CitationContext}.
 *
 * <p>
 * Count all {@link jgreg.internship.nii.types.Sentiment} annotations in all
 * {@link jgreg.internship.nii.types.CitationContext}
 * </p>
 *
 * <p>
 * All data are dumped in an CSV like format in {@link #OUTPUT_FILE}. The
 * {@link #HEADERS} parameter can be used to determine in which order the
 * {@link jgreg.internship.nii.types.Sentiment#name} are displayed.
 * </p>
 *
 * @author Grégoire Jadi
 */
public class ExtractAllAE extends
		org.apache.uima.fit.component.JCasAnnotator_ImplBase {

	/** The Constant logger. */
	protected static final Logger logger = Logger.getLogger(ExtractAllAE.class
			.getCanonicalName());

	/**
	 * This parameter can be used to customize the order in which
	 * {@link jgreg.internship.nii.types.Sentiment#name} are used and dumped.
	 */
	public static final String MAPPING = "mapping";
	@ExternalResource(key = MAPPING, mandatory = true)
	private MappingRES mapping;
	private String[] paramHeaders;

	// ArrayList<String> are easier to manipulate compared to raw arrays
	// String[].
	private ArrayList<String> headers;

	/** The separator used when dumping data. By default it uses a semi-colon. */
	public static final String SEPARATOR = "separator";
	@ConfigurationParameter(name = SEPARATOR, mandatory = false, defaultValue = ";")
	private String separator;

	/**
	 * The file where the data should be dumped.
	 */
	public static final String OUTPUT_FILE = "outputFileName";
	@ConfigurationParameter(name = OUTPUT_FILE, mandatory = true)
	private String outputFileName;

	/** The output file. */
	private File outputFile;

	/** The BufferedWriter */
	private BufferedWriter out;

	private StringBuilder strAcc;

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.apache.uima.fit.component.JCasAnnotator_ImplBase#initialize(org.apache
	 * .uima.UimaContext)
	 */
	@Override
	public void initialize(UimaContext context)
			throws ResourceInitializationException {
		super.initialize(context);
		outputFile = new File(outputFileName);
		logger.info("Dumping information to `" + outputFile.getAbsolutePath()
				+ "'...");

		headers = new ArrayList<String>(mapping.get("order"));

		if (!outputFile.exists()) {
			try {
				outputFile.createNewFile();
			} catch (IOException ex) {
				logger.info("Error when creating "
						+ outputFile.getAbsolutePath() + " " + ex);
				throw new ResourceInitializationException(ex);
			}
		}
		try {
			FileWriter fw = new FileWriter(outputFile);
			out = new BufferedWriter(fw);
			strAcc = new StringBuilder();
			strAcc.append("cites").append(separator).append("ctxID")
					.append(separator).append("cited").append(separator)
					.append(StringUtils.join(headers, separator)).append("\n");
            out.append(strAcc);
		} catch (IOException ex) {
			logger.info("Error when writing to " + outputFile.getAbsolutePath()
					+ " " + ex);
			throw new ResourceInitializationException(ex);
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.apache.uima.analysis_component.JCasAnnotator_ImplBase#process(org
	 * .apache.uima.jcas.JCas)
	 */
	@Override
	public void process(JCas jCas) throws AnalysisEngineProcessException {
		Map<CitationContext, Collection<Sentiment>> map = JCasUtil
				.indexCovered(jCas, CitationContext.class, Sentiment.class);

		// Current article information
		ID id = JCasUtil.selectSingle(jCas, ID.class);
		JCas originalText;
		try {
			originalText = jCas.getView("originalText");
		} catch (CASException ex) {
			throw new AnalysisEngineProcessException(ex);
		}

		Filename filename = JCasUtil.selectSingle(originalText, Filename.class);

		for (CitationContext context : JCasUtil.select(jCas,
				CitationContext.class)) {
			FSArray citationsFSA = context.getCitations();

			// Well, this shouldn't happen, but who knows...
			if (citationsFSA.size() <= 0) {
				continue;
			}
			strAcc = new StringBuilder();
			strAcc.append(id.getPMID()).append(separator)
					.append(context.getID()).append(separator);

			ArrayList<Integer> acc = Utils.List(headers.size(), 0);

			// Compute the number of each kind of Sentiment within the context
			for (Sentiment sentiment : map.get(context)) {
				int idx = headers.indexOf(sentiment.getName());
				acc.set(idx, acc.get(idx) + 1);
			}

			// Add those numbers to all cited articles within the context
			Type citationT = jCas.getTypeSystem().getType(
					"jgreg.internship.nii.types.Citation");
			Feature citationPMIDF = citationT.getFeatureByBaseName("PMID");

			for (int idx = 0; idx < citationsFSA.size() - 1; idx++) {
				FeatureStructure citationFS = citationsFSA.get(idx);
				strAcc.append(citationFS.getStringValue(citationPMIDF)).append(
						',');
			}
			strAcc.append(citationsFSA.get(citationsFSA.size() - 1)
					.getStringValue(citationPMIDF));

			strAcc.append(separator).append(StringUtils.join(acc, separator));

			strAcc.append('\n');
			try {
                out.append(strAcc);
			} catch (IOException ex) {
				logger.info("Error when writing to "
						+ outputFile.getAbsolutePath() + " " + ex);
				throw new AnalysisEngineProcessException(ex);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.apache.uima.analysis_component.AnalysisComponent_ImplBase#
	 * collectionProcessComplete()
	 */
	@Override
	public void collectionProcessComplete()
			throws AnalysisEngineProcessException {
		try {
			out.close();
		} catch (IOException ex) {
			logger.info("Couldn't close " + outputFile.getAbsolutePath() + " "
					+ ex);
			throw new AnalysisEngineProcessException(ex);
		}
  }
}

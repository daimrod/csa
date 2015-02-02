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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jgreg.internship.nii.RES.MappingRES;
import jgreg.internship.nii.Utils.Utils;
import jgreg.internship.nii.types.CitationContext;
import jgreg.internship.nii.types.ID;
import jgreg.internship.nii.types.Sentiment;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
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
 * {@link jgreg.internship.nii.types.CitationContext} using the most frequent
 * one.
 *
 *
 * Count all {@link jgreg.internship.nii.types.Sentiment} annotations in all
 * {@link jgreg.internship.nii.types.CitationContext} and consider the most
 * frequent {@link jgreg.internship.nii.types.Sentiment} as the "real" one.
 *
 *
 * For example, given a {@link jgreg.internship.nii.types.CitationContext} with
 * 3 {@link jgreg.internship.nii.types.Sentiment} named "positive" and 2 named
 * "neutral". Then we consider that the "real"
 * {@link jgreg.internship.nii.types.Sentiment} is "positive".
 *
 *
 * All data are dumped in an CSV like format in {@link #OUTPUT_FILE}. The
 * {@link #HEADERS} parameter can be used to determine in which order the
 * {@link jgreg.internship.nii.types.Sentiment#name} are displayed.
 *
 * @author Grégoire Jadi
 */
public class ExtractMaxAE extends
		org.apache.uima.fit.component.JCasAnnotator_ImplBase {

	/** The Constant logger. */
	protected static final Logger logger = Logger.getLogger(ExtractMaxAE.class
			.getCanonicalName());

	/**
	 * This parameter can be used to customize the order in which
	 * {@link jgreg.internship.nii.types.Sentiment#name} are used and dumped.
	 */
	public static final String HEADERS = "paramHeaders";
	@ConfigurationParameter(name = HEADERS, mandatory = true)
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

	/** The str acc. */
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

		headers = new ArrayList<String>(Arrays.asList(paramHeaders));

		strAcc = new StringBuilder();
		strAcc.append("cites").append(separator).append("cited")
				.append(separator).append(StringUtils.join(headers, separator))
				.append("\n");
	}

	/**
	 * This mapping describe contains the patterns used for each
	 * {@link jgreg.internship.nii.types.Sentiment#name}.
	 */
	public final static String MAPPING = "mapping";
	@ExternalResource(key = MAPPING, mandatory = true)
	private MappingRES mapping;

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

        // Mapping PMID -> [#c1, #c2, #c3] where PMID is a PMID to a
        // document cited by the current article and #cX are the
        // number of Sentiment named cX.
		Map<String, List<Integer>> mem = new HashMap<>();

		for (CitationContext context : JCasUtil.select(jCas,
				CitationContext.class)) {
			ArrayList<Integer> acc = Utils.List(headers.size(), 0);

			// Compute the number of each kind of Sentiment within the context
			for (Sentiment sentiment : map.get(context)) {
				int idx = headers.indexOf(sentiment.getName());
				acc.set(idx, acc.get(idx) + 1);
			}

			// Find the predominant Sentiment
			int max = 0, idx_max = 0;
			for (int idx = 0; idx < acc.size(); idx++) {
				if (max < acc.get(idx)) {
					max = acc.get(idx);
					idx_max = idx;
				}
			}

			// Add those numbers to all cited articles within the context
            FSArray citationsFSA = context.getCitations();
			Type citationT = jCas.getTypeSystem().getType(
					"jgreg.internship.nii.types.Citation");
			Feature citationPMIDF = citationT.getFeatureByBaseName("PMID");
			for (int idx = 0; idx < citationsFSA.size(); idx++) {
				FeatureStructure citationFS = citationsFSA.get(idx);
				String pmid = citationFS.getStringValue(citationPMIDF);

				List<Integer> lst;
				if (!mem.containsKey(pmid)) {
					lst = Utils.List(headers.size(), 0);
					mem.put(pmid, lst);
				} else {
					lst = mem.get(pmid);
				}

				lst.set(idx_max, lst.get(idx_max) + 1);
			}
		}

		for (String pmid : mem.keySet()) {
			strAcc.append(id.getPMID()).append(separator).append(id.getYear())
					.append(separator).append(pmid);
			for (Integer i : mem.get(pmid)) {
				strAcc.append(separator).append(i);
			}
			strAcc.append("\n");
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
		logger.info("Dumping information to `" + outputFile.getAbsolutePath()
				+ "'...");
		try {
			FileUtils.write(outputFile, strAcc.toString());
		} catch (IOException ex) {
			throw new AnalysisEngineProcessException(ex);
		}
	}
}

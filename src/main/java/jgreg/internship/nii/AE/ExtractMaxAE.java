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

// TODO: Auto-generated Javadoc
/**
 * The Class ExtractMax.
 *
 * @author Grégoire Jadi
 */
public class ExtractMaxAE extends
		org.apache.uima.fit.component.JCasAnnotator_ImplBase {
	
	/** The Constant logger. */
	protected static final Logger logger = Logger.getLogger(ExtractMaxAE.class
			.getCanonicalName());

	/**
	 * The headers used to dump the data.
	 */
	public static final String HEADERS = "paramHeaders";
	
	/** The param headers. */
	@ConfigurationParameter(name = HEADERS, mandatory = true)
	private String[] paramHeaders;
	
	/** The headers. */
	private ArrayList<String> headers;

	/** The separator used when dumping data. */
	public static final String SEPARATOR = "separator";
	
	/** The separator. */
	@ConfigurationParameter(name = SEPARATOR, mandatory = false, defaultValue = ";")
	private String separator;

	/**
	 * Where should we dump the data.
	 */
	public static final String OUTPUT_FILE = "outputFileName";
	
	/** The output file name. */
	@ConfigurationParameter(name = OUTPUT_FILE, mandatory = true)
	private String outputFileName;
	
	/** The output file. */
	private File outputFile;

	/** The str acc. */
	private StringBuilder strAcc;

	/* (non-Javadoc)
	 * @see org.apache.uima.fit.component.JCasAnnotator_ImplBase#initialize(org.apache.uima.UimaContext)
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
	 * The name of the files in which we will look for patterns.
	 */
	public final static String MAPPING = "mapping";
	
	/** The mapping. */
	@ExternalResource(key = MAPPING, mandatory = true)
	private MappingRES mapping;

	/* (non-Javadoc)
	 * @see org.apache.uima.analysis_component.JCasAnnotator_ImplBase#process(org.apache.uima.jcas.JCas)
	 */
	@Override
	public void process(JCas jCas) throws AnalysisEngineProcessException {
		Map<CitationContext, Collection<Sentiment>> map = JCasUtil
				.indexCovered(jCas, CitationContext.class, Sentiment.class);
		ID id = JCasUtil.selectSingle(jCas, ID.class);
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
			FSArray citationsFSA = context.getPMIDS();
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

	/* (non-Javadoc)
	 * @see org.apache.uima.analysis_component.AnalysisComponent_ImplBase#collectionProcessComplete()
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

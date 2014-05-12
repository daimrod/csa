package jgreg.internship.nii.AE;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import jgreg.internship.nii.RES.MappingRES;
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
 *
 * @author Grégoire Jadi
 */
public class ExtractAll extends
		org.apache.uima.fit.component.JCasAnnotator_ImplBase {
	protected static final Logger logger = Logger.getLogger(ExtractAll.class
			.getCanonicalName());

	public static final String HEADERS = "paramHeaders";
	@ConfigurationParameter(name = HEADERS, mandatory = true)
	private String[] paramHeaders;
	private ArrayList<String> headers;

	public static final String SEPARATOR = "separator";
	@ConfigurationParameter(name = SEPARATOR, mandatory = false, defaultValue = ";")
	private String separator;

	public static final String OUTPUT_FILE = "outputFileName";
	@ConfigurationParameter(name = OUTPUT_FILE, mandatory = true)
	private String outputFileName;
	private File outputFile;

	private StringBuilder strAcc;

	@Override
	public void initialize(UimaContext context)
			throws ResourceInitializationException {
		super.initialize(context);
		outputFile = new File(outputFileName);

		headers = new ArrayList<String>(Arrays.asList(paramHeaders));
        logger.info(headers);
		strAcc = new StringBuilder();
		strAcc.append("cites").append(separator).append("cited")
				.append(separator).append(StringUtils.join(headers, separator))
				.append("\n");
	}

	/**
	 * The name of the files in which we will look for patterns.
	 */
	public final static String MAPPING = "mapping";
	@ExternalResource(key = MAPPING, mandatory = true)
	private MappingRES mapping;

	@Override
	public void process(JCas jCas) throws AnalysisEngineProcessException {
		Map<CitationContext, Collection<Sentiment>> map = JCasUtil
				.indexCovered(jCas, CitationContext.class, Sentiment.class);
		ID id = JCasUtil.selectSingle(jCas, ID.class);

		for (CitationContext context : JCasUtil.select(jCas,
				CitationContext.class)) {
			List<Integer> acc = new ArrayList(headers.size());
			for (int idx = 0; idx < headers.size(); idx++) {
				acc.add(idx, 0);
			}

			// Compute the number of each kind of Sentiment within the context
			for (Sentiment sentiment : map.get(context)) {
				int idx = headers.indexOf(sentiment.getName());
				acc.set(idx, acc.get(idx) + 1);
			}

			// Add those numbers to all cited articles within the context
			FSArray citationsFSA = context.getPMIDS();
			Type citationT = jCas.getTypeSystem().getType(
					"jgreg.internship.nii.types.Citation");
			Feature citationPMIDF = citationT.getFeatureByBaseName("PMID");
			for (int idx = 0; idx < citationsFSA.size(); idx++) {
				FeatureStructure citationFS = citationsFSA.get(idx);
				String pmid = citationFS.getStringValue(citationPMIDF);
				strAcc.append(id.getPMID()).append(separator).append(pmid);
				for (Integer i : acc) {
					strAcc.append(separator).append(i);
				}
				strAcc.append("\n");
			}
		}
	}

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

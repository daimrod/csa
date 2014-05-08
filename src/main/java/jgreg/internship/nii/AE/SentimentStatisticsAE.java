package jgreg.internship.nii.AE;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jgreg.internship.nii.RES.Article;
import jgreg.internship.nii.RES.ArticlesDB;
import jgreg.internship.nii.types.CitationContext;
import jgreg.internship.nii.types.ID;
import jgreg.internship.nii.types.Negative;
import jgreg.internship.nii.types.Neutral;
import jgreg.internship.nii.types.Positive;
import jgreg.internship.nii.types.Sentiment;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.descriptor.ExternalResource;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

public class SentimentStatisticsAE extends
		org.apache.uima.fit.component.JCasAnnotator_ImplBase {
	private static final Logger logger = Logger
			.getLogger(SentimentStatisticsAE.class.getCanonicalName());

	public final static String PARAM_DB = "articlesDB";
	@ExternalResource(key = PARAM_DB, mandatory = true)
	private ArticlesDB articlesDB;

	public static final String OUTPUT_FILE = "outputFileName";
	@ConfigurationParameter(name = OUTPUT_FILE, mandatory = true)
	private String outputFileName;
	private File outputFile;

	public static final String INPUT_FILE = "inputFileName";
    @ConfigurationParameter(name = INPUT_FILE, mandatory = true)
    private String inputFileName;

	private File inputFile;
    private List<String> PMIDS;

	private StringBuilder acc;

	@Override
	public void initialize(UimaContext context) throws ResourceInitializationException {
        super.initialize(context);

        inputFile = new File(inputFileName);
        if (!inputFile.exists()) {
            logger.fatal(inputFileName + " doesn't exist");
            throw new ResourceInitializationException();
        }
        try {
            PMIDS = FileUtils.readLines(inputFile);
        } catch (IOException ex) {
            throw new ResourceInitializationException(ex);
        }

		acc = new StringBuilder();

		outputFile = new File(outputFileName);
	}

	@Override
	public void process(JCas jCas) throws AnalysisEngineProcessException {
		ID currentId = JCasUtil.selectSingle(jCas, ID.class);
		Article current = articlesDB.get(currentId.getPMID());

		Map<String, List<Integer>> sentiments = new HashMap<>();

		for (Sentiment sent : JCasUtil.select(jCas, Sentiment.class)) {
			CitationContext context = sent.getContext();
			for (int idx = 0; idx < context.getPMIDS().size(); idx++) {
				String pmid = context.getPMIDS(idx).getPMID();
				// We are only interested in citations about some articles
				if (!PMIDS.contains(pmid)) {
					continue;
				}

				int pos;
				// What kind of Sentiment is it...
				if (sent instanceof Positive) {
					pos = 0;
				} else if (sent instanceof Neutral) {
					pos = 1;
				} else if (sent instanceof Negative) {
					pos = 2;
				} else {
					throw new UnsupportedOperationException();
				}

				if (sentiments.containsKey(pmid)) {
					List<Integer> lst = sentiments.get(pmid);
					lst.set(pos, lst.get(pos) + 1);
				} else {
					List<Integer> lst = Arrays.asList(0, 0, 0);
					lst.set(pos, 1);
					sentiments.put(pmid, lst);
				}
			}
		}

		for (String pmid : sentiments.keySet()) {
			List<Integer> lst = sentiments.get(pmid);

			acc.append(current.getPMID()).append(' ').append(pmid).append(' ')
					.append(current.getYear()).append(' ').append(lst.get(0))
					.append(' ').append(lst.get(1)).append(' ')
					.append(lst.get(2)).append('\n');
		}
	}

	@Override
	public void collectionProcessComplete()
			throws AnalysisEngineProcessException {
		try {
			FileUtils.write(outputFile, acc.toString());
		} catch (IOException ex) {
			throw new AnalysisEngineProcessException(ex);
		}
	}
}

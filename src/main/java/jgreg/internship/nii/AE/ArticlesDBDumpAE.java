package jgreg.internship.nii.AE;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import jgreg.internship.nii.RES.Article;
import jgreg.internship.nii.RES.ArticlesDB;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.descriptor.ExternalResource;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

/**
 *
 * @author Grégoire Jadi
 */
public class ArticlesDBDumpAE extends
		org.apache.uima.fit.component.JCasAnnotator_ImplBase {
	private static final Logger logger = Logger
			.getLogger(ArticlesDBDumpAE.class.getCanonicalName());

	/**
	 * The directory in which the GNU Plot data are written
	 */
	public static final String OUTPUT_DIRECTORY = "outputDirName";
	@ConfigurationParameter(name = OUTPUT_DIRECTORY, mandatory = true)
	private String outputDirName;
	private File outputDir;

	public static final String INPUT_FILE = "inputFileName";
	@ConfigurationParameter(name = INPUT_FILE, mandatory = true)
	private String inputFileName;
	private File inputFile;

	public final static String PARAM_DB = "articlesDB";
	@ExternalResource(key = PARAM_DB, mandatory = true)
	private ArticlesDB articlesDB;

	@Override
	public void initialize(UimaContext context)
			throws ResourceInitializationException {
		super.initialize(context);

		logger.info(outputDirName);
		logger.info(inputFileName);
		inputFile = new File(inputFileName);

		outputDir = new File(outputDirName);
		outputDir.mkdirs();
	}

	@Override
	public void process(JCas jCas) throws AnalysisEngineProcessException {
	}

	@Override
	public void collectionProcessComplete()
			throws AnalysisEngineProcessException {
        logger.info("Dumping ArticlesDB...");
		try {
			for (String pmid : FileUtils.readLines(inputFile)) {
				Article article = articlesDB.get(pmid);
				Map<Integer, Integer> positives = new HashMap<>();
				Map<Integer, Integer> neutrals = new HashMap<>();
				Map<Integer, Integer> negatives = new HashMap<>();

				logger.debug(pmid + " -> " + article);
				// Count Sentiment polarity
				for (String pos : article.getPositives()) {
					Integer year = articlesDB.get(pos).getDate().getYear();
					if (positives.containsKey(year)) {
						positives.put(year, positives.get(year) + 1);
					} else {
						positives.put(year, 1);
					}
				}

				for (String pos : article.getNeutrals()) {
					Integer year = articlesDB.get(pos).getDate().getYear();
					if (neutrals.containsKey(year)) {
						neutrals.put(year, neutrals.get(year) + 1);
					} else {
						neutrals.put(year, 1);
					}
				}

				for (String pos : article.getNegatives()) {
					Integer year = articlesDB.get(pos).getDate().getYear();
					if (negatives.containsKey(year)) {
						negatives.put(year, negatives.get(year) + 1);
					} else {
						negatives.put(year, 1);
					}
				}

				// Dump Sentiment polarity
				StringBuilder out = new StringBuilder();
				for (Integer year : positives.keySet()) {
					out.append(year).append(' ').append(positives.get(year))
							.append(' ').append(neutrals.get(year)).append(' ')
							.append(negatives.get(year)).append('\n');
					neutrals.remove(year);
					negatives.remove(year);
				}

				for (Integer year : neutrals.keySet()) {
					out.append(year).append(' ').append('0').append(' ')
							.append(neutrals.get(year)).append(' ')
							.append(negatives.get(year)).append('\n');
					negatives.remove(year);
				}

				for (Integer year : negatives.keySet()) {
					out.append(year).append(' ').append('0').append(' ')
							.append('0').append(' ')
							.append(negatives.get(year)).append('\n');
				}

                File output = new File(outputDir, pmid + ".dat");
                logger.info("Dumping ArticlesDB in `" + output.getAbsolutePath() + "'...");
                FileUtils.write(output, out.toString());
			}
		} catch (IOException ex) {
			throw new AnalysisEngineProcessException(ex);
		}
	}
}

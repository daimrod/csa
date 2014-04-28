package jgreg.internship.nii.AE;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jgreg.internship.nii.Utils.Utils;
import jgreg.internship.nii.types.CitationContext;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.Feature;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.TypeSystem;
import org.apache.uima.cas.text.AnnotationFS;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

public class SentimentMatcherAE extends
		org.apache.uima.fit.component.JCasAnnotator_ImplBase {
	private static final Logger logger = Logger
			.getLogger(SentimentMatcherAE.class.getCanonicalName());

	/**
	 * Default score for sentiment annotation
	 */
	private Integer DEFAULT_SCORE = 1;

	/**
	 * The name of the file in which we will look for patterns.
	 */
	public final static String PARAM_INPUT_MATCH = "input";
	@ConfigurationParameter(name = PARAM_INPUT_MATCH, mandatory = true)
	private File inputFile;

	/**
	 * The name of the class of sentiment that we are matching. .
	 */
	public final static String PARAM_SENTIMENT_CLASS_NAME = "sentimentClassName";
	@ConfigurationParameter(name = PARAM_SENTIMENT_CLASS_NAME, mandatory = true)
	String sentimentClassName;

	/**
	 * The pattern to match in the method process(JCas).
	 */
	private Pattern pattern;

	private boolean typeSystemInitialized = false;
	private Type sentimentT = null;
    private Feature sentimentScoreF = null;

	public void typeSystemInit(JCas jCas) throws AnalysisEngineProcessException {
		typeSystemInitialized = true;
		TypeSystem aTypeSystem = jCas.getTypeSystem();
		sentimentT = aTypeSystem.getType(sentimentClassName);
        sentimentScoreF = sentimentT.getFeatureByBaseName("score");
	}

	@Override
	public void initialize(UimaContext context)
			throws ResourceInitializationException {
		// Retrieve the parameters from the context
		inputFile = new File(
				(String) context.getConfigParameterValue(PARAM_INPUT_MATCH));
		sentimentClassName = (String) context
				.getConfigParameterValue(PARAM_SENTIMENT_CLASS_NAME);

		// Be sure the patterns file exists
		if (!inputFile.exists()) {
			logger.fatal("input file `" + inputFile.getAbsolutePath()
					+ "' does not exist");
			throw new ResourceInitializationException();
		}

		// Read the patterns...
		try {
			pattern = Utils.PatternFactory(FileUtils.readLines(inputFile));
			logger.info("Pattern is `" + pattern.toString() + "'");
		} catch (IOException ex) {
			logger.fatal("Error when reading `" + inputFile.getAbsolutePath()
					+ "'", ex);
			throw new ResourceInitializationException(ex);
		}
	}

	/**
	 * Find all matching patterns in all CitationContext.
	 *
	 * @param jCas
	 *
	 * @throws AnalysisEngineProcessException
	 */
	@Override
	public void process(JCas jCas) throws AnalysisEngineProcessException {
		if (!typeSystemInitialized) {
			typeSystemInit(jCas);
		}

		for (CitationContext context : JCasUtil.select(jCas,
				CitationContext.class)) {
			Matcher match = pattern.matcher(context.getCoveredText());
			while (match.find()) {
				try {
					AnnotationFS sentiment = jCas.getCas().createAnnotation(
							sentimentT,
							context.getBegin() + match.start(),
							context.getBegin() + match.end());
                    sentiment.setLongValue(sentimentScoreF, DEFAULT_SCORE);
                    jCas.addFsToIndexes(sentiment);
				} catch (Exception ex) {
					throw new AnalysisEngineProcessException(ex);
				}
			}
		}
	}
}

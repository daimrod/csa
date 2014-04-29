package jgreg.internship.nii.AE;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jgreg.internship.nii.Utils.Utils;
import jgreg.internship.nii.types.CitationContext;
import jgreg.internship.nii.types.Token;

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

/**
 * Find matches recognized by the patterns in PARAM_PATTERN_FILE and add the
 * Sentiment annotation denoted by PARAM_SENTIMENT_CLASS_NAME.
 *
 * @author Grégoire Jadi
 */
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
	public final static String PARAM_PATTERN_FILE = "patternFileName";
	@ConfigurationParameter(name = PARAM_PATTERN_FILE, mandatory = true)
	private String patternFileName;
	private File patternFile;

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

	/**
	 * This is a double hack.
	 *
	 * Firstly, it uses UIMA introspection to find the type of annotation to use
	 * and the score feature from it because we can only pass "basic" type to
	 * Analysis Engine with the @ConfigurationParameter thing.
	 *
	 * Secondly, we use a flag to run this only once because we need a JCas to
	 * access the TypeSystem, but we don't need to do it for each JCas.
	 *
	 * @param jCas
	 *
	 * @throws AnalysisEngineProcessException
	 */
	public void typeSystemInit(JCas jCas) throws AnalysisEngineProcessException {
		typeSystemInitialized = true;
		TypeSystem aTypeSystem = jCas.getTypeSystem();
		sentimentT = aTypeSystem.getType(sentimentClassName);
		sentimentScoreF = sentimentT.getFeatureByBaseName("score");
	}

	@Override
	public void initialize(UimaContext context)
			throws ResourceInitializationException {
		super.initialize(context);

		// Retrieve the parameters from the context
		patternFile = new File(patternFileName);

		// Be sure the patterns file exists
		if (!patternFile.exists()) {
			logger.fatal("input file `" + patternFile.getAbsolutePath()
					+ "' does not exist");
			throw new ResourceInitializationException();
		}

		// Read the patterns...
		try {
			pattern = buildPattern(FileUtils.readLines(patternFile));
		} catch (IOException ex) {
			logger.fatal("Error when reading `" + patternFile.getAbsolutePath()
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

		Map<CitationContext, Collection<Token>> map = JCasUtil.indexCovered(
				jCas, CitationContext.class, Token.class);

		for (CitationContext context : JCasUtil.select(jCas,
				CitationContext.class)) {

			// String toMatch = buildString(new ArrayList<>(map.get(context)));
			String toMatch = context.getCoveredText();

			Matcher match = pattern.matcher(toMatch);
			while (match.find()) {
				try {
					AnnotationFS sentiment = jCas.getCas().createAnnotation(
							sentimentT, context.getBegin() + match.start(),
							context.getBegin() + match.end());
					sentiment.setLongValue(sentimentScoreF, DEFAULT_SCORE);
					jCas.addFsToIndexes(sentiment);
				} catch (Exception ex) {
					throw new AnalysisEngineProcessException(ex);
				}
			}
		}
	}

	/**
	 * Build a String combining the text and the POS tags.
	 *
	 * word1/tag1 word2/tag2 ...
	 *
	 * @param jCas
	 * @param tokens
	 * @return
	 */
	private String buildString(List<Token> tokens) {
		StringBuilder acc = new StringBuilder();

		if (tokens.size() > 0) {
			Token last = tokens.remove(tokens.size() - 1);
			for (Token token : tokens) {
				acc.append(token.getCoveredText()).append('/')
						.append(token.getPOS()).append(' ');
			}
			acc.append(last.getCoveredText()).append('/').append(last.getPOS());
		}
		return acc.toString();
	}

	public static Pattern buildPattern(List<String> list) {
		StringBuilder acc = new StringBuilder();
		if (list.size() > 0) {
			String last = list.remove(list.size() - 1);

			for (String s : list) {
				acc.append(s).append('|');
			}
			acc.append(last);
		}
		return Pattern.compile(acc.toString());
	}
}

package jgreg.internship.nii.AE;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.tokensregex.TokenSequenceMatcher;
import edu.stanford.nlp.ling.tokensregex.TokenSequencePattern;

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
	private List<TokenSequencePattern> patterns;

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
			patterns = FileUtils.readLines(patternFile).stream()
					.filter(string -> !string.trim().isEmpty())
					.map(line -> TokenSequencePattern.compile(line))
					.collect(Collectors.toList());
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

		// This map is used to find the Token covered by a given CitationContext
		Map<CitationContext, Collection<Token>> map = JCasUtil.indexCovered(
				jCas, CitationContext.class, Token.class);

		// For all CitationContext
		for (CitationContext context : JCasUtil.select(jCas,
				CitationContext.class)) {

			// get the tokens
			List<Token> tokens = new LinkedList(map.get(context));
			// and the equivalent annotations in StanfordNLP
			List<CoreLabel> labels = Utils.convertUIMA2STANFORD(tokens);

			// Then, for all patterns
			for (TokenSequencePattern pattern : patterns) {
				TokenSequenceMatcher matcher = pattern.getMatcher(labels);
				// Let's try to match
				while (matcher.find()) {
					try {
						// matcher.start is the index of the first element that
						// matches
						int begin = tokens.get(matcher.start()).getBegin();
						// matcher.end is the index of the first next element
						// that doesn't match
						int end = tokens.get(matcher.end() - 1).getEnd();

						AnnotationFS sentiment = jCas.getCas()
								.createAnnotation(sentimentT, begin, end);
						sentiment.setLongValue(sentimentScoreF, DEFAULT_SCORE);
						jCas.addFsToIndexes(sentiment);
					} catch (Exception ex) {
						throw new AnalysisEngineProcessException(ex);
					}
				}
			}
		}
	}
}

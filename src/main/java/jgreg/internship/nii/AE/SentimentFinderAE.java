package jgreg.internship.nii.AE;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jgreg.internship.nii.Utils.Utils;
import jgreg.internship.nii.types.CitationContext;
import jgreg.internship.nii.types.Sentiment;
import jgreg.internship.nii.types.Token;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
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
public class SentimentFinderAE extends
		org.apache.uima.fit.component.JCasAnnotator_ImplBase {
	private static final Logger logger = Logger
			.getLogger(SentimentFinderAE.class.getCanonicalName());

	/**
	 * Default score for sentiment annotation
	 */
	private Integer DEFAULT_SCORE = 1;

	/**
	 * The name of the file in which we will look for patterns.
	 */
	public final static String MAPPING_FILE = "mappingFileName";
	@ConfigurationParameter(name = MAPPING_FILE, mandatory = true)
	private String mappingFileName;
	private File mappingFile;

	/**
	 * The patterns to match in the method process(JCas).
	 */
	private Map<String, TokenSequencePattern> patterns;

	@Override
	public void initialize(UimaContext context)
			throws ResourceInitializationException {
		super.initialize(context);

		// Retrieve the parameters from the context
		mappingFile = new File(mappingFileName);

		// Be sure the patterns file exists
		if (!mappingFile.exists()) {
			logger.fatal("input file `" + mappingFileName + "' does not exist");
			throw new ResourceInitializationException();
		}

		// Read the mapping
		Map<String, Set<String>> mapping = new HashMap<>();
		try {
			for (String line : Utils.readLines(mappingFile)) {
				// data[0] = class name
				// data[1] = filename
				String[] data = line.split(";");
				if (data.length != 2) {
					logger.warn("ill-formed line `" + data.toString() + "'");
					continue;
				}
				if (!mapping.containsKey(data[0])) {
					mapping.put(data[0], new HashSet<>());
				}
				mapping.get(data[0]).add(data[1]);
			}
		} catch (IOException ex) {
			throw new ResourceInitializationException(ex);
		}

		// Initialize the patterns for all classes
		patterns = new HashMap<>();
		try {
			for (String className : mapping.keySet()) {
				List<String> strs = new LinkedList<>();
				for (String filename : mapping.get(className)) {
					strs.add(StringUtils.join(
							Utils.readLines(new File(filename)).stream()
									.map(line -> "(" + line + ")").iterator(),
							"|"));
				}
				patterns.put(className, TokenSequencePattern
						.compile(StringUtils.join(strs, "|")));
			}
		} catch (IOException ex) {
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

            // Let's try to match!
            for (String className : patterns.keySet()) {
                TokenSequenceMatcher matcher = patterns.get(className).getMatcher(labels);

                while (matcher.find()) {
                    try {
                        // matcher.start is the index of the first element that
                        // matches
                        int begin = tokens.get(matcher.start()).getBegin();
                        // matcher.end is the index of the first next element
                        // that doesn't match
                        int end = tokens.get(matcher.end() - 1).getEnd();

                        Sentiment sentiment = new Sentiment(jCas);
                        sentiment.setBegin(begin);
                        sentiment.setEnd(end);
                        sentiment.setScore(DEFAULT_SCORE);
                        sentiment.setName(className);
                        sentiment.setContext(context);
                        sentiment.addToIndexes();
                    } catch (Exception ex) {
                        throw new AnalysisEngineProcessException(ex);
                    }
                }
            }
		}
	}
}

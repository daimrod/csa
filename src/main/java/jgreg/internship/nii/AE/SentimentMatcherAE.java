package jgreg.internship.nii.AE;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jgreg.internship.nii.RES.MatcherRES;
import jgreg.internship.nii.RES.SentimentAttr;
import jgreg.internship.nii.Utils.Utils;
import jgreg.internship.nii.types.CitationContext;
import jgreg.internship.nii.types.Sentence;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.descriptor.ExternalResource;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

public class SentimentMatcherAE extends org.apache.uima.fit.component.JCasAnnotator_ImplBase {
    private static final Logger logger = Logger.getLogger(SentimentMatcherAE.class.getCanonicalName());

	/**
	 * The resource that contains the sentiment scores.
	 */
	final static String MATCHER_KEY = "matcher";
    @ExternalResource(key = MATCHER_KEY)
    private MatcherRES<SentimentAttr> matcherRES;

	/**
	 * The name of the file in which we will look for patterns.
	 */
    final static String PARAM_INPUT_MATCH = "input";
    @ConfigurationParameter(name = PARAM_INPUT_MATCH, mandatory = true)
    private File inputFile;

	/**
	 * The name of the class of sentiment that we are matching.
.	 */
    final static String PARAM_MATCHER_NAME = "name";
    @ConfigurationParameter(name = PARAM_MATCHER_NAME, mandatory = true)
    String name;
    
	/**
	 * FIXME The pattern that will be used to determine whether the
	 * given context should be recorded.
	 */
    private Pattern pattern;
    
    @Override
    public void initialize(UimaContext context) throws ResourceInitializationException {
        // Retrieve the parameters from the context
        matcherRES = (MatcherRES<SentimentAttr>) context.getConfigParameterValue(MATCHER_KEY);
        inputFile = new File((String) context.getConfigParameterValue(PARAM_INPUT_MATCH));
        name = (String) context.getConfigParameterValue(PARAM_MATCHER_NAME);

        // Be sure the patterns file exists
        if (!inputFile.exists()) {
            logger.fatal("input file `" + inputFile.getAbsolutePath() + "' does not exist");
            throw new ResourceInitializationException();
        }

        // Read the patterns...
        try {
            pattern = Utils.PatternFactory(FileUtils.readLines(inputFile));
        } catch (IOException ex) {
            logger.fatal("Error when reading `" + inputFile.getAbsolutePath() + "'", ex);
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
        for (CitationContext context : JCasUtil.select(jCas, CitationContext.class)) {
            Matcher match = pattern.matcher(context.getCoveredText());
            while (match.find()) {
                matcherRES.add(context.getPMID(), new SentimentAttr(name, SentimentAttr.DEFAULT_SCORE, match.group()));
            }
        }
    }
}

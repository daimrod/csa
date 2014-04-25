package jgreg.internship.nii.AE;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import jgreg.internship.nii.RES.MatcherRES;
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

public class MatcherAE extends org.apache.uima.fit.component.JCasAnnotator_ImplBase {
    private static final Logger logger = Logger.getLogger(MatcherAE.class.getCanonicalName());

    final static String MATCHER_KEY = "matcher";
    @ExternalResource(key = MATCHER_KEY)
    private MatcherRES matcherRES;

    final static String PARAM_INPUT_MATCH = "input";
    @ConfigurationParameter(name = PARAM_INPUT_MATCH, mandatory = true)
    private File inputFile;

    final static String PARAM_MATCHER_NAME = "name";
    @ConfigurationParameter(name = PARAM_MATCHER_NAME, mandatory = true)
    String name;
    
    private Pattern pattern;
    

    @Override
    public void initialize(UimaContext context) throws ResourceInitializationException {
        matcherRES = (MatcherRES) context.getConfigParameterValue(MATCHER_KEY);
        inputFile = new File((String) context.getConfigParameterValue(PARAM_INPUT_MATCH));
        name = (String) context.getConfigParameterValue(PARAM_MATCHER_NAME);

        if (!inputFile.exists()) {
            logger.fatal("input file `" + inputFile.getAbsolutePath() + "' does not exist");
            throw new ResourceInitializationException();
        }
        
        try {
            pattern = Utils.PatternFactory(FileUtils.readLines(inputFile));
        } catch (IOException ex) {
            logger.fatal("Error when reading `" + inputFile.getAbsolutePath() + "'", ex);
            throw new ResourceInitializationException(ex);
        }
    }
    
    @Override
    public void process(JCas jCas) throws AnalysisEngineProcessException {
        Map<CitationContext, Collection<Sentence>> map = JCasUtil.indexCovered(jCas, CitationContext.class, Sentence.class);
        for (CitationContext context : JCasUtil.select(jCas, CitationContext.class)) {
            for (Sentence sent : map.get(context)) {
                Matcher match = pattern.matcher(sent.getCoveredText());
                while (match.find()) {
                    matcherRES.add(context.getPMID(), match.group());
                }
            }
        }
    }
}

package jgreg.internship.nii.AE;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jgreg.internship.nii.types.Citation;
import jgreg.internship.nii.types.CitationContext;
import jgreg.internship.nii.types.Sentence;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.log4j.Logger;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.StringList;

public class CitationContextExtractorAE extends org.apache.uima.fit.component.JCasAnnotator_ImplBase {
    private static final Logger logger = Logger.getLogger(CitationContextExtractorAE.class.getCanonicalName());

    public static final String PARAM_WINDOW_SIZE = "windowSize";
    @ConfigurationParameter(name = PARAM_WINDOW_SIZE, mandatory = true)
    private Integer windowSize;

    private Set<Sentence> cache = new HashSet<>();

    @Override
    public void process(JCas jCas) throws AnalysisEngineProcessException {
        Map<Citation, Collection<Sentence>> map = JCasUtil.indexCovering(jCas, Citation.class, Sentence.class);

        for (Citation citation : JCasUtil.select(jCas, Citation.class)) {
            Sentence sent = map.get(citation).iterator().next();

            // No need to duplicate the CitationContext
            if (cache.contains(sent)) {
                continue;
            } else {
                cache.add(sent);
            }

            int begin, end;
            // Before
            List<Sentence> precedings = JCasUtil.selectPreceding(Sentence.class, citation, windowSize);
            if (precedings.size() > 0) {
                begin = precedings.get(0).getBegin();
            } else {
                begin = sent.getBegin();
            }

            // After
            List<Sentence> followings = JCasUtil.selectFollowing(Sentence.class, citation, windowSize);
            if (followings.size() > 0) {
                end = followings.get(followings.size() - 1).getEnd();
            } else {
                end = sent.getEnd();
            }
            
            CitationContext context = new CitationContext(jCas);
            context.setBegin(begin);
            context.setEnd(end);
            context.addToIndexes(jCas);
        }
    }
}

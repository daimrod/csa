package jgreg.internship.nii.AE;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import jgreg.internship.nii.types.Citation;
import jgreg.internship.nii.types.CitationContext;
import jgreg.internship.nii.types.Sentence;
import org.apache.log4j.Logger;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

public class CitationContextExtractorAE extends org.apache.uima.fit.component.JCasAnnotator_ImplBase {
    private static final Logger logger = Logger.getLogger(CitationContextExtractorAE.class.getCanonicalName());

    public static final String PARAM_WINDOW_SIZE = "windowSize";
    @ConfigurationParameter(name = PARAM_WINDOW_SIZE, mandatory = true)
    private Integer windowSize;

    @Override
    public void process(JCas jCas) throws AnalysisEngineProcessException {
        Map<Citation, Collection<Sentence>> map = JCasUtil.indexCovering(jCas, Citation.class, Sentence.class);

        for (Citation citation : JCasUtil.select(jCas, Citation.class)) {
            CitationContext context = new CitationContext(jCas);
            context.setPMID(citation.getPMID());

            
            // Before
            List<Sentence> precedings = JCasUtil.selectPreceding(Sentence.class, citation, windowSize);
            if (precedings.size() > 0) {
                context.setBegin(precedings.get(0).getBegin());
            } else {
                Sentence sent = map.get(citation).iterator().next();
                context.setBegin(sent.getBegin());
            }

            // After
            List<Sentence> followings = JCasUtil.selectPreceding(Sentence.class, citation, windowSize);
            if (followings.size() > 0) {
                context.setEnd(followings.get(followings.size() - 1).getEnd());
            } else {
                Sentence sent = map.get(citation).iterator().next();
                context.setEnd(sent.getEnd());
            }
            
            context.addToIndexes();
        }
    }
}

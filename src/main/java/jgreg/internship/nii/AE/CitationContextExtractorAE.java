package jgreg.internship.nii.AE;

import java.util.Collection;
import java.util.LinkedList;
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
        List<Sentence> ring = new LinkedList<>();
        Map<Sentence, Collection<Citation>> map = JCasUtil.indexCovered(jCas, Sentence.class, Citation.class);
        for (Sentence sentence : JCasUtil.select(jCas, Sentence.class)) {
            if (ring.size() == windowSize) {
                ring.remove(0);
            }
            
            ring.add(ring.size(), sentence);
            
            if (map.containsKey(sentence)) {            
                CitationContext context = new CitationContext(jCas);
                context.setBegin(ring.get(0).getBegin());
                context.setEnd(ring.get(ring.size() - 1).getEnd());
                context.addToIndexes();
            }
        }
    }
}

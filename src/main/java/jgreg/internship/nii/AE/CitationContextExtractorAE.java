package jgreg.internship.nii.AE;

import java.util.Collection;
import java.util.LinkedList;
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
        // Let WINDOW_SIZE = 2
        //  /-----\  /-----\ 2 windows (before & after) of WINDOW_SIZE
        // [ s1 s2 s3 s4 s5]
        //   ^^^^^           context before the citation
        //         ^^        the sentence in which the citation occurs
        //            ^^^^^  context after the citation

        LinkedList<Sentence> ring = new LinkedList<>();
        Map<Sentence, Collection<Citation>> map = JCasUtil.indexCovered(jCas, Sentence.class, Citation.class);
        int MIDDLE = windowSize;
        int SIZE_MAX = windowSize * 2 + 1;
        for (Sentence sentence : JCasUtil.select(jCas, Sentence.class)) {
            if (ring.size() == SIZE_MAX) {
                ring.removeFirst();
            }

            ring.add(sentence); // add at the end of the List

            if (ring.size() > MIDDLE &&
                map.containsKey(ring.get(MIDDLE))) {
                for (Citation citation : map.get(ring.get(MIDDLE))) {
                    CitationContext context = new CitationContext(jCas);
                    context.setPMID(citation.getPMID());
                    context.setBegin(ring.getFirst().getBegin());
                    context.setEnd(ring.getLast().getEnd());
                    context.addToIndexes();
                }
            }
        }
    }
}

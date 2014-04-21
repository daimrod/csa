/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jgreg.internship.nii.AE;

import java.util.Collection;
import java.util.Map.Entry;
import jgreg.internship.nii.types.Sentence;
import jgreg.internship.nii.types.Title;
import org.apache.log4j.Logger;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

// Deprecated, the option opennlp.uima.ContainerType did the job
public class SentenceAdjustorAE extends org.apache.uima.fit.component.JCasAnnotator_ImplBase {
    private static final Logger logger = Logger.getLogger(SentenceAdjustorAE.class.getCanonicalName());
    
    @Override
    public void process(JCas jCas) throws AnalysisEngineProcessException {
        for (Entry<Title, Collection<Sentence>> entry : JCasUtil.indexCovering(jCas, Title.class, Sentence.class).entrySet()) {
            Title title = entry.getKey();
            for (Sentence sentence : entry.getValue()) {
                if (sentence.getBegin() == title.getBegin()) {
                    sentence.setBegin(title.getEnd());
                }

                if (sentence.getEnd() == title.getEnd()) {
                    sentence.setEnd(title.getBegin());
                }
            }
        }
    }

}

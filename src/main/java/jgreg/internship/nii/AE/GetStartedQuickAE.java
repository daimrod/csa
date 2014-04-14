package jgreg.internship.nii.AE;

import org.apache.log4j.Logger;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.jcas.JCas;

public class GetStartedQuickAE
    extends org.apache.uima.fit.component.JCasAnnotator_ImplBase {
    private static final Logger logger = Logger.getLogger(GetStartedQuickAE.class.getCanonicalName());
    
    public static final String PARAM_STRING = "paramString";
    @ConfigurationParameter(name = PARAM_STRING)
    private String paramString;

    @Override
    public void process(JCas jCas) throws AnalysisEngineProcessException {
        System.out.println("Hello World! Say `Hi` to " + paramString);
    }

    public static void main( String[] args ) throws Exception {
        logger.info("main");
        JCas jCas = JCasFactory.createJCas();

        AnalysisEngine ae =
            AnalysisEngineFactory.createEngine(GetStartedQuickAE.class,
                                               GetStartedQuickAE.PARAM_STRING, "uimaFIT");

        ae.process(jCas);
    }
}

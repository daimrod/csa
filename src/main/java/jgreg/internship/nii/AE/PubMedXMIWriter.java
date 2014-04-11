package jgreg.internship.nii.AE;

import java.io.File;
import java.io.IOException;
import jgreg.internship.nii.types.ID;
import org.apache.log4j.Logger;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.CasIOUtil;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

public class PubMedXMIWriter
        extends org.apache.uima.fit.component.JCasAnnotator_ImplBase {
    private static final Logger logger = Logger.getLogger(PubMedXMIWriter.class.getCanonicalName());

    public static final String OUTPUT_DIRECTORY = "outputDirectory";
    @ConfigurationParameter(name = OUTPUT_DIRECTORY, mandatory = false, defaultValue="/tmp/xmi/")
    private String outputDirectory;
    
    @Override
    public void process(JCas jCas) throws AnalysisEngineProcessException {
        String pmid = JCasUtil.selectSingle(jCas, ID.class).getPMID();
        
        // Ensure that the outputDirectory exists
        File dir = new File(outputDirectory);
        dir.mkdirs();

        File outputFile = new File(outputDirectory + pmid + ".xmi");

        logger.info("Dumping to `" + outputFile.getAbsolutePath() + "'...");
        try {
            CasIOUtil.writeXmi(jCas, outputFile);
        } catch (IOException ex) {
            logger.fatal("Couldn't dump `" + outputFile + "''", ex);
        }
    }
}

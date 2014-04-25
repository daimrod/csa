package jgreg.internship.nii.AE;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import jgreg.internship.nii.types.ID;
import org.apache.log4j.Logger;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.CasIOUtil;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.TypeSystemUtil;
import org.xml.sax.SAXException;

public class PubMedXMIWriter
        extends org.apache.uima.fit.component.JCasAnnotator_ImplBase {
    private static final Logger logger = Logger.getLogger(PubMedXMIWriter.class.getCanonicalName());

    public static final String OUTPUT_DIRECTORY = "outputDirectory";
    @ConfigurationParameter(name = OUTPUT_DIRECTORY, mandatory = true)
    private File outputDir;

    private boolean ts_dumped = false;

    @Override
    public void initialize(UimaContext context) throws ResourceInitializationException {
        String outputDirectory = (String) context.getConfigParameterValue(OUTPUT_DIRECTORY);
        outputDir = new File(outputDirectory);
        outputDir.mkdirs();                
    }

    
    
    @Override
    public void process(JCas jCas) throws AnalysisEngineProcessException {
        if (!ts_dumped) {
            writeTs(jCas);
        }
        String pmid = JCasUtil.selectSingle(jCas, ID.class).getPMID();


        File outputFile = new File(outputDir, pmid + ".xmi");

        logger.info("Dumping to `" + outputFile.getAbsolutePath() + "'...");
        try {
            CasIOUtil.writeXmi(jCas, outputFile);
        } catch (IOException ex) {
            logger.fatal("Couldn't dump `" + outputFile + "''", ex);
        }
    }

    private void writeTs(JCas jcas) throws AnalysisEngineProcessException {
        try (OutputStream os = new FileOutputStream(
                new File(outputDir, "ts.xml"))) {
            TypeSystemUtil.typeSystem2TypeSystemDescription(
                    jcas.getTypeSystem())
                    .toXML(os);
        } catch (IOException | SAXException ex) {
            throw new AnalysisEngineProcessException(ex);
        }
        ts_dumped = true;
    }
}

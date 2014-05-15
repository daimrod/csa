package jgreg.internship.nii.AE;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import jgreg.internship.nii.types.Filename;

import org.apache.log4j.Logger;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.impl.XmiCasDeserializer;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

// TODO: Auto-generated Javadoc
/**
 * The Class XMIReaderAE.
 *
 * @author Grégoire Jadi
 */
public class XMIReaderAE extends
		org.apache.uima.fit.component.JCasAnnotator_ImplBase {

	/** The Constant logger. */
	private static final Logger logger = Logger.getLogger(XMIReaderAE.class
			.getCanonicalName());

	@Override
	public void process(JCas jCas) throws AnalysisEngineProcessException {
        JCas originalText;
        try {
            originalText = jCas.getView("originalText");
        } catch (CASException ex) {
            throw new AnalysisEngineProcessException(ex);
        }

        Filename filename = JCasUtil.selectSingle(originalText, Filename.class);
        logger.info("Deserializing `" + filename.getFilename() + "'...");
        File file = new File(filename.getFilename());
        
		try {
			InputStream inputStream = new FileInputStream(file);
			XmiCasDeserializer.deserialize(inputStream, jCas.getCas());
		} catch (Exception ex) {
			logger.fatal(null, ex);
		}
	}

}

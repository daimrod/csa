package jgreg.internship.nii.AE;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import jgreg.internship.nii.types.ID;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.Feature;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.TypeSystem;
import org.apache.uima.cas.impl.XmiCasSerializer;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.TypeSystemUtil;
import org.xml.sax.SAXException;

/**
 * Dump all CAS as XMI in {@link #OUTPUT_DIRECTORY}. The complete TypeSystem is
 * also dumped in {@link #OUTPUT_DIRECTORY}/ts.xml.
 *
 * @author Grégoire Jadi
 */
public class XMIWriter extends
		org.apache.uima.fit.component.JCasAnnotator_ImplBase {

	/** The Constant logger. */
	private static final Logger logger = Logger.getLogger(XMIWriter.class
			.getCanonicalName());

	/**
	 * The directory in which the XMI and XML are written.
	 */
	public static final String OUTPUT_DIRECTORY = "outputDirName";
	@ConfigurationParameter(name = OUTPUT_DIRECTORY, mandatory = true)
	private String outputDirName;

	/** The output dir. */
	private File outputDir;

	/**
	 * Empty the directory before writing to it or not.
	 */
	public static final String CLEAR_DIRECTORY = "clearDirectory";
	@ConfigurationParameter(name = CLEAR_DIRECTORY, mandatory = false, defaultValue = "false")
	private boolean clearDirectory;

	/** The name of the Type annotation that contains the name of a CAS. */
	public static final String NAME_TYPE = "nameTYPE";
	@ConfigurationParameter(name = NAME_TYPE, mandatory = true)
	private String nameType;

	/** The name t. */
	private Type nameT;

	/** The name of the Feature that contains the name of a CAS. */
	public static final String NAME_FEATURE = "nameFeature";

	/** The name feature. */
	@ConfigurationParameter(name = NAME_FEATURE, mandatory = true)
	private String nameFeature;

	/** The name f. */
	private Feature nameF;

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.apache.uima.fit.component.JCasAnnotator_ImplBase#initialize(org.apache
	 * .uima.UimaContext)
	 */
	@Override
	public void initialize(UimaContext context)
			throws ResourceInitializationException {
		super.initialize(context);

		outputDir = new File(outputDirName);

		if (clearDirectory) {
			try {
				FileUtils.deleteDirectory(outputDir);
			} catch (IOException ex) {
				throw new ResourceInitializationException(ex);
			}
		}
		outputDir.mkdirs();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.apache.uima.analysis_component.JCasAnnotator_ImplBase#process(org
	 * .apache.uima.jcas.JCas)
	 */
	@Override
	public void process(JCas jCas) throws AnalysisEngineProcessException {
		if (!ts_dumped) {
			writeTs(jCas);
		}
		String pmid = JCasUtil.selectSingle(jCas, ID.class).getPMID();

		File outputFile = new File(outputDir, pmid + ".xmi");

		logger.info("Dumping to `" + outputFile.getAbsolutePath() + "'...");
		try {
			OutputStream outputStream = new FileOutputStream(outputFile);
			XmiCasSerializer.serialize(jCas.getCas(), outputStream);
		} catch (SAXException | IOException ex) {
			logger.fatal("Couldn't dump `" + outputFile + "''", ex);
		}
	}

	/**
	 * Dump the Type System to <outputDir>/ts.xml.
	 *
	 * If everything was cool and fine we would not need this function because
	 * jcasgen.sh would merge the TypeSystem for us. Unfortunately it does not
	 * seem to be possible to do it without setting up the UIMA+Eclipse combo.
	 *
	 * Since the XML version of the Type System is used by the annotationViewer
	 * with the XMI, this is a good place to put it here.
	 *
	 * @param jcas
	 *            the jcas
	 * @throws AnalysisEngineProcessException
	 *             if the TypeSystem can not be converted to XML or if
	 *             <outputDir>/ts.xml can not be written.
	 */
	private void writeTs(JCas jcas) throws AnalysisEngineProcessException {
		try (OutputStream os = new FileOutputStream(new File(outputDir,
				"ts.xml"))) {
			TypeSystem ts = jcas.getTypeSystem();
			TypeSystemUtil.typeSystem2TypeSystemDescription(ts).toXML(os);
			nameT = ts.getType(nameType);
			nameF = nameT.getFeatureByBaseName(nameFeature);
		} catch (IOException | SAXException ex) {
			throw new AnalysisEngineProcessException(ex);
		}
		ts_dumped = true;
	}

	/**
	 * A flag to determine whether or not the TypeSystem has been dumped. @see
	 * writeTs
	 */
	private boolean ts_dumped = false;
}

package jgreg.internship.nii.WF;

import jgreg.internship.nii.AE.CoCitationExtractorAE;
import jgreg.internship.nii.CR.DirectoryReaderCR;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;

// TODO: Auto-generated Javadoc
/**
 * This simple workflow runs the DirectoryReaderCR and the
 * CoCitationExtractorAE.
 */
public class TestWF02 {

	/**
	 * The main method.
	 *
	 * @param args
	 *            the arguments
	 * @throws Exception
	 *             the exception
	 */
	public static void main(String[] args) throws Exception {
		CollectionReaderDescription reader = CollectionReaderFactory
				.createReaderDescription(DirectoryReaderCR.class,
						DirectoryReaderCR.INPUT_DIRECTORY,
						"/home/daimrod/corpus/pubmed/cpa_dump/");

		AnalysisEngineDescription writer = AnalysisEngineFactory
				.createEngineDescription(CoCitationExtractorAE.class,
						CoCitationExtractorAE.OUTPUT_FILE,
						"/tmp/cocitation.output");

		SimplePipeline.runPipeline(reader, writer);
	}
}

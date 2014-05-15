package jgreg.internship.nii.WF;

import jgreg.internship.nii.AE.XMIWriter;
import jgreg.internship.nii.CR.DirectoryReaderCR;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;

// TODO: Auto-generated Javadoc
/**
 * This simple workflow runs the PubMedReaderCR and the PubMedXMIWriter.
 */
public class TestWF01 {

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
				.createEngineDescription(XMIWriter.class);

		SimplePipeline.runPipeline(reader, writer);
	}
}

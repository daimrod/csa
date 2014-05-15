package jgreg.internship.nii.WF;

import jgreg.internship.nii.AE.XMIWriter;
import jgreg.internship.nii.CR.DirectoryReaderCR;
import opennlp.uima.sentdetect.SentenceDetector;
import opennlp.uima.sentdetect.SentenceModelResourceImpl;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.factory.ExternalResourceFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.resource.ExternalResourceDescription;

// TODO: Auto-generated Javadoc
/**
 * This simple workflow runs the DirectoryReaderCR and the CoCitationExtractor.
 */
public class TestWF03 {

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
						"/home/daimrod/corpus/pubmed/cpa_dump/PLoS_Med/");

		ExternalResourceDescription sentenceModel = ExternalResourceFactory
				.createExternalResourceDescription(
						SentenceModelResourceImpl.class,
						"file:org/apache/ctakes/core/sentdetect/sd-med-model.zip");

		AnalysisEngineDescription sentenceSplitter = AnalysisEngineFactory
				.createEngineDescription(SentenceDetector.class,
						"opennlp.uima.ModelName", sentenceModel,
						"opennlp.uima.SentenceType",
						"jgreg.internship.nii.types.Sentence",
						"opennlp.uima.IsRemoveExistingAnnotations", false);

		AnalysisEngineDescription writer = AnalysisEngineFactory
				.createEngineDescription(XMIWriter.class);

		SimplePipeline.runPipeline(reader, sentenceSplitter, writer);
	}
}

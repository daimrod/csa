package jgreg.internship.nii.WF;

import jgreg.internship.nii.AE.XMIWriter;
import jgreg.internship.nii.CR.PubMedReaderCR;
import opennlp.uima.sentdetect.SentenceDetector;
import opennlp.uima.sentdetect.SentenceModelResourceImpl;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.factory.ExternalResourceFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.resource.ExternalResourceDescription;

/**
 * This simple workflow runs the PubMedReaderCR and the CoCitationExtractor.
 */
public class TestWF03 {

    public static void main(String[] args) throws Exception {
        CollectionReaderDescription reader
                = CollectionReaderFactory.createReaderDescription(
                        PubMedReaderCR.class,
                        PubMedReaderCR.INPUT_DIRECTORY, "/home/daimrod/corpus/pubmed/cpa_dump/PLoS_Med/");

        ExternalResourceDescription sentenceModel
                = ExternalResourceFactory.createExternalResourceDescription(
                        SentenceModelResourceImpl.class,
                        "file:org/apache/ctakes/core/sentdetect/sd-med-model.zip");

        AnalysisEngineDescription sentenceSplitter
                = AnalysisEngineFactory.createEngineDescription(
                        SentenceDetector.class,
                        "opennlp.uima.ModelName",
                        sentenceModel,
                        "opennlp.uima.SentenceType",
                        "jgreg.internship.nii.types.Sentence",
                        "opennlp.uima.IsRemoveExistingAnnotations",
                        false);

        AnalysisEngineDescription writer
                = AnalysisEngineFactory.createEngineDescription(
                        XMIWriter.class);

        SimplePipeline.runPipeline(reader,
                sentenceSplitter,
                writer);
    }
}

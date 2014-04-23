package jgreg.internship.nii.WF;

import jgreg.internship.nii.AE.PubMedXMIWriter;
import jgreg.internship.nii.CR.PubMedParserAE;
import jgreg.internship.nii.CR.PubMedReaderCR;
import jgreg.internship.nii.types.Paragraph;
import jgreg.internship.nii.types.Section;
import jgreg.internship.nii.types.Sentence;
import jgreg.internship.nii.types.Title;
import opennlp.uima.sentdetect.SentenceDetector;
import opennlp.uima.sentdetect.SentenceModelResourceImpl;
import opennlp.uima.tokenize.Tokenizer;
import opennlp.uima.tokenize.TokenizerModelResourceImpl;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.cas.CAS;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.factory.ExternalResourceFactory;
import org.apache.uima.fit.factory.TypePrioritiesFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.resource.ExternalResourceDescription;

/**
 * This my full Pipeline
 */
public class Pipeline {

    public static void main(String[] args) throws Exception {
        CollectionReaderDescription reader
                = CollectionReaderFactory.createReaderDescription(
                        PubMedReaderCR.class,
                        PubMedReaderCR.INPUT_DIRECTORY, "/home/daimrod/corpus/pubmed/cpa_dump/PLoS_Med/");
        
        AnalysisEngineDescription xmlParser
                = AnalysisEngineFactory.createEngineDescription(
                        PubMedParserAE.class
                );
              

        ExternalResourceDescription sentenceModel
                = ExternalResourceFactory.createExternalResourceDescription(
                        SentenceModelResourceImpl.class,
                        "file:org/apache/ctakes/core/sentdetect/sd-med-model.zip");

        AnalysisEngineDescription sentenceDetector
                = AnalysisEngineFactory.createEngineDescription(
                        SentenceDetector.class
                        , "opennlp.uima.ModelName"
                        , sentenceModel
                        , "opennlp.uima.SentenceType"
                        , "jgreg.internship.nii.types.Sentence"
                        , "opennlp.uima.ContainerType"
                        , "jgreg.internship.nii.types.Paragraph"
                        , "opennlp.uima.IsRemoveExistingAnnotations"
                        , false
                        );
        
        ExternalResourceDescription tokenModel
                = ExternalResourceFactory.createExternalResourceDescription(
                        TokenizerModelResourceImpl.class
                        , "file:opennlp/uima/models/en-token.bin");

        AnalysisEngineDescription tokenizer
                = AnalysisEngineFactory.createEngineDescription(
                        Tokenizer.class
                        , "opennlp.uima.ModelName"
                        , tokenModel
                        , "opennlp.uima.SentenceType"
                        , "jgreg.internship.nii.types.Sentence"
                        , "opennlp.uima.TokenType"
                        , "jgreg.internship.nii.types.Token");

        AnalysisEngineDescription XMIWriter
                = AnalysisEngineFactory.createEngineDescription(
                        PubMedXMIWriter.class
                        , PubMedXMIWriter.OUTPUT_DIRECTORY
                        , "/tmp/xmi/");

        /* The type priority is important especially to retrieve tokens. The
         rest of the order is not accurate but it does not matter.*/
        AggregateBuilder builder
                = new AggregateBuilder(
                        null,
                        TypePrioritiesFactory.createTypePriorities(
                                Section.class,
                                Paragraph.class,
                                Sentence.class,
                                Title.class),
                        null);
        
        builder.add(xmlParser);
        builder.add(sentenceDetector
                , CAS.NAME_DEFAULT_SOFA
                , "parsed");
        builder.add(tokenizer
                , CAS.NAME_DEFAULT_SOFA
                , "parsed");
        builder.add(XMIWriter
                , CAS.NAME_DEFAULT_SOFA
                , "parsed");
        SimplePipeline.runPipeline(reader,
                builder.createAggregateDescription());
    }
}

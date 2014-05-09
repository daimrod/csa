package jgreg.internship.nii.WF;

import java.util.List;

import jgreg.internship.nii.AE.CitationContextExtractorAE;
import jgreg.internship.nii.AE.PubMedParserAE;
import jgreg.internship.nii.AE.PubMedXMIWriter;
import jgreg.internship.nii.AE.SentimentAnnotator;
import jgreg.internship.nii.AE.SentimentMatcherAE;
import jgreg.internship.nii.AE.SentimentStatisticsAE;
import jgreg.internship.nii.CR.PubMedReaderCR;
import jgreg.internship.nii.RES.ArticlesDB;
import jgreg.internship.nii.types.Citation;
import jgreg.internship.nii.types.CitationContext;
import jgreg.internship.nii.types.ID;
import jgreg.internship.nii.types.Negative;
import jgreg.internship.nii.types.Neutral;
import jgreg.internship.nii.types.Paragraph;
import jgreg.internship.nii.types.Positive;
import jgreg.internship.nii.types.Section;
import jgreg.internship.nii.types.Sentence;
import jgreg.internship.nii.types.Sentiment;
import jgreg.internship.nii.types.Title;
import jgreg.internship.nii.types.Token;

import opennlp.uima.postag.POSModelResourceImpl;
import opennlp.uima.postag.POSTagger;
import opennlp.uima.sentdetect.SentenceDetector;
import opennlp.uima.sentdetect.SentenceModelResourceImpl;
import opennlp.uima.tokenize.Tokenizer;
import opennlp.uima.tokenize.TokenizerModelResourceImpl;

import org.apache.log4j.Logger;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
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
	private static final Logger logger = Logger.getLogger(Pipeline.class
			.getCanonicalName());

	private static Integer WINDOW_SIZE = null;

	public static void process(String inputDirectory, String outputDirectory,
			List<String> articlesFilename, Integer windowSize,
			List<String> citedPMIDS) throws Exception {

		/*
		 * Resources
		 */
		// Articles DB
		ExternalResourceDescription articlesDB = ExternalResourceFactory
				.createExternalResourceDescription(ArticlesDB.class, "");

		// Sentence Model
		ExternalResourceDescription sentenceModel = ExternalResourceFactory
				.createExternalResourceDescription(
						SentenceModelResourceImpl.class,
						"file:org/apache/ctakes/core/sentdetect/sd-med-model.zip");
		// Token Model
		ExternalResourceDescription tokenModel = ExternalResourceFactory
				.createExternalResourceDescription(
						TokenizerModelResourceImpl.class,
						"file:opennlp/uima/models/en-token.bin");

		// POS tagger Model
		ExternalResourceDescription POSModel = ExternalResourceFactory
				.createExternalResourceDescription(POSModelResourceImpl.class,
						"file:opennlp/uima/models/en-pos-perceptron.bin");

		/*
		 * Collection Reader
		 */
		CollectionReaderDescription reader = CollectionReaderFactory
				.createReaderDescription(PubMedReaderCR.class,
						PubMedReaderCR.INPUT_DIRECTORY, inputDirectory,
						PubMedReaderCR.INPUT_LIST,
						"/home/daimrod/corpus/pubmed/dev/test2.lst"); // FIXME

		/*
		 * Analysis Engine
		 */
		// Parser XML
		AnalysisEngineDescription xmlParser = AnalysisEngineFactory
				.createEngineDescription(PubMedParserAE.class,
						PubMedParserAE.PARAM_DB, articlesDB);

		// Sentence Detector
		AnalysisEngineDescription sentenceDetector = AnalysisEngineFactory
				.createEngineDescription(SentenceDetector.class,
						"opennlp.uima.ModelName", sentenceModel,
						"opennlp.uima.SentenceType",
						"jgreg.internship.nii.types.Sentence",
						"opennlp.uima.ContainerType",
						"jgreg.internship.nii.types.Paragraph");

		// Citation Context Detector
		AnalysisEngineDescription citationContextDetector = AnalysisEngineFactory
				.createEngineDescription(CitationContextExtractorAE.class,
						CitationContextExtractorAE.PARAM_WINDOW_SIZE,
						windowSize);

		// Tokenizer
		AnalysisEngineDescription tokenizer = AnalysisEngineFactory
				.createEngineDescription(Tokenizer.class,
						"opennlp.uima.ModelName", tokenModel,
						"opennlp.uima.SentenceType",
						"jgreg.internship.nii.types.Sentence",
						"opennlp.uima.TokenType",
						"jgreg.internship.nii.types.Token");

		// POS Tagger
		AnalysisEngineDescription POSTagger = AnalysisEngineFactory
				.createEngineDescription(POSTagger.class,
						"opennlp.uima.ModelName", POSModel,
						"opennlp.uima.SentenceType",
						"jgreg.internship.nii.types.Sentence",
						"opennlp.uima.TokenType",
						"jgreg.internship.nii.types.Token",
						"opennlp.uima.POSFeature", "POS");

		// XMI Writer
		AnalysisEngineDescription XMIWriter = AnalysisEngineFactory
				.createEngineDescription(PubMedXMIWriter.class,
						PubMedXMIWriter.OUTPUT_DIRECTORY, outputDirectory);

		/*
		 * The type priority is important especially to retrieve tokens. The
		 * rest of the order is not accurate but it does not matter.
		 */
		AggregateBuilder builder = new AggregateBuilder(null,
				TypePrioritiesFactory.createTypePriorities(ID.class,
						Title.class, Section.class, Paragraph.class,
						CitationContext.class, Sentence.class, Citation.class,
						Token.class, Sentiment.class, Negative.class,
						Neutral.class, Positive.class), null);

		builder.add(xmlParser);
		builder.add(sentenceDetector);
		builder.add(citationContextDetector);
		builder.add(tokenizer);
		builder.add(POSTagger);
		builder.add(XMIWriter);
		SimplePipeline
				.runPipeline(reader, builder.createAggregateDescription());
	}

	public static void main(String[] args) throws Exception {

		ExternalResourceDescription articlesDB = ExternalResourceFactory
				.createExternalResourceDescription(ArticlesDB.class, "");

		CollectionReaderDescription reader = CollectionReaderFactory
				.createReaderDescription(PubMedReaderCR.class,
						PubMedReaderCR.INPUT_DIRECTORY,
						"/home/daimrod/corpus/pubmed/corpus/",
						PubMedReaderCR.INPUT_LIST,
						"/home/daimrod/corpus/pubmed/dev/test2.lst");

		AnalysisEngineDescription xmlParser = AnalysisEngineFactory
				.createEngineDescription(PubMedParserAE.class,
						PubMedParserAE.PARAM_DB, articlesDB);

		ExternalResourceDescription sentenceModel = ExternalResourceFactory
				.createExternalResourceDescription(
						SentenceModelResourceImpl.class,
						"file:org/apache/ctakes/core/sentdetect/sd-med-model.zip");

		AnalysisEngineDescription sentenceDetector = AnalysisEngineFactory
				.createEngineDescription(SentenceDetector.class,
						"opennlp.uima.ModelName", sentenceModel,
						"opennlp.uima.SentenceType",
						"jgreg.internship.nii.types.Sentence",
						"opennlp.uima.ContainerType",
						"jgreg.internship.nii.types.Paragraph");

		AnalysisEngineDescription citationContextExtractor = AnalysisEngineFactory
				.createEngineDescription(CitationContextExtractorAE.class,
						CitationContextExtractorAE.PARAM_WINDOW_SIZE,
						WINDOW_SIZE);

		ExternalResourceDescription tokenModel = ExternalResourceFactory
				.createExternalResourceDescription(
						TokenizerModelResourceImpl.class,
						"file:opennlp/uima/models/en-token.bin");

		AnalysisEngineDescription tokenizer = AnalysisEngineFactory
				.createEngineDescription(Tokenizer.class,
						"opennlp.uima.ModelName", tokenModel,
						"opennlp.uima.SentenceType",
						"jgreg.internship.nii.types.Sentence",
						"opennlp.uima.TokenType",
						"jgreg.internship.nii.types.Token");

		ExternalResourceDescription POSModel = ExternalResourceFactory
				.createExternalResourceDescription(POSModelResourceImpl.class,
						"file:opennlp/uima/models/en-pos-perceptron.bin");

		AnalysisEngineDescription POSTagger = AnalysisEngineFactory
				.createEngineDescription(POSTagger.class,
						"opennlp.uima.ModelName", POSModel,
						"opennlp.uima.SentenceType",
						"jgreg.internship.nii.types.Sentence",
						"opennlp.uima.TokenType",
						"jgreg.internship.nii.types.Token",
						"opennlp.uima.POSFeature", "POS");

		AnalysisEngineDescription positiveMatcher = AnalysisEngineFactory
				.createEngineDescription(
						SentimentMatcherAE.class,
						SentimentMatcherAE.PARAM_PATTERN_FILE,
						"/home/daimrod/src/java/nii-internship/csa/src/main/resources/jgreg/internship/nii/patterns/positive.pat",
						SentimentMatcherAE.PARAM_SENTIMENT_CLASS_NAME,
						"jgreg.internship.nii.types.Positive");

		AnalysisEngineDescription neutralMatcher = AnalysisEngineFactory
				.createEngineDescription(
						SentimentMatcherAE.class,
						SentimentMatcherAE.PARAM_PATTERN_FILE,
						"/home/daimrod/src/java/nii-internship/csa/src/main/resources/jgreg/internship/nii/patterns/neutral.pat",
						SentimentMatcherAE.PARAM_SENTIMENT_CLASS_NAME,
						"jgreg.internship.nii.types.Neutral");

		AnalysisEngineDescription negativeMatcher = AnalysisEngineFactory
				.createEngineDescription(
						SentimentMatcherAE.class,
						SentimentMatcherAE.PARAM_PATTERN_FILE,
						"/home/daimrod/src/java/nii-internship/csa/src/main/resources/jgreg/internship/nii/patterns/negative.pat",
						SentimentMatcherAE.PARAM_SENTIMENT_CLASS_NAME,
						"jgreg.internship.nii.types.Negative");

		AnalysisEngineDescription XMIWriter = AnalysisEngineFactory
				.createEngineDescription(PubMedXMIWriter.class,
						PubMedXMIWriter.OUTPUT_DIRECTORY,
						"/home/daimrod/corpus/pubmed/dev/xmi/");

		AnalysisEngineDescription sentimentAnnotator = AnalysisEngineFactory
				.createEngineDescription(SentimentAnnotator.class,
						SentimentAnnotator.PARAM_DB, articlesDB);

		AnalysisEngineDescription sentimentStatistics = AnalysisEngineFactory
				.createEngineDescription(SentimentStatisticsAE.class,
						SentimentStatisticsAE.OUTPUT_FILE,
						"/home/daimrod/corpus/pubmed/dev/output/data-all.out",
						SentimentStatisticsAE.PARAM_STRATEGY, "all",
						SentimentStatisticsAE.INPUT_FILE,
						"/home/daimrod/corpus/pubmed/dev/co-cited.lst",
						SentimentStatisticsAE.PARAM_DB, articlesDB);

		/*
		 * The type priority is important especially to retrieve tokens. The
		 * rest of the order is not accurate but it does not matter.
		 */
		AggregateBuilder builder = new AggregateBuilder(null,
				TypePrioritiesFactory.createTypePriorities(ID.class,
						Title.class, Section.class, Paragraph.class,
						CitationContext.class, Sentence.class, Citation.class,
						Token.class, Sentiment.class, Negative.class,
						Neutral.class, Positive.class), null);

		builder.add(xmlParser);
		builder.add(sentenceDetector);
		builder.add(citationContextExtractor);
		builder.add(tokenizer);
		builder.add(POSTagger);
		builder.add(positiveMatcher);
		builder.add(neutralMatcher);
		builder.add(negativeMatcher);
		builder.add(XMIWriter);
		builder.add(sentimentStatistics);
		// builder.add(sentimentAnnotator);
		// builder.add(gnuplotDumper);
		SimplePipeline
				.runPipeline(reader, builder.createAggregateDescription());

		logger.info("done!");
	}
}

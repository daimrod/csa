package jgreg.internship.nii.WF;

import jgreg.internship.nii.AE.ArticlesDBDumpAE;
import jgreg.internship.nii.AE.CitationContextExtractorAE;
import jgreg.internship.nii.AE.PubMedParserAE;
import jgreg.internship.nii.AE.PubMedXMIWriter;
import jgreg.internship.nii.AE.SentimentAnnotator;
import jgreg.internship.nii.AE.SentimentMatcherAE;
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

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
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

	public static void main(String[] args) throws Exception {
		parseArguments(args);

		ExternalResourceDescription articlesDB = ExternalResourceFactory
				.createExternalResourceDescription(ArticlesDB.class, "");

		CollectionReaderDescription reader = CollectionReaderFactory
				.createReaderDescription(PubMedReaderCR.class,
						PubMedReaderCR.INPUT_DIRECTORY,
						"/home/daimrod/corpus/pubmed/corpus/",
						PubMedReaderCR.INPUT_LIST,
						"/home/daimrod/corpus/pubmed/dev/list1.txt");

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
						PubMedXMIWriter.OUTPUT_DIRECTORY, "/home/daimrod/corpus/pubmed/dev/xmi/");

		AnalysisEngineDescription sentimentAnnotator = AnalysisEngineFactory
				.createEngineDescription(SentimentAnnotator.class,
						SentimentAnnotator.PARAM_DB, articlesDB);

		AnalysisEngineDescription gnuplotDumper = AnalysisEngineFactory
				.createEngineDescription(ArticlesDBDumpAE.class,
						ArticlesDBDumpAE.OUTPUT_DIRECTORY, "/home/daimrod/corpus/pubmed/dev/output/",
						ArticlesDBDumpAE.INPUT_FILE, "/home/daimrod/corpus/pubmed/dev/co-cited.lst",
						ArticlesDBDumpAE.PARAM_DB, articlesDB);

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
		builder.add(sentimentAnnotator);
		builder.add(XMIWriter);
		builder.add(gnuplotDumper);
		SimplePipeline
				.runPipeline(reader, builder.createAggregateDescription());

		logger.info("done!");
	}

	/**
	 * FIXME Parse the command line
	 *
	 * @param args
	 */
	static private void parseArguments(String[] args) {
		Options options = new Options();

		options.addOption(OptionBuilder
				.isRequired(false)
				.withLongOpt("window-size")
				.hasArg()
				.withDescription("The size of the window for citation context.")
				.create("windowSize"));

		CommandLineParser parser = new PosixParser();
		CommandLine cmd = null;
		try {
			cmd = parser.parse(options, args);
		} catch (ParseException ex) {
			System.err.println("The CLI args could not be parsed.");
			System.err.println("The error message was:");
			System.err.println(" " + ex.getMessage());
			System.exit(1);
		}

		if (cmd.hasOption("window-size")) {
			WINDOW_SIZE = new Integer(cmd.getOptionValue("window-size"));
		} else {
			WINDOW_SIZE = 4;
		}
	}
}

/*
 * 
 */
package jgreg.internship.nii.WF;

import jgreg.internship.nii.AE.CitationContextExtractorAE;
import jgreg.internship.nii.AE.PubMedParserAE;
import jgreg.internship.nii.AE.SentimentFinderAE;
import jgreg.internship.nii.AE.XMIWriter;
import jgreg.internship.nii.CR.DirectoryReaderCR;
import jgreg.internship.nii.RES.ArticlesDB;
import jgreg.internship.nii.RES.MappingRES;
import jgreg.internship.nii.RES.StringListRES;
import jgreg.internship.nii.types.Citation;
import jgreg.internship.nii.types.CitationContext;
import jgreg.internship.nii.types.ID;
import jgreg.internship.nii.types.Paragraph;
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

// TODO: Auto-generated Javadoc
/**
 * This my full Pipeline.
 */
public class AnnotatorWF {
	
	/** The Constant logger. */
	private static final Logger logger = Logger.getLogger(AnnotatorWF.class
			.getCanonicalName());

	/**
	 * Run the Pipeline.
	 *
	 * @param inputDirectory            contains all articles.
	 * @param outputDirectory            stores all output data (XMI, ...).
	 * @param listArticlesFilename            lists articles of interest.
	 * @param listFocusedArticlesFilename            lists PMIDS of interest.
	 * @param mappingFilename            describes the mapping system.
	 * @param windowSize            is the size of the citation context.
	 * @throws Exception the exception
	 */
	public static void process(String inputDirectory, String outputDirectory,
			String listArticlesFilename, String listFocusedArticlesFilename,
			String mappingFilename, Integer windowSize) throws Exception {

		/*
		 * Resources
		 */
		// Sentence Model
		ExternalResourceDescription sentenceModel = ExternalResourceFactory
				.createExternalResourceDescription(
						SentenceModelResourceImpl.class, 
						"file:org/apache/ctakes/core/sentdetect/sd-med-model.bin");
		// Token Model
		ExternalResourceDescription tokenModel = ExternalResourceFactory
				.createExternalResourceDescription(
						TokenizerModelResourceImpl.class,
						"file:opennlp/uima/models/en-token.bin");

		// POS tagger Model
		ExternalResourceDescription POSModel = ExternalResourceFactory
				.createExternalResourceDescription(POSModelResourceImpl.class,
						"file:opennlp/uima/models/en-pos-perceptron.bin");

		// Corpus Articles
		ExternalResourceDescription corpusArticles = ExternalResourceFactory
				.createExternalResourceDescription(StringListRES.class,
						listArticlesFilename);

		// Focused Articles
		ExternalResourceDescription focusedArticles = ExternalResourceFactory
				.createExternalResourceDescription(StringListRES.class,
						listFocusedArticlesFilename);

		// Mapping
		ExternalResourceDescription mapping = ExternalResourceFactory
				.createExternalResourceDescription(MappingRES.class,
						mappingFilename);

		/*
		 * Collection Reader
		 */
		CollectionReaderDescription reader = CollectionReaderFactory
				.createReaderDescription(DirectoryReaderCR.class,
						DirectoryReaderCR.INPUT_DIRECTORY, inputDirectory,
						DirectoryReaderCR.CORPUS_ARTICLES, corpusArticles);

		/*
		 * Analysis Engine
		 */
		// Parser XML
		AnalysisEngineDescription xmlParser = AnalysisEngineFactory
				.createEngineDescription(PubMedParserAE.class);

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
						CitationContextExtractorAE.FOCUSED_ARTICLES,
						focusedArticles,
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

		// Sentiment Finder
		AnalysisEngineDescription sentimentFinder = AnalysisEngineFactory
				.createEngineDescription(SentimentFinderAE.class,
						SentimentFinderAE.MAPPING, mapping);

		// XMI Writer
		AnalysisEngineDescription xmiWriter = AnalysisEngineFactory
				.createEngineDescription(XMIWriter.class,
						XMIWriter.OUTPUT_DIRECTORY, outputDirectory,
						XMIWriter.CLEAR_DIRECTORY, true, XMIWriter.NAME_TYPE,
						"jgreg.internship.nii.types.ID",
						XMIWriter.NAME_FEATURE, "PMID");

		/*
		 * The type priority is important especially to retrieve tokens. The
		 * rest of the order is not accurate but it does not matter.
		 */
		AggregateBuilder builder = new AggregateBuilder(null,
				TypePrioritiesFactory.createTypePriorities(ID.class,
						Title.class, Section.class, Paragraph.class,
						CitationContext.class, Sentence.class, Citation.class,
						Token.class, Sentiment.class), null);

		builder.add(xmlParser);
		builder.add(sentenceDetector);
		builder.add(citationContextDetector);
		builder.add(tokenizer);
		builder.add(POSTagger);
		builder.add(sentimentFinder);
		builder.add(xmiWriter);
		SimplePipeline
				.runPipeline(reader, builder.createAggregateDescription());
	}

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 * @throws Exception the exception
	 */
	public static void main(String[] args) throws Exception {
		AnnotatorWF.process("/home/daimrod/corpus/pubmed/corpus/",
				"/home/daimrod/corpus/pubmed/dev/output/",
				"/home/daimrod/corpus/pubmed/dev/test1.lst",
				"/home/daimrod/corpus/pubmed/dev/co-cited.lst",
				"/home/daimrod/corpus/pubmed/dev/mapping.lst", 4);

		logger.info("done!");
	}
}

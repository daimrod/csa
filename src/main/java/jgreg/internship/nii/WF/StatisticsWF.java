package jgreg.internship.nii.WF;

import jgreg.internship.nii.AE.ExtractMaxAE;
import jgreg.internship.nii.CR.XMIReader;
import jgreg.internship.nii.RES.MappingRES;
import jgreg.internship.nii.types.Citation;
import jgreg.internship.nii.types.CitationContext;
import jgreg.internship.nii.types.ID;
import jgreg.internship.nii.types.Paragraph;
import jgreg.internship.nii.types.Section;
import jgreg.internship.nii.types.Sentence;
import jgreg.internship.nii.types.Sentiment;
import jgreg.internship.nii.types.Title;
import jgreg.internship.nii.types.Token;

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

public class StatisticsWF {
	private static final Logger logger = Logger.getLogger(StatisticsWF.class
			.getCanonicalName());

	public static void process(String inputDirectory, String mappingFilename,
			String outputFile) throws Exception {
		CollectionReaderDescription reader = CollectionReaderFactory
				.createReaderDescription(XMIReader.class,
						XMIReader.INPUT_DIRECTORY, inputDirectory);

		AggregateBuilder builder = new AggregateBuilder(null,
				TypePrioritiesFactory.createTypePriorities(ID.class,
						Title.class, Section.class, Paragraph.class,
						CitationContext.class, Sentence.class, Citation.class,
						Token.class, Sentiment.class), null);

		// Mapping
		ExternalResourceDescription mapping = ExternalResourceFactory
				.createExternalResourceDescription(MappingRES.class,
						mappingFilename);

		String[] headers = { "positive", "neutral", "negative" };
		AnalysisEngineDescription extractor = AnalysisEngineFactory
				.createEngineDescription(ExtractMaxAE.class, ExtractMaxAE.MAPPING,
						mapping, ExtractMaxAE.HEADERS, headers,
						ExtractMaxAE.OUTPUT_FILE, outputFile);

		builder.add(extractor);
		SimplePipeline
				.runPipeline(reader, builder.createAggregateDescription());
	}

	public static void main(String[] args) throws Exception {
		StatisticsWF.process("/home/daimrod/corpus/pubmed/dev/output/",
				"/home/daimrod/corpus/pubmed/dev/mapping.lst",
				"/home/daimrod/corpus/pubmed/dev/output/max-out.dat");
		logger.info("done!");
	}
}

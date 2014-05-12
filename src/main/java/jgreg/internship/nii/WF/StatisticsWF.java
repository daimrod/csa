package jgreg.internship.nii.WF;

import jgreg.internship.nii.CR.XMIReader;
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
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.factory.TypePrioritiesFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.resource.ResourceInitializationException;

public class StatisticsWF {
	private static final Logger logger = Logger.getLogger(StatisticsWF.class
			.getCanonicalName());

	public static void process(String inputDirectory) throws Exception {
		CollectionReaderDescription reader = CollectionReaderFactory
				.createReaderDescription(XMIReader.class,
						XMIReader.INPUT_DIRECTORY, inputDirectory);

		AggregateBuilder builder = new AggregateBuilder(null,
				TypePrioritiesFactory.createTypePriorities(ID.class,
						Title.class, Section.class, Paragraph.class,
						CitationContext.class, Sentence.class, Citation.class,
						Token.class, Sentiment.class), null);

		SimplePipeline
				.runPipeline(reader, builder.createAggregateDescription());
	}

	public static void main(String[] args) throws Exception {
		StatisticsWF.process("/home/daimrod/corpus/pubmed/dev/output/");
		logger.info("done!");
	}
}

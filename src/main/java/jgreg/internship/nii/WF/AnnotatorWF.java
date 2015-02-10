//
// Author:: Grégoire Jadi <daimrod@gmail.com>
// Copyright:: Copyright (c) 2014, Grégoire Jadi
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are
// met:
//
//    1. Redistributions of source code must retain the above copyright
//       notice, this list of conditions and the following disclaimer.
//
//    2. Redistributions in binary form must reproduce the above
//       copyright notice, this list of conditions and the following
//       disclaimer in the documentation and/or other materials provided
//       with the distribution.
//
// THIS SOFTWARE IS PROVIDED BY GRÉGOIRE JADI ``AS IS'' AND ANY
// EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
// PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL GRÉGOIRE JADI OR
// CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
// SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
// LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
// USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
// ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
// OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
// OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
// SUCH DAMAGE.
//
// The views and conclusions contained in the software and
// documentation are those of the authors and should not be
// interpreted as representing official policies, either expressed or
// implied, of Grégoire Jadi.
//

package jgreg.internship.nii.WF;

import jgreg.internship.nii.AE.CitationContextAnnotatorAE;
import jgreg.internship.nii.AE.PatternAnnotatorAE;
import jgreg.internship.nii.AE.XMIReaderAE;
import jgreg.internship.nii.AE.XMIWriterAE;
import jgreg.internship.nii.CR.DirectoryReaderCR;
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

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.configuration.PropertiesConfiguration;
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
 * The annotator pipeline.
 */
public class AnnotatorWF {

	/** The Constant logger. */
	private static final Logger logger = Logger.getLogger(AnnotatorWF.class
			.getCanonicalName());

	/**
	 * Run the Pipeline.
	 *
	 * @param annotator_input
	 *            contains all articles.
	 * @param annotator_output
	 *            stores all output data (XMI, ...).
	 * @param annotator_list_articles_filename
	 *            lists articles of interest.
	 * @param coCitationFilename
	 *            lists co-cited PMIDS.
	 * @param mappingFilename
	 *            describes the mapping system.
	 * @param windowSize
	 *            is the size of the citation context.
	 * @throws Exception
	 *             the exception
	 */
	public static void process(String annotator_input, String annotator_output,
			String annotator_list_articles_filename, String coCitationFilename,
			String mappingFilename, Integer windowSize) throws Exception {

		/*
		 * Resources
		 */
		// Corpus Articles
		ExternalResourceDescription annotator_list_articles = ExternalResourceFactory
				.createExternalResourceDescription(StringListRES.class,
						annotator_list_articles_filename);

		// CoCited Articles
		ExternalResourceDescription coCitedArticles = null;
		if (!coCitationFilename.isEmpty()) {
			coCitedArticles = ExternalResourceFactory
					.createExternalResourceDescription(StringListRES.class,
							coCitationFilename);
		}

		// Mapping
		ExternalResourceDescription mapping = ExternalResourceFactory
				.createExternalResourceDescription(MappingRES.class,
						mappingFilename);

		/*
		 * Collection Reader
		 */
		CollectionReaderDescription directoryReader = CollectionReaderFactory
				.createReaderDescription(DirectoryReaderCR.class,
						DirectoryReaderCR.INPUT_DIRECTORY, annotator_input,
						DirectoryReaderCR.LIST_ARTICLES,
						annotator_list_articles);

		/*
		 * Analysis Engine
		 */
		// XMI Reader
		AnalysisEngineDescription xmiReader = AnalysisEngineFactory
				.createEngineDescription(XMIReaderAE.class);

		// Citation Context Annotator
		AnalysisEngineDescription citationContextAnnotator = AnalysisEngineFactory
				.createEngineDescription(CitationContextAnnotatorAE.class,
						CitationContextAnnotatorAE.COCITED_ARTICLES,
						coCitedArticles,
						CitationContextAnnotatorAE.PARAM_WINDOW_SIZE,
						windowSize);

		// Sentiment Finder
		AnalysisEngineDescription sentimentFinder = AnalysisEngineFactory
				.createEngineDescription(PatternAnnotatorAE.class,
						PatternAnnotatorAE.MAPPING, mapping);

		// XMI Writer
		AnalysisEngineDescription xmiWriter = AnalysisEngineFactory
				.createEngineDescription(XMIWriterAE.class,
						XMIWriterAE.OUTPUT_DIRECTORY, annotator_output,
						XMIWriterAE.NAME_TYPE, "jgreg.internship.nii.types.ID",
						XMIWriterAE.NAME_FEATURE, "PMID");

		/*
		 * The type priority is important especially to retrieve tokens. The
		 * rest of the order is not accurate but it does not matter.
		 */
		AggregateBuilder builder = new AggregateBuilder(null,
				TypePrioritiesFactory.createTypePriorities(ID.class,
						Title.class, Section.class, Paragraph.class,
						CitationContext.class, Sentence.class, Citation.class,
						Token.class, Sentiment.class), null);

		builder.add(xmiReader);
		builder.add(citationContextAnnotator);
		builder.add(sentimentFinder);
		builder.add(xmiWriter);
		SimplePipeline.runPipeline(directoryReader,
				builder.createAggregateDescription());
	}

	/**
	 * The main method.
	 *
	 * @param args
	 *            the arguments
	 * @throws Exception
	 *             the exception
	 */
	public static void main(String[] args) throws Exception {
		Options options = new Options();
		options.addOption("help", false, "print this message");

		options.addOption(OptionBuilder.withArgName("inputDirectory").hasArg()
				.isRequired(false).create("inputDirectory"));
		options.addOption(OptionBuilder.withArgName("outputDirectory").hasArg()
				.isRequired(false).create("outputDirectory"));
		options.addOption(OptionBuilder.withArgName("listArticlesFilename")
				.hasArg().isRequired(false).create("listArticlesFilename"));
		options.addOption(OptionBuilder
				.withArgName("listFocusedArticlesFilename").hasArg()
				.isRequired(false).create("listFocusedArticlesFilename"));
		options.addOption(OptionBuilder
				.withArgName("listCoCitedArticlesFilename").hasArg()
				.isRequired(false).create("listCoCitedArticlesFilename"));
		options.addOption(OptionBuilder.withArgName("mappingFilename").hasArg()
				.isRequired(false).create("mappingFilename"));
		options.addOption(OptionBuilder.withArgName("windowSize").hasArg()
				.withType(Integer.class).isRequired(false).create("windowSize"));

		options.addOption(OptionBuilder.withArgName("config").hasArg()
				.isRequired(false).create("config"));

		CommandLineParser parser = new BasicParser();
		CommandLine line = parser.parse(options, args);

		HelpFormatter formatter = new HelpFormatter();
		if (line.hasOption("help")) {
			formatter.printHelp("csa", options);
			return;
		}

		// Initialize configuration file if any
		String configFilename = line.getOptionValue("config", "WF.conf");
		PropertiesConfiguration annotatorConfig = new PropertiesConfiguration(
				configFilename);

		// Initialize parameters
		String annotator_input = line.getOptionValue("annotator_input",
				annotatorConfig.getString("annotator_input"));

		String annotator_output = line.getOptionValue("annotator_output",
				annotatorConfig.getString("annotator_output"));

		String annotator_list_articles_filename = line.getOptionValue(
				"annotator_list_articles",
				annotatorConfig.getString("annotator_list_articles"));

		String coCitationFilename = line.getOptionValue("coCitationFilename",
				annotatorConfig.getString("coCitationFilename", ""));

		String mappingFilename = line.getOptionValue("mappingFilename",
				annotatorConfig.getString("mappingFilename"));

		Integer windowSize = new Integer(line.getOptionValue("windowSize",
				annotatorConfig.getString("windowSize")));

		AnnotatorWF.process(annotator_input, annotator_output,
				annotator_list_articles_filename, coCitationFilename,
				mappingFilename, windowSize);

		logger.info("done!");
 }
}

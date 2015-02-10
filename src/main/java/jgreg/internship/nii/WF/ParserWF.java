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

import jgreg.internship.nii.AE.CitationExtractorAE;
import jgreg.internship.nii.AE.PubMedParserAE;
import jgreg.internship.nii.AE.XMIWriterAE;
import jgreg.internship.nii.CR.DirectoryReaderCR;
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

import opennlp.uima.sentdetect.SentenceDetector;
import opennlp.uima.sentdetect.SentenceModelResourceImpl;
import opennlp.uima.tokenize.Tokenizer;
import opennlp.uima.tokenize.TokenizerModelResourceImpl;

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
import org.apache.uima.fit.factory.FlowControllerFactory;
import org.apache.uima.fit.factory.TypePrioritiesFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.flow.impl.FixedFlowController;
import org.apache.uima.resource.ExternalResourceDescription;

/**
 * Parser
 */
public class ParserWF {

	/** The Constant logger. */
	private static final Logger logger = Logger.getLogger(ParserWF.class
			.getCanonicalName());

	/**
	 * Run the Pipeline.
	 *
	 * @param parser_input
	 *            contains all articles.
	 * @param parser_output
	 *            stores all output data (XMI, ...).
	 * @param parser_list_articles_filename
	 *            lists articles of interest.
	 * @throws Exception
	 *             the exception
	 */
	public static void process(String parser_input, String parser_output,
			String parser_list_articles_filename, String citationFilename)
			throws Exception {

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

		// Corpus Articles
		ExternalResourceDescription parser_list_articles = ExternalResourceFactory
				.createExternalResourceDescription(StringListRES.class,
						parser_list_articles_filename);

		/*
		 * Collection Reader
		 */
		CollectionReaderDescription reader = CollectionReaderFactory
				.createReaderDescription(DirectoryReaderCR.class,
						DirectoryReaderCR.INPUT_DIRECTORY, parser_input,
						DirectoryReaderCR.LIST_ARTICLES,
						parser_list_articles);

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

		// Tokenizer
		AnalysisEngineDescription tokenizer = AnalysisEngineFactory
				.createEngineDescription(Tokenizer.class,
						"opennlp.uima.ModelName", tokenModel,
						"opennlp.uima.SentenceType",
						"jgreg.internship.nii.types.Sentence",
						"opennlp.uima.TokenType",
						"jgreg.internship.nii.types.Token");

		// CoCitatio Extractor
		AnalysisEngineDescription citationExtractor = AnalysisEngineFactory
				.createEngineDescription(CitationExtractorAE.class,
						CitationExtractorAE.OUTPUT_FILE, citationFilename);

		// XMI Writer
		AnalysisEngineDescription xmiWriter = AnalysisEngineFactory
				.createEngineDescription(XMIWriterAE.class,
						XMIWriterAE.OUTPUT_DIRECTORY, parser_output,
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
						Token.class, Sentiment.class),
				FlowControllerFactory.createFlowControllerDescription(
						FixedFlowController.class,
						FixedFlowController.PARAM_ACTION_AFTER_CAS_MULTIPLIER,
						"drop"));

		builder.add(xmlParser);
		builder.add(sentenceDetector);
		builder.add(tokenizer);
		builder.add(citationExtractor);
		builder.add(xmiWriter);
		SimplePipeline
				.runPipeline(reader, builder.createAggregateDescription());

		logger.info("Done!");
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
		logger.info("Starting " + ParserWF.class + "...");

		Options options = new Options();
		options.addOption("help", false, "print this message");

		options.addOption(OptionBuilder.withArgName("parser_input").hasArg()
				.isRequired(false).create("parser_input"));
		options.addOption(OptionBuilder.withArgName("parser_output").hasArg()
				.isRequired(false).create("parser_output"));
		options.addOption(OptionBuilder.withArgName("parser_list_articles")
				.hasArg().isRequired(false).create("parser_list_articles"));
		options.addOption(OptionBuilder.withArgName("citationFilename")
				.hasArg().isRequired(false).create("citationFilename"));
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
		String parser_input = line.getOptionValue("parser_input",
				annotatorConfig.getString("parser_input"));

		String parser_output = line.getOptionValue("parser_output",
				annotatorConfig.getString("parser_output"));

		String parser_list_articles_filename = line.getOptionValue(
				"parser_list_articles",
				annotatorConfig.getString("parser_list_articles"));

		String citationFilename = line.getOptionValue("citationFilename",
				annotatorConfig.getString("citationFilename"));

		ParserWF.process(parser_input, parser_output,
				parser_list_articles_filename, citationFilename);

 }
}

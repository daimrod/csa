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

import jgreg.internship.nii.AE.ExtractAllAE;
import jgreg.internship.nii.AE.XMIReaderAE;
import jgreg.internship.nii.CR.DirectoryReaderCR;
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

// TODO: Auto-generated Javadoc
/**
 * The Class StatisticsWF.
 */
public class StatisticsWF {

	/** The Constant logger. */
	private static final Logger logger = Logger.getLogger(StatisticsWF.class
			.getCanonicalName());

	/**
	 * Process.
	 *
	 * @param inputDirectory
	 *            the input directory
	 * @param mappingFilename
	 *            the mapping filename
	 * @param outputFile
	 *            the output file
	 * @param infoFile
	 *            the info file
	 * @throws Exception
	 *             the exception
	 */
	public static void process(String inputDirectory, String mappingFilename,
			String outputFile, String infoFile) throws Exception {
		String[] extensions = { "xmi" };
		CollectionReaderDescription reader = CollectionReaderFactory
                .createReaderDescription(DirectoryReaderCR.class,
						DirectoryReaderCR.INPUT_DIRECTORY, inputDirectory,
						DirectoryReaderCR.EXTENSIONS, extensions);

		AggregateBuilder builder = new AggregateBuilder(null,
				TypePrioritiesFactory.createTypePriorities(ID.class,
						Title.class, Section.class, Paragraph.class,
						CitationContext.class, Sentence.class, Citation.class,
						Token.class, Sentiment.class), null);

		// Mapping
		ExternalResourceDescription mapping = ExternalResourceFactory
				.createExternalResourceDescription(MappingRES.class,
						mappingFilename);

		AnalysisEngineDescription deserializer = AnalysisEngineFactory
				.createEngineDescription(XMIReaderAE.class);

		AnalysisEngineDescription extractor = AnalysisEngineFactory
				.createEngineDescription(ExtractAllAE.class,
						ExtractAllAE.MAPPING, mapping,
						ExtractAllAE.OUTPUT_FILE, outputFile,
						ExtractAllAE.INFO_FILE, infoFile);

		builder.add(deserializer);
		builder.add(extractor);
		SimplePipeline
				.runPipeline(reader, builder.createAggregateDescription());
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

        options.addOption(OptionBuilder.withArgName("config").hasArg()
				.isRequired(false).create("config"));
		options.addOption(OptionBuilder.withArgName("inputDirectory").hasArg()
				.isRequired(false).create("inputDirectory"));
		options.addOption(OptionBuilder.withArgName("mappingFilename").hasArg()
				.isRequired(false).create("mappingFilename"));
		options.addOption(OptionBuilder.withArgName("outputFile").hasArg()
				.isRequired(false).create("outputFile"));
		options.addOption(OptionBuilder.withArgName("infoFile").hasArg()
				.isRequired(false).create("infoFile"));

		CommandLineParser parser = new BasicParser();
		CommandLine line = parser.parse(options, args);

		HelpFormatter formatter = new HelpFormatter();
		if (line.hasOption("help")) {
			formatter.printHelp("csa", options);
			return;
		}

		// Initialize configuration file if any
		String configFilename;
		configFilename = line.getOptionValue("config", "statistic.conf");
		PropertiesConfiguration statisticConfig = new PropertiesConfiguration(
				configFilename);

		String inputDirectory = line.getOptionValue("inputDirectory",
				statisticConfig.getString("inputDirectory"));

		String mappingFilename = line.getOptionValue("mappingFilename",
				statisticConfig.getString("mappingFilename"));

		String outputFile = line.getOptionValue("outputFile",
				statisticConfig.getString("outputFile"));

		String infoFile = line.getOptionValue("infoFile",
				statisticConfig.getString("infoFile"));

		StatisticsWF.process(inputDirectory, mappingFilename, outputFile,
				infoFile);
		logger.info("done!");
	}
}

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

import jgreg.internship.nii.Utils.CoCitationExtractor;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;

/**
 * Parser
 */
public class CoCitationWF {

	/** The Constant logger. */
	private static final Logger logger = Logger.getLogger(CoCitationWF.class
			.getCanonicalName());

	/**
	 * Run the Pipeline.
	 */
	public static void process(String citationFilename,
			String coCitationFilename, Integer coCitationThreshold)
			throws Exception {

		CoCitationExtractor extractor = new CoCitationExtractor(
				citationFilename, coCitationFilename, coCitationThreshold);
		extractor.write();

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
		logger.info("Starting " + CoCitationWF.class + "...");

		Options options = new Options();
		options.addOption("help", false, "print this message");

		options.addOption(OptionBuilder.withArgName("citationFilename")
				.hasArg().isRequired(false).create("citationFilename"));
		options.addOption(OptionBuilder.withArgName("coCitationFilename")
				.hasArg().isRequired(false).create("coCitationFilename"));
		options.addOption(OptionBuilder.withArgName("coCitationThreshold")
				.hasArg().withType(Integer.class).isRequired(false)
				.create("coCitationThreshold"));

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
		String citationFilename = line.getOptionValue("citationFilename",
				annotatorConfig.getString("citationFilename"));
		String coCitationFilename = line.getOptionValue("coCitationFilename",
				annotatorConfig.getString("coCitationFilename"));
		Integer coCitationThreshold = new Integer(line.getOptionValue(
				"coCitationThreshold",
				annotatorConfig.getString("coCitationThreshold")));

		CoCitationWF.process(citationFilename, coCitationFilename,
				coCitationThreshold);
 }
}

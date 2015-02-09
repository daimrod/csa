//
// Author:: Grégoire Jadi <daimrod@gmail.com>
// Copyright:: Copyright (c) 2014, Grégoire Jadi
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are
// met:
//
//    1	. Redistributions of source code must retain the above copyright
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

import jgreg.internship.nii.AE.CoCitationExtractorAE;
import jgreg.internship.nii.AE.PubMedParserAE;
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
 * This simple workflow runs the DirectoryReaderCR and the
 * CoCitationExtractorAE.
 */
public class TestWF02 {

	/**
	 * The main method.
	 *
	 * @param args
	 *            the arguments
	 * @throws Exception
	 *             the exception
	 */
	public static void main(String[] args) throws Exception {
		String[] extensions = { "xmi" };

		ExternalResourceDescription corpusArticles = ExternalResourceFactory
				.createExternalResourceDescription(StringListRES.class,
						"/media/jadi-g/DATADRIVE1/corpus/PMC OAS/work/test.txt");

		CollectionReaderDescription reader = CollectionReaderFactory
				.createReaderDescription(DirectoryReaderCR.class,
						DirectoryReaderCR.INPUT_DIRECTORY, "/tmp/data",
						DirectoryReaderCR.CORPUS_ARTICLES, corpusArticles);

		// Parser XML
		AnalysisEngineDescription parser = AnalysisEngineFactory
				.createEngineDescription(PubMedParserAE.class);

		AnalysisEngineDescription writer = AnalysisEngineFactory
				.createEngineDescription(CoCitationExtractorAE.class,
						CoCitationExtractorAE.OUTPUT_FILE,
						"/tmp/cocitation.output");

		AggregateBuilder builder = new AggregateBuilder(null,
				TypePrioritiesFactory.createTypePriorities(ID.class,
						Title.class, Section.class, Paragraph.class,
						CitationContext.class, Sentence.class, Citation.class,
						Token.class, Sentiment.class),
				FlowControllerFactory.createFlowControllerDescription(
						FixedFlowController.class,
						FixedFlowController.PARAM_ACTION_AFTER_CAS_MULTIPLIER,
						"drop"));

		builder.add(parser);
		builder.add(writer);
		SimplePipeline
				.runPipeline(reader, builder.createAggregateDescription());
	}
}

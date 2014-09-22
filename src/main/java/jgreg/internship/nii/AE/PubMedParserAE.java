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

package jgreg.internship.nii.AE;

import java.io.Reader;
import java.io.StringReader;
import java.util.List;
import java.util.Map.Entry;

import jgreg.internship.nii.RES.Article;
import jgreg.internship.nii.XML.PubMedXMLParser;
import jgreg.internship.nii.types.Citation;
import jgreg.internship.nii.types.Filename;
import jgreg.internship.nii.types.ID;
import jgreg.internship.nii.types.Paragraph;
import jgreg.internship.nii.types.Section;
import jgreg.internship.nii.types.Title;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Logger;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CASException;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

/**
 * This analysis parses PubMed's articles and add the following annotations:
 * <ul>
 * <li> {@link jgreg.internship.nii.types.Citation}</li>
 * <li> {@link jgreg.internship.nii.types.Section}</li>
 * <li> {@link jgreg.internship.nii.types.Title} (the section's title)</li>
 * <li> {@link jgreg.internship.nii.types.Paragraph}</li>
 * <li> {@link jgreg.internship.nii.types.ID} (article's PMID)</li>
 * </ul>
 *
 * @author Grégoire Jadi
 */
public class PubMedParserAE extends
		org.apache.uima.fit.component.JCasAnnotator_ImplBase {

	/** The Constant logger. */
	private static final Logger logger = Logger.getLogger(PubMedParserAE.class
			.getCanonicalName());

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.apache.uima.analysis_component.JCasAnnotator_ImplBase#process(org
	 * .apache.uima.jcas.JCas)
	 */
	@Override
	public void process(JCas jCas) throws AnalysisEngineProcessException {
		JCas originalText;
		try {
			originalText = jCas.getView("originalText");
		} catch (CASException ex) {
			throw new AnalysisEngineProcessException(ex);
		}

		Filename filename = JCasUtil.selectSingle(originalText, Filename.class);
		logger.info("Parsing `" + filename.getFilename() + "'...");

		PubMedXMLParser parser;
		Reader reader = new StringReader(originalText.getDocumentText());
		parser = new PubMedXMLParser(reader);

		jCas.setDocumentText(parser.getText());

		Article article = parser.getArticle();
		article.setFilename(filename.getFilename());

		ID docId = new ID(jCas);
		docId.setPMID(article.getPMID());
		docId.setYear(article.getYear());
		docId.setTitle(article.getTitle());
		docId.setBegin(0);
		docId.setEnd(1);
		docId.addToIndexes();

		// Citation annotation
		// and Article from citations
		for (Entry<String, List<Pair<Integer, Integer>>> entry : parser
				.getCitations().entrySet()) {
			for (Pair<Integer, Integer> citationIdx : entry.getValue()) {
				// create and add a Citation
				Citation citation = new Citation(jCas);
				citation.setBegin(citationIdx.getLeft());
				citation.setEnd(citationIdx.getRight());
				citation.setPMID(entry.getKey());
				citation.addToIndexes();
			}
		}

		// Section annotation
		for (Pair<Integer, Integer> section : parser.getSections()) {
			Section annotation = new Section(jCas);
			annotation.setBegin(section.getLeft());
			annotation.setEnd(section.getRight());
			annotation.addToIndexes();
		}

		// Section's Title annotation
		for (Pair<Integer, Integer> title : parser.getTitles()) {
			Title annotation = new Title(jCas);
			annotation.setBegin(title.getLeft());
			annotation.setEnd(title.getRight());
			annotation.addToIndexes();
		}

		// Paragraph annotation
		for (Pair<Integer, Integer> paragraph : parser.getParagraphs()) {
			Paragraph annotation = new Paragraph(jCas);
			annotation.setBegin(paragraph.getLeft());
			annotation.setEnd(paragraph.getRight());
			annotation.addToIndexes();
		}
	}
}

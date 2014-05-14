/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jgreg.internship.nii.AE;

import java.io.Reader;
import java.io.StringReader;
import java.util.List;
import java.util.Map.Entry;

import jgreg.internship.nii.RES.Article;
import jgreg.internship.nii.RES.ArticlesDB;
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
import org.apache.uima.fit.descriptor.ExternalResource;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

/**
 * This analysis parses PubMed's articles and add the following annotations: -
 * Citation - Section - Title (the section's title) - Paragraph - ID (article's
 * PMID)
 *
 * All of this is done is a newly created View named "parsed".
 *
 * @author Grégoire Jadi
 */
public class PubMedParserAE extends
		org.apache.uima.fit.component.JCasAnnotator_ImplBase {
	private static final Logger logger = Logger.getLogger(PubMedParserAE.class
			.getCanonicalName());

    public final static String PARAM_DB = "articlesDB";
    @ExternalResource(key = PARAM_DB, mandatory = true)
    private ArticlesDB articlesDB;

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
        articlesDB.add(article);
        
		ID docId = new ID(jCas);
		docId.setPMID(article.getPMID());
        docId.setYear(article.getYear());
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

                if (articlesDB.get(entry.getKey()) == null) {
                    articlesDB.add(new Article(entry.getKey()));
                }
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

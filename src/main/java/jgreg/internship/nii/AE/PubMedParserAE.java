/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jgreg.internship.nii.CR;

import java.io.Reader;
import java.io.StringReader;
import java.util.List;
import java.util.Map.Entry;
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
import org.apache.uima.fit.component.ViewCreatorAnnotator;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

/**
 *
 * @author daimrod
 */
public class PubMedParserAE extends org.apache.uima.fit.component.JCasAnnotator_ImplBase {

    private static final Logger logger = Logger.getLogger(PubMedParserAE.class.getCanonicalName());

    @Override
    public void process(JCas jCas) throws AnalysisEngineProcessException {
        Filename filename = JCasUtil.selectSingle(jCas, Filename.class);
        logger.info("Parsing `" + filename.getFilename() + "'...");
        
        PubMedXMLParser parser;
        Reader reader = new StringReader(jCas.getDocumentText());
        parser = new PubMedXMLParser(reader);
        
        JCas parsedView = ViewCreatorAnnotator.createViewSafely(jCas, "parsed");
        
        parsedView.setDocumentText(parser.getText());
        
        ID docId = new ID(parsedView);
        docId.setPMID(parser.getPMID());
        docId.addToIndexes();

        // Add Citation annotations
        for (Entry<String, List<Pair<Integer, Integer>>> entry : parser.getCitations().entrySet()) {
            for (Pair<Integer, Integer> citationIdx : entry.getValue()) {
                Citation citation = new Citation(parsedView);
                citation.setBegin(citationIdx.getLeft());
                citation.setEnd(citationIdx.getRight());
                citation.setPMID(entry.getKey());
                citation.addToIndexes();
            }
        }

        for (Pair<Integer, Integer> section : parser.getSections()) {
            Section annotation = new Section(parsedView);
            annotation.setBegin(section.getLeft());
            annotation.setEnd(section.getRight());
            annotation.addToIndexes();
        }

        for (Pair<Integer, Integer> title : parser.getTitles()) {
            Title annotation = new Title(parsedView);
            annotation.setBegin(title.getLeft());
            annotation.setEnd(title.getRight());
            annotation.addToIndexes();
        }

        for (Pair<Integer, Integer> paragraph : parser.getParagraphs()) {
            Paragraph annotation = new Paragraph(parsedView);
            annotation.setBegin(paragraph.getLeft());
            annotation.setEnd(paragraph.getRight());
            annotation.addToIndexes();
        }
    }
}

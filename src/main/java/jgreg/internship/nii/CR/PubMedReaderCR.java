/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jgreg.internship.nii.CR;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import jgreg.internship.nii.XML.PubMedXMLParser;
import jgreg.internship.nii.types.Citation;
import jgreg.internship.nii.types.ID;
import jgreg.internship.nii.types.Paragraph;
import jgreg.internship.nii.types.Section;
import jgreg.internship.nii.types.Title;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Logger;
import org.apache.uima.UimaContext;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.fit.component.JCasCollectionReader_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Progress;

/**
 *
 * @author daimrod
 */
public class PubMedReaderCR extends JCasCollectionReader_ImplBase {

    private static final Logger logger = Logger.getLogger(PubMedReaderCR.class.getCanonicalName());

    /**
     * Path to the PubMed Corpus
     */
    public static final String INPUT_DIRECTORY = "inputDirectory";
    @ConfigurationParameter(name = INPUT_DIRECTORY, mandatory = true)
    private String inputDirectory;

    private Iterator<File> files;
    private File pubmedFile;
    private int docIndex = 0;

    /**
     * Get PubMedReaderCR ready to read files in INPUT_DIRECTORY.
     *
     * @param context
     * @throws ResourceInitializationException
     */
    @Override
    public void initialize(UimaContext context) throws ResourceInitializationException {
        logger.info("Listing `" + inputDirectory + "'...");
        pubmedFile = new File(inputDirectory);

        if (!pubmedFile.exists()) {
            logger.error("could not find the PubMed directory at `" + inputDirectory + "'");
            throw new ResourceInitializationException();
        }

        files = FileUtils.listFiles(pubmedFile,
                FileFilterUtils.suffixFileFilter(".nxml"),
                FileFilterUtils.trueFileFilter()).iterator();
    }

    @Override
    public void getNext(JCas jCas) throws IOException, CollectionException, FileNotFoundException {
        File file = files.next();
        PubMedXMLParser parser;
        parser = new PubMedXMLParser(file.getPath());
        
        jCas.setDocumentText(parser.getText());
        ID docId = new ID(jCas);
        docId.setPMID(parser.getPMID());
        docId.addToIndexes();

        // Add Citation annotations
        for (Entry<String, List<Pair<Integer, Integer>>> entry : parser.getCitations().entrySet()) {
            for (Pair<Integer, Integer> citationIdx : entry.getValue()) {
                Citation citation = new Citation(jCas);
                citation.setBegin(citationIdx.getLeft());
                citation.setEnd(citationIdx.getRight());
                citation.setPMID(entry.getKey());
                citation.addToIndexes();
            }
        }

        for (Pair<Integer, Integer> section : parser.getSections()) {
            Section annotation = new Section(jCas);
            annotation.setBegin(section.getLeft());
            annotation.setEnd(section.getRight());
            annotation.addToIndexes();
        }

        for (Pair<Integer, Integer> title : parser.getTitles()) {
            Title annotation = new Title(jCas);
            annotation.setBegin(title.getLeft());
            annotation.setEnd(title.getRight());
            annotation.addToIndexes();
        }

        for (Pair<Integer, Integer> paragraph : parser.getParagraphs()) {
            Paragraph annotation = new Paragraph(jCas);
            annotation.setBegin(paragraph.getLeft());
            annotation.setEnd(paragraph.getRight());
            annotation.addToIndexes();
        }
        docIndex++;
    }

    @Override
    public boolean hasNext() throws IOException, CollectionException {
        return files.hasNext();
    }

    @Override
    public Progress[] getProgress() {
        return new Progress[docIndex];
    }

}

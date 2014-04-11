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
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Level;
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
     * @param context
     * @throws ResourceInitializationException
     */
    @Override
    public void initialize(UimaContext context) throws ResourceInitializationException {
        logger.log(Level.INFO, "Listing `" + inputDirectory + "'...");
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
        PubMedXMLParser parser = new PubMedXMLParser(file.getPath());
        jCas.setDocumentText(parser.getText());

        // Add Citation annotations
        for (Entry<String, List<Pair<Integer, Integer>>> entry : parser.getCitations().entrySet()) {
            for (Pair<Integer, Integer> citationIdx : entry.getValue()) {
                Citation citation = new Citation(jCas);
                citation.setBegin(citationIdx.getLeft());
                citation.setEnd(citationIdx.getRight());
                citation.setPmid(entry.getKey());
            }
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

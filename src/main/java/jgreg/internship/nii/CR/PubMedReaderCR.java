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

import jgreg.internship.nii.types.Filename;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.log4j.Logger;
import org.apache.uima.UimaContext;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.fit.component.JCasCollectionReader_ImplBase;
import org.apache.uima.fit.component.ViewCreatorAnnotator;
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
        try {
            JCas originalText = ViewCreatorAnnotator.createViewSafely(jCas, "originalText");
            originalText.setDocumentText(FileUtils.readFileToString(file));
            Filename filename = new Filename(originalText);
            filename.setFilename(file.getAbsolutePath());
            filename.addToIndexes();
        } catch (Exception ex) {
            throw new CollectionException(ex);
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

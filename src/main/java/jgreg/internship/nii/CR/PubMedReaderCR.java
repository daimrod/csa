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
import java.util.stream.Collectors;

import jgreg.internship.nii.types.Filename;

import org.apache.commons.io.FileUtils;
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
    public static final String INPUT_DIRECTORY = "inputDirectoryName";
    @ConfigurationParameter(name = INPUT_DIRECTORY, mandatory = true)
    private String inputDirectoryName;
    private File inputDirectory;


    public static final String INPUT_LIST = "inputFileNameList";
    @ConfigurationParameter(name = INPUT_LIST, mandatory = true)
    private String inputFileNameList;
    private File inputFileList;

    private List<File> files;
    private Iterator<File> filesIt;
    private int docIndex = 0;

    /**
     * Get PubMedReaderCR ready to read files in INPUT_DIRECTORY.
     *
     * @param context
     * @throws ResourceInitializationException
     */
    @Override
    public void initialize(UimaContext context) throws ResourceInitializationException {
        super.initialize(context);
        
        logger.info("Listing `" + inputDirectoryName + "'...");
        inputDirectory = new File(inputDirectoryName);

        if (!inputDirectory.exists()) {
            logger.error("could not find the PubMed directory at `" + inputDirectoryName + "'");
            throw new ResourceInitializationException();
        }

        inputFileList = new File(inputFileNameList);
        if (!inputFileList.exists()) {
            logger.error("could not find the input file at `" + inputFileNameList + "'");
            throw new ResourceInitializationException();
        }

        try {
            files = FileUtils.readLines(inputFileList).stream()
                .map(filename -> filename.trim())
                .filter(filename -> !filename.isEmpty()) // ignore empty lines
                .filter(filename -> !filename.startsWith("#")) // lines starting with a '#' are ignored
                .map(filename -> new File(inputDirectory, filename))
                .filter(file -> { if (file.exists()) { return true; } else { logger.warn(file.getAbsolutePath() + " doesn't exist"); return false; }})
                .collect(Collectors.toList());
            filesIt = files.iterator();
        } catch (IOException ex) {
            throw new ResourceInitializationException(ex);
        }
    }

    @Override
    public void getNext(JCas jCas) throws IOException, CollectionException, FileNotFoundException {
        File file = filesIt.next();
        logger.info("Reading[" + docIndex + "/" + files.size() + " ] `" + file.getAbsolutePath() + "'...");
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
        return filesIt.hasNext();
    }

    @Override
    public Progress[] getProgress() {
        return new Progress[docIndex];
    }

}

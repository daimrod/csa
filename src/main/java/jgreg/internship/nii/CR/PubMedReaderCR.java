package jgreg.internship.nii.CR;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import jgreg.internship.nii.RES.StringListRES;
import jgreg.internship.nii.types.Filename;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.apache.uima.UimaContext;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.fit.component.JCasCollectionReader_ImplBase;
import org.apache.uima.fit.component.ViewCreatorAnnotator;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.descriptor.ExternalResource;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Progress;

/**
 * List files in {@link #INPUT_DIRECTORY} and make CAS from the raw text.
 *
 * The text isn't stored in the default view but in the view named originalText.
 * This way, the {@link jgreg.internship.nii.AE.PubMedParserAE} can parse the
 * XML and set the CAS text in the default view.
 *
 * @author daimrod
 */
public class PubMedReaderCR extends JCasCollectionReader_ImplBase {

	/** The Constant logger. */
	private static final Logger logger = Logger.getLogger(PubMedReaderCR.class
			.getCanonicalName());

	/** Path to the PubMed Corpus. */
	public static final String INPUT_DIRECTORY = "inputDirectoryName";
	@ConfigurationParameter(name = INPUT_DIRECTORY, mandatory = true)
	private String inputDirectoryName;

	/** The input directory. */
	private File inputDirectory;

	/** The list of articles we want to anaylze. */
	public static final String CORPUS_ARTICLES = "corpusArticles";
	@ExternalResource(key = CORPUS_ARTICLES, mandatory = true)
	StringListRES corpusArticles;

	/** The files. */
	private List<File> files;

	/** The files it. */
	private Iterator<File> filesIt;

	/** The doc index. */
	private int docIndex = 0;

	/**
	 * Get PubMedReaderCR ready to read files in {@link #INPUT_DIRECTORY}.
	 *
	 * @param context
	 *            the context
	 * @throws ResourceInitializationException
	 *             the resource initialization exception
	 */
	@Override
	public void initialize(UimaContext context)
			throws ResourceInitializationException {
		super.initialize(context);

		logger.info("Listing `" + inputDirectoryName + "'...");
		inputDirectory = new File(inputDirectoryName);

		if (!inputDirectory.exists()) {
			logger.error("could not find the PubMed directory at `"
					+ inputDirectoryName + "'");
			throw new ResourceInitializationException();
		}

		files = corpusArticles.getList().stream()
				.map(filename -> new File(inputDirectory, filename))
				.filter(file -> {
					if (file.exists()) {
						return true;
					} else {
						logger.warn(file.getAbsolutePath() + " doesn't exist");
						return false;
					}
				}).collect(Collectors.toList());
		filesIt = files.iterator();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.apache.uima.fit.component.JCasCollectionReader_ImplBase#getNext(org
	 * .apache.uima.jcas.JCas)
	 */
	@Override
	public void getNext(JCas jCas) throws IOException, CollectionException,
			FileNotFoundException {
		docIndex++;

		File file = filesIt.next();
		logger.info("Reading[" + docIndex + "/" + files.size() + " ] `"
				+ file.getAbsolutePath() + "'...");
		try {
			JCas originalText = ViewCreatorAnnotator.createViewSafely(jCas,
					"originalText");
			originalText.setDocumentText(FileUtils.readFileToString(file));
			Filename filename = new Filename(originalText);
			filename.setFilename(file.getAbsolutePath());
			filename.addToIndexes();
		} catch (Exception ex) {
			throw new CollectionException(ex);
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.apache.uima.collection.base_cpm.BaseCollectionReader#hasNext()
	 */
	@Override
	public boolean hasNext() throws IOException, CollectionException {
		return filesIt.hasNext();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.apache.uima.collection.base_cpm.BaseCollectionReader#getProgress()
	 */
	@Override
	public Progress[] getProgress() {
		return new Progress[docIndex];
	}

}

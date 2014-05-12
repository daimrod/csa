package jgreg.internship.nii.CR;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import jgreg.internship.nii.RES.StringListRES;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.apache.uima.UimaContext;
import org.apache.uima.cas.impl.XmiCasDeserializer;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.fit.component.JCasCollectionReader_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.descriptor.ExternalResource;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Progress;

/**
 *
 * @author Grégoire Jadi
 */
public class XMIReader extends JCasCollectionReader_ImplBase {

	private static final Logger logger = Logger.getLogger(XMIReader.class
			.getCanonicalName());

	/**
	 * Path to the XMI documents
	 */
	public static final String INPUT_DIRECTORY = "inputDirectoryName";
	@ConfigurationParameter(name = INPUT_DIRECTORY, mandatory = true)
	private String inputDirectoryName;
	private File inputDirectory;

	public static final String RESTRICT_ON = "restrictOn";
	@ExternalResource(key = RESTRICT_ON, mandatory = false)
	StringListRES restrictOn;

	private List<File> files;
	private Iterator<File> filesIt;
	private int docIndex = 0;

	/**
	 * Get XMIReader ready to read files in INPUT_DIRECTORY.
	 *
	 * @param context
	 * @throws ResourceInitializationException
	 */
	@Override
	public void initialize(UimaContext context)
			throws ResourceInitializationException {
		super.initialize(context);

		logger.info("Listing `" + inputDirectoryName + "'...");
		inputDirectory = new File(inputDirectoryName);

		if (!inputDirectory.exists()) {
			logger.error("could not find the input directory at `"
					+ inputDirectoryName + "'");
			throw new ResourceInitializationException();
		}

		Collection<File> filesList;
		if (restrictOn == null) {
			String[] ext = { "xmi" };
			filesList = FileUtils.listFiles(inputDirectory, ext, true);
		} else {
			filesList = restrictOn.getList().stream()
					.map(filename -> new File(inputDirectory, filename))
					.collect(Collectors.toList());
		}

		files = filesList.stream().filter(file -> {
			if (file.exists()) {
				return true;
			} else {
				logger.warn(file.getAbsolutePath() + " doesn't exist");
				return false;
			}
		}).collect(Collectors.toList());
		filesIt = files.iterator();
	}

	@Override
	public void getNext(JCas jCas) throws IOException, CollectionException,
			FileNotFoundException {
		docIndex++;

		File file = filesIt.next();
		logger.info("Reading[" + docIndex + "/" + files.size() + " ] `"
				+ file.getAbsolutePath() + "'...");
		try {
            InputStream inputStream = new FileInputStream(file);
            XmiCasDeserializer.deserialize(inputStream, jCas.getCas());
		} catch (Exception ex) {
			logger.fatal(null, ex);
		}
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

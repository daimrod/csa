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

package jgreg.internship.nii.CR;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
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
 * This way, the AnalysiEngine that follows can parse the original text and set
 * the CAS text in the default view.
 *
 * This is useful to parse large collection of documents when you really don't
 * want to see your pipelane interrupted because of parsing errors.
 *
 * @author Grégoire Jadi
 */
public class DirectoryReaderCR extends JCasCollectionReader_ImplBase {

	/** The Constant logger. */
	private static final Logger logger = Logger
			.getLogger(DirectoryReaderCR.class.getCanonicalName());

	/** Path to the directory. */
	public static final String INPUT_DIRECTORY = "inputDirectoryName";
	@ConfigurationParameter(name = INPUT_DIRECTORY, mandatory = true)
	private String inputDirectoryName;
	private File inputDirectory;

	/** The list of articles we want to anaylze. */
    public static final String LIST_ARTICLES = "corpusArticles";
    @ExternalResource(key = LIST_ARTICLES, mandatory = false)
	StringListRES corpusArticles;

    /** The extensions of file we want to analyze. */
	public static final String EXTENSIONS = "extensions";
    @ConfigurationParameter(name = EXTENSIONS, mandatory = false)
	private String[] extensions;

	/** The files. */
	private List<File> files;

	/** The files it. */
	private Iterator<File> filesIt;

	/** The doc index. */
	private int docIndex = 0;

	/**
	 * Get DirectoryReaderCR ready to read files in {@link #INPUT_DIRECTORY}.
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
            logger.error("could not find Input Directory at `"
					+ inputDirectoryName + "'");
			throw new ResourceInitializationException();
		}

        if (corpusArticles != null) {
            logger.info("Using corpusArticles...");
            files = corpusArticles.getList().stream()
				.map(filename -> new File(inputDirectory, filename))
                .filter(file -> {
                        boolean ret = true;
                        if (!file.exists()) {
                            ret = false;
                            logger.warn(file.getAbsolutePath() + " doesn't exist.");
                        } else if (!file.isFile()) {
                            ret = false;
                            logger.warn(file.getAbsolutePath() + " isn't a file.");
                        }
                        return ret;
                    }).collect(Collectors.toList());
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("Looking for files matching ");
            for (int i = 0; i < extensions.length; i++) {
                sb.append(extensions[i]).append(' ');
            }
            sb.append("...");
            logger.info(sb.toString());
            files = new ArrayList<>(FileUtils.listFiles(inputDirectory, extensions, true));
        }

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
    public void getNext(JCas jCas) throws CollectionException,
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

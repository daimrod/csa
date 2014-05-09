package jgreg.internship.nii;

import java.io.File;

import jgreg.internship.nii.WF.Pipeline;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

public class App {
    private static final Logger logger = Logger.getLogger(App.class.getCanonicalName());

	public static void main(String[] args) throws Exception {
		String articlesFilenameSource = "/home/daimrod/corpus/pubmed/dev/test1.lst";
		String focusedArticlesFilenameSource = "/home/daimrod/corpus/pubmed/dev/co-cited.lst";

		Pipeline.process("/home/daimrod/corpus/pubmed/corpus/",
				"/home/daimrod/corpus/pubmed/dev/output/",
				FileUtils.readLines(new File(articlesFilenameSource)),
				FileUtils.readLines(new File(focusedArticlesFilenameSource)), 4);

        logger.info("done!");
	}
}

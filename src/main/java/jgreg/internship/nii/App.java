package jgreg.internship.nii;

import java.io.File;

import jgreg.internship.nii.WF.Pipeline;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

public class App {
	private static final Logger logger = Logger.getLogger(App.class
			.getCanonicalName());

	public static void main(String[] args) throws Exception {
		Pipeline.process("/home/daimrod/corpus/pubmed/corpus/",
				"/home/daimrod/corpus/pubmed/dev/output/",
				"/home/daimrod/corpus/pubmed/dev/test1.lst",
				"/home/daimrod/corpus/pubmed/dev/co-cited.lst",
				"/home/daimrod/corpus/pubmed/dev/mapping.lst", 4);

		logger.info("done!");
	}
}

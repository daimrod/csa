/*
 *
 */
package jgreg.internship.nii.Utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import jgreg.internship.nii.types.Token;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import edu.stanford.nlp.ling.CoreLabel;

/**
 * Provide some functions.
 */
public class Utils {

	/** The Constant logger. */
	private static final Logger logger = Logger.getLogger(Utils.class
			.getCanonicalName());

	/**
	 * Convert a List of {@link jgreg.internship.nii.types.Token} into a List of
	 * {@link edu.stanford.nlp.ling.CoreLabel}.
	 *
	 * @param tokens
	 *            to convert
	 * @return a new List
	 */
	public static List<CoreLabel> convertUIMA2STANFORD(List<Token> tokens) {
		return tokens.stream().map(token -> Utils.convertUIMA2STANFORD(token))
				.collect(Collectors.toList());
	}

	/**
	 * Convert a {@link jgreg.internship.nii.types.Token} into the equivalent
	 * Stanford JAVANLP Annotation.
	 *
	 * @param token
	 *            to convert to {@link edu.stanford.nlp.ling.CoreLabel}
	 * @return a new List containing the converted objects
	 */
	public static CoreLabel convertUIMA2STANFORD(Token token) {
		CoreLabel ret = new CoreLabel();
		ret.setBeginPosition(token.getBegin());
		ret.setEndPosition(token.getEnd());
		ret.setTag(token.getPOS());
		ret.setWord(token.getCoveredText());

		return ret;
	}

	/**
	 * Read lines from a file.
	 *
	 * Lines starting with a '#' and empty lines are ignored.
	 *
	 * @param file
	 *            the file
	 * @return the array list
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static ArrayList<String> readLines(File file) throws IOException {
		return new ArrayList(FileUtils.readLines(file).stream()
				.filter(line -> !line.startsWith("#")).map(line -> line.trim())
				.filter(line -> !line.isEmpty()).collect(Collectors.toList()));
	}

	/**
	 * List.
	 *
	 * @param size
	 *            the size
	 * @param initialElement
	 *            the initial element
	 * @return the array list
	 */
	public static ArrayList<Integer> List(int size, Integer initialElement) {
		ArrayList<Integer> ret = new ArrayList<>(size);
		for (int idx = 0; idx < size; idx++) {
			ret.add(idx, initialElement);
		}
		return ret;
	}
}

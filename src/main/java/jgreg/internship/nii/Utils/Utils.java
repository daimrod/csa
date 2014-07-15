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
        return new ArrayList<>(FileUtils.readLines(file).stream()
				.filter(line -> !line.startsWith("#")).map(line -> line.trim())
				.filter(line -> !line.isEmpty()).collect(Collectors.toList()));
	}

	/**
	 * Create a new ArrayList filled with an initial element.
	 *
	 * @param size
	 *            of the list.
	 * @param initialElement
	 *            the initial element.
	 * @return an array list.
	 */
	public static <T> ArrayList<T> List(int size, T initialElement) {
		ArrayList<T> ret = new ArrayList<>(size);
		for (int idx = 0; idx < size; idx++) {
			ret.add(idx, initialElement);
		}
		return ret;
	}
}

package jgreg.internship.nii.Utils;

import java.util.List;
import java.util.stream.Collectors;

import jgreg.internship.nii.types.Token;

import org.apache.log4j.Logger;

import edu.stanford.nlp.ling.CoreLabel;

public class Utils {
    private static final Logger logger = Logger.getLogger(Utils.class.getCanonicalName());
	/**
	 * Convert a List of Token into a List of CoreLabel
	 *
	 * @param tokens to convert
	 * @return a new List
	 */
    public static List<CoreLabel> convertUIMA2STANFORD(List<Token> tokens) {
        return tokens.stream()
            .map(token -> Utils.convertUIMA2STANFORD(token))
            .collect(Collectors.toList());
    }

	/**
	 * Convert a Token into the equivalent Stanford JAVANLP Annotation.
	 *
	 * @param token to convert to CoreLabel
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
}

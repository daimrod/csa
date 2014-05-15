package jgreg.internship.nii.WF;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import jgreg.internship.nii.Utils.Utils;
import jgreg.internship.nii.types.Token;

import org.apache.log4j.Logger;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.jcas.JCas;

import edu.stanford.nlp.ling.tokensregex.MultiPatternMatcher;
import edu.stanford.nlp.ling.tokensregex.TokenSequenceMatcher;
import edu.stanford.nlp.ling.tokensregex.TokenSequencePattern;

// TODO: Auto-generated Javadoc
/**
 * This simple workflow runs the PubMedReaderCR and the
 * CoCitationExtractorAE.
 */
public class TestWF04 {
    
    /** The Constant logger. */
    private static final Logger logger = Logger.getLogger(TestWF04.class.getCanonicalName());
    
    /**
     * The main method.
     *
     * @param args the arguments
     * @throws Exception the exception
     */
    public static void main(String[] args) throws Exception {
        JCas jCas = JCasFactory.createJCas();
        jCas.setDocumentText("some text");
        Token token = new Token(jCas);
        token.setBegin(0);
        token.setEnd(4);
        token.setPOS("NNP");

        List<Token> tokens = new LinkedList<>();
        tokens.add(token);
        logger.info("'" + token.getCoveredText() + "'");

        TokenSequencePattern p1 = TokenSequencePattern.compile("some");
        TokenSequencePattern p2 = TokenSequencePattern.compile("[ tag:\"NNP\" ]?");
        TokenSequenceMatcher m = p1.getMatcher(Utils.convertUIMA2STANFORD(tokens));

        while (m.find()) {
            logger.info("match found!");
        }

        logger.info("done!");
    }
}

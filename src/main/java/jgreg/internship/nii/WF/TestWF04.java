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

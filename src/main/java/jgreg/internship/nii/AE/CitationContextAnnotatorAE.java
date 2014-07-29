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

package jgreg.internship.nii.AE;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jgreg.internship.nii.RES.StringListRES;
import jgreg.internship.nii.types.Citation;
import jgreg.internship.nii.types.CitationContext;
import jgreg.internship.nii.types.Sentence;

import org.apache.log4j.Logger;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.descriptor.ExternalResource;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;

/**
 * Add {@link jgreg.internship.nii.types.CitationContext} annotation around
 * {@link jgreg.internship.nii.types.Citation}.
 *
 * <p>
 * The size of the context corresponds to the number of
 * {@link jgreg.internship.nii.types.Sentence} indicated by
 * {@link #PARAM_WINDOW_SIZE}.
 * </p>
 *
 * <p>
 * For example given the following sentences: {@code s1. s2. s3. s4. s5. s6}.
 * Let's suppose there is a {@link jgreg.internship.nii.types.Citation} in s2
 * and {@link #PARAM_WINDOW_SIZE} is set to 2. Then there will be a
 * {@link jgreg.internship.nii.types.CitationContext} covering s1, s2, s3, and
 * s4. That is, at most {@link #PARAM_WINDOW_SIZE} sentence before and after the
 * sentence.
 * </p>
 *
 * <p>
 * No matter what, the {@link jgreg.internship.nii.types.Sentence} in which the
 * {@link jgreg.internship.nii.types.Citation} occurs is always covered by the
 * {@link jgreg.internship.nii.types.CitationContext}.
 * </p>
 *
 * <p>
 * The {@link jgreg.internship.nii.types.Citation} considered can be restricted
 * with {@link #FOCUSED_ARTICLES}.
 * </p>
 *
 * @author Grégoire Jadi
 */
public class CitationContextAnnotatorAE extends
		org.apache.uima.fit.component.JCasAnnotator_ImplBase {

	/** The Constant logger. */
	private static final Logger logger = Logger
			.getLogger(CitationContextAnnotatorAE.class.getCanonicalName());

	/**
	 * The size of the window to consider around the
	 * {@link jgreg.internship.nii.types.Sentence} in which
	 * {@link jgreg.internship.nii.types.Citation} occur.
	 */
	public static final String PARAM_WINDOW_SIZE = "windowSize";
	@ConfigurationParameter(name = PARAM_WINDOW_SIZE, mandatory = true)
	private Integer windowSize;

	/**
	 * List of PMIDS to focus on.
	 *
	 * For example, an article may cite different articles, but we might not be
	 * interested by all {@link jgreg.internship.nii.types.Citation}.
	 *
	 * With this resource, we can choose to focus on some
	 * {@link jgreg.internship.nii.types.Citation} and ignore all the others.
	 */
	public static final String FOCUSED_ARTICLES = "focusedArticles";
	@ExternalResource(key = FOCUSED_ARTICLES, mandatory = false)
	StringListRES focusedArticles;

	public static final String COCITED_ARTICLES = "cocitedArticles";
	@ExternalResource(key = COCITED_ARTICLES, mandatory = false)
	StringListRES coCitedArticles;

	JCas jCas = null;
	Map<Sentence, Collection<Citation>> sentence2Citations = null;

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.apache.uima.analysis_component.JCasAnnotator_ImplBase#process(org
	 * .apache.uima.jcas.JCas)
	 */
	@Override
	public void process(JCas aCas) throws AnalysisEngineProcessException {
		jCas = aCas;
		sentence2Citations = JCasUtil.indexCovered(jCas, Sentence.class,
				Citation.class);

		int id = 0;

		Collection<Sentence> sentences = JCasUtil.select(jCas, Sentence.class);
		Iterator<Sentence> it = sentences.iterator();

		while (it.hasNext()) {
			Sentence sentence = (Sentence) it.next();

			ArrayList<Citation> citations;
			if (focusedArticles == null) {
				// If we don't have a list of articles to focus on, we
				// look at all of them.
				citations = new ArrayList<>(sentence2Citations.get(sentence));
			} else {
				// Otherwise, we only consider the Citation that
				// interest us.
				citations = new ArrayList<>(sentence2Citations
						.get(sentence)
						.stream()
						.filter(citation -> focusedArticles.contains(citation
								.getPMID())).collect(Collectors.toList()));
			}

			// Skip Sentence with no Citation (to focus on) in it.
			if (citations.isEmpty()) {
				continue;
			}

			// Convert the List<Citation> to List<FeatureStructure>
			// because that's what UIMA manipulates.
			List<FeatureStructure> citationsFS = new ArrayList<>(citations);

			int begin, end;
			// Extract the Sentence that belongs to the
			// CitationContext *before* the Sentence in which the
			// Citation occurs.
			List<Sentence> precedings = JCasUtil.selectPreceding(
					Sentence.class, sentence, windowSize);
			begin = sentence.getBegin();
			for (int i = precedings.size() - 1; i > 0; i--) {
				Sentence s = precedings.get(i);
				if (sentence2Citations.get(s).isEmpty()) {
					begin = s.getBegin();
				} else {
					break;
				}
			}

			// Extract the Sentence that belongs to the
			// CitationContext *after* the Sentence in which the
			// Citation occurs.
			List<Sentence> followings = JCasUtil.selectFollowing(
					Sentence.class, sentence, windowSize);
			end = sentence.getEnd();
			for (int i = 0; it.hasNext() && i <= windowSize; i++) {
				end = sentence.getEnd();
				sentence = (Sentence) it.next();
				if (!sentence2Citations.get(sentence).isEmpty())
					break;
			}

			CitationContext context = new CitationContext(jCas);
			context.setBegin(begin);
			context.setEnd(end);

			// Unfortunately, UIMA "complex" structures are very
			// crudes. That's why this code is ugly.
			FSArray fsArray = new FSArray(jCas, citationsFS.size());
			fsArray.copyFromArray(citationsFS
					.toArray(new FeatureStructure[citationsFS.size()]), 0, 0,
					citationsFS.size());

			context.setPMIDS(fsArray);
			context.setID(id);
			id += 1;

			context.addToIndexes(jCas);
		}
	}

	private List<Sentence> mergeContexts(List<Sentence> l1, List<Sentence> l2) {
		List<Sentence> ret = new ArrayList<>();
		ret.addAll(l1);
		int i = 0, len = l2.size();
		while (i < len && ret.contains(l2.get(i)))
			i++;
		if (i < len)
			ret.addAll(l2.subList(i, len));
		return ret;
	}

}

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jgreg.internship.nii.RES.StringListRES;
import jgreg.internship.nii.types.Citation;
import jgreg.internship.nii.types.CitationContext;
import jgreg.internship.nii.types.Paragraph;
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

	public static final String COCITED_ARTICLES = "coCitedArticles";
	@ExternalResource(key = COCITED_ARTICLES, mandatory = false)
	StringListRES coCitedArticles;

	JCas jCas = null;
    Map<Sentence, Collection<Citation>> sentence2Citations = null;
    Map<Sentence, Collection<Paragraph>> sentence2Paragraphs = null;

	Map<String, List<CitationContext>> citation2ctxs;

	int id;

	Collection<Sentence> sentences;

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

		// 1. Annotate all citation contextes.
		step1();

		// 2. Merge all co-citation contexts when it's possible.
		if (coCitedArticles != null) {
			step2();
		}

		// 3. Merge all remaining citation contexts when they occupy
		// the same place.
		step3();
    }

	private void step1() {
		citation2ctxs = new HashMap<>();
		sentence2Citations = JCasUtil.indexCovered(jCas, Sentence.class,
                Citation.class);
        sentence2Paragraphs = JCasUtil.indexCovering(jCas, Sentence.class, Paragraph.class);

		id = 0;

		sentences = JCasUtil.select(jCas, Sentence.class);

		for (Sentence sentence : sentences) {

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

            // Retrieve the (first) paragraph. It's the first paragraph
            // because a sentence can only be in one paragraph.
            Paragraph paragraph = sentence2Paragraphs.get(sentence).iterator().next();

			int begin, end;
			// Extract the Sentence that belongs to the
			// CitationContext *before* the Sentence in which the
			// Citation occurs.
			List<Sentence> precedings = JCasUtil.selectPreceding(
					Sentence.class, sentence, windowSize);
			begin = sentence.getBegin();
			for (int i = precedings.size() - 1; i > 0; i--) {
				Sentence s = precedings.get(i);
                if (s.getBegin() >= paragraph.getBegin()) {
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
			for (int i = 0; i < followings.size(); i++) {
                Sentence s = followings.get(i);
                if (s.getEnd() <= paragraph.getEnd()) {
                    end = s.getEnd();
                } else {
                    break;
                }
            }

			for (Citation citation : citations) {
				// Convert the List<Citation> to List<FeatureStructure>
				// because that's what UIMA manipulates.
				ArrayList<Citation> citationsArray = new ArrayList<>();
				citationsArray.add(citation);
				List<FeatureStructure> citationsFS = new ArrayList<>(
						citationsArray);

				CitationContext context = new CitationContext(jCas);
				context.setBegin(begin);
				context.setEnd(end);

				// Unfortunately, UIMA "complex" structures are very
				// crudes. That's why this code is ugly.
				FSArray fsArray = new FSArray(jCas, citationsFS.size());
				fsArray.copyFromArray(citationsFS
						.toArray(new FeatureStructure[citationsFS.size()]), 0,
						0, citationsFS.size());

				context.setCitations(fsArray);
				context.setID(id);
				context.setCocited(false);
				id += 1;

				context.addToIndexes(jCas);

				// Store (PMID, CitationContext*)
				String pmid = citation.getPMID();
				if (pmid != null) {
					List<CitationContext> contexts = citation2ctxs.get(pmid);
					if (contexts == null) {
						contexts = new ArrayList<>();
						citation2ctxs.put(pmid, contexts);
					}
					contexts.add(context);
				}
			}
		}
	}

	private void step2() {

		for (String rawPMIDS : coCitedArticles.getList()) {
			List<CitationContext> contexts = new ArrayList<>();
			for (String pmid : rawPMIDS.split(" ")) {
				contexts.addAll(citation2ctxs.get(pmid));
			}

			for (int i = 0; i < contexts.size() - 1; i++) {
				CitationContext c1 = contexts.get(i);

				for (int j = i + 1; j < contexts.size(); j++) {
					CitationContext c2 = contexts.get(j);

                    if (mergeOverlappingContexts(c1, c2)) {
                        c1.setCocited(true);
                        contexts.remove(j);
						j--;
					}
				}
			}
		}
	}

	private void step3() {
		List<CitationContext> contexts = new ArrayList(JCasUtil.select(jCas,
				CitationContext.class));

		for (int i = 0; i < contexts.size() - 1; i++) {
			CitationContext c1 = contexts.get(i);

			for (int j = i + 1; j < contexts.size(); j++) {
				CitationContext c2 = contexts.get(j);

				if (mergeIdenticalContexts(c1, c2)) {
                    contexts.remove(j);
					j--;
				}
			}
		}
	}

	private boolean mergeOverlappingContexts(CitationContext c1,
			CitationContext c2) {
		boolean ret = false;
		if (c2.getBegin() < c1.getEnd()) {
			c1.setEnd(c2.getEnd());
			ret = true;
		} else if (c1.getBegin() < c2.getEnd()) {
			c1.setBegin(c2.getBegin());
			ret = true;
		}

		if (ret) {
			List<FeatureStructure> citationsFS = new ArrayList<>();
			// copy c1 Citations to citationsFS
			FSArray c1FSArray = c1.getCitations();
			for (int idx = 0; idx < c1FSArray.size(); idx++) {
				citationsFS.add(c1FSArray.get(idx));
			}

			// copy c2 Citations to citationsFS
			FSArray c2FSArray = c2.getCitations();
			for (int idx = 0; idx < c2FSArray.size(); idx++) {
				citationsFS.add(c2FSArray.get(idx));
			}

			// copy citationsFS into fsArray
			FSArray fsArray = new FSArray(jCas, citationsFS.size());
			fsArray.copyFromArray(citationsFS
					.toArray(new FeatureStructure[citationsFS.size()]), 0, 0,
					citationsFS.size());

			// store fsArray
			c1.setCitations(fsArray);

			// cleanup unecessary citation context
			c2.removeFromIndexes();
		}

		return ret;
	}

	private boolean mergeIdenticalContexts(CitationContext c1,
			CitationContext c2) {
        if (c1.getBegin() != c2.getBegin() || c1.getEnd() != c2.getEnd() || c1.getCocited() || c2.getCocited()) {
            return false;
        }

		List<FeatureStructure> citationsFS = new ArrayList<>();
		// copy c1 Citations to citationsFS
		FSArray c1FSArray = c1.getCitations();
		for (int idx = 0; idx < c1FSArray.size(); idx++) {
			citationsFS.add(c1FSArray.get(idx));
		}

		// copy c2 Citations to citationsFS
		FSArray c2FSArray = c2.getCitations();
		for (int idx = 0; idx < c2FSArray.size(); idx++) {
			citationsFS.add(c2FSArray.get(idx));
		}

		// copy citationsFS into fsArray
		FSArray fsArray = new FSArray(jCas, citationsFS.size());
		fsArray.copyFromArray(
				citationsFS.toArray(new FeatureStructure[citationsFS.size()]),
				0, 0, citationsFS.size());

		// store fsArray
		c1.setCitations(fsArray);

		// cleanup unecessary citation context
		c2.removeFromIndexes();

		return true;
	}
}

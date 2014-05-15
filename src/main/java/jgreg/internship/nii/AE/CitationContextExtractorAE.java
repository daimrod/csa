package jgreg.internship.nii.AE;

import java.util.ArrayList;
import java.util.Collection;
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
public class CitationContextExtractorAE extends
		org.apache.uima.fit.component.JCasAnnotator_ImplBase {

	/** The Constant logger. */
	private static final Logger logger = Logger
			.getLogger(CitationContextExtractorAE.class.getCanonicalName());

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

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.apache.uima.analysis_component.JCasAnnotator_ImplBase#process(org
	 * .apache.uima.jcas.JCas)
	 */
	@Override
	public void process(JCas jCas) throws AnalysisEngineProcessException {
		Map<Sentence, Collection<Citation>> sentence2Citations = JCasUtil
				.indexCovered(jCas, Sentence.class, Citation.class);

		for (Sentence sentence : JCasUtil.select(jCas, Sentence.class)) {
			List<Citation> citations;
			if (focusedArticles == null) {
				// If we don't have a list of articles to focus on, we
				// look at all of them.
				citations = new ArrayList(sentence2Citations.get(sentence));
			} else {
				// Otherwise, we only consider the Citation that
				// interest us.
				citations = sentence2Citations
						.get(sentence)
						.stream()
						.filter(citation -> focusedArticles.contains(citation
								.getPMID())).collect(Collectors.toList());
			}

			// Skip Sentence with no Citation (to focus on) in it.
			if (citations.isEmpty()) {
				continue;
			}

			// Convert the List<Citation> to List<FeatureStructure>
			// because that's what UIMA manipulates.
			List<FeatureStructure> citationsFS = new ArrayList(citations);

			int begin, end;
			// Extract the Sentence that belongs to the
			// CitationContext *before* the Sentence in which the
			// Citation occurs.
			List<Sentence> precedings = JCasUtil.selectPreceding(
					Sentence.class, sentence, windowSize);
			if (precedings.size() > 0) {
				begin = precedings.get(0).getBegin();
			} else {
				begin = sentence.getBegin();
			}

			// Extract the Sentence that belongs to the
			// CitationContext *after* the Sentence in which the
			// Citation occurs.
			List<Sentence> followings = JCasUtil.selectFollowing(
					Sentence.class, sentence, windowSize);
			if (followings.size() > 0) {
				end = followings.get(followings.size() - 1).getEnd();
			} else {
				end = sentence.getEnd();
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
			context.addToIndexes(jCas);
		}
	}
}

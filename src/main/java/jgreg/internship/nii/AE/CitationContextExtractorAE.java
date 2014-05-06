package jgreg.internship.nii.AE;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import jgreg.internship.nii.types.Citation;
import jgreg.internship.nii.types.CitationContext;
import jgreg.internship.nii.types.Sentence;

import org.apache.log4j.Logger;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;

/**
 * Add CitationContext annotation around all Citation. The size of the context
 * correspond to the number of Sentences indicated by PARAM_WINDOW_SIZE.
 *
 * For example given the following scheme: s1. s2. s3. s4. s5. s6.
 *
 * Let's suppose there is a citation in s2 and PARAM_WINDOW_SIZE is set to 2.
 * Then there will be a CitationContext covering s1, s2, s3, and s4. That is, at
 * most PARAM_WINDOW_SIZE sentence before and after the sentence.
 *
 * No matter what, the sentence in which the citation occurs is always covered.
 *
 * @author Grégoire Jadi
 */
public class CitationContextExtractorAE extends
		org.apache.uima.fit.component.JCasAnnotator_ImplBase {
	private static final Logger logger = Logger
			.getLogger(CitationContextExtractorAE.class.getCanonicalName());

	/**
	 * The size of the window to consider around the Sentence in which Citations
	 * occur.
	 */
	public static final String PARAM_WINDOW_SIZE = "windowSize";
	@ConfigurationParameter(name = PARAM_WINDOW_SIZE, mandatory = true)
	private Integer windowSize;

	@Override
	public void process(JCas jCas) throws AnalysisEngineProcessException {
		Map<Sentence, Collection<Citation>> sentence2Citations = JCasUtil
				.indexCovered(jCas, Sentence.class, Citation.class);

		for (Sentence sentence : JCasUtil.select(jCas, Sentence.class)) {
			List<FeatureStructure> citations = new ArrayList(
					sentence2Citations.get(sentence));
			// Skip sentences with no Citations in it
			if (citations.size() == 0)
				continue;

			int begin, end;
			// Before
			List<Sentence> precedings = JCasUtil.selectPreceding(
					Sentence.class, sentence, windowSize);
			if (precedings.size() > 0) {
				begin = precedings.get(0).getBegin();
			} else {
				begin = sentence.getBegin();
			}

			// After
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
            
			FSArray fsArray = new FSArray(jCas, citations.size());
			fsArray.copyFromArray(
					citations.toArray(new FeatureStructure[citations.size()]),
					0, 0, citations.size());

			context.setPMIDS(fsArray);
			context.addToIndexes(jCas);
		}
	}
}

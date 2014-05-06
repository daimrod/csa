package jgreg.internship.nii.AE;

import java.util.Collection;
import java.util.Map;

import jgreg.internship.nii.RES.Article;
import jgreg.internship.nii.RES.ArticlesDB;
import jgreg.internship.nii.types.Citation;
import jgreg.internship.nii.types.CitationContext;
import jgreg.internship.nii.types.ID;
import jgreg.internship.nii.types.Negative;
import jgreg.internship.nii.types.Neutral;
import jgreg.internship.nii.types.Positive;

import org.apache.log4j.Logger;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ExternalResource;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

public class SentimentAnnotator extends
		org.apache.uima.fit.component.JCasAnnotator_ImplBase {
	private static final Logger logger = Logger
			.getLogger(SentimentAnnotator.class.getCanonicalName());

	public final static String PARAM_DB = "articlesDB";
	@ExternalResource(key = PARAM_DB, mandatory = true)
	private ArticlesDB articlesDB;

	@Override
	public void process(JCas jCas) throws AnalysisEngineProcessException {
		Map<Citation, Collection<Positive>> positives = JCasUtil.indexCovered(
				jCas, Citation.class, Positive.class);
		Map<Citation, Collection<Neutral>> neutrals = JCasUtil.indexCovered(
				jCas, Citation.class, Neutral.class);
		Map<Citation, Collection<Negative>> negatives = JCasUtil.indexCovered(
				jCas, Citation.class, Negative.class);

		Article currentArticle = articlesDB.get(JCasUtil.selectSingle(jCas,
				ID.class).getPMID());
		for (Citation citation : JCasUtil.select(jCas, Citation.class)) {
			for (Positive pos : positives.get(citation)) {
                
			}
		}
	}
}

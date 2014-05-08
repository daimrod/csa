package jgreg.internship.nii.AE;

import jgreg.internship.nii.RES.Article;
import jgreg.internship.nii.RES.ArticlesDB;
import jgreg.internship.nii.types.Citation;
import jgreg.internship.nii.types.CitationContext;
import jgreg.internship.nii.types.ID;
import jgreg.internship.nii.types.Negative;
import jgreg.internship.nii.types.Neutral;
import jgreg.internship.nii.types.Positive;
import jgreg.internship.nii.types.Sentiment;

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
        ID currentId = JCasUtil.selectSingle(jCas, ID.class);
        Article current = articlesDB.get(currentId.getPMID());

        for (Sentiment sent : JCasUtil.select(jCas, Sentiment.class)) {
            CitationContext context = sent.getContext();
            for (int i = 0; i < context.getPMIDS().size(); i++) {
                Citation citation= context.getPMIDS(i);
                Article article = articlesDB.get(citation.getPMID());

                article.add(current.getPMID(), sent);
            }
        }
	}
}

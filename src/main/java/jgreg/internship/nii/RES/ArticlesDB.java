package jgreg.internship.nii.RES;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.uima.resource.DataResource;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.SharedResourceObject;

public final class ArticlesDB implements SharedResourceObject {
	private static final Logger logger = Logger.getLogger(ArticlesDB.class
			.getCanonicalName());

	private Map<String, Article> db;

	public void load(DataResource aData) throws ResourceInitializationException {
		db = new HashMap<>();
	}

	public Article get(String PMID) {
		return db.get(PMID);
	}

	public Article add(Article article) {
		return db.put(article.getPMID(), article);
	}

	public Article remove(String PMID) {
		return db.remove(PMID);
	}
}

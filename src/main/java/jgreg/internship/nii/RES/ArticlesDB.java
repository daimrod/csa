package jgreg.internship.nii.RES;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.apache.uima.resource.DataResource;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.SharedResourceObject;

// TODO: Auto-generated Javadoc
/**
 * The Class ArticlesDB.
 */
public final class ArticlesDB implements SharedResourceObject {
	
	/** The Constant logger. */
	private static final Logger logger = Logger.getLogger(ArticlesDB.class
			.getCanonicalName());

	/** The db. */
	private Map<String, Article> db;

	/* (non-Javadoc)
	 * @see org.apache.uima.resource.SharedResourceObject#load(org.apache.uima.resource.DataResource)
	 */
	public void load(DataResource aData) throws ResourceInitializationException {
		db = new HashMap<>();
	}

	/**
	 * Gets the.
	 *
	 * @param PMID the pmid
	 * @return the article
	 */
	public Article get(String PMID) {
		return db.get(PMID);
	}

	/**
	 * Adds the.
	 *
	 * @param article the article
	 * @return the article
	 */
	public Article add(Article article) {
		return db.put(article.getPMID(), article);
	}

	/**
	 * Removes the.
	 *
	 * @param PMID the pmid
	 * @return the article
	 */
	public Article remove(String PMID) {
		return db.remove(PMID);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuilder ret = new StringBuilder();
		ret.append('[');
		for (Entry<String, Article> entry : db.entrySet()) {
			ret.append(entry.getKey()).append(", ").append(entry.getValue()).append(' ');
		}
		ret.append(']');
		return ret.toString();
	}
}

package jgreg.internship.nii.RES;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

// TODO: Auto-generated Javadoc
/**
 * The Class Article.
 */
public class Article {
	
	/** The Constant logger. */
	private static final Logger logger = Logger.getLogger(Article.class
			.getCanonicalName());

	/** The pmid. */
	private String PMID;
	
	/** The filename. */
	private String filename;
	
	/** The year. */
	private Integer year;

	/**
	 * Create a new ArticleScore.
	 *
	 * @param PMID            of the article
	 * @param filename the filename
	 * @param year the year
	 */
	public Article(String PMID, String filename, Integer year) {
		setPMID(PMID);
		setFilename(filename);
		setYear(year);
	}

	/**
	 * Create an empty ArticleScore.
	 *
	 * @param PMID the pmid
	 */
	public Article(String PMID) {
		this(PMID, "", null);
	}

	/**
	 * Gets the pmid.
	 *
	 * @return the PMID
	 */
	public String getPMID() {
		return PMID;
	}

	/**
	 * Sets the pmid.
	 *
	 * @param PMID            the PMID to set
	 */
	public void setPMID(String PMID) {
		this.PMID = PMID;
	}

	/**
	 * Gets the filename.
	 *
	 * @return the filename
	 */
	public String getFilename() {
		return filename;
	}

	/**
	 * Sets the filename.
	 *
	 * @param filename            the filename to set
	 */
	public void setFilename(String filename) {
		this.filename = filename;
	}

	/**
	 * Gets the year.
	 *
	 * @return the year
	 */
	public Integer getYear() {
		return year;
	}

	/**
	 * Sets the year.
	 *
	 * @param year            the year to set
	 */
	public void setYear(Integer year) {
		this.year = year;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object other) {
		return (other != null && other instanceof Article && ((Article) other).PMID
				.equals(PMID));
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder ret = new StringBuilder();
		ret.append("Article ").append(this.PMID);
		return ret.toString();
	}
}

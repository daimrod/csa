package jgreg.internship.nii.RES;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public class Article {
	private static final Logger logger = Logger.getLogger(Article.class
			.getCanonicalName());

	private String PMID;
	private String filename;
	private Integer year;

	/**
	 * Create a new ArticleScore
	 *
	 * @param PMID
	 *            of the article
	 */
	public Article(String PMID, String filename, Integer year) {
		setPMID(PMID);
		setFilename(filename);
		setYear(year);
	}

	/**
	 * Create an empty ArticleScore
	 *
	 * @param PMID
	 */
	public Article(String PMID) {
		this(PMID, "", null);
	}

	/**
	 * @return the PMID
	 */
	public String getPMID() {
		return PMID;
	}

	/**
	 * @param PMID
	 *            the PMID to set
	 */
	public void setPMID(String PMID) {
		this.PMID = PMID;
	}

	/**
	 * @return the filename
	 */
	public String getFilename() {
		return filename;
	}

	/**
	 * @param filename
	 *            the filename to set
	 */
	public void setFilename(String filename) {
		this.filename = filename;
	}

	/**
	 * @return the year
	 */
	public Integer getYear() {
		return year;
	}

	/**
	 * @param year
	 *            the year to set
	 */
	public void setYear(Integer year) {
		this.year = year;
	}

	@Override
	public boolean equals(Object other) {
		return (other != null && other instanceof Article && ((Article) other).PMID
				.equals(PMID));
	}

	@Override
	public String toString() {
		StringBuilder ret = new StringBuilder();
		ret.append("Article ").append(this.PMID);
		return ret.toString();
	}
}

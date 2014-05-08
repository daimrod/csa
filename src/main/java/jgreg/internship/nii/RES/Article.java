package jgreg.internship.nii.RES;

import java.util.ArrayList;
import java.util.List;

import jgreg.internship.nii.types.Negative;
import jgreg.internship.nii.types.Neutral;
import jgreg.internship.nii.types.Positive;
import jgreg.internship.nii.types.Sentiment;

import org.apache.log4j.Logger;

public class Article {
    private static final Logger logger = Logger.getLogger(Article.class.getCanonicalName());

    private String PMID;
    private String filename;
    private Integer year;
    private List<String> positives;
    private List<String> neutrals;
    private List<String> negatives;

	/**
	 * Create a new ArticleScore
	 *
	 * @param PMID of the article
	 * @param positiveScore
	 * @param neutralScore
	 * @param negativeScore
	 */
	public Article(String PMID, String filename, Integer year) {
        setPMID(PMID);
        setFilename(filename);
        setYear(year);
        positives = new ArrayList<>();
        neutrals = new ArrayList<>();
        negatives = new ArrayList<>();
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
	 * @param PMID the PMID to set
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
	 * @param filename the filename to set
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
	 * @param year the year to set
	 */
	public void setYear(Integer year) {
		this.year = year;
	}

	/**
	 * @return the positives
	 */
	public List<String> getPositives() {
		return positives;
	}

	/**
	 * @param positives the positives to set
	 */
	public void addPositive(String PMID) {
		positives.add(PMID);
	}

	/**
	 * @return the neutrals
	 */
	public List<String> getNeutrals() {
		return neutrals;
	}

    public void add(String PMID, Sentiment sent) {
        if (sent instanceof Positive)
            addPositive(PMID);
        else if (sent instanceof Neutral)
            addNeutral(PMID);
        else if (sent instanceof Negative)
            addNegative(PMID);
        else
            throw new UnsupportedOperationException();
    }

	/**
	 * @param neutrals the neutrals to set
	 */
	public void addNeutral(String PMID) {
		neutrals.add(PMID);
	}

	/**
	 * @return the negatives
	 */
	public List<String> getNegatives() {
		return negatives;
	}

	/**
	 * @param negatives the negatives to set
	 */
	public void addNegative(String PMID) {
		negatives.add(PMID);
	}

	@Override
    public boolean equals(Object other) {
        return (other != null && other instanceof Article && ((Article)other).PMID.equals(PMID));
    }

    @Override
    public String toString() {
        StringBuilder ret = new StringBuilder();
        ret.append("ArticleScore { ")
            .append(this.PMID)
            .append(", +")
            .append(this.positives.size())
            .append(", ~")
            .append(this.neutrals.size())
            .append(", -")
            .append(this.negatives.size())
            .append(" }");
        return ret.toString();
    }
}

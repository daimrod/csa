package jgreg.internship.nii.RES;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

public class Article {
    private static final Logger logger = Logger.getLogger(Article.class.getCanonicalName());

    private String PMID;
    private String filename;
    private LocalDate date;
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
	public Article(String PMID, String filename, LocalDate date) {
        setPMID(PMID);
        setFilename(filename);
        setDate(date);
        positives = new LinkedList<>();
        neutrals = new LinkedList<>();
        negatives = new LinkedList<>();
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
	 * @return the date
	 */
	public LocalDate getDate() {
		return date;
	}

	/**
	 * @param date the date to set
	 */
	public void setDate(LocalDate date) {
		this.date = date;
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
	public void setPositives(List<String> positives) {
		this.positives = positives;
	}

	/**
	 * @return the neutrals
	 */
	public List<String> getNeutrals() {
		return neutrals;
	}

	/**
	 * @param neutrals the neutrals to set
	 */
	public void setNeutrals(List<String> neutrals) {
		this.neutrals = neutrals;
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
	public void setNegatives(List<String> negatives) {
		this.negatives = negatives;
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

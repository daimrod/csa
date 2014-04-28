package jgreg.internship.nii.RES;

import org.apache.log4j.Logger;

public class SentimentAttr {
    private static final Logger logger = Logger.getLogger(SentimentAttr.class.getCanonicalName());

    public static final Integer DEFAULT_SCORE = 1;

    private String name;
	private String reason;
    private Integer score;

    public SentimentAttr(String name, Integer score, String reason) {
        setName(name);
        setScore(score);
        setReason(reason);
    }

	/**
	 * @return the score
	 */
	public Integer getScore() {
		return score;
	}

	/**
	 * @param score the score to set
	 */
	public void setScore(Integer score) {
		this.score = score;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the reason
	 */
	public String getReason() {
		return reason;
	}

	/**
	 * @param reason the reason to set
	 */
	public void setReason(String reason) {
		this.reason = reason;
	}
}


package jgreg.internship.nii.XML;

import java.io.Reader;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import jgreg.internship.nii.RES.Article;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

// TODO: Auto-generated Javadoc
/**
 * Parse PubMed articles.
 *
 * <p>
 * PubMed's articles use the JATS format which is described <a
 * href="http://jats.nlm.nih.gov/archiving/tag-library/0.4/t-2000.html"
 * >here</a>.
 * </p>
 *
 * @author Grégoire Jadi
 */
public class PubMedXMLParser {

	/** The Constant logger. */
	private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger
			.getLogger(PubMedXMLParser.class.getCanonicalName());

	/** The text. */
	private StringBuilder text;

	/** The citations. */
	private Map<String, List<Pair<Integer, Integer>>> citations;

	/** The sections. */
	private List<Pair<Integer, Integer>> sections;

	/** The section start. */
	private Integer sectionStart;

	/** The paragraphs. */
	private List<Pair<Integer, Integer>> paragraphs;

	/** The paragraph start. */
	private Integer paragraphStart;

	/** The titles. */
	private List<Pair<Integer, Integer>> titles;

	/** The title start. */
	private Integer titleStart;

	/** The xmlr. */
	private XMLStreamReader xmlr;

	/** The xmlif. */
	private XMLInputFactory xmlif;

	/** The event type. */
	private int eventType;

	/** The article. */
	private Article article;

	/**
	 * Initialize a parser.
	 *
	 * @param reader
	 *            the reader
	 */
	public PubMedXMLParser(Reader reader) {
		text = new StringBuilder();
		citations = new HashMap<>();
		article = new Article("");

		sections = new LinkedList<>();
		titles = new LinkedList<>();
		paragraphs = new LinkedList<>();

		/* Initialization */
		xmlif = XMLInputFactory.newInstance();
		try {
			xmlr = xmlif.createXMLStreamReader(reader);
		} catch (XMLStreamException ex) {
			logger.fatal(null, ex);
		}
		parse();
	}

	/**
	 * Parse the body of a PubMed's article.
	 *
	 * @throws XMLStreamException
	 *             the XML stream exception
	 */
	private void parseBody() throws XMLStreamException {
		while (xmlr.hasNext()) {
			eventType = xmlr.next();

			if (xmlr.hasName() && XMLStreamConstants.END_ELEMENT == eventType
					&& xmlr.getLocalName().equals("body")) {
				break;
			} else if (xmlr.hasName()
					&& XMLStreamConstants.START_ELEMENT == eventType
					&& (xmlr.getLocalName().equals("p")
							|| xmlr.getLocalName().equals("title") || xmlr
							.getLocalName().equals("sec"))) {
				addStart(text.length(), xmlr.getLocalName());
			} else if (xmlr.hasName()
					&& XMLStreamConstants.END_ELEMENT == eventType
					&& (xmlr.getLocalName().equals("p")
							|| xmlr.getLocalName().equals("title") || xmlr
							.getLocalName().equals("sec"))) {
				/*
				 * Add newline when it's necessary
				 */
				text.append("\n\n");
				addEnd(text.length(), xmlr.getLocalName());
			} else if (xmlr.hasName()
					&& XMLStreamConstants.START_ELEMENT == eventType
					&& xmlr.getLocalName().equals("xref")
					&& xmlr.getAttributeValue(null, "ref-type").equals("bibr")) {
				/*
				 * Store references
				 */

				// Keep this order
				String citationIds = xmlr.getAttributeValue(null, "rid"); // 1
				String citation = xmlr.getElementText(); // 2
				logger.debug("Found xref `" + citationIds + "'");
				for (String citationId : citationIds.split(" ")) {
					int start = text.length();
					int end = start + citation.length();
					addCitation(citationId, start, end);
					addText(citation);
				}
			} else if (xmlr.hasName()
					&& XMLStreamConstants.START_ELEMENT == eventType
					&& (xmlr.getLocalName().equals("table-wrap") || xmlr
							.getLocalName().equals("fig"))) {
				/*
				 * Ignore table and figure
				 */
				skipSubtree();
			} else if (xmlr.hasText()) {
				/*
				 * append all text
				 */
				addText(xmlr.getText());
			}
		}
	}

	/**
	 * Parse the references of a PubMed's article.
	 *
	 * @throws XMLStreamException
	 *             the XML stream exception
	 */
	private void parseReferences() throws XMLStreamException {
		boolean continue_ = true;

		while (xmlr.hasNext() && continue_) {
			eventType = xmlr.next();
			if (XMLStreamConstants.START_ELEMENT == eventType && xmlr.hasName()
					&& xmlr.getLocalName().equals("ref")) {
				String localId = xmlr.getAttributeValue(null, "id");
				if (citations.containsKey(localId)
						&& gotoTag(
								"pub-id",
								(XMLStreamReader xr) -> xr.getAttributeValue(
										null, "pub-id-type").equals("pmid"))) {
					/**
					 * Update the citations Replace the local reference by the
					 * appropriate PMID
					 */
					String pmid = StringUtils.trim(xmlr.getElementText());
					List<Pair<Integer, Integer>> tmp = citations.get(localId);
					citations.remove(localId);
					citations.put(pmid, tmp);
					logger.debug("Found PMID(" + pmid + ") for `" + localId
							+ "'");
				} else {
					citations.remove(localId);
					logger.debug("Could not find PMID for `" + localId + "'");
				}
			}
		}
	}

	/**
	 * Extract Meta information from the article.
	 *
	 * @throws XMLStreamException
	 *             the XML stream exception
	 */
	private void parseMeta() throws XMLStreamException {
		while (xmlr.hasNext()) {
			eventType = xmlr.next();

			if
			/**
			 * Extract the PMID from the document
			 */
			(!hasPMID()
					&& xmlr.hasName()
					&& XMLStreamConstants.START_ELEMENT == eventType
					&& xmlr.getLocalName().equals("article-id")
					&& xmlr.getAttributeValue(null, "pub-id-type").equals(
							"pmid")) {
				article.setPMID(StringUtils.trim(xmlr.getElementText()));

			} else if
			/**
			 * Extract the YEAR from the document
			 */
			(article.getYear() == null
					&& XMLStreamConstants.START_ELEMENT == eventType
					&& xmlr.hasName() && xmlr.getLocalName().equals("pub-date")) {
				if (gotoTag("year")) {
					article.setYear(new Integer(xmlr.getElementText()));
				}
			} else if
			/**
			 * End parsing of the metadata
			 */
			(XMLStreamConstants.END_ELEMENT == eventType && xmlr.hasName()
					&& xmlr.getLocalName().equals("article-meta")) {
				if (article.getYear() == null) {
					logger.warn("No date for " + article.getPMID());
				}
				break;
			}
		}
	}

	/**
	 * Extract the important stuff from the XML.
	 */
	private void parse() {
		try {
			while (xmlr.hasNext()) {
				eventType = xmlr.next();
				/**
				 * Detect whether we are in the body or not.
				 */
				if (XMLStreamConstants.START_ELEMENT == eventType
						&& xmlr.hasName()) {
					switch (xmlr.getLocalName().toLowerCase()) {
					case "article-meta":
						parseMeta();
						break;
					case "body":
						parseBody();
						break;
					case "ref-list":
						parseReferences();
						break;
					}
				}
			}
		} catch (XMLStreamException ex) {
			logger.fatal("Could not parse `" + getPMID() + "'", ex);
		}
	}

	/**
	 * *************************************************************************
	 * Utils
	 * ************************************************************************.
	 *
	 * @throws XMLStreamException
	 *             the XML stream exception
	 */
	/**
	 * Skip the current subtree
	 *
	 * @throws XMLStreamException
	 */
	private void skipSubtree() throws XMLStreamException {
		String toTrack = xmlr.getLocalName();
		int count = 1;
		while (xmlr.hasNext() && count > 0) {
			eventType = xmlr.next();
			if (XMLStreamConstants.END_ELEMENT == eventType && xmlr.hasName()
					&& xmlr.getLocalName().equals(toTrack)) {
				count = count - 1;
			}
		}
	}

	/**
	 * Add text after having trimmed it.
	 *
	 * @param text
	 *            to add
	 * @return a StringBuilder to continue the processing if needed
	 */
	private StringBuilder addText(String text) {
		return this.text.append(StringUtils.trim(text));
	}

	/**
	 * Go to the next specified tag in the current subtree.
	 *
	 * @param tag
	 *            to go
	 * @param op
	 *            is a predicate
	 * @return true if the op returned true during the last run, false otherwise
	 * @throws XMLStreamException
	 *             the XML stream exception
	 */
	private boolean gotoTag(String tag, Predicate<XMLStreamReader> op)
			throws XMLStreamException {
		int depth = 1;
		boolean continue_ = true;
		while (xmlr.hasNext() && continue_ && depth > 0) {
			eventType = xmlr.next();
			continue_ = !(XMLStreamConstants.START_ELEMENT == eventType
					&& xmlr.hasName() && xmlr.getLocalName().equals(tag)
					&& op != null && op.test(xmlr));
			switch (eventType) {
			case XMLStreamConstants.START_ELEMENT:
				depth += 1;
				break;
			case XMLStreamConstants.END_ELEMENT:
				depth -= 1;
				break;
			}

		}
		return !continue_;
	}

	/**
	 * Goto tag.
	 *
	 * @param tag
	 *            the tag
	 * @return true, if successful
	 * @throws XMLStreamException
	 *             the XML stream exception
	 */
	private boolean gotoTag(String tag) throws XMLStreamException {
		return gotoTag(tag, _ignore -> true);
	}

	/**
	 * Add a citation at the given position.
	 *
	 * @param citation
	 *            to add
	 * @param start
	 *            of the citation
	 * @param end
	 *            of the citation
	 */
	private void addCitation(String citation, int start, int end) {
		if (citations.containsKey(citation)) {
			citations.get(citation).add(new ImmutablePair<>(start, end));
		} else {
			List<Pair<Integer, Integer>> tmp = new LinkedList<>();
			tmp.add(new ImmutablePair<>(start, end));
			citations.put(citation, tmp);
		}
	}

	/**
	 * Adds the start.
	 *
	 * @param start
	 *            the start
	 * @param name
	 *            the name
	 */
	private void addStart(Integer start, String name) {
		switch (name.toLowerCase()) {
		case "p":
			paragraphStart = start;
			break;
		case "title":
			titleStart = start;
			break;
		case "sec":
			sectionStart = start;
			break;
		}
	}

	/**
	 * Adds the end.
	 *
	 * @param end
	 *            the end
	 * @param name
	 *            the name
	 */
	private void addEnd(Integer end, String name) {
		switch (name.toLowerCase()) {
		case "p":
			paragraphs.add(new ImmutablePair<>(paragraphStart, end));
			break;
		case "title":
			titles.add(new ImmutablePair<>(titleStart, end));
			break;
		case "sec":
			sections.add(new ImmutablePair<>(sectionStart, end));
			break;
		}
	}

	/**
	 * *************************************************************************
	 * Getters
	 * ************************************************************************.
	 *
	 * @return the text
	 */
	public String getText() {
		return text.toString();
	}

	/**
	 * Gets the citations.
	 *
	 * @return the citations
	 */
	public Map<String, List<Pair<Integer, Integer>>> getCitations() {
		return citations;
	}

	/**
	 * Gets the sections.
	 *
	 * @return the sections
	 */
	public List<Pair<Integer, Integer>> getSections() {
		return sections;
	}

	/**
	 * Gets the titles.
	 *
	 * @return the titles
	 */
	public List<Pair<Integer, Integer>> getTitles() {
		return titles;
	}

	/**
	 * Gets the article.
	 *
	 * @return the article
	 */
	public Article getArticle() {
		return article;
	}

	/**
	 * Gets the paragraphs.
	 *
	 * @return the paragraphs
	 */
	public List<Pair<Integer, Integer>> getParagraphs() {
		return paragraphs;
	}

	/**
	 * Gets the pmid.
	 *
	 * @return the pmid
	 */
	public String getPMID() {
		return article.getPMID();
	}

	/**
	 * Checks for pmid.
	 *
	 * @return true, if successful
	 */
	public boolean hasPMID() {
		return !getPMID().isEmpty();
	}
}

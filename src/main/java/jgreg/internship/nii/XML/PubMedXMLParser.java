//
// Author:: Grégoire Jadi <daimrod@gmail.com>
// Copyright:: Copyright (c) 2014, Grégoire Jadi
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are
// met:
//
//    1. Redistributions of source code must retain the above copyright
//       notice, this list of conditions and the following disclaimer.
//
//    2. Redistributions in binary form must reproduce the above
//       copyright notice, this list of conditions and the following
//       disclaimer in the documentation and/or other materials provided
//       with the distribution.
//
// THIS SOFTWARE IS PROVIDED BY GRÉGOIRE JADI ``AS IS'' AND ANY
// EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
// PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL GRÉGOIRE JADI OR
// CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
// SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
// LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
// USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
// ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
// OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
// OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
// SUCH DAMAGE.
//
// The views and conclusions contained in the software and
// documentation are those of the authors and should not be
// interpreted as representing official policies, either expressed or
// implied, of Grégoire Jadi.
//

package jgreg.internship.nii.XML;

import java.io.Reader;
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
					&& "body".equals(xmlr.getLocalName())) {
				break;
			} else if (xmlr.hasName()
					&& XMLStreamConstants.START_ELEMENT == eventType
					&& ("p".equals(xmlr.getLocalName())
							|| "title".equals(xmlr.getLocalName()) || "sec"
								.equals(xmlr.getLocalName()))) {
				addStart(text.length(), xmlr.getLocalName());
			} else if (xmlr.hasName()
					&& XMLStreamConstants.END_ELEMENT == eventType
					&& ("p".equals(xmlr.getLocalName())
							|| "title".equals(xmlr.getLocalName()) || "sec"
								.equals(xmlr.getLocalName()))) {
				/*
				 * Add newline when it's necessary
				 */
				text.append("\n\n");
				addEnd(text.length(), xmlr.getLocalName());
			} else if (xmlr.hasName()
					&& XMLStreamConstants.START_ELEMENT == eventType
                    && "xref".equals(xmlr.getLocalName())
                       // http://jats.nlm.nih.gov/archiving/tag-library/0.4/n-cyk2.html
                       // ref-type="bib" should not be used but some
                       // articles do so anyway. (e.g. 1043602 or 11067871)
                    && ("bibr".equals(xmlr.getAttributeValue(null, "ref-type"))
                        || "bib".equals(xmlr.getAttributeValue(null, "ref-type")))) {
				/*
				 * Store references
				 */

				// Keep this order
				String citationIds = xmlr.getAttributeValue(null, "rid"); // 1
				String citation = getElementsText(); // 2
				logger.debug("Found xref `" + citationIds + "' for `"
						+ citation + "'");

				for (String citationId : citationIds.split(" ")) {
					int start = text.length();
					int end = start + citation.length();
					addCitation(citationId, start, end);
					addText(citation);
				}
			} else if (xmlr.hasName()
					&& XMLStreamConstants.START_ELEMENT == eventType
					&& ("table-wrap".equals(xmlr.getLocalName()) || "fig"
							.equals(xmlr.getLocalName()))) {
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
					&& "ref".equals(xmlr.getLocalName())) {
				String localId = xmlr.getAttributeValue(null, "id");
				if (citations.containsKey(localId)
						&& gotoTag(
								"pub-id",
								(XMLStreamReader xr) -> "pmid".equals(xr
										.getAttributeValue(null, "pub-id-type")))) {
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
					&& "article-id".equals(xmlr.getLocalName())
					&& "pmid".equals(xmlr
							.getAttributeValue(null, "pub-id-type"))) {
				article.setPMID(StringUtils.trim(xmlr.getElementText()));

			} else if
            /**
			 * Extract the TITLE from the document
			 */
			(article.getYear() == null
					&& XMLStreamConstants.START_ELEMENT == eventType
					&& xmlr.hasName() && "article-title".equals(xmlr.getLocalName())) {
                article.setTitle(getElementsText());
			} else if
			/**
			 * Extract the YEAR from the document
			 */
			(article.getYear() == null
					&& XMLStreamConstants.START_ELEMENT == eventType
					&& xmlr.hasName() && "pub-date".equals(xmlr.getLocalName())) {
				if (gotoTag("year")) {
					article.setYear(new Integer(xmlr.getElementText()));
				}
			} else if
			/**
			 * End parsing of the metadata
			 */
			(XMLStreamConstants.END_ELEMENT == eventType && xmlr.hasName()
					&& "article-meta".equals(xmlr.getLocalName())) {
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
		int count = 1;
		while (xmlr.hasNext() && count > 0) {
			eventType = xmlr.next();
			if (XMLStreamConstants.START_ELEMENT == eventType) {
				count += 1;
			} else if (XMLStreamConstants.END_ELEMENT == eventType) {
				count -= 1;
			}
		}
	}

	private String getElementsText() throws XMLStreamException {
		StringBuilder ret = new StringBuilder();
		int count = 1;
		while (xmlr.hasNext() && count > 0) {
			eventType = xmlr.next();
			if (XMLStreamConstants.START_ELEMENT == eventType) {
				count += 1;
			} else if (XMLStreamConstants.END_ELEMENT == eventType) {
				count -= 1;
			} else if (XMLStreamConstants.CHARACTERS == eventType
					|| XMLStreamConstants.CDATA == eventType
					|| XMLStreamConstants.SPACE == eventType
					|| XMLStreamConstants.ENTITY_REFERENCE == eventType) {
				ret.append(xmlr.getText());
			}
		}

		return ret.toString();
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
					&& xmlr.hasName() && tag.equals(xmlr.getLocalName())
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

	private static String debugXMLStreamConstants(int eventType) {
		switch (eventType) {
		case XMLStreamConstants.ATTRIBUTE:
			return "ATTRIBUTE";
		case XMLStreamConstants.CDATA:
			return "CDATA";
		case XMLStreamConstants.CHARACTERS:
			return "CHARACTERS";
		case XMLStreamConstants.COMMENT:
			return "COMMENT";
		case XMLStreamConstants.DTD:
			return "DTD";
		case XMLStreamConstants.END_DOCUMENT:
			return "END_DOCUMENT";
		case XMLStreamConstants.END_ELEMENT:
			return "END_ELEMENT";
		case XMLStreamConstants.ENTITY_DECLARATION:
			return "ENTITY_DECLARATION";
		case XMLStreamConstants.ENTITY_REFERENCE:
			return "ENTITY_REFERENCE";
		case XMLStreamConstants.NAMESPACE:
			return "NAMESPACE";
		case XMLStreamConstants.NOTATION_DECLARATION:
			return "NOTATION_DECLARATION";
		case XMLStreamConstants.PROCESSING_INSTRUCTION:
			return "PROCESSING_INSTRUCTION";
		case XMLStreamConstants.SPACE:
			return "SPACE";
		case XMLStreamConstants.START_DOCUMENT:
			return "START_DOCUMENT";
		case XMLStreamConstants.START_ELEMENT:
			return "START_ELEMENT";
		default:
			return "Unknown constants";
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

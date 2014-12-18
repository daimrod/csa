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

package jgreg.internship.nii.AE;

import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.function.Predicate;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import jgreg.internship.nii.RES.Article;
import jgreg.internship.nii.types.Citation;
import jgreg.internship.nii.types.Filename;
import jgreg.internship.nii.types.ID;
import jgreg.internship.nii.types.Paragraph;
import jgreg.internship.nii.types.Section;
import jgreg.internship.nii.types.Title;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CASException;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

/**
 * This analysis parses PubMed's articles and add the following annotations:
 * <ul>
 * <li> {@link jgreg.internship.nii.types.Citation}</li>
 * <li> {@link jgreg.internship.nii.types.Section}</li>
 * <li> {@link jgreg.internship.nii.types.Title} (the section's title)</li>
 * <li> {@link jgreg.internship.nii.types.Paragraph}</li>
 * <li> {@link jgreg.internship.nii.types.ID} (article's PMID)</li>
 * </ul>
 *
 * @author Grégoire Jadi
 */
public class PubMedParserAE extends
		org.apache.uima.fit.component.JCasAnnotator_ImplBase {

	/** The Constant logger. */
	private static final Logger logger = Logger.getLogger(PubMedParserAE.class
			.getCanonicalName());

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.apache.uima.analysis_component.JCasAnnotator_ImplBase#process(org
	 * .apache.uima.jcas.JCas)
	 */

	private JCas jCas;

	/** The text. */
	private StringBuilder text;

	/** The citations. */
	private Map<String, Citation> citations;

	/** The sections. */
	private Stack<Integer> sections;

	/** The paragraphs. */
	private Stack<Integer> paragraphs;

	/** The titles. */
	private Stack<Integer> titles;

	/** The xmlr. */
	private XMLStreamReader xmlr;

	/** The xmlif. */
	private XMLInputFactory xmlif;

	/** The event type. */
	private int eventType;

	/** The article. */
	private Article article;

	/** The Read */
	Reader reader;

	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		jCas = aJCas;

		// retrieve the originalText read in
		// jgreg.internship.nii.CR.DirectoryReaderCR
		JCas originalText;
		try {
			originalText = jCas.getView("originalText");
		} catch (CASException ex) {
			throw new AnalysisEngineProcessException(ex);
		}

		Filename filename = JCasUtil.selectSingle(originalText, Filename.class);
		logger.info("Parsing `" + filename.getFilename() + "'...");

        reader = new StringReader(originalText.getDocumentText());
        try {
            parse();

            jCas.setDocumentText(text.toString());

            article.setFilename(filename.getFilename());

            ID docId = new ID(jCas);
            docId.setPMID(article.getPMID());
            docId.setYear(article.getYear());
            docId.setTitle(article.getTitle());
            docId.setBegin(0);
            docId.setEnd(1);
            docId.addToIndexes();
        } catch (Exception ex) {
            logger.error("Couldn't parse " + filename.getFilename() + "\n" + ex);
            throw new AnalysisEngineProcessException();
        }
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
					&& "xref".equals(xmlr.getLocalName())) {

				if ("bibr".equals(xmlr.getAttributeValue(null, "ref-type"))
				// http://jats.nlm.nih.gov/archiving/tag-library/0.4/n-cyk2.html
				// ref-type="bib" should not be used but some articles
				// do so anyway. (e.g. 1043602 or 11067871)
						|| "bib".equals(xmlr
								.getAttributeValue(null, "ref-type"))) {
					// Store bibliographic references

					// WARNING: DONT CHANGE THE ORDER OF THE NEXT
					// INSTRUCTIONS UNLESS YOU KNOW WHAT YOU ARE DOING
					// (operations on xmlr change the cursor position)
					// BEGIN
					String citationIds = xmlr.getAttributeValue(null, "rid"); // 1
					String citationText = getElementsText(); // 2
					// END
					logger.debug("Found xref `" + citationIds + "' for `"
							+ citationText + "'");

					for (String citationId : citationIds.split(" ")) {
						String placeholder = "CITE";
						Citation citation = new Citation(jCas);

						citation.setBegin(text.length());
						citation.setEnd(citation.getBegin()
								+ placeholder.length());
						citation.setRID(citationId);
						citation.setText(citationText);
						citation.addToIndexes();

						citations.put(citationId, citation);

						addText(placeholder);
					}
				} else {
					// Ignore references to anything else (table, fig, ...)
					skipSubtree();
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
					Citation citation = citations.get(localId);
					citation.setPMID(pmid);

					logger.debug("Found PMID(" + pmid + ") for `" + localId
							+ "'");
				} else {
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
			(article.getPMID().isEmpty()
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
					&& xmlr.hasName()
					&& "article-title".equals(xmlr.getLocalName())) {
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
    private void parse() throws XMLStreamException {
		text = new StringBuilder();
		citations = new HashMap<>();
		article = new Article("");

		sections = new Stack<>();
		titles = new Stack<>();
		paragraphs = new Stack<>();

		/* Initialization */
		xmlif = XMLInputFactory.newInstance();
		try {
			xmlr = xmlif.createXMLStreamReader(reader);
		} catch (XMLStreamException ex) {
			logger.fatal(null, ex);
		}

		while (xmlr.hasNext()) {
			eventType = xmlr.next();
			/**
			 * Detect whether we are in the body or not.
			 */
			if (XMLStreamConstants.START_ELEMENT == eventType && xmlr.hasName()) {
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
			paragraphs.push(start);
			break;
		case "title":
			titles.push(start);
			break;
		case "sec":
			sections.push(start);
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
			Paragraph paragraph = new Paragraph(jCas);
			paragraph.setBegin(paragraphs.pop());
			paragraph.setEnd(end);
			paragraph.addToIndexes();
			break;
		case "title":
			Title title = new Title(jCas);
			title.setBegin(titles.pop());
			title.setEnd(end);
			title.addToIndexes();
			break;
		case "sec":
			Section section = new Section(jCas);
			section.setBegin(sections.pop());
			section.setEnd(end);
			section.addToIndexes();
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
}

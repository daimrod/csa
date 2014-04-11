/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jgreg.internship.nii.XML;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Level;

/**
 *
 * @author daimrod
 */
public class PubMedXMLParser {

    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(PubMedXMLParser.class.getCanonicalName());

    private String filename;
    private String PMID;
    private StringBuilder text;
    private Map<String, List<Pair<Integer, Integer>>> citations;

    private XMLStreamReader xmlr;
    private XMLInputFactory xmlif;
    private int eventType;

    /**
     * Dummy main to test interactively the class
     *
     * @param args
     * @throws XMLStreamException
     * @throws FileNotFoundException
     */
    public static void main(String[] args) throws XMLStreamException, FileNotFoundException {
        PubMedXMLParser parser = new PubMedXMLParser();

        System.out.println(parser.getFilename() + ": " + parser.getPMID());
        System.out.println(parser.getText());
        System.out.println(parser.getCitations());
    }

    /**
     * Initialize a parser
     *
     * @param filename to parse
     * @throws FileNotFoundException
     */
    public PubMedXMLParser(String filename) throws FileNotFoundException {
        this.filename = filename;
        text = new StringBuilder();
        PMID = "";
        citations = new HashMap<>();

        /* Initialization */
        xmlif = XMLInputFactory.newInstance();
        try {
            xmlr = xmlif.createXMLStreamReader(filename,
                    new FileInputStream(filename));
        } catch (XMLStreamException ex) {
            logger.log(Level.FATAL, null, ex);
        }
        parse();
    }

    /**
     * Dummy constructor to test the class on a file
     *
     * @throws XMLStreamException
     * @throws FileNotFoundException
     */
    public PubMedXMLParser() throws XMLStreamException, FileNotFoundException {
        this("/home/daimrod/corpus/pubmed/AAPS_J/AAPS_J_2008_Apr_2_10(1)_193-199.nxml");
    }

    /**
     * Parse the body of a PubMed's article
     *
     * @throws javax.xml.stream.XMLStreamException
     */
    private void parseBody() throws XMLStreamException {
        boolean continue_ = true;
        while (xmlr.hasNext() && continue_) {
            eventType = xmlr.next();

            if (xmlr.hasName()
                    && XMLStreamConstants.END_ELEMENT == eventType
                    && xmlr.getLocalName().equals("body")) {
                continue_ = false;
            } else if (xmlr.hasName()
                    && XMLStreamConstants.END_ELEMENT == eventType
                    && (xmlr.getLocalName().equals("p")
                    || xmlr.getLocalName().equals("section")
                    || xmlr.getLocalName().equals("title")
                    || xmlr.getLocalName().equals("sec"))) {
                // Add newline when it's necessary
                text.append('\n');
            } else if (xmlr.hasName()
                    && XMLStreamConstants.START_ELEMENT == eventType
                    && xmlr.getLocalName().equals("xref")
                    && xmlr.getAttributeValue(null, "ref-type").equals("bibr")) {
                // Store references
                String citationId = xmlr.getAttributeValue(null, "rid"); // 1
                String citation = xmlr.getElementText();                 // 2
                int start = text.length();
                int end = start + citation.length();
                addCitation(citationId, start, end);
                addText(citation);
            } else if (xmlr.hasName()
                    && XMLStreamConstants.START_ELEMENT == eventType
                    && (xmlr.getLocalName().equals("table-wrap")
                    || xmlr.getLocalName().equals("fig"))) {
                // Ignore table and figure
                skipSubtree();
            } else if (xmlr.hasText()) {
                // append all text
                addText(xmlr.getText());
            }
        }
    }

    /**
     * Parse the references of a PubMed's article
     *
     * @throws javax.xml.stream.XMLStreamException
     */
    private void parseReferences() throws XMLStreamException {
        boolean continue_ = true;

        while (xmlr.hasNext() && continue_) {
            eventType = xmlr.next();
            if (XMLStreamConstants.START_ELEMENT == eventType
                    && xmlr.hasName()
                    && xmlr.getLocalName().equals("ref")) {
                String localId = xmlr.getAttributeValue(null, "id");
                if (citations.containsKey(localId)
                    && gotoTag("pub-id", 
                               (XMLStreamReader xr) -> xr.getAttributeValue(null, "pub-id-type").equals("pmid"))) {
                    /**
                     * Update the citations Replace the local reference by the appropriate
                     * PMID
                     */
                    String pmid = StringUtils.trim(xmlr.getElementText());
                    List<Pair<Integer, Integer>> tmp = citations.get(localId);
                    citations.remove(localId);
                    citations.put(pmid, tmp);
                } else {
                    citations.remove(localId);
                    logger.log(Level.WARN, "Could not find PMID for `" + localId + "'");
                }
            }
        }
    }

    /**
     * Extract the important stuff from the XML
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
                        case "body":
                            parseBody();
                            break;
                        case "ref-list":
                            parseReferences();
                            break;
                    }
                }

                /**
                 * Extract the PMID from the document
                 */
                if (!hasPMID() && xmlr.hasName()
                        && XMLStreamConstants.START_ELEMENT == eventType
                        && xmlr.getLocalName().equals("article-id")
                        && xmlr.getAttributeValue(null, "pub-id-type").equals("pmid")) {
                    PMID = StringUtils.trim(xmlr.getElementText());
                }

            }
        } catch (XMLStreamException ex) {
            logger.log(Level.FATAL, null, ex);
        }
    }

    /**
     * *************************************************************************
     * Utils
     * ************************************************************************
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
            if (XMLStreamConstants.END_ELEMENT == eventType
                    && xmlr.hasName()
                    && xmlr.getLocalName().equals(toTrack)) {
                count = count - 1;
            }
        }
    }

    /**
     * Add text after having trimmed it.
     *
     * @param text to add
     * @return a StringBuilder to continue the processing if needed
     */
    private StringBuilder addText(String text) {
        return this.text.append(StringUtils.trim(text));
    }

    /**
     * Go to the next specified tag in the current subtree.
     *
     * @param tag to go
     * @param op is a predicate
     * @throws XMLStreamException
     * @return true if the op returned true during the last run, false
     * otherwise
     */
    private boolean gotoTag(String tag, Predicate<XMLStreamReader> op) throws XMLStreamException {
        int depth = 1;
        boolean continue_ = true;
        while (xmlr.hasNext()
               && continue_
               && depth > 0) {
            eventType = xmlr.next();
            continue_ = !(XMLStreamConstants.START_ELEMENT == eventType
                    && xmlr.hasName()
                    && xmlr.getLocalName().equals(tag)
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

    private boolean gotoTag(String tag) throws XMLStreamException {
        return gotoTag(tag, null);
    }

    /**
     * Add a citation at the given position
     * @param citation to add
     * @param start of the citation
     * @param end of the citation
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
     * *************************************************************************
     * Getters
     * ************************************************************************
     */
    /**
     * Return the filename to parse or parsed.
     *
     * @return the filename to parse or parsed.
     */
    public String getFilename() {
        return filename;
    }

    public String getText() {
        return text.toString();
    }

    public Map<String, List<Pair<Integer, Integer>>> getCitations() {
        return citations;
    }

    public String getPMID() {
        return PMID;
    }

    public boolean hasPMID() {
        return !PMID.isEmpty();
    }
}

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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import jgreg.internship.nii.types.ID;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.Feature;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.TypeSystem;
import org.apache.uima.cas.impl.XmiCasSerializer;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.TypeSystemUtil;
import org.xml.sax.SAXException;

/**
 * Dump all CAS as XMI in {@link #OUTPUT_DIRECTORY}. The complete TypeSystem is
 * also dumped in {@link #OUTPUT_DIRECTORY}/ts.xml.
 *
 * @author Grégoire Jadi
 */
public class XMIWriter extends
        org.apache.uima.fit.component.JCasAnnotator_ImplBase {

    /** The Constant logger. */
    private static final Logger logger = Logger.getLogger(XMIWriter.class
            .getCanonicalName());

    /**
     * The directory in which the XMI and XML are written.
     */
    public static final String OUTPUT_DIRECTORY = "outputDirName";
    @ConfigurationParameter(name = OUTPUT_DIRECTORY, mandatory = true)
    private String outputDirName;

    /** The output dir. */
    private File outputDir;

    /**
     * Empty the directory before writing to it or not.
     */
    public static final String CLEAR_DIRECTORY = "clearDirectory";
    @ConfigurationParameter(name = CLEAR_DIRECTORY, mandatory = false, defaultValue = "false")
    private boolean clearDirectory;

    /** The name of the Type annotation that contains the name of a CAS. */
    public static final String NAME_TYPE = "nameTYPE";
    @ConfigurationParameter(name = NAME_TYPE, mandatory = true)
    private String nameType;

    /** The name t. */
    private Type nameT;

    /** The name of the Feature that contains the name of a CAS. */
    public static final String NAME_FEATURE = "nameFeature";

    /** The name feature. */
    @ConfigurationParameter(name = NAME_FEATURE, mandatory = true)
    private String nameFeature;

    /** The name f. */
    private Feature nameF;

    /*
     * (non-Javadoc)
     *
     * @see
     * org.apache.uima.fit.component.JCasAnnotator_ImplBase#initialize(org.apache
     * .uima.UimaContext)
     */
    @Override
    public void initialize(UimaContext context)
            throws ResourceInitializationException {
        super.initialize(context);

        outputDir = new File(outputDirName);

        if (clearDirectory && outputDir.exists()) {
            try {
                FileUtils.cleanDirectory(outputDir);
            } catch (IOException ex) {
                // It doesn't really matter if we couldn't cleanup the
                // directory, but report the error just in case.
                logger.fatal(ex);
            }
        }
        outputDir.mkdirs();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.apache.uima.analysis_component.JCasAnnotator_ImplBase#process(org
     * .apache.uima.jcas.JCas)
     */
    @Override
    public void process(JCas jCas) throws AnalysisEngineProcessException {
        if (!ts_dumped) {
            writeTs(jCas);
        }
        String pmid = JCasUtil.selectSingle(jCas, ID.class).getPMID();

        File outputFile = new File(outputDir, pmid + ".xmi");

        logger.info("Dumping to `" + outputFile.getAbsolutePath() + "'...");
        try {
            OutputStream outputStream = new FileOutputStream(outputFile);
            XmiCasSerializer.serialize(jCas.getCas(), outputStream);
        } catch (SAXException | IOException ex) {
            logger.fatal("Couldn't dump `" + outputFile + "''", ex);
        }
    }

    /**
     * Dump the Type System to <outputDir>/ts.xml.
     *
     * If everything was cool and fine we would not need this function because
     * jcasgen.sh would merge the TypeSystem for us. Unfortunately it does not
     * seem to be possible to do it without setting up the UIMA+Eclipse combo.
     *
     * Since the XML version of the Type System is used by the annotationViewer
     * with the XMI, this is a good place to put it here.
     *
     * @param jcas
     *            the jcas
     * @throws AnalysisEngineProcessException
     *             if the TypeSystem can not be converted to XML or if
     *             <outputDir>/ts.xml can not be written.
     */
    private void writeTs(JCas jcas) throws AnalysisEngineProcessException {
        try (OutputStream os = new FileOutputStream(new File(outputDir,
                "ts.xml"))) {
            TypeSystem ts = jcas.getTypeSystem();
            TypeSystemUtil.typeSystem2TypeSystemDescription(ts).toXML(os);
            nameT = ts.getType(nameType);
            nameF = nameT.getFeatureByBaseName(nameFeature);
        } catch (IOException | SAXException ex) {
            throw new AnalysisEngineProcessException(ex);
        }
        ts_dumped = true;
    }

    /**
     * A flag to determine whether or not the TypeSystem has been dumped. @see
     * writeTs
     */
    private boolean ts_dumped = false;
}

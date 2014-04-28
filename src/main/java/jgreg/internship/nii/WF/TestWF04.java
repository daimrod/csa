package jgreg.internship.nii.WF;

import java.io.File;
import jgreg.internship.nii.RES.MatcherRES;
import org.apache.log4j.Logger;
import static org.apache.uima.fit.factory.ExternalResourceFactory.createExternalResourceDescription;
import org.apache.uima.resource.ExternalResourceDescription;

/**
 * This simple workflow initialize the MatcherRES resource
 */
public class TestWF04 {
    private static final Logger logger = Logger.getLogger(TestWF04.class.getCanonicalName());
    
    public static void main(String[] args) throws Exception {
        ExternalResourceDescription matcherRES =
            createExternalResourceDescription(MatcherRES.class, "");
        
        logger.info("done!");
    }
}

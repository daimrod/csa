
/* First created by JCasGen Wed Apr 23 17:36:01 JST 2014 */
package jgreg.internship.nii.types;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.FSGenerator;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.Feature;
import org.apache.uima.jcas.tcas.Annotation_Type;

/** Mark a Citation Context
 * Updated by JCasGen Wed Apr 23 17:36:01 JST 2014
 * @generated */
public class CitationContext_Type extends Annotation_Type {
  /** @generated 
   * @return the generator for this type
   */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (CitationContext_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = CitationContext_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new CitationContext(addr, CitationContext_Type.this);
  			   CitationContext_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new CitationContext(addr, CitationContext_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = CitationContext.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("jgreg.internship.nii.types.CitationContext");
 
  /** @generated */
  final Feature casFeat_PMID;
  /** @generated */
  final int     casFeatCode_PMID;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getPMID(int addr) {
        if (featOkTst && casFeat_PMID == null)
      jcas.throwFeatMissing("PMID", "jgreg.internship.nii.types.CitationContext");
    return ll_cas.ll_getStringValue(addr, casFeatCode_PMID);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setPMID(int addr, String v) {
        if (featOkTst && casFeat_PMID == null)
      jcas.throwFeatMissing("PMID", "jgreg.internship.nii.types.CitationContext");
    ll_cas.ll_setStringValue(addr, casFeatCode_PMID, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public CitationContext_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_PMID = jcas.getRequiredFeatureDE(casType, "PMID", "uima.cas.String", featOkTst);
    casFeatCode_PMID  = (null == casFeat_PMID) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_PMID).getCode();

  }
}



    
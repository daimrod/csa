
/* First created by JCasGen Thu Dec 18 14:16:20 CET 2014 */
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

/** A citation.
 * Updated by JCasGen Thu Dec 18 14:16:20 CET 2014
 * @generated */
public class Citation_Type extends Annotation_Type {
  /** @generated */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (Citation_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = Citation_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new Citation(addr, Citation_Type.this);
  			   Citation_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new Citation(addr, Citation_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = Citation.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("jgreg.internship.nii.types.Citation");
 
  /** @generated */
  final Feature casFeat_PMID;
  /** @generated */
  final int     casFeatCode_PMID;
  /** @generated */ 
  public String getPMID(int addr) {
        if (featOkTst && casFeat_PMID == null)
      jcas.throwFeatMissing("PMID", "jgreg.internship.nii.types.Citation");
    return ll_cas.ll_getStringValue(addr, casFeatCode_PMID);
  }
  /** @generated */    
  public void setPMID(int addr, String v) {
        if (featOkTst && casFeat_PMID == null)
      jcas.throwFeatMissing("PMID", "jgreg.internship.nii.types.Citation");
    ll_cas.ll_setStringValue(addr, casFeatCode_PMID, v);}
    
  
 
  /** @generated */
  final Feature casFeat_RID;
  /** @generated */
  final int     casFeatCode_RID;
  /** @generated */ 
  public String getRID(int addr) {
        if (featOkTst && casFeat_RID == null)
      jcas.throwFeatMissing("RID", "jgreg.internship.nii.types.Citation");
    return ll_cas.ll_getStringValue(addr, casFeatCode_RID);
  }
  /** @generated */    
  public void setRID(int addr, String v) {
        if (featOkTst && casFeat_RID == null)
      jcas.throwFeatMissing("RID", "jgreg.internship.nii.types.Citation");
    ll_cas.ll_setStringValue(addr, casFeatCode_RID, v);}
    
  
 
  /** @generated */
  final Feature casFeat_text;
  /** @generated */
  final int     casFeatCode_text;
  /** @generated */ 
  public String getText(int addr) {
        if (featOkTst && casFeat_text == null)
      jcas.throwFeatMissing("text", "jgreg.internship.nii.types.Citation");
    return ll_cas.ll_getStringValue(addr, casFeatCode_text);
  }
  /** @generated */    
  public void setText(int addr, String v) {
        if (featOkTst && casFeat_text == null)
      jcas.throwFeatMissing("text", "jgreg.internship.nii.types.Citation");
    ll_cas.ll_setStringValue(addr, casFeatCode_text, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	* @generated */
  public Citation_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_PMID = jcas.getRequiredFeatureDE(casType, "PMID", "uima.cas.String", featOkTst);
    casFeatCode_PMID  = (null == casFeat_PMID) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_PMID).getCode();

 
    casFeat_RID = jcas.getRequiredFeatureDE(casType, "RID", "uima.cas.String", featOkTst);
    casFeatCode_RID  = (null == casFeat_RID) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_RID).getCode();

 
    casFeat_text = jcas.getRequiredFeatureDE(casType, "text", "uima.cas.String", featOkTst);
    casFeatCode_text  = (null == casFeat_text) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_text).getCode();

  }
}



    
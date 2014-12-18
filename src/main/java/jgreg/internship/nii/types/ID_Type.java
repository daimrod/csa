
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

/** ID(s) of the document.
 * Updated by JCasGen Thu Dec 18 14:16:20 CET 2014
 * @generated */
public class ID_Type extends Annotation_Type {
  /** @generated */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (ID_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = ID_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new ID(addr, ID_Type.this);
  			   ID_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new ID(addr, ID_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = ID.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("jgreg.internship.nii.types.ID");
 
  /** @generated */
  final Feature casFeat_PMID;
  /** @generated */
  final int     casFeatCode_PMID;
  /** @generated */ 
  public String getPMID(int addr) {
        if (featOkTst && casFeat_PMID == null)
      jcas.throwFeatMissing("PMID", "jgreg.internship.nii.types.ID");
    return ll_cas.ll_getStringValue(addr, casFeatCode_PMID);
  }
  /** @generated */    
  public void setPMID(int addr, String v) {
        if (featOkTst && casFeat_PMID == null)
      jcas.throwFeatMissing("PMID", "jgreg.internship.nii.types.ID");
    ll_cas.ll_setStringValue(addr, casFeatCode_PMID, v);}
    
  
 
  /** @generated */
  final Feature casFeat_year;
  /** @generated */
  final int     casFeatCode_year;
  /** @generated */ 
  public long getYear(int addr) {
        if (featOkTst && casFeat_year == null)
      jcas.throwFeatMissing("year", "jgreg.internship.nii.types.ID");
    return ll_cas.ll_getLongValue(addr, casFeatCode_year);
  }
  /** @generated */    
  public void setYear(int addr, long v) {
        if (featOkTst && casFeat_year == null)
      jcas.throwFeatMissing("year", "jgreg.internship.nii.types.ID");
    ll_cas.ll_setLongValue(addr, casFeatCode_year, v);}
    
  
 
  /** @generated */
  final Feature casFeat_title;
  /** @generated */
  final int     casFeatCode_title;
  /** @generated */ 
  public String getTitle(int addr) {
        if (featOkTst && casFeat_title == null)
      jcas.throwFeatMissing("title", "jgreg.internship.nii.types.ID");
    return ll_cas.ll_getStringValue(addr, casFeatCode_title);
  }
  /** @generated */    
  public void setTitle(int addr, String v) {
        if (featOkTst && casFeat_title == null)
      jcas.throwFeatMissing("title", "jgreg.internship.nii.types.ID");
    ll_cas.ll_setStringValue(addr, casFeatCode_title, v);}
    
  
 
  /** @generated */
  final Feature casFeat_Filename;
  /** @generated */
  final int     casFeatCode_Filename;
  /** @generated */ 
  public String getFilename(int addr) {
        if (featOkTst && casFeat_Filename == null)
      jcas.throwFeatMissing("Filename", "jgreg.internship.nii.types.ID");
    return ll_cas.ll_getStringValue(addr, casFeatCode_Filename);
  }
  /** @generated */    
  public void setFilename(int addr, String v) {
        if (featOkTst && casFeat_Filename == null)
      jcas.throwFeatMissing("Filename", "jgreg.internship.nii.types.ID");
    ll_cas.ll_setStringValue(addr, casFeatCode_Filename, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	* @generated */
  public ID_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_PMID = jcas.getRequiredFeatureDE(casType, "PMID", "uima.cas.String", featOkTst);
    casFeatCode_PMID  = (null == casFeat_PMID) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_PMID).getCode();

 
    casFeat_year = jcas.getRequiredFeatureDE(casType, "year", "uima.cas.Long", featOkTst);
    casFeatCode_year  = (null == casFeat_year) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_year).getCode();

 
    casFeat_title = jcas.getRequiredFeatureDE(casType, "title", "uima.cas.String", featOkTst);
    casFeatCode_title  = (null == casFeat_title) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_title).getCode();

 
    casFeat_Filename = jcas.getRequiredFeatureDE(casType, "Filename", "uima.cas.String", featOkTst);
    casFeatCode_Filename  = (null == casFeat_Filename) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_Filename).getCode();

  }
}



    
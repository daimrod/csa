
/* First created by JCasGen Tue Sep 23 15:15:42 CEST 2014 */
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
 * Updated by JCasGen Tue Sep 23 15:15:42 CEST 2014
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
  final Feature casFeat_ID;
  /** @generated */
  final int     casFeatCode_ID;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public long getID(int addr) {
        if (featOkTst && casFeat_ID == null)
      jcas.throwFeatMissing("ID", "jgreg.internship.nii.types.CitationContext");
    return ll_cas.ll_getLongValue(addr, casFeatCode_ID);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setID(int addr, long v) {
        if (featOkTst && casFeat_ID == null)
      jcas.throwFeatMissing("ID", "jgreg.internship.nii.types.CitationContext");
    ll_cas.ll_setLongValue(addr, casFeatCode_ID, v);}
    
  
 
  /** @generated */
  final Feature casFeat_PMIDS;
  /** @generated */
  final int     casFeatCode_PMIDS;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public int getPMIDS(int addr) {
        if (featOkTst && casFeat_PMIDS == null)
      jcas.throwFeatMissing("PMIDS", "jgreg.internship.nii.types.CitationContext");
    return ll_cas.ll_getRefValue(addr, casFeatCode_PMIDS);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setPMIDS(int addr, int v) {
        if (featOkTst && casFeat_PMIDS == null)
      jcas.throwFeatMissing("PMIDS", "jgreg.internship.nii.types.CitationContext");
    ll_cas.ll_setRefValue(addr, casFeatCode_PMIDS, v);}
    
   /** @generated
   * @param addr low level Feature Structure reference
   * @param i index of item in the array
   * @return value at index i in the array 
   */
  public int getPMIDS(int addr, int i) {
        if (featOkTst && casFeat_PMIDS == null)
      jcas.throwFeatMissing("PMIDS", "jgreg.internship.nii.types.CitationContext");
    if (lowLevelTypeChecks)
      return ll_cas.ll_getRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_PMIDS), i, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_PMIDS), i);
	return ll_cas.ll_getRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_PMIDS), i);
  }
   
  /** @generated
   * @param addr low level Feature Structure reference
   * @param i index of item in the array
   * @param v value to set
   */ 
  public void setPMIDS(int addr, int i, int v) {
        if (featOkTst && casFeat_PMIDS == null)
      jcas.throwFeatMissing("PMIDS", "jgreg.internship.nii.types.CitationContext");
    if (lowLevelTypeChecks)
      ll_cas.ll_setRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_PMIDS), i, v, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_PMIDS), i);
    ll_cas.ll_setRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_PMIDS), i, v);
  }
 



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public CitationContext_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_ID = jcas.getRequiredFeatureDE(casType, "ID", "uima.cas.Long", featOkTst);
    casFeatCode_ID  = (null == casFeat_ID) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_ID).getCode();

 
    casFeat_PMIDS = jcas.getRequiredFeatureDE(casType, "PMIDS", "uima.cas.FSArray", featOkTst);
    casFeatCode_PMIDS  = (null == casFeat_PMIDS) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_PMIDS).getCode();

  }
}



    
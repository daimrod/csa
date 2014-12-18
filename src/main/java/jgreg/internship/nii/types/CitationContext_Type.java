
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

/** Mark a Citation Context
 * Updated by JCasGen Thu Dec 18 14:16:20 CET 2014
 * @generated */
public class CitationContext_Type extends Annotation_Type {
  /** @generated */
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
  /** @generated */ 
  public long getID(int addr) {
        if (featOkTst && casFeat_ID == null)
      jcas.throwFeatMissing("ID", "jgreg.internship.nii.types.CitationContext");
    return ll_cas.ll_getLongValue(addr, casFeatCode_ID);
  }
  /** @generated */    
  public void setID(int addr, long v) {
        if (featOkTst && casFeat_ID == null)
      jcas.throwFeatMissing("ID", "jgreg.internship.nii.types.CitationContext");
    ll_cas.ll_setLongValue(addr, casFeatCode_ID, v);}
    
  
 
  /** @generated */
  final Feature casFeat_cocited;
  /** @generated */
  final int     casFeatCode_cocited;
  /** @generated */ 
  public boolean getCocited(int addr) {
        if (featOkTst && casFeat_cocited == null)
      jcas.throwFeatMissing("cocited", "jgreg.internship.nii.types.CitationContext");
    return ll_cas.ll_getBooleanValue(addr, casFeatCode_cocited);
  }
  /** @generated */    
  public void setCocited(int addr, boolean v) {
        if (featOkTst && casFeat_cocited == null)
      jcas.throwFeatMissing("cocited", "jgreg.internship.nii.types.CitationContext");
    ll_cas.ll_setBooleanValue(addr, casFeatCode_cocited, v);}
    
  
 
  /** @generated */
  final Feature casFeat_Citations;
  /** @generated */
  final int     casFeatCode_Citations;
  /** @generated */ 
  public int getCitations(int addr) {
        if (featOkTst && casFeat_Citations == null)
      jcas.throwFeatMissing("Citations", "jgreg.internship.nii.types.CitationContext");
    return ll_cas.ll_getRefValue(addr, casFeatCode_Citations);
  }
  /** @generated */    
  public void setCitations(int addr, int v) {
        if (featOkTst && casFeat_Citations == null)
      jcas.throwFeatMissing("Citations", "jgreg.internship.nii.types.CitationContext");
    ll_cas.ll_setRefValue(addr, casFeatCode_Citations, v);}
    
   /** @generated */
  public int getCitations(int addr, int i) {
        if (featOkTst && casFeat_Citations == null)
      jcas.throwFeatMissing("Citations", "jgreg.internship.nii.types.CitationContext");
    if (lowLevelTypeChecks)
      return ll_cas.ll_getRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_Citations), i, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_Citations), i);
	return ll_cas.ll_getRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_Citations), i);
  }
   
  /** @generated */ 
  public void setCitations(int addr, int i, int v) {
        if (featOkTst && casFeat_Citations == null)
      jcas.throwFeatMissing("Citations", "jgreg.internship.nii.types.CitationContext");
    if (lowLevelTypeChecks)
      ll_cas.ll_setRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_Citations), i, v, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_Citations), i);
    ll_cas.ll_setRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_Citations), i, v);
  }
 



  /** initialize variables to correspond with Cas Type and Features
	* @generated */
  public CitationContext_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_ID = jcas.getRequiredFeatureDE(casType, "ID", "uima.cas.Long", featOkTst);
    casFeatCode_ID  = (null == casFeat_ID) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_ID).getCode();

 
    casFeat_cocited = jcas.getRequiredFeatureDE(casType, "cocited", "uima.cas.Boolean", featOkTst);
    casFeatCode_cocited  = (null == casFeat_cocited) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_cocited).getCode();

 
    casFeat_Citations = jcas.getRequiredFeatureDE(casType, "Citations", "uima.cas.FSArray", featOkTst);
    casFeatCode_Citations  = (null == casFeat_Citations) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_Citations).getCode();

  }
}



    
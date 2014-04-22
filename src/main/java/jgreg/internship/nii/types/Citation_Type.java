
/* First created by JCasGen Tue Apr 22 20:32:10 JST 2014 */
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
 * Updated by JCasGen Tue Apr 22 20:32:10 JST 2014
 * @generated */
public class Citation_Type extends Annotation_Type {
  /** @generated 
   * @return the generator for this type
   */
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
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getPMID(int addr) {
        if (featOkTst && casFeat_PMID == null)
      jcas.throwFeatMissing("PMID", "jgreg.internship.nii.types.Citation");
    return ll_cas.ll_getStringValue(addr, casFeatCode_PMID);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setPMID(int addr, String v) {
        if (featOkTst && casFeat_PMID == null)
      jcas.throwFeatMissing("PMID", "jgreg.internship.nii.types.Citation");
    ll_cas.ll_setStringValue(addr, casFeatCode_PMID, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public Citation_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_PMID = jcas.getRequiredFeatureDE(casType, "PMID", "uima.cas.String", featOkTst);
    casFeatCode_PMID  = (null == casFeat_PMID) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_PMID).getCode();

  }
}



    
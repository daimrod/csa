
/* First created by JCasGen Tue May 06 15:49:21 JST 2014 */
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

/** filename of the document.
 * Updated by JCasGen Tue May 06 15:49:21 JST 2014
 * @generated */
public class Filename_Type extends Annotation_Type {
  /** @generated 
   * @return the generator for this type
   */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (Filename_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = Filename_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new Filename(addr, Filename_Type.this);
  			   Filename_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new Filename(addr, Filename_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = Filename.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("jgreg.internship.nii.types.Filename");
 
  /** @generated */
  final Feature casFeat_Filename;
  /** @generated */
  final int     casFeatCode_Filename;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getFilename(int addr) {
        if (featOkTst && casFeat_Filename == null)
      jcas.throwFeatMissing("Filename", "jgreg.internship.nii.types.Filename");
    return ll_cas.ll_getStringValue(addr, casFeatCode_Filename);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setFilename(int addr, String v) {
        if (featOkTst && casFeat_Filename == null)
      jcas.throwFeatMissing("Filename", "jgreg.internship.nii.types.Filename");
    ll_cas.ll_setStringValue(addr, casFeatCode_Filename, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public Filename_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_Filename = jcas.getRequiredFeatureDE(casType, "Filename", "uima.cas.String", featOkTst);
    casFeatCode_Filename  = (null == casFeat_Filename) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_Filename).getCode();

  }
}



    
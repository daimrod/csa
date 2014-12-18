
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

/** 
 * Updated by JCasGen Thu Dec 18 14:16:20 CET 2014
 * @generated */
public class Sentiment_Type extends Annotation_Type {
  /** @generated */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (Sentiment_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = Sentiment_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new Sentiment(addr, Sentiment_Type.this);
  			   Sentiment_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new Sentiment(addr, Sentiment_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = Sentiment.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("jgreg.internship.nii.types.Sentiment");
 
  /** @generated */
  final Feature casFeat_score;
  /** @generated */
  final int     casFeatCode_score;
  /** @generated */ 
  public long getScore(int addr) {
        if (featOkTst && casFeat_score == null)
      jcas.throwFeatMissing("score", "jgreg.internship.nii.types.Sentiment");
    return ll_cas.ll_getLongValue(addr, casFeatCode_score);
  }
  /** @generated */    
  public void setScore(int addr, long v) {
        if (featOkTst && casFeat_score == null)
      jcas.throwFeatMissing("score", "jgreg.internship.nii.types.Sentiment");
    ll_cas.ll_setLongValue(addr, casFeatCode_score, v);}
    
  
 
  /** @generated */
  final Feature casFeat_name;
  /** @generated */
  final int     casFeatCode_name;
  /** @generated */ 
  public String getName(int addr) {
        if (featOkTst && casFeat_name == null)
      jcas.throwFeatMissing("name", "jgreg.internship.nii.types.Sentiment");
    return ll_cas.ll_getStringValue(addr, casFeatCode_name);
  }
  /** @generated */    
  public void setName(int addr, String v) {
        if (featOkTst && casFeat_name == null)
      jcas.throwFeatMissing("name", "jgreg.internship.nii.types.Sentiment");
    ll_cas.ll_setStringValue(addr, casFeatCode_name, v);}
    
  
 
  /** @generated */
  final Feature casFeat_correct;
  /** @generated */
  final int     casFeatCode_correct;
  /** @generated */ 
  public String getCorrect(int addr) {
        if (featOkTst && casFeat_correct == null)
      jcas.throwFeatMissing("correct", "jgreg.internship.nii.types.Sentiment");
    return ll_cas.ll_getStringValue(addr, casFeatCode_correct);
  }
  /** @generated */    
  public void setCorrect(int addr, String v) {
        if (featOkTst && casFeat_correct == null)
      jcas.throwFeatMissing("correct", "jgreg.internship.nii.types.Sentiment");
    ll_cas.ll_setStringValue(addr, casFeatCode_correct, v);}
    
  
 
  /** @generated */
  final Feature casFeat_valid;
  /** @generated */
  final int     casFeatCode_valid;
  /** @generated */ 
  public String getValid(int addr) {
        if (featOkTst && casFeat_valid == null)
      jcas.throwFeatMissing("valid", "jgreg.internship.nii.types.Sentiment");
    return ll_cas.ll_getStringValue(addr, casFeatCode_valid);
  }
  /** @generated */    
  public void setValid(int addr, String v) {
        if (featOkTst && casFeat_valid == null)
      jcas.throwFeatMissing("valid", "jgreg.internship.nii.types.Sentiment");
    ll_cas.ll_setStringValue(addr, casFeatCode_valid, v);}
    
  
 
  /** @generated */
  final Feature casFeat_context;
  /** @generated */
  final int     casFeatCode_context;
  /** @generated */ 
  public int getContext(int addr) {
        if (featOkTst && casFeat_context == null)
      jcas.throwFeatMissing("context", "jgreg.internship.nii.types.Sentiment");
    return ll_cas.ll_getRefValue(addr, casFeatCode_context);
  }
  /** @generated */    
  public void setContext(int addr, int v) {
        if (featOkTst && casFeat_context == null)
      jcas.throwFeatMissing("context", "jgreg.internship.nii.types.Sentiment");
    ll_cas.ll_setRefValue(addr, casFeatCode_context, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	* @generated */
  public Sentiment_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_score = jcas.getRequiredFeatureDE(casType, "score", "uima.cas.Long", featOkTst);
    casFeatCode_score  = (null == casFeat_score) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_score).getCode();

 
    casFeat_name = jcas.getRequiredFeatureDE(casType, "name", "uima.cas.String", featOkTst);
    casFeatCode_name  = (null == casFeat_name) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_name).getCode();

 
    casFeat_correct = jcas.getRequiredFeatureDE(casType, "correct", "uima.cas.String", featOkTst);
    casFeatCode_correct  = (null == casFeat_correct) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_correct).getCode();

 
    casFeat_valid = jcas.getRequiredFeatureDE(casType, "valid", "uima.cas.String", featOkTst);
    casFeatCode_valid  = (null == casFeat_valid) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_valid).getCode();

 
    casFeat_context = jcas.getRequiredFeatureDE(casType, "context", "jgreg.internship.nii.types.CitationContext", featOkTst);
    casFeatCode_context  = (null == casFeat_context) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_context).getCode();

  }
}



    
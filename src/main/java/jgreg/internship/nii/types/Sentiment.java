

/* First created by JCasGen Thu Sep 25 16:41:50 CEST 2014 */
package jgreg.internship.nii.types;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** 
 * Updated by JCasGen Thu Sep 25 16:41:50 CEST 2014
 * XML source: src/main/resources/jgreg/internship/nii/types/CitationSentimentAnalysisTS.xml
 * @generated */
public class Sentiment extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(Sentiment.class);
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int type = typeIndexID;
  /** @generated
   * @return index of the type  
   */
  @Override
  public              int getTypeIndexID() {return typeIndexID;}
 
  /** Never called.  Disable default constructor
   * @generated */
  protected Sentiment() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public Sentiment(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public Sentiment(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public Sentiment(JCas jcas, int begin, int end) {
    super(jcas);
    setBegin(begin);
    setEnd(end);
    readObject();
  }   

  /** 
   * <!-- begin-user-doc -->
   * Write your own initialization here
   * <!-- end-user-doc -->
   *
   * @generated modifiable 
   */
  private void readObject() {/*default - does nothing empty block */}
     
 
    
  //*--------------*
  //* Feature: score

  /** getter for score - gets 
   * @generated
   * @return value of the feature 
   */
  public long getScore() {
    if (Sentiment_Type.featOkTst && ((Sentiment_Type)jcasType).casFeat_score == null)
      jcasType.jcas.throwFeatMissing("score", "jgreg.internship.nii.types.Sentiment");
    return jcasType.ll_cas.ll_getLongValue(addr, ((Sentiment_Type)jcasType).casFeatCode_score);}
    
  /** setter for score - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setScore(long v) {
    if (Sentiment_Type.featOkTst && ((Sentiment_Type)jcasType).casFeat_score == null)
      jcasType.jcas.throwFeatMissing("score", "jgreg.internship.nii.types.Sentiment");
    jcasType.ll_cas.ll_setLongValue(addr, ((Sentiment_Type)jcasType).casFeatCode_score, v);}    
   
    
  //*--------------*
  //* Feature: name

  /** getter for name - gets 
   * @generated
   * @return value of the feature 
   */
  public String getName() {
    if (Sentiment_Type.featOkTst && ((Sentiment_Type)jcasType).casFeat_name == null)
      jcasType.jcas.throwFeatMissing("name", "jgreg.internship.nii.types.Sentiment");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Sentiment_Type)jcasType).casFeatCode_name);}
    
  /** setter for name - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setName(String v) {
    if (Sentiment_Type.featOkTst && ((Sentiment_Type)jcasType).casFeat_name == null)
      jcasType.jcas.throwFeatMissing("name", "jgreg.internship.nii.types.Sentiment");
    jcasType.ll_cas.ll_setStringValue(addr, ((Sentiment_Type)jcasType).casFeatCode_name, v);}    
   
    
  //*--------------*
  //* Feature: correct

  /** getter for correct - gets 
   * @generated
   * @return value of the feature 
   */
  public String getCorrect() {
    if (Sentiment_Type.featOkTst && ((Sentiment_Type)jcasType).casFeat_correct == null)
      jcasType.jcas.throwFeatMissing("correct", "jgreg.internship.nii.types.Sentiment");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Sentiment_Type)jcasType).casFeatCode_correct);}
    
  /** setter for correct - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setCorrect(String v) {
    if (Sentiment_Type.featOkTst && ((Sentiment_Type)jcasType).casFeat_correct == null)
      jcasType.jcas.throwFeatMissing("correct", "jgreg.internship.nii.types.Sentiment");
    jcasType.ll_cas.ll_setStringValue(addr, ((Sentiment_Type)jcasType).casFeatCode_correct, v);}    
   
    
  //*--------------*
  //* Feature: valid

  /** getter for valid - gets 
   * @generated
   * @return value of the feature 
   */
  public String getValid() {
    if (Sentiment_Type.featOkTst && ((Sentiment_Type)jcasType).casFeat_valid == null)
      jcasType.jcas.throwFeatMissing("valid", "jgreg.internship.nii.types.Sentiment");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Sentiment_Type)jcasType).casFeatCode_valid);}
    
  /** setter for valid - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setValid(String v) {
    if (Sentiment_Type.featOkTst && ((Sentiment_Type)jcasType).casFeat_valid == null)
      jcasType.jcas.throwFeatMissing("valid", "jgreg.internship.nii.types.Sentiment");
    jcasType.ll_cas.ll_setStringValue(addr, ((Sentiment_Type)jcasType).casFeatCode_valid, v);}    
   
    
  //*--------------*
  //* Feature: context

  /** getter for context - gets 
   * @generated
   * @return value of the feature 
   */
  public CitationContext getContext() {
    if (Sentiment_Type.featOkTst && ((Sentiment_Type)jcasType).casFeat_context == null)
      jcasType.jcas.throwFeatMissing("context", "jgreg.internship.nii.types.Sentiment");
    return (CitationContext)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((Sentiment_Type)jcasType).casFeatCode_context)));}
    
  /** setter for context - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setContext(CitationContext v) {
    if (Sentiment_Type.featOkTst && ((Sentiment_Type)jcasType).casFeat_context == null)
      jcasType.jcas.throwFeatMissing("context", "jgreg.internship.nii.types.Sentiment");
    jcasType.ll_cas.ll_setRefValue(addr, ((Sentiment_Type)jcasType).casFeatCode_context, jcasType.ll_cas.ll_getFSRef(v));}    
  }

    
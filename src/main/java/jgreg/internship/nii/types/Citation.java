

/* First created by JCasGen Thu Dec 18 14:16:20 CET 2014 */
package jgreg.internship.nii.types;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** A citation.
 * Updated by JCasGen Thu Dec 18 14:16:20 CET 2014
 * XML source: src/main/resources/jgreg/internship/nii/types/CitationSentimentAnalysisTS.xml
 * @generated */
public class Citation extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(Citation.class);
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int type = typeIndexID;
  /** @generated  */
  @Override
  public              int getTypeIndexID() {return typeIndexID;}
 
  /** Never called.  Disable default constructor
   * @generated */
  protected Citation() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated */
  public Citation(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public Citation(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated */  
  public Citation(JCas jcas, int begin, int end) {
    super(jcas);
    setBegin(begin);
    setEnd(end);
    readObject();
  }   

  /** <!-- begin-user-doc -->
    * Write your own initialization here
    * <!-- end-user-doc -->
  @generated modifiable */
  private void readObject() {/*default - does nothing empty block */}
     
 
    
  //*--------------*
  //* Feature: PMID

  /** getter for PMID - gets 
   * @generated */
  public String getPMID() {
    if (Citation_Type.featOkTst && ((Citation_Type)jcasType).casFeat_PMID == null)
      jcasType.jcas.throwFeatMissing("PMID", "jgreg.internship.nii.types.Citation");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Citation_Type)jcasType).casFeatCode_PMID);}
    
  /** setter for PMID - sets  
   * @generated */
  public void setPMID(String v) {
    if (Citation_Type.featOkTst && ((Citation_Type)jcasType).casFeat_PMID == null)
      jcasType.jcas.throwFeatMissing("PMID", "jgreg.internship.nii.types.Citation");
    jcasType.ll_cas.ll_setStringValue(addr, ((Citation_Type)jcasType).casFeatCode_PMID, v);}    
   
    
  //*--------------*
  //* Feature: RID

  /** getter for RID - gets 
   * @generated */
  public String getRID() {
    if (Citation_Type.featOkTst && ((Citation_Type)jcasType).casFeat_RID == null)
      jcasType.jcas.throwFeatMissing("RID", "jgreg.internship.nii.types.Citation");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Citation_Type)jcasType).casFeatCode_RID);}
    
  /** setter for RID - sets  
   * @generated */
  public void setRID(String v) {
    if (Citation_Type.featOkTst && ((Citation_Type)jcasType).casFeat_RID == null)
      jcasType.jcas.throwFeatMissing("RID", "jgreg.internship.nii.types.Citation");
    jcasType.ll_cas.ll_setStringValue(addr, ((Citation_Type)jcasType).casFeatCode_RID, v);}    
   
    
  //*--------------*
  //* Feature: text

  /** getter for text - gets 
   * @generated */
  public String getText() {
    if (Citation_Type.featOkTst && ((Citation_Type)jcasType).casFeat_text == null)
      jcasType.jcas.throwFeatMissing("text", "jgreg.internship.nii.types.Citation");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Citation_Type)jcasType).casFeatCode_text);}
    
  /** setter for text - sets  
   * @generated */
  public void setText(String v) {
    if (Citation_Type.featOkTst && ((Citation_Type)jcasType).casFeat_text == null)
      jcasType.jcas.throwFeatMissing("text", "jgreg.internship.nii.types.Citation");
    jcasType.ll_cas.ll_setStringValue(addr, ((Citation_Type)jcasType).casFeatCode_text, v);}    
  }

    
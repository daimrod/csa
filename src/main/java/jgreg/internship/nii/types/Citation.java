

/* First created by JCasGen Fri Apr 11 13:47:21 JST 2014 */
package jgreg.internship.nii.types;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** 
 * Updated by JCasGen Fri Apr 11 13:47:21 JST 2014
 * XML source: CitationSentimentAnalysisTS.xml
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
  /** @generated
   * @return index of the type  
   */
  @Override
  public              int getTypeIndexID() {return typeIndexID;}
 
  /** Never called.  Disable default constructor
   * @generated */
  protected Citation() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public Citation(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public Citation(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public Citation(JCas jcas, int begin, int end) {
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
  //* Feature: pmid

  /** getter for pmid - gets 
   * @generated
   * @return value of the feature 
   */
  public String getPmid() {
    if (Citation_Type.featOkTst && ((Citation_Type)jcasType).casFeat_pmid == null)
      jcasType.jcas.throwFeatMissing("pmid", "jgreg.internship.nii.types.Citation");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Citation_Type)jcasType).casFeatCode_pmid);}
    
  /** setter for pmid - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setPmid(String v) {
    if (Citation_Type.featOkTst && ((Citation_Type)jcasType).casFeat_pmid == null)
      jcasType.jcas.throwFeatMissing("pmid", "jgreg.internship.nii.types.Citation");
    jcasType.ll_cas.ll_setStringValue(addr, ((Citation_Type)jcasType).casFeatCode_pmid, v);}    
  }

    
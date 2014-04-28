

/* First created by JCasGen Mon Apr 28 17:31:59 JST 2014 */
package jgreg.internship.nii.types;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** Mark a Citation Context
 * Updated by JCasGen Mon Apr 28 17:31:59 JST 2014
 * XML source: src/main/resources/jgreg/internship/nii/types/CitationSentimentAnalysisTS.xml
 * @generated */
public class CitationContext extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(CitationContext.class);
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
  protected CitationContext() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public CitationContext(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public CitationContext(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public CitationContext(JCas jcas, int begin, int end) {
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
  //* Feature: PMID

  /** getter for PMID - gets 
   * @generated
   * @return value of the feature 
   */
  public String getPMID() {
    if (CitationContext_Type.featOkTst && ((CitationContext_Type)jcasType).casFeat_PMID == null)
      jcasType.jcas.throwFeatMissing("PMID", "jgreg.internship.nii.types.CitationContext");
    return jcasType.ll_cas.ll_getStringValue(addr, ((CitationContext_Type)jcasType).casFeatCode_PMID);}
    
  /** setter for PMID - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setPMID(String v) {
    if (CitationContext_Type.featOkTst && ((CitationContext_Type)jcasType).casFeat_PMID == null)
      jcasType.jcas.throwFeatMissing("PMID", "jgreg.internship.nii.types.CitationContext");
    jcasType.ll_cas.ll_setStringValue(addr, ((CitationContext_Type)jcasType).casFeatCode_PMID, v);}    
  }

    
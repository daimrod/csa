

/* First created by JCasGen Mon Jul 14 16:48:56 JST 2014 */
package jgreg.internship.nii.types;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.tcas.Annotation;


/** Mark a Citation Context
 * Updated by JCasGen Mon Jul 14 16:48:56 JST 2014
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
  //* Feature: ID

  /** getter for ID - gets Context ID
   * @generated
   * @return value of the feature 
   */
  public long getID() {
    if (CitationContext_Type.featOkTst && ((CitationContext_Type)jcasType).casFeat_ID == null)
      jcasType.jcas.throwFeatMissing("ID", "jgreg.internship.nii.types.CitationContext");
    return jcasType.ll_cas.ll_getLongValue(addr, ((CitationContext_Type)jcasType).casFeatCode_ID);}
    
  /** setter for ID - sets Context ID 
   * @generated
   * @param v value to set into the feature 
   */
  public void setID(long v) {
    if (CitationContext_Type.featOkTst && ((CitationContext_Type)jcasType).casFeat_ID == null)
      jcasType.jcas.throwFeatMissing("ID", "jgreg.internship.nii.types.CitationContext");
    jcasType.ll_cas.ll_setLongValue(addr, ((CitationContext_Type)jcasType).casFeatCode_ID, v);}    
   
    
  //*--------------*
  //* Feature: PMIDS

  /** getter for PMIDS - gets The PMIDS covered by the CitationContext
   * @generated
   * @return value of the feature 
   */
  public FSArray getPMIDS() {
    if (CitationContext_Type.featOkTst && ((CitationContext_Type)jcasType).casFeat_PMIDS == null)
      jcasType.jcas.throwFeatMissing("PMIDS", "jgreg.internship.nii.types.CitationContext");
    return (FSArray)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((CitationContext_Type)jcasType).casFeatCode_PMIDS)));}
    
  /** setter for PMIDS - sets The PMIDS covered by the CitationContext 
   * @generated
   * @param v value to set into the feature 
   */
  public void setPMIDS(FSArray v) {
    if (CitationContext_Type.featOkTst && ((CitationContext_Type)jcasType).casFeat_PMIDS == null)
      jcasType.jcas.throwFeatMissing("PMIDS", "jgreg.internship.nii.types.CitationContext");
    jcasType.ll_cas.ll_setRefValue(addr, ((CitationContext_Type)jcasType).casFeatCode_PMIDS, jcasType.ll_cas.ll_getFSRef(v));}    
    
  /** indexed getter for PMIDS - gets an indexed value - The PMIDS covered by the CitationContext
   * @generated
   * @param i index in the array to get
   * @return value of the element at index i 
   */
  public Citation getPMIDS(int i) {
    if (CitationContext_Type.featOkTst && ((CitationContext_Type)jcasType).casFeat_PMIDS == null)
      jcasType.jcas.throwFeatMissing("PMIDS", "jgreg.internship.nii.types.CitationContext");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((CitationContext_Type)jcasType).casFeatCode_PMIDS), i);
    return (Citation)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((CitationContext_Type)jcasType).casFeatCode_PMIDS), i)));}

  /** indexed setter for PMIDS - sets an indexed value - The PMIDS covered by the CitationContext
   * @generated
   * @param i index in the array to set
   * @param v value to set into the array 
   */
  public void setPMIDS(int i, Citation v) { 
    if (CitationContext_Type.featOkTst && ((CitationContext_Type)jcasType).casFeat_PMIDS == null)
      jcasType.jcas.throwFeatMissing("PMIDS", "jgreg.internship.nii.types.CitationContext");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((CitationContext_Type)jcasType).casFeatCode_PMIDS), i);
    jcasType.ll_cas.ll_setRefArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((CitationContext_Type)jcasType).casFeatCode_PMIDS), i, jcasType.ll_cas.ll_getFSRef(v));}
  }

    
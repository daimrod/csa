

/* First created by JCasGen Fri Sep 26 17:16:48 CEST 2014 */
package jgreg.internship.nii.types;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.tcas.Annotation;


/** Mark a Citation Context
 * Updated by JCasGen Fri Sep 26 17:16:48 CEST 2014
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
  //* Feature: cocited

  /** getter for cocited - gets 
   * @generated
   * @return value of the feature 
   */
  public boolean getCocited() {
    if (CitationContext_Type.featOkTst && ((CitationContext_Type)jcasType).casFeat_cocited == null)
      jcasType.jcas.throwFeatMissing("cocited", "jgreg.internship.nii.types.CitationContext");
    return jcasType.ll_cas.ll_getBooleanValue(addr, ((CitationContext_Type)jcasType).casFeatCode_cocited);}
    
  /** setter for cocited - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setCocited(boolean v) {
    if (CitationContext_Type.featOkTst && ((CitationContext_Type)jcasType).casFeat_cocited == null)
      jcasType.jcas.throwFeatMissing("cocited", "jgreg.internship.nii.types.CitationContext");
    jcasType.ll_cas.ll_setBooleanValue(addr, ((CitationContext_Type)jcasType).casFeatCode_cocited, v);}    
   
    
  //*--------------*
  //* Feature: Citations

  /** getter for Citations - gets The citations covered by the CitationContext
   * @generated
   * @return value of the feature 
   */
  public FSArray getCitations() {
    if (CitationContext_Type.featOkTst && ((CitationContext_Type)jcasType).casFeat_Citations == null)
      jcasType.jcas.throwFeatMissing("Citations", "jgreg.internship.nii.types.CitationContext");
    return (FSArray)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((CitationContext_Type)jcasType).casFeatCode_Citations)));}
    
  /** setter for Citations - sets The citations covered by the CitationContext 
   * @generated
   * @param v value to set into the feature 
   */
  public void setCitations(FSArray v) {
    if (CitationContext_Type.featOkTst && ((CitationContext_Type)jcasType).casFeat_Citations == null)
      jcasType.jcas.throwFeatMissing("Citations", "jgreg.internship.nii.types.CitationContext");
    jcasType.ll_cas.ll_setRefValue(addr, ((CitationContext_Type)jcasType).casFeatCode_Citations, jcasType.ll_cas.ll_getFSRef(v));}    
    
  /** indexed getter for Citations - gets an indexed value - The citations covered by the CitationContext
   * @generated
   * @param i index in the array to get
   * @return value of the element at index i 
   */
  public Citation getCitations(int i) {
    if (CitationContext_Type.featOkTst && ((CitationContext_Type)jcasType).casFeat_Citations == null)
      jcasType.jcas.throwFeatMissing("Citations", "jgreg.internship.nii.types.CitationContext");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((CitationContext_Type)jcasType).casFeatCode_Citations), i);
    return (Citation)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((CitationContext_Type)jcasType).casFeatCode_Citations), i)));}

  /** indexed setter for Citations - sets an indexed value - The citations covered by the CitationContext
   * @generated
   * @param i index in the array to set
   * @param v value to set into the array 
   */
  public void setCitations(int i, Citation v) { 
    if (CitationContext_Type.featOkTst && ((CitationContext_Type)jcasType).casFeat_Citations == null)
      jcasType.jcas.throwFeatMissing("Citations", "jgreg.internship.nii.types.CitationContext");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((CitationContext_Type)jcasType).casFeatCode_Citations), i);
    jcasType.ll_cas.ll_setRefArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((CitationContext_Type)jcasType).casFeatCode_Citations), i, jcasType.ll_cas.ll_getFSRef(v));}
  }

    
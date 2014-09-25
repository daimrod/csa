

/* First created by JCasGen Thu Sep 25 16:41:50 CEST 2014 */
package jgreg.internship.nii.types;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** ID(s) of the document.
 * Updated by JCasGen Thu Sep 25 16:41:50 CEST 2014
 * XML source: src/main/resources/jgreg/internship/nii/types/CitationSentimentAnalysisTS.xml
 * @generated */
public class ID extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(ID.class);
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
  protected ID() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public ID(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public ID(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public ID(JCas jcas, int begin, int end) {
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

  /** getter for PMID - gets The PMID of the document.
   * @generated
   * @return value of the feature 
   */
  public String getPMID() {
    if (ID_Type.featOkTst && ((ID_Type)jcasType).casFeat_PMID == null)
      jcasType.jcas.throwFeatMissing("PMID", "jgreg.internship.nii.types.ID");
    return jcasType.ll_cas.ll_getStringValue(addr, ((ID_Type)jcasType).casFeatCode_PMID);}
    
  /** setter for PMID - sets The PMID of the document. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setPMID(String v) {
    if (ID_Type.featOkTst && ((ID_Type)jcasType).casFeat_PMID == null)
      jcasType.jcas.throwFeatMissing("PMID", "jgreg.internship.nii.types.ID");
    jcasType.ll_cas.ll_setStringValue(addr, ((ID_Type)jcasType).casFeatCode_PMID, v);}    
   
    
  //*--------------*
  //* Feature: year

  /** getter for year - gets The Year of the document.
   * @generated
   * @return value of the feature 
   */
  public long getYear() {
    if (ID_Type.featOkTst && ((ID_Type)jcasType).casFeat_year == null)
      jcasType.jcas.throwFeatMissing("year", "jgreg.internship.nii.types.ID");
    return jcasType.ll_cas.ll_getLongValue(addr, ((ID_Type)jcasType).casFeatCode_year);}
    
  /** setter for year - sets The Year of the document. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setYear(long v) {
    if (ID_Type.featOkTst && ((ID_Type)jcasType).casFeat_year == null)
      jcasType.jcas.throwFeatMissing("year", "jgreg.internship.nii.types.ID");
    jcasType.ll_cas.ll_setLongValue(addr, ((ID_Type)jcasType).casFeatCode_year, v);}    
   
    
  //*--------------*
  //* Feature: title

  /** getter for title - gets The title of the document.
   * @generated
   * @return value of the feature 
   */
  public String getTitle() {
    if (ID_Type.featOkTst && ((ID_Type)jcasType).casFeat_title == null)
      jcasType.jcas.throwFeatMissing("title", "jgreg.internship.nii.types.ID");
    return jcasType.ll_cas.ll_getStringValue(addr, ((ID_Type)jcasType).casFeatCode_title);}
    
  /** setter for title - sets The title of the document. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setTitle(String v) {
    if (ID_Type.featOkTst && ((ID_Type)jcasType).casFeat_title == null)
      jcasType.jcas.throwFeatMissing("title", "jgreg.internship.nii.types.ID");
    jcasType.ll_cas.ll_setStringValue(addr, ((ID_Type)jcasType).casFeatCode_title, v);}    
  }

    
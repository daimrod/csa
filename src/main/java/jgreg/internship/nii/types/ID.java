

/* First created by JCasGen Thu Dec 18 14:16:20 CET 2014 */
package jgreg.internship.nii.types;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** ID(s) of the document.
 * Updated by JCasGen Thu Dec 18 14:16:20 CET 2014
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
  /** @generated  */
  @Override
  public              int getTypeIndexID() {return typeIndexID;}
 
  /** Never called.  Disable default constructor
   * @generated */
  protected ID() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated */
  public ID(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public ID(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated */  
  public ID(JCas jcas, int begin, int end) {
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

  /** getter for PMID - gets The PMID of the document.
   * @generated */
  public String getPMID() {
    if (ID_Type.featOkTst && ((ID_Type)jcasType).casFeat_PMID == null)
      jcasType.jcas.throwFeatMissing("PMID", "jgreg.internship.nii.types.ID");
    return jcasType.ll_cas.ll_getStringValue(addr, ((ID_Type)jcasType).casFeatCode_PMID);}
    
  /** setter for PMID - sets The PMID of the document. 
   * @generated */
  public void setPMID(String v) {
    if (ID_Type.featOkTst && ((ID_Type)jcasType).casFeat_PMID == null)
      jcasType.jcas.throwFeatMissing("PMID", "jgreg.internship.nii.types.ID");
    jcasType.ll_cas.ll_setStringValue(addr, ((ID_Type)jcasType).casFeatCode_PMID, v);}    
   
    
  //*--------------*
  //* Feature: year

  /** getter for year - gets The Year of the document.
   * @generated */
  public long getYear() {
    if (ID_Type.featOkTst && ((ID_Type)jcasType).casFeat_year == null)
      jcasType.jcas.throwFeatMissing("year", "jgreg.internship.nii.types.ID");
    return jcasType.ll_cas.ll_getLongValue(addr, ((ID_Type)jcasType).casFeatCode_year);}
    
  /** setter for year - sets The Year of the document. 
   * @generated */
  public void setYear(long v) {
    if (ID_Type.featOkTst && ((ID_Type)jcasType).casFeat_year == null)
      jcasType.jcas.throwFeatMissing("year", "jgreg.internship.nii.types.ID");
    jcasType.ll_cas.ll_setLongValue(addr, ((ID_Type)jcasType).casFeatCode_year, v);}    
   
    
  //*--------------*
  //* Feature: title

  /** getter for title - gets The title of the document.
   * @generated */
  public String getTitle() {
    if (ID_Type.featOkTst && ((ID_Type)jcasType).casFeat_title == null)
      jcasType.jcas.throwFeatMissing("title", "jgreg.internship.nii.types.ID");
    return jcasType.ll_cas.ll_getStringValue(addr, ((ID_Type)jcasType).casFeatCode_title);}
    
  /** setter for title - sets The title of the document. 
   * @generated */
  public void setTitle(String v) {
    if (ID_Type.featOkTst && ((ID_Type)jcasType).casFeat_title == null)
      jcasType.jcas.throwFeatMissing("title", "jgreg.internship.nii.types.ID");
    jcasType.ll_cas.ll_setStringValue(addr, ((ID_Type)jcasType).casFeatCode_title, v);}    
   
    
  //*--------------*
  //* Feature: Filename

  /** getter for Filename - gets The Filename of the document.
   * @generated */
  public String getFilename() {
    if (ID_Type.featOkTst && ((ID_Type)jcasType).casFeat_Filename == null)
      jcasType.jcas.throwFeatMissing("Filename", "jgreg.internship.nii.types.ID");
    return jcasType.ll_cas.ll_getStringValue(addr, ((ID_Type)jcasType).casFeatCode_Filename);}
    
  /** setter for Filename - sets The Filename of the document. 
   * @generated */
  public void setFilename(String v) {
    if (ID_Type.featOkTst && ((ID_Type)jcasType).casFeat_Filename == null)
      jcasType.jcas.throwFeatMissing("Filename", "jgreg.internship.nii.types.ID");
    jcasType.ll_cas.ll_setStringValue(addr, ((ID_Type)jcasType).casFeatCode_Filename, v);}    
  }

    
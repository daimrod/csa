

/* First created by JCasGen Tue May 06 16:07:26 JST 2014 */
package jgreg.internship.nii.types;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** filename of the document.
 * Updated by JCasGen Tue May 06 16:07:26 JST 2014
 * XML source: src/main/resources/jgreg/internship/nii/types/CitationSentimentAnalysisTS.xml
 * @generated */
public class Filename extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(Filename.class);
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
  protected Filename() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public Filename(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public Filename(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public Filename(JCas jcas, int begin, int end) {
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
  //* Feature: Filename

  /** getter for Filename - gets The Filename of the document.
   * @generated
   * @return value of the feature 
   */
  public String getFilename() {
    if (Filename_Type.featOkTst && ((Filename_Type)jcasType).casFeat_Filename == null)
      jcasType.jcas.throwFeatMissing("Filename", "jgreg.internship.nii.types.Filename");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Filename_Type)jcasType).casFeatCode_Filename);}
    
  /** setter for Filename - sets The Filename of the document. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setFilename(String v) {
    if (Filename_Type.featOkTst && ((Filename_Type)jcasType).casFeat_Filename == null)
      jcasType.jcas.throwFeatMissing("Filename", "jgreg.internship.nii.types.Filename");
    jcasType.ll_cas.ll_setStringValue(addr, ((Filename_Type)jcasType).casFeatCode_Filename, v);}    
  }

    


/* First created by JCasGen Thu Dec 18 14:16:20 CET 2014 */
package jgreg.internship.nii.types;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** Mark a Paragraph
 * Updated by JCasGen Thu Dec 18 14:16:20 CET 2014
 * XML source: src/main/resources/jgreg/internship/nii/types/CitationSentimentAnalysisTS.xml
 * @generated */
public class Paragraph extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(Paragraph.class);
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
  protected Paragraph() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated */
  public Paragraph(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public Paragraph(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated */  
  public Paragraph(JCas jcas, int begin, int end) {
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
     
}

    
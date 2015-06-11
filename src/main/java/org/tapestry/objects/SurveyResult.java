package org.tapestry.objects;

import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;

import org.oscarehr.util.XmlUtils;
import org.survey_component.data.QuestionAnswerPair;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
* Survey Result bean
* Represents the results of a survey.
* @author Darryl Hui
* @version 1.0
*/
public class SurveyResult implements Comparable<SurveyResult> {

	private int resultID;
	private int surveyID;
	private String surveyTitle;
	private int patientID;
	private String patientName;
	private boolean completed;
	private String startDate;
	private String editDate;
	private byte[] results;
	private int priority;
	private String description;
	private ArrayList<QuestionAnswerPair> questionAnswerPairs;
	private String strCompleted;
	private int volunteerID;
	private String volunteerName;
	
	public SurveyResult(){
		//Default constructor
	}
	
	public SurveyResult processMumpsResults(SurveyResult surveyResult) throws IOException, SAXException, ParserConfigurationException
	{
		ArrayList<QuestionAnswerPair> questionAnswerPairs = new ArrayList<QuestionAnswerPair>();		
		Document doc = XmlUtils.toDocument(surveyResult.getResults());
		
		Node rootNode = doc.getFirstChild();

		Node resultsNode = XmlUtils.getChildNode(rootNode, "Results");
		NodeList qaNodes = resultsNode.getChildNodes();
		for (int i = 0; i < qaNodes.getLength(); i++)
		{
			Node qaNode = qaNodes.item(i);

			// everything should be an IndivoSurveyQuestion
			if (!"IndivoSurveyQuestion".equals(qaNode.getNodeName())) continue;

			QuestionAnswerPair qaPair = new QuestionAnswerPair();
			qaPair.questionId = XmlUtils.getChildNodeTextContents(qaNode, "QuestionId");
			qaPair.questionText = XmlUtils.getChildNodeTextContents(qaNode, "QuestionText");

			Node answerNode = XmlUtils.getChildNode(qaNode, "QuestionAnswer");
			if (answerNode != null)
			{
				NodeList answerValueNodes = answerNode.getChildNodes();
				for (int j = 0; j < answerValueNodes.getLength(); j++)
				{
					Node answerValueNode = answerValueNodes.item(j);

					// everything should be an AnswerValue
					if (!"AnswerValue".equals(answerValueNode.getNodeName())) continue;

					qaPair.answers.add(answerValueNode.getTextContent());
				}
			}
			questionAnswerPairs.add(qaPair);
		}
		surveyResult.setQuestionAnswerPairs(questionAnswerPairs);
		return surveyResult;
	}

	//Accessor methods
	/**
	*@return result_ID The numeric ID of the results
	*/
	public int getResultID(){
		return resultID;
	}
	
	/**
	*@return survey_ID The numeric ID of the survey
	*/
	public int getSurveyID(){
		return surveyID;
	}
	
	/**
	 * @return status The status of the survey
	 */
	public String getSurveyTitle(){
		return surveyTitle;
	}
	
	/**
	*@return patient_ID The numeric ID of the patient
	*/
	public int getPatientID(){
		return patientID;
	}
	
	/**
	 * @return status The status of the survey
	 */
	public String getPatientName(){
		return patientName;
	}
	
	/**
	 * @return status The status of the survey
	 */
	public boolean isCompleted(){
		return completed;
	}

	/**
	*@return start_date The start date of the survey
	*/
	public String getStartDate(){
		return startDate;
	}
	
	/**
	 * @return edit_date The date the survey was completed
	 */
	public String getEditDate(){
		return editDate;
	}
	
	/**
	 * @param results The results of the survey
	 */
	public byte[] getResults(){
		return results;
	}
	
	/**
	 * @param results The results of the survey
	 */
	public ArrayList<QuestionAnswerPair> getQuestionAnswerPairs(){
		return questionAnswerPairs;
	}
	
	public int getPriority(){
		return priority;
	}
	
	public String getDescription(){
		return description;
	}

	//Mutator methods
	public void setResultID(int id){
		this.resultID = id;
	}
	
	/**
	 * @param id The new survey ID
	 */
	public void setSurveyID(int id){
		this.surveyID = id;
	}
	
	/**
	 * @param status The new status
	 */
	public void setSurveyTitle(String surveyTitle){
		this.surveyTitle = surveyTitle;
	}
	
	/**
	 * @param id The new patient ID
	 */
	public void setPatientID(int id){
		this.patientID = id;
	}
	
	/**
	 * @param status The new status
	 */
	public void setPatientName(String patientName){
		this.patientName = patientName;
	}
	
	/**
	 * @param status The new status
	 */
	public void setCompleted(boolean completed){
		this.completed = completed;
	}

	/**
	* @param startDate The new start date
	*/
	public void setStartDate(String startDate){
		this.startDate = startDate;
	}
	
	/**
	 * @param endDate The new completed date
	 */
	public void setEditDate(String editDate){
		this.editDate = editDate;
	}
	
	/**
	 * @param results The new results
	 */
	public void setResults(byte[] results){
		this.results = results;
	}
	
	/**
	 * @param questionAnswerPair The new Question Answer Pair Array
	 */
	public void setQuestionAnswerPairs(ArrayList<QuestionAnswerPair> questionAnswerPairs){
		this.questionAnswerPairs = questionAnswerPairs;
	}
	
	public void setPriority(int priority){
		this.priority = priority;
	}
	
	public void setDescription(String description){
		this.description = description;
	}
	
	/**
	 * Compares the object with another one by priority
	 * @o The object to be compared with
	 */
	@Override
	public int compareTo(SurveyResult o) throws ClassCastException {
		return o.getPriority() - this.priority;
	}
	

	public String getStrCompleted() {
		return strCompleted;
	}

	public void setStrCompleted(String strCompleted) {
		this.strCompleted = strCompleted;
	}
	
	public int getVolunteerID() {
		return volunteerID;
	}

	public void setVolunteerID(int volunteerID) {
		this.volunteerID = volunteerID;
	}

	public String getVolunteerName() {
		return volunteerName;
	}

	public void setVolunteerName(String volunteerName) {
		this.volunteerName = volunteerName;
	}

}
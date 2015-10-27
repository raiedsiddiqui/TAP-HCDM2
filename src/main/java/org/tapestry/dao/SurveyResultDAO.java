package org.tapestry.dao;

import java.util.List;

import org.tapestry.objects.SurveyResult;

/**
 * SurveyTemplateDAO
 * Defines DAO operations for the SurveyResult model.
 *
 * @author lxie 
 */
public interface SurveyResultDAO {
	/**
	* a List of Survey results for patient whose id is patientId
	* @param patientId
	* @return A List of SurveyResult objects
	*/
	public List<SurveyResult> getSurveysByPatientID(int patientId);
	
	/**
	* a List of completed Survey results for patient whose id is patientId
	* @param patientId
	* @return A List of SurveyResult objects
	*/
	public List<SurveyResult> getCompletedSurveysByPatientID(int patientId);	
	
	/**
	* a List of uncompleted Survey results for patient whose id is patientId
	* @param patientId
	* @return A List of SurveyResult objects
	*/
	public List<SurveyResult> getIncompleteSurveysByPatientID(int patientId);

	/**
	 * 
	 * A object of SurveyResult which result_Id is id
	 * @param id
	 * @return an object of SurveyResult
	 */
	public SurveyResult getSurveyResultByID(int id);
	
	/**
	 * Ordered by title ascending
	 * @return a list of SurveyResult objects
	 */
	public List<SurveyResult> getAllSurveyResults();
	
	/**
	 * 
	 * @param siteId
	 * @return a list of SurveyResult objects
	 */
	public List<SurveyResult> getAllSurveyResultsBySite(int siteId);
	
	/**
	 * Retrieves all survey results by survey Id	
	 * @param id survey ID
	 * @return a list of SurveyResult objects
	 */
	public List<SurveyResult> getAllSurveyResultsBySurveyId(int id);
	
	/**
	 * Retrieves all survey results by survey Id and patientId
	 * @param itemsToReturn 
	 * @param id survey ID
	 * @return a list of SurveyResult objects
	 */
	public SurveyResult getSurveyResultByPatientAndSurveyId(int patientId, int surveyId);
	
	/**
	 * Retrieves all completed survey results by survey Id and patientId
	 * @param itemsToReturn 
	 * @param id survey ID
	 * @return a list of SurveyResult objects
	 */
	public SurveyResult getCompletedSurveyResultByPatientAndSurveyTitle(int patientId, String surveyTitle);
	
	/**
	 * Uploads a survey template to the database
	 * @param st
	 */
	public String assignSurvey(SurveyResult sr);
	
	/**
	 * Delete a survey from the database
	 * @param id
	 */
	public void deleteSurvey(int id);
	
	/**
	 * Mark a survey as completed
	 * @param id
	 */
	public void markAsComplete(int id);
	
	/**
	 * Upload survey results
	 * @param id The ID of the survey result
	 * @param data The survey results
	 */
	public void updateSurveyResults(int id, byte[] data);
	
	/**
	 * Set start date of survey result
	 * @param id The ID of the survey result
	 */
	public void updateStartDate(int id) ;
	
	/**
	 * Count number of completed survey result for a patient
	 * @param patientId 
	 * @return number of completed survey result 
	 */
	public int countCompletedSurveys(int patientId);
	
	/**
	 * Count number of uncompleted survey result for a patient
	 * @param patientId 
	 * @return number of completed survey result 
	 */
	public int countUnCompletedSurveys(int patientId);
	
	/**
	 * Count number of survey result for a survey template
	 * @param surveyTemplateId 
	 * @return number of survey results 
	 */
	public int countSurveysBySurveyTemplateId(int surveyTemplateId);
	
	/**
	 * 
	 * @param surveyTemplateId
	 * @param siteId
	 * @return a list of survey results for selected survey and site
	 */
	public int countSurveysBySurveyTemplateIdAndSite(int surveyTemplateId, int siteId);
	
	/**
	 * 
	 * @param surveyId
	 * @param patientId
	 * @return true if survey is completed by patient
	 */
	public boolean hasCompleteSurvey(int surveyId, int patientId);
	
	/**
	 * Keep a copy of deleted survey result
	 * @param sr
	 */
	public void archiveSurveyResult(SurveyResult sr, String patient, String deletedBy);
	
	//============= UBC ================
	/**
	 * Ordered by title ascending
	 * @return a list of SurveyResult objects
	 */
	public List<SurveyResult> getAllVolunteerSurveyResults();
	
	/**
	 * Ordered by title ascending
	 * @return a list of SurveyResult objects
	 */
	public List<SurveyResult> getVolunteerSurveyResultsByVolunteerId(int volunteerId);
	
	/**
	 * Uploads a volunteer survey template to the database
	 * @param st
	 */
	public String assignVolunteerSurvey(SurveyResult sr);
	
	/**
	* a List of completed Survey results for patient whose id is patientId
	* @param volunteerId
	* @return A List of SurveyResult objects
	*/
	public List<SurveyResult> getCompletedVolunteerSurveys(int volunteerId);	
	
	/**
	* a List of uncompleted Survey results for patient whose id is patientId
	* @param volunteerId
	* @return A List of SurveyResult objects
	*/
	public List<SurveyResult> getIncompleteVolunteerSurveys(int volunteerId);
	
	/**
	 * 
	 * A object of SurveyResult which result_Id is id
	 * @param id
	 * @return an object of SurveyResult
	 */
	public SurveyResult getVolunteerSurveyResultByID(int id);
	
	/**
	 * Upload survey results
	 * @param id The ID of the survey result
	 * @param data The survey results
	 */
	public void updateVolunteerSurveyResults(int id, byte[] data);
	
	/**
	 * Delete a volunteer survey from the database
	 * @param id
	 */
	public void deleteVolunteerSurvey(int id);
	
	/**
	 * Mark a survey as completed
	 * @param id
	 */
	public void markAsCompleteForVolunteerSurvey(int id);
	
	/**
	 * 
	 * @param surveyResultId
	 * @return
	 */
	public int getSiteBySurveyResultID(int surveyResultId);
	
}

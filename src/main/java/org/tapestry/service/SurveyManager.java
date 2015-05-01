package org.tapestry.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tapestry.objects.SurveyResult;
import org.tapestry.objects.SurveyTemplate;

/**
 * service for Model SurveyResult, SurveyTemplate
 * @author lxie 
 */
@Service
public interface SurveyManager {
	/**
	* Returns the survey template with the given ID
	* @param id The ID of the survey template to find
	* @return A SurveyTemplate object representing the result
	*/
	@Transactional
	public SurveyTemplate getSurveyTemplateByID(int id);
	/**
	 * A list of Survey template
	 * @return a list of SurveyTemplate objects
	 */
	@Transactional
	public List<SurveyTemplate> getAllSurveyTemplates();
	
	/**
	 * 
	 * @param siteId
	 * @return A list of Survey templates grouped by site
	 */
	@Transactional
	public List<SurveyTemplate> getSurveyTemplatesBySite(int siteId); 
	
	/**
	 * check if there is any survey result associates with survey template(can not be deleted)
	 * @return
	 */
	@Transactional
	public List<SurveyTemplate> getSurveyTemplatesWithCanDelete(int siteId);
	/**
	 * A list of Survey template which title contains partialTitle
	 * @param partialTitle
	 * @return a list of SurveyTemplate objects
	 */
	@Transactional
	public List<SurveyTemplate> getSurveyTemplatesByPartialTitle(String partialTitle);
	
	/**
	 * 
	 * @param title
	 * @return surveyId
	 */
	@Transactional
	public int getSurveyIdByTitle(String title);
	
	/**
	 * Uploads a survey template to the database
	 * @param st
	 */
	@Transactional
	public void uploadSurveyTemplate(SurveyTemplate st);
	
	/**
	 * Modify a survey template on title and description fields
	 * @param st
	 */
	@Transactional
	public void updateSurveyTemplate(SurveyTemplate st);
	/**
	 * Delete a survey template from the database
	 * @param id
	 */
	@Transactional
	public void deleteSurveyTemplate(int id);
	/**
	 * Count how many survey template 
	 * @return number of survey template
	 */
	@Transactional
	public int countSurveyTemplate();
	
	/**
	* a List of Survey results for patient whose id is patientId
	* @param patientId
	* @return A List of SurveyResult objects
	*/
	@Transactional
	public List<SurveyResult> getSurveysByPatientID(int patientId);
	
	/**
	* a List of completed Survey results for patient whose id is patientId
	* @param patientId
	* @return A List of SurveyResult objects
	*/
	@Transactional
	public List<SurveyResult> getCompletedSurveysByPatientID(int patientId);	
	
	/**
	* a List of uncompleted Survey results for patient whose id is patientId
	* @param patientId
	* @return A List of SurveyResult objects
	*/
	@Transactional
	public List<SurveyResult> getIncompleteSurveysByPatientID(int patientId);

	/**
	 * 
	 * A object of SurveyResult which result_Id is id
	 * @param id
	 * @return an object of SurveyResult
	 */
	@Transactional
	public SurveyResult getSurveyResultByID(int id);
	
	/**
	 * 
	 * @param siteId
	 * @return
	 */
	@Transactional
	public List<SurveyResult> getAllSurveyResultsBySite(int siteId);
	
	/**
	 * Ordered by title ascending
	 * @return a list of SurveyResult objects
	 */
	@Transactional
	public List<SurveyResult> getAllSurveyResults();
	
	/**
	 * Retrieves all survey results by survey Id	
	 * @param id survey ID
	 * @return a list of SurveyResult objects
	 */
	@Transactional
	public List<SurveyResult> getAllSurveyResultsBySurveyId(int id);
	
	/**
	 * Retrieves survey result by survey Id and patientId
	 * @param itemsToReturn 
	 * @param id survey ID
	 * @return a  SurveyResult object
	 */
	@Transactional
	public SurveyResult getSurveyResultByPatientAndSurveyId(int patientId, int surveyId);
	
	/**
	 * Retrieves  completed survey result by survey Id and patientId
	 * @param itemsToReturn 
	 * @param id survey ID
	 * @return a  SurveyResult object
	 */
	@Transactional
	public SurveyResult getCompletedSurveyResultByPatientAndSurveyTitle(int patientId, String surveyTitle);
	
	/**
	 * Uploads a survey template to the database
	 * @param st
	 */
	@Transactional
	public String assignSurvey(SurveyResult sr);
	
	/**
	 * Delete a survey from the database
	 * @param id
	 */
	@Transactional
	public void deleteSurvey(int id);
	
	/**
	 * Mark a survey as completed
	 * @param id
	 */
	@Transactional
	public void markAsComplete(int id);
	
	/**
	 * Upload survey results
	 * @param id The ID of the survey result
	 * @param data The survey results
	 */
	@Transactional
	public void updateSurveyResults(int id, byte[] data);
	
	/**
	 * Set start date of survey result
	 * @param id The ID of the survey result
	 */
	@Transactional
	public void updateStartDate(int id) ;
	
	/**
	 * Count number of completed survey result for a patient
	 * @param patientId 
	 * @return number of completed survey result 
	 */
	@Transactional
	public int countCompletedSurveys(int patientId);
	
	/**
	 * 
	 * @param surveyId
	 * @param patientId
	 * @return true if the survey is completed for the patient
	 */
	@Transactional
	public boolean hasCompleteSurvey(int surveyId, int patientId);	
	
	/**
	 * Keep a copy of deleted survey template
	 * @param st
	 */
	@Transactional
	public void archiveSurveyTemplate(SurveyTemplate st, String deletedBy);
	
	/**
	 * Keep a copy of deleted survey result
	 * @param sr
	 */
	@Transactional
	public void archiveSurveyResult(SurveyResult sr, String patient,String deletedBy);

}

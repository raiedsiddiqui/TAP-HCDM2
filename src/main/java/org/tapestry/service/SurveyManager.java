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
	 * A list of default Survey template
	 * @return a list of SurveyTemplate objects
	 */
	@Transactional
	public List<SurveyTemplate> getDefaultSurveyTemplates();
	
	/**
	 * A list of Survey template
	 * @return a list of default SurveyTemplate objects
	 */
	@Transactional
	public List<SurveyTemplate> getDefaultSurveyTemplatesBySite(int siteId);
	
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
	
	/**	 * 
	 * @param title
	 * @return surveyId
	 */
	@Transactional
	public int getSurveyIdByTitle(String title);
	
	/**	 * 
	 * @param title
	 * @return surveyId
	 */
	@Transactional
	public int getSurveyIdByTitle(String title, int site);
	
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
	 * Set survey templates as default
	 * @param surveyTemplateIds
	 */
	@Transactional
	public void setDefaultSurveyTemplate(String[] surveyTemplateIds);
	
	/**
	 * Remove survey templates from default list
	 * @param surveyTemplateIds
	 */
	@Transactional
	public void removeDefaultSurveyTemplateSetting(String[] surveyTemplateIds);
	
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
	 * Count how many survey template by site
	 * @return number of survey template of a site
	 */
	@Transactional
	public int countSurveyTemplateBySite(int site);
	
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
	 * Count number of uncompleted survey result for a patient
	 * @param patientId 
	 * @return number of completed survey result 
	 */
	@Transactional
	public int countUnCompletedSurveys(int patientId);
	
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
	
	/**
	 * A list of Volunteer Survey template for UBC
	 * @return a list of SurveyTemplate objects
	 */
	@Transactional
	public List<SurveyTemplate> getAllVolunteerSurveyTemplates();
	
	/**
	 * 
	 * @param siteId
	 * @return A list of Survey templates grouped by site
	 */
	@Transactional
	public List<SurveyTemplate> getVolunteerSurveyTemplatesBySite(int siteId); 
	
	/**
	 * Uploads a survey template to the database
	 * @param st
	 */
	@Transactional
	public void uploadVolunteerSurveyTemplate(SurveyTemplate st);
	
	/**
	 * A list of volunteer Survey template which title contains partialTitle
	 * @param partialTitle
	 * @return a list of SurveyTemplate objects
	 */
	@Transactional
	public List<SurveyTemplate> getVolunteerSurveyTemplatesByPartialTitle(String partialTitle);
	
	/**
	 * Modify a volunteer survey template on title and description fields
	 * @param st
	 */
	@Transactional
	public void updateVolunteerSurveyTemplate(SurveyTemplate st);
	
	/**
	 * Ordered by title ascending
	 * @return a list of SurveyResult objects
	 */
	@Transactional
	public List<SurveyResult> getAllVolunteerSurveyResults();
	
	/**
	 * Uploads a survey template to the database
	 * @param st
	 */
	@Transactional
	public String assignVolunteerSurvey(SurveyResult sr);
	
	/**
	* a List of completed volunteer Survey results for a volunteer 
	* @param volunteerId
	* @return A List of SurveyResult objects
	*/
	@Transactional
	public List<SurveyResult> getCompletedVolunteerSurveys(int volunteerId);
	
	/**
	* a List of completed volunteer Survey results for a volunteer 
	* @param volunteerId
	* @return A List of SurveyResult objects
	*/
	@Transactional
	public List<SurveyResult> getIncompleteVolunteerSurveys(int volunteerId);
	
	/**
	* Returns the survey template with the given ID
	* @param id The ID of the survey template to find
	* @return A SurveyTemplate object representing the result
	*/
	@Transactional
	public SurveyTemplate getVolunteerSurveyTemplateByID(int id);
	
	/**
	 * 
	 * A object of SurveyResult which result_Id is id
	 * @param id
	 * @return an object of SurveyResult
	 */
	@Transactional
	public SurveyResult getVolunteerSurveyResultByID(int id);
	
	/**
	 * Upload survey results
	 * @param id The ID of the survey result
	 * @param data The survey results
	 */
	@Transactional
	public void updateVolunteerSurveyResults(int id, byte[] data);
	
	/**
	 * Delete a volunteer survey template from the database
	 * @param id
	 */
	@Transactional
	public void deleteVolunteerSurveyTemplate(int id);
	
	/**
	 * Delete a volunteer survey from the database
	 * @param id
	 */
	@Transactional
	public void deleteVolunteerSurvey(int id);
	
	/**
	 * Mark a survey as completed
	 * @param id
	 */
	@Transactional
	public void markAsCompleteForVolunteerSurvey(int id);
	
	/**
	 * 
	 * @param volunteerId
	 * @return
	 */
	@Transactional
	public List<SurveyResult> getVolunteerSurveyResultsByVolunteerId(int volunteerId);
	
	/**
	 * 
	 * @param patientId
	 * @return
	 */
	@Transactional
	public boolean hasCompletedAllSurveysForReport(int patientId, int siteId);
	/**
	 * 
	 * @param surveyId
	 * @return
	 */
	@Transactional
	public int countVolunteerSurveyResultsBySurveyId(int surveyId);
	
	
	/**
	 * 
	 * @param surveyResultId
	 * @return
	 */
	@Transactional
	public int getSiteBySurveyResultID(int surveyResultId);

}

package org.tapestry.dao;

import java.util.List;

import org.tapestry.objects.SurveyTemplate;
/**
 * Defines DAO operations for the SurveyTemplate model.
 * 
 * @author lxie 
*/
public interface SurveyTemplateDAO {
	/**
	* Returns the survey template with the given ID
	* @param id The ID of the survey template to find
	* @return A SurveyTemplate object representing the result
	*/
	public SurveyTemplate getSurveyTemplateByID(int id);
	
	/**
	 * A list of Survey template
	 * @return a list of SurveyTemplate objects
	 */
	public List<SurveyTemplate> getAllSurveyTemplates();
	
	/**
	 * A list of default Survey template
	 * @return a list of SurveyTemplate objects
	 */
	public List<SurveyTemplate> getDefaultSurveyTemplates();
	
	/**
	 * A list of default Survey template
	 * @return a list of SurveyTemplate objects
	 */
	public List<SurveyTemplate> getDefaultSurveyTemplatesBySite(int siteId);
	
	/**
	 * A list of Survey template
	 * @return a list of SurveyTemplate objects belong to a same site
	 */
	public List<SurveyTemplate> getSurveyTemplatesBySite(int siteId);
	
	/**
	 * A list of Survey template which title contains partialTitle
	 * @param partialTitle
	 * @return a list of SurveyTemplate objects
	 */
	public List<SurveyTemplate> getSurveyTemplatesByPartialTitle(String partialTitle);
	
	/**
	 * 
	 * @param title
	 * @return surveyId
	 */
	public int getSurveyIdByTitle(String title);
	
	/**
	 * @param site
	 * @param title
	 * @return surveyId
	 */
	public int getSurveyIdByTitle(String title, int site);
	
	/**
	 * Uploads a survey template to the database
	 * @param st
	 */
	public void uploadSurveyTemplate(SurveyTemplate st);
	
	/**
	 * Modify a survey template on title and description fields
	 * @param st
	 */
	public void updateSurveyTemplate(SurveyTemplate st);
	
	/**
	 * Set survey templates as default
	 * @param surveyIds
	 */
	public void setDefaultSurveyTemplate(String[] surveyIds);
				
	/**
	 * removed survey templates from default
	 * @param surveyIds
	 */
	public void removeDefaultSurveyTemplateSetting(String[] surveyIds);
	
	/**
	 * Delete a survey template from the database
	 * @param id
	 */
	public void deleteSurveyTemplate(int id);
	
	/**
	 * Count how many survey templates 
	 * @return number of survey template
	 */
	public int countSurveyTemplate();
	
	/**
	 * Count how many survey templates for a site
	 * @return number of survey template
	 */
	public int countSurveyTemplateBySite(int site);
	
	/**
	 * Keep a copy of deleted survey template
	 * @param st
	 */
	public void archiveSurveyTemplate(SurveyTemplate st, String deletedBy);
	
	/**
	 * 
	 * @param siteId
	 * @return
	 */
	public List<String> getSurveyTemplateTitlesBySite(int siteId);
	
	//=================== UBC ===============
	/**
	 * A list of Volunteer Survey template for UBC
	 * @return a list of SurveyTemplate objects
	 */
	public List<SurveyTemplate> getAllVolunteerSurveyTemplates();
	
	/**
	 * Uploads a survey template to the database
	 * @param st
	 */
	public void uploadVolunteerSurveyTemplate(SurveyTemplate st);
	
	/**
	 * A list of Volunteer Survey template which title contains partialTitle
	 * @param partialTitle
	 * @return a list of SurveyTemplate objects
	 */
	public List<SurveyTemplate> getVolunteerSurveyTemplatesByPartialTitle(String partialTitle);
	
	/**
	 * 
	 * @param siteId
	 * @return
	 */
	public List<SurveyTemplate> getVolunteerSurveyTemplatesBySite(int siteId);
	
	/**
	 * Modify a survey template on title and description fields
	 * @param st
	 */
	public void updateVolunteerSurveyTemplate(SurveyTemplate st);
	
	/**
	* Returns the survey template with the given ID
	* @param id The ID of the survey template to find
	* @return A SurveyTemplate object representing the result
	*/
	public SurveyTemplate getVolunteerSurveyTemplateByID(int id);
	
	/**
	 * 
	 * @param siteId
	 * @return
	 */
	public List<String> getVolunteerSurveyTemplateTitlesBySite(int siteId);
	
	/**
	 * Delete a volunteer survey template from the database
	 * @param id
	 */
	public void deleteVolunteerSurveyTemplate(int id);	
	
	/**
	 * 
	 * @param surveyId
	 * @return
	 */
	public int countVolunteerSurveyResultsBySurveyId(int surveyId);
	
	/**
	 * 
	 * @param surveyIds
	 */
	public void setDefaultVolunteerSurveyTemplate(String[] surveyIds);
	
	/**
	 * 
	 * @param surveyIds
	 */
	public void removeDefaultVolunteerSurveyTemplateSetting(String[] surveyIds);
	
}

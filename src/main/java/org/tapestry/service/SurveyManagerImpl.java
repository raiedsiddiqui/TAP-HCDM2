package org.tapestry.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tapestry.dao.SurveyResultDAO;
import org.tapestry.dao.SurveyTemplateDAO;
import org.tapestry.objects.SurveyResult;
import org.tapestry.objects.SurveyTemplate;
/**
 * Implementation for service SurveyManager
 * @author lxie 
 */
@Service
public class SurveyManagerImpl implements SurveyManager {
	@Autowired
	private SurveyResultDAO surveyResultDao;
	@Autowired
	private SurveyTemplateDAO surveyTemplateDao;
	
	@Override
	public List<SurveyResult> getSurveysByPatientID(int patientId) {
		return surveyResultDao.getSurveysByPatientID(patientId);
	}

	@Override
	public List<SurveyResult> getCompletedSurveysByPatientID(int patientId) {
		return surveyResultDao.getCompletedSurveysByPatientID(patientId);
	}

	@Override
	public List<SurveyResult> getIncompleteSurveysByPatientID(int patientId) {
		return surveyResultDao.getIncompleteSurveysByPatientID(patientId);
	}

	@Override
	public SurveyResult getSurveyResultByID(int id) {
		return surveyResultDao.getSurveyResultByID(id);
	}

	@Override
	public List<SurveyResult> getAllSurveyResults() {
		return surveyResultDao.getAllSurveyResults();
	}
	
	@Override
	public List<SurveyResult> getAllSurveyResultsBySite(int siteId) {
		return surveyResultDao.getAllSurveyResultsBySite(siteId);
	}

	@Override
	public List<SurveyResult> getAllSurveyResultsBySurveyId(int id) {
		return surveyResultDao.getAllSurveyResultsBySurveyId(id);
	}

	@Override
	public SurveyResult getSurveyResultByPatientAndSurveyId(int patientId, int surveyId) {
		return surveyResultDao.getSurveyResultByPatientAndSurveyId(patientId, surveyId);
	}

	@Override
	public SurveyResult getCompletedSurveyResultByPatientAndSurveyTitle(int patientId, String surveyTitle) {
		return surveyResultDao.getCompletedSurveyResultByPatientAndSurveyTitle(patientId, surveyTitle);
	}

	@Override
	public String assignSurvey(SurveyResult sr) {
		return surveyResultDao.assignSurvey(sr);
	}

	@Override
	public void deleteSurvey(int id) {
		surveyResultDao.deleteSurvey(id);
	}

	@Override
	public void markAsComplete(int id) {
		surveyResultDao.markAsComplete(id);
	}

	@Override
	public void updateSurveyResults(int id, byte[] data) {
		surveyResultDao.updateSurveyResults(id, data);
	}

	@Override
	public void updateStartDate(int id) {
		surveyResultDao.updateStartDate(id);
	}

	@Override
	public int countCompletedSurveys(int patientId) {
		return surveyResultDao.countCompletedSurveys(patientId);
	}
	
	@Override
	public int countUnCompletedSurveys(int patientId) {	
		return surveyResultDao.countUnCompletedSurveys(patientId);
	}

	@Override
	public boolean hasCompleteSurvey(int surveyId, int patientId){
		return surveyResultDao.hasCompleteSurvey(surveyId, patientId);
	}
	
	@Override
	public SurveyTemplate getSurveyTemplateByID(int id) {
		return surveyTemplateDao.getSurveyTemplateByID(id);
	}

	@Override
	public int getSurveyIdByTitle(String title, int site) {
		return surveyTemplateDao.getSurveyIdByTitle(title, site);
	}

	@Override
	public List<SurveyTemplate> getAllSurveyTemplates() {
		return surveyTemplateDao.getAllSurveyTemplates();
	}

	@Override
	public List<SurveyTemplate> getSurveyTemplatesBySite(int siteId) {
		return surveyTemplateDao.getSurveyTemplatesBySite(siteId);
	}

	@Override
	public List<SurveyTemplate> getDefaultSurveyTemplates() {		
		return surveyTemplateDao.getDefaultSurveyTemplates();
	}

	@Override
	public List<SurveyTemplate> getDefaultSurveyTemplatesBySite(int siteId) {
		return surveyTemplateDao.getDefaultSurveyTemplatesBySite(siteId);
	}
	
	@Override
	public List<SurveyTemplate> getSurveyTemplatesWithCanDelete(int siteId) {
		List<SurveyTemplate> surveyTemplates;
		int surveyTemplateId;
		boolean noDelete;
		
		if (siteId == 0)//for central admin
		{
			surveyTemplates = getAllSurveyTemplates();			
			for (SurveyTemplate st: surveyTemplates)
			{
				surveyTemplateId = st.getSurveyID();
				noDelete = surveyResultDao.countSurveysBySurveyTemplateId(surveyTemplateId) > 0;
				if(noDelete)
					st.setShowDelete(false);
				else
					st.setShowDelete(true);			
			}
		}
		else// for local admin/site admin
		{
			surveyTemplates = this.getSurveyTemplatesBySite(siteId);			
			for (SurveyTemplate st: surveyTemplates)
			{
				surveyTemplateId = st.getSurveyID();
				noDelete = surveyResultDao.countSurveysBySurveyTemplateId(surveyTemplateId) > 0;
				if(noDelete)
					st.setShowDelete(false);
				else
					st.setShowDelete(true);			
			}
		}	
		return surveyTemplates;
	}

	@Override
	public List<SurveyTemplate> getSurveyTemplatesByPartialTitle(String partialTitle) {
		return surveyTemplateDao.getSurveyTemplatesByPartialTitle(partialTitle);
	}
	
	@Override
	public int getSurveyIdByTitle(String title){
		return surveyTemplateDao.getSurveyIdByTitle(title);
	}

	@Override
	public void uploadSurveyTemplate(SurveyTemplate st) {
		surveyTemplateDao.uploadSurveyTemplate(st);
	}

	@Override
	public void updateSurveyTemplate(SurveyTemplate st) {
		surveyTemplateDao.updateSurveyTemplate(st);		
	}
	
	@Override
	public void setDefaultSurveyTemplate(String[] surveyTemplateIds) {
		surveyTemplateDao.setDefaultSurveyTemplate(surveyTemplateIds);
	}
	
	@Override
	public void removeDefaultSurveyTemplateSetting(String[] surveyTemplateIds) {
		surveyTemplateDao.removeDefaultSurveyTemplateSetting(surveyTemplateIds);
	}
	
	@Override
	public void deleteSurveyTemplate(int id) {
		surveyTemplateDao.deleteSurveyTemplate(id);
	}

	@Override
	public int countSurveyTemplate() {
		return surveyTemplateDao.countSurveyTemplate();
	}
	
	@Override
	public int countSurveyTemplateBySite(int site) {
		return surveyTemplateDao.countSurveyTemplateBySite(site);
	}


	@Override
	public void archiveSurveyTemplate(SurveyTemplate st, String deletedBy) {
		surveyTemplateDao.archiveSurveyTemplate(st, deletedBy);		
	}

	@Override
	public void archiveSurveyResult(SurveyResult sr, String patient, String deletedBy) {
		surveyResultDao.archiveSurveyResult(sr, patient, deletedBy);
	}

	@Override
	public List<SurveyTemplate> getAllVolunteerSurveyTemplates() {		
		return surveyTemplateDao.getAllVolunteerSurveyTemplates();
	}

	@Override
	public List<SurveyTemplate> getVolunteerSurveyTemplatesBySite(int siteId) {		
		return surveyTemplateDao.getVolunteerSurveyTemplatesBySite(siteId);
	}
	
	@Override
	public void uploadVolunteerSurveyTemplate(SurveyTemplate st) {
		surveyTemplateDao.uploadVolunteerSurveyTemplate(st);
		
	}

	@Override
	public List<SurveyTemplate> getVolunteerSurveyTemplatesByPartialTitle(String partialTitle) {		
		return surveyTemplateDao.getVolunteerSurveyTemplatesByPartialTitle(partialTitle);
	}

	@Override
	public void updateVolunteerSurveyTemplate(SurveyTemplate st) {
		surveyTemplateDao.updateVolunteerSurveyTemplate(st);
		
	}

	@Override
	public List<SurveyResult> getAllVolunteerSurveyResults() {
		return surveyResultDao.getAllVolunteerSurveyResults();
	}
	
	@Override
	public String assignVolunteerSurvey(SurveyResult sr){
		return surveyResultDao.assignVolunteerSurvey(sr);
	}

	@Override
	public List<SurveyResult> getCompletedVolunteerSurveys(int volunteerId) {
		return surveyResultDao.getCompletedVolunteerSurveys(volunteerId);
	}

	@Override
	public List<SurveyResult> getIncompleteVolunteerSurveys(int volunteerId) {
		return surveyResultDao.getIncompleteVolunteerSurveys(volunteerId);
	}

	@Override
	public SurveyTemplate getVolunteerSurveyTemplateByID(int id) {		
		return surveyTemplateDao.getVolunteerSurveyTemplateByID(id);
	}

	@Override
	public SurveyResult getVolunteerSurveyResultByID(int id) {
		return surveyResultDao.getVolunteerSurveyResultByID(id);
	}

	@Override
	public void updateVolunteerSurveyResults(int id, byte[] data) {
		surveyResultDao.updateVolunteerSurveyResults(id, data);
		
	}
	
	@Override
	public void deleteVolunteerSurveyTemplate(int id) {
		surveyTemplateDao.deleteVolunteerSurveyTemplate(id);
		
	}

	@Override
	public void markAsCompleteForVolunteerSurvey(int id) {
		surveyResultDao.markAsCompleteForVolunteerSurvey(id);
		
	}

	@Override
	public List<SurveyResult> getVolunteerSurveyResultsByVolunteerId(int volunteerId) {		
		return surveyResultDao.getVolunteerSurveyResultsByVolunteerId(volunteerId);
	}

	@Override
	public boolean hasCompletedAllSurveysForReport(int patientId, int siteId) {
		boolean completed = false;		
		List<SurveyResult> surveys = getCompletedSurveysByPatientID(patientId);		
		List<String> completedSurveyTitles = new ArrayList<String>();
		
		for (SurveyResult st : surveys)
			completedSurveyTitles.add(st.getSurveyTitle());				
		
		//all surveys except "3 Month Follow" are finished
//		int templateCount = countSurveyTemplateBySite(siteId);
//		int completedSurveyCount = surveys.size();
//		
//		if ((templateCount == completedSurveyCount)||((templateCount - completedSurveyCount) == 1) 
//				&& (!completedSurveyTitles.contains("3 Month Follow Up")))
//			completed = true;		
		
		List<SurveyTemplate> stList = getSurveyTemplatesBySite(siteId);
		List<String> titles = new ArrayList<String>();
		for (SurveyTemplate st: stList)
		{
			if (!"3 Month Follow Up".equals(st.getTitle()))
				titles.add(st.getTitle());
		}
		
		if (completedSurveyTitles.containsAll(titles))
			completed = true;
		
		return completed;
	}

	@Override
	public int countVolunteerSurveyResultsBySurveyId(int surveyId) {
		return surveyTemplateDao.countVolunteerSurveyResultsBySurveyId(surveyId);
	}

	@Override
	public void deleteVolunteerSurvey(int id) {
		surveyResultDao.deleteVolunteerSurvey(id);		
	}

	@Override
	public int getSiteBySurveyResultID(int surveyResultId) {		
		return surveyResultDao.getSiteBySurveyResultID(surveyResultId);
	}

	

}

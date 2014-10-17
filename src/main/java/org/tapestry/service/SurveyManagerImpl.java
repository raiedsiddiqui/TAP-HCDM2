package org.tapestry.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
	public List<SurveyResult> getAllSurveyResultsBySurveyId(int id) {
		return surveyResultDao.getAllSurveyResultsBySurveyId(id);
	}

	@Override
	public List<SurveyResult> getSurveyResultByPatientAndSurveyId(int patientId, int surveyId) {
		return surveyResultDao.getSurveyResultByPatientAndSurveyId(patientId, surveyId);
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
	public SurveyTemplate getSurveyTemplateByID(int id) {
		return surveyTemplateDao.getSurveyTemplateByID(id);
	}

	@Override
	public List<SurveyTemplate> getAllSurveyTemplates() {
		return surveyTemplateDao.getAllSurveyTemplates();
	}

	@Override
	public List<SurveyTemplate> getSurveyTemplatesByPartialTitle(String partialTitle) {
		return surveyTemplateDao.getSurveyTemplatesByPartialTitle(partialTitle);
	}

	@Override
	public void uploadSurveyTemplate(SurveyTemplate st) {
		surveyTemplateDao.uploadSurveyTemplate(st);
	}

	@Override
	public void deleteSurveyTemplate(int id) {
		surveyTemplateDao.deleteSurveyTemplate(id);
	}

	@Override
	public int countSurveyTemplate() {
		return surveyTemplateDao.countSurveyTemplate();
	}

}

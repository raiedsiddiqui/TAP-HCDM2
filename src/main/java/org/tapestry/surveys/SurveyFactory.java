package org.tapestry.surveys;

import java.util.Hashtable;

import org.survey_component.data.SurveyException;
import org.survey_component.source.SurveyParseException;
import org.tapestry.objects.SurveyTemplate;

public class SurveyFactory {
	//DO NOT MAKE THIS PUBLIC!!!!!
	private static Hashtable<String, TapestryPHRSurvey> loadedSurveys = new Hashtable<String, TapestryPHRSurvey>();
	
	public TapestryPHRSurvey getSurveyTemplate(SurveyTemplate surveyTemplate) throws SurveyException
	{
		//check if it's loaded, if not, try to load
		ensureLoaded(surveyTemplate);
		return (TapestryPHRSurvey)loadedSurveys.get(Integer.toString(surveyTemplate.getSurveyID())).cloneSurvey(); //write-protecting to the template survey
	}
	
	//optimized
	public TapestryPHRSurvey getSurveyTemplateNoQuestions(SurveyTemplate surveyTemplate) throws SurveyException
	{
		//check if it's loaded, if not, try to load
		ensureLoaded(surveyTemplate);
		return (TapestryPHRSurvey)loadedSurveys.get(Integer.toString(surveyTemplate.getSurveyID())).cloneSurveyNoQuestions(); //write-protecting to the template survey
	}
		
	private void ensureLoaded(SurveyTemplate surveyTemplate) throws SurveyException
	{
		if (surveyTemplate == null)
		{
			throw new SurveyException("Requested survey was null");
		}

		//comment condition check out, since there is loading problem when client survey ID is same volunteer survey ID
//		if (!loadedSurveys.containsKey(Integer.toString(surveyTemplate.getSurveyID())))
//		{
			TapestryPHRSurvey loadedSurvey = SurveyActionMumps.loadSurveySource(surveyTemplate);
			if (loadedSurvey == null) throw new SurveyException("Requested survey '" + surveyTemplate.getSurveyID() + "' cannot be found");
			loadedSurveys.put(Integer.toString(surveyTemplate.getSurveyID()), loadedSurvey);
//		}
	}
		
	public void reloadSurveyTemplate(SurveyTemplate surveyTemplate) throws SurveyParseException
	{
		TapestryPHRSurvey loadedSurvey = SurveyActionMumps.loadSurveySource(surveyTemplate);
		if (loadedSurvey == null) throw new SurveyParseException("Specified survey '" + surveyTemplate.getSurveyID() + "' cannot be found");
		loadedSurveys.put(Integer.toString(surveyTemplate.getSurveyID()), loadedSurvey);
	}
}

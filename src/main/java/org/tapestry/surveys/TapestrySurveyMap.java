package org.tapestry.surveys;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

public class TapestrySurveyMap extends LinkedHashMap<String, TapestryPHRSurvey> {
	
	public TapestrySurveyMap()
	{
		super();
	}

	public TapestrySurveyMap(List<TapestryPHRSurvey> surveyList)
	{
		for (TapestryPHRSurvey survey : surveyList)
		{
			this.put(survey.getDocumentId(), survey);
		}
	}

	public List<TapestryPHRSurvey> getSurveyList()
	{
		Set<String> keys = this.keySet();
		ArrayList<TapestryPHRSurvey> surveys = new ArrayList<TapestryPHRSurvey>();
		for (String key : keys)
		{
			surveys.add(this.get(key));
		}
		return surveys;
	}

	public List<TapestryPHRSurvey> getSurveyListById(String surveyId)
	{
		Set<Entry<String, TapestryPHRSurvey>> entries = this.entrySet();
		ArrayList<TapestryPHRSurvey> surveys = new ArrayList<TapestryPHRSurvey>();
		
		for (Entry<String, TapestryPHRSurvey> entry : entries)
		{
			TapestryPHRSurvey curSurvey = entry.getValue();
			if (curSurvey.getSurveyId().equals(surveyId)) 
				surveys.add(curSurvey);
		}
		return surveys;
	}

	public TapestryPHRSurvey getSurvey(String documentId)
	{
		return this.get(documentId);
	}

	public void addSurvey(TapestryPHRSurvey survey)
	{
		this.put(survey.getDocumentId(), survey);
	}

}

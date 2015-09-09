package org.tapestry.surveys;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import org.survey_component.data.PHRSurvey;
import org.survey_component.data.QuestionAnswerPair;
import org.survey_component.data.SurveyException;
import org.survey_component.data.SurveyQuestion;
import org.survey_component.data.answer.SurveyAnswer;
import org.survey_component.data.answer.SurveyAnswerString;
import org.survey_component.services.SurveyServiceIndivo;
import org.survey_component.source.SurveyParseException;
import org.tapestry.objects.SurveyResult;
import org.tapestry.objects.SurveyTemplate;

public class SurveyActionMumps {
	public static TapestryPHRSurvey loadSurveySource(SurveyTemplate surveyTemplate) throws SurveyParseException
	{
		try
		{
			ByteArrayInputStream bais = new ByteArrayInputStream(surveyTemplate.getContents());

			Long templateId = Long.parseLong(Integer.toString(surveyTemplate.getSurveyID()));

			return (TapestrySurveySourceMumps.loadSurveySource(bais, templateId));
		}
		catch (Exception e)
		{
			throw new SurveyParseException("Survey Construction failed, see previous errors", e);
		}
	}
	
	public static TapestryPHRSurvey toPhrSurvey(List<SurveyTemplate> surveyTemplates, SurveyResult surveyResult) throws SurveyException
	{				
		SurveyTemplate st = new SurveyTemplate();
		for (SurveyTemplate tempTemplate : surveyTemplates)
		{
			if(tempTemplate.getSurveyID() == surveyResult.getSurveyID()) {
				st = tempTemplate;
			}
		}
		
		SurveyFactory surveyFactory = new SurveyFactory();
		TapestryPHRSurvey phrSurveyResult = (TapestryPHRSurvey)surveyFactory.getSurveyTemplateNoQuestions(st);

		phrSurveyResult.setDocumentId(Integer.toString(surveyResult.getResultID()));
		phrSurveyResult.setSurveyId(Integer.toString(surveyResult.getSurveyID()));
		phrSurveyResult.setComplete(surveyResult.isCompleted());

		//load answers  
		PHRSurvey phrSurveyTemplate = surveyFactory.getSurveyTemplate(st);
		for (QuestionAnswerPair qaPair : surveyResult.getQuestionAnswerPairs())
		{
			if (SurveyServiceIndivo.INDIVO_SURVEY_ID.equals(qaPair.questionId)) continue;
			if (SurveyServiceIndivo.INDIVO_SURVEY_HASH.equals(qaPair.questionId)) continue;

			//assuming that survey ID question is loaded by now
			SurveyQuestion templateQuestion = phrSurveyTemplate.getQuestionById(qaPair.questionId);

			SurveyQuestion mQuestion = templateQuestion;
			
			for (String answer : qaPair.answers)
			{
				SurveyAnswer mAnswer = new SurveyAnswerString(answer);						
				mQuestion.getAnswers().add(mAnswer);
			}
			
			phrSurveyResult.getQuestions().add(mQuestion);
		}

		return phrSurveyResult;
	}
}

package org.tapestry.surveys;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.tapestry.objects.DisplayedSurveyResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;



public class ResultParser {

    private static Document loadXMLFromString(String xml) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        InputSource is = new InputSource(new StringReader(xml));
        return builder.parse(is);
    }
    
    public static List<String> getSurveyQuestions(String surveyData){
    	List<String> questionTexts = new ArrayList<String>();
    	String qText = "";
    	
    	try{
            Document doc = loadXMLFromString(surveyData);
            doc.getDocumentElement().normalize();

            NodeList questions = doc.getElementsByTagName("IndivoSurveyQuestion");
            for (int i = 0; i < questions.getLength(); i++){
                Element question = (Element) questions.item(i);

                NodeList questionTextList = question.getElementsByTagName("QuestionText");
                if (questionTextList.getLength() > 0){
                    Element questionText = (Element) questionTextList.item(0);
                    qText = questionText.getTextContent().trim();
                    
                    questionTexts.add(qText);
                }
                
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
       
    	 return questionTexts;
    	
    }

    public static LinkedHashMap<String, String> getResults(String surveyData) {
        LinkedHashMap<String, String> results = new LinkedHashMap<String, String>();
        String qText = "";
        String questionTextWithObserverNotes="";        
        String separator = "/observernote/";
        String observerNote= "";
        int ind = 0;
             
        try{
            Document doc = loadXMLFromString(surveyData);
            doc.getDocumentElement().normalize();
            Node titleNode = doc.getElementsByTagName("Title").item(0);
            String title = titleNode.getTextContent().trim();
            results.put("title", title);
            Node dateNode = doc.getElementsByTagName("IssueDate").item(0);
            String date = dateNode.getTextContent().trim();
            results.put("date", date);
            NodeList questions = doc.getElementsByTagName("IndivoSurveyQuestion");
            for (int i = 0; i < questions.getLength(); i++){
                Element question = (Element) questions.item(i);
                String questionString = "";
                String questionAnswerString = "";
              
                NodeList questionIDList = question.getElementsByTagName("QuestionId");
                if (questionIDList.getLength() > 0){
                    Element questionID = (Element) questionIDList.item(0);
                    if (questionID.getTextContent().trim().equals("surveyHash"))
                        continue;
                    questionString += questionID.getTextContent().trim();
                }
                
                NodeList questionTextList = question.getElementsByTagName("QuestionText");
                
                if (questionTextList.getLength() > 0){
                    Element questionText = (Element) questionTextList.item(0);                      
                    questionTextWithObserverNotes += questionText.getTextContent().trim();  
                    
                    qText = "/question_text/" + questionText.getTextContent().trim();                 
                    results.put(questionString, qText);
                }
                
                ind = questionTextWithObserverNotes.lastIndexOf(separator);                
                if (ind != (-1))
                {
                	observerNote = questionTextWithObserverNotes.substring(ind);    
                	results.put(questionString, observerNote);
                }              
                
                NodeList questionAnswerList = question.getElementsByTagName("QuestionAnswer");
                
                if (questionAnswerList.getLength() > 0){                	
                	for (int j = 0; j < questionAnswerList.getLength(); j++){
                		Element questionAnswer = (Element) questionAnswerList.item(j);                		                		
                		questionAnswerString += questionAnswer.getTextContent().trim();
                		
                		if (questionAnswer.getNextSibling() != null)
                			questionAnswerString += ", ";
                	}
                }                  
                //append observernote to question answer               
                if (results.get(questionString) != null)               
                	questionAnswerString += results.get(questionString).toString();
               
   //             questionString = qText;
                results.put(questionString, questionAnswerString);     
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return results;
    }
    
    /**
     * Display bean for Survey Result
     * ObserverNotes is appended in questionAnswer, and need to be extracted 
     * @param results
     * @return
     */
    public static List<DisplayedSurveyResult> getDisplayedSurveyResults(Map<String, String> results){
    	List<DisplayedSurveyResult> resultList = new ArrayList<DisplayedSurveyResult>();
    	DisplayedSurveyResult result;
    	String answer = "";
		String questionAnswer = "";
		String observerNotes = "";
		String qText = "";
		String key;
		String title = getTitleOrDate(results, "title");
		String date = getTitleOrDate(results, "date");
		
    	for (Map.Entry<String, String> entry: results.entrySet()){
    		key = entry.getKey();        		
    		result = new DisplayedSurveyResult();  		
    		
    		//set question key, answer and observer notes
    		if (!key.contains("surveyId") && !key.equals("date") && !key.equals("title"))
    		{//all answer, observe note and question text are in the value of map  
    			answer = entry.getValue();    	
    			
    			if (!answer.startsWith("-"))//remove first non-question-answer pair, only information
    			{
        			String separator1 = "/observernote/";
        			int index1 = answer.indexOf(separator1);
        			int length1 = separator1.length();
        			String separator2 = "/question_text/";
        			int index2 = answer.indexOf(separator2);
        			int length2 = separator2.length();
        			
        			if (index1 != -1)// has /observernote/...
        			{
        				if (index1 == 0)
        					questionAnswer ="";
        				else
        					questionAnswer = answer.substring(0, index1);
        				
        				if (index2 != -1)
        				{
        					observerNotes = answer.substring(index1 + length1, index2);
        					qText = answer.substring(index2+ length2);
        				}
        				else
        				{
        					observerNotes = answer.substring(index1 + length1);
        					qText = "";
        				}    				
        			}
        			else// doesn't have /observernote/...
        			{
        				if (index2 != -1)//has /questionText/...
        				{
        					if (index2 == 0)
        						questionAnswer ="";
        					else
        						questionAnswer = answer.substring(0, index2);
    						
        					qText = answer.substring(index2 + length2);
        				}
        				else
        					questionAnswer = answer;
        			}  
            	    result.setQuestionId(key);
            	    result.setQuestionAnswer(questionAnswer);
            	    result.setObserverNotes(observerNotes);        	   
            		result.setTitle(title); 
            		result.setDate(date);
            		result.setQuestionText(qText);
            		
            	    resultList.add(result);
    			}       			
    		}
    	}    	
    	return resultList;
    }
    
    private static String getTitleOrDate(Map<String, String> results, String type){    	
    	String res = "";
    	for (Map.Entry<String, String> entry: results.entrySet()){	
			String key = entry.getKey();
    		if (key.equals(type)){
    			res = entry.getValue();    		
    		}
		}    	
    	return res;
    }
    
    /**
     * Concatenate a LinkedHashMap of results with the specified characters
     * @param results The results list returned by getResults()
     * @param join The character(s) to use to separate the question ID from the answer
     * @return The results
     */
    private static String joinResults(LinkedHashMap<String, String> results, String join){
		String ret = "";
		String separator = "/observernote/";
		for (Map.Entry<String, String> r : results.entrySet())
		{
			ret += r.getKey() + join + r.getValue() + "\n";
			//remove seperator from string
			ret = ret.replaceAll(separator, "");
		}
		return ret;
	}
    
    /**
     * Converts a result set to a series of comma-separated values that
     * can then be loaded into a spreadsheet or something.
     * @param results The LinkedHashMap returned by getResults()
     * @return The results as comma-separated values
     */
    public static String resultsAsCSV(LinkedHashMap<String, String> results){
		return joinResults(results, ",");
	}
	
	/**
	 * Converts a result set to a relatively human-readable list of values
	 * @param results The LinkedHashMap returned by getResults()
	 * @return The results as Question: Answer pairs
	 */
	public static String resultsAsText(LinkedHashMap<String, String> results){
		return joinResults(results, ": ");
	}
	
	public static String resultsAsHTML(LinkedHashMap<String, String> results){
		String s = joinResults(results, ": ");
		return s.replace("\n", "<br/>");
	}

}
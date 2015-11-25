package org.tapestry.surveys;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.tapestry.objects.DisplayedSurveyResult;
import org.tapestry.utils.Utils;
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
        String strQuestionID="";       
        String strQuestionText="";
        StringBuffer sb;        
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
                    strQuestionID = questionID.getTextContent().trim();
              
//                    if (strQuestionID.equals("surveyHash") || strQuestionID.equals("surveyId")||strQuestionID.equals("finish"))                  
//                    	continue;
                    if (strQuestionID.equals("surveyHash") ||strQuestionID.equals("finish"))                  
                    	continue;
                   
                    questionString += strQuestionID;                  
                }
                NodeList questionTextList = question.getElementsByTagName("QuestionText");
                if (questionTextList.getLength() > 0){
                	Element questionText = (Element) questionTextList.item(0);   
                	strQuestionText = questionText.getTextContent().trim();                	
                }
                sb = new StringBuffer();
                sb.append(strQuestionText);
                sb.append("/answer/");
                NodeList questionAnswerList = question.getElementsByTagName("QuestionAnswer");                
                if (questionAnswerList.getLength() > 0){                	
                	for (int j = 0; j < questionAnswerList.getLength(); j++){
                		Element questionAnswer = (Element) questionAnswerList.item(j);                		                		
                		questionAnswerString += questionAnswer.getTextContent().trim();
                		
                		if (questionAnswer.getNextSibling() != null)
                			questionAnswerString += ", ";
    //            			questionAnswerString += "|";
                	}
                }                    
                sb.append(questionAnswerString);
                results.put(questionString, sb.toString()); 
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
		String surveyId= "";
		String title = getTitleOrDate(results, "title");
		String date = getTitleOrDate(results, "date");		
		String regex = "[0-9]"; 
				
    	for (Map.Entry<String, String> entry: results.entrySet()){
    		key = entry.getKey();           		
    		result = new DisplayedSurveyResult();  		
    		
    		String separator2 = "/answer/";
    		int length2 = separator2.length();	
    		
    		if (key.contains("surveyId"))
    		{
    			surveyId = entry.getValue();
    			if (surveyId.indexOf(separator2) != -1)
    				surveyId = surveyId.substring(length2);
    		}
    		//set question key, answer and observer notes
    		if (!key.contains("surveyId") && !key.equals("date") && !key.equals("title"))
    		{//all answer, observe note and question text are in the value of map  
    			answer = entry.getValue();  
    			
    			String separator1 = "/observernote/";
    			int index1 = answer.indexOf(separator1);
    			int length1 = separator1.length(); 
    			int index2 = answer.indexOf(separator2);
    			
    			questionAnswer = answer.substring(index2 + length2);    			

    			//Void questions in mumps have a answer value "-". Check if a normal answer starts with "-" and remove the start and end void questions. 
    			if ((!questionAnswer.startsWith("-")) || (questionAnswer.startsWith("-") && questionAnswer.length()>1))//remove first non-question-answer pair, only information
    			{
    				if (index1 != -1)// has /observernote/...
    				{ 
    					qText = answer.substring(0,index1);   
    					observerNotes = answer.substring(answer.lastIndexOf(separator1)+ length1, index2);

            		}
    				else //has no /observernote
    					qText = answer.substring(0,index2);
    				
    				if (qText.startsWith("Question "))//remove "Question * of *"
    				{
    					qText = qText.substring(16);
    					//for case "Question * of **"
    					if (qText.substring(0, 1).matches(regex))
    						qText = qText.substring(1);    						
    				}
    				
    				if(qText.contains("&rsquo;"))
    					qText = qText.replace("&rsquo;", "'");
    				
    				if (questionAnswer.contains("\n"))//replace newline character(^M which is a carriage-return character) with "." for research requests
    					questionAnswer = Utils.replaceNewlineChar(questionAnswer,  ".");    					
    			
    				if (observerNotes.contains("\n"))//replace newline character(^M which is a carriage-return character) with "." for research requests
    					observerNotes = Utils.replaceNewlineChar(observerNotes,  ".");
    			    				
    				result.setQuestionId(key);
    				result.setQuestionAnswer(questionAnswer);			
    				result.setObserverNotes(observerNotes);        	   
    				result.setTitle(title); 
    				result.setDate(date);
    				result.setQuestionText(qText);
    				result.setSurveyId(surveyId);    
    		   				
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
		String separator1 = "/observernote/";
		String separator2 = "/answer/";
		
		for (Map.Entry<String, String> r : results.entrySet())
		{
			ret += r.getKey() + join + r.getValue() + "\n";
			//remove separator from string
			ret = ret.replaceAll(separator1, "");
			ret = ret.replaceAll(separator2, "");
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
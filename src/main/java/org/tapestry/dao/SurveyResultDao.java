package org.tapestry.dao;

import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.tapestry.objects.SurveyResult;

/**
 * SurveyTemplateDAO
 * Allow searching for appointments on the current date for a user,
 * all appointments for a user; adding new appointments.
 */
public class SurveyResultDao
{
	private PreparedStatement statement;
	private Connection con;
	
	/**
	* Constructor
	* @param url The URL of the database, prefixed with jdbc: (probably "jdbc:mysql://localhost:3306")
	* @param username The username of the database user
	* @param password The password of the database user
	*/
    public SurveyResultDao(String url, String username, String password){
    	try{
    		con = DriverManager.getConnection(url, username, password);
    	} catch (SQLException e){
    		System.out.println("Error: Could not connect to database");
    		e.printStackTrace();
    	}
    }
    
    public ArrayList<SurveyResult> getSurveysByPatientID(int patientId){
    	try {
			statement = con.prepareStatement("SELECT * FROM survey_results WHERE patient_ID=?");
			statement.setInt(1, patientId);
			ResultSet result = statement.executeQuery();
			ArrayList<SurveyResult> allSurveyResults = new ArrayList<SurveyResult>();
			while(result.next()){
				SurveyResult sr = createFromSearch(result);
				allSurveyResults.add(sr);
			}
			return allSurveyResults;
		} catch (SQLException e) {
			System.out.println("Error: could not retrieve survey templates");
			e.printStackTrace();
			return null;
		}
    }
    
    public SurveyResult getSurveyResultByID(int id){
    	try{
    		statement = con.prepareStatement("SELECT * FROM survey_results WHERE result_ID=?");
    		statement.setInt(1, id);
    		ResultSet result = statement.executeQuery();
    		return createFromSearch(result);
    	} catch (SQLException e){
    		System.out.println("Error: Could not retrieve survey result");
    		e.printStackTrace();
    		return null;
    	}
    }
    
	/**
	 * Ordered by title ascending
	 * @param itemsToReturn 
	 * @param startIndex 
	 */
	public ArrayList<SurveyResult> getAllSurveyResults()
	{
		try {
			statement = con.prepareStatement("SELECT * FROM survey_results");
			ResultSet result = statement.executeQuery();
			ArrayList<SurveyResult> allSurveyResults = new ArrayList<SurveyResult>();
			while(result.next()){
				SurveyResult sr = createFromSearch(result);
				allSurveyResults.add(sr);
			}
			return allSurveyResults;
		} catch (SQLException e) {
			System.out.println("Error: could not retrieve survey templates");
			e.printStackTrace();
			return null;
		}
	}

	/**
	* Creates a Survey Template object from a database query
	* @param result The ResultSet from the database query
	* @return The Patient object
	*/
	private SurveyResult createFromSearch(ResultSet result){
		SurveyResult sr = new SurveyResult();
		try{
            sr.setResultID(result.getInt("result_ID"));
            int surveyID = result.getInt("survey_ID");
            sr.setSurveyID(surveyID);
            //Look up the name of the survey
   			statement = con.prepareStatement("SELECT title FROM surveys WHERE survey_ID=?");
   			statement.setInt(1, surveyID);
   			ResultSet r = statement.executeQuery();
   			r.first();
   			sr.setSurveyTitle(r.getString("title"));
   			
   			int patientID = result.getInt("patient_ID");
   			sr.setPatientID(patientID);
			//Look up the name of the patient
   			statement = con.prepareStatement("SELECT firstname, lastname FROM patients WHERE patient_ID=?");
   			statement.setInt(1, patientID);
   			r = statement.executeQuery();
   			r.first();
   			sr.setPatientName(r.getString("firstname") + " " + r.getString("lastname"));
   			sr.setCompleted(result.getBoolean("completed"));
            sr.setStartDate(result.getString("startDate"));
            sr.setEditDate(result.getString("editDate"));
            sr.setResults(result.getBytes("data"));
		} catch (SQLException e) {
			System.out.println("Error: Failed to create Patient object");
			e.printStackTrace();
		}
		return sr;
	}
	
	/**
	 * Uploads a survey template to the database
	 * @param st
	 */
	public String assignSurvey(SurveyResult sr) {
		String resultId = null;
		try {
			statement = con.prepareStatement("INSERT INTO survey_results (patient_ID, survey_ID, data, editDate) values (?,?,?, now())");
			statement.setInt(1, sr.getPatientID());
			statement.setInt(2, sr.getSurveyID());
			statement.setBytes(3, sr.getResults());
			statement.execute();
			
			 //Look up the id of the new survey result
   			statement = con.prepareStatement("SELECT MAX(result_ID) AS result_ID FROM survey_results");
   			ResultSet r = statement.executeQuery();
   			r.first();
   			resultId = r.getString("result_ID");
		} catch (SQLException e){
			System.out.println("Error: Could not assign survey # " + sr.getSurveyID() + " to patient # " + sr.getPatientID());
			e.printStackTrace();
		}
		return resultId;
	}
	
	/**
	 * Delete a survey from the database
	 * @param id
	 */
	public void deleteSurvey(int id) {
		try {
			statement = con.prepareStatement("DELETE FROM survey_results WHERE result_ID=?");
			statement.setInt(1, id);
			statement.execute();
		} catch (SQLException e){
			System.out.println("Error: Could not delete survey");
			e.printStackTrace();
		}
	}
	
	/**
	 * Mark a survey as completed
	 * @param id
	 */
	public void markAsComplete(int id){
		try{
			statement = con.prepareStatement("UPDATE survey_results SET completed=1 WHERE result_ID=?");
			statement.setInt(1, id);
			statement.execute();
		} catch (SQLException e){
			System.out.println("Error: Could not mark survey as completed");
			e.printStackTrace();
		}
	}
}
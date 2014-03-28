package org.tapestry.dao;

import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import org.tapestry.objects.Narrative;

/**
 * 
 * @author lxie
 * 
 * NarrativeDAO
 * Allow searching for narratives for current user,
 * adding new narrative, modifying, and deleting a selected narrative.
 */

public class NarrativeDao {
	
	private PreparedStatement stmt;
	private Connection con;	
	private ResultSet rs;
	
	private static Logger logger = Logger.getLogger(NarrativeDao.class);
	
	/**  
	* Constructor
	* @param url The URL of the database, prefixed with jdbc: (probably "jdbc:mysql://localhost:3306/survey_app")
	* @param username The username of the database user
	* @param password The password of the database user
	*/
	public NarrativeDao(String url, String username, String password){
		try{
			con = DriverManager.getConnection(url, username, password);
		} catch (SQLException e){
			logger.error("Error from  NarrativeDao: Could not connect to database");
			System.out.println("Error from  NarrativeDao: Could not connect to database");
			e.printStackTrace();
		}
	}

	//get all narratives for volunteer by patient
	public List<Narrative> getAllNarrativesByUser(int userId, int patientId){
		
		List<Narrative> narratives = new ArrayList<Narrative>();
		
		try{
			stmt = con.prepareStatement("SELECT * FROM narratives WHERE user_ID=? AND patient_ID=? ORDER BY edit_Date DESC ");
			stmt.setInt(1, userId);
			stmt.setInt(2, patientId);
			rs = stmt.executeQuery();
			
			narratives = getNarrativesByResultSet(rs);
			
			if (rs != null)
				rs.close();			
			
		}catch (SQLException e){			
			logger.error("Error: Could not retrieve narratives");				
			e.printStackTrace();			
		}finally {
    		try{
    			//close  statement    			
    			if (stmt != null)
    				stmt.close();  
    		} catch (Exception e) {
    			//Ignore
    		}
    	}
				
		return narratives;
	}
	
	//get narrative for volunteer by patient, and appointment
	public List<Narrative> getAllNarrativesByUser(int userId, int patientId, int appointmentId){
		
		List<Narrative> narratives = new ArrayList<Narrative>();
		
		try{
			stmt = con.prepareStatement("SELECT * FROM narratives WHERE user_ID=? AND patient_ID=? AND appointment=? ORDER BY edit_Date DESC ");
			stmt.setInt(1, userId);
			stmt.setInt(2, patientId);
			stmt.setInt(3, appointmentId);
			rs = stmt.executeQuery();
			
			narratives = getNarrativesByResultSet(rs);
			
			if (rs != null)
				rs.close();
			
		}catch (SQLException e){			
			logger.error("Error: Could not retrieve narratives");				
			e.printStackTrace();			
		}finally {
    		try{
    			//close statement  
    			if (stmt != null)
    				stmt.close();  
    		} catch (Exception e) {
    			//Ignore
    		}
    	}
				
		return narratives;
	}
	
	//get narrative for selected ID
	public Narrative getNarrativeById(int narrativeId){
		Narrative narrative = null;
		List<Narrative> narratives = new ArrayList<Narrative>();
		
		try{
			stmt = con.prepareStatement("SELECT * FROM narratives WHERE narrative_ID=?");
			stmt.setInt(1, narrativeId);
			rs = stmt.executeQuery();
						
			narratives = getNarrativesByResultSet(rs);			
			narrative = narratives.get(0);
			
			if (rs != null)
				rs.close();
			
		}catch (SQLException e){
			logger.error("Error: Could not retrieve a narrative");				
			e.printStackTrace();
		}finally {
    		try{
    			//close statement   
    			if (stmt != null)
    				stmt.close();  
    		} catch (Exception e) {
    			//Ignore
    		}
		}
		
		return narrative;
	}
	
	public void addNarrative(Narrative narrative){
		try{
			stmt = con.prepareStatement("INSERT INTO narratives (title, contents, user_ID, edit_Date, patient_ID,"
					+ " appointment) VALUES (?, ?, ?, ?, ?, ?)");
			
			stmt.setString(1, narrative.getTitle());
			stmt.setString(2, narrative.getContents());
			stmt.setInt(3, narrative.getUserId());
			stmt.setString(4, narrative.getEditDate());
			stmt.setInt(5, narrative.getPatientId());
			stmt.setInt(6, narrative.getAppointmentId());
			
			stmt.execute();
		}catch (SQLException e){
			logger.error("Error: Could not create a narrative");		
			
			e.printStackTrace();
		}finally {
    		try{
    			//close statement    			
    			if (stmt != null)
    				stmt.close();  
    
    		} catch (Exception e) {
    			//Ignore
    		}
		}	
	}
	
	public void updateNarrative(Narrative narrative){
		try{
			stmt = con.prepareStatement("UPDATE narratives SET title=?,contents=?,edit_Date=? WHERE narrative_ID=?");
			
			stmt.setString(1, narrative.getTitle());
			stmt.setString(2, narrative.getContents());
			stmt.setString(3, narrative.getEditDate());
			stmt.setInt(4, narrative.getNarrativeId());
			
			stmt.execute();
		}catch (SQLException e){
			logger.error("Error: updating narrative is failed ");		
			
			e.printStackTrace();
		}finally {
    		try{
    			//close statement    			
    			if (stmt != null)
    				stmt.close();  
    
    		} catch (Exception e) {
    			//Ignore
    		}
		}
	}
	
	public void deleteNarrativeById(int narrativeId){
		try{
			stmt = con.prepareStatement("DELETE FROM narratives WHERE narrative_ID=?");
			stmt.setInt(1, narrativeId);
			stmt.execute();
		} catch (SQLException e){
			logger.error("Error: Could not delete narrative");			
			e.printStackTrace();
		} finally {
    		try{    			
    			//close statement
    			if (stmt != null)
    				stmt.close();  
    		} catch (Exception e) {
    			//Ignore
    		}
    	}
	}
	
	private List<Narrative> getNarrativesByResultSet(ResultSet results){
		List<Narrative> narratives = new ArrayList<Narrative>();
		Narrative narrative = null;		
		
		try{
			while(results.next())
			{
				narrative = new Narrative();	
				
				narrative.setNarrativeId(results.getInt("narrative_ID"));			
				narrative.setTitle(results.getString("title"));
				narrative.setContents(results.getString("contents"));
				
				if (results.getString("edit_Date") != null)
					narrative.setEditDate(results.getString("edit_Date").substring(0,10));

				narrative.setUserId(results.getInt("user_ID"));	
				narrative.setPatientId(results.getInt("patient_ID"));
				narrative.setAppointmentId(results.getInt("appointment"));
				
				narratives.add(narrative);
			}
						
		}catch (SQLException e){
			e.printStackTrace();
		}
		
		return narratives;		
	}
}

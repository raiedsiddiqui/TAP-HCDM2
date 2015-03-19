package org.tapestry.dao;

import java.util.List;

import org.tapestry.objects.Narrative;

/**
 * Defines DAO operations for the Narrative model.
 * 
 * @author lxie *
 */
public interface NarrativeDAO {
	/**
	 * 
	 * @param volunteerId
	 * @return
	 */
	public List<Narrative> getAllNarrativesByUser(int volunteerId);
	
	/**
	 * Retrieve narrativea for volunteer
	 * @param volunteerId
	 * @param patientId
	 * @param appointmentId
	 * @return
	 */
	public List<Narrative> getNarrativesByVolunteer(int volunteerId, int patientId, int appointmentId);
	
	/**
	 * retrieve narrative for volunteer
	 * @param narrativeId
	 * @return
	 */
	public Narrative getNarrativeById(int narrativeId);
	
	/**
	 * retrieve narrative for an appointment
	 * @param appointmentId
	 * @return
	 */
	public Narrative getNarrativeByAppointmentId(int appointmentId);
	
	/**
	 * create new narrative
	 * @param narrative
	 */
	public void addNarrative(Narrative narrative);
	
	/**
	 * Update a narrative
	 * @param narrative
	 */
	public void updateNarrative(Narrative narrative);
	
	/**
	 * Delete a narrative
	 * @param narrativeId
	 */
	public void deleteNarrativeById(int narrativeId);
	
	/**
	 * Save a copy of updated narrative
	 * @param n
	 * @param updatedBy
	 * @param whatAction
	 */
	public void archiveNarrative(Narrative n, String updatedBy, String whatAction);
}

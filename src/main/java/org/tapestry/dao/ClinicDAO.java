package org.tapestry.dao;

import java.util.List;
import org.tapestry.objects.Clinic;
/**
 * Defines DAO operations for the clinic model.
 * 
 * @author lxie 
 */

public interface ClinicDAO {
	/** 
	 * @return all clinics 
	 */
	public List<Clinic> getAllClinics();
	
	/**
	 * 
	 * @param siteId
	 * @return all clinics belong to a site
	 */
	public List<Clinic> getClinicsBySite(int siteId);
	
	/**
	 * 
	 * @param clinicId
	 * @return
	 */
	public Clinic getClinicById(int clinicId);
	
	/**
	 * 
	 * @param clinicId
	 * @return siteId
	 */
	public int getSiteByClinic(int clinicId);
	
	/**
	 * 
	 * @param clinic
	 */
	public void addClinic(Clinic clinic);
	
	/**
	 * 
	 * @param clinic
	 */
	public void modifyClinic(Clinic clinic);
	
	/**
	 * 
	 * @param clinicId
	 */
	public void deleteClinic(int clinicId);
}

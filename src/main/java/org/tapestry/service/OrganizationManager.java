package org.tapestry.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tapestry.objects.Clinic;
import org.tapestry.objects.Site;

/**
 * service for Model Clinic, Site
 * @author lxie 
 */
@Service
public interface OrganizationManager {
	
	/**
	 * 
	 * @return all clinics
	 */
	@Transactional
	public List<Clinic> getAllClinics();

	/**
	 * 
	 * @param siteId
	 * @return a list of clinics those belong to same site
	 */
	@Transactional
	public List<Clinic> getClinicsBySite(int siteId);
	
	/**
	 * 
	 * @param clinicId
	 * @return a clinic 
	 */
	@Transactional
	public Clinic getClinicById(int clinicId);
	
	/**
	 * 
	 * @param clinicId
	 * @return a site id
	 */
	@Transactional
	public int getSiteByClinic(int clinicId);
	
	/**
	 * add a new clinic
	 * @param clinic
	 */
	@Transactional
	public void addClinic(Clinic clinic);
	
	/**
	 * modify a clinic
	 * @param clinic
	 */
	@Transactional
	public void modifyClinic(Clinic clinic);
	
	/**
	 * delete a clinic
	 * @param clinicId
	 */
	@Transactional
	public void deleteClinic(int clinicId);
	
	/**
	 * 
	 * @return all sites
	 */
	@Transactional
	public List<Site> getAllSites();
	
	/**
	 * 
	 * @param siteId
	 * @return a site
	 */
	@Transactional
	public Site getSiteById(int siteId);
	
	/**
	 * add new site
	 * @param site
	 */
	@Transactional
	public void addSite(Site site);
	
	/**
	 * modify site
	 * @param site
	 */
	@Transactional
	public void modifySite(Site site);
	
	/**
	 * delete a site
	 * @param siteId
	 */
	@Transactional
	public void deleteSite(int siteId);
}

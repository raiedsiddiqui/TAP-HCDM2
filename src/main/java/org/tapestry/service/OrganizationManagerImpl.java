package org.tapestry.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tapestry.dao.ClinicDAO;
import org.tapestry.dao.SiteDAO;
import org.tapestry.objects.Clinic;
import org.tapestry.objects.Site;

/**
 * Implementation for service OrganizationManager
 * @author lxie 
 */
@Service
public class OrganizationManagerImpl implements OrganizationManager {
	@Autowired
	private ClinicDAO clinicDao;
	
	@Autowired
	private SiteDAO siteDao;
	
	@Override
	public List<Clinic> getAllClinics(){
		return clinicDao.getAllClinics();
	}
	
	@Override
	public List<Clinic> getClinicsBySite(int siteId) {		
		return clinicDao.getClinicsBySite(siteId);
	}

	@Override
	public Clinic getClinicById(int clinicId) {
		return clinicDao.getClinicById(clinicId);
	}
	

	@Override
	public int getSiteByClinic(int clinicId) {		
		return clinicDao.getSiteByClinic(clinicId);
	}

	@Override
	public void addClinic(Clinic clinic) {
		clinicDao.addClinic(clinic);

	}

	@Override
	public void modifyClinic(Clinic clinic) {
		clinicDao.modifyClinic(clinic);

	}

	@Override
	public void deleteClinic(int clinicId) {
		clinicDao.deleteClinic(clinicId);

	}

	//============ Site =============
	@Override
	public List<Site> getAllSites() {
		return siteDao.getAllSites();
	}

	@Override
	public Site getSiteById(int siteId) {
		return siteDao.getSiteById(siteId);
	}

	@Override
	public void addSite(Site site) {
		siteDao.addSite(site);		
	}

	@Override
	public void modifySite(Site site) {
		siteDao.modifySite(site);		
	}

	@Override
	public void deleteSite(int siteId) {
		siteDao.deleteSite(siteId);		
	}


}

package org.tapestry.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tapestry.dao.PreferenceDAO;
import org.tapestry.objects.Preference;

/**
 * Implementation for service PreferenceManager
 * @author lxie 
 */
@Service
public class PreferenceServiceImpl implements PreferenceManager {
	@Autowired
	private PreferenceDAO preferenceDao;
	
	@Override
	public Preference getPreferenceBySite(int site) {		
		return preferenceDao.getPreferenceBySite(site);
	}

	@Override
	public void addPreference(Preference preference) {
		preferenceDao.addPreference(preference);

	}

	@Override
	public void updatePreference(Preference preference) {
		preferenceDao.updatePreference(preference);
	}

	@Override
	public String getSosReciversBySite(int site) {
		return preferenceDao.getSosReciversBySite(site);
	}

	@Override
	public String getApptNotiReceiversBySite(int site) {		
		return preferenceDao.getApptNotiReceiversBySite(site);
	}

	@Override
	public String getReportNotiReceiversBySite(int site) {
		return preferenceDao.getReportNotiReceiversBySite(site);
	}

	@Override
	public String getSocialContextOnReportBySite(int site) {
		return preferenceDao.getSocialContextOnReportBySite(site);
	}

	@Override
	public String getSocialContextTemplateBySite(int site) {
		return preferenceDao.getSocialContextTemplateBySite(site);
	}

	@Override
	public String getAlertsOnReportBySite(int site) {
		return preferenceDao.getAlertsOnReportBySite(site);
	}

	@Override
	public String getAlertsContentBySite(int site) {
		return preferenceDao.getAlertsContentBySite(site);
	}

}

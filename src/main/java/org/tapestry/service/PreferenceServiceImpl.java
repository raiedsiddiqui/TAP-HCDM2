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

}

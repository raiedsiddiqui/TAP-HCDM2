package org.tapestry.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tapestry.objects.Preference;

@Service
public interface PreferenceManager {
	
	@Transactional
	public Preference getPreferenceBySite(int site);
	
	@Transactional
	public void addPreference(Preference preference);
	
	@Transactional
	public void updatePreference(Preference preference);

}

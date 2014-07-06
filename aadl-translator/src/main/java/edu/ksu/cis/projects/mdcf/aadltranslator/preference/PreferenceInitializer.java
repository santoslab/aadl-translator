package edu.ksu.cis.projects.mdcf.aadltranslator.preference;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import edu.ksu.cis.projects.mdcf.aadltranslator.AadlTranslatorPlugin;

/**
 * Class used to initialize default preference values.
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	public void initializeDefaultPreferences() {
		IPreferenceStore store = AadlTranslatorPlugin.getDefault().getPreferenceStore();
		store.setDefault(PreferenceConstants.P_USERSHELLS, true);
		store.setDefault(PreferenceConstants.P_REPORTFORMAT, "markdown");
	}

}

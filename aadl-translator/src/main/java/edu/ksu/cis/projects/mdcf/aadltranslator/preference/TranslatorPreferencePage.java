package edu.ksu.cis.projects.mdcf.aadltranslator.preference;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import edu.ksu.cis.projects.mdcf.aadltranslator.AadlTranslatorPlugin;

/**
 * This class represents a preference page that is contributed to the
 * Preferences dialog. By subclassing <samp>FieldEditorPreferencePage</samp>, we
 * can use the field support built into JFace that allows us to create a page
 * that is small and knows how to save, restore and apply itself.
 * <p>
 * This page is used to modify preferences only. They are stored in the
 * preference store that belongs to the main plug-in class. That way,
 * preferences can be accessed directly via the preference store.
 */

public class TranslatorPreferencePage extends FieldEditorPreferencePage
		implements IWorkbenchPreferencePage {

	public TranslatorPreferencePage() {
		super(GRID);
		setPreferenceStore(AadlTranslatorPlugin.getDefault()
				.getPreferenceStore());
		setDescription("Preferences for the AADL to Java / MIDAS translator");
	}

	/**
	 * Creates the field editors. Field editors are abstractions of the common
	 * GUI blocks needed to manipulate various types of preferences. Each field
	 * editor knows how to save and restore itself.
	 */
	public void createFieldEditors() {
		DirectoryFieldEditor appDevPathField = new DirectoryFieldEditor(
				PreferenceConstants.P_APPDEVPATH, "&AppDev Directory:",
				getFieldEditorParent());
		addField(appDevPathField);
		
		BooleanFieldEditor userShellsField = new BooleanFieldEditor(
				PreferenceConstants.P_USERSHELLS, "Generate &User Shells:",
				BooleanFieldEditor.SEPARATE_LABEL, getFieldEditorParent());
		addField(userShellsField);
		
		// First field: Displayed value, second field: stored value
		String[][] reportFormatOptions = new String[][] {{"html", "HTML"}, {"markdown", "MARKDOWN"}}; //{"pdf", "PDF"}, 
		ComboFieldEditor formatOptionsField = new ComboFieldEditor(
				PreferenceConstants.P_REPORTFORMAT, "Report &Format:",
				reportFormatOptions, getFieldEditorParent());
		addField(formatOptionsField);
		
		FileFieldEditor pandocPathField = new FileFieldEditor(
				PreferenceConstants.P_PANDOCPATH, "Path to &Pandoc Executable:",
				getFieldEditorParent());
		addField(pandocPathField);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
	}

}
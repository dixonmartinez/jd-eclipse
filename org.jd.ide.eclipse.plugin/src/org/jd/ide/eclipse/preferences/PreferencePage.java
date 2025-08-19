/*
 * Copyright (c) 2008, 2019 Emmanuel Dupuy.
 * This project is distributed under the GPLv3 license.
 * This is a Copyleft license that gives the user the right to use,
 * copy and modify the code freely for non-commercial purposes.
 */

package org.jd.ide.eclipse.preferences;

import java.util.List;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.jd.ide.eclipse.JavaDecompilerPlugin;

/**
 * PreferencePage
 * 
 * @project Java Decompiler Eclipse Plugin
 * @version 0.1.3
 */
public class PreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {
	public PreferencePage() {
		super(GRID);
		setDescription("JD-Eclipse preference page");
	}

	/**
	 * Creates the field editors. Field editors are abstractions of the common GUI
	 * blocks needed to manipulate various types of preferences. Each field editor
	 * knows how to save and restore itself.
	 */
	public void createFieldEditors() {
		Composite fieldEditorParent = getFieldEditorParent();

		new Label(fieldEditorParent, SWT.NONE);
		createBooleanPreferences(fieldEditorParent);
		
	}
	
	private void createBooleanPreferences(Composite fieldEditorParent) {
	    List<PreferenceOption> options = List.of(
	        new PreferenceOption(JavaDecompilerPlugin.PREF_ESCAPE_UNICODE_CHARACTERS,
	            "Escape unicode characters",
	            "Convierte caracteres especiales a formato Unicode (ej. é → \\u00E9)"),

	        new PreferenceOption(JavaDecompilerPlugin.PREF_REALIGN_LINE_NUMBERS,
	            "Realign line numbers",
	            "Reajusta los números de línea para que coincidan con el código original"),

	        new PreferenceOption(JavaDecompilerPlugin.PREF_SHOW_LINE_NUMBERS,
	            "Show original line numbers",
	            "Muestra los números de línea del archivo .class original"),

	        new PreferenceOption(JavaDecompilerPlugin.PREF_SHOW_METADATA,
	            "Show metadata",
	            "Incluye información adicional como versión de clase, constantes, etc."),

	        new PreferenceOption(JavaDecompilerPlugin.PREF_USE_AS_DEFAULT_EDITOR,
	            "Use this decompiler as default editor",
	            "Establece este editor como el predeterminado para abrir archivos .class")
	    );

	    for (PreferenceOption option : options) {
	        BooleanFieldEditor editor = new BooleanFieldEditor(option.key, option.label, fieldEditorParent);
	        editor.getDescriptionControl(getFieldEditorParent()).setToolTipText(option.tooltip);
	        addField(editor);
	    }
	}

	/**
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
		setPreferenceStore(JavaDecompilerPlugin.getDefault().getPreferenceStore());
	}
	
	class PreferenceOption {
	    public final String key;
	    public final String label;
	    public final String tooltip;

	    public PreferenceOption(String key, String label, String tooltip) {
	        this.key = key;
	        this.label = label;
	        this.tooltip = tooltip;
	    }
	}
}
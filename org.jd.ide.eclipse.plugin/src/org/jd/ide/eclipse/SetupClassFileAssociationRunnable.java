package org.jd.ide.eclipse;

import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IFileEditorMapping;
import org.eclipse.ui.internal.WorkbenchPlugin;
import org.eclipse.ui.internal.registry.EditorDescriptor;
import org.eclipse.ui.internal.registry.EditorRegistry;
import org.eclipse.ui.internal.registry.FileEditorMapping;

public class SetupClassFileAssociationRunnable implements Runnable {
	
	public void run() {
		EditorRegistry registry = (EditorRegistry)WorkbenchPlugin.getDefault().getEditorRegistry();
		
		IFileEditorMapping[] mappings = registry.getFileEditorMappings();
		IFileEditorMapping c = null;
		IFileEditorMapping cws = null;
		
		// Search Class file editor mappings
		for (IFileEditorMapping mapping : mappings) {
			if (mapping.getExtension().equals("class")) {
				// ... Helios 3.6, Indigo 3.7, Juno 4.2, Kepler 4.3, ...
				c = mapping;
			} else if (mapping.getExtension().equals("class without source")) {
				// Juno 4.2, Kepler 4.3, ...
				cws = mapping;
			}
		}

		if ((c != null) && (cws != null)) {
			// Search JD editor descriptor on "class" extension
			for (IEditorDescriptor descriptor : c.getEditors()) {		
				if (descriptor.getId().equals(JavaDecompilerPlugin.EDITOR_ID)) {
					// Remove JD editor on "class" extension
					((FileEditorMapping)c).removeEditor((EditorDescriptor)descriptor);

					// Set JD as default editor on "class without source" extension
					registry.setDefaultEditor("." + cws.getExtension(), descriptor.getId());
					break;
				}
			}
			
			// Restore the default editor for "class" extension
			IEditorDescriptor defaultClassFileEditor = registry.findEditor(JavaUI.ID_CF_EDITOR);
			
			if (defaultClassFileEditor != null) {
				registry.setDefaultEditor("." + c.getExtension(), JavaUI.ID_CF_EDITOR);
			}				
			
			registry.setFileEditorMappings((FileEditorMapping[]) mappings);
			registry.saveAssociations();			
		}
	}
}
package org.jd.ide.eclipse;

import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorRegistry;
import org.eclipse.ui.PlatformUI;

public class SetupClassFileAssociationRunnable implements Runnable {
	
	private static final String CLASS_EXTENSION = "class";
    private static final String JD_EDITOR_ID = JavaDecompilerPlugin.EDITOR_ID;

    private IEditorDescriptor findEditorById(IEditorDescriptor[] editors, String id) {
        for (IEditorDescriptor editor : editors) {
            if (editor.getId().equals(id)) {
                return editor;
            }
        }
        return null;
    }
	
	@Override
    public void run() {
        IEditorRegistry registry = PlatformUI.getWorkbench().getEditorRegistry();
        if (registry == null) {
            System.err.println("EditorRegistry not available.");
            return;
        }

        IEditorDescriptor[] editors = registry.getEditors("." + CLASS_EXTENSION);
        IEditorDescriptor jdEditor = findEditorById(editors, JD_EDITOR_ID);

        if (jdEditor != null) {
            System.out.println("JD Editor found: " + jdEditor.getLabel());

            // Set JD editor as default for .class files
            registry.setDefaultEditor("." + CLASS_EXTENSION, JD_EDITOR_ID);

            // Optionally restore JavaUI default if needed
            IEditorDescriptor defaultEditor = registry.findEditor(JavaUI.ID_CF_EDITOR);
            if (defaultEditor != null && !defaultEditor.getId().equals(JD_EDITOR_ID)) {
                System.out.println("Restoring default editor: " + defaultEditor.getLabel());
                registry.setDefaultEditor("." + CLASS_EXTENSION, JavaUI.ID_CF_EDITOR);
            }
        } else {
            System.out.println("JD Editor not found among registered editors.");
        }
    }
}
/*
 * Copyright (c) 2008, 2019 Emmanuel Dupuy.
 * This project is distributed under the GPLv3 license.
 * This is a Copyleft license that gives the user the right to use,
 * copy and modify the code freely for non-commercial purposes.
 */

package org.jd.ide.eclipse.editors;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IBuffer;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.internal.core.BufferManager;
import org.eclipse.jdt.internal.core.PackageFragmentRoot;
import org.eclipse.jdt.internal.ui.javaeditor.IClassFileEditorInput;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.editors.text.TextEditor;
import org.jd.ide.eclipse.JavaDecompilerPlugin;
import org.jd.ide.eclipse.util.MessageUtils;

/**
 * JDClassFileEditor
 * 
 * @project Java Decompiler Eclipse Plugin
 * @version 0.1.4
 */
public class JDClassFileEditor extends TextEditor implements IPropertyChangeListener {
	public JDClassFileEditor() {
		super();
		// Configura el Document Provider en el constructor
//		setDocumentProvider(new JDDocumentProvider());
		JavaDecompilerPlugin.getDefault().getPreferenceStore().addPropertyChangeListener(this);
	}

	@Override
	protected void doSetInput(IEditorInput input) throws CoreException {
		IClassFile classFile = null;
		if (input instanceof IFileEditorInput) {
			IFile file = ((IFileEditorInput) input).getFile();

			if (file instanceof IClassFile) {
				classFile = (IClassFile) file;
			}
		}

		if (input instanceof IClassFileEditorInput classFileEditorInput) {
			classFile = classFileEditorInput.getClassFile();
		}

		if (classFile == null || classFile.getJavaProject() == null || !classFile.exists()) {
			// Asumiendo que tienes una clase de utilidad para mensajes
			throw new CoreException(
					new Status(Status.ERROR, JavaDecompilerPlugin.PLUGIN_ID, MessageUtils.getString("editor.error.notInClasspath")));
		}
		cleanupBuffer(classFile);
		setupSourceMapper(classFile);

		super.doSetInput(input);
	}

	protected static void cleanupBuffer(IClassFile file) {
		IBuffer buffer = BufferManager.getDefaultBufferManager().getBuffer(file);

		if (buffer != null) {
			// ADVERTENCIA: Se usa reflexión para acceder a un método interno de Eclipse.
			// Esto podría romperse en futuras versiones del IDE. Se hace para
			// eliminar un búfer de clase y forzar la recarga del contenido decompilado.
			// Se debe buscar una alternativa en la API pública si es posible.
			try {
				// Remove the buffer
				Method method = BufferManager.class.getDeclaredMethod("removeBuffer", new Class[] { IBuffer.class });
				method.setAccessible(true);
				method.invoke(BufferManager.getDefaultBufferManager(), new Object[] { buffer });
			} catch (Exception e) {
				JavaDecompilerPlugin.getDefault().getLog()
						.log(new Status(Status.ERROR, JavaDecompilerPlugin.PLUGIN_ID, 0, e.getMessage(), e));
			}
		}
	}

	protected void setupSourceMapper(IClassFile classFile) {
		try {
			// Search package fragment root and classPath
			IJavaElement packageFragment = classFile.getParent();
			IJavaElement packageFragmentRoot = packageFragment.getParent();

			if (packageFragmentRoot instanceof PackageFragmentRoot) {
				// Setup a new source mapper.
				PackageFragmentRoot root = (PackageFragmentRoot) packageFragmentRoot;

				// Location of the archive file containing classes.
				IPath basePath = root.getPath();
				File baseFile = basePath.makeAbsolute().toFile();

				if (!baseFile.exists()) {
					IResource resource = root.getCorrespondingResource();
					basePath = resource.getLocation();
					baseFile = basePath.makeAbsolute().toFile();
				}

				// Class path
				String classPath = classFile.getElementName();
				String packageName = packageFragment.getElementName();
				if ((packageName != null) && (packageName.length() > 0)) {
					classPath = packageName.replace('.', '/') + '/' + classPath;
				}

				// Location of the archive file containing source.
				IPath sourcePath = root.getSourceAttachmentPath();
				if (sourcePath == null) {
					sourcePath = basePath;
				}

				// Location of the package fragment root within the zip
				// (empty specifies the default root).
				IPath sourceAttachmentRootPath = root.getSourceAttachmentRootPath();
				String sourceRootPath;

				if (sourceAttachmentRootPath == null) {
					sourceRootPath = null;
				} else {
					sourceRootPath = sourceAttachmentRootPath.toString();
					if ((sourceRootPath != null) && (sourceRootPath.length() == 0))
						sourceRootPath = null;
				}

				// Options
				Map<String, String> options = root.getJavaProject().getOptions(true);

				root.setSourceMapper(new JDSourceMapper(baseFile, sourcePath, sourceRootPath, options));
			}
		} catch (CoreException e) {
			JavaDecompilerPlugin.getDefault().getLog()
					.log(new Status(Status.ERROR, JavaDecompilerPlugin.PLUGIN_ID, 0, e.getMessage(), e));
		}
	}

	@Override
	public boolean isEditable() {
		return false;
	}

	@Override
	public boolean isDirty() {
		return false;
	}

	@Override
	public boolean isEditorInputReadOnly() {
		return false;
	}

	@Override
	public void dispose() {
		JavaDecompilerPlugin.getDefault().getPreferenceStore().removePropertyChangeListener(this);
	}

	/**
	 * Refresh decompiled source code.
	 * 
	 * @see org.eclipse.jface.util.IPropertyChangeListener#propertyChange(org.eclipse.jface.util.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent event) {
		String property = event.getProperty();

		if (isRelevantPreference(property) && getEditorInput() != null) {
			try {
//				if (getDocumentProvider() instanceof JDDocumentProvider)
//					return;

				setInput(getEditorInput()); // Esto vuelve a ejecutar doSetInput
			} catch (Exception e) {
				JavaDecompilerPlugin.getDefault().getLog().log(new Status(Status.ERROR, JavaDecompilerPlugin.PLUGIN_ID,
						0, "Error al refrescar el editor JD", e));
			}
		}
	}

	private boolean isRelevantPreference(String property) {
		return property.equals(JavaDecompilerPlugin.PREF_ESCAPE_UNICODE_CHARACTERS)
				|| property.equals(JavaDecompilerPlugin.PREF_REALIGN_LINE_NUMBERS)
				|| property.equals(JavaDecompilerPlugin.PREF_SHOW_LINE_NUMBERS)
				|| property.equals(JavaDecompilerPlugin.PREF_SHOW_METADATA);
	}
}

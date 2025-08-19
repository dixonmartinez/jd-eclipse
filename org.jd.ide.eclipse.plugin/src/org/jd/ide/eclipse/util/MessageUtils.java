package org.jd.ide.eclipse.util;

import org.eclipse.osgi.util.NLS;

/**
 * Utility class for internationalizing messages.
 * This class loads messages from a properties file and makes them available
 * through static fields.
 */
public class MessageUtils extends NLS {
    private static final String BUNDLE_NAME = "org.jd.ide.eclipse.util.messages"; // El nombre del archivo de propiedades

    static {
        // Inicializa los campos estáticos
        NLS.initializeMessages(BUNDLE_NAME, MessageUtils.class);
    }
    
    // Declara los campos estáticos para cada mensaje en el archivo de propiedades
    public static String editor_error_notInClasspath;
    public static String editor_error_to_decompile;

    private MessageUtils() {
        // Constructor privado para evitar instanciación
    }

    /**
     * Retrieves the string for the given key.
     * @param key The key to look up in the messages file.
     * @return The message string, or a default string if not found.
     */
    public static String getString(String key, Object... args) {
        return NLS.bind(key, args);
    }
}

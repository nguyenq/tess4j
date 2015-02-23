/**
 * Copyright @ 2014 Quan Nguyen
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package net.sourceforge.tess4j.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Logger;
import java.util.logging.Level;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import com.sun.jna.Native;
import com.sun.jna.Platform;

import net.sourceforge.tess4j.TessAPI;

/**
 * Loads native libraries from JAR or project folder.
 *
 * @author O.J. Sousa Rodrigues
 * @author Quan Nguyen
 */
public class LoadLibs {

    private static final String JNA_LIBRARY_PATH = "jna.library.path";
    public static final String TESS4J_TEMP_DIR = new File(System.getProperty("java.io.tmpdir"), "tess4j").getPath();

    /**
     * Native library name.
     */
    public static final String LIB_NAME = "libtesseract303";
    public static final String LIB_NAME_NON_WIN = "tesseract";

    private final static Logger logger = Logger.getLogger(LoadLibs.class.getName());

    static {
        System.setProperty("jna.encoding", "UTF8");
        File targetTempFolder = extractTessResources(Platform.RESOURCE_PREFIX);
        if (targetTempFolder != null && targetTempFolder.exists()) {
            String userCustomizedPath = System.getProperty(JNA_LIBRARY_PATH);
            if (null == userCustomizedPath || userCustomizedPath.isEmpty()) {
                System.setProperty(JNA_LIBRARY_PATH, targetTempFolder.getPath());
            } else {
                System.setProperty(JNA_LIBRARY_PATH, userCustomizedPath + File.pathSeparator + targetTempFolder.getPath());
            }
        }
    }

    /**
     * Loads Tesseract library via JNA.
     *
     * @return TessAPI instance being loaded using
     * <code>Native.loadLibrary()</code>.
     */
    public static TessAPI getTessAPIInstance() {
        return (TessAPI) Native.loadLibrary(getTesseractLibName(), TessAPI.class);
    }

    /**
     * Gets native library name.
     *
     * @return the name of the tesseract library to be loaded using the
     * <code>Native.register()</code>.
     */
    public static String getTesseractLibName() {
        return Platform.isWindows() ? LIB_NAME : LIB_NAME_NON_WIN;
    }

    /**
     * Extracts tesseract resources to temp folder.
     *
     * @param resourceName name of file or directory
     * @return target path, which could be file or directory
     */
    public static File extractTessResources(String resourceName) {
        File targetPath = null;

        try {
            targetPath = new File(TESS4J_TEMP_DIR, resourceName);

            URL tessResourceUrl = LoadLibs.class.getResource(resourceName.startsWith("/") ? resourceName : "/" + resourceName);
            if (tessResourceUrl == null) {
                return null;
            }

            URLConnection urlConnection = tessResourceUrl.openConnection();

            /**
             * Copy resources either from inside jar or from project folder.
             */
            if (urlConnection instanceof JarURLConnection) {
                copyJarResourceToPath((JarURLConnection) urlConnection, targetPath);
            } else {
                File file = new File(tessResourceUrl.getPath());
                if (file.isDirectory()) {
                    FileUtils.copyDirectory(file, targetPath);
                } else {
                    FileUtils.copyFile(file, targetPath);
                }
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }

        return targetPath;
    }

    /**
     * Copies resources from the jar file of the current thread and extract it
     * to the destination path.
     *
     * @param jarConnection
     * @param destPath destination file or directory
     */
    static void copyJarResourceToPath(JarURLConnection jarConnection, File destPath) {
        try {
            JarFile jarFile = jarConnection.getJarFile();
            String jarConnectionEntryName = jarConnection.getEntryName();

            /**
             * Iterate all entries in the jar file.
             */
            for (Enumeration<JarEntry> e = jarFile.entries(); e.hasMoreElements();) {
                JarEntry jarEntry = e.nextElement();
                String jarEntryName = jarEntry.getName();

                /**
                 * Extract files only if they match the path.
                 */
                if (jarEntryName.startsWith(jarConnectionEntryName)) {
                    String filename = jarEntryName.substring(jarConnectionEntryName.length());
                    File currentFile = new File(destPath, filename);

                    if (jarEntry.isDirectory()) {
                        currentFile.mkdirs();
                    } else {
                        currentFile.deleteOnExit();
                        InputStream is = jarFile.getInputStream(jarEntry);
                        OutputStream out = FileUtils.openOutputStream(currentFile);
                        IOUtils.copy(is, out);
                        is.close();
                        out.close();
                    }
                }
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
    }
}

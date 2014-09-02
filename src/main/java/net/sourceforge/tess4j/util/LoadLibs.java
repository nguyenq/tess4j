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

import net.sourceforge.tess4j.TessAPI;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import com.sun.jna.Native;

public enum LoadLibs {

    INSTANCE;

    private TessAPI      api                     = null;
    private OSLibs       os                      = null;
    private final String DEFAULT_TESSDATA_FOLDER = "/tessdata";

    private LoadLibs() {

        System.setProperty("jna.encoding", "UTF8");

        if (System.getProperty("os.name").toLowerCase().contains("windows")) {

            if ("64".equalsIgnoreCase(System.getProperty("sun.arch.data.model"))) {
                os = new OSLibsWin64();
            }

            if ("32".equalsIgnoreCase(System.getProperty("sun.arch.data.model"))) {
                os = new OSLibsWin32();
            }
        }
        
        
        loadLibs();
    }

    /**
     * Will load the tesseract library using Native.loadLibrary().
     * @return TessAPI instance being loaded using the Native.loadLibrary().
     */
    public TessAPI getTessAPIInstance() {
        api = (TessAPI) Native.loadLibrary(os.getLibTesseract(), TessAPI.class);
        return api;
    }
    
    /**
     * 
     * @return the name of the tesseract library to be loaded using the Native.register().
     */
    public String getTesseractLibName() {
        return os.getLibTesseract();
    }
    
    /**
     * This method will, extract the libraries from the current jar into the
     * operating system temporary folder and load the libraries making them available
     * for the JVM.
     */
    private void loadLibs() {

        if (null == api) {

            try {

                for (String fileName : os.getLibsToLoad()) {

                    /**
                     * OS library being loaded from resources.
                     */
                    String rscFilePath = String.format("/%s/%s", os.getOsArchFolder(), fileName);
                    InputStream in = this.getClass().getResourceAsStream(rscFilePath);

                    /**
                     * Temporary files being set to be copied.
                     */
                    String tmpFilePath = String.format("%s/%s", os.getTess4jArchTempFolder(), fileName);
                    File tmpFile = new File(tmpFilePath);

                    if (!tmpFile.exists()) {

                        OutputStream out = FileUtils.openOutputStream(tmpFile);
                        IOUtils.copy(in, out);
                        in.close();
                        out.close();
                    }

                    /**
                     * Making the library available to the JVM.
                     */
                    System.load(tmpFile.getAbsolutePath());
                }

                

            } catch (IOException e) {
                // TODO add logger
                System.out.println(e.getMessage());
                e.printStackTrace();
            } catch (UnsatisfiedLinkError e) {
                // TODO add logger
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
        }

    }

    
    /**
     * This method will load the tessdata folder from resources and copy it into the temporary folder.
     */
    public File loadDefaultTessDataFolder() {

        File targetTempFolder = null;

        try {

            /**
             * Target temporary tessdata folder.
             */
            String targetTempFolderPath = String.format("%s/%s", os.TESS4J_TEMP_PATH, DEFAULT_TESSDATA_FOLDER);
            targetTempFolder = new File(targetTempFolderPath);


            URL tessDataFolderUrl = getClass().getResource(DEFAULT_TESSDATA_FOLDER);
            URLConnection urlConnection = tessDataFolderUrl.openConnection();

            /**
             * Either load from resources from jar or project resource folder.
             */
            if (urlConnection instanceof JarURLConnection) {
                copyJarResourceToFolder((JarURLConnection) urlConnection, targetTempFolder);
            } else {
                FileUtils.copyDirectory(new File(tessDataFolderUrl.getPath()), targetTempFolder);
            }

        } catch (Exception e) {
            // TODO add logger
            e.printStackTrace();
        }

        return targetTempFolder;
    }
    
    

    /**
     * This method will copy resources from the jar file of the current thread and extract it to the destination folder.
     * 
     * @param jarConnection
     * @param destDir
     * @throws IOException
     */
    public void copyJarResourceToFolder(JarURLConnection jarConnection, File destDir) {

        try {
            JarFile jarFile = jarConnection.getJarFile();

            /**
             * Iterate all entries in the jar file.
             */
            for (Enumeration<JarEntry> e = jarFile.entries(); e.hasMoreElements();) {

                JarEntry jarEntry = e.nextElement();
                String jarEntryName = jarEntry.getName();
                String jarConnectionEntryName = jarConnection.getEntryName();

                /**
                 * Extract files only if they match the path.
                 */
                if (jarEntryName.startsWith(jarConnectionEntryName)) {

                    String filename = jarEntryName.startsWith(jarConnectionEntryName) ? jarEntryName.substring(jarConnectionEntryName.length()) : jarEntryName;
                    File currentFile = new File(destDir, filename);

                    if (jarEntry.isDirectory()) {
                        currentFile.mkdirs();
                    } else {
                        InputStream is = jarFile.getInputStream(jarEntry);
                        OutputStream out = FileUtils.openOutputStream(currentFile);
                        IOUtils.copy(is, out);
                        is.close();
                        out.close();
                    }
                }
            }
        } catch (IOException e) {
            // TODO add logger
            e.printStackTrace();
        }

    }

}

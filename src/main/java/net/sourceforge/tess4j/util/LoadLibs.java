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

import net.sourceforge.tess4j.TessAPI;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import com.sun.jna.Native;

public enum LoadLibs {

    INSTANCE;

    private TessAPI api = null;

    /**
     * Native library name.
     */

    public TessAPI getTessAPIInstance() {

        System.setProperty("jna.encoding", "UTF8");

        OSLibs os = null;

        if (System.getProperty("os.name").toLowerCase().contains("windows")) {
            
            if ("64".equalsIgnoreCase(System.getProperty("sun.arch.data.model"))) {
                os = new OSLibsWin64();
            }
            
            if ("32".equalsIgnoreCase(System.getProperty("sun.arch.data.model"))) {
                os = new OSLibsWin32();
            }
        }

        if (null == api) {

            try {

                for (String fileName : os.getLibsToLoad()) {

                    String rscFilePath = String.format("/%s/%s", os.getOsArchFolder(), fileName);
                    String tmpFilePath = String.format("%s/%s", os.getTess4jTempFolder(), fileName);

                    InputStream in = this.getClass().getResourceAsStream(rscFilePath);
                    File tmpFile = new File(tmpFilePath);

                    if (!tmpFile.exists()) {

                        OutputStream out = FileUtils.openOutputStream(tmpFile);
                        IOUtils.copy(in, out);
                        in.close();
                        out.close();
                    }

                    System.load(tmpFile.getAbsolutePath());
                }

                api = (TessAPI) Native.loadLibrary(os.getLibTesseract(), TessAPI.class);

            } catch (IOException e) {
                // TODO add logger
                System.out.println(e.getMessage());
            } catch (UnsatisfiedLinkError e) {
                // TODO add logger
                System.out.println(e.getMessage());
            }
        }

        return api;
    }
}
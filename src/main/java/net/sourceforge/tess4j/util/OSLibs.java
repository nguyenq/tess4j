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

import java.util.List;

/**
 * Abstract class to support n operating systems.
 */
public abstract class OSLibs {
    
    static final String TESS4JPATH = "tess4j.tmp";

    /**
     * @return the operating system architecture folder
     */
    abstract String getOsArchFolder();

    /**
     * @return the name of the tesseract library for this operating system.
     */
    abstract String getLibTesseract();

    /**
     * @return the name of the leptonica library for this operating system.
     */
    abstract String getLibLeptonica();

    /**
     * @return the name of the ghost-script library for this operating system.
     */
    abstract String getLibGhostScript();

    /**
     * @return the path of the temporary folder where the libraries will be extracted to.
     */
    abstract String getTess4jTempFolder();

    /**
     * @return a list with the names of the libraries to be loaded.
     */
    abstract List<String> getLibsToLoad();

}

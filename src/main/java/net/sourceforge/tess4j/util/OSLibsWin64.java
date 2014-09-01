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

import java.util.ArrayList;
import java.util.List;

/**
 * Windows x64 bit libraries information.
 */
public class OSLibsWin64 extends OSLibs {

    private List<String>        list       = null;

    @Override
    public String getOsArchFolder() {
        return "win32-x86-64";
    }

    @Override
    public String getLibTesseract() {
        return "libtesseract303.dll";
    }

    @Override
    public String getLibLeptonica() {
        return "liblept170.dll";
    }

    @Override
    public String getLibGhostScript() {
        return "gsdll64.dll";
    }


    @Override
    public List<String> getLibsToLoad() {

        if (null == list) {
            list = new ArrayList<String>();
            list.add(getLibGhostScript());
            list.add(getLibLeptonica());
            list.add(getLibTesseract());
        }

        return list;
    }

}
/**
 * Copyright @ 2024 Quan Nguyen
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
package net.sourceforge.tess4j;

/**
 * Encapsulates Tesseract Orientation Script Detection (OSD) results.
 */
public class OSDResult {

    private final int orientDeg;
    private final float orientConf;
    private final String scriptName;
    private final float scriptConf;

    /**
     * Default constructor.
     */
    public OSDResult() {  
        this(0, 0, "", 0);
    }
    
    /**
     * Constructor.
     *
     * @param orientDeg the detected clockwise rotation of the input image in degrees (0, 90, 180, 270)
     * @param orientConf confidence in the orientation (15.0 is reasonably confident)
     * @param scriptName the name of the script
     * @param scriptConf confidence level in the script
     */
    public OSDResult(int orientDeg, float orientConf, String scriptName, float scriptConf) {
        this.orientDeg = orientDeg;
        this.orientConf = orientConf;
        this.scriptName = scriptName;
        this.scriptConf = scriptConf;
    }

    /**
     *
     * @return the orientDeg
     */
    public int getOrientDeg() {
        return orientDeg;
    }
    
    /**
     * @return the orientConf
     */
    public float getOrientConf() {
        return orientConf;
    }
    
    /**
     * @return the scriptName
     */
    public String getScriptName() {
        return scriptName;
    }

    /**
     * @return the scriptConf
     */
    public float getScriptConf() {
        return scriptConf;
    }
    
    @Override
    public String toString() {
        return String.format("Orientation: %d degrees, confidence: %f; script name: %s, confidence: %f", getOrientDeg(), getOrientConf(), getScriptName(), getScriptConf());
    }
}
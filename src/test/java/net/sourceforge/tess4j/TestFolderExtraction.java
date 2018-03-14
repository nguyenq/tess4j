/**
 * Copyright @ 2008 Quan Nguyen
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

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

import net.sourceforge.tess4j.util.LoadLibs;
import net.sourceforge.tess4j.util.LoggHelper;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestFolderExtraction {

    private static final Logger logger = LoggerFactory.getLogger(new LoggHelper().toString());

    @Test
    public void testFolderExtraction() {
        File tessDataFolder = null;
        
        try {
            /**
             * Loads the image from resources.
             */
            String filename = String.format("%s/%s", "/test-data", "eurotext.pdf");
            URL defaultImage = getClass().getResource(filename);
            File imageFile = new File(defaultImage.toURI());

            /**
             * Extracts <code>tessdata</code> folder into a temp folder.
             */
            logger.info("Loading the tessdata folder into a temporary folder.");
            tessDataFolder = LoadLibs.extractTessResources("tessdata");
            
            /**
             * Gets tesseract instance and sets data path.
             */
            ITesseract instance = new Tesseract();
            
            if (tessDataFolder != null) {
                logger.info(tessDataFolder.getAbsolutePath());
                instance.setDatapath(tessDataFolder.getPath());
            }

            /**
             * Performs OCR on the image.
             */
            String result = instance.doOCR(imageFile);
            logger.info(result);
        } catch (TesseractException e) {
            logger.error(e.getMessage());
            logger.error(e.getMessage(), e);
        } catch (URISyntaxException e) {
            logger.error(e.getMessage(), e);
        }
        
        // checks if tessdata folder exists
        assertTrue(tessDataFolder != null && tessDataFolder.exists());
    }
}

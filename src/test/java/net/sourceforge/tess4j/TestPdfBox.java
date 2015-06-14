/**
 * Copyright @ 2008 Quan Nguyen
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package net.sourceforge.tess4j;

import net.sourceforge.tess4j.util.LoggHelper;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.util.ImageIOUtil;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class TestPdfBox {

    private static final Logger logger = LoggerFactory.getLogger(new LoggHelper().toString());

    @Test
    public void testPdfBox() {

        File tessDataFolder = null;

        try {
            String filename = String.format("%s/%s", "/test-data", "eurotext.pdf");
            File imageFile = new File(filename);

            PDDocument document = PDDocument.loadNonSeq(new File(filename), null);
            List<PDPage> pdPages = document.getDocumentCatalog().getAllPages();
            int page = 0;
            for (PDPage pdPage : pdPages) {
                ++page;
                BufferedImage bim = pdPage.convertToImage(BufferedImage.TYPE_INT_RGB, 300);
                ImageIOUtil.writeImage(bim, filename, 300);
            }
            document.close();


        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}

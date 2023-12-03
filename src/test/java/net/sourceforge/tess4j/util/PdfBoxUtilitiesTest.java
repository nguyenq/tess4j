package net.sourceforge.tess4j.util;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract1;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class PdfBoxUtilitiesTest {
    private final String testResourcesDataPath = "src/test/resources/test-data";
    private final String datapath = "src/main/resources/tessdata";

    ITesseract instance;

    @BeforeEach
    public void setUp() {
        instance = new Tesseract1();
        instance.setDatapath(new File(datapath).getPath());
    }

    @Test
    public void mergeHocrIntoAPdf_multiplePages() throws Exception {
        String hOcrFilename = String.format("%s/%s", this.testResourcesDataPath, "multipage-img.hocr");
        String pdfFilename = String.format("%s/%s", this.testResourcesDataPath, "multipage-img.pdf");
        String outputPdf = "target/test-classes/test-results/multipage-img-with-hocr.pdf";
        PdfBoxUtilities.mergeHocrIntoAPdf(hOcrFilename, pdfFilename, outputPdf, false);
        assertPdfContainsText("Auf der Registerkarte 'Einflgen' enthalten", outputPdf);
    }

    @Test
    public void mergeHocrIntoAPdf_singlePage() throws Exception {
        String hOcrFilename = String.format("%s/%s", this.testResourcesDataPath, "eurotext.hocr");
        String pdfFilename = String.format("%s/%s", this.testResourcesDataPath, "eurotext.pdf");
        String outputPdf = "target/test-classes/test-results/eurotext-withHocr.pdf";
        PdfBoxUtilities.mergeHocrIntoAPdf(hOcrFilename, pdfFilename, outputPdf, false);
        assertPdfContainsText("The (quick) [brown]", outputPdf);
    }

    @Test
    public void mergeHocrIntoAPdf_createHocrThenMergeToPDF() throws Exception {
        String pdfFilename = String.format("%s/%s", this.testResourcesDataPath, "eurotext.pdf");
        File imageFile1 = new File(pdfFilename);
        String outputbase1 = "target/test-classes/test-results/docrenderer2-1";
        String outputbase2 = "target/test-classes/test-results/docrenderer2-1-merge.pdf";
        List<ITesseract.RenderedFormat> formats = new ArrayList<>(Arrays.asList(ITesseract.RenderedFormat.HOCR));
        instance.createDocuments(new String[]{imageFile1.getPath()}, new String[]{outputbase1}, formats);
        assertTrue(new File(outputbase1 + ".hocr").exists());
        PdfBoxUtilities.mergeHocrIntoAPdf(outputbase1 + ".hocr", pdfFilename, outputbase2, false);
        assertTrue(new File(outputbase2).exists());
        assertPdfContainsText("The (quick) [brown]", outputbase2);
    }

    @Test
    public void mergeHocrIntoAPdf_createHocrOnMultipageThenMergeToPDF() throws Exception {
        String pdfFilename = String.format("%s/%s", this.testResourcesDataPath, "multipage-img.pdf");
        File imageFile1 = new File(pdfFilename);
        String outputbase1 = "target/test-classes/test-results/docrenderer2-2";
        String outputbase2 = "target/test-classes/test-results/docrenderer2-2-merge.pdf";
        List<ITesseract.RenderedFormat> formats = new ArrayList<>(Arrays.asList(ITesseract.RenderedFormat.HOCR));
        instance.createDocuments(new String[]{imageFile1.getPath()}, new String[]{outputbase1}, formats);
        assertTrue(new File(outputbase1 + ".hocr").exists());
        PdfBoxUtilities.mergeHocrIntoAPdf(outputbase1 + ".hocr", pdfFilename, outputbase2, false);
        assertPdfContainsText("Auf der Registerkarte 'Einflgen' enthalten", outputbase2);
    }

    private void assertPdfContainsText(String expectedString, String pdfFilepath) throws IOException {
        PDDocument doc = Loader.loadPDF(new File(pdfFilepath));
        String extractText = new PDFTextStripper().getText(doc);
        assertTrue(extractText.contains(expectedString));
    }
}
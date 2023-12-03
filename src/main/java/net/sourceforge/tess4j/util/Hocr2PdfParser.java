package net.sourceforge.tess4j.util;

import org.apache.commons.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.pdmodel.graphics.state.RenderingMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.*;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.awt.*;
import java.io.*;

import static java.lang.Float.valueOf;

public class Hocr2PdfParser implements ContentHandler, ErrorHandler {
    private static final Logger logger = LoggerFactory.getLogger(new LoggHelper().toString());
    public static final String SPACE = " ";
    public static final String BBOX = "bbox";
    public static final String TITLE = "title";
    public static final String SEMICOL = ";";
    public static final String DIV = "div";
    public static final String SPAN = "span";
    public static final String OCRX_WORD = "ocrx_word";
    public static final String OCR_WORD = "ocr_word";
    public static final String OCR_LINE = "ocr_line";
    public static final String CLASS = "class";
    public static final String OCR_PAGE = "ocr_page";

    private final RenderingMode renderingMode;
    private final PDFont font;
    private final boolean useHocrLineToY;
    private float xPageScaling;
    private float yPageScaling;
    private final StringBuilder text = new StringBuilder();
    private String[] coordsText;
    private String[] coordsLine;
    private PDPageContentStream pdfPageCanvas = null;
    private PDRectangle pdfPageBBox = null;
    private final PDDocument pdDocument;
    private final String hocrFilepath;

    @Override
    public void setDocumentLocator(Locator locator) {
        //nothing to do
    }

    @Override
    public void startDocument() throws SAXException {
        //nothing to do
    }

    @Override
    public void endDocument() throws SAXException {
        finishCurrentPage();
    }

    @Override
    public void startPrefixMapping(String prefix, String uri) throws SAXException {
        //nothing to do
    }

    @Override
    public void endPrefixMapping(String prefix) throws SAXException {
        //nothing to do
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
        text.setLength(0);
        this.coordsText = null;
        final String klass = atts.getValue(CLASS);
        if (SPAN.equals(qName) || SPAN.equals(localName)) {
            logger.debug("start of span, there is a new text or line");

            if (OCRX_WORD.equals(klass) || OCR_WORD.equals(klass)) {
                this.coordsText = readBboxCoordsFromAttributs(atts);
            } else if (OCR_LINE.equals(klass)) {
                this.coordsLine = readBboxCoordsFromAttributs(atts);
            } else {
                logger.debug("ignore {0} : {1}", new Object[]{CLASS, klass});
            }
        } else if (DIV.equals(qName) || DIV.equals(localName)) {
            if (OCR_PAGE.equals(klass)) {
                logger.debug("start div, start of page and close the previous page");
                finishCurrentPage();
                beginNewPage(
                        convertHocrAttributsToPageNum(atts),
                        readBboxCoordsFromAttributs(atts)
                );
            } else {
                logger.debug("ignore {0} : {1}", new Object[]{CLASS, klass});
            }
        } else {
            logger.debug("ignore qName : {0} or localName :{1} ", new Object[]{qName, localName});
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (coordsText != null && text.length() > 0) {
            logger.debug("OCRed word span closed, coords: {0} {1}", new Object[]{coordsText, text});

            if(useHocrLineToY) {
                //Override text coords by line coords for Y
                coordsText[2] = coordsLine[2];
                coordsText[4] = coordsLine[4];
            }

            addTextToPDF(coordsText, text.toString());
        } else {
            logger.debug("ignore endElement no text readed");
        }
        coordsText = null;
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        text.append(ch, start, length);
    }

    @Override
    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
        //nothing to do
    }

    @Override
    public void processingInstruction(String target, String data) throws SAXException {
        //nothing to do
    }

    @Override
    public void skippedEntity(String name) throws SAXException {
        //nothing to do
    }

    @Override
    public void warning(SAXParseException exception) throws SAXException {
        logger.warn("warning: " + exception.getMessage());
    }

    @Override
    public void error(SAXParseException exception) throws SAXException {
        logger.error("error: " + exception.getMessage());
    }

    @Override
    public void fatalError(SAXParseException exception) throws SAXException {
        logger.error("fatalError: " + exception.getMessage());
    }

    private String[] readBboxCoordsFromAttributs(Attributes attributes) {
        String title = attributes.getValue(TITLE);
        int bboxStart = title.indexOf(BBOX);
        int bboxEnd = title.indexOf(SEMICOL, bboxStart);
        return title.substring(bboxStart, bboxEnd).split(SPACE);
    }

    private Integer convertHocrAttributsToPageNum(Attributes atts) {
        final String pageId = atts.getValue("id");
        return Integer.valueOf(pageId.split("_")[1], 10) - 1;
    }

    private void beginNewPage(Integer pageId, String hocrPageBbox[]) throws SAXException {
        try {
            final PDPage sourcePage = pdDocument.getPage(pageId);

            this.pdfPageBBox = sourcePage.getBBox();
            this.pdfPageCanvas = new PDPageContentStream(
                    this.pdDocument,
                    sourcePage,
                    PDPageContentStream.AppendMode.APPEND,
                    true,
                    true);
            float hocrImgWidth = valueOf(hocrPageBbox[3]) - valueOf(hocrPageBbox[1]);
            float hocrImgHeight = valueOf(hocrPageBbox[4]) - valueOf(hocrPageBbox[2]);
            this.xPageScaling = this.pdfPageBBox.getWidth() / hocrImgWidth;
            this.yPageScaling = this.pdfPageBBox.getHeight() / hocrImgHeight;

            logger.debug("Load page {0} with scaling {1}x{2}",
                    new Object[]{pageId, this.xPageScaling, this.yPageScaling});

        } catch (NumberFormatException | IOException e) {
            throw new SAXException(e.getMessage());
        }
    }

    private void finishCurrentPage() {
        if (this.pdfPageCanvas != null) {
            IOUtils.closeQuietly(this.pdfPageCanvas);
        }
    }

    public void addTextToPDF(String[] imgCoords, String text) throws SAXException {
        try {
            PDRectangle textRect = new PDRectangle(
                    Integer.valueOf(imgCoords[1]),
                    Integer.valueOf(imgCoords[4]),
                    Integer.valueOf(imgCoords[3]) - Integer.valueOf(imgCoords[1]),
                    Integer.valueOf(imgCoords[4]) - Integer.valueOf(imgCoords[2])
            );

            float expectedTextWidth = textRect.getWidth() * xPageScaling;
            float fontSize = expectedTextWidth * 1000 / font.getStringWidth(text);
            float xText = textRect.getLowerLeftX() * xPageScaling;
            float yText = pdfPageBBox.getHeight() - textRect.getLowerLeftY() * yPageScaling;

            printTextAtCoordinates(text, fontSize, xText, yText);
        } catch (IOException e) {
            throw new SAXException(e.getMessage());
        }
    }

    private void printTextAtCoordinates(String text, float fontSize, float xText, float yText) throws IOException {
        logger.debug("Text,{1},{2},{3},{4},{5},{6}",
                new Object[]{xText, yText, fontSize, pdfPageBBox.getWidth(), pdfPageBBox.getHeight(), text});

        pdfPageCanvas.setFont(font, fontSize);
        pdfPageCanvas.saveGraphicsState();
        pdfPageCanvas.beginText();
        pdfPageCanvas.setRenderingMode(this.renderingMode);
        pdfPageCanvas.setNonStrokingColor(Color.red);
        pdfPageCanvas.setLineWidth(0);
        pdfPageCanvas.newLineAtOffset(xText, yText);
        pdfPageCanvas.showText(text);
        pdfPageCanvas.endText();
        pdfPageCanvas.restoreGraphicsState();
    }

    public Hocr2PdfParser(String hocrFilepath, PDDocument pdDocument, boolean visible, boolean useHocrLineToY,  String fontName) throws IOException {
        this.hocrFilepath = hocrFilepath;
        this.pdDocument = pdDocument;
        this.useHocrLineToY = useHocrLineToY;

        if (visible) {
            this.renderingMode = RenderingMode.FILL;
        } else {
            this.renderingMode = RenderingMode.NEITHER;
        }
        if (fontName != null) {
            this.font = new PDType1Font(Standard14Fonts.FontName.valueOf(fontName));
        } else {
            this.font = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
        }
    }

    public void parse() throws SAXException, IOException, ParserConfigurationException {
        SAXParserFactory parserFactory = SAXParserFactory.newInstance();
        SAXParser parser = parserFactory.newSAXParser();
        XMLReader reader = parser.getXMLReader();
        reader.setContentHandler(this);
        reader.setErrorHandler(this);

        reader.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        reader.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", false);

        try (FileInputStream fileInputStream = new FileInputStream(this.hocrFilepath)) {
            final BufferedReader characterStream = new BufferedReader(new InputStreamReader(fileInputStream, "UTF8"));
            characterStream.readLine();
            Reader filter = new Reader() {
                @Override
                public int read(char[] cbuf, int off, int len) throws IOException {
                    int chars = characterStream.read(cbuf, off, len);
                    // filter out all non XML characters
                    for (int i = 0; i < chars; i++) {
                        int c = cbuf[i + off];
                        if (c == 0x0009 || c == 0x000A || c == 0x000D
                                || (c >= 0x0020 && c <= 0xD7FF)
                                || (0x10000 <= c && c <= 0x10FFFF)) {
                            // c is XML valid, leave it alone
                        } else {
                            cbuf[i + off] = ' ';
                        }
                    }
                    return chars;
                }

                @Override
                public void close() throws IOException {
                    characterStream.close();
                }
            };
            reader.parse(new InputSource(filter));
        }
    }

}
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
package net.sourceforge.tess4j.util;

import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;

import org.w3c.dom.NodeList;

import com.github.jaiimageio.plugins.tiff.BaselineTIFFTagSet;
import com.github.jaiimageio.plugins.tiff.TIFFDirectory;
import com.github.jaiimageio.plugins.tiff.TIFFField;
import com.github.jaiimageio.plugins.tiff.TIFFImageWriteParam;
import com.github.jaiimageio.plugins.tiff.TIFFTag;
import com.recognition.software.jdeskew.ImageDeskew;
import com.recognition.software.jdeskew.ImageUtil;
import org.apache.commons.io.FilenameUtils;

public class ImageIOHelper {

    static final String OUTPUT_FILE_NAME = "Tesstmp";
    public static final String TIFF_EXT = ".tif";
    static final String TIFF_FORMAT = "tiff";
    public static final String JAI_IMAGE_WRITER_MESSAGE = "Need to install JAI Image I/O package.\nhttps://github.com/jai-imageio/jai-imageio-core";
    public static final String JAI_IMAGE_READER_MESSAGE = "Unsupported image format. May need to install JAI Image I/O package.\nhttps://github.com/jai-imageio/jai-imageio-core";

    /**
     * Creates a list of TIFF image files from an image file. It basically
     * converts images of other formats to TIFF format, or a multi-page TIFF
     * image to multiple TIFF image files.
     *
     * @param imageFile input image file
     * @param index an index of the page; -1 means all pages, as in a multi-page
     * TIFF image
     * @return a list of TIFF image files
     * @throws IOException
     */
    public static List<File> createTiffFiles(File imageFile, int index) throws IOException {
        return createTiffFiles(imageFile, index, false);
    }

    /**
     * Creates a list of TIFF image files from an image file. It basically
     * converts images of other formats to TIFF format, or a multi-page TIFF
     * image to multiple TIFF image files.
     *
     * @param imageFile input image file
     * @param index an index of the page; -1 means all pages, as in a multi-page
     * TIFF image
     * @param preserve preserve compression mode
     * @return a list of TIFF image files
     * @throws IOException
     */
    public static List<File> createTiffFiles(File imageFile, int index, boolean preserve) throws IOException {
        List<File> tiffFiles = new ArrayList<File>();

        String imageFormat = getImageFileFormat(imageFile);

        Iterator<ImageReader> readers = ImageIO.getImageReadersByFormatName(imageFormat);
        if (!readers.hasNext()) {
            throw new RuntimeException(JAI_IMAGE_READER_MESSAGE);
        }
        ImageReader reader = readers.next();

        // Get tiff writer and set output to file
        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName(TIFF_FORMAT);
        if (!writers.hasNext()) {
            throw new RuntimeException(JAI_IMAGE_WRITER_MESSAGE);
        }
        ImageWriter writer = writers.next();

        try (ImageInputStream iis = ImageIO.createImageInputStream(imageFile)) {
            reader.setInput(iis);
            // Read the stream metadata
            // IIOMetadata streamMetadata = reader.getStreamMetadata();

            // Set up the writeParam
            TIFFImageWriteParam tiffWriteParam = new TIFFImageWriteParam(Locale.US);
            if (!preserve) {
                // not preserve original sizes; decompress
                tiffWriteParam.setCompressionMode(ImageWriteParam.MODE_DISABLED);
            }
            // Read the stream metadata
            IIOMetadata streamMetadata = writer.getDefaultStreamMetadata(tiffWriteParam);

            int imageTotal = reader.getNumImages(true);

            for (int i = 0; i < imageTotal; i++) {
                // all if index == -1; otherwise, only index-th
                if (index == -1 || i == index) {
                    IIOImage oimage = reader.readAll(i, reader.getDefaultReadParam());
                    File tiffFile = File.createTempFile(OUTPUT_FILE_NAME, TIFF_EXT);
                    try (ImageOutputStream ios = ImageIO.createImageOutputStream(tiffFile)) {
                        writer.setOutput(ios);
                        writer.write(streamMetadata, oimage, tiffWriteParam);
                        tiffFiles.add(tiffFile);
                    }
                }
            }

            return tiffFiles;
        } finally {
            if (reader != null) {
                reader.dispose();
            }
            if (writer != null) {
                writer.dispose();
            }
        }
    }

    /**
     * Creates a list of TIFF image files from a list of <code>IIOImage</code>
     * objects.
     *
     * @param imageList a list of <code>IIOImage</code> objects
     * @param index an index of the page; -1 means all pages
     * @return a list of TIFF image files
     * @throws IOException
     */
    public static List<File> createTiffFiles(List<IIOImage> imageList, int index) throws IOException {
        return createTiffFiles(imageList, index, 0, 0);
    }

    public static List<File> createTiffFiles(List<IIOImage> imageList, int index, int dpiX, int dpiY) throws IOException {
        List<File> tiffFiles = new ArrayList<File>();

        //Set up the writeParam
        TIFFImageWriteParam tiffWriteParam = new TIFFImageWriteParam(Locale.US);
        tiffWriteParam.setCompressionMode(ImageWriteParam.MODE_DISABLED);

        //Get tiff writer and set output to file
        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName(TIFF_FORMAT);
        if (!writers.hasNext()) {
            throw new RuntimeException(JAI_IMAGE_WRITER_MESSAGE);
        }
        ImageWriter writer = writers.next();

        //Get the stream metadata
        IIOMetadata streamMetadata = writer.getDefaultStreamMetadata(tiffWriteParam);

        // all if index == -1; otherwise, only index-th
        for (IIOImage oimage : (index == -1 ? imageList : imageList.subList(index, index + 1))) {
            if (dpiX != 0 && dpiY != 0) {
                // Get the default image metadata.
                ImageTypeSpecifier imageType = ImageTypeSpecifier.createFromRenderedImage(oimage.getRenderedImage());
                ImageWriteParam param = writer.getDefaultWriteParam();
                IIOMetadata imageMetadata = writer.getDefaultImageMetadata(imageType, param);
                imageMetadata = setDPIViaAPI(imageMetadata, dpiX, dpiY);
                oimage.setMetadata(imageMetadata);
            }

            File tiffFile = File.createTempFile(OUTPUT_FILE_NAME, TIFF_EXT);
            try (ImageOutputStream ios = ImageIO.createImageOutputStream(tiffFile)) {
                writer.setOutput(ios);
                writer.write(streamMetadata, oimage, tiffWriteParam);
                tiffFiles.add(tiffFile);
            }
        }
        writer.dispose();

        return tiffFiles;
    }

    /**
     * Set DPI using API.
     *
     * @param imageMetadata original IIOMetadata
     * @param dpiX horizontal resolution
     * @param dpiY vertical resolution
     * @return modified IIOMetadata
     * @throws IIOInvalidTreeException
     */
    private static IIOMetadata setDPIViaAPI(IIOMetadata imageMetadata, int dpiX, int dpiY)
            throws IIOInvalidTreeException {
        // Derive the TIFFDirectory from the metadata.
        TIFFDirectory dir = TIFFDirectory.createFromMetadata(imageMetadata);

        // Get {X,Y}Resolution tags.
        BaselineTIFFTagSet base = BaselineTIFFTagSet.getInstance();
        TIFFTag tagXRes = base.getTag(BaselineTIFFTagSet.TAG_X_RESOLUTION);
        TIFFTag tagYRes = base.getTag(BaselineTIFFTagSet.TAG_Y_RESOLUTION);

        // Create {X,Y}Resolution fields.
        TIFFField fieldXRes = new TIFFField(tagXRes, TIFFTag.TIFF_RATIONAL,
                1, new long[][]{{dpiX, 1}});
        TIFFField fieldYRes = new TIFFField(tagYRes, TIFFTag.TIFF_RATIONAL,
                1, new long[][]{{dpiY, 1}});

        // Append {X,Y}Resolution fields to directory.
        dir.addTIFFField(fieldXRes);
        dir.addTIFFField(fieldYRes);

        // Convert to metadata object.
        IIOMetadata metadata = dir.getAsMetadata();

        // Add other metadata.
        IIOMetadataNode root = new IIOMetadataNode("javax_imageio_1.0");
        IIOMetadataNode horiz = new IIOMetadataNode("HorizontalPixelSize");
        horiz.setAttribute("value", Double.toString(25.4f / dpiX));
        IIOMetadataNode vert = new IIOMetadataNode("VerticalPixelSize");
        vert.setAttribute("value", Double.toString(25.4f / dpiY));
        IIOMetadataNode dim = new IIOMetadataNode("Dimension");
        dim.appendChild(horiz);
        dim.appendChild(vert);
        root.appendChild(dim);
        metadata.mergeTree("javax_imageio_1.0", root);

        return metadata;
    }

    /**
     * Gets pixel data of an <code>IIOImage</code> object.
     *
     * @param image an <code>IIOImage</code> object
     * @return a byte buffer of pixel data
     */
    public static ByteBuffer getImageByteBuffer(IIOImage image) {
        return getImageByteBuffer(image.getRenderedImage());
    }

    /**
     * Gets pixel data of an <code>RenderedImage</code> object.
     *
     * @param image an <code>RenderedImage</code> object
     * @return a byte buffer of pixel data
     */
    public static ByteBuffer getImageByteBuffer(RenderedImage image) {
        ColorModel cm = image.getColorModel();
        WritableRaster wr = image.getData().createCompatibleWritableRaster(image.getWidth(), image.getHeight());
        image.copyData(wr);
        BufferedImage bi = new BufferedImage(cm, wr, cm.isAlphaPremultiplied(), null);
        return convertImageData(bi);
    }

    /**
     * Converts <code>BufferedImage</code> to <code>ByteBuffer</code>.
     *
     * @param bi Input image
     * @return pixel data
     */
    public static ByteBuffer convertImageData(BufferedImage bi) {
        DataBuffer buff = bi.getRaster().getDataBuffer();
        // ClassCastException thrown if buff not instanceof DataBufferByte because raster data is not necessarily bytes.
        // Convert the original buffered image to grayscale.
        if (!(buff instanceof DataBufferByte)) {
            BufferedImage grayscaleImage = ImageHelper.convertImageToGrayscale(bi);
            buff = grayscaleImage.getRaster().getDataBuffer();
        }
        byte[] pixelData = ((DataBufferByte) buff).getData();
        //        return ByteBuffer.wrap(pixelData);
        ByteBuffer buf = ByteBuffer.allocateDirect(pixelData.length);
        buf.order(ByteOrder.nativeOrder());
        buf.put(pixelData);
        ((Buffer) buf).flip();
        return buf;
    }

    /**
     * Gets image file format.
     *
     * @param imageFile input image file
     * @return image file format
     */
    public static String getImageFileFormat(File imageFile) {
        String imageFileName = imageFile.getName();
        String imageFormat = imageFileName.substring(imageFileName.lastIndexOf('.') + 1);
        if (imageFormat.matches("(pbm|pgm|ppm)")) {
            imageFormat = "pnm";
        } else if (imageFormat.matches("(jp2|j2k|jpf|jpx|jpm)")) {
            imageFormat = "jpeg2000";
        }
        return imageFormat;
    }

    /**
     * Gets image file. Convert to multi-page TIFF if given PDF.
     *
     * @param inputFile input file (common image or PDF)
     * @return image file
     * @throws IOException
     */
    public static File getImageFile(File inputFile) throws IOException {
        File imageFile = inputFile;
        if (inputFile.getName().toLowerCase().endsWith(".pdf")) {
            imageFile = PdfUtilities.convertPdf2Tiff(inputFile);
        }
        return imageFile;
    }

    /**
     * Gets a list of <code>BufferedImage</code> objects for an image file.
     *
     * @param inputFile input image file. It can be any of the supported
     * formats, including TIFF, JPEG, GIF, PNG, BMP, JPEG, and PDF if GPL
     * Ghostscript or PDFBox is installed
     * @return a list of <code>BufferedImage</code> objects
     * @throws IOException
     */
    public static List<BufferedImage> getImageList(File inputFile) throws IOException {
        // convert to TIFF if PDF
        File imageFile = getImageFile(inputFile);

        List<BufferedImage> biList = new ArrayList<BufferedImage>();
        String imageFormat = getImageFileFormat(imageFile);

        Iterator<ImageReader> readers = ImageIO.getImageReadersByFormatName(imageFormat);
        if (!readers.hasNext()) {
            throw new RuntimeException(JAI_IMAGE_READER_MESSAGE);
        }
        ImageReader reader = readers.next();

        try (ImageInputStream iis = ImageIO.createImageInputStream(imageFile)) {
            reader.setInput(iis);

            int imageTotal = reader.getNumImages(true);

            for (int i = 0; i < imageTotal; i++) {
                BufferedImage bi = reader.read(i);
                biList.add(bi);
            }

            return biList;
        } finally {
            if (reader != null) {
                reader.dispose();
            }

            // delete temporary TIFF image for PDF
            if (imageFile != null && imageFile.exists() && imageFile != inputFile && imageFile.getName().startsWith("multipage") && imageFile.getName().endsWith(TIFF_EXT)) {
                imageFile.delete();
            }
        }
    }

    /**
     * Gets a list of <code>IIOImage</code> objects for an image file.
     *
     * @param inputFile input image file. It can be any of the supported
     * formats, including TIFF, JPEG, GIF, PNG, BMP, JPEG, and PDF if GPL
     * Ghostscript or PDFBox is installed
     * @return a list of <code>IIOImage</code> objects
     * @throws IOException
     */
    public static List<IIOImage> getIIOImageList(File inputFile) throws IOException {
        // convert to TIFF if PDF
        File imageFile = getImageFile(inputFile);

        List<IIOImage> iioImageList = new ArrayList<IIOImage>();
        String imageFormat = getImageFileFormat(imageFile);

        Iterator<ImageReader> readers = ImageIO.getImageReadersByFormatName(imageFormat);
        if (!readers.hasNext()) {
            throw new RuntimeException(JAI_IMAGE_READER_MESSAGE);
        }
        ImageReader reader = readers.next();

        try (ImageInputStream iis = ImageIO.createImageInputStream(imageFile)) {
            reader.setInput(iis);

            int imageTotal = reader.getNumImages(true);

            for (int i = 0; i < imageTotal; i++) {
//                IIOImage oimage = new IIOImage(reader.read(i), null, reader.getImageMetadata(i));
                IIOImage oimage = reader.readAll(i, reader.getDefaultReadParam());
                iioImageList.add(oimage);
            }

            return iioImageList;
        } finally {
            if (reader != null) {
                reader.dispose();
            }

            // delete temporary TIFF image for PDF
            if (imageFile != null && imageFile.exists() && imageFile != inputFile && imageFile.getName().startsWith("multipage") && imageFile.getName().endsWith(TIFF_EXT)) {
                imageFile.delete();
            }
        }
    }

    /**
     * Gets a list of <code>IIOImage</code> objects for a
     * <code>BufferedImage</code>.
     *
     * @param bi input image
     * @return a list of <code>IIOImage</code> objects
     * @throws IOException
     */
    public static List<IIOImage> getIIOImageList(BufferedImage bi) throws IOException {
        List<IIOImage> iioImageList = new ArrayList<IIOImage>();
        IIOImage oimage = new IIOImage(bi, null, null);
        iioImageList.add(oimage);
        return iioImageList;
    }

    /**
     * Merges multiple images into one multi-page TIFF image.
     *
     * @param inputImages an array of image files
     * @param outputTiff the output multi-page TIFF file
     * @throws IOException
     */
    public static void mergeTiff(File[] inputImages, File outputTiff) throws IOException {
        if (inputImages.length == 0) {
            // if no image
            return;
        }

        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName(TIFF_FORMAT);
        if (!writers.hasNext()) {
            throw new RuntimeException(JAI_IMAGE_WRITER_MESSAGE);
        }
        ImageWriter writer = writers.next();

        //Set up the writeParam
        TIFFImageWriteParam tiffWriteParam = new TIFFImageWriteParam(Locale.US);
//        tiffWriteParam.setCompressionMode(ImageWriteParam.MODE_DISABLED); // commented out to preserve original sizes

        //Get the stream metadata
        IIOMetadata streamMetadata = writer.getDefaultStreamMetadata(tiffWriteParam);

        try (ImageOutputStream ios = ImageIO.createImageOutputStream(outputTiff)) {
            writer.setOutput(ios);
            boolean firstPage = true;
            int index = 1;
            for (File inputImage : inputImages) {
                String imageFileFormat = getImageFileFormat(inputImage);
                Iterator<ImageReader> readers = ImageIO.getImageReadersByFormatName(imageFileFormat);
                if (!readers.hasNext()) {
                    throw new RuntimeException(JAI_IMAGE_READER_MESSAGE);
                }
                ImageReader reader = readers.next();
                try (ImageInputStream iis = ImageIO.createImageInputStream(inputImage)) {
                    reader.setInput(iis);
                    int imageTotal = reader.getNumImages(true);
                    for (int i = 0; i < imageTotal; i++) {
                        IIOImage oimage = reader.readAll(i, reader.getDefaultReadParam());
                        if (firstPage) {
                            writer.write(streamMetadata, oimage, tiffWriteParam);
                            firstPage = false;
                        } else {
                            writer.writeInsert(index++, oimage, tiffWriteParam);
                        }
                    }
                } finally {
                    if (reader != null) {
                        reader.dispose();
                    }
                }
            }
        } finally {
            writer.dispose();
        }
    }

    /**
     * Merges multiple images into one multi-page TIFF image.
     *
     * @param inputImages an array of <code>BufferedImage</code>
     * @param outputTiff the output TIFF file
     * @throws IOException
     */
    public static void mergeTiff(BufferedImage[] inputImages, File outputTiff) throws IOException {
        mergeTiff(inputImages, outputTiff, null);
    }

    /**
     * Merges multiple images into one multi-page TIFF image.
     *
     * @param inputImages an array of <code>BufferedImage</code>
     * @param outputTiff the output TIFF file
     * @param compressionType valid values: LZW, CCITT T.6, PackBits
     * @throws IOException
     */
    public static void mergeTiff(BufferedImage[] inputImages, File outputTiff, String compressionType) throws IOException {
        List<IIOImage> imageList = new ArrayList<IIOImage>();

        for (BufferedImage inputImage : inputImages) {
            imageList.add(new IIOImage(inputImage, null, null));
        }

        mergeTiff(imageList, outputTiff, compressionType);
    }

    /**
     * Merges multiple images into one multi-page TIFF image.
     *
     * @param imageList a list of <code>IIOImage</code> objects
     * @param outputTiff the output TIFF file
     * @throws IOException
     */
    public static void mergeTiff(List<IIOImage> imageList, File outputTiff) throws IOException {
        mergeTiff(imageList, outputTiff, null);
    }

    /**
     * Merges multiple images into one multi-page TIFF image.
     *
     * @param imageList a list of <code>IIOImage</code> objects
     * @param outputTiff the output TIFF file
     * @param compressionType valid values: LZW, CCITT T.6, PackBits
     * @throws IOException
     */
    public static void mergeTiff(List<IIOImage> imageList, File outputTiff, String compressionType) throws IOException {
        if (imageList == null || imageList.isEmpty()) {
            // if no image
            return;
        }

        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName(TIFF_FORMAT);
        if (!writers.hasNext()) {
            throw new RuntimeException(JAI_IMAGE_WRITER_MESSAGE);
        }
        ImageWriter writer = writers.next();

        //Set up the writeParam
        TIFFImageWriteParam tiffWriteParam = new TIFFImageWriteParam(Locale.US);
//        tiffWriteParam.setCompressionMode(ImageWriteParam.MODE_DISABLED); // comment out to preserve original sizes
        if (compressionType != null) {
            tiffWriteParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            tiffWriteParam.setCompressionType(compressionType);
        }

        //Get the stream metadata
        IIOMetadata streamMetadata = writer.getDefaultStreamMetadata(tiffWriteParam);

        try (ImageOutputStream ios = ImageIO.createImageOutputStream(outputTiff)) {
            writer.setOutput(ios);

            int dpiX = 300;
            int dpiY = 300;

            for (IIOImage iioImage : imageList) {
                // Get the default image metadata.
                ImageTypeSpecifier imageType = ImageTypeSpecifier.createFromRenderedImage(iioImage.getRenderedImage());
                ImageWriteParam param = writer.getDefaultWriteParam();
                IIOMetadata imageMetadata = writer.getDefaultImageMetadata(imageType, param);
                imageMetadata = setDPIViaAPI(imageMetadata, dpiX, dpiY);
                iioImage.setMetadata(imageMetadata);
            }

            IIOImage firstIioImage = imageList.remove(0);
            writer.write(streamMetadata, firstIioImage, tiffWriteParam);

            int i = 1;
            for (IIOImage iioImage : imageList) {
                writer.writeInsert(i++, iioImage, tiffWriteParam);
            }
        } finally {
            writer.dispose();
        }
    }

    /**
     * Deskews image.
     *
     * @param imageFile input image
     * @param minimumDeskewThreshold minimum deskew threshold (typically, 0.05d)
     * @return temporary multi-page TIFF image file
     * @throws IOException
     */
    public static File deskewImage(File imageFile, double minimumDeskewThreshold) throws IOException {
        List<BufferedImage> imageList = getImageList(imageFile);
        for (int i = 0; i < imageList.size(); i++) {
            BufferedImage bi = imageList.get(i);
            ImageDeskew deskew = new ImageDeskew(bi);
            double imageSkewAngle = deskew.getSkewAngle();

            if ((imageSkewAngle > minimumDeskewThreshold || imageSkewAngle < -(minimumDeskewThreshold))) {
                bi = ImageUtil.rotate(bi, -imageSkewAngle, bi.getWidth() / 2, bi.getHeight() / 2);
                imageList.set(i, bi); // replace original with deskewed image
            }
        }

        File tempImageFile = File.createTempFile(FilenameUtils.getBaseName(imageFile.getName()), ".tif");
        mergeTiff(imageList.toArray(new BufferedImage[0]), tempImageFile);

        return tempImageFile;
    }

    /**
     * Reads image meta data.
     *
     * @param oimage
     * @return a map of meta data
     */
    public static Map<String, String> readImageData(IIOImage oimage) {
        Map<String, String> dict = new HashMap<String, String>();

        IIOMetadata imageMetadata = oimage.getMetadata();
        if (imageMetadata != null) {
            IIOMetadataNode dimNode = (IIOMetadataNode) imageMetadata.getAsTree("javax_imageio_1.0");
            NodeList nodes = dimNode.getElementsByTagName("HorizontalPixelSize");
            int dpiX;
            if (nodes.getLength() > 0) {
                float dpcWidth = Float.parseFloat(nodes.item(0).getAttributes().item(0).getNodeValue());
                dpiX = (int) Math.round(25.4f / dpcWidth);
            } else {
                dpiX = Toolkit.getDefaultToolkit().getScreenResolution();
            }
            dict.put("dpiX", String.valueOf(dpiX));

            nodes = dimNode.getElementsByTagName("VerticalPixelSize");
            int dpiY;
            if (nodes.getLength() > 0) {
                float dpcHeight = Float.parseFloat(nodes.item(0).getAttributes().item(0).getNodeValue());
                dpiY = (int) Math.round(25.4f / dpcHeight);
            } else {
                dpiY = Toolkit.getDefaultToolkit().getScreenResolution();
            }
            dict.put("dpiY", String.valueOf(dpiY));
        }

        return dict;
    }
}

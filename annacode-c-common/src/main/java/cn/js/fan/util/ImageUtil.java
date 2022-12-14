package cn.js.fan.util;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Image utilities class.
 *
 * @author T55555
 * @version 1.0 2003-01-29
 */
public class ImageUtil {

    /**
     * Return scaled image.
     * Pre-conditions: (source != null) && (width > 0) && (height > 0)
     *
     * @param source the image source
     * @param width  the new image's width
     * @param height the new image's height
     * @return the new image scaled
     */
    public static BufferedImage getScaleImage(BufferedImage source,
                                              int width, int height) {
        //assert(source != null && width > 0 && height > 0);
        int type = source.getType();
        if (type == 0) {
            type = BufferedImage.TYPE_3BYTE_BGR;
        }
        BufferedImage image = new BufferedImage(width, height, type);
        image.createGraphics().drawImage(source, 0, 0, width, height, null);
        return image;
    }

    /**
     * Return scaled image.
     * Pre-conditions: (source != null) && (xscale > 0) && (yscale > 0)
     *
     * @param source the image source
     * @param xscale the percentage of the source image's width
     * @param yscale the percentage of the source image's height
     * @return the new image scaled
     */
    public static BufferedImage getScaleImage(BufferedImage source,
                                              double xscale, double yscale) {
        //assert(source != null && width > 0 && height > 0);
        return getScaleImage(source,
                (int) (source.getWidth() * xscale), (int) (source.getHeight() * yscale));
    }

    /**
     * Read the input image file, scaled then write the output image file.
     *
     * @param input  the input image file
     * @param output the output image file
     * @param width  the output image's width
     * @param height the output image's height
     * @return true for sucessful,
     * false if no appropriate reader or writer is found.
     */
    public static boolean scaleImage(File input, File output,
                                     int width, int height) throws IOException {
        BufferedImage image = ImageIO.read(input);
        if (image == null) {
            return false;
        }
        image = getScaleImage(image, width, height);
        String name = output.getName();
        String format = name.substring(name.lastIndexOf('.') + 1).toLowerCase();
        return ImageIO.write(image, format, output);
    }

    /**
     * Read the input image file, scaled then write the output image file.
     *
     * @param input  the input image file
     * @param output the output image file
     * @param xscale the percentage of the input image's width for output image's width
     * @param yscale the percentage of the input image's height for output image's height
     * @return true for sucessful,
     * false if no appropriate reader or writer is found.
     */
    public static boolean scaleImage(File input, File output,
                                     double xscale, double yscale) throws IOException {
        BufferedImage image = ImageIO.read(input);
        if (image == null) {
            return false;
        }
        image = getScaleImage(image, xscale, yscale);
        String name = output.getName();
        String format = name.substring(name.lastIndexOf('.') + 1).toLowerCase();
        return ImageIO.write(image, format, output);
    }

    /**
     * ???????????????????????????????????????????????????????????????????????????????????????????????????????????????
     *
     * @param input  File
     * @param output File
     * @return boolean
     * @throws IOException
     */
    public static boolean Image2Thumb(File input, File output,
                                      int thumbWidth) throws IOException {
        BufferedImage image = ImageIO.read(input);
        if (image == null) {
            return false;
        }
        int w = image.getWidth();
        int h = image.getHeight();

        double dHeight = ((double) thumbWidth) / w * h;
        int height = (int) dHeight;
        if (w > thumbWidth)
            image = getScaleImage(image, thumbWidth, height);
        else
            image = getScaleImage(image, w, h);
        String name = output.getName();
        String format = name.substring(name.lastIndexOf('.') + 1).toLowerCase();
        return ImageIO.write(image, format, output);
    }

    /**
     * ????????????????????????????????????????????????????????????????????????????????????????????????
     *
     * @param imgPath String ????????????
     * @param m       int ??????????????????
     */
    public static void splitFile(String imgPath, int m) {
        File src = new File(imgPath);
        if (src.isFile()) {
            // ????????????????????????
            long fileLength = src.length();
            // ???????????????
            String fileName = src.getName().substring(0,
                    src.getName().indexOf("."));
            // ??????????????????
            String ext = src.getName().substring(
                    src.getName().lastIndexOf("."));
            InputStream in = null;
            try {
                in = new FileInputStream(src);
                for (int i = 1; i <= m; i++) {
                    StringBuffer sb = new StringBuffer();
                    sb.append(src.getParent()).append("\\").append(fileName)
                            .append("_data").append(i).append(ext);
                    System.out.println(sb.toString());
                    File file2 = new File(sb.toString());
                    // ???????????????????????????
                    OutputStream out = new FileOutputStream(file2);
                    boolean isWrited = false;
                    int len = -1;
                    byte[] bytes = new byte[600 * 600];
                    while ((len = in.read(bytes)) != -1) {
                        isWrited = true;
                        out.write(bytes, 0, len);
                        if (file2.length() > (fileLength / m)) {
                            break;
                        }
                    }
                    out.close();

                    if (!isWrited) {
                        file2.delete();
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // ?????????????????????????????????????????????????????????
    public static void joinFile(String... src) {
        for (int i = 0; i < src.length; i++) {
            File file = new File(src[i]);
            String fileName = file.getName().substring(0,
                    file.getName().indexOf("_"));
            String endName = file.getName().substring(
                    file.getName().lastIndexOf("."));
            StringBuffer sb = new StringBuffer();
            sb.append(file.getParent()).append("\\").append(fileName).append(
                    endName);
            System.out.println(sb.toString());
            try {
                // ???????????????????????????
                InputStream in = new FileInputStream(file);
                // ???????????????????????????
                File file2 = new File(sb.toString());
                OutputStream out = new FileOutputStream(file2, true);
                int len = -1;
                byte[] bytes = new byte[10 * 1024 * 1024];
                while ((len = in.read(bytes)) != -1) {
                    out.write(bytes, 0, len);
                }
                out.close();
                in.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.out.println("?????????????????????");
    }

    public static BufferedImage readImage(String imgUrl) {
        BufferedImage img = null;
        try {
            if (imgUrl.startsWith("http:")) {
                img = ImageIO.read(new URL(imgUrl));
            } else {
                // ??????ImageIO.write??????????????????
                // img = ImageIO.read(new File(imgUrl));
                img = toBufferedImage(new File(imgUrl));
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return img;
    }

    public static BufferedImage toBufferedImage(File img) throws IOException {
        Image image = Toolkit.getDefaultToolkit().getImage(img.getPath());
        BufferedImage bimage = null;
        if (image instanceof BufferedImage) {
            return (BufferedImage) image;
        }
        image = new ImageIcon(image).getImage();
        int width = image.getWidth(null);
        int height = image.getHeight(null);
        if (width < 0 || height < 0) {
            /*Map<String,Integer> map = getImageSize(img);
            width = map.get(IMAGE_WIDTH);
            height = map.get(IMAGE_HEIGHT);*/
            return null;
        }
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        try {
            int transparency = Transparency.OPAQUE;
            GraphicsDevice gs = ge.getDefaultScreenDevice();
            GraphicsConfiguration gc = gs.getDefaultConfiguration();
            bimage = gc.createCompatibleImage(width, height, transparency);
        } catch (HeadlessException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (bimage == null) {
            int type = BufferedImage.TYPE_INT_RGB;
            bimage = new BufferedImage(image.getWidth(null), image.getHeight(null), type);
        }
        Graphics g = bimage.createGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();
        return bimage;
    }

    private static final int[] RGB_MASKS = {0xFF0000, 0xFF00, 0xFF};
    private static final ColorModel RGB_OPAQUE =
            new DirectColorModel(32, RGB_MASKS[0], RGB_MASKS[1], RGB_MASKS[2]);

    public static BufferedImage cat(int x, int y, int wight, int hight,
                                    BufferedImage img, String fileName, String suffix, String ext) {
        int w = img.getWidth();
        int h = img.getHeight();
        int[] simgRgb = new int[wight * hight];
        img.getRGB(x, y, wight, hight, simgRgb, 0, wight);

        BufferedImage newImage = new BufferedImage(wight, hight,
                BufferedImage.TYPE_INT_ARGB);
        newImage.setRGB(0, 0, wight, hight, simgRgb, 0, wight);
        /*
         * ???????????????JPG?????????????????????Alpha??????????????????????????????????????????jpg?????????Alpha??????????????????????????????write???????????????
        try {
            ImageIO.write(newImage, "JPEG", new File(fileName + suffix + "." + ext));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }  
        */
        PixelGrabber pg = new PixelGrabber(newImage, 0, 0, -1, -1, true);
        try {
            pg.grabPixels();
            int width = pg.getWidth(), height = pg.getHeight();

            DataBuffer buffer = new DataBufferInt((int[]) pg.getPixels(), pg.getWidth() * pg.getHeight());
            WritableRaster raster = Raster.createPackedRaster(buffer, width, height, width, RGB_MASKS, null);
            BufferedImage bi = new BufferedImage(RGB_OPAQUE, raster, false, null);
            ImageIO.write(bi, "JPEG", new File(fileName + suffix + "." + ext));
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return newImage;
    }

    public static void main(String argv[]) {
/*      try {
            ImageUtil.splitFile("d:\\hysht.jpg", 10);
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        String imgUrl = "d:\\test\\hysht.jpg";

        File src = new File(imgUrl);
        String fileName = src.getName().substring(0,
                src.getName().indexOf("."));
        // ??????????????????
        String ext = src.getName().substring(
                src.getName().lastIndexOf("."));

        fileName = "d:\\test\\" + fileName;

        BufferedImage img = readImage(imgUrl);
        int w = 500, h = 500;
        int imgW = img.getWidth();
        int imgH = img.getHeight();

        int wCount = imgW / w;
        if (imgW % w != 0) {
            wCount++;
        }

        int hCount = imgH / h;
        if (imgH % h != 0) {
            hCount++;
        }

        for (int i = 0; i < hCount; i++) {
            for (int j = 0; j < wCount; j++) {
                int x = j * w;
                int y = i * h;
                int blockW = w;
                int blockH = h;
                if (x + w > imgW) {
                    blockW = imgW - x;
                }
                if (y + h > imgH) {
                    blockH = imgH - y;
                }

                String suffix = "_" + i + "_" + j;
                ImageUtil.cat(x, y, blockW, blockH, img, fileName, suffix, ext);
            }
        }
    }
}


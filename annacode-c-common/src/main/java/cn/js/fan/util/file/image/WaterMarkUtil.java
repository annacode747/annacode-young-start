package cn.js.fan.util.file.image;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class WaterMarkUtil {
    public static final int POS_CENTER = 0;
    public static final int POS_RIGHT_BOTTOM = 3;
    public static final int POS_RIGHT_TOP = 4;
    public static final int POS_LEFT_TOP = 1;
    public static final int POS_LEFT_BOTTOM = 2;
    public static int offsetX = 12;
    public static int offsetY = 12;
    public int pos;

    public int srcImgWidth, srcImgHeight;
    public int markImgWidth, markImgHeight;

    public WaterMarkUtil(int pos) {
        this.pos = pos;
    }

    public int getMarkTop() {
        int n = offsetY;
        switch (pos) {
            case POS_RIGHT_TOP:
                n = offsetY;
                break;
            case POS_RIGHT_BOTTOM:
                n = srcImgHeight - offsetY - markImgHeight;
                break;
            case POS_LEFT_TOP:
                n = offsetY;
                break;
            case POS_LEFT_BOTTOM:
                n = srcImgHeight - offsetY - markImgHeight;
                break;
            default:
                n = srcImgHeight - offsetY - markImgHeight;
        }
        return n;
    }

    public int getMarkLeft() {
        int n = offsetX;
        switch (pos) {
            case POS_RIGHT_TOP:
                n = srcImgWidth - offsetX - markImgWidth;
                break;
            case POS_RIGHT_BOTTOM:
                n = srcImgWidth - offsetX - markImgWidth;
                break;
            case POS_LEFT_TOP:
                n = offsetX;
                break;
            case POS_LEFT_BOTTOM:
                n = offsetX;
                break;
            default:
                n = offsetX;
        }
        return n;
    }

    public int getMarkStringTop() {
        int n = offsetY;
        switch (pos) {
            case POS_RIGHT_TOP:
                n = offsetY;
                break;
            case POS_RIGHT_BOTTOM:
                n = srcImgHeight - offsetY;
                break;
            case POS_LEFT_TOP:
                n = offsetY;
                break;
            case POS_LEFT_BOTTOM:
                n = srcImgHeight - offsetY;
                break;
            default:
                n = offsetY;
        }
        return n;
    }

    public int getMarkStringLeft() {
        int n = offsetX;
        switch (pos) {
            case POS_RIGHT_TOP:
                n = srcImgWidth - offsetX;
                break;
            case POS_RIGHT_BOTTOM:
                n = srcImgWidth - offsetX;
                break;
            case POS_LEFT_TOP:
                n = offsetX;
                break;
            case POS_LEFT_BOTTOM:
                n = offsetX;
                break;
            default:
                n = offsetX;
        }
        return n;
    }

    /**
     * ????????????
     *
     * @param srcFilePath  String
     * @param newFilePath  String
     * @param waterMarkStr String
     * @param font         Font
     * @param color        Color
     * @param alpha        float
     */
    public void mark(String srcFilePath, String newFilePath, String waterMarkStr, Font font, Color color, float alpha) {
        try {
            File srcFile = new File(srcFilePath);
            Image srcImg = javax.imageio.ImageIO.read(srcFile);
            srcImgWidth = srcImg.getWidth(null);
            srcImgHeight = srcImg.getHeight(null);
            BufferedImage bufImg = new BufferedImage(srcImgWidth, srcImgHeight,
                    BufferedImage.TYPE_INT_RGB);
            Graphics2D g = bufImg.createGraphics();
            g.drawImage(srcImg, 0, 0, srcImgWidth, srcImgHeight, null);
            if (font == null)
                g.setFont(new Font("??????", Font.BOLD, 20)); // ??????????????????
            else
                g.setFont(font);
            if (color == null)
                g.setColor(Color.BLUE); // ??????????????????
            else
                g.setColor(color);

            AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.
                    SRC_OVER, alpha);
            g.setComposite(ac);

            /*
             FontMetrics fm = g.getFontMetrics();
             int stringWidth(String str)?????????????????????
            */

            // System.out.println("WaterMarkUtil.java top=" + getMarkStringTop());
            g.drawString(waterMarkStr, getMarkStringLeft(), getMarkStringTop()); // ???BufferedImage??????????????????
            g.dispose(); // ???????????????

            FileOutputStream out = new FileOutputStream(newFilePath);
            JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
            encoder.encode(bufImg);

            // JPEGEncodeParam param = encoder.getDefaultJPEGEncodeParam(bufImg);
            // param.setQuality(qualNum, true);
            // encoder.encode(bufImg, param);

            out.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * ????????????
     *
     * @param srcFilePath      String
     * @param newFilePath      String
     * @param waterMarkImgPath String ?????????????????????
     * @param alpha            float ????????? 0 ????????????
     */
    public void mark(String srcFilePath, String newFilePath, String waterMarkImgPath, float alpha) {
        try {
            File srcFile = new File(srcFilePath);
            Image srcImg = javax.imageio.ImageIO.read(srcFile);
            srcImgWidth = srcImg.getWidth(null);
            srcImgHeight = srcImg.getHeight(null);
            BufferedImage bufImg = new BufferedImage(srcImgWidth, srcImgHeight,
                    BufferedImage.TYPE_INT_RGB);

            Graphics2D g = bufImg.createGraphics();
            g.drawImage(srcImg, 0, 0, srcImgWidth, srcImgHeight, null);
            Image img1 = javax.imageio.ImageIO.read(new File(waterMarkImgPath));
            markImgWidth = img1.getWidth(null);
            markImgHeight = img1.getHeight(null);

            AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.
                    SRC_OVER, alpha);
            g.setComposite(ac);

            g.drawImage(img1, getMarkLeft(), getMarkTop(), markImgWidth,
                    markImgHeight, null);

            g.dispose(); // ???????????????

            FileOutputStream out = new FileOutputStream(newFilePath);
            JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
            encoder.encode(bufImg);

            // JPEGEncodeParam param = encoder.getDefaultJPEGEncodeParam(bufImg);
            // param.setQuality(qualNum, true);
            // encoder.encode(bufImg, param);

            out.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("water mark img path:" + waterMarkImgPath);
            e.printStackTrace();
        }
    }

    public void setOffsetX(int x) {
        offsetX = x;
    }

    public void setOffsetY(int y) {
        offsetY = y;
    }
}

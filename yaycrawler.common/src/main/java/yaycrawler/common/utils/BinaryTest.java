package yaycrawler.common.utils;

/**
 * @author bill
 * @create 2017-08-21 10:18
 * @desc 二值化
 **/
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import yaycrawler.common.interceptor.SignatureSecurityInterceptor;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class BinaryTest {

    private static final Logger logger  = LoggerFactory.getLogger(BinaryTest.class);

    public static void main(String[] args) throws IOException {
        File testDataDir = new File("d:/tmp/ocr");
        final String destDir = testDataDir.getAbsolutePath()+"/tmp6";

        File destF = new File(destDir);
        if (!destF.exists())
        {
            destF.mkdirs();
        }
        for (File file : testDataDir.listFiles()) {
            if (file.isFile()) {
                BufferedImage bi = ImageIO.read(file);//通过imageio将图像载入
                int h = bi.getHeight();//获取图像的高
                int w = bi.getWidth();//获取图像的宽
                int rgb = bi.getRGB(0, 0);//获取指定坐标的ARGB的像素值
                BufferedImage nbi = getBufferedImage(bi, h, w);
                printGrayRGB(h, w, nbi);
                ImageIO.write(nbi, "jpg", new File(destDir, file.getName()));
            }
        }
    }

    public static BufferedImage getBufferedImage(BufferedImage bi, int h, int w) {
        int[][] gray = new int[w][h];
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                gray[x][y] = getGray(bi.getRGB(x, y));
            }
        }

        BufferedImage nbi = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_BINARY);
        int SW = 200;
        int pw;
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                pw = getAverageColor(gray, x, y, w, h);
                if (pw > SW ) {
                    int max = new Color(255, 255, 255).getRGB();
                    nbi.setRGB(x, y, max);
                } else {
                    int min = new Color(0, 0, 0).getRGB();
                    nbi.setRGB(x, y, min);
                }
            }
        }
        return nbi;
    }

    public static void printGrayRGB(int h, int w, BufferedImage nbi) {
        // 矩阵打印
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                if (isBlack(nbi.getRGB(x, y))) {
                    System.out.print("*");
                } else {
                    System.out.print(" ");
                }
            }
            System.out.println();
        }
    }

    public static boolean isBlack(int colorInt)
    {
        Color color = new Color(colorInt);
        if (color.getRed() + color.getGreen() + color.getBlue() <= 300)
        {
            return true;
        }
        return false;
    }

    public static int getGray(int rgb){
        String str=Integer.toHexString(rgb);
        int r=Integer.parseInt(str.substring(2,4),16);
        int g=Integer.parseInt(str.substring(4,6),16);
        int b=Integer.parseInt(str.substring(6,8),16);
        //or 直接new个color对象
        Color c=new Color(rgb);
        r=c.getRed();
        g=c.getGreen();
        b=c.getBlue();
        int top=(r+g+b)/3;
        if(top <= 13 || top >= 190) {
            return 255;
        }
        return (int)(top);
    }

    /**
     * 自己加周围8个灰度值再除以9，算出其相对灰度值
     * @param gray
     * @param x
     * @param y
     * @param w
     * @param h
     * @return
     */
    public static int  getAverageColor(int[][] gray, int x, int y, int w, int h)
    {
        int rs = gray[x][y]
                + (x == 0 ? 255 : gray[x - 1][y])
                + (x == 0 || y == 0 ? 255 : gray[x - 1][y - 1])
                + (x == 0 || y == h - 1 ? 255 : gray[x - 1][y + 1])
                + (y == 0 ? 255 : gray[x][y - 1])
                + (y == h - 1 ? 255 : gray[x][y + 1])
                + (x == w - 1 ? 255 : gray[x + 1][ y])
                + (x == w - 1 || y == 0 ? 255 : gray[x + 1][y - 1])
                + (x == w - 1 || y == h - 1 ? 255 : gray[x + 1][y + 1]);
        return rs / 9;
    }

}

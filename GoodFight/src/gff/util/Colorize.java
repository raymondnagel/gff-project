/*
 * Colorize.java
 * Created on March 26, 2007, 11:18 AM
 * @author rnagel
 */

package gff.util;

import java.awt.Color;
import java.awt.image.BufferedImage;


public class Colorize
{
    private static Color tempColor = null;
   
    public static BufferedImage filter(BufferedImage img, Color filterColor)
    {
        BufferedImage target = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);
        int r;
        int g;
        int b;
        int a;
        for (int x = 0; x < img.getWidth(); x++)
        {
            for (int y = 0; y < img.getHeight(); y++)
            {               
                int srcRGB = img.getRGB(x, y);                               
                r = (srcRGB & 0x00ff0000) >> 16;
                g = (srcRGB & 0x0000ff00) >> 8;
                b = srcRGB & 0x000000ff;
                a = (srcRGB>>24) & 0xff;               
                if (r == g && r == b && a > 0)
                {
                    float color = (float)r/255f;                   
                    r = (filterColor.getRed()+(int)(r*color))/2;
                    g = (filterColor.getGreen()+(int)(g*color))/2;
                    b = (filterColor.getBlue()+(int)(b*color))/2;       
                   
//                    // The lighter, lower-contrast version:
//                    r = (filterColor.getRed()+r)/2;
//                    g = (filterColor.getGreen()+g)/2;
//                    b = (filterColor.getBlue()+b)/2;                   
                }
                tempColor = new Color(r,g,b,a);
                target.setRGB(x, y, tempColor.getRGB());               
            }           
        }
        return target;
    }
}
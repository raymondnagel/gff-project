/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gff.components;

import gff.GoodFight;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

/**
 *
 * @author Ray
 */
public class GameIcon extends InterfaceComponent{
    private BufferedImage myIconImage = null;
    private String myCaption = null;
    private String myShortcut = null;
    
    private static BufferedImage ourGlowImage = GoodFight.getLoadedImage("interface/icon_glow.png");

    public GameIcon(String caption, int fKey, BufferedImage iconImage, Point location) {
        myCaption = caption;
        myIconImage = iconImage;
        myShortcut = "F" + fKey;
        setSize(new Dimension(64, 80));
        setLocation(location);
    }

    @Override
    protected void paintContent(Graphics2D g) {
        
        // Paint the icon's image, if it exists:
        if (myIconImage != null)
        {              
            if (hasMouse())
            {
                g.drawImage(myIconImage, getX(), getY(), null);
                g.drawImage(ourGlowImage, getX(), getY(), null);
            }
            else
            {
                g.drawImage(myIconImage, getX(), getY(), null);
            }
        }
        else
        {
            super.paintContent(g);
        }
        
        // Paint the caption under the image:
        Rectangle2D met = g.getFontMetrics().getStringBounds(myCaption, g);
        g.setColor(Color.WHITE);                        
        g.drawString(myCaption, (int)(getX()+(getWidth()/2)-(met.getWidth()/2)), getY()+getHeight());
        
        // Paint the F_ key shortcut on the top-right corner:
        if (GoodFight.ShowFKeyShortcuts)
        {
            g.setColor(Color.LIGHT_GRAY);
            met = g.getFontMetrics().getStringBounds(myShortcut, g);
            g.fillRect(getX(), getY(), 21, 21);        
            g.setColor(Color.BLACK);        
            g.drawString(myShortcut, getX()+10-(int)(met.getWidth()/2), getY()+((int)met.getHeight()));
        }
    }
    
    
}

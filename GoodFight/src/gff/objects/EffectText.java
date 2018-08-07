/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gff.objects;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

/**
 *
 * @author Ray
 */
public class EffectText {
    private static final long STD_DURATION = 1500;
    private static final float MIN_SCALE = .5f;
    private static final float SCALE_INC = .1f;
    private static final int MAX_SIZE = 20;
    private static final int NUM_INCS = 10;
    private static final int FONT_BASE_MARGIN = 4;
    private static final Font BASE_FONT = new Font("Arial", Font.BOLD, MAX_SIZE);   
    private static final BufferedImage REF_IMG = new BufferedImage(1,1, BufferedImage.TYPE_INT_ARGB);
    private static final Color TEXT_COLOR = Color.WHITE;
    
    private Sprite mySpeaker = null;
    private String myText = null;    
    private long myFirstUpdate = 0;
    private float myAlpha = 1f;
    private float myScale = 0f;
    private BufferedImage myImage = null;
    //private float 
    
    public EffectText(Sprite speaker, String text) {
        mySpeaker = speaker;
        myText = text;
        myFirstUpdate = System.currentTimeMillis();   
        
        Graphics2D g = REF_IMG.createGraphics();
        FontMetrics fMx = g.getFontMetrics(BASE_FONT);  
        
        myImage = new BufferedImage(fMx.stringWidth(myText), fMx.getHeight(), REF_IMG.getType());                
        g = myImage.createGraphics();
        g.setFont(BASE_FONT);        

        // Paint the text:
        g.setColor(TEXT_COLOR);       
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.drawString(myText, 0, fMx.getAscent());
    }    
    
    public void update()
    {
        long elapsed = System.currentTimeMillis() - myFirstUpdate;
        
        myAlpha = (float)elapsed / (float)STD_DURATION;
        if (myAlpha < 0f)        
            myAlpha = 0f;
        myScale = .5f + (myAlpha/2f);
        
        if (elapsed >= STD_DURATION)
        {
            mySpeaker.cancelEffectText();
        }            
    }
    
    public void paint(Graphics2D g)
    {                        
        // Paint the text image:
        Composite oc = g.getComposite();
        AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f-myAlpha);  
        g.setComposite(ac);
        
        float tW = myImage.getWidth() * myScale;
        float tH = myImage.getHeight() * myScale;

        int dx1 = (int)(mySpeaker.getCenterPoint().x - (tW*.5f));
        int dy1 = (mySpeaker.getTop()-FONT_BASE_MARGIN) - (int)tH;
        int dx2 = dx1 + (int)tW;
        int dy2 = dy1 + (int)tH;
        
        g.drawImage(myImage, dx1, dy1, dx2, dy2, 0, 0, myImage.getWidth()-1, myImage.getHeight()-1, null);
        g.setComposite(oc);
    }
}

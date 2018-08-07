/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gff.objects;

import gff.Global;
import gff.GoodFight;
import static gff.objects.TalkBubble.W_MARGIN;
import static gff.objects.TalkBubble.lineDelay;
import static gff.objects.TalkBubble.ourNECorner;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

/**
 *
 * @author Ray
 */
public final class ThinkBubble extends TalkBubble{
    protected static BufferedImage ourThinkTrail = null;
    
    {
        ourThinkTrail = GoodFight.getLoadedImage("interface/thinktrail.png");
    }
    
    public ThinkBubble(Sprite thinker, String text) {
        super(thinker, text);
        //sayWord(null);
    }
    
    @Override
    public void update()
    {        
        myTime -= (System.currentTimeMillis() - myLastUpdate);
        if (myTime <= 0 && myWordIndex < myWords.size())
        {
            myWordIndex += 1;
            myTime = lineDelay;            
        }
        myLastUpdate = System.currentTimeMillis();
        
    }
    
    public void paint(Graphics2D g)
    {                
        // Figure out measurements of the text:
        g.setFont(Global.SimpleSmFont);
        FontMetrics fMx = g.getFontMetrics();
        int tH = fMx.getHeight()*myFormattedText.length;
        int tW = 0;
        int lineW = 0;
        for (int n = 0; n < myFormattedText.length; n++)
        {
            lineW = (int)fMx.getStringBounds(myFormattedText[n], g).getWidth();
            if (lineW > tW)
            {
                tW = lineW;
            }
        }
        
        // Figure out measurements of the bubble:
        int bW = tW + (W_MARGIN*2);
        int bH = tH + (H_MARGIN);           
        if (bW < MIN_WIDTH)
        {
            bW = MIN_WIDTH;
        }
        
        // Paint the bubble:
        int x = mySpeaker.getCenterPoint().x - (bW/2);
        int y = mySpeaker.getTop() - bH;
        
        // Paint the corners. Need images for this because g.fillRoundRect() sucks.
        g.drawImage(ourNECorner, x+bW-ourCornerSize, y, null);
        g.drawImage(ourSECorner, x+bW-ourCornerSize, y+bH-ourCornerSize, null);
        g.drawImage(ourSWCorner, x, y+bH-ourCornerSize, null);
        g.drawImage(ourNWCorner, x, y, null);
        
        // Fill 2 rectangles within the corner images:
        g.setColor(Color.WHITE);
        g.fillRect(x+ourCornerSize, y, bW-(ourCornerSize*2), bH);
        g.fillRect(x, y+ourCornerSize, bW, bH-(ourCornerSize*2));        

        // Make some circles:
        g.drawImage(ourThinkTrail, mySpeaker.getRight(), mySpeaker.getTop()+1, null);
        
        // Paint the text:
        g.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON );
        int numWords = myWordIndex;
        g.setColor(Color.BLACK);        
        for (int s = 0; s < myFormattedText.length; s++)
        {      
            if (numWords > 0)
            {
                if (myFormattedText.length == 1)
                {
                    // If there is only 1 line of text, this code centers it in case
                    // it is shorter than the MIN_WIDTH.
                    numWords = paintWords(myFormattedText[s], mySpeaker.getCenterPoint().x - (tW/2), (y-3)+(fMx.getHeight())+(s*fMx.getHeight()), g, numWords);                        
                }
                else
                {
                    // If there is more than one line, we don't need to center it.
                    // There is virtually no chance of getting a multi-line message
                    // in which EVERY line is shorter than MIN_WIDTH; normal people
                    // just don't talk like that, and there are no cavemen in the game.
                    numWords = paintWords(myFormattedText[s], x+W_MARGIN, (y-3)+(fMx.getHeight())+(s*fMx.getHeight()), g, numWords);
                }
            }
            
            if (myWordIndex >= myWords.size())
            {                
                if (System.currentTimeMillis() % 700 < 350)
                {
                    g.drawImage(GoodFight.getLoadedImage("interface/enter_prompt.png"), x+bW+2, y+bH-17, null);                    
                }
                
            }
        }
    }
        
}

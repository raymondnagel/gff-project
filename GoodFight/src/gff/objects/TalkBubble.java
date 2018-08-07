/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gff.objects;

import gff.Global;
import gff.GoodFight;
import gff.sound.old.Sound;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 *
 * @author Ray
 */
public class TalkBubble {
    protected static long lineDelay = 100;
    
    protected static BufferedImage ourNECorner = null;
    protected static BufferedImage ourSECorner = null;
    protected static BufferedImage ourSWCorner = null;
    protected static BufferedImage ourNWCorner = null;
    
    private static final int TRI_TOP_OFFSET = 3;
    private static final int TRI_V_SD = 10;
    private static final int TRI_H_SD = 10;
    
    protected static final int MIN_WIDTH = 54;
    
    protected static final int W_MARGIN = 4;
    protected static final int H_MARGIN = 1;
    protected static final int MAX_LINE_WIDTH = 42;
    protected Sprite mySpeaker = null;
    protected long myLastUpdate = 0;
    protected long myTime = 0;
    protected String[] myFormattedText = null;  
    protected ArrayList<String> myWords = null;
    protected int myWordIndex = 0;
            
    protected static int ourCornerSize = 0;
    protected static String[] ourMaleSounds = new String[3];
    protected static String[] ourFemaleSounds = new String[3];

    {
        ourMaleSounds[0] = "speech/m1_yada";
        ourMaleSounds[1] = "speech/m2_yada";
        ourMaleSounds[2] = "speech/m3_yada";
        ourFemaleSounds[0] = "speech/f1_yada";
        ourFemaleSounds[1] = "speech/f2_yada";
        ourFemaleSounds[2] = "speech/f3_yada";        
        
        ourNECorner = GoodFight.getLoadedImage("interface/bubble_corner_ne.png");
        ourSECorner = GoodFight.getLoadedImage("interface/bubble_corner_se.png");
        ourSWCorner = GoodFight.getLoadedImage("interface/bubble_corner_sw.png");
        ourNWCorner = GoodFight.getLoadedImage("interface/bubble_corner_nw.png");
        ourCornerSize = ourNECorner.getWidth();
    }
    
    public TalkBubble(Sprite speaker, String text) {
        mySpeaker = speaker;
        myFormattedText = getFormattedStatement(text);  
        myWords = new ArrayList<>();
        for (int f = 0; f < myFormattedText.length; f++)
        {
            String words[] = myFormattedText[f].split(" ");
            for (int w = 0; w < words.length; w++)
            {
                myWords.add(words[w]);
            }
        }
        
        myTime = lineDelay;
        myLastUpdate = System.currentTimeMillis();
    }

    public void update()
    {        
        myTime -= (System.currentTimeMillis() - myLastUpdate);
        if (myTime <= 0 && myWordIndex < myWords.size())
        {
            myWordIndex += 1;
            myTime = lineDelay;
            sayWord(myWords.get(myWordIndex-1));
        }
        myLastUpdate = System.currentTimeMillis();
        
    }

    protected void sayWord(String word)
    {
        if (mySpeaker instanceof Person)
        {
            word = word.toLowerCase();
            Person speaker = (Person)mySpeaker;
            String sound;
            if (speaker.getSex() == GoodFight.Sex.MALE)
            {
                sound = ourMaleSounds[speaker.getVoiceSet()-1];
            }
            else
            {
                sound = ourFemaleSounds[speaker.getVoiceSet()-1];
            }

            GoodFight.playSoundEffect(sound);
            
        }
    }
    
    public String[] getFormattedStatement(String statement)
    {
        StringBuilder builder = new StringBuilder(statement);
        int lineCharCount = 0;
        for (int p = 0; p < builder.length(); p++)
        {
            if (lineCharCount >= MAX_LINE_WIDTH)
            {
                if (builder.charAt(p) == '\n')
                {
                    builder.setCharAt(p, '@');
                    lineCharCount = 0;
                }
                else if (builder.charAt(p) == ' ')
                {
                    builder.setCharAt(p, '@');
                    lineCharCount = 0;
                }
                else if (builder.charAt(p) == '-' && builder.length() > p+1)
                {
                    builder.insert(p+1, '@');
                    lineCharCount = 0;
                    p++;
                }
            }
            lineCharCount++;
        }
        return builder.toString().split("@");
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

        // Make a lil triangle:
        int[] xPoints = {mySpeaker.getRight()+TRI_TOP_OFFSET, mySpeaker.getRight(), mySpeaker.getRight()+TRI_H_SD};
        int[] yPoints = {y+bH, y+bH+TRI_V_SD, y+bH};
        g.fillPolygon(xPoints, yPoints, 3);
                
        // Paint the text:
        g.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON );
        int numWords = myWordIndex;
                
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
    
    protected int paintWords(String text, int startX, int startY, Graphics g, int wordsRemaining)
    {
        g.setColor(Color.BLACK);
        g.setFont(Global.SimpleSmFont);
        FontMetrics fm = g.getFontMetrics();
        int spaceWidth = fm.charWidth(' ');        
        String[] words = text.split(" ");
        int offset = 0;
        for (int w = 0; w < words.length; w++)
        {
            if (wordsRemaining > 0)
            {                
                g.drawString(words[w], startX + offset, startY);
                offset += (int)fm.getStringBounds(words[w], g).getWidth() + spaceWidth;
                wordsRemaining--;
            }
            else
            {
                return wordsRemaining;
            }
        }    
        return wordsRemaining;
    }
}

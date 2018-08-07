/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gff;

import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;

/**
 *
 * @author Raymond
 */
public class WigglingText {
    private String myText = null;
    private int myVar = 0;
    private Point[] myCharPoints = null;

    public WigglingText(String text, int var) {
        myText = text;
        myVar = var;
        myCharPoints = new Point[myText.length()];
        for (int c = 0; c < myCharPoints.length; c++)
        {
            myCharPoints[c] = new Point(0,0);
        }
    }
    
    public void wiggle()
    {
        for (int c = 0; c < myCharPoints.length; c++)
        {
            myCharPoints[c].x = Global.getRandomInt(-myVar, myVar);
            myCharPoints[c].y = Global.getRandomInt(-myVar, myVar);
        }
    }
    
    public void draw(int x, int y, Graphics2D g)
    {
        g.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON );
        FontMetrics fm = g.getFontMetrics();
        y -= fm.getAscent();
        for (int c = 0; c < myText.length(); c++)
        {
            g.drawString(myText.charAt(c)+"", x+myCharPoints[c].x, y+myCharPoints[c].y);
            x += fm.charWidth(myText.charAt(c)) + myVar;
        }
    }
}

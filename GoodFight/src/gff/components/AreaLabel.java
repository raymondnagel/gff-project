/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gff.components;

import gff.Global;
import gff.GoodFight;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;

/**
 * This Label stretches horizontally to accommodate the width of the text.
 * X and Y values in the constructor are for the top/left corner of the Label.
 * @author Ray
 */
public class AreaLabel extends InterfaceComponent{
    
    private Color fgColor = Color.BLACK;
    private Color bgColor = null;    
    private Font font = Global.PrintFont;
    private String[] formattedText = null;

    public AreaLabel(int x, int y, int w, int h, String text, Font font, Color fg, Color bg) {        
        setSize(new Dimension(w, h));
        setText(text);
        if (font != null)
            this.font = font;
        if (fg != null)
            this.fgColor = fg;
        if (bg != null)
            this.bgColor = bg;  
        setX(x);
        setY(y);
    }
    
    public void setFgColor(Color fg)
    {
        this.fgColor = fg;
    }
    public void setBgColor(Color bg)
    {
        this.bgColor = bg;
    }
    
    public final void setText(String text)
    {
        // Width-4 makes sure there will be at least a 2 pixel Margin.
        this.formattedText = Global.getFormattedStatement(text, this.font, getWidth()-4);
    }
    
    private int getMargin()
    {
        Graphics g = GoodFight.getScreenManager().getGraphics();
        int mostPixels = 0;
        for (int f = 0; f < formattedText.length; f++)
        {
            int pixels = (int)g.getFontMetrics(font).getStringBounds(formattedText[f], g).getWidth();
            if (pixels > mostPixels)
                mostPixels = pixels;
        }
        return (getWidth()-mostPixels)/2;
    }
    
    @Override
    protected void paintContent(Graphics2D g) {
        if (bgColor != null)
        {
            g.setColor(bgColor);
            g.fillRect(getX(), getY(), getWidth(), getHeight());
        }
        if (fgColor != null && formattedText != null && formattedText.length > 0)
        {
            g.setColor(fgColor);
            g.setFont(font);
            FontMetrics fm = g.getFontMetrics();
            Rectangle2D bounds = fm.getStringBounds(formattedText[0], g);
            int x = getX();
            int y = getY();
            int h = fm.getAscent();
            
            x += getMargin();
//            if (formattedText.length == 1) // If only one line, center it horizontally:
//            {
//                int lW = (int)bounds.getWidth();
//                x = getX() + ((getWidth()/2)-(lW/2));
//            }
            
            // Always center vertically:
            int tH = (int)((formattedText.length*h)+fm.getDescent());
            y = getY() + ((getHeight()/2)-(tH/2));
                       
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            for (int s = 0; s < formattedText.length; s++)
            {
                g.drawString(formattedText[s], x, y+fm.getAscent());
                y+=h;
            }
        }
    }

    
    
}

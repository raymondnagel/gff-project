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
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

/**
 * This Label stretches horizontally to accommodate the width of the text.
 * X and Y values in the constructor are for the top/left corner of the Label.
 * @author Ray
 */
public class Label extends InterfaceComponent{
    
    private Color fgColor = Color.BLACK;
    private Color bgColor = null;    
    private Font font = Global.GreekMedFont;
    private String text = null;

    public Label(int x, int y, String text, Font font, Color fg, Color bg) {
        FontMetrics fm = GoodFight.getScreenManager().getGraphics().getFontMetrics(font);
        Rectangle2D rect = fm.getStringBounds(text, GoodFight.getScreenManager().getGraphics());
        setSize(new Dimension((int)rect.getWidth(), (int)rect.getHeight()));
        if (font != null)
            this.font = font;
        if (fg != null)
            this.fgColor = fg;
        if (bg != null)
            this.bgColor = bg;
        setX(x);
        setY(y);
        this.text = text;
    }
    
    public void setFgColor(Color fg)
    {
        this.fgColor = fg;
    }
    public void setBgColor(Color bg)
    {
        this.bgColor = bg;
    }
    
    public void setText(String text)
    {
        FontMetrics fm = GoodFight.getScreenManager().getGraphics().getFontMetrics(font);
        Rectangle2D rect = fm.getStringBounds(text, GoodFight.getScreenManager().getGraphics());
        setSize(new Dimension((int)rect.getWidth(), (int)rect.getHeight()));
        this.text = text;
    }
    
    @Override
    protected void paintContent(Graphics2D g) {
        if (bgColor != null)
        {
            g.setColor(bgColor);
            g.fillRect(getX(), getY(), getWidth(), getHeight());
        }
        if (fgColor != null)
        {
            g.setColor(fgColor);
            g.setFont(font);
            g.drawString(text, getX(), getY()+g.getFontMetrics().getAscent());
        }
    }

    
    
}

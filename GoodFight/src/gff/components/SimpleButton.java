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
import java.awt.Point;
import java.awt.geom.Rectangle2D;

/**
 *
 * @author Ray
 */
public class SimpleButton extends InterfaceComponent{
    
    private static final int   TEXT_MARGIN = 4;
    
    private Font font = Global.GreekMedFont;
    private boolean centered = true;
    private Color normalBgColor = Color.GRAY;
    private Color rollBgColor = Color.BLUE;
    private Color pressBgColor = Color.LIGHT_GRAY;
    private Color borderColor = Color.WHITE;
    private Color textColor = Color.WHITE;
    
    private String text = null;

    public SimpleButton(int x, int y, int width, int height, String text, Font f, Color normalBg, Color rollBg, Color pressBg, Color borderFg, Color textFg)
    {
        
        this.font = f;        
        this.setSize(new Dimension(width, height));
        this.setLocation(new Point(x,y));
        this.text = text;
        this.normalBgColor = normalBg;
        this.rollBgColor = rollBg;
        this.pressBgColor = pressBg;
        this.borderColor = borderFg;
        this.textColor = textFg;
        setCentered(true);
    }
    
    public SimpleButton(Point centerLocation, String text, Font f, Color normalBg, Color rollBg, Color pressBg, Color borderFg, Color textFg)
    {
        this(centerLocation, text, f);
        this.normalBgColor = normalBg;
        this.rollBgColor = rollBg;
        this.pressBgColor = pressBg;
        this.borderColor = borderFg;
        this.textColor = textFg;
    }
    
    public SimpleButton(Point centerLocation, String text, Font f) {
        if (f != null)
            this.font = f;
        FontMetrics fm = GoodFight.getScreenManager().getGraphics().getFontMetrics(font);
        Rectangle2D rect = fm.getStringBounds(text, GoodFight.getScreenManager().getGraphics());
        setSize(new Dimension((int)rect.getWidth()+(TEXT_MARGIN*2), (int)rect.getHeight()+(TEXT_MARGIN*2)));
        setLocation(new Point(centerLocation.x - (int)(rect.getWidth()/2), centerLocation.y - (int)(rect.getHeight()/2)));
        this.text = text;   
        setCentered(false);
    }

    public final void setCentered(boolean centered)
    {
        this.centered = centered;
    }
    public boolean isCentered()
    {
        return this.centered;
    }
    
    public void setText(String text)
    {
        this.text = text;
    }
    public String getText()
    {
        return this.text;
    }
    
    @Override
    protected void paintContent(Graphics2D g) {
            if (!hasMouse())
                g.setColor(normalBgColor);
            else if (hasMouse() && !isMousePressed())
                g.setColor(rollBgColor);
            else if (isMousePressed())
                g.setColor(pressBgColor);
               
        g.fillRect(getX(), getY(), getWidth(), getHeight());
        
        g.setColor(borderColor);
        g.draw3DRect(getX(), getY(), getWidth(), getHeight(), !isMousePressed());
        
        g.setFont(font);
        if (isCentered())
        {
            int halfWidth = (int)(g.getFontMetrics().getStringBounds(text, g).getWidth()/2.0);
            int halfHeight = (int)(g.getFontMetrics().getAscent()/2.0);
            g.drawString(text, (getX()+(getWidth()/2))-halfWidth, (getY()+(getHeight()/2)+halfHeight));
        }
        else
            g.drawString(text, getX()+TEXT_MARGIN, getY()+TEXT_MARGIN+g.getFontMetrics().getAscent());
    }

    
    
}

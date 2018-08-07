/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gff.components;

import gff.Global;
import gff.GoodFight;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;

/**
 *
 * @author Ray
 */
public class CheckItem extends InterfaceComponent{
    
    private static final int   TEXT_MARGIN = 4;
    private static final int   CHECK_MARGIN = 2;
    private static final int   CHECK_WIDTH = 16;
    private static final int   HEIGHT = CHECK_WIDTH + (CHECK_MARGIN*2);
    private static final Color NORMAL = Color.YELLOW;
    private static final Color DISABLED = Color.DARK_GRAY;
    private static final Color HIGHLIGHT = Color.BLUE;
    
    private String text = null;
    private boolean enabled = false;
    private boolean checked = false;

    public CheckItem(int x, int y, String text, boolean enabled, boolean checked) {
        FontMetrics fm = GoodFight.getScreenManager().getGraphics().getFontMetrics(Global.GreekMedFont);
        Rectangle2D rect = fm.getStringBounds(text, GoodFight.getScreenManager().getGraphics());
        setSize(new Dimension(CHECK_WIDTH+TEXT_MARGIN+(int)rect.getWidth(), HEIGHT));
        setX(x);
        setY(y);
        this.text = text;        
        this.enabled = enabled;
        this.checked = checked;
    }

    public boolean isEnabled()
    {
        return this.enabled;
    }
    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }
    
    public boolean isChecked()
    {
        return this.checked;
    }
    public void setChecked(boolean checked)
    {
        this.checked = checked;
    }
    
    public String getText()
    {
        return this.text;
    }
    
    @Override
    protected void mousePressAction(MouseEvent e) {
        super.mousePressAction(e);
        if (enabled)
        {
            checked = !checked;
            onChecked(checked);
        }
    }

    public void onChecked(boolean checked)
    {
        
    }
    
    @Override
    protected void paintContent(Graphics2D g) {
            if (!enabled)
                g.setColor(DISABLED);
            else if (!hasMouse())
                g.setColor(NORMAL);
            else if (hasMouse())
                g.setColor(HIGHLIGHT);
               
        g.drawRect(getX(), getY()+CHECK_MARGIN, CHECK_WIDTH, CHECK_WIDTH);
        g.setFont(Global.GreekMedFont);
        g.drawString(text, getX()+CHECK_WIDTH+TEXT_MARGIN, getY()+CHECK_MARGIN+CHECK_WIDTH);

        if (checked)
            g.drawImage(GoodFight.getLoadedImage("interface/check.png"), getX()+1, getY()+CHECK_MARGIN, null);
    }

    
    
}

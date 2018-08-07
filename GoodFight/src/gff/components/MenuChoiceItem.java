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
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;

/**
 *
 * @author Ray
 */
public class MenuChoiceItem extends InterfaceComponent{
    
    private static final Font  FONT = Global.GreekMedFont.deriveFont(30f);
    private static final int   TEXT_MARGIN = 4;
    private static final int   HEIGHT = 32;
    private static final Color NORMAL = Color.LIGHT_GRAY;
    private static final Color DISABLED = Color.DARK_GRAY;
    private static final Color HIGHLIGHT = Color.BLUE;
  
    private String text = null;
    private String description = null;
    private boolean enabled = false;
    private Object choiceObj = null;
    private Menu owningMenu = null;
    
    public MenuChoiceItem(String text, Object choiceObj, String description, boolean enabled) {
        FontMetrics fm = GoodFight.getScreenManager().getGraphics().getFontMetrics(FONT);
        Rectangle2D rect = fm.getStringBounds(text, GoodFight.getScreenManager().getGraphics());
        setSize(new Dimension(TEXT_MARGIN+(int)rect.getWidth(), HEIGHT));
        this.text = text;        
        this.enabled = enabled;
        this.choiceObj = choiceObj;
        this.description = description;
    }
    
    public void setMenu(Menu menu)
    {
        this.owningMenu = menu;
    }
    
    public boolean isEnabled()
    {
        return this.enabled;
    }
    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }
    
    public String getText()
    {
        return this.text;
    }
    
    @Override
    protected void mousePressAction(MouseEvent e) {
        super.mousePressAction(e);
        if (enabled && owningMenu != null)
        {
            owningMenu.setChoice(choiceObj);
        }
    }

    @Override
    protected void mouseMoveAction(MouseEvent e) {
        super.mouseMoveAction(e);
        if (enabled && owningMenu != null)
        {
            owningMenu.setDescriptionLabel(description);
        }
    }

    @Override
    protected void mouseExitAction(MouseEvent e) {
        super.mouseExitAction(e);
        owningMenu.setDescriptionLabel("");
    }

    
    
    @Override
    protected void paintContent(Graphics2D g) {
            if (!enabled)
                g.setColor(DISABLED);
            else if (!hasMouse())
                g.setColor(NORMAL);
            else if (hasMouse())
                g.setColor(HIGHLIGHT);
               
        g.setFont(FONT);
        g.drawString(text, getX()+TEXT_MARGIN, getY()+(HEIGHT-2));
    }

    
    
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gff.components;

import gff.Global;
import gff.GoodFight;
import gff.items.Item;
import gff.util.FinalBypasser;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 *
 * @author Ray
 */
public class Menu extends InterfaceComponent {
    
    private static final int   MARGIN = 6;
    private int menuWidth = 0;
    private int menuHeight = 0;
    private int menuX = 0;
    private int menuY = 0;
    private final FinalBypasser<Object> returnObject = new FinalBypasser<>();
    private Label descriptionLabel = null;

    public Menu(String title, String[] choices, String[] descriptions) {
        FontMetrics fm = GoodFight.getScreenManager().getGraphics().getFontMetrics(Global.GreekMedFont.deriveFont(48f));
        menuHeight = (choices.length*(MARGIN+32)) + (MARGIN*3) + 48;
        menuWidth = (int)fm.getStringBounds(title, GoodFight.getScreenManager().getGraphics()).getWidth() + (MARGIN*2);                                        
        int iY = menuY+MARGIN;        
        
        // Iterate once to determine width:
        fm = GoodFight.getScreenManager().getGraphics().getFontMetrics(Global.GreekMedFont.deriveFont(30f));
        for (int c = 0; c < choices.length; c++)
        {
            int w = (int)fm.getStringBounds(choices[c], GoodFight.getScreenManager().getGraphics()).getWidth() + (MARGIN*2);
            if (w > menuWidth) menuWidth = w;
        }        

        // Set size and location of the menu window:
        setSize(new Dimension(GoodFight.SCREEN_W, menuHeight+MARGIN+30));
        menuX = (GoodFight.SCREEN_W/2) - (menuWidth/2);
        menuY = (GoodFight.SCREEN_H/2) - (menuHeight/2);
        setLocation(new Point(0, menuY));
        
        
        // Add title and choice items:
        Label titleLabel = new Label(0, iY, title, Global.GreekMedFont.deriveFont(48f), Color.WHITE, null);
        titleLabel.setX(menuX+(menuWidth/2)-(titleLabel.getWidth()/2));
        addComponent(titleLabel);
        iY += (titleLabel.getHeight() + MARGIN);
        
        // Iterate again, after width is known, to create choice items:
        for (int c = 0; c < choices.length; c++)
        {
            MenuChoiceItem item = new MenuChoiceItem(choices[c], c, descriptions[c], true);            
            addComponent(item);
            item.setMenu(this);
            item.setY(iY);
            item.setX(menuX + (menuWidth/2) - (item.getWidth()/2));
            iY += (item.getHeight()+MARGIN);
        } 
        
        // Add description label:        
        descriptionLabel = new Label(0, menuHeight+MARGIN, "", Global.GreekMedFont.deriveFont(18f), Color.WHITE, null);
        addComponent(descriptionLabel);
    }

    public void setDescriptionLabel(String text)
    {
        descriptionLabel.setText(text);       
        descriptionLabel.setX(menuX+(menuWidth/2)-(descriptionLabel.getWidth()/2));
    }
    
    public void setChoice(Object choice)
    {
        returnObject.set(choice);
    }            
    public Object getChoice()
    {
        return returnObject.get();
    }
    
    @Override
    protected void paintContent(Graphics2D g) {
        BufferedImage bg = GoodFight.getLoadedImage("bg/menu_bg.png");
        g.drawImage(bg, 0, 0, null);
        bg = GoodFight.getLoadedImage("bg/greystone_bg.png").getSubimage(0, 0, menuWidth, menuHeight);
        g.drawImage(bg, menuX, menuY, null);
        g.setColor(Color.LIGHT_GRAY);
        g.drawRect(menuX, menuY, menuWidth, menuHeight);
        paintComponents(g);
    }
    
}

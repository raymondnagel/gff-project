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
public class Popup extends InterfaceComponent {
    private ArrayList<SimpleButton> choiceButtons = new ArrayList<>();
    private final FinalBypasser<Object> returnObject = new FinalBypasser<>();

    public Popup(int width, int height) {
        setSize(new Dimension(width, height));
        int x = GoodFight.SCENE_CTR_X - (width/2);
        int y = GoodFight.SCENE_CTR_Y - (height/2);
        setLocation(new Point(x, y));
    }

    @Override
    protected void paintContent(Graphics2D g) {
        BufferedImage bg = GoodFight.getLoadedImage("bg/greystone_bg.png").getSubimage(0, 0, getWidth(), getHeight());
        g.drawImage(bg, getLocation().x, getLocation().y, null);
        g.setColor(Color.LIGHT_GRAY);
        g.drawRect(getLocation().x, getLocation().y, getWidth(), getHeight());
        paintComponents(g);
    }
    
    public void addChoiceButton(SimpleButton button)
    {
        choiceButtons.add(button);
        this.addComponent(button);
        int totalButtonWidths = 0;
        for (int b = 0; b < choiceButtons.size(); b++)
        {
            totalButtonWidths += choiceButtons.get(b).getWidth();
        }
        int totalSpaces = getWidth() - totalButtonWidths;
        int spaceBetween = totalSpaces/(choiceButtons.size()+1);
        int y = getHeight()-39;
        int x = spaceBetween;
        for (int b = 0; b < choiceButtons.size(); b++)
        {
            choiceButtons.get(b).setX(x);
            choiceButtons.get(b).setY(y);
            x += (choiceButtons.get(b).getWidth() + spaceBetween);
        }
    }
    
        
    public Object getReturnObject()
    {
        return returnObject.get();
    }
    public void setReturnObject(Object obj)
    {
        returnObject.set(obj);
    }
            
    public static Popup makeSimplePopup(final String text, final String title)
    {
        final int MARGIN = 10;
        final Popup pop = new Popup(512, 150){
            @Override
            protected void paintContent(Graphics2D g) {
                super.paintContent(g);                
                Graphics g2 = g.create(getX(), getY(), getWidth(), getHeight());

                // Draw the title:
                g2.setFont(Global.GreekFont.deriveFont(32f));
                FontMetrics fm = g2.getFontMetrics();
                Rectangle2D bound = fm.getStringBounds(title, g2);
                int tX = (getWidth()/2)-(int)(bound.getWidth()/2);
                int tY = MARGIN;
                g2.drawString(title, tX, tY+fm.getAscent());
                
                // Draw the text:
                if (text != null && text.length() > 0)
                {                
                    g2.setFont(Global.GreekFont.deriveFont(16f));
                    fm = g2.getFontMetrics();
                    int dY = (int)(bound.getHeight() + (MARGIN*2));
                    bound = fm.getStringBounds(text, g2);
                    int dX = (getWidth()/2)-(int)(bound.getWidth()/2);
                    g2.drawString(text, dX, dY+fm.getAscent());
                }
            }            
        };
        return pop;
    }
    
    public static Popup makeMultiChoicePopup(final String[] choices, final String text, final String title)
    {
        final int MARGIN = 10;
        final Popup pop = new Popup(512, 150){
            @Override
            protected void paintContent(Graphics2D g) {
                super.paintContent(g);                
                Graphics g2 = g.create(getX(), getY(), getWidth(), getHeight());

                // Draw the title:
                g2.setFont(Global.GreekFont.deriveFont(32f));
                FontMetrics fm = g2.getFontMetrics();
                Rectangle2D bound = fm.getStringBounds(title, g2);
                int tX = (getWidth()/2)-(int)(bound.getWidth()/2);
                int tY = MARGIN;
                g2.drawString(title, tX, tY+fm.getAscent());
                
                // Draw the text:
                if (text != null && text.length() > 0)
                {                
                    g2.setFont(Global.GreekFont.deriveFont(16f));
                    fm = g2.getFontMetrics();
                    int dY = (int)(bound.getHeight() + (MARGIN*2));
                    bound = fm.getStringBounds(text, g2);
                    int dX = (getWidth()/2)-(int)(bound.getWidth()/2);
                    g2.drawString(text, dX, dY+fm.getAscent());
                }
            }            
        };
        
        for (int c = 0; c < choices.length; c++)
        {
            final Integer choiceNum = new Integer(c);
            SimpleButton optionButton = new SimpleButton(new Point(0,0), choices[c], Global.GreekMedFont) {
                @Override
                protected void mouseReleaseAction(MouseEvent e, boolean stillWithin) {
                    pop.returnObject.set(choiceNum);
                }            
            };
            
            pop.addChoiceButton(optionButton);
        }
        return pop;
    }
    
    public static Popup makeFindItemPopup(final Item item)
    {
        final int MARGIN = 10;
        Popup pop = new Popup(512, 256){
            String[] formattedText = Global.getFormattedStatement(item.getDescription(), Global.GreekFont.deriveFont(16f), 354);
            @Override
            protected void paintContent(Graphics2D g) {
                super.paintContent(g);                
                Graphics g2 = g.create(getX(), getY(), getWidth(), getHeight());

                // Draw the icon:
                int iX = MARGIN;
                int iY = (getHeight()/2) - (item.getIcon().getHeight()/2);
                g2.drawImage(item.getIcon(), iX, iY, null);
                
                // Draw the title:
                g2.setFont(Global.GreekFont.deriveFont(16f));
                FontMetrics fm = g2.getFontMetrics();
                String title = "God hath been gracious unto thee; for thou hast obtained:";
                Rectangle2D titleBound = fm.getStringBounds(title, g2);
                int tX = (getWidth()/2)-(int)(titleBound.getWidth()/2);
                int tY = MARGIN;
                g2.drawString(title, tX, tY+fm.getAscent());
                                                                
                // Draw the item name:
                g2.setFont(Global.GreekFont.deriveFont(Font.BOLD, 32f));                
                int nX = iX+item.getIcon().getWidth()+MARGIN;
                int nY = tY+fm.getHeight()+(MARGIN*2);
                fm = g2.getFontMetrics();
                g2.drawString(item.getName(), nX, nY+fm.getAscent());
                
                // Draw the item description:
                if (formattedText == null || formattedText.length == 0)
                    return;
                
                int dX = nX;
                int dY = nY+fm.getHeight()+MARGIN;
                g2.setFont(Global.GreekFont.deriveFont(16f));
                fm = g2.getFontMetrics();
                for (int s = 0; s < formattedText.length; s++)
                {
                    g2.drawString(formattedText[s], dX, dY+fm.getAscent());
                    dY+=fm.getHeight();
                }
            }
            
        };
        return pop;
    }
    
}

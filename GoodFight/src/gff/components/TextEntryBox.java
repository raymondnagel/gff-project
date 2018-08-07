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
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;

/**
 * @author Ray
 */
public class TextEntryBox extends InterfaceComponent implements KeyboardEditable{
    
    private String allowedCharacterSet = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890() .!?,;:'-";
    private int maxLength = 3;
    private Color fgColor = Color.BLACK;
    private Color bgColor = null;  
    private Color borderColor = null;
    private Font font = Global.GreekMedFont;
    private String text = null;
    private boolean centered = true;
    private boolean focused = false;

    public TextEntryBox(int x, int y, int width, int height, String text, Font font, Color fg, Color bg, Color border) {
        setSize(new Dimension(width, height));
        if (font != null)
            this.font = font;
        if (fg != null)
            this.fgColor = fg;
        if (bg != null)
            this.bgColor = bg;
        if (border != null)
            this.borderColor = border;
        setX(x);
        setY(y);
        setText(text);
    }
    
    public String getText()
    {
        return this.text;
    }
    public final void setText(String text)
    {
        this.text = text;
    }
    
    public void setAllowedCharacters(String characterSet)    
    {
        allowedCharacterSet = characterSet;
    }
    
    public void setMaxLength(int length)
    {
        maxLength = length;
    }
    public int getMaxLength()
    {
        return maxLength;
    }
    
    @Override
    public boolean isEditing()
    {
        return focused;
    }
    @Override
    public void setEditing(boolean editing)
    {
        this.focused = editing;
        if (editing)
        {
            GoodFight.excludeOtherEditingControls(this);
        }
    }
    
    public final void setCentered(boolean centered)
    {
        this.centered = centered;
    }
    public boolean isCentered()
    {
        return this.centered;
    }
    
    protected boolean isBlink()
    {
        return focused && System.currentTimeMillis() % 500 <= 250;
    }
    
    @Override
    protected void paintContent(Graphics2D g) {
        if (bgColor != null)
        {
            g.setColor(bgColor);
            g.fillRect(getX(), getY(), getWidth(), getHeight());
        }
        int bX = -1;
        int bY = -1;
        if (fgColor != null)
        {
            
            g.setColor(fgColor);
            g.setFont(font);
            if (isCentered())
            {
                int halfWidth = (int)(g.getFontMetrics().getStringBounds(text, g).getWidth()/2.0);
                int halfHeight = (int)(g.getFontMetrics().getAscent()/2.0);
                g.drawString(text, (getX()+(getWidth()/2))-halfWidth, (getY()+(getHeight()/2))+halfHeight);
                bX = (getX()+(getWidth()/2))+halfWidth;
                bY = (getY()+(getHeight()/2))+halfHeight;
            }
            else
            {
                g.drawString(text, getX(), getY()+g.getFontMetrics().getAscent());
                bX = (int)(getX()+g.getFontMetrics().getStringBounds(text, g).getWidth());
                bY = getY()+g.getFontMetrics().getAscent();
            }
        }
        
        if (isBlink() && bX != -1 && bY != -1)
        {
            g.setColor(fgColor);
            g.fillRect(bX, bY, 8, 1);
        }
        
        if (borderColor != null)
        {
            g.setColor(borderColor);
            g.drawRect(getX(), getY(), getWidth()-1, getHeight()-1);
        }
    }

    @Override
    public boolean keyTyped(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE)
        {
            if (text.length() > 0)
                text = text.substring(0, text.length()-1);
            return true;
        }
        else if(e.getKeyCode() == KeyEvent.VK_ENTER)
        {
            enterPressed();
            return true;
        }
        else if (e.getKeyCode() == KeyEvent.VK_TAB)
        {
            tabPressed();
            return true;
        }
        else if(e.getKeyCode() == KeyEvent.VK_ESCAPE)
        {
            escapePressed();
            return true;
        }
        else
        {
            char c = e.getKeyChar();
            if (c != KeyEvent.CHAR_UNDEFINED && allowedCharacterSet.contains(Character.toString(c)))
            {
                if (text.length() < maxLength)
                    text = text + c;
                
                return true;
            }
            return false;
        }
    }

    @Override
    public void enterPressed() {
        this.focused = false;  
    }

    @Override
    public void escapePressed() {
        this.focused = false;
    }

    @Override
    public void tabPressed() {
        this.focused = false;
    }

    
    
}

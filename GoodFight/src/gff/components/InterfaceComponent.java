/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gff.components;

import java.awt.AlphaComposite;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

/**
 *
 * @author Ray
 */
public abstract class InterfaceComponent extends MouseAdapter{
    private float myAlphaComposite = 1.0f;
    private Point myLocation = new Point(0, 0);
    private Dimension mySize = new Dimension(0, 0);
    private boolean myVisible = true;
    protected boolean myContainsMouse = false;
    protected boolean myMouseDown = false;
    private boolean myGlobalMouseRelease = false;
    private ArrayList<InterfaceComponent> myComponents = new ArrayList<>();
    
    public void setAlphaComposite(float alpha)
    {
        myAlphaComposite = alpha;
        for (int c = 0; c < getComponentCount(); c++)
        {
            myComponents.get(c).setAlphaComposite(alpha);
        }
    }
    
    public boolean isGlobalMouseRelease()
    {
        return myGlobalMouseRelease;
    }
    public void setGlobalMouseRelease(boolean globalRelease)
    {
        myGlobalMouseRelease = globalRelease;
    }
    
    public void setVisible(boolean visible)
    {
        myVisible = visible;
    }
    
    public boolean isVisible()
    {
        return myVisible;
    }
    
    public void addComponent(InterfaceComponent component)
    {
        myComponents.add(component);
    }
    
    public void removeComponent(InterfaceComponent component)
    {
        myComponents.remove(component);
    }
    
    public void clear()
    {
        myComponents.clear();
    }
    
    public ArrayList<InterfaceComponent> getComponents()
    {
        return myComponents;
    }
    
    public int getComponentCount()
    {
        return myComponents.size();
    }
    
    public void paintComponents(Graphics g)
    {
        Graphics g2 = g.create(getX(), getY(), getWidth(), getHeight());
        for (int c = 0; c < getComponentCount(); c++)
        {
            myComponents.get(c).paint(g2);
        }        
    }
    
    public boolean hasMouse()
    {
        return myContainsMouse;
    }
    
    public boolean isMousePressed()
    {
        return myMouseDown;
    }
    
    public Point getLocation()
    {
        return myLocation;
    }
    public void setLocation(Point p)
    {
        myLocation.x = p.x;
        myLocation.y = p.y;
    }
    
    public final void setSize(Dimension size)
    {
        mySize.width = size.width;
        mySize.height = size.height;
    }
    
    public int getX()
    {
        return myLocation.x;
    }
    public void setX(int x)
    {
        myLocation.x = x;
    }
    public int getY()
    {
        return myLocation.y;
    }
    public void setY(int y)
    {
        myLocation.y = y;
    }
    
    public int getWidth()
    {
        return mySize.width;
    }
    public int getHeight()
    {
        return mySize.height;
    }
    
    public final Rectangle getBounds()
    {
        return new Rectangle(myLocation.x, myLocation.y, mySize.width, mySize.height);
    }
    
    public final void paintIntoRelativeGraphics(Graphics g, int xOffset, int yOffset)
    {
        int x = getX();
        int y = getY();
        setX(xOffset);
        setY(yOffset);
        Graphics2D g2 = (Graphics2D)g;
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, myAlphaComposite));         
        paintContent(g2);
        paintComponents(g2);                    
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
        setX(x);
        setY(y);
    }
    
    public final void paint(Graphics g)
    {
        Graphics2D g2 = (Graphics2D)g;
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, myAlphaComposite));         
        if (isVisible())
        {                  
            paintContent(g2);
            paintComponents(g2);            
        }
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
    }
    
    protected void paintContent(Graphics2D g)
    {}

    @Override
    public final void mouseEntered(MouseEvent e) {
        if (isVisible())
        {
            myContainsMouse = true;
            mouseEnterAction(e);
        }
    }
    protected void mouseEnterAction(MouseEvent e)
    {}

    @Override
    public final void mouseExited(MouseEvent e) {
        if (isVisible())
        {
            myContainsMouse = false;
            mouseExitAction(e);
        }
    }
    protected void mouseExitAction(MouseEvent e)
    {}

    @Override
    public final void mouseMoved(MouseEvent e) {
        if (isVisible())
        {         
            boolean stolen = false;
            for (int c = 0; c < myComponents.size(); c++)
            {                
                InterfaceComponent comp = myComponents.get(c);
                Rectangle bounds = (Rectangle)comp.getBounds().clone();
                bounds.translate(getX(), getY());
                if (bounds.contains(e.getPoint()))
                {
                    stolen = true;
                    if (!comp.hasMouse())
                    {
                        // The mouse wasn't here before, but now it is:
                        comp.mouseEntered(e);
                    }
                    comp.mouseMoved(e);
                }
                else if (comp.hasMouse())
                {
                    // The mouse was here before, but now it isn't:
                    comp.mouseExited(e);
                }
            }
            if (!stolen)
                mouseMoveAction(e);
        }
    }
    protected void mouseMoveAction(MouseEvent e)
    {}

    @Override
    public final void mouseDragged(MouseEvent e) {
        if (isVisible())
        {            
            boolean stolen = false;
            for (int c = 0; c < myComponents.size(); c++)
            {                
                InterfaceComponent comp = myComponents.get(c);
                Rectangle bounds = (Rectangle)comp.getBounds().clone();
                bounds.translate(getX(), getY());
                if (bounds.contains(e.getPoint()))
                {
                    stolen = true;
                    if (!comp.hasMouse())
                    {
                        // The mouse wasn't here before, but now it is:
                        comp.mouseEntered(e);
                    }
                    comp.mouseMoved(e);
                }
                else if (comp.hasMouse())
                {
                    // The mouse was here before, but now it isn't:
                    comp.mouseExited(e);
                }
            }
            if (!stolen)
                mouseDragAction(e);
        }
    }
    protected void mouseDragAction(MouseEvent e)
    {}
    
    @Override
    public final void mousePressed(MouseEvent e) {
        if (isVisible())
        {            
            boolean stolen = false;
            // See if the press was "stolen" by a sub-component:
            for (int c = 0; c < myComponents.size(); c++)
            {
                InterfaceComponent comp = myComponents.get(c);
                Rectangle bounds = (Rectangle)comp.getBounds().clone();
                bounds.translate(getX(), getY());
                if (bounds.contains(e.getPoint()))
                {                    
                    comp.mousePressed(e);
                    stolen = true;
                }                
            }  
            if (!stolen)
            {
                myMouseDown = true;
                mousePressAction(e);
            }
        }
    }
    protected void mousePressAction(MouseEvent e)
    {}

    @Override
    public final void mouseReleased(MouseEvent e) {   
        boolean stolen = false;
        if (isVisible())
        {
            myMouseDown = false;            
            for (int c = 0; c < myComponents.size(); c++)
            {
                InterfaceComponent comp = myComponents.get(c);
                Rectangle bounds = (Rectangle)comp.getBounds().clone();
                bounds.translate(getX(), getY());
                if (bounds.contains(e.getPoint()))
                {                    
                    comp.mouseReleased(e);
                    stolen = true;
                }                
            }        
            mouseReleaseAction(e, !stolen && this.hasMouse());
        }
    }
    protected void mouseReleaseAction(MouseEvent e, boolean stillWithin)
    {}

}

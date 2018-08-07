/**
 * CompManip.java
 * Created on February 8, 2007, 1:39 PM
 * @author rnagel
 */

package gff.util;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import javax.swing.Icon;
import javax.swing.JLabel;

/**
 * This class contains static methods for manipulating visual components.
 * I think its functionality should really be broken up when it
 * includes more methods.
 * 
 * @author   rnagel
 * @version  1.0
 * @since    JDK1.6.1
 */
public final class CompManip
{
    //____________________PUBLIC STATIC METHODS____________________//
    
   /**
    * Centers a <code>Component</code> within the specified parent
    * <code>Container</code>.
    * 
    * @param     comp      the <code>Component</code> to center
    * @param     parent    any <code>Container</code> which contains
    *            the <code>Component</code>
    */
    public static void centerInContainer(Component comp, Container parent)
    {
        comp.setLocation( (parent.getWidth()/2)-(comp.getWidth()/2),
                          (parent.getHeight()/2)-(comp.getHeight()/2) );
    }
    
    public static void centerInContainerHorz(Component comp, Container parent)
    {
        comp.setLocation( (parent.getWidth()/2)-(comp.getWidth()/2),
                          comp.getY() );
    }
    
    public static void centerInContainerVert(Component comp, Container parent)
    {
        comp.setLocation( comp.getX(),
                          (parent.getHeight()/2)-(comp.getHeight()/2) );
    }
    
   /**
    * Centers one Window over another Window.
    * 
    * @param     dlg    the Window (usually a dialog) to center
    * @param     win    the Window over which <code>dlg</code> is centered   
    */
    public static void centerOverWindow(Window dlg, Window win)
    {
        dlg.setLocation( win.getX() + (win.getWidth()/2)-(dlg.getWidth()/2),
                         win.getY() + (win.getHeight()/2)-(dlg.getHeight()/2) );
    }


    public static void resizeDefinite(Component comp, Dimension newSize)
    {   
        comp.setPreferredSize(newSize);
        comp.setMinimumSize(newSize);
        comp.setMaximumSize(newSize);
        comp.setSize(newSize);
    }
    
    public static int getXOnScreen(Component c, int xInCntnr)
    {
        return c.getLocationOnScreen().x + xInCntnr;
    }
    
    public static int getYOnScreen(Component c, int yInCntnr)
    {
        return c.getLocationOnScreen().y + yInCntnr;
    }
    
    public static Point getLocalPoint(Component c, Point pt)
    {
        return new Point(pt.x - c.getLocationOnScreen().x, pt.y - c.getLocationOnScreen().y);
    }
    
    public static Point translateLocalPoint(Component fromComponent, Component toComponent, Point localPoint)
    {
        Point p = getPointOnScreen((Container)fromComponent, localPoint);
        return getLocalPoint((Container)toComponent, p);
    }
    
    public static Point getPointOnScreen(Container c, Point pt)
    {
        return new Point(getXOnScreen(c,pt.x), getYOnScreen(c,pt.y));
    }
    
    public static void centerToCompHorz(Component centerThis, Component centerTo)
    {
        int ctr = centerTo.getX() + (centerTo.getWidth() / 2);
        centerThis.setLocation(ctr - (centerThis.getWidth() / 2), centerThis.getY());
    }
    
    public static void centerToCompVert(Component centerThis, Component centerTo)
    {
        int ctr = centerTo.getY() + (centerTo.getHeight() / 2);
        centerThis.setLocation(centerThis.getX(), ctr - (centerThis.getHeight() / 2));
    }
    
    public static void centerInScreen(Window w)
    {
        w.setLocationRelativeTo(null);
//        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
//        int x = (d.width/2);
//        int y = (d.height/2);
//        w.setLocation(new Point(x-(w.getWidth()/2), y-(w.getHeight()/2)));
    }
    
    
    public static ArrayList<Component> getDescendentComponents(Container c)
    {
        ArrayList<Component> v = new ArrayList<Component>();        
        v = recurseSubComponents(c, v);
        return v;
    }
    
    private static ArrayList<Component> recurseSubComponents(Container c, ArrayList<Component> v)
    {
        v.add(c);
        for (Component sub:c.getComponents())
        {
            if (sub instanceof Container)
                v = recurseSubComponents((Container)sub, v);
            else
                v.add(sub);
        }
        return v;
    }
    
    public static void autoSizeJLabel(JLabel j)
    {
        boolean textWasNull = false;
        Insets ins = j.getInsets();        
        Graphics g = j.getGraphics();
        if (j.getText() == null)
        {
            textWasNull = true;
            j.setText(" ");
        }
        FontMetrics fm = g.getFontMetrics();
        Rectangle2D r = fm.getStringBounds(j.getText(), g);
        int iconW = 0;
        Icon icon = j.getIcon();
        if (icon != null)
            iconW = icon.getIconWidth() + j.getIconTextGap();
        CompManip.resizeDefinite(j, new Dimension((int)r.getWidth()+ins.left+ins.right+iconW, (int)r.getHeight()+ins.top+ins.bottom));
        if (textWasNull)
        {
            j.setText(null);
        }
    }
    
    public static ArrayList<Component> getAncestors(Component c)
    {
        ArrayList<Component> ancs = new ArrayList<Component>();
        while (c.getParent() != null)
        {
            c = c.getParent();
            ancs.add(c);
        }
        return ancs;
    }
    
    public static void centerOverUsableDesktop(Window win)
    {
        Rectangle r = getUsableDesktopArea(win.getGraphicsConfiguration());
        win.setLocation( r.x + (r.width/2)-(win.getWidth()/2),
                         r.y + (r.height/2)-(win.getHeight()/2) );
    }
    
    public static Rectangle getUsableDesktopArea(GraphicsConfiguration config)
    {
        int w = config.getDevice().getDisplayMode().getWidth();
        int h = config.getDevice().getDisplayMode().getHeight();
        Insets insets = Toolkit.getDefaultToolkit().getScreenInsets(config);
        return new Rectangle(insets.left, insets.top, w - (insets.left+insets.right), h - (insets.top+insets.bottom));
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gff.objects.scenery;

import gff.GoodFight.Dir;
import gff.Scene;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;

/**
 *
 * @author Ray
 * 
 * For this class, "Enter" means going into the Portal;
 * "Exit" means coming out from the Portal.
 * 
 */
public class Portal extends Scenery {
    
    private Scene scene = null;
    private Point exitPosition = null;    
    private Dir enterDirection = null;
    
    private Portal exitPortal = null;
    
    
    public Portal(int x, int y, int w, int h, Scene scene, Point exitPosition, Dir enterDirection) {
        super(null, x, y);
        setSize(new Dimension(w, h));
        Point basePt = getRelativeBasePoint();
        setRelativePhysicalShape(new Rectangle(basePt.x-10, basePt.y-10, 20, 10));  
        this.scene = scene;
        this.exitPosition = exitPosition;
        this.enterDirection = enterDirection;
    }    

    public Scene getScene()
    {
        return this.scene;
    }
    
    public void setExitPortal(Portal exit)
    {
        this.exitPortal = exit;
    }
    
    public Portal getExitPortal()
    {
        return this.exitPortal;
    }
    
    public Dir getEnterDirection()
    {
        return this.enterDirection;
    }
    public Dir getExitDirection()
    {
        return this.exitPortal.enterDirection;
    }    
    public Point getExitPosition()
    {
        return this.exitPosition;
    }
    
    
    public static void link(Portal portal1, Portal portal2)
    {
        portal1.setExitPortal(portal2);
        portal2.setExitPortal(portal1);
    }
}
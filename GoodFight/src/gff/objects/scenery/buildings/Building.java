/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gff.objects.scenery.buildings;

import gff.maps.Map;
import gff.Scene;
import gff.objects.scenery.Portal;
import gff.objects.scenery.Scenery;
import java.awt.image.BufferedImage;

/**
 *
 * @author Raymond
 */
public abstract class Building extends Scenery{
    private Map map = null;
    private Scene scene = null;
    private Portal innerPortal = null;
    private Portal outerPortal = null;
    
    
    
    public Building(BufferedImage image, int x, int y) {
        super(image, x, y);        
    }
    
    public void setContainingScene(Scene scene)
    {
        this.scene = scene;
    }
    
    public Scene getContainingScene()
    {
        return this.scene;
    }
    
    public void setMap(Map map)
    {
        this.map = map;
    }
    
    public Map getMap()
    {
        return this.map;
    }
    
    public Portal getInnerPortal()
    {
        return this.innerPortal;
    }
    public Portal getOuterPortal()
    {
        return this.outerPortal;
    }
    
    public Scene getEntranceRoomScene()
    {
        return this.innerPortal.getScene();
    }
    
    public void setEntrancePortals(Portal inner, Portal outer)
    {
        this.innerPortal = inner;
        this.outerPortal = outer;
        Portal.link(inner, outer);
    }
}

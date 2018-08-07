/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gff.objects.scenery.buildings;

import gff.Church;
import gff.maps.ChurchMap;
import gff.GoodFight;
import java.awt.Point;
import java.awt.Rectangle;

/**
 *
 * @author Raymond
 */
public class ChurchBuilding extends Building {
    
    private Church church = null;

    public ChurchBuilding() {
        super(GoodFight.getLoadedImage("sprites/church.png"), 0, 0);
        Point basePt = getRelativeBasePoint();
        setRelativePhysicalShape(new Rectangle(basePt.x-100, basePt.y-30, 200, 30));
        setMap(new ChurchMap(this));
    }
    
    public void setChurch(Church church)
    {
        this.church = church;
    }
    public Church getChurch()
    {
        return this.church;
    }
}

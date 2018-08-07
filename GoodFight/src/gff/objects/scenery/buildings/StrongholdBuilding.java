/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gff.objects.scenery.buildings;

import gff.GoodFight;
import gff.Stronghold;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

/**
 *
 * @author Raymond
 */
public abstract class StrongholdBuilding extends Building {
    
    private Stronghold stronghold = null;

    public StrongholdBuilding(BufferedImage image) {
        super(image, 0, 0);
        Point basePt = getRelativeBasePoint();
        setRelativePhysicalShape(new Rectangle(basePt.x-90, basePt.y-50, 180, 50));
    }
    
    public void setStronghold(Stronghold stronghold)
    {
        this.stronghold = stronghold;
    }
    public Stronghold getStronghold()
    {
        return this.stronghold;
    }
}

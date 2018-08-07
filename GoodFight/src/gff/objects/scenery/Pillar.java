/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gff.objects.scenery;

import gff.GoodFight;
import java.awt.Point;
import java.awt.Rectangle;

/**
 *
 * @author Ray
 */
public class Pillar extends Scenery{

    public Pillar(int x, int y)
    {
        super(GoodFight.getLoadedImage("sprites/pillar1.png"), x, y);
        Point basePt = getRelativeBasePoint();
        setRelativePhysicalShape(new Rectangle(basePt.x-16, basePt.y-20, 32, 20));
    }

}
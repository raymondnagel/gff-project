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
public class Cactus3 extends Scenery{

    public Cactus3(int x, int y)
    {
        super(GoodFight.getLoadedImage("sprites/cactus3.png"), x, y);
        Point basePt = getRelativeBasePoint();
        setRelativePhysicalShape(new Rectangle(basePt.x-23, basePt.y-20, 46, 20));
    }

}

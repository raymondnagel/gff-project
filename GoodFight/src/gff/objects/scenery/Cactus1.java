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
public class Cactus1 extends Scenery{

    public Cactus1(int x, int y)
    {
        super(GoodFight.getLoadedImage("sprites/cactus1.png"), x, y);
        Point basePt = getRelativeBasePoint();
        setRelativePhysicalShape(new Rectangle(basePt.x-8, basePt.y-20, 16, 20));
    }

}

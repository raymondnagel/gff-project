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
public class Cactus4 extends Scenery{

    public Cactus4(int x, int y)
    {
        super(GoodFight.getLoadedImage("sprites/cactus4.png"), x, y);
        Point basePt = getRelativeBasePoint();
        setRelativePhysicalShape(new Rectangle(basePt.x-44, basePt.y-30, 88, 30));
    }

}

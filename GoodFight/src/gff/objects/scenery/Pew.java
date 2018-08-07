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
public class Pew extends Scenery{

    public Pew(int x, int y)
    {
        super(GoodFight.getLoadedImage("sprites/pew.png"), x, y);
        Point basePt = getRelativeBasePoint();
        setRelativePhysicalShape(new Rectangle(basePt.x-70, basePt.y-12, 140, 12));
    }

}

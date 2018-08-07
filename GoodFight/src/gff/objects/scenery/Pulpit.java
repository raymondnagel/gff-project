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
public class Pulpit extends Scenery{

    public Pulpit(int x, int y)
    {
        super(GoodFight.getLoadedImage("sprites/pulpit.png"), x, y);
        Point basePt = getRelativeBasePoint();
        setRelativePhysicalShape(new Rectangle(basePt.x-9, basePt.y-7, 18, 7));
    }

}

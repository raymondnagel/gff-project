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
public class PineTree extends Scenery{

    public PineTree(int x, int y)
    {
        super(GoodFight.getLoadedImage("sprites/pine_tree.png"), x, y);
        Point basePt = getRelativeBasePoint();
        setRelativePhysicalShape(new Rectangle(basePt.x-7, basePt.y-20, 14, 20));
    }

}

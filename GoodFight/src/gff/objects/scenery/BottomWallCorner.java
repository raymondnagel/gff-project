/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gff.objects.scenery;

import gff.GoodFight;
import java.awt.Rectangle;

/**
 *
 * @author Ray
 */
public class BottomWallCorner extends Scenery{

    public BottomWallCorner(int x, int y)
    {
        super(GoodFight.getLoadedImage("sprites/bottom_wall_corner.png"), x+25, y+50);
        setRelativePhysicalShape(new Rectangle(0, 0, getWidth(), getHeight()));
    }

}

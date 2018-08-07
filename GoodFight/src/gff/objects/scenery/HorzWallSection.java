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
public class HorzWallSection extends Scenery{

    public HorzWallSection(int x, int y)
    {
        super(GoodFight.getLoadedImage("sprites/h_wall_section.png"), x+38, y+50);
        setRelativePhysicalShape(new Rectangle(0, 0, getWidth(), getHeight()));
    }

}

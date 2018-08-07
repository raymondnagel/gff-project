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
public class VertWallSection extends Scenery{

    public VertWallSection(int x, int y)
    {
        super(GoodFight.getLoadedImage("sprites/v_wall_section.png"), x+25, y+58);
        setRelativePhysicalShape(new Rectangle(0, 0, getWidth(), getHeight()));
    }

}

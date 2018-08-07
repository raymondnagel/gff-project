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
public class Lake3 extends Scenery{

    public Lake3(int x, int y)
    {
        super(GoodFight.getLoadedImage("sprites/lake3.png"), x, y);
        setRelativePhysicalShape(new Rectangle(0, 0, getWidth(), getHeight()));
    }

}

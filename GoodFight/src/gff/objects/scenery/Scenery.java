/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gff.objects.scenery;

import gff.objects.Thing;
import java.awt.image.BufferedImage;

/**
 *
 * @author Ray
 */
public abstract class Scenery extends Thing{    

    public Scenery(BufferedImage image, int x, int y) {
        super(image, x, y);
    }

}

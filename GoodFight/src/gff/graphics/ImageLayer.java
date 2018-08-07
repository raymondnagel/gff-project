/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gff.graphics;

import gff.GoodFight;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 *
 * @author Raymond Nagel
 */
public class ImageLayer implements SceneLayer
{
    private BufferedImage myImage = null;
    private boolean myVisible = true;

    public ImageLayer(int imageType)
    {
        myImage = new BufferedImage(GoodFight.SCENE_W, GoodFight.SCENE_H, imageType);
    }    
    
    public ImageLayer(BufferedImage image)
    {
        myImage = image;
    }

    public int getWidth() {
        return GoodFight.SCREEN_W;
    }

    public int getHeight() {
        return GoodFight.SCREEN_H;
    }

    
    
    public boolean isVisible()
    {
        return myVisible;
    }

    public void draw(Graphics2D g)
    {
        if (myImage != null)
        {
            g.drawImage(myImage, GoodFight.SCENE_X, GoodFight.SCENE_Y, null);
        }
        
    }

}

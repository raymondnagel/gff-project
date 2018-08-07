/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gff.graphics;

import gff.GoodFight;
import gff.Scene;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 *
 * @author Raymond Nagel
 */
public class BackgroundLayer implements SceneLayer
{
    private BufferedImage myImage = null;
    private boolean myVisible = true;

    public BackgroundLayer(int imageType)
    {
        myImage = new BufferedImage(GoodFight.SCENE_W, GoodFight.SCENE_H, imageType);
    }    
    
    public BackgroundLayer(BufferedImage image)
    {
        myImage = image;
    }

    public int getWidth() {
        return GoodFight.SCENE_W;
    }

    public int getHeight() {
        return GoodFight.SCENE_H;
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
        drawDirectionalTransitions(g);
    }

    private void drawDirectionalTransitions(Graphics2D g)
    {
        // NORTH
        Scene scene = GoodFight.getCurrentScene();
        Scene neighbor = scene.getNeighbor(GoodFight.Dir.N);
        if (neighbor != null && (neighbor.getTerrain() != scene.getTerrain()))
        {
            BufferedImage trans = null;
            switch(neighbor.getTerrain())
            {
                case PLAIN:
                case FOREST:
                    trans = GoodFight.getLoadedImage("bg/grass_n.png");
                    break;
                case DESERT:
                    trans = GoodFight.getLoadedImage("bg/desert_n.png");
                    break;
                case MOUNTAIN:
                    trans = GoodFight.getLoadedImage("bg/rocky_n.png");
                    break;
                case SWAMP:
                    trans = GoodFight.getLoadedImage("bg/swamp_n.png");
                    break;
                case TUNDRA:
                    trans = GoodFight.getLoadedImage("bg/tundra_n.png");
                    break;
            }
            if (trans != null)
            {
                g.drawImage(trans, GoodFight.SCENE_LEFT, GoodFight.SCENE_TOP, null);
            }
        }
        
        // EAST
        neighbor = scene.getNeighbor(GoodFight.Dir.E);
        if (neighbor != null && (neighbor.getTerrain() != scene.getTerrain()))
        {
            BufferedImage trans = null;
            switch(neighbor.getTerrain())
            {
                case PLAIN:
                case FOREST:
                    trans = GoodFight.getLoadedImage("bg/grass_e.png");
                    break;
                case DESERT:
                    trans = GoodFight.getLoadedImage("bg/desert_e.png");
                    break;
                case MOUNTAIN:
                    trans = GoodFight.getLoadedImage("bg/rocky_e.png");
                    break;
                case SWAMP:
                    trans = GoodFight.getLoadedImage("bg/swamp_e.png");
                    break;
                case TUNDRA:
                    trans = GoodFight.getLoadedImage("bg/tundra_e.png");
                    break;
            }
            if (trans != null)
            {
                g.drawImage(trans, GoodFight.SCENE_RIGHT-(trans.getWidth()-1), GoodFight.SCENE_TOP, null);
            }
        }
        
        // SOUTH
        neighbor = scene.getNeighbor(GoodFight.Dir.S);
        if (neighbor != null && (neighbor.getTerrain() != scene.getTerrain()))
        {
            BufferedImage trans = null;
            switch(neighbor.getTerrain())
            {
                case PLAIN:
                case FOREST:
                    trans = GoodFight.getLoadedImage("bg/grass_s.png");
                    break;
                case DESERT:
                    trans = GoodFight.getLoadedImage("bg/desert_s.png");
                    break;
                case MOUNTAIN:
                    trans = GoodFight.getLoadedImage("bg/rocky_s.png");
                    break;
                case SWAMP:
                    trans = GoodFight.getLoadedImage("bg/swamp_s.png");
                    break;
                case TUNDRA:
                    trans = GoodFight.getLoadedImage("bg/tundra_s.png");
                    break;
            }
            if (trans != null)
            {
                g.drawImage(trans, GoodFight.SCENE_LEFT, GoodFight.SCENE_BOTTOM-(trans.getHeight()-1), null);
            }
        }
        
        // WEST
        neighbor = scene.getNeighbor(GoodFight.Dir.W);
        if (neighbor != null && (neighbor.getTerrain() != scene.getTerrain()))
        {
            BufferedImage trans = null;
            switch(neighbor.getTerrain())
            {
                case PLAIN:
                case FOREST:
                    trans = GoodFight.getLoadedImage("bg/grass_w.png");
                    break;
                case DESERT:
                    trans = GoodFight.getLoadedImage("bg/desert_w.png");
                    break;
                case MOUNTAIN:
                    trans = GoodFight.getLoadedImage("bg/rocky_w.png");
                    break;
                case SWAMP:
                    trans = GoodFight.getLoadedImage("bg/swamp_w.png");
                    break;
                case TUNDRA:
                    trans = GoodFight.getLoadedImage("bg/tundra_w.png");
                    break;
            }
            if (trans != null)
            {
                g.drawImage(trans, GoodFight.SCENE_LEFT, GoodFight.SCENE_TOP, null);
            }
        }
    }
}

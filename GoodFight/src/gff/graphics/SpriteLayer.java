/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gff.graphics;

import gff.GoodFight;
import gff.objects.scenery.Scenery;
import gff.objects.Sprite;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 *
 * @author Raymond Nagel
 */
public class SpriteLayer implements SceneLayer
{
    public static enum TYPE {GROUND, ACTION, SKY};
    
    private TYPE myType = TYPE.ACTION;    
    private boolean myVisible = true;
    private boolean mySceneryModified = false;
    private ArrayList<Scenery> myScenery = new ArrayList<>();
    private static Comparator<Scenery> ourSceneryComparator = null;

    public SpriteLayer(TYPE type)
    {       
        myType = type;
        if (ourSceneryComparator == null)
        {
            ourSceneryComparator = new Comparator<Scenery>(){
                @Override
                public int compare(Scenery o1, Scenery o2) {
                    return o1.getBottom() - o2.getBottom();
                }            
            };
        }
    }        
    
    public TYPE getType()
    {
        return myType;
    }
    
    public ArrayList<Scenery> getSceneryObjects()
    {
        return myScenery;
    }
    
    public void addScenery(Scenery scenery)
    {
        myScenery.add(scenery);
        mySceneryModified = true;
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

    // Because this is the sprite layer, other non-scenery Sprites will be drawn
    // at the same time; therefore we have to get the global collection of non-scenery
    // Sprites and incorporate them into the draw order as appropriate (in order of
    // their "base").
    public void draw(Graphics2D g)
    {
        // Remove all Scenery that is marked for destruction:
        for (int r = myScenery.size()-1; r >= 0; r--)
        {                         
            if (myScenery.get(r).shouldBeDestroyed())
            {
                myScenery.remove(r);
                mySceneryModified = true;
            }
        }
        
        if (mySceneryModified)
        {
            Collections.sort(myScenery, ourSceneryComparator);
            mySceneryModified = false;
        }
        
        ArrayList<Sprite> charSprites = GoodFight.getSprites();
        
        int lastBase = -1;
        for (int s = 0; s < myScenery.size(); s++)
        {
            Scenery scenery = myScenery.get(s);
            
            // Draw any character Sprites that should appear between the last scenery and this one:
            if (myType == TYPE.ACTION)
            {                
                for (int c = 0; c < charSprites.size(); c++)
                {                    
                    if (lastBase != scenery.getBottom() && charSprites.get(c).getBottom() >= lastBase && charSprites.get(c).getBottom() < scenery.getBottom())
                    {
                        charSprites.get(c).render(g);
                    }
                }
            }
            
            // Draw the current scenery in the back-to-front order:
            scenery.render(g);
            lastBase = scenery.getBottom();
        }
        
        // One more iteration is needed to draw Sprites that are in front of all scenery:
        if (myType == TYPE.ACTION)
        {                
            for (int c = 0; c < charSprites.size(); c++)
            {                    
                if (charSprites.get(c).getBottom() >= lastBase)
                {
                    charSprites.get(c).render(g);
                }
            }
        }
    }

}

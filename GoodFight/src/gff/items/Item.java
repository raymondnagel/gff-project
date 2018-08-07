/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gff.items;

import gff.Global;
import gff.GoodFight;
import java.awt.image.BufferedImage;

/**
 *
 * @author Ray
 */
public abstract class Item {
    
    private String myName = null;
    private String myDescription = null;
    private BufferedImage myIcon = null;

    public Item()
    {
        
    }
    
    public Item(String name, String description, BufferedImage icon) {
        myName = name;
        myDescription = description;
        myIcon = icon;
    }
    
    public String getName()
    {
        return myName;
    }
    public void setName(String name)
    {
        myName = name;
    }
    public String getDescription()
    {
        return myDescription;
    }
    public void setDescription(String description)
    {
        myDescription = description;
    }
    public BufferedImage getIcon()
    {
        return myIcon;
    }
    
    public void onAcquire()
    {
        Global.log("Obtained " + this.getName() + ".");  
        GoodFight.doShowItemPopup(this);
    }
    
    
}

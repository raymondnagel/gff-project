/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gff.items;

import gff.GoodFight;
import java.awt.image.BufferedImage;

/**
 *
 * @author Ray
 */
public abstract class Armour extends Item {

    private String defendText = null;
    
    public Armour(String name, String description, String defendText, BufferedImage img) {
        super(name, description, img);
        this.defendText = defendText;
    }

    public String getDefendText()
    {
        return this.defendText;
    }
    
    public String getSoundName()
    {
        return "clink";
    }
    
    public int getBlockPct()
    {
        return 15;
    }
    
    @Override
    public void onAcquire() {
        super.onAcquire();        
        GoodFight.getSubject().acquireArmour(this);
    }
    
}

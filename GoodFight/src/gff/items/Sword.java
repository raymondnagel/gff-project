/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gff.items;

import gff.GoodFight;

/**
 *
 * @author Raymond
 */
public class Sword extends Armour {

    public Sword() {
        super("Sword of the Spirit", "For the word of God is quick, and powerful, and sharper than any twoedged sword, piercing even to the dividing asunder of soul and spirit, and of the joints and marrow, and is a discerner of the thoughts and intents of the heart. (Hebrews 4:12)", "Parried by the Sword of the Spirit!", GoodFight.getLoadedImage("items/am_sword.png"));
    }
    
    @Override
    public String getSoundName()
    {
        return "parry";
    }

    @Override
    public int getBlockPct() {
        return GoodFight.getDifficulty().getSwordBlockPct();
    }
    
    
}

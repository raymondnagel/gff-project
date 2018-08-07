/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gff.items;

import gff.GoodFight;

/**
 *
 * @author Ray
 */
public class StrongMeat extends Item {

    public StrongMeat() {
        super("Strong Meat", "\"But strong meat belongeth to them that are of full age, even those who by reason of use have their senses exercised to discern both good and evil.\" (Hebrews 5:14)", GoodFight.getLoadedImage("items/strong_meat.png"));
    }

    @Override
    public void onAcquire() {
        super.onAcquire();
        int faith = (int)(.75f * (float)(GoodFight.getSubject().getMaxFaith()));
        GoodFight.getSubject().restoreFaith(faith);
    }
    
}

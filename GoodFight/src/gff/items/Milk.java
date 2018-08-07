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
public class Milk extends Item {

    public Milk() {
        super("Milk", "\"As newborn babes, desire the sincere milk of the word, that ye may grow thereby:\" (1 Peter 2:2)", GoodFight.getLoadedImage("items/milk.png"));
    }

    @Override
    public void onAcquire() {
        super.onAcquire();
        int faith = (int)(.25f * (float)(GoodFight.getSubject().getMaxFaith()));
        GoodFight.getSubject().restoreFaith(faith);
    }
    
}

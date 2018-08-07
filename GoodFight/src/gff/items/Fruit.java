/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gff.items;

import gff.Global;
import gff.GoodFight;

/**
 *
 * @author Ray
 */
public class Fruit extends Item {

    public Fruit(String fruitName, String description) {
        super(fruitName, description, GoodFight.getLoadedImage("items/fruit_" + fruitName.toLowerCase() + ".png"));
    }

    @Override
    public void onAcquire() {
        super.onAcquire();
        GoodFight.getSubject().acquireFruit(this);
    }
    
}

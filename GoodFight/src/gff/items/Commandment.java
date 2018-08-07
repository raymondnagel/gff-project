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
public class Commandment extends Item {

    public Commandment(int num, String description) {
        super("Commandment #" + num, description, GoodFight.getLoadedImage("items/cmd" + num + ".png"));
    }

    @Override
    public void onAcquire() {
        super.onAcquire();
        GoodFight.getSubject().acquireCommandment(this);
    }
    
}

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
public class Shield extends Armour {

    public Shield() {
        super("Shield of Faith", "Above all, taking the shield of faith, wherewith ye shall be able to quench all the fiery darts of the wicked. (Ephesians 6:16)", "Blocked by the Shield of Faith!", GoodFight.getLoadedImage("items/am_shield.png"));
    }
    
}

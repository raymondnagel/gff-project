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
public class Meat extends Item {

    public Meat() {
        super("Meat", "\"Jesus saith unto them, My meat is to do the will of him that sent me, and to finish his work.\" (John 4:34)", GoodFight.getLoadedImage("items/meat.png"));
    }

    @Override
    public void onAcquire() {
        super.onAcquire();
        int faith = (int)(.5f * (float)(GoodFight.getSubject().getMaxFaith()));        
        GoodFight.getSubject().restoreFaith(faith);
    }
    
}

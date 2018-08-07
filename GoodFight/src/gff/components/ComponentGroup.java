/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gff.components;

import gff.Controllable;
import gff.GoodFight;
import java.awt.Graphics;
import java.util.ArrayList;

/**
 *
 * @author Ray
 */
public class ComponentGroup extends InterfaceComponent implements Controllable {

    // This must be overridden because a ComponentGroup does not have its own
    // content area.
    @Override
    public void paintComponents(Graphics g)
    {
        ArrayList<InterfaceComponent> comps = getComponents();
        for (int c = 0; c < getComponentCount(); c++)
        {
            comps.get(c).paint(g);
        }        
    }
    
    @Override
    public void keysAreDown(boolean[] keyMap) {
        
    }

    @Override
    public void keyPressed(int keyCode) {
        
    }

    @Override
    public void keyReleased(int keyCode) {
        
    }

    @Override
    public boolean isControlled() {
        return GoodFight.getCurrentComponents().contains(this);
    }

    @Override
    public void setControlled(boolean controlled) {
        
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gff;

/**
 *
 * @author Raymond Nagel
 */
public interface Controllable
{
    public void keysAreDown(boolean[] keyMap);
    public void keyPressed(int keyCode);
    public void keyReleased(int keyCode);
    public boolean isControlled();
    public void setControlled(boolean controlled);
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gff.components;

import java.awt.event.KeyEvent;

/**
 *
 * @author Raymond
 */
public interface KeyboardEditable {
    public boolean isEditing();
    public void setEditing(boolean editing);
    
    // Returns true if the keyPress was consumed. Some keys may not be used by the editor:
    public boolean keyTyped(KeyEvent e);
    
    public void enterPressed();
    public void escapePressed();
    public void tabPressed();    
}

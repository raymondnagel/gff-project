/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gff;

import java.awt.Graphics;
import java.awt.event.KeyEvent;

/**
 *
 * @author Ray
 * 
 * A SpecialEvent represents a game event that progresses in a series of iterations,
 * being updated and drawn in every cycle of the main loop. There can only be one
 * current SpecialEvent at any given time.
 * 
 * Examples of SpecialEvents include scene transitions, cutscenes, or mass
 * scenery-change animations.
 * 
 * For smaller effects that can occur simultaneously, use the SpecialEffect interface.
 */
public interface SpecialEvent {
    public abstract void start();
    public abstract void onFinish();
    public abstract void doIteration();
    public abstract boolean isFinished();
    public abstract boolean disablesNormalCycle();
    public abstract boolean useKeyPress(KeyEvent e);
    public abstract void draw(Graphics g);
}

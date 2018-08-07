/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gff;

import java.awt.Graphics;

/**
 *
 * @author Ray
 * 
 * A SpecialEffect represents a game effect that progresses in a series of iterations,
 * being updated and drawn in every cycle of the main loop. There can be multiple
 * SpecialEffects active simultaneously, as they are independent of one another.
 * 
 * Examples of SpecialEffects include explosions, small scenery changes, or other
 * relatively small instance animations.
 * 
 * For smaller effects that can occur simultaneously, use the SpecialEffect interface.
 */
public interface SpecialEffect {
    public abstract void start();
    public abstract void doIteration();
    public abstract boolean isFinished();
    public abstract void draw(Graphics g);
}

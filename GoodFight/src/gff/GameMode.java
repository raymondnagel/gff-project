/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gff;

import java.awt.Graphics2D;
import java.awt.event.KeyListener;

/**
 *
 * @author Raymond
 */
public interface GameMode extends KeyListener{
    public abstract String getName();
    public abstract void switchTo();
    public abstract void switchFrom();
    public abstract void cycle();    
    public abstract void draw(Graphics2D g);
}

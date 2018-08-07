/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gff.spritecommands;

/**
 *
 * @author Raymond
 */
public abstract class SpriteCommand {
    public void start()
    {
        onStart();
    }
    public void stop()
    {
        onStop();
    }
    protected void onStart() {};
    protected void onStop() {};
    public void iterate() {};
    public abstract boolean isDone();
}

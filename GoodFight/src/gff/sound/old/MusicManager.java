/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gff.sound.old;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 *
 * @author rnagel
 */
public class MusicManager implements LineListener
{
    private PCMFilePlayer pcmPlayer = null;
    private boolean shouldLoop = false;

    public MusicManager()
    {
        
    }

    @Override
    public void update(LineEvent event)
    {      
        // We are only interested in STOP events; all other event types can be ignored.
        // We also want to make sure the Line is for the current pcmPlayer; we don't want
        // to loop anything that isn't current; and this method is only for looping.
        if (event.getType() == LineEvent.Type.STOP && event.getLine() == pcmPlayer.getLine() && (!pcmPlayer.terminated) && shouldLoop)
        {            
            if (shouldLoop)
            {
                // Get the gain value:
                FloatControl control = (FloatControl)event.getLine().getControl(FloatControl.Type.MASTER_GAIN);
                float value = control.getValue();
                // Restart the file:
                loopMusic(pcmPlayer.getFile());
                // Use the previous gain value for the new player:
                control = (FloatControl)pcmPlayer.getLine().getControl(FloatControl.Type.MASTER_GAIN);
                control.setValue(limit(control,value));
            }
        }
    }

    private void loopMusic(File soundFile)
    {
        try
        {
            this.shouldLoop = true;       
            pcmPlayer = new PCMFilePlayer(soundFile);
            pcmPlayer.getLine().addLineListener(this);
            pcmPlayer.start();
        } catch (IOException | UnsupportedAudioFileException | LineUnavailableException ex)
        {
            System.err.println(ex.getMessage());
        }
    }
    
    public void startMusic(File soundFile, boolean loop)
    {        
        try
        {
            if (pcmPlayer != null)
            {                
                stop(); 
            }            
            this.shouldLoop = loop;
            pcmPlayer = new PCMFilePlayer(soundFile);
            pcmPlayer.getLine().addLineListener(this);
            pcmPlayer.start();
        } catch (IOException | UnsupportedAudioFileException | LineUnavailableException ex)
        {
            System.err.println(ex.getMessage());
        }
    }
    
    // Use this method if we want to start again from the same position, not terminate:
    public void setPaused(boolean paused)
    {
        if (paused)
        {
            pcmPlayer.getLine().removeLineListener(this);
            pcmPlayer.stop();
        }
        else
        {
            pcmPlayer.getLine().addLineListener(this);
            pcmPlayer.start();
        }
    }
    
    public void stop()
    {      
        //System.out.println("MusicManager stop() is requesting PCMFilePlayer to terminate...");
        pcmPlayer.terminate();             
    }

    public File getCurrentlyPlayingFile()
    {
        if (pcmPlayer == null)
            return null;
        else
        {
            return pcmPlayer.getFile();
        }
    }
    
    public void fadeVolume(float change)
    {
        FloatControl control = (FloatControl)pcmPlayer.getLine().getControl(FloatControl.Type.MASTER_GAIN);
        float value = control.getValue();
        control.setValue(limit(control,value+change));
    }
    public float getCurrentVolume()
    {
        FloatControl control = (FloatControl)pcmPlayer.getLine().getControl(FloatControl.Type.MASTER_GAIN);
        return control.getValue();
    }
    
    // Returns whichever is less out of these two:
    // 1: the control's maximum volume
    // 2: whichever is greater: the control's minimum volume or the specified level
    // In other words, it returns the specified level unless it's outside the control's range.
    private static float limit(FloatControl control,float level)
    { return Math.min(control.getMaximum(), Math.max(control.getMinimum(), level)); }
}

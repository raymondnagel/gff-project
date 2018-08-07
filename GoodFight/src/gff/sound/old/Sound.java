/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gff.sound.old;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.CompoundControl;
import javax.sound.sampled.Control;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;

/**
 *
 * @author rnagel
 */
public class Sound
{
    private AudioFormat myAudioFormat = null;
    private Clip        myClip = null;


    public Sound(String filename)
    {
        this(new File(filename));
    }

    public Sound(File file)
    {
        try
        {
            myAudioFormat = AudioSystem.getAudioFileFormat(file).getFormat();
            AudioInputStream aiStream = AudioSystem.getAudioInputStream(myAudioFormat, AudioSystem.getAudioInputStream(file));
            DataLine.Info info = new DataLine.Info(Clip.class, myAudioFormat);
            myClip = (Clip)AudioSystem.getLine(info);
            myClip.open(aiStream);
            myClip.setFramePosition(0);
            setVolume(.01f);
        } catch (Exception ex)
        {
            Logger.getLogger(Sound.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


    public void setVolume(float volume)
    {
        Control[] controls = myClip.getControls();
        Control volControl = null;
        for (Control c : controls) {

            if (c.getType() == FloatControl.Type.VOLUME) {
                volControl = c;
            }

            if (c.getType() instanceof CompoundControl.Type) {
                for (Control c2 : ((CompoundControl) c).getMemberControls()) {
                    if (c2.getType() == FloatControl.Type.VOLUME) {
                        volControl = c2;
                    }
                }
            }
            if (volControl != null) {
                ((FloatControl)volControl).setValue(volume);
                break;
            }
        }
        
    }
    public void setGain(float gain)
    {
        FloatControl gainControl = (FloatControl) myClip.getControl(FloatControl.Type.MASTER_GAIN);
        gainControl.setValue(gain);
    }


    public AudioFormat getFormat()
    {
        return myAudioFormat;
    }
    public Clip getClip()
    {
        return myClip;
    }

    public void start()
    {
        myClip.start();
    }
    public void stop()
    {
        myClip.stop();
    }
    public void reset()
    {
        myClip.setFramePosition(0);
    }

    public boolean isPlaying()
    {
        return myClip.isActive();
    }

    public void startAt(int frameNo)
    {
        myClip.setFramePosition(frameNo);
        start();
    }
    
    public void loop(int count)
    {
        myClip.loop(count);
    }
}

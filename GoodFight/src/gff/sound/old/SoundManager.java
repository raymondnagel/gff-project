/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gff.sound.old;

import java.util.ArrayList;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;

/**
 *
 * @author rnagel
 */
public class SoundManager implements LineListener
{
    public static final int SOUND_DISTANCE = 1000;
    private ArrayList<Sound>   mySounds = null;
    private int             myMaxSimultaneousSounds = 0;

    public SoundManager(int maxSounds)
    {
        mySounds = new ArrayList<>(maxSounds);
        myMaxSimultaneousSounds = maxSounds;
    }

    public void update(LineEvent event)
    {
        if (event.getType() == LineEvent.Type.STOP)
        {
            ((Clip)event.getLine()).drain();
            ((Clip)event.getLine()).close();
        }
        else if (event.getType() == LineEvent.Type.CLOSE)
        {
            for (int s = mySounds.size()-1; s >= 0; s--)
            {
                if (!mySounds.get(s).getClip().isOpen())
                {
                    mySounds.remove(s);
                }
            }
        }
    }

    public boolean playSound(Sound sound)
    {
        if (mySounds.size() < myMaxSimultaneousSounds)
        {
            sound.getClip().addLineListener(this);
            mySounds.add(sound);
            sound.start();
            return true;
        }
        return false;
    }


    public boolean loopSound(Sound sound, int count)
    {
        if (mySounds.size() < myMaxSimultaneousSounds)
        {
            sound.getClip().addLineListener(this);
            mySounds.add(sound);
            sound.loop(count);
            return true;
        }
        return false;
    }
}

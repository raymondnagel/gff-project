/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gff.graphics;

import gff.GoodFight;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 *
 * @author Raymond
 */
public class Animation
{
    private ArrayList<AnimFrame>    myFrames = new ArrayList<AnimFrame>();
    private boolean                 myRepeat = false;
    private boolean                 myDone = false;
    private String                  myName = null;
    private int                     myIndex = 0;
    private int                     myCycleCount = 0;

    public Animation(String name, boolean repeating, int w, int wGap, int commonDuration)
    {
        myName = name;
        myRepeat = repeating;
        BufferedImage sheet = GoodFight.getLoadedImage("anims/" + name + ".png");
        int count = sheet.getWidth()/w;
        
        for (int f = 0; f < count; f++){
            AnimFrame frame = AnimFrame.loadFrame(sheet, name + f, f*(w+wGap), 0, w, sheet.getHeight(), commonDuration);
            myFrames.add(frame);
        }
        reset();
    }
    
    public Animation(String name, boolean repeating, ArrayList<AnimFrame> frames)
    {
        myName = name;
        myRepeat = repeating;
        myFrames.addAll(frames);
        reset();
    }
    public Animation(String name, boolean repeating, String[] filenames)
    {
        myName = name;
        myRepeat = repeating;
        for (int f = 0; f < filenames.length; f++)
            myFrames.add(AnimFrame.getFrameByFilename(filenames[f]));
        reset();
    }

    public BufferedImage getImage()
    {
        return myFrames.get(myIndex).getImage();
    }

    public boolean isRepeating()
    {
        return myRepeat;
    }

    public boolean isDone()
    {
        return myDone;
    }

    public String getName()
    {
        return myName;
    }

    public int getCurrentIndex()
    {
        return myIndex;
    }

    public void advance()
    {
        if (!myDone)
        {
            myCycleCount++;
            if (myCycleCount >= myFrames.get(myIndex).getDuration())
            {
                myCycleCount = 0;
                myIndex++;
                if (myIndex >= myFrames.size())
                {
                    if (myRepeat)
                    {
                        myIndex = 0;
                    }
                    else
                    {
                        myDone = true;
                        myIndex--;
                        done();
                    }
                }
            }
        }
    }

    public void reset()
    {
        myIndex = 0;
        myCycleCount = 0;
    }

    public void done()
    {
        
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gff.sound.old;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.*;

public class PCMFilePlayer implements Runnable
{

    File file;
    AudioInputStream in;
    SourceDataLine line;
    int frameSize;
    byte[] buffer = new byte[32 * 1024]; // 32k is arbitrary
    Thread playThread;
    boolean playing;
    boolean notYetEOF;
    boolean terminated;

    public PCMFilePlayer(File f)
            throws IOException,
            UnsupportedAudioFileException,
            LineUnavailableException
    {
        file = f;
        in = AudioSystem.getAudioInputStream(f);
        AudioFormat format = in.getFormat();
        AudioFormat.Encoding formatEncoding = format.getEncoding();
        if (!(formatEncoding.equals(AudioFormat.Encoding.PCM_SIGNED)
                || formatEncoding.equals(AudioFormat.Encoding.PCM_UNSIGNED)))
        {
            throw new UnsupportedAudioFileException(
                    file.getName() + " is not PCM audio");
        }
        //System.out.println ("got PCM format");
        frameSize = format.getFrameSize();
        DataLine.Info info
                = new DataLine.Info(SourceDataLine.class, format);
        //System.out.println ("got info");
        line = (SourceDataLine) AudioSystem.getLine(info);
        //System.out.println ("got line");
        line.open();
        //System.out.println ("opened line");
        playThread = new Thread(this, "PCM: " + f.getName());
        playing = false;
        notYetEOF = true;
        terminated = false;
    }

    @Override
    public void run()
    {
        int readPoint = 0;
        int bytesRead = 0;

        try
        {
            while (notYetEOF && !terminated)
            {
                if (playing)
                {
                    bytesRead = in.read(buffer,
                            readPoint,
                            buffer.length - readPoint);
                    if (bytesRead == -1)
                    {
                        notYetEOF = false;
                        break;
                    }
                    // how many frames did we get,
                    // and how many are left over?
                    int frames = bytesRead / frameSize;
                    int leftover = bytesRead % frameSize;
                    // send to line
                    line.write(buffer, readPoint, bytesRead - leftover);
                    // save the leftover bytes
                    System.arraycopy(buffer, bytesRead,
                            buffer, 0,
                            leftover);
                    readPoint = leftover;
                }
                else
                {
                    // If not playing (it may be "paused"); this doesn't
                    // necessarily mean that it won't continue playing!
                    Thread.yield();
                }
            } // while notYetEOF && !terminated
            System.out.println("PCMFilePlayer is done because it " + (!notYetEOF ? "reached EOF" : "") + (terminated ? "was terminated" : ""));
            
            if (!notYetEOF)
                line.drain();
            
            line.stop();
        } catch (IOException ioe)
        {
            ioe.printStackTrace();
        } finally
        {
            line.close();
        }
    } // run

    public void start()
    {
        playing = true;
        if (!playThread.isAlive())
        {
            playThread.start();
        }
        line.start();
    }

    public void stop()
    {
        playing = false;
        line.stop();
    }

    public void terminate()
    {
        // Called when we are done with this player.
        playing = false;
        terminated = true;
    }

    public SourceDataLine getLine()
    {
        return line;
    }

    public File getFile()
    {
        return file;
    }
}

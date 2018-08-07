/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gff.sound.old;

/**
 *
 * @author rnagel
 */
import gff.Global;
import javax.sound.midi.*;
import java.io.*;
import javax.swing.SwingWorker;


public class SimpleMidiPlayer {
    private static File     myMidiFile = null;
    private static Sequencer mySequencer = null;
    private static SwingWorker myMonitor = null;

    private static boolean myLoop = false;

    public static void playMidiFile(File midiFile, final boolean loopForever)
    {   
        myLoop = loopForever;
        myMidiFile = midiFile;
        myMonitor = new SwingWorker() {
            @Override
            protected Object doInBackground() throws Exception 
            {
                try {
                    mySequencer = MidiSystem.getSequencer();
                    mySequencer.setSequence(MidiSystem.getSequence(myMidiFile));
                    mySequencer.open();                    
                } catch(MidiUnavailableException mue) {
                    Global.log("Midi device unavailable!");
                } catch(InvalidMidiDataException imde) {
                    Global.log("Invalid Midi data!");
                } catch(IOException ioe) {
                    Global.log("I/O Error!");
                } 
                
                do
                {
                    mySequencer.setTickPosition(0);
                    mySequencer.start();
                    while(mySequencer.isRunning()) 
                    {                
                        try {
                            Thread.sleep(1000); // Check every second
                        } catch(InterruptedException ignore) {
                            break;
                        }
                    }
                } while (myLoop);
                mySequencer.stop();
                mySequencer.close();
                return this;
            }                
        };        
        myMonitor.execute();
    }
    
    public static void stop()
    {
        if (mySequencer.isRunning())
        {
            myLoop = false;
            mySequencer.stop();
        }
    }
}

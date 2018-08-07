/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gff;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Keeps track of multiple [type:double] entries which change over time at
 * a constant rate.
 * 
 * To use this class, call the update() method at regular intervals (in a loop)
 * along with the rate at which it should change. It returns the value as it should
 * be right now. This allows the rate to be changed over time via the parameter.
 * 
 * @author Raymond
 */
public class TimingRegister {
    private static HashMap<String, Long> entries = new HashMap<>();
    
    public static double update(String id, double currentVal, double changePerMilli)
    {
        long now = System.currentTimeMillis(); 
        long then = now;
        if (entries.containsKey(id))
        {
            then = entries.get(id);
        }           
        entries.put(id, now);
        long elapsed = now - then;
        return currentVal + (((double)elapsed)*changePerMilli);
    }
    
    public static void listAllEntries()
    {
        if (!entries.isEmpty())
        {
            System.err.println("Remaining TimingRegister entries:");
            Iterator<String> iter = entries.keySet().iterator();
            while (iter.hasNext())
            {
                System.err.println(iter.next());
            }
        }
    }
    
    public static void remove(String id)
    {
        entries.remove(id);
    }
}

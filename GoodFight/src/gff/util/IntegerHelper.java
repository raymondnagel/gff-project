/**
 * IntegerHelper.java
 * Created on May 8, 2007, 5:21 PM
 * @author rnagel
 */

package gff.util;

/** 
 * <i>IntegerHelper</i> contains various <code>public static</code> methods that
 * simplify some <code>int</code>-related tasks. This class contains no constructors
 * and should never be instantiated.
 *
 * @author  rnagel
 * @version 1.0
 * @see     Integer
 * @since   JDK1.6.1
 */
public final class IntegerHelper
{
    //____________________PUBLIC STATIC METHODS____________________//
    
   /**
    * Returns the highest positive <code>int</code> in the specified array.
    * 
    * @param     numbers    an array of <code>int</code>s to search
    * @return    the highest positive <code>int</code> found, or -1 if no
    *            positive <code>int</code> was found in the array.
    */ 
    public static int findHighestPositiveInt(int[] numbers)
    {
        int high = -1;
        for (int i:numbers)
        {
            if (i > high) high = i;
        }
        return high;
    }
   /**
    * Subtracts an array of <code>int</code><i>subtrahends</i> from an array of 
    * <code>int</code> <i>minuends</i> and returns an array of <code>int</code>
    * <i>differences</i>.
    * Each element in the returned array is the result of the corresponding
    * minuend minus the corresponding subtrahend:<blockquote>
    * returnValue[n] = minuends[n] - subtrahends[n]</blockquote>
    * 
    * @param    minuends        an array of <code>int</code> minuends.
    * @param    subtrahends     an array of <code>int</code> subtrahends.
    * @return   an array of <code>int</code> differences
    */ 
    public static int[] subtractArray(int[] minuends, int[] subtrahends)
    {
        int[] differences = new int[minuends.length];
        for (int i = 0; i < minuends.length; i++)
        {
            differences[i] = minuends[i] - subtrahends[i];
        }
        return differences;
    }    
    
   /**
    * Subtracts a single <code>int</code><i>subtrahend</i> from an array of 
    * <code>int</code> <i>minuends</i> and returns an array of <code>int</code>
    * <i>differences</i>.
    * Each element in the returned array is the result of the corresponding
    * minuend minus the subtrahend:<blockquote>
    * returnValue[n] = minuends[n] - subtrahends</blockquote>
    * 
    * @param    minuends        an array of <code>int</code> minuends.
    * @param    subtrahend      a single <code>int</code> subtrahend.
    * @return   an array of <code>int</code> differences
    */ 
    public static int[] subtractArray(int[] minuends, int subtrahend)
    {
        int[] differences = new int[minuends.length];
        for (int i = 0; i < minuends.length; i++)
        {
            differences[i] = minuends[i] - subtrahend;
        }
        return differences;
    }
    
    public static boolean isAnInteger(String text)
    {
        boolean returnVal = true;
        try 
        {
            Integer.parseInt(text);
        }
        catch (NumberFormatException ex)
        {
            returnVal = false;
        }
        finally
        {
            return returnVal;
        }
    }
             
    
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gff.util;

/**
 *
 * @author Raymond
 * 
 * This class was written to be a simple wrapper that can be declare "final" to
 * allow access to anonymous (inner) classes, but still allow the value to be changed.
 */
public class FinalBypasser<T> {
    private T myObject = null;
    
    public T get()
    {
        return myObject;
    }
    public void set(T object)
    {
        myObject = object;
    }
}

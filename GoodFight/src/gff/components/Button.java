/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gff.components;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.image.BufferedImage;

/**
 *
 * @author Ray
 */
public abstract class Button extends InterfaceComponent{
    private BufferedImage myNormalImage = null;
    private BufferedImage myRollOverImage = null;
    private BufferedImage myClickedImage = null;

    public Button(BufferedImage normalImg, BufferedImage rollImg, BufferedImage clickImg, Point location) {
        myNormalImage = normalImg;
        myRollOverImage = rollImg;
        myClickedImage = clickImg;
        setSize(new Dimension(normalImg.getWidth(), normalImg.getHeight()));
        setLocation(location);
    }

    
}

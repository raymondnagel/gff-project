/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gff.objects;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

/**
 *
 * @author rnagel
 */
public abstract class Rotatable extends Thing
{    
    private double          myAngle = 0.0;
    protected final static double RADIANS = 2 * Math.PI;

    public Rotatable(BufferedImage image, int x, int y) {
        super(image, x, y);
    }

    
    
    public void setAngle(double theta)
    {
        myAngle = theta;
        while (myAngle > RADIANS)
        {
            myAngle -= RADIANS;
        }
        while (myAngle < 0)
        {
            myAngle += RADIANS;
        }
    }
    public double getAngle()
    {
        return myAngle;
    }

    private Point2D.Double getDir()
    {
        double pct = myAngle / RADIANS;
        double horz = 0;
        double vert = 0;
        int xMod = pct >= .5 ? -1 : 1;
        int yMod = pct >= .25 && pct < .75 ? 1 : -1;
        double m = 0;
        if (xMod == 1)
            m = pct;
        else
            m = pct-.5;

        horz = .25 - (Math.abs(.25-m));
        vert = Math.abs(.25 - horz);
        horz *= xMod;
        vert *= yMod;
        return new Point2D.Double(horz, vert);
    }

    public double getXDir()
    {
        Point2D.Double dir = getDir();
        return ( Math.abs(dir.x) / (Math.abs(dir.x)+Math.abs(dir.y)) ) * Math.signum(dir.x);
    }
    public double getYDir()
    {
        Point2D.Double dir = getDir();
        return ( Math.abs(dir.y) / (Math.abs(dir.y)+Math.abs(dir.x)) ) * Math.signum(dir.y);
    }

    public Point2D.Double getAdjustedDir()
    {
        Point2D.Double dir = getDir();
        double xDir = Math.abs(dir.x) / (Math.abs(dir.x)+Math.abs(dir.y));
        double yDir = Math.abs(dir.y) / (Math.abs(dir.y)+Math.abs(dir.x));
        double dist = Math.sqrt((xDir*xDir)+(yDir*yDir));
        xDir = xDir/dist;
        yDir = yDir/dist;
        return new Point2D.Double(xDir*Math.signum(dir.x), yDir*Math.signum(dir.y));
    }

    public void rotate(double rotation)
    {
        setAngle(myAngle+rotation);
    }

    @Override
    public void render(Graphics2D g)
    {
        Point p = getCenterPoint();
        g.rotate(myAngle, p.x, p.y);
        paint(g);
        g.rotate(-myAngle, p.x, p.y);
    }

}

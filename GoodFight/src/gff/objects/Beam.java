/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gff.objects;

import gff.GoodFight;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;

/**
 *
 * @author Raymond
 */
public class Beam extends Drawing
{
    private Point myStart = null;
    private Point myEnd = null;
    private Color myColor = null;
    private float myWidth = 0.0f;

    public Beam(Point startPt, Point endPt, Color color, float width)
    {
        super(1);
        myStart = startPt;
        myEnd = endPt;
        myColor = color;
        myWidth = width;
    }

    public void draw(Graphics2D g)
    {
        int sx = 0;
        int sy = 0;
        g.setColor(myColor);
        g.setStroke(new BasicStroke(myWidth));
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.drawLine(myStart.x - sx, myStart.y - sy, myEnd.x - sx, myEnd.y - sy);
        g.setColor(Color.WHITE);
        g.setStroke(new BasicStroke(1.0f));
        g.drawLine(myStart.x - sx, myStart.y - sy, myEnd.x - sx, myEnd.y - sy);
    }


}

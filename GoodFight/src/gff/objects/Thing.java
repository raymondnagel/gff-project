/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gff.objects;

import gff.Global;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;

/**
 *
 * @author Ray
 */
public abstract class Thing {
    protected boolean         myDestroyWhenPossible = false;
    protected BufferedImage   myImage = null;
    protected Point           myLocation = new Point(0,0);
    protected Dimension       mySize = new Dimension(0,0);
    protected Shape           myRelativePhysicalShape = null;
    
    public Thing()
    {
        
    }
    
    public void destroy()
    {
        myDestroyWhenPossible = true;
    }
    
    public boolean shouldBeDestroyed()
    {
        return myDestroyWhenPossible;
    }
    
    public Thing(BufferedImage image)
    {
        setImage(image);
    }
    
    public Thing(BufferedImage image, int x, int y)
    {
        setImage(image);
        setBasePoint(new Point(x, y));
    }
    
    public Shape getRelativePhysicalShape()
    {
        return myRelativePhysicalShape;
    }
    public void setRelativePhysicalShape(Shape physicalBounds)
    {
        myRelativePhysicalShape = physicalBounds;
    }
    public Shape getPhysicalShape()
    {
        if (myRelativePhysicalShape instanceof Rectangle)
        {
            Rectangle newShape = new Rectangle((Rectangle)myRelativePhysicalShape);
            newShape.translate(getX(), getY());
            return newShape;
        }
        else if (myRelativePhysicalShape instanceof Polygon)
        {                   
            Polygon oldShape = (Polygon)myRelativePhysicalShape;
            Polygon newShape = new Polygon(oldShape.xpoints.clone(), oldShape.ypoints.clone(), oldShape.npoints);
            newShape.translate(getX(), getY());
            return newShape;
        }
        else if (myRelativePhysicalShape instanceof Ellipse2D.Float)
        {                   
            Ellipse2D.Float oldShape = (Ellipse2D.Float)myRelativePhysicalShape;
            Ellipse2D.Float newShape = new Ellipse2D.Float(oldShape.x, oldShape.y, oldShape.width, oldShape.height);
            newShape.setFrame(oldShape.x+getX(), oldShape.y+getY(), oldShape.width, oldShape.height);
            return newShape;
        }
        return null;
    }
    
    public int getPhysicalWidth()
    {
        return getPhysicalShape().getBounds().width;
    }
    public int getPhysicalHeight()
    {
        return getPhysicalShape().getBounds().height;
    }
    public int getPhysicalLeft()
    {
        return getPhysicalShape().getBounds().x;
    }
    public int getPhysicalRight()
    {
        return getPhysicalShape().getBounds().x+getPhysicalShape().getBounds().width;
    }
    public int getPhysicalTop()
    {
        return getPhysicalShape().getBounds().y;
    }
    public int getPhysicalBottom()
    {
        return getPhysicalShape().getBounds().y+getPhysicalShape().getBounds().height;
    }
    
    public int getLeftMargin()
    {
        return myRelativePhysicalShape.getBounds().x;
    }
    public int getRightMargin()
    {
        return getWidth() - (myRelativePhysicalShape.getBounds().x + myRelativePhysicalShape.getBounds().width);
    }
    public int getTopMargin()
    {
        return myRelativePhysicalShape.getBounds().y;
    }
    public int getBottomMargin()
    {
        return getHeight() - (myRelativePhysicalShape.getBounds().y + myRelativePhysicalShape.getBounds().height);
    }
    
    public void setPhysicalLeft(int x)
    {
        setX(x - myRelativePhysicalShape.getBounds().x);
    }
    public void setPhysicalRight(int x)
    {
        int relPhysRight = myRelativePhysicalShape.getBounds().x+myRelativePhysicalShape.getBounds().width;
        setX((x-getWidth()) + (getWidth()-relPhysRight));
    }
    public void setPhysicalTop(int y)
    {
        setY(y - myRelativePhysicalShape.getBounds().y);
    }
    public void setPhysicalBottom(int y)
    {
        int relPhysBottom = myRelativePhysicalShape.getBounds().y+myRelativePhysicalShape.getBounds().height;
        setY((y-getHeight()) + (getHeight()-relPhysBottom));
    }
    
    public void setImage(BufferedImage image)
    {
        myImage = image;
        if (myImage != null)
            mySize = new Dimension(myImage.getWidth(), myImage.getHeight());
    }
    public BufferedImage getImage()
    {
        return myImage;
    }

    public void setLocation(int x, int y)
    {
        myLocation.x = x;
        myLocation.y = y;
    }
    public void setLocation(Point location)
    {
        if (location == null)
            myLocation = null;
        else        
            setLocation(location.x, location.y); 
    }
    public Point getLocation()
    {
        return myLocation;
    }

    public void setSize(Dimension size)
    {
        mySize = size;
    }
    public Dimension getSize()
    {
        return mySize;
    }

    public void setX(int x)
    {
        myLocation.x = x;
    }
    public int getX()
    {
        return myLocation.x;
    }
    public void setY(int y)
    {
        myLocation.y = y;
    }
    public int getY()
    {
        return myLocation.y;
    }

    public int getWidth()
    {
        return mySize.width;
    }
    public int getHeight()
    {
        return mySize.height;
    }

    public int getLeft()
    {
        return getX();
    }
    public int getTop()
    {
        return getY();
    }
    public int getRight()
    {
        return getX()+getWidth();
    }
    public int getBottom()
    {
        return getY()+getHeight();
    }

    public boolean containsPoint(Point p)
    {
        return p.x >= myLocation.x && p.x <= myLocation.x + mySize.width && p.y >= myLocation.y && p.y <= myLocation.y + mySize.height;
    }
    
    public boolean intersectsWith(Thing otherThing)
    {
        return intersectsWith(otherThing.getBounds());
    }
    public boolean intersectsWith(Rectangle rect)
    {
        return getBounds().intersects(rect);
    }
    
    public boolean intersectsPhysicallyWith(Thing otherThing)
    {
        return intersectsPhysicallyWith(otherThing.getPhysicalShape());
    }
    public boolean intersectsPhysicallyWith(Shape otherPhysicalShape)
    {
        return Global.isIntersecting(getPhysicalShape(), otherPhysicalShape);
    }
    
    
    public Rectangle getBounds()
    {
        return new Rectangle(getX(), getY(), getWidth(), getHeight());
    }

    public Point getCenterPoint()
    {
        return new Point(getX()+(getWidth()/2), getY()+(getHeight()/2));
    }
    public void setCenterPoint(Point ctr)
    {
        setLocation(ctr.x - (getWidth()/2), ctr.y - (getHeight()/2));
    }
    public int getCtrX()
    {
        return getX()+(getWidth()/2);
    }
    public int getCtrY()
    {
        return getY()+(getHeight()/2);
    }
    public Point getBasePoint()
    {
        return new Point(getX()+(getWidth()/2), getBottom());
    }
    public void setBasePoint(Point p)
    {
        setLocation(p.x - (getWidth()/2), p.y-getHeight());
    }
    public Point getRelativeBasePoint()
    {
        return new Point((getWidth()/2), getHeight());
    }
    public void setBasePoint(int x, int y)
    {
        setBasePoint(new Point(x, y));
    }
    public void setBaseX(int x)
    {
        setX(x-(getWidth()/2));
    }
    public void setBaseY(int y)
    {
        setY(y-getHeight());
    }
    
    
    public Point getRandomPointWithinRange(int range)
    {
        Point p = new Point();
        Point bsPoint = getBasePoint();
        do
        {
            p.x = Global.getRandomInt(bsPoint.x-range, bsPoint.x+range);
            p.y = Global.getRandomInt(bsPoint.y-range, bsPoint.y+range);
        } while (getDistanceFromPoint(p) > range);
        return p;
    }
    
    public double getDistanceFromPoint(Point target)
    {
        return Global.getDistance(this.getBasePoint(), target);
    }
    
    protected void paint(Graphics2D g)
    {
        if (getImage() != null)
            g.drawImage(getImage(), getX(), getY(), null);
    }
    
    public void render(Graphics2D g)
    {
        paint(g);
        if (Global.TestMode)
        {
            physBoundsTest(g);
        }
    }
    
    private void physBoundsTest(Graphics2D g)
    {
        g.setColor(Color.PINK);
        Graphics g2 = g.create(getX(), getY(), getWidth()+1, getHeight()+1);
        Shape bounds = getPhysicalShape();
        if (bounds != null)
        {    
            if (myRelativePhysicalShape instanceof Rectangle)
            {
                Rectangle rectangle = (Rectangle)myRelativePhysicalShape;
                g2.drawRect(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
            }
            else if (myRelativePhysicalShape instanceof Polygon)
            {                   
                Polygon polygon = (Polygon)myRelativePhysicalShape;
                g2.drawPolygon(polygon);
            }
            else if (myRelativePhysicalShape instanceof Ellipse2D.Float)
            {                   
                Ellipse2D.Float ellipse = (Ellipse2D.Float)myRelativePhysicalShape;
                g2.drawOval((int)ellipse.x, (int)ellipse.y, (int)ellipse.width, (int)ellipse.height);
            }
        }
    }
}

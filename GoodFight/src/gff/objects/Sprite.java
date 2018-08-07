/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gff.objects;

import gff.objects.scenery.Scenery;
import gff.Global;
import gff.GoodFight;
import gff.GoodFight.Dir;
import gff.Scene;
import gff.spritecommands.SpriteCommand;
import gff.graphics.Animation;
import gff.graphics.SpriteLayer;
import gff.spritecommands.WalkCommand;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 *
 * @author Raymond Nagel
 */
public abstract class Sprite extends Thing
{
    private ArrayList<Animation>     myAnimations = new ArrayList<>();
    private Animation                myAnimation = null;
    private Dir                      myDirection = Dir.S;
    protected double                 mySpeedFactor = 100.0;
    protected double                 myRemainingHorz = 0;
    protected double                 myRemainingVert = 0;
    protected long                   myLastUpdate = System.nanoTime();
    protected boolean                myMoving = false;
    protected TalkBubble             myTalkBubble = null;
    protected EffectText             myEffectText = null;
    protected String                 myScriptLabel = null;
    protected SpriteCommand          myCommand = null;    

    public Sprite(BufferedImage image, int x, int y) {
        super(image, x, y);
    }
    
    public Sprite()
    {
        
    }
    
    public void setScriptLabel(String scriptLabel)
    {
        myScriptLabel = scriptLabel;
    }
    
    public String getScriptLabel()
    {
        return myScriptLabel;
    }    
    
    public boolean isMoving()
    {
        return myMoving;
    }
    
    public void go()
    {
        myMoving = true;
    }
    public void stop()
    {
        myMoving = false;
    }
    
    public void unpause()
    {
        myLastUpdate = System.nanoTime();
    }
    
    public double getSpeedFactor()
    {
        return mySpeedFactor;
    }
    
    public void setDirection(Dir dir)
    {
        myDirection = dir;
    }
    public Dir getDirection()
    {
        return myDirection;
    }
    public void faceDirection(Dir dir)
    {
        setDirection(dir);
        go();
        stop();
    }
    public void faceToward(Thing thing)
    {        
        int xDif = thing.getBasePoint().x - getBasePoint().x;
        int yDif = thing.getBasePoint().y - getBasePoint().y;
        if (Math.abs(xDif) >= Math.abs(yDif))
        {
            // The horz distance is greatest - face west or east:
            if (xDif < 0)
                faceDirection(Dir.W);
            else
                faceDirection(Dir.E);
        }
        else
        {
            // The vert distance is greatest - face north or south:
            if (yDif < 0)
                faceDirection(Dir.N);
            else
                faceDirection(Dir.S);
        }
    }
    
    public void addAnimation(Animation animation)
    {
        if (getAnimationByName(animation.getName()) == null)
        {
            myAnimations.add(animation);
        }
    }

    public void setAnimation(Animation animation)
    {
        myAnimation = animation;
    }
    public boolean setAnimation(String name)
    {
        myAnimation = getAnimationByName(name);
        return myAnimation != null;
    }

    public Animation getAnimationByName(String name)
    {
        for (int a = 0; a < myAnimations.size(); a++)
        {
            if (myAnimations.get(a).getName().equalsIgnoreCase(name))
            {
                return myAnimations.get(a);
            }
        }
        return null;
    }

    public Animation getCurrentAnimation()
    {
        return myAnimation;
    }

    public void update()
    {        
        updateSpeech();
        
        // Get time elapsed since my last update via this method.
        double elapsedTime = (double)(System.nanoTime()-myLastUpdate)/1000000000;
        Dir dir = getDirection();
        
        int dX = 0, dY = 0;
        if (myMoving)
        {
            if (dir == Dir.E || dir == Dir.NE || dir == Dir.SE)
                dX = 1;
            if (dir == Dir.W || dir == Dir.NW || dir == Dir.SW)
                dX = -1;
            if (dir == Dir.S || dir == Dir.SE || dir == Dir.SW)
                dY = 1;
            if (dir == Dir.N || dir == Dir.NE || dir == Dir.NW)
                dY = -1;
        }
        
        // Update location.
        double speed = Global.isDiagonal(dir) ? mySpeedFactor*.7 : mySpeedFactor;
        double xT = (speed*dX * elapsedTime) + myRemainingHorz;
        double yT = (speed*dY * elapsedTime) + myRemainingVert;

        int xMov = (int)xT;
        int yMov = (int)yT;
        myRemainingHorz = xT-xMov;
        myRemainingVert = yT-yMov;
        
        // Test for physical collisions that may prevent the move.
        Shape projXPhysBounds = Global.getProjectedShape(getPhysicalShape(), xMov, 0);
        Shape projYPhysBounds = Global.getProjectedShape(getPhysicalShape(), 0, yMov);  
        
        // Check for scenery collisions in the ACTION SpriteLayer:
        Scene currentScene = GoodFight.getCurrentScene();
        SpriteLayer spLayer = currentScene.getSpriteLayer(SpriteLayer.TYPE.ACTION);
        for (int sc = 0; sc < spLayer.getSceneryObjects().size(); sc++)
        {
            Scenery scenery = spLayer.getSceneryObjects().get(sc);
            boolean touching = false;
            if (scenery.intersectsPhysicallyWith(projXPhysBounds))
            {
                touching = true;
                if (!canIntrude(scenery))
                {                            
                    xMov = 0;
                }
            }
            if (scenery.intersectsPhysicallyWith(projYPhysBounds))
            {
                touching = true;
                if (!canIntrude(scenery))
                {                            
                    yMov = 0;
                }
            }
            if (touching)
            {
                touch(scenery);
            }
        }
        
        // Check for character collisions:
        for (int cs = 0; cs < GoodFight.getSprites().size(); cs++)
        {
            Sprite otherSprite = GoodFight.getSprites().get(cs);
            if (otherSprite != this)
            {
                boolean touching = false;
                if (otherSprite.intersectsPhysicallyWith(projXPhysBounds))
                {
                    touching = true;
                    if (!canIntrude(otherSprite))
                    {                        
                        xMov = 0;
                    }
                }
                if (otherSprite.intersectsPhysicallyWith(projYPhysBounds))
                {
                    touching = true;
                    if (!canIntrude(otherSprite))
                    {
                        yMov = 0;
                    }
                }
                if (touching)
                {
                    touch(otherSprite);
                }
            }
        }
        
        if (getPhysicalLeft()+xMov > GoodFight.TV_LEFT_BOUND && 
            getPhysicalRight()+xMov < GoodFight.TV_RIGHT_BOUND)
        {
            setLocation(getX()+xMov, getY()); 
        }
        if (getPhysicalTop()+yMov > GoodFight.TV_TOP_BOUND && 
            getPhysicalBottom()+yMov < GoodFight.TV_BOTTOM_BOUND)
        {
            setLocation(getX(), getY()+yMov); 
        }
               
        // Ensure that a commanded Sprite does not move beyond the commanded Point:
        if (myCommand != null && myCommand instanceof WalkCommand)       
        {
            ((WalkCommand)myCommand).preventGoingTooFar();
        }
        
        double traveledDistance = Math.sqrt((xT*xT)+(yT*yT));
        
//        if (traveledDistance > 100)
//            Global.log("");

        // Update animation.
        if (getCurrentAnimation() != null)
            getCurrentAnimation().advance();

        // Record the time that this update finished.
        myLastUpdate = System.nanoTime();
    }
    
    public void updateSpeech()
    {
        // Update the Sprite's TalkBubble, if one exists.
        if (myTalkBubble != null)
        {
            myTalkBubble.update();
        }
        // Update the Sprite's EffectText, if one exists.
        if (myEffectText != null)
        {
            myEffectText.update();
        }
    }

    public abstract boolean canIntrude(Thing thing);
    public abstract void touch(Thing thing);
    
    @Override
    public BufferedImage getImage()
    {
        if (myAnimation != null)
            return myAnimation.getImage();
        else
            return super.getImage();
    }

    public void destroy()
    {
        GoodFight.removeSprite(this);
    }

    
    public void setCommand(SpriteCommand command)
    {
        myCommand = command;
        if (myCommand != null)
        {
            myCommand.start();
        }
    }
    public SpriteCommand getCommand()
    {
        return myCommand;
    }
    public void clearCommand()
    {
        myCommand = null;
    }
    
    public String getNameTagText()
    {
        return null;
    }
    public Color getNameTagColor()
    {
        return null;
    }
    
    @Override
    public void render(Graphics2D g)
    {
        super.render(g); 
    }    
    
    public void renderNameTag(Graphics2D g)
    {    
        String nameTag = getNameTagText();
        Color tagColor = getNameTagColor();
        if (nameTag != null && tagColor != null)
        {
            g.setFont(Global.SimpleTinyFont);
            Rectangle2D rect = g.getFontMetrics().getStringBounds(nameTag, g);
            int h = g.getFontMetrics().getAscent()-1;
            int y = (getCtrY()-(int)(rect.getHeight()/2))+h;
            int x = getCtrX() - (int)(rect.getWidth()/2);
            
            // Backing:
            g.setColor(new Color(0, 0, 0, 82));            
            g.fillRect(x, y-h, (int)rect.getWidth(), (int)rect.getHeight());
            
            g.setColor(Color.BLACK);
            g.drawString(nameTag, x+1, y+1);
            g.setColor(tagColor);
            g.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON );
            g.drawString(nameTag, x, y);            
        }
    }
    
    public void renderSpeech(Graphics2D g)
    {
        if (isTalking())
        {
            myTalkBubble.paint(g);
        }
        if (myEffectText != null)
        {
            myEffectText.paint(g);
        }
    }
    
    public boolean isTalking()
    {
        return myTalkBubble != null;
    }
    public void say(String text)
    {
        myTalkBubble = new TalkBubble(this, text);
    }
    public void shutUp()
    {
        myTalkBubble = null;
    }
    public void think(String text)
    {
        myTalkBubble = new ThinkBubble(this, text);
    }
    
    public void showEffectText(String text)
    {
        myEffectText = new EffectText(this, text);
    }
    public void cancelEffectText()
    {
        myEffectText = null;
    }

}
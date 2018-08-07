/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gff.objects;

import gff.Automaton;
import gff.Church;
import gff.Conversation;
import gff.Global;
import gff.GoodFight;
import gff.GoodFight.Dir;
import gff.GoodFight.Sex;
import gff.graphics.Animation;
import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.Queue;

/**
 *
 * @author Ray
 */
public class Person extends Sprite implements Automaton{

    public static enum PersonMode {STILL, WANDER, FOLLOW, MOSEY};
           
    protected static final float MAX_KNOWLEDGE = 100f;
    protected static final int TARGET_RANGE = 100;
    protected static final long TIMEOUT = 100;
    protected static final int CLOSE_ENOUGH = 10;
    protected Point myLastLocation = null;
    protected Point myGoal = null;
    protected long myTimer = 0;
    protected long myRestTimer = 0;
    protected Queue<Conversation> myConversationQueue = new LinkedList<>();
    protected Sex mySex = Sex.RANDOM;
    protected String myFirstName = null;
    protected String myLastName = null;    
    protected boolean mySalvation = false;
    protected boolean myAlreadyTalked = false;
    protected boolean myConversing = false; // Conversing is whether the person is in the middle of a Conversation,
                                            // regardless of whether they are Talking (have a talk bubble).
    protected Church myChurch = null;
    
    protected int myVoiceSet = Global.getRandomInt(1, 3);
    protected int myFamiliarity = 0; // The more times you talk to the person, the more familiar they become.
    protected float myKnowledge = 0f; // Knowledge of salvation/Christ. When this exceeds 100, they get saved.
    protected float myWillingness = 0f; // Determines whether preaching of the gospel will draw them closer to Christ or harden their heart.
    protected PersonMode myMode = PersonMode.STILL;
    
    public Person(Sex sex, boolean saved, String firstName, String lastName, String scriptLabel, Church church, float responsiveness, float receptiveness)
    {
        mySex = sex;
        mySalvation = saved;
        myFirstName = firstName;
        myLastName = lastName;
        myScriptLabel = scriptLabel;
        myChurch = church;
        myKnowledge = responsiveness;
        myWillingness = receptiveness;
        
        Global.log("Person " + getFullName() + " was born:");
        Global.log("Sex: " + getSex());
        Global.log("Knowledge: " + myKnowledge);
        Global.log("Willingness: " + myWillingness);
        
        String sexType = mySex==Sex.MALE ? "boy" : "girl";
        Animation anim = new Animation(sexType + "_d", true, 21, 1, 13);
        this.addAnimation(anim);
        anim = new Animation(sexType + "_l", true, 21, 1, 13);
        this.addAnimation(anim);
        anim = new Animation(sexType + "_r", true, 21, 1, 13);
        this.addAnimation(anim);
        anim = new Animation(sexType + "_u", true, 21, 1, 13);
        this.addAnimation(anim);
        
        this.setImage(GoodFight.getLoadedImage("sprites/" + sexType + "_default.png"));
        
        Point basePt = getRelativeBasePoint();
        setRelativePhysicalShape(new Rectangle(basePt.x-10, basePt.y-10, 20, 10));
        this.setLocation(400, 200);
        this.setDirection(Dir.S);
    }
    
    public Person(Sex sex, boolean saved, Church church) {
        initPerson(sex);
        this.mySalvation = saved;
        this.myChurch = church;
        
        String sexType = mySex==Sex.MALE ? "boy" : "girl";
        Animation anim = new Animation(sexType + "_d", true, 21, 1, 13);
        this.addAnimation(anim);
        anim = new Animation(sexType + "_l", true, 21, 1, 13);
        this.addAnimation(anim);
        anim = new Animation(sexType + "_r", true, 21, 1, 13);
        this.addAnimation(anim);
        anim = new Animation(sexType + "_u", true, 21, 1, 13);
        this.addAnimation(anim);
        
        this.setImage(GoodFight.getLoadedImage("sprites/" + sexType + "_default.png"));
        
        Point basePt = getRelativeBasePoint();
        setRelativePhysicalShape(new Rectangle(basePt.x-10, basePt.y-10, 20, 10));
        this.setLocation(400, 200);
        this.setDirection(Dir.S);
    }
    
    protected void initPerson(Sex sex)
    {
        // Determine gender of the person:
        if (sex == Sex.RANDOM)
        {
            if (Global.getRandomInt(1, 2) == 1)
                mySex = Sex.MALE;
            else
                mySex = Sex.FEMALE;
        }
        else
        {
            mySex = sex;
        }
        
        // Based on the person's gender, get a random name & outfit:
        switch (mySex)
        {
            case MALE:
                myFirstName = (String)Global.getRandomFromList(Global.MaleNames);
                break;
            case FEMALE:
                myFirstName = (String)Global.getRandomFromList(Global.FemaleNames);
                break;
        }
        myLastName = (String)Global.getRandomFromList(Global.LastNames);        
        
        // Determine personality randomly:
        myKnowledge = Global.getRandomInt(0, (int)MAX_KNOWLEDGE-1);
        myWillingness = (float)Global.getRandomInt(-100, 100)/100f;
        
        Global.log("Person " + getFullName() + " was born:");
        Global.log("Sex: " + getSex());
        Global.log("Knowledge: " + myKnowledge);
        Global.log("Willingness: " + myWillingness);
                
    }

    public int getFamiliarity()
    {
        return myFamiliarity;
    }
    
    public float getKnowledge()
    {
        return myKnowledge;
    }
    public float getWillingness()
    {
        return myWillingness;
    }
    
    public Sex getSex()
    {
        return mySex;
    }
    
    public String getFirstName()
    {
        return myFirstName;
    }
    
    public String getLastName()
    {
        return myLastName;
    }
    
    public String getFullName()
    {
        return (myFirstName + " " + myLastName).trim();
    }
    
    public String getBrotherlyName()
    {
        return (mySex == Sex.FEMALE ? "Sister " : "Brother ") + myFirstName;
    }
    
    public String getFormalName()
    {
        return (mySex == Sex.FEMALE ? "Ms. " : "Mr. ") + myLastName;
    }

    public String getAppropriateNameToCall()
    {
        if (isSaved())
        {
            // SAVED:
            if (myFamiliarity >= 2)
                return getBrotherlyName();
            else
                return getFirstName();
        }
        else
        {
            // LOST:
            if (myFamiliarity >= 4)            
                return getFirstName();
            else
                return getFormalName();
        }
    }
    
    @Override
    public String getNameTagText() {
        return getFullName();
    }     
    
    @Override
    public Color getNameTagColor()
    {
        return Color.WHITE;
    }
    
    public int getVoiceSet()
    {
        return myVoiceSet;
    }
    
    public PersonMode getMode()
    {
        return myMode;
    }
    public void setMode(PersonMode mode)
    {
        myMode = mode;
        if (myMode == PersonMode.STILL)
            stop();
    }
    
    public void convert()
    {
        this.mySalvation = true;
        this.myChurch.becomeMember(this);        
    }
    
    public boolean isSaved()
    {
        return mySalvation;
    }
    
    @Override
    public void go() {
        super.go();
        Dir dir = getDirection();
        String sexType = mySex==Sex.MALE ? "boy" : "girl";
        if (dir == Dir.S || dir == Dir.SW || dir == Dir.SE)
            this.setAnimation(getAnimationByName(sexType+"_d"));
        else if (dir == Dir.W)
            this.setAnimation(getAnimationByName(sexType+"_l"));
        else if (dir == Dir.E)
            this.setAnimation(getAnimationByName(sexType+"_r"));
        else if (dir == Dir.N || dir == Dir.NW || dir == Dir.NE)
            this.setAnimation(getAnimationByName(sexType+"_u"));
    }

    @Override
    public void stop() {
        BufferedImage still = getImage();
        if (getCurrentAnimation() != null)
        {
            still = getCurrentAnimation().getImage();
        }
        super.stop();        
        setAnimation((Animation)null);
        setImage(still);
    }

    @Override
    public void touch(Thing thing) {
    }

    @Override
    public boolean canIntrude(Thing thing) {
        return false;
    }

    @Override
    public void automate()
    {      
        if (myConversing)
            return;
        
        switch(myMode)
        {
            case STILL:
                break;
            case WANDER:
                automateWander();
                break;
            case MOSEY:
                automateMosey();
                break;
            case FOLLOW:
                automateFollow();
                break;
        }
    }
    
    public void automateFollow() {
        
        if (!isMoving())
        {
            // Turn toward Adam:
            int xDif = GoodFight.getSubject().getX() - getX();
            int yDif = GoodFight.getSubject().getY() - getY();
            go();            
            if (xDif < 0 && Math.abs(xDif) > Math.abs(yDif))
            {
                setDirection(Dir.W);
            }
            else if (xDif > 0 && Math.abs(xDif) > Math.abs(yDif))
            {
                setDirection(Dir.E);
            }
            else if (yDif < 0 && Math.abs(xDif) < Math.abs(yDif))
            {
                setDirection(Dir.N);
            }
            else if (yDif > 0 && Math.abs(xDif) < Math.abs(yDif))
            {
                setDirection(Dir.S);
            }
            stop();
        }
        
        if ((myGoal != null && getDistanceFromPoint(myGoal) <= CLOSE_ENOUGH))
        {
            // I'm close enough to my current goal; I don't have to follow it anymore.
            myGoal = null;
            stop();
        }
        
        if (myGoal != null && getLocation().equals(myLastLocation) && myTimer > TIMEOUT)
        {
            // I've been pursuing this goal for too long; time for a new one:
            myGoal = null;
            stop();
        }
        
        if (myGoal == null && getDistanceFromPoint(GoodFight.getSubject().getBasePoint()) > TARGET_RANGE)
        {
            // I don't have a goal, and I'm too far away from Adam.
            // Pick a new goal:
            do
            {
                myGoal = GoodFight.getSubject().getRandomPointWithinRange(TARGET_RANGE);
            } while (myGoal.x >= GoodFight.TV_RIGHT_BOUND || myGoal.x <= GoodFight.TV_LEFT_BOUND
                    || myGoal.y >= GoodFight.TV_BOTTOM_BOUND || myGoal.y <= GoodFight.TV_TOP_BOUND);
            myTimer = 0;
        }
       
        if (myGoal != null)
        {
            Point bsPoint = getBasePoint();
            // Follow the existing goal:
            if (myGoal.x < bsPoint.x && myGoal.y < bsPoint.y)
            {
                setDirection(GoodFight.Dir.NW);            
                go();
            }
            else if (myGoal.x < bsPoint.x && myGoal.y > bsPoint.y)
            {
                setDirection(GoodFight.Dir.SW);
                go();
            }        
            else if (myGoal.x > bsPoint.x && myGoal.y < bsPoint.y)
            {
                setDirection(GoodFight.Dir.NE);
                go();
            }
            else if (myGoal.x > bsPoint.x && myGoal.y > bsPoint.y)
            {
                setDirection(GoodFight.Dir.SE);
                go();
            }
            else if (myGoal.y < bsPoint.y)
            {
                setDirection(GoodFight.Dir.N);            
                go();
            }
            else if (myGoal.y > bsPoint.y)
            {
                setDirection(GoodFight.Dir.S);
                go();
            }        
            else if (myGoal.x < bsPoint.x)
            {
                setDirection(GoodFight.Dir.W);
                go();
            }
            else if (myGoal.x > bsPoint.x)
            {
                setDirection(GoodFight.Dir.E);
                go();
            }
            myTimer += 1;
        }
        myLastLocation = getLocation();
    }
    
    public void automateWander()
    {
        if (myGoal == null || myTimer >= TIMEOUT || getDistanceFromPoint(myGoal) <= CLOSE_ENOUGH)
        {
            // Pick a new goal:
            do
            {
                myGoal = getRandomPointWithinRange(TARGET_RANGE);
            } while (myGoal.x >= GoodFight.TV_RIGHT_BOUND || myGoal.x <= GoodFight.TV_LEFT_BOUND
                    || myGoal.y >= GoodFight.TV_BOTTOM_BOUND || myGoal.y <= GoodFight.TV_TOP_BOUND);
            myTimer = 0;
        }
        else
        {
            Point bsPoint = getBasePoint();
            // Follow the existing goal:
            if (myGoal.x < bsPoint.x && myGoal.y < bsPoint.y)
            {
                setDirection(GoodFight.Dir.NW);            
                go();
            }
            else if (myGoal.x < bsPoint.x && myGoal.y > bsPoint.y)
            {
                setDirection(GoodFight.Dir.SW);
                go();
            }        
            else if (myGoal.x > bsPoint.x && myGoal.y < bsPoint.y)
            {
                setDirection(GoodFight.Dir.NE);
                go();
            }
            else if (myGoal.x > bsPoint.x && myGoal.y > bsPoint.y)
            {
                setDirection(GoodFight.Dir.SE);
                go();
            }
            else if (myGoal.y < bsPoint.y)
            {
                setDirection(GoodFight.Dir.N);            
                go();
            }
            else if (myGoal.y > bsPoint.y)
            {
                setDirection(GoodFight.Dir.S);
                go();
            }        
            else if (myGoal.x < bsPoint.x)
            {
                setDirection(GoodFight.Dir.W);
                go();
            }
            else if (myGoal.x > bsPoint.x)
            {
                setDirection(GoodFight.Dir.E);
                go();
            }
            myTimer += 1;
        }
    }
    
    public void automateMosey()
    {
        if (myRestTimer > 0)
        {
            myRestTimer--;
        }
        else if (myGoal == null || myTimer >= TIMEOUT || getDistanceFromPoint(myGoal) <= CLOSE_ENOUGH)
        {
            myRestTimer = 0;
            boolean pause = Global.oddsCheck(1, 2);
            if (pause)
            {
                // Pause the person for a random period between 10-100 iterations:
                stop();
                myRestTimer = Global.getRandomInt(10, 100);                
            }
            else
            {
                // Pick a new goal:
                do
                {
                    myGoal = getRandomPointWithinRange(TARGET_RANGE);
                } while (myGoal.x >= GoodFight.TV_RIGHT_BOUND || myGoal.x <= GoodFight.TV_LEFT_BOUND
                        || myGoal.y >= GoodFight.TV_BOTTOM_BOUND || myGoal.y <= GoodFight.TV_TOP_BOUND);
                myTimer = 0;
            }
        }
        else
        {
            Point bsPoint = getBasePoint();
            // Follow the existing goal:
            if (myGoal.x < bsPoint.x && myGoal.y < bsPoint.y)
            {
                setDirection(GoodFight.Dir.NW);            
                go();
            }
            else if (myGoal.x < bsPoint.x && myGoal.y > bsPoint.y)
            {
                setDirection(GoodFight.Dir.SW);
                go();
            }        
            else if (myGoal.x > bsPoint.x && myGoal.y < bsPoint.y)
            {
                setDirection(GoodFight.Dir.NE);
                go();
            }
            else if (myGoal.x > bsPoint.x && myGoal.y > bsPoint.y)
            {
                setDirection(GoodFight.Dir.SE);
                go();
            }
            else if (myGoal.y < bsPoint.y)
            {
                setDirection(GoodFight.Dir.N);            
                go();
            }
            else if (myGoal.y > bsPoint.y)
            {
                setDirection(GoodFight.Dir.S);
                go();
            }        
            else if (myGoal.x < bsPoint.x)
            {
                setDirection(GoodFight.Dir.W);
                go();
            }
            else if (myGoal.x > bsPoint.x)
            {
                setDirection(GoodFight.Dir.E);
                go();
            }
            myTimer += 1;
        }
    }
    
    public void queueUpConversation(Conversation c)
    {
        myConversationQueue.add(c);
    }
    
    public Conversation getNextConversation()
    {
        if (myAlreadyTalked)
            return null;

        Conversation c = myConversationQueue.peek();
        if (c != null)
            myConversationQueue.remove();
        else
        {
            c = Conversation.createRandomConversation(this, null);
        }
        myAlreadyTalked = true;
        myFamiliarity++;
        return c;
    }
    
    
    public void resetAlreadyTalked()
    {
        myAlreadyTalked = false;
    }
    
    
    public void setConversing(boolean conversing)
    {
        myConversing = conversing;
        if (!myConversing)
        {
            unpause();
        }
    }
    public boolean isConversing()
    {
        return myConversing;
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gff.objects;

import gff.Controllable;
import gff.Global;
import gff.GoodFight;
import gff.GoodFight.Dir;
import gff.attacks.EnemyAttack;
import gff.graphics.Animation;
import gff.items.Armour;
import gff.items.Book;
import gff.items.Commandment;
import gff.items.Fruit;
import gff.objects.scenery.Portal;
import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 *
 * @author Ray
 */
public class Hero extends Person implements Controllable{
    private boolean myControlled = false;
    private int myLevel = 0; // Player begins at level 0;
    private int myMaxFaith = 50;  // 50 is the starting faith for each game.
    private int myCurrentFaith = myMaxFaith; // Player begins with full faith.
    private int myCurrentExp = 0; // Player begins with no experience.
    private ArrayList<String> myAcquiredBooks = new ArrayList<>();
    private ArrayList<Commandment> myAcquiredCommandments = new ArrayList<>();
    private ArrayList<Armour> myAcquiredArmour = new ArrayList<>();

    private HashMap<String, Integer> myFruits = new HashMap<>();
    
    
    
    public Hero() {
        super(GoodFight.Sex.MALE, true, "Adam", "Cesar", "Adam", Global.ChurchList.get(9), 100f, 1f);
        Animation anim = new Animation("amc_d", true, 21, 1, 13);
        this.addAnimation(anim);
        anim = new Animation("amc_l", true, 21, 1, 13);
        this.addAnimation(anim);
        anim = new Animation("amc_r", true, 21, 1, 13);
        this.addAnimation(anim);
        anim = new Animation("amc_u", true, 21, 1, 13);
        this.addAnimation(anim);
        
        myVoiceSet = 2;
        this.setImage(GoodFight.getLoadedImage("sprites/amc_default.png"));
        
        Point basePt = getRelativeBasePoint();
        setRelativePhysicalShape(new Rectangle(basePt.x-10, basePt.y-10, 20, 10));
        this.setDirection(Dir.S);
    }

    @Override
    public void keysAreDown(boolean[] keyMap) {
        boolean move = false;
                
        if (keyMap[KeyEvent.VK_HOME] || (keyMap[KeyEvent.VK_UP] && keyMap[KeyEvent.VK_LEFT]))
        {
            setDirection(GoodFight.Dir.NW);            
            go();
            move = true;
        }
        else if (keyMap[KeyEvent.VK_END] || (keyMap[KeyEvent.VK_DOWN] && keyMap[KeyEvent.VK_LEFT]))
        {
            setDirection(GoodFight.Dir.SW);
            go();
            move = true;
        }        
        else if (keyMap[KeyEvent.VK_PAGE_UP] || (keyMap[KeyEvent.VK_UP] && keyMap[KeyEvent.VK_RIGHT]))
        {
            setDirection(GoodFight.Dir.NE);
            go();
            move = true;
        }
        else if (keyMap[KeyEvent.VK_PAGE_DOWN] || (keyMap[KeyEvent.VK_DOWN] && keyMap[KeyEvent.VK_RIGHT]))
        {
            setDirection(GoodFight.Dir.SE);
            go();
            move = true;
        }
        else if (keyMap[KeyEvent.VK_UP])
        {
            setDirection(GoodFight.Dir.N);            
            go();
            move = true;
        }
        else if (keyMap[KeyEvent.VK_DOWN])
        {
            setDirection(GoodFight.Dir.S);
            go();
            move = true;
        }        
        else if (keyMap[KeyEvent.VK_LEFT])
        {
            setDirection(GoodFight.Dir.W);
            go();
            move = true;
        }
        else if (keyMap[KeyEvent.VK_RIGHT])
        {
            setDirection(GoodFight.Dir.E);
            go();
            move = true;
        }
        
        if (!move)
            stop();
    }

    @Override
    public void keyPressed(int keyCode) {
        
    }

    @Override
    public void keyReleased(int keyCode) {        
    }

    @Override
    public boolean isControlled() {
        return myControlled && !myConversing;
    }

    @Override
    public void setControlled(boolean controlled) {
        myControlled = controlled;
    }
    
    @Override
    public void go() {
        super.go();
        Dir dir = getDirection();
        if (dir == Dir.S || dir == Dir.SW || dir == Dir.SE)
            this.setAnimation(getAnimationByName("amc_d"));
        else if (dir == Dir.W)
            this.setAnimation(getAnimationByName("amc_l"));
        else if (dir == Dir.E)
            this.setAnimation(getAnimationByName("amc_r"));
        else if (dir == Dir.N || dir == Dir.NW || dir == Dir.NE)
            this.setAnimation(getAnimationByName("amc_u"));
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
        if (thing instanceof TreasureChest && myCurrentFaith > 0)
        {
            TreasureChest chest = (TreasureChest)thing;
            chest.open();
        }     
        if (thing instanceof Portal)
        {
            Portal portal = (Portal)thing;
            if (this.getDirection() == portal.getEnterDirection())
            {
                GoodFight.doEnterPortal(portal);
            }
        }
    }

    @Override
    public boolean canIntrude(Thing thing) {
        if (thing instanceof TreasureChest && myCurrentFaith > 0)
        {
            return true;
        }
        return false;
    }

    @Override
    public void setConversing(boolean conversing) {
        if (conversing) stop();
        super.setConversing(conversing);        
    }

    
    
    @Override
    public Color getNameTagColor() {
        return Color.CYAN.brighter();
    }        
    
    public int getExpForLevelUp()
    {
        if (myLevel >= 100)
            return Integer.MAX_VALUE;
        else
            return (myLevel+1)*10;
    }
    
    public int getCurrentExp()
    {
        return myCurrentExp;
    }
    
    public void addExp(int expAmount)
    {
        // Add the amount to our current Exp:
        myCurrentExp += (int)(((float)expAmount)*GoodFight.getDifficulty().getExpMultiplier());            
    }
    public boolean canLevel()
    {        
        return myLevel < 100 && myCurrentExp >= getExpForLevelUp();        
    }
    public void increaseLevel()
    {                
        myCurrentExp -= getExpForLevelUp();
        myLevel++;
        increaseMaxFaith(5);
    }
    
    public int getLevel()
    {
        return myLevel;
    }
    
    public String[] getFruitsForLevelUp()
    {
        ArrayList<String> fruitList = new ArrayList<>();
        Iterator<String> iter = myFruits.keySet().iterator();
        while (iter.hasNext())
        {
            String fruitName = iter.next();
            if (myFruits.get(fruitName) > 0)
            {
                fruitList.add(fruitName);
            }
        }
        String[] fruits = new String[0];
        return fruitList.toArray(fruits);
    }
    
    public int getCurrentFaith()
    {
        return myCurrentFaith;
    }
    public void setCurrentFaith(int faith)
    {
        myCurrentFaith = faith;
        if (myCurrentFaith > myMaxFaith)
            myCurrentFaith = myMaxFaith;
        else if (myCurrentFaith < 0)
            myCurrentFaith = 0;
    }
    public void restoreFaith(int faithAmount)
    {
        setCurrentFaith(myCurrentFaith+faithAmount);
    }
    public void doubtFaith(int doubtAmount)
    {
        setCurrentFaith(myCurrentFaith-doubtAmount);
    }
    public int getMaxFaith()
    {
        return myMaxFaith;
    }
    public void increaseMaxFaith(int faithAmount)
    {
        float pctFaith = (float)myCurrentFaith / (float)myMaxFaith;
        myMaxFaith += faithAmount;
        setCurrentFaith((int)((float)myMaxFaith * pctFaith));
    }
    
    public boolean isDefeated()
    {
        return myCurrentFaith <= 0;
    }
    
    public void setFruitLevel(String fruitName, int level)
    {
        myFruits.put(fruitName, level);
    }
    public void incrementFruitLevel(String fruitName)
    {
        myFruits.put(fruitName, myFruits.get(fruitName)+1);
    }
    public int getFruitLevel(String fruitName)
    {
        return myFruits.get(fruitName);
    }
    
    public Armour getBlockingArmour(EnemyAttack atk, Enemy enemy)
    {
        int chance = Global.getRandomInt(1, 100);
        int total = 0;
        for (int a = 0; a < myAcquiredArmour.size(); a++)
        {
            Armour piece = myAcquiredArmour.get(a);
            total += piece.getBlockPct();
            if (total >= chance)
            {
                return piece;
            }
        }
        return null;
    }
    
    public void acquireCommandment(Commandment cmd)
    {        
        myAcquiredCommandments.add(cmd);
        increaseMaxFaith(5);
        Global.setNumCommandments(myAcquiredCommandments.size());
    }
    public void acquireFruit(Fruit fruit)
    {        
        setFruitLevel(fruit.getName(), 1);
        increaseMaxFaith(5);
    }
    public void acquireArmour(Armour armour)
    {
        myAcquiredArmour.add(armour);
        increaseMaxFaith(5);
    }
    public void acquireBook(Book book)
    {
        myAcquiredBooks.add(book.getName());
        increaseMaxFaith(5);
        GoodFight.enableBookCheckbox(book.getName());
    }
    public boolean hasBook(String bookName)
    {
        return myAcquiredBooks.contains(bookName);            
    }
}

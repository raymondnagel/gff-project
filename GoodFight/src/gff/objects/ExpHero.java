/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gff.objects;

import gff.Controllable;
import gff.GoodFight;
import gff.GoodFight.Dir;
import gff.graphics.Animation;
import gff.items.Armour;
import gff.items.Commandment;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Ray
 */
public class ExpHero extends Hero implements Controllable{
    private boolean myControlled = false;
    private int myLevel = 0; // Player begins at level 0;
    private int myMaxFaith = 50;  // 50 is the starting faith for each game.
    private int myCurrentFaith = myMaxFaith; // Player begins with full faith.
    private int myCurrentExp = 0; // Player begins with no experience.
    private ArrayList<String> myAcquiredBooks = new ArrayList<>();
    private ArrayList<Commandment> myAcquiredCommandments = new ArrayList<>();
    private ArrayList<Armour> myAcquiredArmour = new ArrayList<>();

    private HashMap<String, Integer> myFruits = new HashMap<>();
    
    
    
    public ExpHero() {
        super();
        Animation anim = new Animation("amc_n", true, 42, 0, 3);
        this.addAnimation(anim);
        anim = new Animation("amc_ne", true, 42, 0, 3);
        this.addAnimation(anim);
        anim = new Animation("amc_nw", true, 42, 0, 3);
        this.addAnimation(anim);
        anim = new Animation("amc_e", true, 42, 0, 3);
        this.addAnimation(anim);
        anim = new Animation("amc_w", true, 42, 0, 3);
        this.addAnimation(anim);
        anim = new Animation("amc_s", true, 42, 0, 3);
        this.addAnimation(anim);
        anim = new Animation("amc_se", true, 42, 0, 3);
        this.addAnimation(anim);
        anim = new Animation("amc_sw", true, 42, 0, 3);
        this.addAnimation(anim);
        mySpeedFactor = 140;
        myVoiceSet = 2;
        this.setImage(GoodFight.getLoadedImage("sprites/exp_default.png"));
        
        Point basePt = getRelativeBasePoint();
        setRelativePhysicalShape(new Rectangle(basePt.x-9, basePt.y-10, 18, 10));
        this.setDirection(Dir.S);
    }

    @Override
    public void go() {
        super.go();
        Dir dir = getDirection();
        
        switch (dir)
        {
            case S:
                this.setAnimation(getAnimationByName("amc_s"));
                break;
            case SE:
                this.setAnimation(getAnimationByName("amc_se"));
                break;
            case SW:
                this.setAnimation(getAnimationByName("amc_sw"));
                break;
            case E:
                this.setAnimation(getAnimationByName("amc_e"));
                break;
            case W:
                this.setAnimation(getAnimationByName("amc_w"));
                break;
            case N:
                this.setAnimation(getAnimationByName("amc_n"));
                break;
            case NE:
                this.setAnimation(getAnimationByName("amc_ne"));
                break;
            case NW:
                this.setAnimation(getAnimationByName("amc_nw"));
                break;
        }
    }

}

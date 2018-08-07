/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gff.objects;

import gff.Global;
import gff.attacks.EnemyAttack;
import gff.GoodFight;
import gff.attacks.RegularAttack;
import java.awt.Color;
import java.util.ArrayList;

/**
 *
 * @author Ray
 */
public abstract class Enemy extends Wanderer{

    public static final int DETECT_DISTANCE = 200;
    
    private int myLevel = 0;
    private String myName = null;
    private int myMaxResistance = 0;
    private int myBaseAttack = 0;
    private int myResistance = 0;
    private boolean myTracking = false;
    protected ArrayList<EnemyAttack> mySpecialAttacks = new ArrayList<>();
    protected RegularAttack myRegularAttack = new RegularAttack();

    public Enemy() {
        mySpeedFactor = GoodFight.getDifficulty().getEnemySpeed();
    }
    
    public EnemyAttack getRandomAttack()
    {
        int aType = Global.getRandomInt(1, 2);
        if (aType == 2 && mySpecialAttacks.size() > 0)
        {
            EnemyAttack attack = (EnemyAttack)Global.getRandomFromList(mySpecialAttacks);
            if (attack.isReady())
                return attack;
        }
        return myRegularAttack;        
    }
    public void decrementAllAttackCooldowns()
    {
        for (int a = 0; a < mySpecialAttacks.size(); a++)
        {
            mySpecialAttacks.get(a).decrementCooldown();
        }
    }
   
    public String getName()
    {
        return myName;
    }
    protected void setName(String name)
    {
        myName = name;
        myScriptLabel = name;
    }

    @Override
    public String getNameTagText() {
        return getName();
    }        

    @Override
    public Color getNameTagColor() {
        return Color.RED;
    }
    
    
    
    public int getLevel()
    {
        return myLevel;
    }
    public void setLevel(int level)
    {
        myLevel = level;
        myMaxResistance = getResistancePerLevel() * myLevel;
        myBaseAttack = getBaseAttackPerLevel() * myLevel;
    }
    public void levelUp()
    {
        setLevel(myLevel+1);
    }
    
    protected abstract int getResistancePerLevel();
    
    protected abstract int getBaseAttackPerLevel();
    
    protected void setResistance(int resistance)
    {
        myResistance = resistance;
    }
    
    public void restoreResistance()
    {
        myResistance = myMaxResistance;
    }
    
    public int getMaxResistance()
    {
        return myMaxResistance;
    }
    
    public int getResistance()
    {
        return myResistance;
    }
    
    public int getBaseAttack()
    {
        return myBaseAttack;
    }
    
    public void hurt(int loss)
    {
        myResistance -= loss;
        if (myResistance <= 0)
        {
            myResistance = 0;
        }
    }
    
    public boolean isDefeated()
    {
        return myResistance <= 0;
    }
    
    @Override
    public void automate() {
        // If the Hero is within detection range, is not in a conversation, 
        // and he has some faith to be destroyed... muahahaha...
        if (this.getDistanceFromPoint(GoodFight.getSubject().getBasePoint()) <= DETECT_DISTANCE &&
            GoodFight.getSubject().getCurrentFaith() > 0 && !GoodFight.getSubject().isConversing())
        {
            // Show an exclamation indicator when the Hero is first detected:
            if (!myTracking)
            {                
                this.showEffectText("Grrr!");
                GoodFight.playSoundEffect("demon_growl");
            }

            myGoal = GoodFight.getSubject().getBasePoint();
            myTracking = true;
        }
        else
        {
            myTracking = false;
        }
        super.automate();        
    }

    @Override
    public void touch(Thing thing) {
        if (thing == GoodFight.getSubject() && GoodFight.getSubject().getCurrentFaith() > 0 && !GoodFight.getSubject().isConversing())
        {
            GoodFight.doEncounterEnemy(this);
        }
    }
    
    
}

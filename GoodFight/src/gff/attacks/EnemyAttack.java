/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gff.attacks;

import gff.Global;
import gff.objects.Enemy;
import gff.objects.Hero;
import java.awt.Graphics2D;

/**
 *
 * @author Raymond
 */
public abstract class EnemyAttack {
    private String name = null;
    private String description = null;
    private int maxCooldown = 0;
    private int cooldown = 0;
    private boolean inUse = false;

    public EnemyAttack(String name, String description, int cooldownTurns) {
        this.name = name;
        this.description = description;
        this.maxCooldown = cooldownTurns;        
    }
    
    public String getName()
    {
        return this.name;
    }
    public String getDescription()
    {
        return this.description;
    }
    
    public abstract int getAttackDamage(Enemy user);
    
    public void decrementCooldown()
    {
        cooldown--;
        if (cooldown < 0)
            cooldown = 0;
    }
    
    public void useAttack()
    {
        cooldown = maxCooldown;
        inUse = true;
        onStart();
    }
    
    public abstract void onStart();
    public void onFinish()
    {
        cooldown = maxCooldown;
        inUse = false;
    }
    
    public boolean isInUse()
    {
        return inUse;
    }
    public boolean isReady()
    {
        return cooldown == 0;
    }
    
    public abstract boolean isFinished();
        
    public void doAttackIteration(Enemy user, Hero victim)
    {

    }
    
    public abstract void drawAttack(Graphics2D g);
}

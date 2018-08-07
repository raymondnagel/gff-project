/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gff.attacks;

import gff.Global;
import gff.GoodFight;
import gff.TimingRegister;
import gff.objects.Enemy;
import gff.objects.Hero;
import java.awt.Graphics2D;

/**
 *
 * @author Raymond
 */
public class RegularAttack extends EnemyAttack{    
    private double inc = .6;
    private double time = 0.0;
    private boolean done = false;
    
    public RegularAttack() {
        super("Attack", "_ attacks!", 0);
    }
    
    @Override
    public void onStart() {
        GoodFight.playSoundEffect("kapow");
        done = false;
        inc = .6;
        time = 0.0;
    }
    
    @Override
    public void onFinish() { 
        super.onFinish();
        TimingRegister.remove("attack");
    }
    
    @Override
    public boolean isFinished() {
        return done;
    }

    @Override
    public void doAttackIteration(Enemy user, Hero victim) {        
        time = TimingRegister.update("attack", time, inc);
        if (time > 200)
        {
            done = true;
        }
        super.doAttackIteration(user, victim);
    }
    
    @Override
    public void drawAttack(Graphics2D g) {
        int x = 120;   
        int y = 284;
        
        int f = ((int)(time/25))+1;
        if (f > 8) f = 8;
        
        //g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, tsp));
        g.drawImage(GoodFight.getLoadedImage("sprites/effect_sprites/slash-" + f + ".png"), x, y, null);
        //g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
    }

    @Override
    public int getAttackDamage(Enemy user) {
        int base = user.getBaseAttack();
        int var = (int)(base * .25);
        return Global.getRandomInt(base-var, base+var);
    }

    
    
}

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
public class FieryDartAttack extends EnemyAttack{    
    private double flare = 0.0;
    private double flareInc = 0.1;
    private double inc = .45;
    private double wobble = 0.0;
    private double wobbleInc = .005;
    private double turn = 0.0;
    private double turnInc = .04;
    private double distance = 0.0;
    private boolean done = false;
    
    public FieryDartAttack() {
        super("Fiery Dart", "_ hurls a Fiery Dart!", 3);
    }
    
    @Override
    public void onStart() {
        GoodFight.playSoundEffect("fiery_dart");
        done = false;
        inc = .45;
        wobble = 0.0;
        wobbleInc = .2;
        turn = 0.0;
        turnInc = .008;
        distance = 0.0;
    }
    
    @Override
    public void onFinish() { 
        super.onFinish();
        TimingRegister.remove("attack");
        TimingRegister.remove("turn");
        TimingRegister.remove("wobble");
        TimingRegister.remove("flare");
    }
    
    @Override
    public boolean isFinished() {
        return done;
    }

    @Override
    public void doAttackIteration(Enemy user, Hero victim) {    
        flare = TimingRegister.update("flare", flare, flareInc);
        wobble = TimingRegister.update("wobble", wobble, wobbleInc);
        if (wobble > 5 || wobble < -5)
        {
            wobbleInc = -wobbleInc;
        }
        
        distance = TimingRegister.update("attack", distance, inc);
        turn = TimingRegister.update("turn", turn, turnInc);
        if (turn >= 2.0)
        {
            turn = 2.0;
            turnInc = -turnInc;
        }
        else if (turn <= 0.3)
        {
            turn = 0.3;
            turnInc = -turnInc;
        }
        
        if (distance > 800)
        {
            done = true;
        }
        super.doAttackIteration(user, victim);
    }
    
    @Override
    public void drawAttack(Graphics2D g) {
        int x = 832-(int)distance;   
        int y = 404+(int)wobble;
        int h = (int)(40 * turn);
        int f = (int)(flare % 3) + 1;
        g.drawImage(GoodFight.getLoadedImage("sprites/effect_sprites/fiery_dart-" + f + ".png"), x, y-(h/2), x+160, y+(h/2), 0, 0, 160, 40, null);
    }

    @Override
    public int getAttackDamage(Enemy user) {
        int base = user.getBaseAttack();
        int var = (int)(base * .25);
        return base + (Global.getRandomInt(1, 5)*var);
    }
    
}

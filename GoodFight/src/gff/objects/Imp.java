/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gff.objects;

import gff.Global;
import gff.GoodFight;
import gff.GoodFight.Dir;
import gff.attacks.FieryDartAttack;
import gff.graphics.Animation;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.File;
import java.util.ArrayList;

/**
 *
 * @author Ray
 */
public class Imp extends Enemy {

    private static ArrayList<String> names = new ArrayList<>();

    static
    {
        names = Global.getTextListFromFile(new File("extern/text/imp_names.txt"));        
    }
    
    public Imp(int level) {
        super();
        setLevel(level);
        setName((String)Global.withdrawRandomFromList(names));
        setResistance(getMaxResistance());
        
        mySpecialAttacks.add(new FieryDartAttack());
        
        Animation anim = new Animation("imp_1", true, 32, 0, 8);
        this.addAnimation(anim);
        
        this.setImage(GoodFight.getLoadedImage("sprites/imp_1_default.png"));
        
        Point basePt = getRelativeBasePoint();
        setRelativePhysicalShape(new Rectangle(basePt.x-10, basePt.y-10, 20, 10));        
        
        this.setDirection(Dir.S);
        this.setAnimation(anim);
    }

    
    @Override
    public void go() {
        super.go();
    }

    @Override
    public void stop() {
        super.stop();
        setAnimation((Animation)null);
    }

    @Override
    protected int getResistancePerLevel() {
        return 15;
    }

    @Override
    protected int getBaseAttackPerLevel() {
        return 5;
    }
}

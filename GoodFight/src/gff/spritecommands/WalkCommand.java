/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gff.spritecommands;

import gff.Global;
import gff.GoodFight;
import gff.objects.Sprite;
import java.awt.Point;

/**
 *
 * @author Raymond
 */
public class WalkCommand extends SpriteCommand{
    private Sprite subject = null;
    private Point destination = null;
    private boolean westImpasse = true; // true = can't proceed west beyond limit.x; false = can't proceed east beyond limit.x
    private boolean northImpasse = true; // true = can't proceed north beyond limit.y; false = can't proceed south beyond limit.y
    
    public WalkCommand(Sprite subject, int moveDist, GoodFight.Dir direction) {
        this.subject = subject;
        destination = Global.move(subject.getLocation(), direction, moveDist);
            
        subject.setDirection(direction);
        westImpasse = destination.x < subject.getX();
        northImpasse = destination.y < subject.getY();
    }
    
    public WalkCommand(Sprite subject, int xMove, int yMove) {
        this.subject = subject;
        destination = new Point(subject.getX()+xMove, subject.getY()+yMove);
        if (xMove < 0)
        {
            subject.setDirection(GoodFight.Dir.W);
            westImpasse = true;
        }
        else
        {
            subject.setDirection(GoodFight.Dir.E);
            westImpasse = false;
        }
        if (yMove < 0)
        {            
            subject.setDirection(GoodFight.Dir.N);
            northImpasse = true;
        }
        else
        {
            subject.setDirection(GoodFight.Dir.S);
            northImpasse = false;
        }
    }
    
    public Point getDestination()
    {
        return destination;
    }
    
    // This method should only be called by Sprite.update():
    public void preventGoingTooFar()
    {
        if (westImpasse == (subject.getX() < destination.x))
            subject.setLocation(destination.x, subject.getY());
        if (northImpasse == (subject.getY() < destination.y))
            subject.setLocation(subject.getX(), destination.y);
    }
    
    @Override
    public void onStart() {
        subject.go();
    }
    @Override
    public void onStop() {
        subject.stop();
    }
    @Override
    public boolean isDone() {                                
        return subject.getX() == destination.x && subject.getY() == destination.y;
    }
    
}

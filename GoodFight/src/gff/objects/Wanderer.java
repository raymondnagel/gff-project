/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gff.objects;

import gff.Automaton;
import gff.GoodFight;
import java.awt.Point;

/**
 *
 * @author Ray
 */
public class Wanderer extends Sprite implements Automaton {
    protected static final int TARGET_RANGE = 200;
    protected static final long TIMEOUT = 100;
    protected static final int CLOSE_ENOUGH = 10;
    protected Point myGoal = null;
    protected long myTimer = 0;
    
    public void automate()
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

    @Override
    public void touch(Thing thing) {
        
    }

    @Override
    public boolean canIntrude(Thing thing) {
        return false;
    }
    
    
}

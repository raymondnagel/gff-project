/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gff;

/**
 *
 * @author Ray
 */
public class GameDifficulty {
    private String myName = null;
    private int mySwordBlockPct = 0;
    private int myMinEnemies = 0;
    private int myMaxEnemies = 0;
    private float myExpMultiplier = 1.0f;
    private double myEnemySpeed = 0.0;

    public GameDifficulty(String name, int swordBlockPct, float expMultiplier, double enemySpeed, int minEnemies, int maxEnemies) {
        myName = name;
        myEnemySpeed = enemySpeed;
        mySwordBlockPct = swordBlockPct;
        myExpMultiplier = expMultiplier;
        myMinEnemies = minEnemies;
        myMaxEnemies = maxEnemies;
    }
    
    public String getName()
    {
        return myName;
    }
    
    public int getSwordBlockPct()
    {
        return mySwordBlockPct;
    }
    
    public float getExpMultiplier()
    {
        return myExpMultiplier;
    }
    
    public int getRandomEnemyCountForUnsafeScene()
    {
        return Global.getRandomInt(myMinEnemies, myMaxEnemies);
    }
    
    public double getEnemySpeed()
    {
        return myEnemySpeed;
    }
}

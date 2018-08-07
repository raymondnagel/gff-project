/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gff.graphics;

import gff.Global;
import gff.GoodFight;
import gff.TimingRegister;
import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 *
 * @author Raymond
 */
public class SpiritBall {
    private final double gravity = .005;
    private int alpha = 255;
    private BufferedImage image = null;
    private double horzVel = 0;
    private double vertVel = 0;
    private String id = null;
    private double x = 0;
    private double y = 0;
    private long timeCreated = 0;
    
    public SpiritBall(int x, int y) {
        this.x = x;
        this.y = y;
        timeCreated = System.currentTimeMillis();
        id = "ball-" + System.nanoTime() + String.valueOf(Global.getRandomInt(0, 10000));
        int c = Global.getRandomInt(1, 3);
        switch(c)
        {
            case 1:
                image = GoodFight.getLoadedImage("sprites/effect_sprites/r_ball.png");
                break;
            case 2:
                image = GoodFight.getLoadedImage("sprites/effect_sprites/g_ball.png");
                break;
            case 3:
                image = GoodFight.getLoadedImage("sprites/effect_sprites/b_ball.png");
                break;                    
        }
        horzVel = (((double)Global.getRandomInt(0, 150))/100.0)-.75;
        vertVel = -(((double)Global.getRandomInt(0, 200))/100.0);        
    }
       
    public void update()
    {
        if (!isDone())
        {
            long sinceCreation = System.currentTimeMillis()-timeCreated;
            int cutOff = (int)sinceCreation-745;
            if (cutOff < 0) cutOff = 0;
            alpha = 255 - cutOff;
            if (alpha < 0) alpha = 0;
            vertVel = TimingRegister.update(id+"-grav", vertVel, gravity);
            x = TimingRegister.update(id+"-x", x, horzVel);
            y = TimingRegister.update(id+"-y", y, vertVel);            
            if (isDone())
            {
                TimingRegister.remove(id+"-grav");
                TimingRegister.remove(id+"-x");
                TimingRegister.remove(id+"-y");
            }
        }        
    }
 
    public boolean isDone()
    {
        return alpha <= 0;
    }
    
    public void paint(Graphics g)
    {
        if (!isDone())
        {
            Graphics2D g2 = (Graphics2D)g;   
            float tsp = (float)alpha/255f; 
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, tsp));            
            g2.drawImage(image, (int)x, (int)y, null);
        }
    }
}

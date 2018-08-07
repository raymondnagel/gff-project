/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gff.objects;

import gff.Global;
import gff.GoodFight;
import gff.items.Item;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

/**
 *
 * @author Ray
 */
public class TreasureChest extends Sprite {

    private static BufferedImage ourChestGraphic = GoodFight.getLoadedImage("sprites/chest.png");
    private static BufferedImage ourPremiumGraphic = GoodFight.getLoadedImage("sprites/premium_chest.png");
    
    private Item myItem = null;

    
    public TreasureChest(int x, int y, Item item, boolean premium) {
        super(premium ? ourPremiumGraphic : ourChestGraphic, x, y);
        Point basePt = getRelativeBasePoint();
        setRelativePhysicalShape(new Rectangle(basePt.x-10, basePt.y-10, 20, 10));
        myItem = item;
    }
    
    public Item getItem()
    {
        return myItem;
    }
    
    public void open()
    {            
        GoodFight.playSoundEffect("open_chest");
        Global.delay(700);
        myItem.onAcquire();
        this.destroy();
        GoodFight.getCurrentScene().obtainChest(this);          
    }

    @Override
    public boolean canIntrude(Thing thing) {
        return false;
    }

    @Override
    public void touch(Thing thing) {}
    
}
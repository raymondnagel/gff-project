/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gff.maps;

import gff.GoodFight;
import gff.Scene;
import gff.graphics.BackgroundLayer;
import gff.graphics.SpriteLayer;
import gff.objects.scenery.buildings.ChurchBuilding;
import gff.objects.scenery.Pew;
import gff.objects.scenery.Pulpit;

/**
 *
 * @author Ray
 */
public class ChurchMap extends Map{
    
    private ChurchBuilding building = null;
    
    public ChurchMap(ChurchBuilding building) {
        super("Church", 1, 1, 1, 0); 
        this.building = building;
        
        setScene(0, 0, mainFloor, new Scene(this, 0, 0, 0));
        myScenes[0][0][0].setTerrain(GoodFight.Terrain.CHURCH);
                        
        addLayersToScenes();
        createFurniture();        
//
//        setWallAt(getScene(0, 0, 0), Dir.W, true);
//        setWallAt(getScene(0, 0, 0), Dir.N, true);
//        setWallAt(getScene(0, 0, 0), Dir.S, true);
//        setWallAt(getScene(0, 0, 0), Dir.E, true);
    }
    
    public ChurchBuilding getBuilding()
    {
        return this.building;
    }
    
    private void createFurniture()
    {        
        SpriteLayer layer = myScenes[0][0][0].getSpriteLayer(SpriteLayer.TYPE.ACTION);
        
        // Pulpit:
        Pulpit pulpit = new Pulpit(512, 376);
        layer.addScenery(pulpit);
            
        Pew pew;
        for (int y = 0; y < 4; y++)
        {
            // Left pews:
            pew = new Pew(418, 472+(y*39));
            layer.addScenery(pew);
            
            // Right pews:
            pew = new Pew(606, 472+(y*39));
            layer.addScenery(pew);                        
        }
    }

    private void addLayersToScenes()
    {        
        Scene scene = myScenes[0][0][0];
        String bgImageFilename = "bg/church_bg.png";

        BackgroundLayer bgLayer = new BackgroundLayer(GoodFight.getLoadedImage(bgImageFilename));
        scene.addLayer(bgLayer);

        // Ground sprite layer (sprites under our feet):
        SpriteLayer groundLayer = new SpriteLayer(SpriteLayer.TYPE.GROUND);
        scene.addLayer(groundLayer);

        // Action layer (shared with characters):
        SpriteLayer spLayer = new SpriteLayer(SpriteLayer.TYPE.ACTION);
        scene.addLayer(spLayer);

        // Sky sprite layer (sprites over our heads):
        SpriteLayer skyLayer = new SpriteLayer(SpriteLayer.TYPE.SKY);
        scene.addLayer(skyLayer);                                                                
    }
    
}

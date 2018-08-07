/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gff;

import gff.graphics.SpriteLayer;
import gff.maps.StrongholdMap;
import gff.objects.scenery.Portal;
import gff.objects.scenery.buildings.StrongholdBuilding;
import java.awt.Point;

/**
 *
 * @author Raymond
 */
public class Stronghold {
    
    private StrongholdBuilding building = null;
    private String name = null;
    private Scene scene = null;

    public String getName()
    {
        return this.name;
    }
    
    public StrongholdBuilding getBuilding()
    {
        return this.building;
    }
    
    public final StrongholdMap getMap()
    {
        return (StrongholdMap)building.getMap();
    }
    
    public Scene getScene()
    {
        return this.scene;
    }
    
    public Stronghold(String name, StrongholdBuilding building, Scene scene) {  
        final Stronghold self = this;
        this.name = name;
        this.building = building;
        this.building.getMap().setName(this.name);
        this.building.setStronghold(self);
        this.scene = scene;
        
        // This section should be in subclasses instead, because each Stronghold will be different:        
        Portal outerPortal = new Portal(this.building.getX()+71, this.building.getBottom(), 30, 4, this.scene, new Point(this.building.getX()+80, this.building.getBottom()-20), GoodFight.Dir.N);
        this.scene.getSpriteLayer(SpriteLayer.TYPE.ACTION).addScenery(outerPortal);
        
        Portal innerPortal = new Portal(GoodFight.SCENE_LEFT+488, GoodFight.SCENE_TOP+576, 49, 7, getMap().getScene(0, 0, 0), new Point(GoodFight.SCENE_LEFT+500, GoodFight.SCENE_TOP+530), GoodFight.Dir.S);
        getMap().getEntranceScene().getSpriteLayer(SpriteLayer.TYPE.ACTION).addScenery(innerPortal);
        
        this.building.setEntrancePortals(innerPortal, outerPortal);

    }
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gff.maps;

import gff.Global;
import gff.Scene;
import gff.objects.scenery.buildings.DungeonBuilding;

/**
 *
 * @author Raymond
 */
public class DungeonMap extends StrongholdMap {

    public DungeonMap(DungeonBuilding building) {
        super("Dungeon of Doubt", building, 7, 7, 2, 1);
    }

    @Override
    protected void generateStronghold() {
        int eX = Global.getRandomInt(0, getWidth()-1);
        int eY = getHeight()-1;
        setScene(eX, eY, mainFloor, new Scene(this, eX, eY, mainFloor));
        entranceScene = myScenes[eX][eY][mainFloor];
    }

}

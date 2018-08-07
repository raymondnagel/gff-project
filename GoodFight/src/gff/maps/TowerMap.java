/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gff.maps;

import gff.Global;
import gff.Scene;
import gff.objects.scenery.buildings.TowerBuilding;

/**
 *
 * @author Raymond
 */
public class TowerMap extends StrongholdMap {

    public TowerMap(TowerBuilding building) {
        super("Tower of Deception", building, 5, 5, 6, 0);
    }

    @Override
    protected void generateStronghold() {
        int eX = 3;
        int eY = getHeight()-1;
        setScene(eX, eY, mainFloor, new Scene(this, eX, eY, mainFloor));
        entranceScene = myScenes[eX][eY][mainFloor];
    }

}

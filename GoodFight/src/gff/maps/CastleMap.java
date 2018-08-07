/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gff.maps;

import gff.Global;
import gff.Scene;
import gff.objects.scenery.buildings.CastleBuilding;

/**
 *
 * @author Raymond
 */
public class CastleMap extends StrongholdMap {

    public CastleMap(CastleBuilding building) {
        super("Castle of Perdition", building, 9, 9, 4, 0);
    }

    @Override
    protected void generateStronghold() {
        int eX = 5;
        int eY = getHeight()-1;
        setScene(eX, eY, mainFloor, new Scene(this, eX, eY, mainFloor));
        entranceScene = myScenes[eX][eY][mainFloor];
    }

}

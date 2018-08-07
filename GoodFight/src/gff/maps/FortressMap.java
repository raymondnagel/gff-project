/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gff.maps;

import gff.Global;
import gff.Scene;
import gff.objects.scenery.buildings.FortressBuilding;

/**
 *
 * @author Raymond
 */
public class FortressMap extends StrongholdMap {

    public FortressMap(FortressBuilding building) {
        super("Fortress of Enmity", building, 8, 8, 2, 0);
    }

    @Override
    protected void generateStronghold() {
        int eX = Global.getRandomInt(0, getWidth()-1);
        int eY = getHeight()-1;
        setScene(eX, eY, mainFloor, new Scene(this, eX, eY, mainFloor));
        entranceScene = myScenes[eX][eY][mainFloor];
    }

}

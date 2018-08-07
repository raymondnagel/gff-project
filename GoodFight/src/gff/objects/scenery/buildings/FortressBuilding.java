/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gff.objects.scenery.buildings;

import gff.GoodFight;
import gff.maps.FortressMap;

/**
 *
 * @author Raymond
 */
public class FortressBuilding extends StrongholdBuilding{

    public FortressBuilding() {
        super(GoodFight.getLoadedImage("sprites/stronghold.png"));
        setMap(new FortressMap(this));
    }
    
}

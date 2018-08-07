/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gff.objects.scenery.buildings;

import gff.GoodFight;
import gff.maps.KeepMap;

/**
 *
 * @author Raymond
 */
public class KeepBuilding extends StrongholdBuilding{

    public KeepBuilding() {
        super(GoodFight.getLoadedImage("sprites/stronghold.png"));
        setMap(new KeepMap(this));
    }
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gff.maps;

import gff.Global;
import gff.GoodFight;
import gff.GoodFight.Dir;
import gff.Scene;
import gff.objects.scenery.buildings.KeepBuilding;
import java.io.File;
import java.util.ArrayList;

/**
 *
 * @author Raymond
 */
public class KeepMap extends StrongholdMap {

    public KeepMap(KeepBuilding building) {
        super("Keep of Wickedness", building, 12, 12, 1, 0);
    }

    @Override
    protected void generateStronghold() {
        int eX = Global.getRandomInt(0, getWidth()-1);
        int eY = getHeight()-1; 
        createSection(eX, eY, mainFloor);
        entranceScene = myScenes[eX][eY][mainFloor];
    }

    protected void createSection(int x, int y, int floor) {
        ArrayList<Scene> section = new ArrayList<>();
        expand(x, y, floor, null, Global.getRandomInt(10, 12), section);
        sections.add(section);
    }    
    
    private int expand(int x, int y, int floor, Dir entryDir, int remaining, ArrayList<Scene> section)
    {        
        System.out.println("Expanding to: " + x + ", " + y + " (" + remaining + " remaining)");
        Scene room = new Scene(this, x, y, floor);
        room.setTerrain(GoodFight.Terrain.STRONGHOLD);
        
        // Add room to the map:
        setScene(x, y, floor, room);
        // Add room to the currently expanding section:
        section.add(room);
        
        remaining--;
        
        // Save the map to file (testing only):
        //saveToFile(floor, room, new File("stronghold_map_test.png" ));
        
        // First, do a check to see which walls MUST be in place.
        // If the room in a direction is out-of-bounds, or an already existing room, then create a wall.
        int walls = 0;
        
        for (int d = 0; d < Global.CardinalDirections.length; d++)
        {
            Dir dir = Global.CardinalDirections[d];
            if (dir != entryDir)
            {
                if (!isInBounds(x+dir.getXChange(), y+dir.getYChange(), floor) || room.getNeighbor(dir) != null)
                {
                    setWallAt(room, dir, true);
                    walls++;
                }
            }
        }
        
        System.out.println("mandatory wall check complete!");
        // Next, 50% chance to keep and expand into each remaining opening:
        
        for (int d = 0; d < Global.CardinalDirections.length; d++)
        {
            Dir dir = Global.CardinalDirections[d];
            if (dir != entryDir && !room.isWall(dir) && walls < 3)
            {
                if (remaining > 0 && Global.oddsCheck(1, 2)) // 50/50 chance:
                {
                    // 50%: if there are rooms remaining for this section, EXPAND in this direction
                    remaining = expand(x+dir.getXChange(), y+dir.getYChange(), floor, Global.oppositeDir(dir), remaining, section);                    
                }       
                else                {
                    // 50%: set a wall in this direction (DO NOT EXPAND)
                    setWallAt(room, dir, true);
                    walls++;
                }
            }
        }
        System.out.println("optional wall check complete!");        
        return remaining;
    }
}

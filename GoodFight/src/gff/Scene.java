/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gff;

import gff.maps.WorldMap;
import gff.maps.Map;
import gff.GoodFight.Dir;
import static gff.GoodFight.Dir.E;
import static gff.GoodFight.Dir.N;
import static gff.GoodFight.Dir.NE;
import static gff.GoodFight.Dir.NW;
import static gff.GoodFight.Dir.S;
import static gff.GoodFight.Dir.SE;
import static gff.GoodFight.Dir.SW;
import static gff.GoodFight.Dir.W;
import gff.GoodFight.Terrain;
import gff.graphics.SceneLayer;
import gff.graphics.SpriteLayer;
import gff.items.Item;
import gff.objects.scenery.buildings.Building;
import gff.objects.scenery.buildings.ChurchBuilding;
import gff.objects.scenery.Pedestal;
import gff.objects.scenery.Pillar;
import gff.objects.Thing;
import gff.objects.TreasureChest;
import gff.objects.scenery.Scenery;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.util.ArrayList;

/**
 *
 * @author Raymond
 */
public class Scene
{
    private boolean myNEWall = false;
    private boolean mySEWall = false;
    private boolean mySWWall = false;
    private boolean myNWWall = false;
    private boolean[] myNorthWall = {false, false, false, false, false, false, false, false, false, false, false, false};
    private boolean[] mySouthWall = {false, false, false, false, false, false, false, false, false, false, false, false};
    private boolean[] myEastWall = {false, false, false, false, false, false, false, false, false, false};
    private boolean[] myWestWall = {false, false, false, false, false, false, false, false, false, false};
    
    private boolean[] myWalls = {false, false, false, false};
    private Point myLocation = null;
    private int myFloor = 0;
    private int myWidth = GoodFight.SCENE_W;
    private int myHeight = GoodFight.SCENE_H;
    private boolean myDiscovered = false;
    private ArrayList<SceneLayer> myLayers = new ArrayList<>();
    
    private ArrayList<Shape> mySceneryZones = new ArrayList<>();    
    private ArrayList<Shape> myNoIntersectZones = new ArrayList<>(); // Zones which may not be intersected by the bounding rectangle of any Thing.
    private ArrayList<Shape> myNoIntrusionZones = new ArrayList<>(); // Zones which may not be intersected by the physical shape of any Thing.   
    
    
    protected Terrain myTerrain = Terrain.PLAIN;
    protected Map myMap = null;    
    protected TreasureChest myPermaChest = null;
    protected Building myBuilding = null;
    
    
    public Scene(Map map, int x, int y, int floor) {
        myMap = map;
        myLocation = new Point(x,y);
    }
    
    public SpriteLayer getSpriteLayer(SpriteLayer.TYPE type)
    {
        for (int i = 0; i < myLayers.size(); i++)
        {
            if (myLayers.get(i) instanceof SpriteLayer)
            {
                if (((SpriteLayer)myLayers.get(i)).getType() == type)
                    return (SpriteLayer)myLayers.get(i);
            }
        }
        return null;
    }
    
    public boolean isWall(Dir d)
    {
        switch(d)
        {
            case N:
                return myWalls[0];
            case E:
                return myWalls[1];
            case S:
                return myWalls[2];
            case W:
                return myWalls[3];
            default:
                return true;
        }
    }
    public void setWall(Dir d, boolean wall)
    {
        switch(d)
        {
            case N:
                myWalls[0] = wall;
                for (int s = 0; s < myNorthWall.length; s++)
                    myNorthWall[s] = wall;
                break;
            case E:
                myWalls[1] = wall;
                for (int s = 0; s < myEastWall.length; s++)
                    myEastWall[s] = wall;
                break;
            case S:
                myWalls[2] = wall;
                for (int s = 0; s < mySouthWall.length; s++)
                    mySouthWall[s] = wall;
                break;
            case W:
                myWalls[3] = wall;
                for (int s = 0; s < myWestWall.length; s++)
                    myWestWall[s] = wall;
                break;
            default:
                return;
        }
    }
    public void setWallSections(Dir d, boolean[] sections)
    {
        switch (d)
        {
            case N:
                myNorthWall = sections;
                break;
            case E:
                myEastWall = sections;
                break;
            case S:
                mySouthWall = sections;
                break;
            case W:
                myWestWall = sections;
                break;
        }
    }
    public boolean[] getWallSection(Dir d)
    {
        switch (d)
        {
            case N:
                return myNorthWall;
            case E:
                return myEastWall;
            case S:
                return mySouthWall;
            case W:
                return myWestWall;
        }
        return null;
    }
    
    public void setCornerWallSection(Dir d, boolean wall)
    {
        switch (d)
        {
            case NE:
                myNEWall = wall;
                if (getNeighbor(NE) != null) getNeighbor(NE).mySWWall = wall;
                if (getNeighbor(N) != null) getNeighbor(N).mySEWall = wall;
                if (getNeighbor(E) != null) getNeighbor(E).myNWWall = wall;                
                break;
            case SE:
                mySEWall = wall;
                if (getNeighbor(SE) != null) getNeighbor(SE).myNWWall = wall;
                if (getNeighbor(S) != null) getNeighbor(S).myNEWall = wall;
                if (getNeighbor(E) != null) getNeighbor(E).mySWWall = wall; 
                break;
            case SW:
                mySWWall = wall;
                if (getNeighbor(SW) != null) getNeighbor(SW).myNEWall = wall;
                if (getNeighbor(S) != null) getNeighbor(S).myNWWall = wall;
                if (getNeighbor(W) != null) getNeighbor(W).mySEWall = wall; 
                break;
            case NW:
                myNWWall = wall;
                if (getNeighbor(NW) != null) getNeighbor(NW).mySEWall = wall;
                if (getNeighbor(N) != null) getNeighbor(N).mySWWall = wall;
                if (getNeighbor(W) != null) getNeighbor(W).myNEWall = wall; 
                break;
        }
    }
    public boolean getCornerWallSection(Dir d)
    {
        switch (d)
        {
            case NE:
                return myNEWall;
            case SE:
                return mySEWall;
            case SW:
                return mySWWall;
            case NW:
                return myNWWall;
        }
        return false;
    }
    
    public ArrayList<Shape> getNoIntersectZones()
    {
        return myNoIntersectZones;
    }
    public ArrayList<Shape> getNoIntrusionZones()
    {
        return myNoIntrusionZones;
    }
    public ArrayList<Shape> getSceneryZones()
    {
        return mySceneryZones;
    }
    
    public void addNoIntersectZone(Shape sector)
    {       
        myNoIntersectZones.add(sector);
    }
    public void addNoIntrusionZone(Shape sector)
    {       
        myNoIntrusionZones.add(sector);
    }
    public void addSceneryZone(Shape sector)
    {       
        mySceneryZones.add(sector);
    }
    public void clearZones()
    {
        mySceneryZones.clear();
        myNoIntersectZones.clear();
        myNoIntrusionZones.clear();
    }
        
    public boolean isObjectified()
    {
        return this == GoodFight.getObjectifiedScene();
    }
    
    public void addPermaChest(Item containedItem)
    {
        // Add accompanying scenery, the "shrine":
        // surrounded by pillars, with a pedestal in the center under the chest.
        
        int sW = 140;
        int sH = 140;
        int sL = Global.getRandomInt(GoodFight.LEFT_BOUND, GoodFight.RIGHT_BOUND-sW);
        int sT = Global.getRandomInt(GoodFight.TOP_BOUND, GoodFight.BOTTOM_BOUND-sH);
        
        addNoIntersectZone(new Rectangle(sL, sT, sW, sH));
        
        SpriteLayer layer = getSpriteLayer(SpriteLayer.TYPE.ACTION);
        Pillar p = new Pillar(0,0);
        p.setLocation(sL, sT);
        layer.addScenery(p);
        
        p = new Pillar(0,0);
        p.setLocation((sL+sW)-p.getWidth(), sT);
        layer.addScenery(p);                
        
        int pedY = (int)(p.getPhysicalShape().getBounds().y+p.getPhysicalShape().getBounds().height);
        
        p = new Pillar(0,0);
        p.setLocation(sL, (sT+sH)-p.getHeight());
        layer.addScenery(p);
        
        p = new Pillar(0,0);
        p.setLocation((sL+sW)-p.getWidth(), (sT+sH)-p.getHeight());
        layer.addScenery(p);
        
        pedY = (int)((pedY+p.getPhysicalShape().getBounds().y)/2);
        
        layer = getSpriteLayer(SpriteLayer.TYPE.GROUND);
        Pedestal ped = new Pedestal(0, 0);
        ped.setLocation((sL+(sW/2)) - (ped.getWidth()/2), pedY);
        layer.addScenery(ped);
        
        // Create the chest here.
        // It will be loaded into GoodFight.ourSprites when this Scene becomes active.
        myPermaChest = new TreasureChest(sL+(sW/2), pedY+2, containedItem, true);
    }
    
    public TreasureChest getPermaChest()
    {
        return myPermaChest;
    }
    
    public void setBuilding(Building building)
    {
        myBuilding = building;
        myBuilding.setContainingScene(this);
    }
    
    public Building getBuilding()
    {
        return myBuilding;
    }
    
    public void obtainChest(TreasureChest chest)
    {
        if (chest == myPermaChest)
        {
            myPermaChest = null;
            if (myMap instanceof WorldMap)
            {
                WorldMap map = (WorldMap)myMap;
                map.getPremiumChestScenes().remove(this);
                GoodFight.objectifyNearestPremiumChest();
            }            
        }
    }
    
    public void hide()
    {
        myDiscovered = false;
    }
    
    public void discover()
    {
        myDiscovered = true;
    }
    
    public boolean isDiscovered()
    {
        return myDiscovered;
    }
    
    public Scene getRandomNeighbor()
    {
        int n = Global.getRandomInt(1, 4);
        int start = n;        
        do
        {
            if (n == 1 && getNeighbor(N) != null)
                return getNeighbor(N);
            if (n == 2 && getNeighbor(E) != null)
                return getNeighbor(E);
            if (n == 3 && getNeighbor(S) != null)
                return getNeighbor(S);
            if (n == 4 && getNeighbor(W) != null)
                return getNeighbor(W);
            n++;
            if (n > 4) n = 1;
        } while (n != start);
        return null;         
    }
    
    public Scene connectToRandomUndiscoveredNeighbor()
    {
        int n = Global.getRandomInt(1, 4);
        int start = n;        
        do
        {
            Scene neighbor = getNeighbor(N);
            if (n == 1 && (neighbor != null) && !neighbor.isDiscovered())
            {
                getMap().setWallAt(this, N, false);
                return neighbor;
            }
            neighbor = getNeighbor(E);
            if (n == 2 && (neighbor != null) && !neighbor.isDiscovered())
            {
                getMap().setWallAt(this, E, false);
                return neighbor;
            }
            neighbor = getNeighbor(S);
            if (n == 3 && (neighbor != null) && !neighbor.isDiscovered())
            {
                getMap().setWallAt(this, S, false);
                return neighbor;
            }
            neighbor = getNeighbor(W);
            if (n == 4 && (neighbor != null) && !neighbor.isDiscovered())
            {
                getMap().setWallAt(this, W, false);
                return neighbor;
            }
            n++;
            if (n > 4) n = 1;
        } while (n != start);
        return null;         
    }
    
    public boolean isSafe()
    {
        return myTerrain == Terrain.CHURCH || (myBuilding != null && myBuilding instanceof ChurchBuilding);
    }

    // The Scene will be "interesting" if it contains a church, stronghold,
    // boss, or permanent chest. This is checked only to make sure we spread such
    // "interesting" things evenly across the world.
    public boolean isInteresting()
    {
        return getBuilding() != null || getPermaChest() != null;
    }
    
    public int getFloor()
    {
        return myFloor;
    }
    
    public Scene getNeighbor(Dir d)
    {
        Map map = getMap();
        Scene neighbor = map.getScene(Global.move(myLocation, d, 1), myFloor);
        return neighbor;
    }
    
    public boolean isNeighborDiscovered(Dir d)
    {
        Scene neighbor = getNeighbor(d);
        if (neighbor != null)
        {
            return neighbor.isDiscovered();
        }
        else
        {
            return false;
        }
    }
    
    public Point getLocation()
    {
        return myLocation;
    }
    public int getX()
    {
        return myLocation.x;
    }
    public int getY()
    {
        return myLocation.y;
    }
    
    public Map getMap()
    {
        return myMap;
    }
    
    public void setTerrain(Terrain terr)
    {
        myTerrain = terr;
    }
    public Terrain getTerrain()
    {
        return myTerrain;
    }
    
    public int getWidth()
    {
        return myWidth;
    }

    public int getHeight()
    {
        return myHeight;
    }

    public ArrayList<SceneLayer> getLayers()
    {
        return myLayers;
    }

    public int getNumLayers()
    {
        return myLayers.size();
    }

    public void addLayer(SceneLayer layer)
    {
        myLayers.add(layer);
    }

    public Point getRandomPoint()
    {
        return new Point(Global.getRandomInt(GoodFight.LEFT_BOUND, GoodFight.RIGHT_BOUND), Global.getRandomInt(GoodFight.TOP_BOUND, GoodFight.BOTTOM_BOUND));
    }
    
    public Point getRandomPointForThing(Thing thing, boolean usePhysBound)
    {
        return getRandomPointForThingWithin(thing, GoodFight.LEFT_BOUND, GoodFight.TOP_BOUND, GoodFight.RIGHT_BOUND-GoodFight.LEFT_BOUND, GoodFight.BOTTOM_BOUND-GoodFight.TOP_BOUND, usePhysBound);
    }
    
    public Point getRandomPointForThingWithin(Thing thing, int x, int y, int width, int height, boolean usePhysBound)
    {
        if (usePhysBound)
            return new Point(Global.getRandomInt(x-thing.getLeftMargin(), ((x+width)-thing.getWidth())+thing.getRightMargin()), Global.getRandomInt(y-thing.getTopMargin(), ((y+height)-thing.getHeight())+thing.getBottomMargin()));
        else
            return new Point(Global.getRandomInt(x, (x+width)-thing.getWidth()), Global.getRandomInt(y, (y+height)-thing.getHeight()));
    }

    public boolean withinScene(Thing thing)
    {
        return thing.getLeft() >= GoodFight.SCENE_LEFT && thing.getRight() <= GoodFight.SCENE_RIGHT &&
               thing.getTop() >= GoodFight.SCENE_TOP && thing.getBottom() <= GoodFight.SCENE_BOTTOM;                
    }
    
    public boolean withinBounds(Thing thing)
    {
        return thing.getLeft() >= GoodFight.LEFT_BOUND && thing.getRight() <= GoodFight.RIGHT_BOUND &&
               thing.getTop() >= GoodFight.TOP_BOUND && thing.getBottom() <= GoodFight.BOTTOM_BOUND;
    }

    
    public boolean overlapsScenery(Thing thing, SpriteLayer layer)
    {
        for (int s = 0; s < layer.getSceneryObjects().size(); s++)
        {
            if (layer.getSceneryObjects().get(s).intersectsPhysicallyWith(thing))
                return true;
        }
        return false;
    }
    
    public boolean overlapsRestrictedZone(Thing thing)
    {
        for (int z = 0; z < myNoIntersectZones.size(); z++)
        {
            if (Global.isIntersecting(myNoIntersectZones.get(z), thing.getBounds()))
                return true;
        }
        for (int z = 0; z < myNoIntrusionZones.size(); z++)
        {
            if (Global.isIntersecting(myNoIntrusionZones.get(z), thing.getPhysicalShape()))
                return true;
        }
        return false;
    }
    
    public boolean overlapsAnything(Thing thing, SpriteLayer layer)
    {
        return overlapsRestrictedZone(thing) || overlapsScenery(thing, layer) || GoodFight.overlapsSprite(thing);
    }
    
    public boolean placeRandomlyIfPossible(Thing thing, SpriteLayer layer, int attempts, boolean checkCurrentSprites, boolean usePhysBound)
    {
        boolean ok;
        Point p;        
        do
        {
            thing.setLocation(getRandomPointForThing(thing, usePhysBound));
            ok = !overlapsAnything(thing, layer);            
            attempts--;
        } while(!ok && attempts > 0);
        if (attempts > 0)
        {
            if (thing instanceof Scenery)
                layer.addScenery((Scenery)thing);
            return true;
        }
        else
        {
            thing.setLocation(null);
            return false;
        }
    }
    
    public boolean placeRandomlyWithinIfPossible(Thing thing, SpriteLayer layer, int attempts, boolean checkCurrentSprites, boolean usePhysBound, int x, int y, int width, int height)
    {
        boolean ok;
        Point p;        
        do
        {
            thing.setLocation(getRandomPointForThingWithin(thing, x, y, width, height, usePhysBound));
            ok = !overlapsAnything(thing, layer);            
            attempts--;
        } while(!ok && attempts > 0);
        if (attempts > 0)
        {
            if (thing instanceof Scenery)
                layer.addScenery((Scenery)thing);
            return true;
        }
        else
        {
            thing.setLocation(null);
            return false;
        }
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gff.maps;

import gff.Global;
import gff.GoodFight;
import gff.GoodFight.Dir;
import static gff.GoodFight.MAP_WALL_COLOR;
import static gff.GoodFight.Terrain.CHURCH;
import static gff.GoodFight.Terrain.DESERT;
import static gff.GoodFight.Terrain.FOREST;
import static gff.GoodFight.Terrain.MOUNTAIN;
import static gff.GoodFight.Terrain.STRONGHOLD;
import static gff.GoodFight.Terrain.SWAMP;
import static gff.GoodFight.Terrain.TUNDRA;
import static gff.GoodFight.getLoadedImage;
import gff.Scene;
import static gff.maps.WorldMap.TRIFLE_TRIES;
import gff.graphics.SpriteLayer;
import gff.objects.scenery.BottomWallCorner;
import gff.objects.scenery.TopWallCorner;
import gff.objects.scenery.HorzWallSection;
import gff.objects.scenery.SceneryConstruct;
import gff.objects.scenery.Scenery;
import gff.objects.scenery.VertWallSection;
import gff.objects.scenery.buildings.ChurchBuilding;
import gff.objects.scenery.buildings.StrongholdBuilding;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author Ray
 */
public abstract class Map {
    public static final int ADJACENT_SCENERY_GAP = 20;
    public static final int ADJACENT_SCENERY_WIGGLE_ROOM = 10;
    public static final int SCENERY_PLACEMENT_ATTEMPTS = 10;
    public static final int TRIFLE_TRIES = 10; // Number of attempts to place menial scenery (trees) before giving up.
    
    private int myWidth = 0;
    private int myHeight = 0;
    private int myDepth = 0;
    protected int mainFloor = 0;
    protected String myName = null;
    protected Scene[][][] myScenes = null;
    protected ArrayList<Scene>[] myScenesByFloor;
    
    public void setName(String areaName)
    {
        myName = areaName;
    }
    public String getName()
    {
        return myName;
    }
    public String getFloorName()
    {               
        if (myDepth == 1)
            return getName();
        else
        {
            int currentFloor = GoodFight.getCurrentScene().getFloor();
            int diff = mainFloor - currentFloor;
            
            if (diff == 0)
                return getName() + " (main floor)";
            else if (diff > 0)
                return getName() + " (" + Global.getOrdinalNumber(diff+1) + ")";
            else
                return getName() + " (basement)";             
        }
    }
    public int getWidth()
    {
        return myWidth;
    }
    public int getHeight()
    {
        return myHeight;
    }
    public int getDepth()
    {
        return myDepth;
    }
    
    public boolean isInBounds(int x, int y, int floor)
    {
        return x >= 0 && x < myWidth && y >= 0 && y < myHeight && floor >= 0 && floor < myDepth;
    }
    
    public void setScene(int x, int y, int floor, Scene scene)
    {
        myScenes[x][y][floor] = scene;
        this.myScenesByFloor[floor].add(scene);
    }
    
    public Scene getScene(int x, int y, int floor)
    {
        if (x < 0 || x >= myWidth || y < 0 || y >= myHeight)
        {
            return null;
        }
        else
        {
            return myScenes[x][y][floor];
        }
    }
    
    public Scene getScene(Point location, int floor)
    {
        return getScene(location.x, location.y, floor);
    }
        
    public Scene getRandomSceneByFloor(int floor)
    {
        return (Scene)Global.getRandomFromList(myScenesByFloor[floor]);
    }
    
    public Scene getRandomScene()
    {
        int floor = Global.getRandomInt(1, myDepth)-1;
        return (Scene)Global.getRandomFromList(myScenesByFloor[floor]);
    }
    
    public Map(String name, int widthInScenes, int heightInScenes, int numFloors, int mainFloor) {
        myName = name;
        myWidth = widthInScenes;
        myHeight = heightInScenes;
        myDepth = numFloors;
        this.mainFloor = mainFloor;
        myScenes = new Scene[myWidth][myHeight][numFloors];
        myScenesByFloor = new ArrayList[numFloors];
        for (int f = 0; f < numFloors; f++)
        {
            myScenesByFloor[f] = new ArrayList<>();
        }
    }

    
    
    // Methods for scene creation:
    
    public void setWallAt(Scene scene, GoodFight.Dir d, boolean wall)
    {             
        if (scene != null)
        {            
            scene.setWall(d, wall);
            Scene neighboringScene = scene.getNeighbor(d);
            if (neighboringScene != null)
            {
                neighboringScene.setWall(Global.oppositeDir(d), wall);
            }
        }
        
    }
    
    public void setWallAt(int x, int y, int floor, GoodFight.Dir d, boolean wall)
    {
        setWallAt(getScene(x, y, floor), d, wall);
    }
        
    protected static boolean[] getRandomHorzWallSections()
    {
        boolean[] sections = new boolean[12];
        int type = Global.getRandomInt(1, 4);
        switch(type)
        {
            case 1: // sprinkle
                for (int w = 0; w < 11; w++)
                {
                    int s = Global.getRandomInt(0, 11);
                    sections[s] = true;
                }
                break;
            case 2: // symmetrical
                int num = Global.getRandomInt(1, 6);
                for (int w = 0; w < num; w++)
                {
                    int s = Global.getRandomInt(0, 5);
                    sections[s] = true;
                    sections[11-s] = true;
                }
                break;
            case 3: // block
                int b = Global.getRandomInt(0, 11);
                int bS = Global.getRandomInt(0, 11);
                for (int r = 0; r < bS; r++)
                {
                    if (b+r <= 11)
                        sections[b+r] = true;
                }
                break;
            case 4: // hole
                for (int f = 0; f < 12; f++)
                    sections[f] = true;
                int h = Global.getRandomInt(0, 11);
                int hS = Global.getRandomInt(0, 11);
                for (int r = 0; r < hS; r++)
                {
                    if (h+r <= 11)
                        sections[h+r] = false;
                }
                break;
        }
        return sections;
    }
    
    protected static boolean[] getRandomVertWallSections()
    {
        boolean[] sections = new boolean[10];
        int type = Global.getRandomInt(1, 4);
        switch(type)
        {
            case 1: // sprinkle
                for (int w = 0; w < 9; w++)
                {
                    int s = Global.getRandomInt(0, 9);
                    sections[s] = true;
                }
                break;
            case 2: // symmetrical
                int num = Global.getRandomInt(1, 5);
                for (int w = 0; w < num; w++)
                {
                    int s = Global.getRandomInt(0, 4);
                    sections[s] = true;
                    sections[7-s] = true;
                }
                break;
            case 3: // block
                int b = Global.getRandomInt(0, 9);
                int bS = Global.getRandomInt(0, 9);
                for (int r = 0; r < bS; r++)
                {
                    if (b+r <= 9)
                        sections[b+r] = true;
                }
                break;
            case 4: // hole
                for (int f = 0; f < 10; f++)
                    sections[f] = true;
                int h = Global.getRandomInt(0, 9);
                int hS = Global.getRandomInt(0, 9);
                for (int r = 0; r < hS; r++)
                {
                    if (h+r <= 9)
                        sections[h+r] = false;
                }
                break;
        }
        return sections;
    }
    
    
    private int getAdjustedGap()
    {
        return ADJACENT_SCENERY_GAP + (Global.getRandomInt(-ADJACENT_SCENERY_WIGGLE_ROOM, ADJACENT_SCENERY_WIGGLE_ROOM));
    }
    
    protected Point getBaseForNextAdjacentObject(Scenery scenery, Dir d)
    {
        Point base = scenery.getBasePoint();
        switch (d)
        {
            case N:
                base.y -= (scenery.getHeight()/2);
                break;
            case NE:
                base.x += (scenery.getWidth()/2);
                base.y -= (scenery.getHeight()/2);
                break;
            case E:
                base.x += (scenery.getWidth()/2);
                break;
            case SE:
                base.x += (scenery.getWidth()/2);
                base.y += (scenery.getHeight()/2);
                break;
            case S:
                base.y += (scenery.getHeight()/2);
                break;
            case SW:
                base.x -= (scenery.getWidth()/2);
                base.y += (scenery.getHeight()/2);
                break;
            case W:
                base.x -= (scenery.getWidth()/2);                  
                break;
        }
        base.x += getAdjustedGap();
        base.y += getAdjustedGap();
        return base;
    }
    
    protected void generateWallSceneryPattern(Scene scene, Dir wallDir, SceneryConstruct construct)
    {
        boolean fail = false;
        SpriteLayer layer = scene.getSpriteLayer(SpriteLayer.TYPE.ACTION);
        do
        {
            Scenery obj = construct.getNewSceneryObject();
            
            switch (wallDir)
            {
                case N:
                    fail = !scene.placeRandomlyWithinIfPossible(obj, layer, 10, false, true, GoodFight.SCENE_LEFT, GoodFight.SCENE_TOP, GoodFight.SCENE_W, GoodFight.NATURAL_WALL_THICKNESS);
                    break;
                case E:
                    fail = !scene.placeRandomlyWithinIfPossible(obj, layer, 10, false, true, GoodFight.SCENE_RIGHT-GoodFight.NATURAL_WALL_THICKNESS, GoodFight.SCENE_TOP, GoodFight.NATURAL_WALL_THICKNESS, GoodFight.SCENE_H);
                    break;
                case S:
                    fail = !scene.placeRandomlyWithinIfPossible(obj, layer, 10, false, true, GoodFight.SCENE_LEFT, GoodFight.SCENE_BOTTOM-GoodFight.NATURAL_WALL_THICKNESS, GoodFight.SCENE_W, GoodFight.NATURAL_WALL_THICKNESS);
                    break;
                case W:
                    fail = !scene.placeRandomlyWithinIfPossible(obj, layer, 10, false, true, GoodFight.SCENE_LEFT, GoodFight.SCENE_TOP, GoodFight.NATURAL_WALL_THICKNESS, GoodFight.SCENE_H);
                    break;
           }            
        } while (!fail);
    }
    
    protected void generateAsymmetricalSceneryPattern(Scene scene, int objectCount, SceneryConstruct construct)
    {
        int objects = 0;
        SpriteLayer actionLayer = scene.getSpriteLayer(SpriteLayer.TYPE.ACTION);
        boolean placed;
        Scenery scenery;
        
        // Create a random number of "pieces". Each pieces contains multiple Scenery objects.
        int numPieces = Global.getRandomInt(1, 10);        
        for (int p = 0; p < numPieces; p++)
        {
            // Construct a new Scenery object and attempt to place it:
            scenery = construct.getNewSceneryObject();
            do 
            { 
                placed = scene.placeRandomlyIfPossible(scenery, actionLayer, SCENERY_PLACEMENT_ATTEMPTS, false, true);             
            } while (!placed);            
            // When the first object is placed, extend a line of scenery in a random direction.
            Dir d = Global.randomDir();
            int count = Global.getRandomInt(1, 10);
            for (int c = 0; c < count; c++)
            {
                Point nextBase = getBaseForNextAdjacentObject(scenery, d);
                scenery = construct.getNewSceneryObject();
                scenery.setBasePoint(nextBase);
                placed = scene.withinBounds(scenery) && !scene.overlapsAnything(scenery, actionLayer);
                if (placed)
                {
                    actionLayer.addScenery(scenery);
                }
            }
        }
       
    }
    protected void generateHorzSymmetricalSceneryPattern(Scene scene, int objectCount, SceneryConstruct construct)
    {
        
    }
    protected void generateVertSymmetricalSceneryPattern(Scene scene, int objectCount, SceneryConstruct construct)
    {
        
    }
    protected void generateQuadSymmetricalSceneryPattern(Scene scene, int objectCount, SceneryConstruct construct)
    {
        
    }
    protected void generateSprinkleSceneryPattern(Scene scene, int objectCount, SceneryConstruct construct)
    {
        SpriteLayer actionLayer = scene.getSpriteLayer(SpriteLayer.TYPE.ACTION);
        for (int t = 0; t < objectCount; t++)
        {
            Scenery obj = construct.getNewSceneryObject();
            scene.placeRandomlyIfPossible(obj, actionLayer, TRIFLE_TRIES, false, true);
        }
    }
    protected void generateMazelikeSceneryPattern(Scene scene, int objectCount, SceneryConstruct construct)
    {
        
    }
    protected void generateChambersSceneryPattern(Scene scene, int objectCount, SceneryConstruct construct)
    {
        
    }
    protected void generateTunnelsSceneryPattern(Scene scene, int objectCount, SceneryConstruct construct)
    {
        
    }
    
    protected void addWallScenery(Scene scene)
    {
        // NW Corner:
        if (scene.getCornerWallSection(Dir.NW))
        {
            TopWallCorner wall = new TopWallCorner(0, GoodFight.SCENE_TOP+0);
            scene.getSpriteLayer(SpriteLayer.TYPE.ACTION).addScenery(wall);            
        }
        // NE Corner:
        if (scene.getCornerWallSection(Dir.NE))
        {
            TopWallCorner wall = new TopWallCorner(GoodFight.SCENE_RIGHT-49, GoodFight.SCENE_TOP+0);
            scene.getSpriteLayer(SpriteLayer.TYPE.ACTION).addScenery(wall);            
        }
        // SW Corner:
        if (scene.getCornerWallSection(Dir.SW))
        {
            BottomWallCorner wall = new BottomWallCorner(0, GoodFight.SCENE_BOTTOM-49);
            scene.getSpriteLayer(SpriteLayer.TYPE.ACTION).addScenery(wall);            
        }
        // SE Corner:
        if (scene.getCornerWallSection(Dir.SE))
        {
            BottomWallCorner wall = new BottomWallCorner(GoodFight.SCENE_RIGHT-49, GoodFight.SCENE_BOTTOM-49);
            scene.getSpriteLayer(SpriteLayer.TYPE.ACTION).addScenery(wall);            
        }
            
        for (int x = 0; x < 12; x++)
        {
            if (scene.isWall(Dir.N) || scene.getWallSection(Dir.N)[x])
            {
                HorzWallSection wall = new HorzWallSection(50+(x*77), GoodFight.SCENE_TOP+0);
                scene.getSpriteLayer(SpriteLayer.TYPE.ACTION).addScenery(wall);
            }
            if (scene.isWall(Dir.S) || scene.getWallSection(Dir.S)[x])
            {
                HorzWallSection wall = new HorzWallSection(50+(x*77), GoodFight.SCENE_BOTTOM-49);
                scene.getSpriteLayer(SpriteLayer.TYPE.ACTION).addScenery(wall);
            }
        }        
        for (int y = 0; y < 10; y++)
        {
            if (scene.isWall(Dir.W) || scene.getWallSection(Dir.W)[y])
            {
                VertWallSection wall = new VertWallSection(GoodFight.SCENE_LEFT+0, GoodFight.SCENE_TOP+(50+(y*58)));
                scene.getSpriteLayer(SpriteLayer.TYPE.ACTION).addScenery(wall);
            }
            if (scene.isWall(Dir.E) || scene.getWallSection(Dir.E)[y])
            {
                VertWallSection wall = new VertWallSection(GoodFight.SCENE_RIGHT-49, GoodFight.SCENE_TOP+(50+(y*58)));
                scene.getSpriteLayer(SpriteLayer.TYPE.ACTION).addScenery(wall);
            }
        }
    }
    
    public void saveToFile(int floor, Scene marked, File f)
    {
        int sqW = 38; int sqH = 22; int cr = 1;
        int dX = sqW; int dY = sqH;
        BufferedImage mapImage = new BufferedImage((getWidth()+2)*sqW, (getHeight()+2)*sqH, BufferedImage.TYPE_INT_RGB);
        BufferedImage stampImg;
        Scene drawnScene;   
        Graphics g = mapImage.getGraphics();
        
        
        for (int x = 0; x < getWidth(); x++)
        {
            for (int y = 0; y < getHeight(); y++)
            {
                drawnScene = getScene(x,y, floor);
                if (drawnScene != null)
                {
                    switch(drawnScene.getTerrain())
                    {
                        case PLAIN:
                            stampImg = getLoadedImage("interface/map/map_grass.png");
                            break;
                        case FOREST:
                            stampImg = getLoadedImage("interface/map/map_forest.png");
                            break;
                        case MOUNTAIN:
                            stampImg = getLoadedImage("interface/map/map_mount.png");
                            break;
                        case DESERT:
                            stampImg = getLoadedImage("interface/map/map_desert.png");
                            break;
                        case SWAMP:
                            stampImg = getLoadedImage("interface/map/map_swamp.png");
                            break;
                        case TUNDRA:
                            stampImg = getLoadedImage("interface/map/map_tundra.png");
                            break;
                        case STRONGHOLD:
                        case CHURCH:
                            stampImg = getLoadedImage("interface/map/map_floor.png");
                            break;
                        default:
                            stampImg = getLoadedImage("interface/map/map_floor.png");                            
                    }
                    g.drawImage(stampImg, dX+(x*sqW), dY+(y*sqH), null);

                    // Draw the Building, if applicable:
                    if (drawnScene.getBuilding() instanceof ChurchBuilding)
                    {
                        g.drawImage(getLoadedImage("interface/map/map_church.png"), dX+(x*sqW), dY+(y*sqH), null);
                    }
                    else if (drawnScene.getBuilding() instanceof StrongholdBuilding)
                    {
                        g.drawImage(getLoadedImage("interface/map/map_hold.png"), dX+(x*sqW), dY+(y*sqH), null);
                    }    

                    // Draw wall lines:
                    g.setColor(MAP_WALL_COLOR);

                    // North/South
                    for (int h = 0; h < 12; h++)
                    {
                        int wX = dX+(x*sqW)+cr+(h*3);
                        if (drawnScene.getWallSection(Dir.N)[h])
                            g.drawLine(wX, dY+(y*sqH), wX+2, dY+(y*sqH));
                        if (drawnScene.getWallSection(Dir.S)[h])
                            g.drawLine(wX, dY+(y*sqH)+(sqH-1), wX+2, dY+(y*sqH)+(sqH-1));
                    }
                    // East/West
                    for (int v = 0; v < 10; v++)
                    {
                        int wY = dY+(y*sqH)+cr+(v*2);
                        if (drawnScene.getWallSection(Dir.W)[v])
                            g.drawLine(dX+(x*sqW), wY, dX+(x*sqW), wY+1);
                        if (drawnScene.getWallSection(Dir.E)[v])
                            g.drawLine(dX+(x*sqW)+(sqW-1), wY, dX+(x*sqW)+(sqW-1), wY+1);
                    }
                    // 4 Corners:
                    if (drawnScene.getCornerWallSection(Dir.NW))
                        Global.drawDot(g, dX+(x*sqW), dY+(y*sqH));
                    if (drawnScene.getCornerWallSection(Dir.NE))
                        Global.drawDot(g, dX+(x*sqW)+(sqW-1), dY+(y*sqH));
                    if (drawnScene.getCornerWallSection(Dir.SW))
                        Global.drawDot(g, dX+(x*sqW), dY+(y*sqH)+(sqH-1));
                    if (drawnScene.getCornerWallSection(Dir.SE))
                        Global.drawDot(g, dX+(x*sqW)+(sqW-1), dY+(y*sqH)+(sqH-1));
                
                    // If the Scene represents an objective:
                    if (drawnScene.isObjectified())
                    {
                        g.drawImage(getLoadedImage("interface/map/map_objective.png"), dX+(x*sqW), dY+(y*sqH), null);
                    }

                    // If the Scene is the current one:
                    if (marked == drawnScene)
                    {                            
                        g.drawImage(getLoadedImage("interface/map/map_current.png"), dX+(x*sqW), dY+(y*sqH), null);
                    }
                
                }
            }
        }
        
        try {
            ImageIO.write(mapImage, "PNG", f);
        } catch (IOException ex) {
            Logger.getLogger(Map.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }        
    
}

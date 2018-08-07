/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gff.maps;

import gff.Global;
import gff.GoodFight;
import static gff.GoodFight.Dir;
import gff.Scene;
import gff.graphics.BackgroundLayer;
import gff.graphics.SpriteLayer;
import gff.items.Book;
import gff.objects.scenery.Pillar;
import gff.objects.scenery.SceneryConstruct;
import gff.objects.scenery.Scenery;
import gff.objects.scenery.buildings.StrongholdBuilding;
import java.util.ArrayList;

/**
 *
 * @author Ray
 */
public abstract class StrongholdMap extends Map{
    
    public static final int EXTRA_WALLS_TO_REMOVE = 350; // Number of attempts to remove a random wall after generating the maze.    

    private StrongholdBuilding building = null;
    protected Scene entranceScene = null;
    protected ArrayList<Scene> myPremiumChestScenes = new ArrayList<>();
    protected ArrayList<ArrayList<Scene>> sections = new ArrayList<>();

    public StrongholdMap(String name, StrongholdBuilding building, int width, int height, int floors, int mainFloor) {
        super(name, width, height, floors, mainFloor);      
        this.building = building;
        
        generateStronghold();        

        addLayersToScenes();

//        addFeaturesWithNoIntersectZones();
//        
//        addFeaturesWithNoIntrusionZones();
        
        createScenesForTerrains();
                
        //clearAllZones();
    }

    public Scene getEntranceScene()
    {
        return this.entranceScene;
    }
    
    private void clearAllZones()
    {
        for (int x = 0; x < getWidth(); x++)
        {
            for (int y = 0; y < getHeight(); y++)
            {
                getScene(x, y, 0).clearZones();
            }
        }   
    }
    
    private void addFeaturesWithNoIntersectZones()
    {        
        for (int b = 0; b < 7; b++)
        {
            Scene scene;
            do {
                scene = getRandomScene();
            } while (scene == null || scene.isInteresting());        
            Book book = Global.withdrawRandomBook();
            if (book != null)
            {
                scene.addPermaChest(book);
                myPremiumChestScenes.add(scene);
            }
        }
    }
    
    private void addFeaturesWithNoIntrusionZones()
    {
        
    }
   
    private void makeMaze()
    {
        // Put 4 walls in each Scene (required for the maze algorithm:
        for (int x = 0; x < getWidth(); x++)
        {
            for (int y = 0; y < getHeight(); y++)
            {
                setWallAt(x, y, 0, Dir.N, true);
                setWallAt(x, y, 0, Dir.E, true);
                setWallAt(x, y, 0, Dir.S, true);
                setWallAt(x, y, 0, Dir.W, true);
            }
        }        
        
        // Recursive maze algorithm:
        recurseMaze(getRandomSceneByFloor(0));        
        
        // Re-hide all the scenes (they were "discovered" in the maze algorithm):
        for (int x = 0; x < getWidth(); x++)
        {
            for (int y = 0; y < getHeight(); y++)
            {
                getScene(x, y, 0).hide();
            }
        }        
        
        // Remove some walls to make it more "open", and looking less like a structured maze:
        for (int n = 0; n < EXTRA_WALLS_TO_REMOVE; n++)
        {
            int d = Global.getRandomInt(1, 4);
            switch (d)
            {
                case 1:
                    setWallAt(getRandomSceneByFloor(0), Dir.N, false);
                    break;
                case 2:
                    setWallAt(getRandomSceneByFloor(0), Dir.E, false);
                    break;
                case 3:
                    setWallAt(getRandomSceneByFloor(0), Dir.S, false);
                    break;
                case 4:
                    setWallAt(getRandomSceneByFloor(0), Dir.W, false);
                    break;                
            }
        }
        
        // Make a complete border of walls:
        for (int x = 0; x < getWidth(); x++)
        {            
            for (int y = 0; y < getHeight(); y++)
            {
                if (x == 0)
                    setWallAt(getScene(x, y, 0), Dir.W, true);
                if (y == 0)
                    setWallAt(getScene(x, y, 0), Dir.N, true);
                if (y == getHeight()-1)
                    setWallAt(getScene(x, y, 0), Dir.S, true);
                if (x == getWidth()-1)
                    setWallAt(getScene(x, y, 0), Dir.E, true);
            }            
        }
        
        // Get some random "empty" walls and replace them with partial walls:
        int numPartials = Global.getRandomInt(150, 400);
        for (int s = 0; s < numPartials; s++)
        {
            Scene scene = getRandomSceneByFloor(0);
            Dir d = Global.randomCardDir();
            boolean sections[] = null;
            if (!scene.isWall(d))
            {
                switch(d)
                {
                    case N:
                    case S:
                        sections = getRandomHorzWallSections();
                        break;
                    case E:
                    case W:
                        sections = getRandomVertWallSections();
                        break;
                }
                scene.setWallSections(d, sections);
                Scene neighbor = scene.getNeighbor(d);
                if (neighbor != null)
                {
                    neighbor.setWallSections(Global.oppositeDir(d), sections);
                }
            }
        }
        fixCorners();
    }        
    
    private void recurseMaze(Scene scene)
    {
        scene.discover();
        Scene neighbor;
        do 
        {
            neighbor = scene.connectToRandomUndiscoveredNeighbor();
            if (neighbor != null)
                recurseMaze(neighbor);
        } while (neighbor != null);
    }

    private void fixCorners()
    {
        for (int x = 0; x < getWidth(); x++)
        {
            for (int y = 0; y < getHeight(); y++)
            {
                Scene scene = getScene(x, y, 0);
                // NW Corner:
                if (scene.getWallSection(Dir.N)[0] || scene.getWallSection(Dir.W)[0])
                {
                    scene.setCornerWallSection(Dir.NW, true);                    
                }
                // NE Corner:
                if (scene.getWallSection(Dir.N)[11] || scene.getWallSection(Dir.E)[0])
                {
                    scene.setCornerWallSection(Dir.NE, true);
                }
                // SW Corner:
                if (scene.getWallSection(Dir.S)[0] || scene.getWallSection(Dir.W)[9])
                {
                    scene.setCornerWallSection(Dir.SW, true);
                }
                // SE Corner:
                if (scene.getWallSection(Dir.S)[11] || scene.getWallSection(Dir.E)[9])
                {
                    scene.setCornerWallSection(Dir.SE, true);
                }
            }
        }     
    }
    
    protected abstract void generateStronghold();
    
    private void addLayersToScenes()
    {
        for (int mX = 0; mX < getWidth(); mX++)
        {
            for (int mY = 0; mY < getHeight(); mY++)
            {
                Scene scene = myScenes[mX][mY][0];
                if (scene != null)
                {
                    String bgImageFilename = null;

                    // Background image layer:
                    switch(scene.getTerrain())
                    {                    
                        case STRONGHOLD:   
                            bgImageFilename = "bg/stronghold_bg.png";
                            break;                    
                    }
                    BackgroundLayer bgLayer = new BackgroundLayer(GoodFight.getLoadedImage(bgImageFilename));
                    scene.addLayer(bgLayer);

                    // Ground sprite layer (sprites under our feet):
                    SpriteLayer groundLayer = new SpriteLayer(SpriteLayer.TYPE.GROUND);
                    scene.addLayer(groundLayer);

                    // Action layer (shared with characters):
                    SpriteLayer spLayer = new SpriteLayer(SpriteLayer.TYPE.ACTION);
                    scene.addLayer(spLayer);

                    // Sky sprite layer (sprites over our heads):
                    SpriteLayer skyLayer = new SpriteLayer(SpriteLayer.TYPE.SKY);
                    scene.addLayer(skyLayer);                                                                
                }
            }
        }
    }
    
    private void createScenesForTerrains()
    {
        for (int x = 0; x < getWidth(); x++)
        {
            for (int y = 0; y < getHeight(); y++)
            {
                Scene scene = getScene(x, y, 0);
                if (scene != null)
                    createSceneForTerrain(scene);
            }
        }
    }
    
    private void createSceneForTerrain(Scene scene)
    {
        addWallScenery(scene);
        
        switch(scene.getTerrain())
        {
            case STRONGHOLD:
                makeStrongholdScene(scene);
                break;            
        }
    }
    
    private void makeStrongholdScene(Scene scene)
    {
        SceneryConstruct construct = new SceneryConstruct(){
            @Override
            public Scenery getNewSceneryObject() {
                return new Pillar(0,0);
            }
        };
        
        generateAsymmetricalSceneryPattern(scene, 5, construct);
    }    
    
}

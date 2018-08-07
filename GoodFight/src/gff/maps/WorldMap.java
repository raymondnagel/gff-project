/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gff.maps;

import gff.Global;
import gff.GoodFight;
import static gff.GoodFight.Dir;
import gff.GoodFight.Terrain;
import gff.Scene;
import static gff.GoodFight.Terrain.DESERT;
import static gff.GoodFight.Terrain.FOREST;
import static gff.GoodFight.Terrain.MOUNTAIN;
import static gff.GoodFight.Terrain.PLAIN;
import static gff.GoodFight.Terrain.SWAMP;
import static gff.GoodFight.Terrain.TUNDRA;
import gff.graphics.BackgroundLayer;
import gff.graphics.SpriteLayer;
import gff.items.Book;
import gff.items.Commandment;
import gff.objects.scenery.SceneryConstruct;
import gff.objects.scenery.buildings.Building;
import gff.objects.scenery.Cactus1;
import gff.objects.scenery.Cactus2;
import gff.objects.scenery.Cactus3;
import gff.objects.scenery.Cactus4;
import gff.objects.scenery.buildings.ChurchBuilding;
import gff.objects.scenery.Lake1;
import gff.objects.scenery.Lake2;
import gff.objects.scenery.Lake3;
import gff.objects.scenery.Lake4;
import gff.objects.scenery.PalmTree;
import gff.objects.scenery.PineTree;
import gff.objects.scenery.Scenery;
import gff.objects.scenery.buildings.StrongholdBuilding;
import gff.objects.scenery.Tree;
import gff.objects.scenery.buildings.CastleBuilding;
import gff.objects.scenery.buildings.DungeonBuilding;
import gff.objects.scenery.buildings.FortressBuilding;
import gff.objects.scenery.buildings.KeepBuilding;
import gff.objects.scenery.buildings.TowerBuilding;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Ray
 */
public class WorldMap extends Map{
    public static final int BUILDING_PERIMETER_WIDTH = 20;
    public static final int EXTRA_WALLS_TO_REMOVE = 350; // Number of attempts to remove a random wall after generating the maze.    
    public static final int MIN_AREA_SIZE = 15; // Minimum number of scenes required per area.
    public static final double SPREAD_DIEOFF = 20.0; // Additional chance for the spread to terminate after spreading.
    public static final int AREA_CTR_MARGIN = 2; // Minimum distance of an area center from the map edge.
    public static final int MIN_CTR_DISTANCE = 5; // Minimum distance between area centers.
    
    private ArrayList<Scene> myTundraScenes = new ArrayList<>();
    private ArrayList<Scene> mySwampScenes = new ArrayList<>();
    private ArrayList<Scene> myDesertScenes = new ArrayList<>();
    private ArrayList<Scene> myForestScenes = new ArrayList<>();
    private ArrayList<Scene> myMountainScenes = new ArrayList<>();
    
    private ArrayList<Scene> myChurchScenes = new ArrayList<>();
    private ArrayList<Scene> myStrongholdScenes = new ArrayList<>();
    private ArrayList<Scene> myPremiumChestScenes = new ArrayList<>();
    
    private ArrayList<Point> myAreaCenters = new ArrayList<>();

    public WorldMap() {
        super("Land of Allegoria", 16, 16, 1, 0);      
        
        determineTerrains();
        
        makeMaze();

        addLayersToScenes();

        addFeaturesWithNoIntersectZones();
        
        addFeaturesWithNoIntrusionZones();
        
        createScenesForTerrains();
                
        //clearAllZones();
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
        makeStrongholds();
        makeChurches();
        
        for (int b = 0; b < 30; b++)
        {
            Scene scene;
            do {
                scene = getRandomSceneByFloor(0);
            } while (scene.isInteresting());        
            Book book = Global.withdrawRandomBook();
            if (book != null)
            {
                scene.addPermaChest(book);
                myPremiumChestScenes.add(scene);
            }
        }
        for (int c = 0; c < 10; c++)
        {
            Scene scene;
            do {
                scene = getRandomSceneByFloor(0);
            } while (scene.isInteresting());            
            Commandment com = Global.withdrawRandomCommandment();
            if (com != null)
            {
                scene.addPermaChest(com);
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
    
    private void determineTerrains() {
        
        // Fill the entire map with the default terrain (PLAIN)
        for (int mX = 0; mX < getWidth(); mX++)
        {
            for (int mY = 0; mY < getHeight(); mY++)
            {
                setScene(mX, mY, 0, new Scene(this, mX, mY, 0));
                myScenes[mX][mY][0].setTerrain(Terrain.PLAIN);
            }
        }
        
        // Make each themed area:
        
        // Do these first, because they are geographically limited:
        makeTundraArea();
        makeDesertArea();
        makeSwampArea();
        
        // It doesn't matter so much where these go:
        makeMountainArea();
        makeForestArea();        
    }

    private void makeStrongholds()
    {        
        // Forest stronghold:
        //placeBuildingInArea(new CastleBuilding(), myForestScenes);
        // Desert stronghold:
        //placeBuildingInArea(new FortressBuilding(), myDesertScenes);
        // Mountain stronghold:
        //placeBuildingInArea(new TowerBuilding(), myMountainScenes);
        // Swamp stronghold:
        //placeBuildingInArea(new DungeonBuilding(), mySwampScenes);
        // Tundra stronghold:
        placeBuildingInArea(new KeepBuilding(), myTundraScenes);
    }
    
    private void makeChurches()
    {        
        // First, we make sure there is at least 1 church in each area:
        
        // Forest church:
        placeBuildingInArea(new ChurchBuilding(), myForestScenes);
        // Desert church:
        placeBuildingInArea(new ChurchBuilding(), myDesertScenes);
        // Mountain church:
        placeBuildingInArea(new ChurchBuilding(), myMountainScenes);
        // Swamp church:
        placeBuildingInArea(new ChurchBuilding(), mySwampScenes);
        // Tundra church:
        placeBuildingInArea(new ChurchBuilding(), myTundraScenes);
        
        // Second, we make 5 additional random churches:
        for (int c = 0; c < 5; c++)
        {
            Scene scene;
            boolean bad;       
            do
            {
                scene = getRandomSceneByFloor(0);
                bad = isNearBuilding(scene);                    
            } while (bad);
            ChurchBuilding church = new ChurchBuilding();
            boolean placed = scene.placeRandomlyIfPossible(church, scene.getSpriteLayer(SpriteLayer.TYPE.ACTION), 100, false, false);
            if (placed)
            {
                scene.addNoIntersectZone(new Rectangle(church.getLeft()-BUILDING_PERIMETER_WIDTH, church.getTop()-BUILDING_PERIMETER_WIDTH, church.getWidth()+(BUILDING_PERIMETER_WIDTH*2), church.getHeight()+(BUILDING_PERIMETER_WIDTH*2)));
                scene.setBuilding(church);
                myChurchScenes.add(scene);          
                Global.createChurch(scene, church);
            }
        }
    }
    
    private void placeBuildingInArea(Building building, List areaSceneList)
    {
        Scene scene;
        boolean bad;       
        do
        {
            scene = (Scene)Global.getRandomFromList(areaSceneList);
            bad = isNearBuilding(scene);                    
        } while (bad);
        boolean placed = scene.placeRandomlyIfPossible(building, scene.getSpriteLayer(SpriteLayer.TYPE.ACTION), 100, false, false);
        if (placed)
        {
            scene.addNoIntersectZone(new Rectangle(building.getLeft()-BUILDING_PERIMETER_WIDTH, building.getTop()-BUILDING_PERIMETER_WIDTH, building.getWidth()+(BUILDING_PERIMETER_WIDTH*2), building.getHeight()+(BUILDING_PERIMETER_WIDTH*2)));
            scene.setBuilding(building);
            if (building instanceof ChurchBuilding)
            {
                myChurchScenes.add(scene);
                Global.createChurch(scene, (ChurchBuilding)building);
            }
            else if (building instanceof StrongholdBuilding)
            {
                myStrongholdScenes.add(scene);
                Global.createStronghold(scene, (StrongholdBuilding)building);
            }
        }
    }
    
    public boolean isNearBuilding(Scene scene)
    {
        if (scene.getBuilding() != null)
            return true;
        Scene neighbor = scene.getNeighbor(GoodFight.Dir.N);
        if (neighbor != null && neighbor.getBuilding() != null)
            return true;
        neighbor = scene.getNeighbor(GoodFight.Dir.NE);
        if (neighbor != null && neighbor.getBuilding() != null)
            return true;
        neighbor = scene.getNeighbor(GoodFight.Dir.E);
        if (neighbor != null && neighbor.getBuilding() != null)
            return true;
        neighbor = scene.getNeighbor(GoodFight.Dir.SE);
        if (neighbor != null && neighbor.getBuilding() != null)
            return true;
        neighbor = scene.getNeighbor(GoodFight.Dir.S);
        if (neighbor != null && neighbor.getBuilding() != null)
            return true;
        neighbor = scene.getNeighbor(GoodFight.Dir.SW);
        if (neighbor != null && neighbor.getBuilding() != null)
            return true;
        neighbor = scene.getNeighbor(GoodFight.Dir.W);
        if (neighbor != null && neighbor.getBuilding() != null)
            return true;
        neighbor = scene.getNeighbor(GoodFight.Dir.NW);
        if (neighbor != null && neighbor.getBuilding() != null)
            return true;
        return false;
    }
    
    private void addLayersToScenes()
    {
        for (int mX = 0; mX < getWidth(); mX++)
        {
            for (int mY = 0; mY < getHeight(); mY++)
            {
                Scene scene = myScenes[mX][mY][0];
                String bgImageFilename = null;
                
                // Background image layer:
                switch(scene.getTerrain())
                {
                    case PLAIN:
                    case FOREST:   
                        bgImageFilename = "bg/grass_bg.png";
                        break;
                    case MOUNTAIN:
                        bgImageFilename = "bg/rocky_bg.png";
                        break;
                    case DESERT:
                        bgImageFilename = "bg/desert_bg.png";
                        break;
                    case SWAMP:
                        bgImageFilename = "bg/swamp_bg.png";
                        break;
                    case TUNDRA:
                        bgImageFilename = "bg/tundra_bg.png";
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
    
    private void createScenesForTerrains()
    {
        for (int x = 0; x < getWidth(); x++)
        {
            for (int y = 0; y < getHeight(); y++)
            {
                createSceneForTerrain(getScene(x, y, 0));
            }
        }
    }
    
    private void createSceneForTerrain(Scene scene)
    {
        addWallScenery(scene);
        
        switch(scene.getTerrain())
        {
            case PLAIN:
                makePlainScene(scene);
                break;
            case FOREST:   
                makeForestScene(scene);
                break;
            case MOUNTAIN:
                makeMountainScene(scene);
                break;
            case DESERT:
                makeDesertScene(scene);
                break;
            case SWAMP:
                makeSwampScene(scene);
                break;
            case TUNDRA:
                makeTundraScene(scene);
                break;
        }
    }
    
    private void makePlainScene(Scene scene)
    {
        SceneryConstruct construct = new SceneryConstruct(){
            @Override
            public Scenery getNewSceneryObject() {
                return new Tree(0,0);
            }
        };
        
        generateAsymmetricalSceneryPattern(scene, 5, construct);
    }
    private void makeForestScene(Scene scene)
    {
        SceneryConstruct construct = new SceneryConstruct(){
            @Override
            public Scenery getNewSceneryObject() {
                return new Tree(0,0);
            }
        };
                
//        if (scene.isWall(Dir.N))
//            generateWallSceneryPattern(scene, Dir.N, construct);
//        if (scene.isWall(Dir.E))
//            generateWallSceneryPattern(scene, Dir.E, construct);
//        if (scene.isWall(Dir.S))
//            generateWallSceneryPattern(scene, Dir.S, construct);
//        if (scene.isWall(Dir.W))
//            generateWallSceneryPattern(scene, Dir.W, construct);
                
        generateAsymmetricalSceneryPattern(scene, 15, construct);
    }
    private void makeTundraScene(Scene scene)
    {
        SpriteLayer spLayer = scene.getSpriteLayer(SpriteLayer.TYPE.ACTION);
        for (int t = 0; t < 15; t++)
        {
            PineTree tree = new PineTree(0,0);
            scene.placeRandomlyIfPossible(tree, spLayer, TRIFLE_TRIES, false, true);
        }
    }
    private void makeDesertScene(Scene scene)
    {        
        SpriteLayer spLayer = scene.getSpriteLayer(SpriteLayer.TYPE.ACTION);
     
        // Determine the type of desert scene:
        int type = Global.getRandomInt(1, 10);
        
        if (type == 6)
        {
            // Has lake(s)
            int n = Global.getRandomInt(1, 2);
            for (int t = 0; t < n; t++)
            {
                Scenery lake;
                int lT = Global.getRandomInt(1, 4);
                switch (lT)
                {
                    case 1:
                        lake = new Lake1(0,0);
                        break;
                    case 2:
                        lake = new Lake2(0,0);
                        break;
                    case 3:
                        lake = new Lake3(0,0);
                        break;
                    default:
                        lake = new Lake4(0,0);
                        break;                    
                }
                scene.placeRandomlyIfPossible(lake, spLayer, TRIFLE_TRIES, false, false);
            }
        }
        if (type >= 1)
        {
            // Has Cacti
            int n = Global.getRandomInt(1, 10);
            for (int t = 0; t < n; t++)
            {
                Scenery cactus;
                int cT = Global.getRandomInt(1, 4);
                switch (cT)
                {
                    case 1:
                        cactus = new Cactus1(0,0);
                        break;
                    case 2:
                        cactus = new Cactus2(0,0);
                        break;
                    case 3:
                        cactus = new Cactus3(0,0);
                        break;
                    default:
                        cactus = new Cactus4(0,0);
                        break;                    
                }
                scene.placeRandomlyIfPossible(cactus, spLayer, TRIFLE_TRIES, false, true);
            }
        }
        if (type == 3)
        {
            // Has Pine Trees
            int n = Global.getRandomInt(1, 10);
            for (int t = 0; t < n; t++)
            {
                PineTree tree = new PineTree(0,0);
                scene.placeRandomlyIfPossible(tree, spLayer, TRIFLE_TRIES, false, true);
            }
        }
        if (type >= 4 && type <= 6)
        {
            // Has Palm Trees
            int n = Global.getRandomInt(1, 10);
            for (int t = 0; t < n; t++)
            {
                PalmTree tree = new PalmTree(0,0);
                scene.placeRandomlyIfPossible(tree, spLayer, TRIFLE_TRIES, false, true);
            }
        }
    }
    private void makeSwampScene(Scene scene)
    {
        SpriteLayer spLayer = scene.getSpriteLayer(SpriteLayer.TYPE.ACTION);
        for (int t = 0; t < 10; t++)
        {
            Tree tree = new Tree(0,0);
            scene.placeRandomlyIfPossible(tree, spLayer, TRIFLE_TRIES, false, true);
        }
        for (int t = 0; t < 10; t++)
        {
            PineTree tree = new PineTree(0,0);
            scene.placeRandomlyIfPossible(tree, spLayer, TRIFLE_TRIES, false, true);
        }
    }
    private void makeMountainScene(Scene scene)
    {
        SpriteLayer spLayer = scene.getSpriteLayer(SpriteLayer.TYPE.ACTION);
        for (int t = 0; t < 30; t++)
        {
            PineTree tree = new PineTree(0,0);
            scene.placeRandomlyIfPossible(tree, spLayer, TRIFLE_TRIES, false, true);
        }
    }    
    
    private void makeForestArea()
    {
        // Get a center Point for this area:
        Point ctrPt = null;
        do
        {
            int ctrX = Global.getRandomInt(AREA_CTR_MARGIN, getWidth()-AREA_CTR_MARGIN);
            int ctrY = Global.getRandomInt(AREA_CTR_MARGIN, getHeight()-AREA_CTR_MARGIN);
            ctrPt = new Point(ctrX, ctrY);
        } while (isTooClose(ctrPt));
        
        do
        {          
            // Clear any previous attempt by converting them back to PLAINS:
            clearArea(myForestScenes);
            // Spread the Forest recursively from the center Point:        
            spread(ctrPt, ctrPt.x, ctrPt.y, Terrain.FOREST, myForestScenes);
        } while (myForestScenes.size() < MIN_AREA_SIZE);
        System.out.println(myForestScenes.size() + " forests.");
        
        // Add this area center to the list for future use:
        myAreaCenters.add(ctrPt);
    }
    private void makeTundraArea()
    {
        // Get a center Point for this area:
        Point ctrPt = null;
        do
        {
            int ctrX = Global.getRandomInt(AREA_CTR_MARGIN, getWidth()-AREA_CTR_MARGIN);
            // Way up North...
            int ctrY = Global.getRandomInt(AREA_CTR_MARGIN, AREA_CTR_MARGIN+1);
            ctrPt = new Point(ctrX, ctrY);
        } while (isTooClose(ctrPt));
        
        do
        {            
            // Clear any previous attempt by converting them back to PLAINS:
            clearArea(myTundraScenes);
            // Spread the Tundra recursively from the center Point:        
            spread(ctrPt, ctrPt.x, ctrPt.y, Terrain.TUNDRA, myTundraScenes);
        } while (myTundraScenes.size() < MIN_AREA_SIZE);
        System.out.println(myTundraScenes.size() + " tundras.");
        
        // Add this area center to the list for future use:
        myAreaCenters.add(ctrPt);
    }
    private void makeDesertArea()
    {
        // Get a center Point for this area:
        Point ctrPt = null;
        do
        {
            int ctrX = Global.getRandomInt(AREA_CTR_MARGIN, getWidth()-AREA_CTR_MARGIN);
            // Headin' South...
            int ctrY = Global.getRandomInt((getHeight()-AREA_CTR_MARGIN)-2, getHeight()-AREA_CTR_MARGIN);
            ctrPt = new Point(ctrX, ctrY);
        } while (isTooClose(ctrPt));
        
        do
        {            
            // Clear any previous attempt by converting them back to PLAINS:
            clearArea(myDesertScenes);
            // Spread the Desert recursively from the center Point:        
            spread(ctrPt, ctrPt.x, ctrPt.y, Terrain.DESERT, myDesertScenes);
        } while (myDesertScenes.size() < MIN_AREA_SIZE);
        System.out.println(myDesertScenes.size() + " deserts.");
        
        // Add this area center to the list for future use:
        myAreaCenters.add(ctrPt);
    }
    private void makeSwampArea()
    {
        // Get a center Point for this area:
        Point ctrPt = null;
        do
        {
            int ctrX = Global.getRandomInt(AREA_CTR_MARGIN, getWidth()-AREA_CTR_MARGIN);
            // Think Everglades...
            int ctrY = Global.getRandomInt((getHeight()-AREA_CTR_MARGIN)-5, getHeight()-AREA_CTR_MARGIN);
            ctrPt = new Point(ctrX, ctrY);
        } while (isTooClose(ctrPt));
        
        do
        {    
            // Clear any previous attempt by converting them back to PLAINS:
            clearArea(mySwampScenes);
            // Spread the Swamp recursively from the center Point:        
            spread(ctrPt, ctrPt.x, ctrPt.y, Terrain.SWAMP, mySwampScenes);
        } while (mySwampScenes.size() < MIN_AREA_SIZE);
        System.out.println(mySwampScenes.size() + " swamps.");
        
        // Add this area center to the list for future use:
        myAreaCenters.add(ctrPt);
    }
    private void makeMountainArea()
    {
        // Get a center Point for this area:
        Point ctrPt = null;
        do
        {
            int ctrX = Global.getRandomInt(AREA_CTR_MARGIN, getWidth()-AREA_CTR_MARGIN);
            int ctrY = Global.getRandomInt(AREA_CTR_MARGIN, getHeight()-AREA_CTR_MARGIN);
            ctrPt = new Point(ctrX, ctrY);
        } while (isTooClose(ctrPt));
        
        do
        {           
            // Clear any previous attempt by converting them back to PLAINS:
            clearArea(myMountainScenes);
            // Spread the Mountains recursively from the center Point:        
            spread(ctrPt, ctrPt.x, ctrPt.y, Terrain.MOUNTAIN, myMountainScenes);
        } while (myMountainScenes.size() < MIN_AREA_SIZE);
        System.out.println(myMountainScenes.size() + " mountains.");
        
        // Add this area center to the list for future use:
        myAreaCenters.add(ctrPt);
    }

    
    public ArrayList<Scene> getChurchScenes()
    {
        return myChurchScenes;
    }
    
    public ArrayList<Scene> getStrongholdScenes()
    {
        return myStrongholdScenes;
    }
    
    public ArrayList<Scene> getPremiumChestScenes()
    {
        return myPremiumChestScenes;
    }
    
    private boolean isTooClose(Point ctrPt)
    {
        for (int c = 0; c < myAreaCenters.size(); c++)
        {
            if (Global.getDistance(ctrPt, myAreaCenters.get(c)) < MIN_CTR_DISTANCE)
            {
                return true;
            }
        }
        return false;
    }
    
    private void clearArea(List<Scene> sceneList)
    {
        for (int s = 0; s < sceneList.size(); s++)
            sceneList.get(s).setTerrain(Terrain.PLAIN);
        sceneList.clear();
    }
    
    private void spread(Point ctrPt, int x, int y, Terrain terr, List<Scene> scenes)
    {      
        if (x < 0 || x >= getWidth() || y < 0 || y >= getHeight())
        {
            return;
        }        
        Scene scene = getScene(x, y, 0);
        double distance = Global.getDistance(ctrPt, new Point(x, y));
        double chance = 100.0 - (distance * SPREAD_DIEOFF);
        if (scene.getTerrain() != terr && chance > 0)
        {
            boolean spread = Global.oddsCheck((int)chance, 100);
            if (spread)
            {           
                scenes.add(scene);
                Global.log("Spreading " + terr.toString().toLowerCase() + " to: " + x + ", " + y);
                scene.setTerrain(terr);
                spread(ctrPt, x, y-1, terr, scenes); // N
                spread(ctrPt, x+1, y, terr, scenes); // E
                spread(ctrPt, x, y+1, terr, scenes); // S
                spread(ctrPt, x-1, y, terr, scenes); // W
            }
        }
    }
    
}

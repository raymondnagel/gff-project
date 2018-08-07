/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gff;

import gff.maps.ChurchMap;
import gff.graphics.SpriteLayer;
import gff.objects.Person;
import gff.objects.Sprite;
import gff.objects.scenery.buildings.ChurchBuilding;
import gff.objects.scenery.Portal;
import java.awt.Point;
import java.util.ArrayList;

/**
 *
 * @author Raymond
 */
public class Church {
    public static final int ReservedSeatNum = 28;
    
    private static Point[] ourSeatPoints = new Point[32];
    
    private ChurchBuilding building = null;
    private String name = null;
    private Scene scene = null;
    private ArrayList<Person> members = new ArrayList<>();
    private ArrayList<Person> neighbors = new ArrayList<>();
    private boolean[] seats = new boolean[32];
    private int inService = 0;

    static
    {       
        int p = 0;
        for (int y = 0; y < 4; y++)
        {
            for (int s = 0; s < 2; s++)
                for (int x = 0; x < 4; x++)
                {
                    ourSeatPoints[p] = new Point(356+(s*187)+(x*34), 418+(y*39));
                    p++;
                }
        }
    }
    
    public void toggleService()
    {
        inService++;
        if (inService >= 3)
        {
            inService = 0;
        }
    }
    
    public boolean isInService()
    {
        return inService == 0;
    }
    
    public String getName()
    {
        return this.name;
    }
    
    public ChurchBuilding getBuilding()
    {
        return this.building;
    }
    
    public final ChurchMap getMap()
    {
        return (ChurchMap)building.getMap();
    }
    
    public Scene getScene()
    {
        return this.scene;
    }
    
    public ArrayList<Person> getMembers()
    {
        return this.members;
    }
    public ArrayList<Person> getNeighbors()
    {
        return this.neighbors;
    }
    
    public Person getPreacher()
    {
        for (int p = 0; p < members.size(); p++)
        {
            members.get(p).setScriptLabel(null);
        }
        
        int index = Global.getRandomInt(0, members.size()-1);
        while (members.get(index).getSex() != GoodFight.Sex.MALE)
        {
            index++;
            if (index >= members.size())
            {
                index = 0;
            }
        }
        Person preacher = members.get(index);
        preacher.setScriptLabel("Preacher");
        preacher.setCenterPoint(new Point(GoodFight.SCENE_W/2, GoodFight.SCENE_TOP + 260));
        preacher.faceDirection(GoodFight.Dir.S);
        preacher.setMode(Person.PersonMode.STILL);
        return preacher;
    }
    
    public Person getRandomMember()
    {
        return (Person)Global.getRandomFromList(members);
    }
    
    public Person getRandomNeighbor()
    {
        return (Person)Global.getRandomFromList(neighbors);
    }
    
    public void becomeMember(Person neighbor)
    {
        this.neighbors.remove(neighbor);
        this.members.add(neighbor);
    }
    
    public Church(ChurchBuilding building, Scene scene) {  
        final Church self = this;
        this.name = Global.withdrawRandomChurchName();
        this.building = building;
        this.building.getMap().setName(this.name);
        this.building.setChurch(self);
        this.scene = scene;
        
        Portal outerPortal = new Portal(this.building.getX()+110, this.building.getBottom(), 36, 4, this.scene, new Point(this.building.getX()+110, this.building.getBottom()-20), GoodFight.Dir.N);
        this.scene.getSpriteLayer(SpriteLayer.TYPE.ACTION).addScenery(outerPortal);
        
        Portal innerPortal = new Portal(GoodFight.SCENE_LEFT+488, GoodFight.SCENE_TOP+576, 49, 7, getMap().getScene(0, 0, 0), new Point(GoodFight.SCENE_LEFT+500, GoodFight.SCENE_TOP+530), GoodFight.Dir.S);
        getMap().getScene(0, 0, 0).getSpriteLayer(SpriteLayer.TYPE.ACTION).addScenery(innerPortal);
        
        this.building.setEntrancePortals(innerPortal, outerPortal);
        
        createPeople();
    }
    
    private void createPeople()
    {        
        // Create the first member of the church. This simply ensures that there
        // is at least one male in each church to do the preaching.
        String firstName = (String)Global.getRandomFromList(Global.MaleNames);
        String lastName = (String)Global.getRandomFromList(Global.LastNames);
        Person member = new Person(GoodFight.Sex.MALE, true, firstName, lastName, null, this, 100f, 5f);
        member.setMode(Person.PersonMode.STILL);
        members.add(member);
       
        int savedCount = Global.getRandomInt(5, 9);
        for (int p = 0; p < savedCount; p++)
        {
            member = new Person(GoodFight.Sex.RANDOM, true, this);
            member.setMode(Person.PersonMode.STILL);
            members.add(member);
        }
        
        int lostCount = Global.getRandomInt(6, 10);
        for (int p = 0; p < lostCount; p++)
        {
            member = new Person(GoodFight.Sex.RANDOM, false, this);
            member.setMode(Person.PersonMode.STILL);
            neighbors.add(member);
        }
    }
    
    public void clearSeats()
    {
        ArrayList<Sprite> sprites = GoodFight.getSprites();
        for (int s = 0; s < 32; s++)
        {
            for (int p = sprites.size()-1; p >= 0; p--)
            {
                if (sprites.get(p).getLocation().x == ourSeatPoints[s].x && sprites.get(p).getLocation().y == ourSeatPoints[s].y)
                {
                    GoodFight.removeSprite(sprites.get(p));
                }
            }
            seats[s] = false;
        }
        
    }
    
    public void assignToSeat(Person person, int seatNum)
    {
        person.setLocation(ourSeatPoints[seatNum]);
        seats[seatNum] = true;
        person.faceDirection(GoodFight.Dir.N);
        person.setMode(Person.PersonMode.STILL); 
    }
    
    public void prepareForService()
    {
        Person preacher = getPreacher();        
        
        int s = 0;        
        for (int p = 0; p < members.size(); p++)
        {            
            GoodFight.addSprite(members.get(p));
            if (preacher != members.get(p))
            {
                int skip = Global.getRandomInt(1, 7);
                s+=skip;
                if (s >= 32)
                    s -= 32;
                while (seats[s] || s == ReservedSeatNum)
                {
                    s++;
                    if (s >= 32)
                    s -= 32;                
                }
                assignToSeat(members.get(p), s);
            }
        }
    }
    
    public void closeService()
    {
        for (int p = 0; p < members.size(); p++)
        {
            members.get(p).setScriptLabel(null);
            GoodFight.removeSprite(members.get(p));
        }
        clearSeats();
    }
    
}

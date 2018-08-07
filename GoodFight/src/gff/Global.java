/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gff;

import gff.GoodFight.Dir;
import static gff.GoodFight.getFontFile;
import gff.bible.BibleReference;
import gff.items.Book;
import gff.items.Commandment;
import gff.items.Fruit;
import gff.objects.Enemy;
import gff.objects.Imp;
import gff.objects.Person;
import gff.objects.scenery.buildings.ChurchBuilding;
import gff.objects.scenery.buildings.StrongholdBuilding;
import gff.sound.old.MusicManager;
import gff.util.ReadWriteTextFile;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author rnagel
 */
public class Global
{        
    public static final Dir[] CardinalDirections = Dir.getCardinalDirections();
    
    public static boolean TestMode = false;
    public static boolean ShowNameTags = false;
    public static final boolean LOG = true;
    
    public static final Font PrintFont = getFontFile("print.ttf").deriveFont(18f);
    public static final Font GreekFont = getFontFile("adonais.ttf").deriveFont(18f);    
    public static final Font FancyFont = getFontFile("olde.ttf").deriveFont(18f);
    public static final Font SimpleFont = new Font("Arial", Font.PLAIN, 18);
    
    public static final Font SimpleSmFont = SimpleFont.deriveFont(12f);    
    public static final Font SimpleTinyFont = SimpleFont.deriveFont(9.5f);
    public static final Font GreekMedFont = GreekFont.deriveFont(Font.PLAIN, 18f);        
    
    public static ArrayList<Book> BookList = new ArrayList<>();
    public static ArrayList<Commandment> CommandmentList = new ArrayList<>();
    public static ArrayList<Fruit> FruitList = new ArrayList<>();
    public static ArrayList<Church> ChurchList = new ArrayList<>();
    public static ArrayList<Stronghold> StrongholdList = new ArrayList<>();
    public static ArrayList<Enemy> EnemyList = new ArrayList<>();
    
    public static ArrayList<String> Amens = new ArrayList<>();
    public static ArrayList<String> Sermons = new ArrayList<>();
    public static ArrayList<String> ChurchNames = new ArrayList<>();
    public static ArrayList<String> MaleNames = new ArrayList<>();
    public static ArrayList<String> FemaleNames = new ArrayList<>();
    public static ArrayList<String> LastNames = new ArrayList<>();
    public static ArrayList<String> DevilNames = new ArrayList<>();
    public static ArrayList<String> SaveTexts = new ArrayList<>();
    public static ArrayList<String> FleeTexts = new ArrayList<>();
    public static ArrayList<String> EnemyTaunts = new ArrayList<>();
    public static ArrayList<String> PlayerRetorts = new ArrayList<>();
    
    private static MusicManager ourMusicManager = new MusicManager();    
    // This is loaded directly from the file because there is only 1 instance,
    // and it gets replaced whenever the commandment count is updated. At no time
    // will all of the radius images need to be in memory simultaneously.
    private static BufferedImage ourVisibilityImage = GoodFight.getImageFromFile("radii/0.png");
    private static int           ourNumCommandments = 0;
    
    private static Random ourRandom = new Random();

    public static void initGlobalSettings()
    {
        initializeBookList();
        initializeCommandmentList();
        initializeFruitList();
        initializeTextLists();
        //initializeEnemyList();
        initializeSermons();
        initializeAmens();
        initializeBanters();
    }
    
    private static void initializeBanters()
    {
        File f = new File("extern/text/retorts.txt");
        PlayerRetorts = getTextListFromFile(f);

        EnemyTaunts.add("En guard, Christian!");
    }
    
    private static void initializeAmens()
    {
        File f = new File("extern/text/amens.txt");
        Amens = getTextListFromFile(f);
    }
    
    private static void initializeSermons()
    {
        String[] sermons = new File("extern/text/sermons").list(new FilenameFilter() {
            @Override
            public boolean accept(File file, String string) {
                return string.toLowerCase().endsWith(".scp");
            }
        });
        for (int s = 0; s < sermons.length; s++)
        {
            Sermons.add(sermons[s].split("\\.")[0]);
        }
    }
    
    private static void initializeCommandmentList()
    {
        CommandmentList.add(new Commandment(1, "Thou shalt have no other gods before me. (Exodus 20:2-3)"));
        CommandmentList.add(new Commandment(2, "Thou shalt not make unto thee any graven image... thou shalt not bow down thyself to them, nor serve them. (Exodus 20:4-5)"));
        CommandmentList.add(new Commandment(3, "Thou shalt not take the name of the Lord thy God in vain. (Exodus 20:7)"));
        CommandmentList.add(new Commandment(4, "Remember the sabbath day, to keep it holy. (Exodus 20:8-10)"));
        CommandmentList.add(new Commandment(5, "Honour thy father and thy mother: that thy days may be long upon the land which the Lord thy God giveth thee. (Exodus 20:12)"));
        CommandmentList.add(new Commandment(6, "Thou shalt not kill. (Exodus 20:13)"));
        CommandmentList.add(new Commandment(7, "Thou shalt not commit adultery. (Exodus 20:14)"));
        CommandmentList.add(new Commandment(8, "Thou shalt not steal. (Exodus 20:15)"));
        CommandmentList.add(new Commandment(9, "Thou shalt not bear false witness against thy neighbour. (Exodus 20:16)"));
        CommandmentList.add(new Commandment(10, "Thou shalt not covet... any thing that is thy neighbour's. (Exodus 20:17)"));        
    }
    
    private static void initializeFruitList()
    {
        FruitList.add(new Fruit("Love", "Thou shalt have no other gods before me. (Exodus 20:2-3)"));
        FruitList.add(new Fruit("Joy", "Thou shalt not make unto thee any graven image... thou shalt not bow down thyself to them, nor serve them. (Exodus 20:4-5)"));
        FruitList.add(new Fruit("Peace", "Thou shalt not take the name of the Lord thy God in vain. (Exodus 20:7)"));
        FruitList.add(new Fruit("Longsuffering", "Remember the sabbath day, to keep it holy. (Exodus 20:8-10)"));
        FruitList.add(new Fruit("Gentleness", "Honour thy father and thy mother: that thy days may be long upon the land which the Lord thy God giveth thee. (Exodus 20:12)"));
        FruitList.add(new Fruit("Goodness", "Thou shalt not kill. (Exodus 20:13)"));
        FruitList.add(new Fruit("Faith", "Thou shalt not commit adultery. (Exodus 20:14)"));
        FruitList.add(new Fruit("Meekness", "Thou shalt not steal. (Exodus 20:15)"));
        FruitList.add(new Fruit("Temperance", "Thou shalt not bear false witness against thy neighbour. (Exodus 20:16)"));
    }
    
    public static void initializeEnemyList()
    {
        for (int e = 0; e < 20; e++)
        {
            addEnemy(new Imp(1));
        }
    }
    
    private static void initializeTextLists()
    {
        ChurchNames = getTextListFromFile(GoodFight.getFile("text/church_names.txt"));
        MaleNames = getTextListFromFile(GoodFight.getFile("text/male_names.txt"));
        FemaleNames = getTextListFromFile(GoodFight.getFile("text/female_names.txt"));
        LastNames = getTextListFromFile(GoodFight.getFile("text/last_names.txt"));
        DevilNames = getTextListFromFile(GoodFight.getFile("text/devil_names.txt"));
        SaveTexts = getTextListFromFile(GoodFight.getFile("text/save_text.txt"));
        FleeTexts = getTextListFromFile(GoodFight.getFile("text/flee_text.txt"));
    }
    
    public static void createChurch(Scene scene, ChurchBuilding building)
    {
        Church church = new Church(building, scene);
        ChurchList.add(church);
    }
    
    public static void createStronghold(Scene scene, StrongholdBuilding building)
    {
        Stronghold stronghold = new Stronghold(building.getMap().getName(), building, scene);
        StrongholdList.add(stronghold);
    }
    
    public static Church getStartingChurch()
    {
        return ChurchList.get(9);
    }
    
    public static Church getNearestChurch(Scene scene)
    {
        Church nearestChurch = null;
        double nearestDist = 1000.0;
        for (int c = 0; c < ChurchList.size(); c++)
        {
            double dist = getDistance(scene.getLocation(), ChurchList.get(c).getScene().getLocation());
            if (dist < nearestDist)
            {
                nearestDist = dist;
                nearestChurch = ChurchList.get(c);
            }
        }
        return nearestChurch;
    }
    
    public static Person getRandomChristian()
    {
        Church randomChurch = (Church)getRandomFromList(ChurchList);
        return randomChurch.getRandomMember();
    }
    
    public static void toggleChurchServices()
    {
        for (int c = 0; c < ChurchList.size(); c++)
        {
            ChurchList.get(c).toggleService();
        }
    }
    
    public static Enemy withdrawRandomEnemy()
    {
        // Enemies must be withdrawn from the Global pool so that we don't use the same enemy twice.
        int i = getRandomInt(0, EnemyList.size()-1);
        return EnemyList.remove(i);
    }
    public static void addEnemy(Enemy enemy)
    {
        EnemyList.add(enemy);
    }
    public static void replaceLowestEnemy(Enemy enemy)
    {
        int index = -1;
        int lowestLevel = 9999;
        for (int e = 0; e < EnemyList.size(); e++)
        {
            if (EnemyList.get(e).getLevel() <= lowestLevel)
            {
                lowestLevel = EnemyList.get(e).getLevel();
                index = e;
            }
        }
        if (index != -1)
        {
            EnemyList.remove(index);
            addEnemy(enemy);
        }
    }
    
    public static String withdrawRandomChurchName()
    {
        int i = getRandomInt(0, ChurchNames.size()-1);
        return ChurchNames.remove(i);
    }
    
    private static void initializeBookList()
    {
        // Populate the list with new Book objects by their names:
        for (int b = 0; b < BibleReference.BookNames.length; b++)
        {
            BookList.add(new Book(BibleReference.BookNames[b]));
        }       
    }
    
    public static Book withdrawBookByName(String bookName)
    {
        for (int b = 0; b < BookList.size(); b++)
        {
            if (BookList.get(b).getName().equalsIgnoreCase(bookName))
            {
                return BookList.remove(b);
            }
        }
        return null;
    }
    
    public static Book withdrawRandomBook()
    {
        if (BookList.isEmpty())
            return null;
        
        int i = getRandomInt(0, BookList.size()-1);
        return BookList.remove(i);
    }
    
    public static Object withdrawRandomFromList(ArrayList list)
    {
        int i = getRandomInt(0, list.size()-1);
        return list.remove(i);
    }
    
    
    public static void setBackgroundMusic(String songName)
    {
        File f = new File("extern/music/" + songName + ".wav");
        ourMusicManager.startMusic(f, true);
    }
    public static void pauseBackgroundMusic(boolean paused)
    {
        ourMusicManager.setPaused(paused);
    }
    public static void fadeBackgroundMusic(float change)
    {
        ourMusicManager.fadeVolume(change);
    }
    public static float getBackgroundMusicVolume()
    {
        return ourMusicManager.getCurrentVolume();
    }
    
    public static Commandment withdrawRandomCommandment()
    {
        return (Commandment)withdrawRandomFromList(CommandmentList);
    }
    public static int getNumCommandments()
    {
        return ourNumCommandments;
    }
    public static void setNumCommandments(int numCommandments)
    {
        ourNumCommandments = numCommandments;
        if (ourNumCommandments >= 10)
            ourVisibilityImage = null;
        else
            ourVisibilityImage = GoodFight.getImageFromFile("radii/" + ourNumCommandments + ".png");
    }
    public static BufferedImage getVisibilityImage()
    {
        return ourVisibilityImage;
    }
    
    public static boolean isDiagonal(Dir dir)
    {
        return dir == Dir.NE || dir == Dir.SE || dir == Dir.SW || dir == Dir.NW;
    }
    
    public static Dir randomDir()
    {
        int dNum = new Random().nextInt(Dir.values().length);
        return Dir.values()[dNum];
    }

    public static Dir randomCardDir()
    {
        int dNum = new Random().nextInt(4);
        return Dir.values()[dNum];
    }

    public static Dir oppositeDir(Dir dir)
    {
        switch (dir)
        {
            case N: return Dir.S;
            case NE: return Dir.SW;
            case E: return Dir.W;
            case SE: return Dir.NW;
            case S: return Dir.N;
            case SW: return Dir.NE;
            case W: return Dir.E;
            case NW: return Dir.SE;
        }
        return null;
    }

    public static Dir cwDir(Dir dir)
    {
        switch (dir)
        {
            case N: return Dir.NE;
            case NE: return Dir.E;
            case E: return Dir.SE;
            case SE: return Dir.S;
            case S: return Dir.SW;
            case SW: return Dir.W;
            case W: return Dir.NW;
            case NW: return Dir.N;
        }
        return null;
    }

    public static Dir ccwDir(Dir dir)
    {
        switch (dir)
        {
            case N: return Dir.NW;
            case NE: return Dir.N;
            case E: return Dir.NE;
            case SE: return Dir.E;
            case S: return Dir.SE;
            case SW: return Dir.S;
            case W: return Dir.SW;
            case NW: return Dir.W;
        }
        return null;
    }

    public static Dir cardCwDir(Dir dir)
    {
        switch (dir)
        {
            case N: return Dir.E;
            case E: return Dir.S;
            case S: return Dir.W;
            case W: return Dir.N;
        }
        return null;
    }

    public static Dir cardCcwDir(Dir dir)
    {
        switch (dir)
        {
            case N: return Dir.W;
            case E: return Dir.N;
            case S: return Dir.E;
            case W: return Dir.S;
        }
        return null;
    }

    
    public static Point move(Point p, Dir direction, int steps)
    {
        Point n = new Point(p.x, p.y);
        switch(direction)
        {
            case N:
                n.y-=steps;
                break;
            case W:
                n.x-=steps;
                break;
            case E:
                n.x+=steps;
                break;
            case S:
                n.y+=steps;
                break;
            case NW:
                n.x-=steps;
                n.y-=steps;
                break;
            case NE:
                n.x+=steps;
                n.y-=steps;
                break;
            case SE:
                n.x+=steps;
                n.y+=steps;
                break;
            case SW:
                n.x-=steps;
                n.y+=steps;
                break;
        }
        return n;
    }

    public static BufferedImage makeTiledGraphic(BufferedImage tileImage, int width, int height)
    {
        BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = newImage.createGraphics();
        for (int y = 0; y < height; y += tileImage.getHeight())
        {
            for (int x = 0; x < width; x += tileImage.getWidth())
            {
                g.drawImage(tileImage, x, y, null);
            }
        }
        g.dispose();
        return newImage;
    }
    
    public static void drawDot(Graphics g, int x, int y)
    {
        g.drawLine(x, y, x, y);
    }
    
    public static boolean oddsCheck(int numerator, int denominator)
    {
        return (new Random()).nextInt(denominator)+1 <= numerator;
    }

    public static String capitalize(String text)
    {
        return text.substring(0, 1).toUpperCase() + text.substring(1);
    }
    
    public static int getRandomInt(int lowest, int highest)
    {     
        int range = (highest-lowest)+1;        
        return ourRandom.nextInt(range)+lowest;
    }


    public static void shuffleList(List list)
    {
        // If there are 1 or 0 items, shuffle can't do anything:
        if (list.size() <= 1)
            return;
        
        for (int a = 0; a < list.size(); a++)
        {
            // Pick a random index:            
            int b = getRandomInt(0, list.size()-1);
            
            // Swap the current Object with the random Object:
            Object swapper = list.get(b);
            list.set(b, list.get(a));
            list.set(a, swapper);
        }
    }
    
    public static Object getRandomFromList(List list)
    {
        int i = getRandomInt(0, list.size()-1);
        return list.get(i);
    }
    
    public static ArrayList<String> getTextListFromFile(File f)
    {
        String[] text = ReadWriteTextFile.getContents(f).replace("\r", "").split("\n");  
        ArrayList<String> list = new ArrayList();
        list.addAll(Arrays.asList(text));
        return list;
    }
    
    public static double getDistance(Point ptA, Point ptB)
    {        
        int xDiff = ptA.x-ptB.x;
        int yDiff = ptA.y-ptB.y;
        return Math.sqrt(Math.abs(xDiff*xDiff)+Math.abs(yDiff*yDiff));
    }
    
    public static boolean isIntersecting(Shape shapeA, Shape shapeB)
    {
        Area areaA = new Area(shapeA);
        areaA.intersect(new Area(shapeB));
        return !areaA.isEmpty();
    }
    
    public static Shape getProjectedShape(Shape shape, int xMod, int yMod)
    {
        if (shape instanceof Rectangle)
        {
            Rectangle newShape = new Rectangle((Rectangle)shape);
            newShape.translate(xMod, yMod);
            return newShape;
        }
        else if (shape instanceof Polygon)
        {                   
            Polygon oldShape = (Polygon)shape;
            Polygon newShape = new Polygon(oldShape.xpoints.clone(), oldShape.ypoints.clone(), oldShape.npoints);
            newShape.translate(xMod, yMod);
            return newShape;
        }
        else if (shape instanceof Ellipse2D.Float)
        {                   
            Ellipse2D.Float oldShape = (Ellipse2D.Float)shape;
            Ellipse2D.Float newShape = new Ellipse2D.Float(oldShape.x, oldShape.y, oldShape.width, oldShape.height);
            newShape.setFrame(oldShape.x+xMod, oldShape.y+yMod, oldShape.width, oldShape.height);
            return newShape;
        }
        return null;
    }
    
    public static String[] getFormattedStatement(String statement, Font font, int pixelsPerLine)
    {
        if (statement == null)
            return null;
        
        StringBuilder builder = new StringBuilder(statement);
        int lineStart = 0;
        int lastSpace = 0;
        String part;
        for (int p = 0; p < builder.length(); p++)
        {
            // If you run into a space, it becomes the lastSpace
            if (builder.charAt(p) == '\n' || builder.charAt(p) == ' ' /*|| builder.charAt(p) == '-'*/)
            {
                lastSpace = p;
            }           
            
            // Get the part of the String from the lineStart to the current position:
            part = builder.substring(lineStart, p+1);
            
            // Get the pixel width of the part:
            Graphics g = GoodFight.getScreenManager().getGraphics();
            int partPixels = (int)g.getFontMetrics(font).getStringBounds(part, g).getWidth();
            
            // If the current part is too long, put a line break at the last space:
            if (partPixels >= pixelsPerLine)
            {
                builder.setCharAt(lastSpace, '@');
                lineStart = lastSpace+1;
                p = lastSpace+1;
            }

        }
        String[] lines = builder.toString().split("@");
        for (int i = 0; i < lines.length; i++)
        {
            lines[i] = lines[i].trim();
        }
        return lines;
    }
    
    public static boolean isFacingNorth(Dir dir)
    {
        return dir == Dir.N || dir == Dir.NE || dir == Dir.NW;
    }
    public static boolean isFacingEast(Dir dir)
    {
        return dir == Dir.NE || dir == Dir.E || dir == Dir.SE;
    }
    public static boolean isFacingSouth(Dir dir)
    {
        return dir == Dir.S || dir == Dir.SE || dir == Dir.SW;
    }
    public static boolean isFacingWest(Dir dir)
    {
        return dir == Dir.NW || dir == Dir.W || dir == Dir.SW;
    }
    
    public static void log(String text)
    {
        if (LOG)
            System.out.println(text);
    }
    
    public static void delay(long milliseconds)
    {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException ex) {
            Logger.getLogger(Global.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static String getOrdinalNumber(int num)
    {
        String strInt = Integer.toString(num);
        int lastDigit = Integer.parseInt(strInt.substring(strInt.length()-1));
        switch(lastDigit)
        {
            case 1:
                return num + "st";
            case 2:
                return num + "nd";
            case 3:
                return num + "rd";
            default:
                return num + "th";
        }
    }
}

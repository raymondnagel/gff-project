/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gff;

import gff.maps.WorldMap;
import gff.maps.Map;
import gff.maps.ChurchMap;
import gff.spritecommands.SpriteCommand;
import gff.attacks.EnemyAttack;
import gff.bible.BibleOps;
import gff.bible.BibleReference;
import gff.components.AreaLabel;
import gff.components.BookSelector;
import gff.components.CheckItem;
import gff.components.ComponentGroup;
import gff.components.GameIcon;
import gff.components.InterfaceComponent;
import gff.components.KeyboardEditable;
import gff.components.Label;
import gff.components.Menu;
import gff.components.Popup;
import gff.components.SimpleButton;
import gff.components.TextEntryBox;
import gff.graphics.BackgroundLayer;
import gff.graphics.NullRepaintManager;
import gff.graphics.SceneLayer;
import gff.graphics.ScreenManager;
import gff.graphics.SpiritBall;
import gff.graphics.SpriteLayer;
import gff.items.Armour;
import gff.items.Book;
import gff.items.Item;
import gff.items.Meat;
import gff.items.Milk;
import gff.items.StrongMeat;
import gff.items.Sword;
import gff.objects.Person;
import gff.objects.Drawing;
import gff.objects.Enemy;
import gff.objects.ExpHero;
import gff.objects.Hero;
import gff.objects.Sprite;
import gff.objects.Thing;
import gff.objects.TreasureChest;
import gff.objects.scenery.buildings.ChurchBuilding;
import gff.objects.scenery.Portal;
import gff.objects.scenery.buildings.StrongholdBuilding;
import gff.sound.old.MidiPlayer;
import gff.sound.old.Sound;
import gff.sound.old.SoundManager;
import gff.spritecommands.WalkCommand;
import gff.util.FinalBypasser;
import gff.util.ReadWriteTextFile;
import java.awt.AWTException;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.DisplayMode;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Robot;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import org.jdom2.Element;

/**
 *
 * @author Raymond Nagel
 */
public class GoodFight
{        
    public static enum Sex {MALE, FEMALE, RANDOM};
    public static enum Dir {                
        NONE (0, 0, 0),
        N (0, -1, 0),
        NE (1, -1, 0),
        E (1, 0, 0),
        SE (1, 1, 0), 
        S (0, 1, 0),
        SW (-1, 1, 0),
        W (-1, 0, 0),
        NW (-1, -1, 0),
        UP (0, 0, -1),
        DOWN (0, 0, 1);

        private int xChange = 0;
        private int yChange = 0;
        private int zChange = 0;
        private Dir(int xChange, int yChange, int zChange) {
            this.xChange = xChange;
            this.yChange = yChange;
            this.zChange = zChange;
        }
        public int getXChange()
        {
            return this.xChange;
        }
        public int getYChange()
        {
            return this.yChange;
        }
        public int getZChange()
        {
            return this.zChange;
        }
        
        public static Dir[] getCardinalDirections()
        {
            Dir[] cards = new Dir[4];
            cards[0] = N;
            cards[1] = E;
            cards[2] = S;
            cards[3] = W;
            return cards;
        }
    };
    public static enum Terrain {CHURCH, STRONGHOLD, PLAIN, FOREST, MOUNTAIN, DESERT, SWAMP, TUNDRA};
    
    public static final long MOUSE_TIMEOUT = 5000;
    public static final Color COLOR_GOLD = new Color(255, 150, 40);
    public static final Color MAP_WALL_COLOR = new Color(83, 55, 6);
    
    public static String STARTING_BOOK = "John";
    public static Book ourGiftBook = null;
    
    public static final GameDifficulty BABE_DIFFICULTY = new GameDifficulty("Babe", 15, 2f, 80.0, 1, 2);
    public static final GameDifficulty DISCIPLE_DIFFICULTY = new GameDifficulty("Disciple", 10, 1.5f, 100.0, 2, 3);
    public static final GameDifficulty SOLDIER_DIFFICULTY = new GameDifficulty("Soldier", 5, 1f, 120.0, 3, 4);
    
    public static final int MAP_TITLE_MARGIN = 16;
    public static final int SCREEN_W = 1024;
    public static final int SCREEN_H = 768;
    public static final int SCENE_W = 1024;
    public static final int SCENE_H = 680;
    public static final int SCENE_X = 0;
    public static final int SCENE_Y = 88;
    public static final int SCENE_LEFT = SCENE_X;
    public static final int SCENE_RIGHT = (SCENE_X + SCENE_W) - 1;
    public static final int SCENE_TOP = SCENE_Y;
    public static final int SCENE_BOTTOM = (SCENE_Y + SCENE_H) - 1;
    public static final int SCENE_CTR_X = SCENE_X + (SCENE_W/2);
    public static final int SCENE_CTR_Y = SCENE_Y + (SCENE_H/2);        
    
    // Scene Border: the edge of the scene itself.
    // Scenery can only occur inside this border.
    // Note that the scene's background extends past this border.
    public static final int SCENE_BORDER = 40;
    public static final int LEFT_BOUND = SCENE_BORDER;
    public static final int RIGHT_BOUND = (SCENE_X+SCENE_W)-(SCENE_BORDER+1);
    public static final int TOP_BOUND = SCENE_Y+SCENE_BORDER;
    public static final int BOTTOM_BOUND = (SCENE_Y+SCENE_H)-(SCENE_BORDER+1);
    
    // Travel Border: the edge of where sprites can travel to
    // Player's phys bound moving beyond the scene border into the travel border can trigger a scene change.
    // Travel Border width should be equal to Player's Height - Player's Phys Height.
    public static final int TRAVEL_BORDER = 32;
    public static final int TV_LEFT_BOUND = TRAVEL_BORDER;
    public static final int TV_RIGHT_BOUND = (SCENE_X+SCENE_W)-(TRAVEL_BORDER+1);
    public static final int TV_TOP_BOUND = SCENE_Y+TRAVEL_BORDER;
    public static final int TV_BOTTOM_BOUND = (SCENE_Y+SCENE_H)-(TRAVEL_BORDER+1);
    
    public static final int NATURAL_WALL_THICKNESS = SCENE_BORDER;
    public static final int SPAWN_ATTEMPTS = 20;
    
    public static Robot                 ourRobot = null;  
    private static JFrame               ourMainFrame = null;
    private static ScreenManager        ourScreenManager = null;
    private static SoundManager         ourSoundManager = null;
    private static MidiPlayer           ourMidiPlayer = null;
    
    
    // Interface, Input, and Technical:
    private static Color                ourMapWritColor = new Color(120,80,15);
    
    private static HashMap<String, BufferedImage> ourLoadedImages = new HashMap<>();
    private final static boolean[]      ourKeyMap = new boolean[600];    
    private static Cursor               ourCursor = null;
    private static Cursor               ourBlankCursor = null;    
    private static long                 ourMouseTimeout = 0;
    private static Comparator           ourSpritesComparator = new Comparator<Sprite>(){
        public int compare(Sprite o1, Sprite o2) {
            return o1.getBottom() - o2.getBottom();
        }            
    };
    public static boolean               ShowFKeyShortcuts = false;
    private static boolean              ourStop = false;
    private static boolean              ourPrayerMode = false;
    private static float                ourUniversalFade = 0f;
    
    
    private static ArrayList<ComponentGroup> ourCurrentComponentGroups = new ArrayList<>();
    private static ComponentGroup       ourMainComponents = new ComponentGroup();
    private static ComponentGroup       ourSpecialEventComponents = new ComponentGroup();
    private static ComponentGroup       ourBookListComponents = new ComponentGroup();      
    private static ComponentGroup       ourBattleComponents = new ComponentGroup();   
    private static ComponentGroup       ourBattleReferenceComponents = new ComponentGroup();
    private static ComponentGroup       ourBattleEvaluationComponents = new ComponentGroup();
    private static AreaLabel            ourVerseTextLabel = null;
    private static AreaLabel            ourActionLabel = null;
    private static AreaLabel            ourGuessCaptionLabel = null;
    private static AreaLabel            ourGuessReferenceLabel = null;   
    private static AreaLabel            ourActualCaptionLabel = null;
    private static AreaLabel            ourActualReferenceLabel = null;
    private static Label                ourChapterScoreLabel = null;
    private static Label                ourVerseScoreLabel = null;
    private static Label                ourBooksMultiplierLabel = null;
    private static Label                ourBonusScoreLabel = null;
    private static Label                ourFinalScoreLabel = null;
    private static InterfaceComponent   ourSmallHeroMeter = null;
    private static InterfaceComponent   ourLargeHeroMeter = null;
    private static InterfaceComponent   ourLargeEnemyMeter = null;
    private static InterfaceComponent   ourHeroPortrait = null;
    private static InterfaceComponent   ourEnemyPortrait = null;
    private static SimpleButton         ourBookSelectButton = null;
    private static BookSelector         ourBookSelector = null;
    private static TextEntryBox         ourChapterTextEntry = null;
    private static TextEntryBox         ourVerseTextEntry = null;
    
    
    
    private static MouseAdapter         ourMouseHandler = new MouseAdapter() {
        @Override
        public void mouseMoved(MouseEvent e) {  
            ourMouseTimeout = MOUSE_TIMEOUT;
            if (ourMainFrame.getCursor() == ourBlankCursor)
            {
                ourMainFrame.setCursor(ourCursor);
            }
            
            // For each component group:
            for (int g = 0; g < ourCurrentComponentGroups.size(); g++)
            {
                // For each component in the component group:
                for (int c = 0; c < ourCurrentComponentGroups.get(g).getComponentCount(); c++)
                {
                    InterfaceComponent comp = ourCurrentComponentGroups.get(g).getComponents().get(c);
                    if (comp.getBounds().contains(e.getPoint()))
                    {
                        if (!comp.hasMouse())
                        {
                            // The mouse wasn't here before, but now it is:
                            comp.mouseEntered(e);
                        }
                        comp.mouseMoved(e);
                    }
                    else if (comp.hasMouse())
                    {
                        // The mouse was here before, but now it isn't:
                        comp.mouseExited(e);
                    }
                }
            }
        }    

        @Override
        public void mouseDragged(MouseEvent e) {       
            // For each component group:
            for (int g = 0; g < ourCurrentComponentGroups.size(); g++)
            {
                // For each component in the component group:
                for (int c = 0; c < ourCurrentComponentGroups.get(g).getComponentCount(); c++)
                {
                    InterfaceComponent comp = ourCurrentComponentGroups.get(g).getComponents().get(c);
                    if (comp.getBounds().contains(e.getPoint()))
                    {
                        if (!comp.hasMouse())
                        {
                            // The mouse wasn't here before, but now it is:
                            comp.mouseEntered(e);
                        }
                        comp.mouseMoved(e);
                    }
                    else if (comp.hasMouse())
                    {
                        // The mouse was here before, but now it isn't:
                        comp.mouseExited(e);
                    }
                }
            }
        }    
        
        @Override
        public void mousePressed(MouseEvent e) {
            // For each component group:
            for (int g = 0; g < ourCurrentComponentGroups.size(); g++)
            {
                // For each component in the component group:
                for (int c = 0; c < ourCurrentComponentGroups.get(g).getComponentCount(); c++)
                {
                    InterfaceComponent comp = ourCurrentComponentGroups.get(g).getComponents().get(c);
                    if (comp.getBounds().contains(e.getPoint()))
                    {                        
                        comp.mousePressed(e);
                    }
                }
            }
        }
        
        @Override
        public void mouseReleased(MouseEvent e) {
            // For each component group:
            for (int g = 0; g < ourCurrentComponentGroups.size(); g++)
            {
                // For each component in the component group:
                for (int c = 0; c < ourCurrentComponentGroups.get(g).getComponentCount(); c++)
                {
                    InterfaceComponent comp = ourCurrentComponentGroups.get(g).getComponents().get(c);
                    // If a component has "GlobalMouseRelease" and the mouse is pressed on it, 
                    // it should receive the MouseReleased event even if it does not contain the mouse when it is released.
                    if ((comp.isGlobalMouseRelease() && comp.isMousePressed()) || comp.getBounds().contains(e.getPoint()))
                    {                        
                        comp.mouseReleased(e);
                    }
                }
            }
        }
    };
    private static KeyAdapter           ourKeyHandler = new KeyAdapter(){
        @Override
        public void keyPressed(KeyEvent e)
        {                                       
            // Don't do anything with the KeyPress if the key is already mapped
            // as being held down. We only want the initial KeyPress event!
            if (ourKeyMap[e.getKeyCode()]) return;
            
            // First, try to consume this on the current DialogEvent, if there is one:            
            if (ourCurrentDialogEvent != null)
            {
                if (ourCurrentDialogEvent.useKeyPress(e))
                    return;
                else
                    return; // A dialog should block any other keypress, anyway.
            }
            
            // Second, try to consume this on the current SpecialEvent, if there is one:            
            if (ourCurrentSpecialEvent != null)
            {
                if (ourCurrentSpecialEvent.useKeyPress(e))
                    return;
            }
            
            // Next, try to consume this on an "editing" control, if one is active: 
            boolean consumed = false;
            // For each component group:
            for (int g = 0; g < ourCurrentComponentGroups.size(); g++)
            {
                // For each component in the component group:
                for (int c = 0; c < ourCurrentComponentGroups.get(g).getComponentCount(); c++)
                {
                    InterfaceComponent comp = ourCurrentComponentGroups.get(g).getComponents().get(c);
                    if (comp instanceof KeyboardEditable)
                    {
                        KeyboardEditable keyEd = (KeyboardEditable)comp;
                        if (keyEd.isEditing())
                        {
                            if (keyEd.keyTyped(e))
                                consumed = true;                            
                        }
                    }
                }
            }
            if (consumed)
                return;
            
            switch(e.getKeyCode())
            {
                // Deal with specific cases of GameMode-independent key presses;
                // i.e. that can be accessed from any GameMode:
                case KeyEvent.VK_ESCAPE:
                {
                    // If [Escape] is pressed from any Subscreen GameMode
                    // it should revert the game to Adventure mode:
                    if (isSubscreenMode(ourCurrentGameMode))
                    {
                        changeGameMode(AdventureMode);
                    }
                    else if (!isAuxiliaryMode(ourCurrentGameMode))// If [Escape] is pressed in any other non-auxiliary mode, the game should exit:
                    {
                        doShowQuitPopup();
                    }
                    break;
                }           
                case KeyEvent.VK_F2:
                {            
                    // [F2] switches to BookList mode from Adventure mode or any Subscreen mode:
                    if (ourCurrentGameMode == AdventureMode || isSubscreenMode(ourCurrentGameMode))
                    {
                        changeGameMode(BookListMode);
                    }
                    break;
                }
                case KeyEvent.VK_F5:
                {            
                    // [F5] switches to Map mode from Adventure mode or any Subscreen mode:
                    if (ourCurrentGameMode == AdventureMode || isSubscreenMode(ourCurrentGameMode))
                    {
                        changeGameMode(MapMode);
                    }
                    break;
                }
                case KeyEvent.VK_F11:
                {            
                    // [F11] toggles debugging flag; it's ok to do this
                    // from any GameMode:
                    Global.TestMode = !Global.TestMode;
                    break;
                }
                case KeyEvent.VK_F12:
                {            
                    // [F12] takes a screen shot of any GameMode:
                    BufferedImage screenShot = new BufferedImage(SCREEN_W, SCREEN_H, BufferedImage.TYPE_INT_RGB);
                    Graphics2D g = screenShot.createGraphics();
                    ourCurrentGameMode.draw(g);
                    drawCurrentComponents(g);
                    try {
                        ImageIO.write(screenShot, "PNG", new File("screenshots/ss_" + System.currentTimeMillis() + ".png"));
                    } catch (IOException ex) {
                        Logger.getLogger(GoodFight.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    break;
                }
                case KeyEvent.VK_P:
                {
                    // [P] pauses the game in any non-auxiliary GameMode:
                    if (!isAuxiliaryMode(ourCurrentGameMode))
                    {
                        setPaused(!ourPaused);
                    }
                    break;
                }
                case KeyEvent.VK_ADD:
                {
                    // [+] adds a random commandment, for debugging only. Subject to change.
//                    Commandment com = Global.withdrawRandomCommandment();
//                    GoodFight.showItemPopup(com);
//                    com.onAcquire();
//                    break;
                }
                case KeyEvent.VK_SUBTRACT:
                {
                    if (ourCompanion != null)
                    {
                        if (ourCompanion.getMode() == Person.PersonMode.STILL)
                        {
                            ourCompanion.setMode(Person.PersonMode.FOLLOW);
                            ourCompanion.showEffectText("Follow Mode");
                        }
                        else if (ourCompanion.getMode() == Person.PersonMode.FOLLOW)
                        {
                            ourCompanion.setMode(Person.PersonMode.WANDER);
                            ourCompanion.showEffectText("Wander Mode");
                        }
                        else if (ourCompanion.getMode() == Person.PersonMode.WANDER)
                        {
                            ourCompanion.setMode(Person.PersonMode.STILL);
                            ourCompanion.showEffectText("Still Mode");
                        }
                    }
                }
                default:
                {                    
                    // Any other key press may have GameMode-specific behavior,
                    // so forward it to the current GameMode:
                    ourCurrentGameMode.keyPressed(e);
                    break;
                }
            }
            ourKeyMap[e.getKeyCode()] = true;            
        }
        @Override
        public void keyReleased(KeyEvent e)
        {
            ourKeyMap[e.getKeyCode()] = false;
            switch (e.getKeyCode())
            {
                default:
                {
                    // Any other key release may have GameMode-specific behavior,
                    // so forward it to the current GameMode:
                    ourCurrentGameMode.keyReleased(e);
                    break;
                }
            }
        }
        @Override
        public void keyTyped(KeyEvent e) {
            switch (e.getKeyCode())
            {
                default:
                {
                    // A key type may have GameMode-specific behavior,
                    // so forward it to the current GameMode:
                    ourCurrentGameMode.keyTyped(e);
                    break;
                }
            }
        }        
    };
    
    // Gameplay:    
    private final static ArrayList<Sprite> ourSprites = new ArrayList<Sprite>();
    private final static ArrayList<Drawing> ourDrawings = new ArrayList<Drawing>(); 
    private final static ArrayList<SpecialEffect> ourSpecialEffects = new ArrayList<>();
    
    private static GameDifficulty       ourGameDifficulty = DISCIPLE_DIFFICULTY;
    private static WorldMap             ourWorldMap = null;
    private static Map                  ourCurrentMap = null;
    private static Scene                ourCurrentScene = null;
    private static Scene                ourObjectifiedScene = null;
    private static SpecialEvent         ourCurrentSpecialEvent = null;
    private static SpecialEvent         ourCurrentDialogEvent = null;
    private static Hero                 ourHero = null;
    private static Person               ourCompanion = null;
    private static Enemy                ourCurrentEnemy = null;
    private static boolean              ourPaused = false;
    private static BibleReference       ourServedReference = null;
    private static BibleReference       ourGuessedReference = null;      
    
    public final static GameMode       NullMode = new GameMode() {
        @Override
        public String getName() {
            return "Null";
        }
        @Override
        public void switchTo() {
        }
        @Override
        public void switchFrom() {            
        }
        @Override
        public void cycle() {
        }
        @Override
        public void draw(Graphics2D g) {           
        }
        @Override
        public void keyTyped(KeyEvent ke) {   
        }
        @Override
        public void keyPressed(KeyEvent ke) {
        }
        @Override
        public void keyReleased(KeyEvent ke) {
        }
    };  
    public final static GameMode       TitleMode = new GameMode() {
        private boolean showBg = false;
        private boolean showTitle = false;
        private boolean fadingOut = false;
        private double bgInc = .06;
        private double titleInc = .085;
        private double promptInc = .4;
        private double allInc = .1;
        private double fadeTitle = 0;
        private double fadeBg = 0;
        private double fadePrompt = 0; 
        private double fadeAll = 0;
        @Override
        public String getName() {
            return "Title";
        }
        @Override
        public void switchTo() {            
            ourCurrentComponentGroups.remove(ourMainComponents);
//            ourCurrentComponentGroups.add(ourBattleComponents);
//            ourCurrentComponentGroups.add(ourBattleReferenceComponents);
//            ourCurrentComponentGroups.add(ourBattleEvaluationComponents);            
//            ourBattleReferenceComponents.setVisible(false);
            
            playSoundEffect("intro_w_drumroll");
        }
        @Override
        public void switchFrom() {   
            TimingRegister.remove("fade_in_bg");
            TimingRegister.remove("fade_in_title");
            TimingRegister.remove("fade_prompt");
            TimingRegister.remove("fade_all");    
            ourCurrentComponentGroups.add(ourMainComponents);
        }
        @Override
        public void cycle() {
            if (!showBg)
            {
                fadeBg = TimingRegister.update("fade_in_bg", fadeBg, bgInc);
                if (fadeBg >= 255)
                {
                    fadeBg = 255;
                    showBg = true;  
                    Global.setBackgroundMusic("battlecry");
                }
            }
            else if (!showTitle)
            {
                fadeTitle = TimingRegister.update("fade_in_title", fadeTitle, titleInc);
                if (fadeTitle >= 255)
                {
                    fadeTitle = 255;
                    showTitle = true;
                }
            }
            else if (!fadingOut)
            {
                fadePrompt = TimingRegister.update("fade_prompt", fadePrompt, promptInc);
                if (fadePrompt >= 255)
                {
                    fadePrompt = 255;
                    promptInc = -promptInc;                    
                }
                else if (fadePrompt <= 0)
                {
                    fadePrompt = 0;
                    promptInc = -promptInc;                    
                }
            }
            else if (fadingOut)
            {
                fadeAll = TimingRegister.update("fade_all", fadeAll, allInc);
                Global.fadeBackgroundMusic(-.12f);
                if (fadeAll >= 255)
                {
                    fadeAll = 255;
                    Global.delay(1000);                    
                    doMainMenu();
                }
            }                
        }
        @Override
        public void draw(Graphics2D g) {  
            if (fadeAll != 255)
            {
                if (fadingOut)
                {
                    float tsp = 1.0f - (float)fadeAll/255f;  
                    g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, tsp));
                    g.drawImage(getLoadedImage("interface/title_bg.png"), 0, 0, null);
                    g.drawImage(getLoadedImage("interface/title_overlay.png"), (SCREEN_W/2)-(getLoadedImage("interface/title_overlay.png").getWidth()/2), 80, null);
                }
                else
                {
                    float tsp = (float)fadeBg/255f;  
                    g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, tsp));
                    g.drawImage(getLoadedImage("interface/title_bg.png"), 0, 0, null);                          

                    if (showBg)
                    {
                        tsp = (float)fadeTitle/255f;  
                        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, tsp));
                        g.drawImage(getLoadedImage("interface/title_overlay.png"), (SCREEN_W/2)-(getLoadedImage("interface/title_overlay.png").getWidth()/2), 80, null);
                    }
                    if (showTitle)
                    {
                        tsp = (float)fadePrompt/255f;  
                        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, tsp));
                        g.drawImage(getLoadedImage("interface/title_prompt.png"), (SCREEN_W/2)-(getLoadedImage("interface/title_prompt.png").getWidth()/2), 648, null);
                    }
                }    
            }
        }
        @Override
        public void keyTyped(KeyEvent ke) {
            
        }
        @Override
        public void keyPressed(KeyEvent ke) {
            // An Enter keyPress should only be accepted after the title is shown
            if (ke.getKeyCode() == KeyEvent.VK_ENTER && showTitle && !fadingOut)
            {
                playSoundEffect("success");
                fadingOut = true;
            }
        }
        @Override
        public void keyReleased(KeyEvent ke) {
            
        }
    };
    public final static GameMode       AdventureMode = new GameMode() {
        @Override
        public String getName() {
            return "Adventure";
        }
        @Override
        public void cycle() {
            if (ourCurrentMap instanceof ChurchMap)
            {
                Church church = ((ChurchMap)ourCurrentMap).getBuilding().getChurch();
                if (church.isInService())
                    ourHero.restoreFaith(1);
            }
            
            if (ourCurrentSpecialEvent == null && ourHero.canLevel())
            {
                doLevelUp();
            }
            
            // Sort Sprites according to y-order:
            // (Scenery sorting is handled within the SpriteLayer)
            Collections.sort(ourSprites, ourSpritesComparator);

            // Move Sprites:
            for (int r = 0; r < ourSprites.size(); r++)
            {
                // Any Sprites that are following Commands:
                SpriteCommand command = ourSprites.get(r).getCommand();
                if (command != null)
                {
                    command.iterate();
                    if (command.isDone())
                    {
                        command.stop();
                        // Although we just checked it, we need to check whether the Sprite's command
                        // is done, because it may have been re-assigned in the last command's onStop().
                        if (ourSprites.get(r).getCommand().isDone())
                        {
                            ourSprites.get(r).clearCommand();
                        }
                    }
                }                
                // Controllable ones: (this should be only the Hero)
                else if (ourSprites.get(r) instanceof Controllable)
                {
                    Controllable cont = (Controllable)ourSprites.get(r);
                    if (cont.isControlled())
                    {
                        cont.keysAreDown(ourKeyMap);
                    }
                }
                // Any other Sprites that are Automated:
                else if (ourSprites.get(r) instanceof Automaton)
                {
                    Automaton auto = (Automaton)ourSprites.get(r);
                    auto.automate();
                }
            }

            // Update all Sprites:
            for (int r = 0; r < ourSprites.size(); r++)
            {                         
                ourSprites.get(r).update();
            }

            // Remove all Sprites that are marked for destruction:
            for (int r = ourSprites.size()-1; r >= 0; r--)
            {                         
                if (ourSprites.get(r).shouldBeDestroyed())
                {
                    ourSprites.remove(r);
                }
            }         

            // Test for Scene-edge travel:
            checkSceneEdgeTravel();
        }
        @Override
        public void draw(Graphics2D g) {            
            ArrayList<SceneLayer> layers = ourCurrentScene.getLayers();
            for (int layer = 0; layer < layers.size(); layer++)
            {
                layers.get(layer).draw(g);
                
                if (Global.TestMode && layers.get(layer) instanceof BackgroundLayer)
                {
                    ArrayList<Shape> zones = ourCurrentScene.getNoIntersectZones();
                    for (int z = 0; z < zones.size(); z++)
                        drawZone(zones.get(z), g, Color.YELLOW);
                    zones = ourCurrentScene.getNoIntrusionZones();
                    for (int z = 0; z < zones.size(); z++)
                        drawZone(zones.get(z), g, Color.BLACK);
                    zones = ourCurrentScene.getSceneryZones();
                    for (int z = 0; z < zones.size(); z++)
                        drawZone(zones.get(z), g, Color.GREEN);
                }
            }
            if (Global.TestMode)
            {
                drawSceneDims(g);
            }
            drawNameTags(g);            
            drawVisibilityRadius(g);
            drawSpeech(g);
            drawDrawings(g);
        }        
        @Override
        public void keyTyped(KeyEvent ke) {
            
        }
        @Override
        public void keyPressed(KeyEvent ke) {
            // Send a signal to each currently controlled Controllable:
            for (int r = ourSprites.size()-1; r >= 0; r--)
            {
                if (ourSprites.get(r) instanceof Controllable)
                {
                    Controllable c = (Controllable)ourSprites.get(r);
                    if (c.isControlled())
                        c.keyPressed(ke.getKeyCode());
                }
            }
        }
        @Override
        public void keyReleased(KeyEvent ke) {
            if (ke.getKeyCode() == KeyEvent.VK_SPACE)
            {
                final Person p = getNearestFacingPerson(ourHero);
                if (p != null)
                {
                    Conversation c = p.getNextConversation();
                    if (c != null)
                    {
                        p.faceToward(ourHero);
                        p.setConversing(true);
                        ourHero.setConversing(true);
                        doStartConversation(c, new CodeDelegate() {
                            @Override
                            public void run() {
                                p.setConversing(false);
                                ourHero.setConversing(false);
                            }
                        });
                    }
                }
            }
            if (ke.getKeyCode() == KeyEvent.VK_BACK_SPACE)
            {
                ourHero.showEffectText("Seed Planted!");
            }
            if (ke.getKeyCode() == KeyEvent.VK_N)
            {
                Global.ShowNameTags = !Global.ShowNameTags;
            }
            
            // Send a signal to each currently controlled Controllable:
            for (int r = 0; r < ourSprites.size(); r++)
            {
                if (ourSprites.get(r) instanceof Controllable)
                {
                    Controllable c = (Controllable)ourSprites.get(r);
                    if (c.isControlled())
                        c.keyReleased(ke.getKeyCode());
                }
            }
        }
        @Override
        public void switchTo() {
            // When switching back to Adventure mode, we need to do an "unpause" on
            // all Sprites, because we don't want them to have moved during the
            // previous non-Adventure GameMode. This is the same effect we use
            // during a SpecialEvent that disables normal operation.
            if (ourCurrentGameMode != AdventureMode)
            {
                for (int s = 0; s < ourSprites.size(); s++)
                {
                    ourSprites.get(s).unpause();
                }
            }
            // Change the music:
            if (ourCurrentGameMode == NullMode || ourCurrentGameMode == BattleMode)
            {
                //ourMidiPlayer.stop(); 
                //ourMidiPlayer.play(ourMidiPlayer.getSequence("extern/midi/REDEEMED.mid"), true);
                Global.setBackgroundMusic("togodglory");
            }
        }
        @Override
        public void switchFrom() {            
        }
        // Mode-specific drawing methods:
        public void drawVisibilityRadius(Graphics2D g)
        {
            int numCmds = Global.getNumCommandments();
            if (numCmds < 10 && !ourCurrentScene.isSafe() && !Global.TestMode) 
            {        
                BufferedImage img = Global.getVisibilityImage();
                int r = img.getWidth() / 2;
                Point ctr = ourHero.getCenterPoint();

                int iX = ctr.x - r;
                int iY = ctr.y - r;

                int iR = iX + img.getWidth();
                int iB = iY + img.getHeight();

                // Draw visibility image:
                g.drawImage(img, iX, iY, null);

                // Fill up to 4 rectangles around it, if necessary
                g.setColor(Color.BLACK);
                if (iY > SCENE_TOP) {
                    // Fill top rect:
                    int h = iY - SCENE_TOP;
                    int w = SCENE_W;                
                    g.fillRect(0, iY-h, w, h);
                }
                if (iX > SCENE_LEFT) {
                    // Fill left rect:
                    int h = SCENE_H;
                    int w = iX - SCENE_LEFT;               
                    g.fillRect(iX-w, SCENE_TOP, w, h);
                }
                if (iR <= SCENE_RIGHT) {
                    // Fill right rect:
                    int h = SCENE_H;
                    int w = (SCENE_RIGHT - iR) + 1; 
                    g.fillRect(iR, SCENE_TOP, w, h);
                }
                if (iB <= SCENE_BOTTOM) {
                    // Fill bottom rect:
                    int h = (SCENE_BOTTOM - iB) + 1;
                    int w = SCENE_W;             
                    g.fillRect(0, iB, w, h);
                }
            }
        }
        public void drawNameTags(Graphics2D g)
        {
            if (Global.ShowNameTags)
            {
                for (int s = 0; s < ourSprites.size(); s++)
                {
                    ourSprites.get(s).renderNameTag(g);
                }
            }
        }
        public void drawSpeech(Graphics2D g)
        {
            if (Global.TestMode)
            {
                Person conversant = getNearestFacingPerson(ourHero);
                if (conversant != null)
                {
                    g.setColor(Color.WHITE);
                    Stroke drawingStroke = new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0, new float[]{2, 2}, 0f);
                    g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                    g.setStroke(drawingStroke);
                    g.drawLine(ourHero.getCtrX(), ourHero.getTop(), conversant.getCtrX(), conversant.getTop());
                }
            }
            
            for (int s = 0; s < ourSprites.size(); s++)
            {
                ourSprites.get(s).renderSpeech(g);
            }            
        }
        public void drawZone(Shape zone, Graphics g, Color color)
        {            
            if (zone != null)
            {    
                g.setColor(color);
                if (zone instanceof Rectangle)
                {
                    Rectangle rectangle = (Rectangle)zone;
                    g.drawRect(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
                }
                else if (zone instanceof Polygon)
                {                   
                    Polygon polygon = (Polygon)zone;
                    g.drawPolygon(polygon);
                }
                else if (zone instanceof Ellipse2D.Float)
                {                   
                    Ellipse2D.Float ellipse = (Ellipse2D.Float)zone;
                    g.drawOval((int)ellipse.x, (int)ellipse.y, (int)ellipse.width, (int)ellipse.height);
                }
            }
        }
        public void drawSceneDims(Graphics g)
        {
            g.setFont(Global.GreekMedFont);
            
            // Scene Edge
            g.setColor(Color.ORANGE);
            g.drawRect(SCENE_LEFT, SCENE_TOP, SCENE_W-1, SCENE_H-1);
            g.drawString("Scene Edge", SCENE_LEFT, SCENE_TOP+g.getFontMetrics().getAscent());
            
            // Bounds
            g.setColor(Color.BLUE);
            g.drawRect(LEFT_BOUND, TOP_BOUND, RIGHT_BOUND-LEFT_BOUND, BOTTOM_BOUND-TOP_BOUND);
            g.drawString("Scenery Bounds", LEFT_BOUND, TOP_BOUND+g.getFontMetrics().getAscent());
            
            // Travel Bounds
            g.setColor(Color.GREEN);
            g.drawRect(TV_LEFT_BOUND, TV_TOP_BOUND, TV_RIGHT_BOUND-TV_LEFT_BOUND, TV_BOTTOM_BOUND-TV_TOP_BOUND);     
            g.drawString("Travel Bounds", TV_LEFT_BOUND, TV_TOP_BOUND+g.getFontMetrics().getAscent());
        }
    };
    public final static GameMode       BookListMode = new GameMode() {
        @Override
        public String getName() {
            return "BookList";
        }
        @Override
        public void switchTo() {
            ourCurrentComponentGroups.add(ourBookListComponents);
        }
        @Override
        public void switchFrom() {  
            ourCurrentComponentGroups.remove(ourBookListComponents);
        }
        @Override
        public void cycle() {

        }
        @Override
        public void draw(Graphics2D g) {
            g.drawImage(getLoadedImage("interface/rock_bg.png"), SCENE_LEFT, SCENE_TOP, null);
            if (Global.TestMode)
                g.drawImage(getLoadedImage("test/book_list_layout.png"), SCENE_LEFT, SCENE_TOP, null);
        }
        @Override
        public void keyTyped(KeyEvent ke) {
            
        }
        @Override
        public void keyPressed(KeyEvent ke) {
            
        }
        @Override
        public void keyReleased(KeyEvent ke) {
            
        }
    };
    public final static GameMode       MapMode = new GameMode() {
        @Override
        public String getName() {
            return "Map";
        } 
        @Override
        public void cycle() {
            
        }
        @Override
        public void draw(Graphics2D g) {
            // Dimensions of the "squares"
            int sqW = 38;
            int sqH = 22;
            int cr = 1;

            // Draw the map graphic (background)
            BufferedImage mapImg = getLoadedImage("interface/map/map.png");
            int x = SCENE_CTR_X - mapImg.getWidth()/2;
            int y = SCENE_CTR_Y - mapImg.getHeight()/2;
            g.drawImage(mapImg, x, y, null);

            // Total width & height (of actual map content)
            int tW = sqW * ourCurrentMap.getWidth();
            int tH = sqH * ourCurrentMap.getHeight();

            // Center the map content drawing:
            int dX = SCENE_CTR_X - (tW/2);
            int dY = SCENE_CTR_Y - (tH/2);

            // Paint the map's title:
            g.setColor(ourMapWritColor);
            g.setFont(Global.FancyFont.deriveFont(Font.PLAIN, 50));
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            int width = g.getFontMetrics().stringWidth(ourCurrentMap.getFloorName());
            int height = g.getFontMetrics().getHeight();
            
            // Bump the map content origin down by half of the title height plus
            // half of the MAP_TITLE_MARGIN; this keeps content+title centered.
            dY += ((height/2));
            
            g.drawString(ourCurrentMap.getFloorName(), SCENE_CTR_X - (width/2), dY - MAP_TITLE_MARGIN);
            
            Scene drawnScene;
            for (x = 0; x < ourCurrentMap.getWidth(); x++)
            {
                for (y = 0; y < ourCurrentMap.getHeight(); y++)
                {
                    drawnScene = ourCurrentMap.getScene(x,y, ourCurrentScene.getFloor());
                    if (drawnScene.isDiscovered() || Global.TestMode)
                    {
                        switch(drawnScene.getTerrain())
                        {
                            case PLAIN:
                                mapImg = getLoadedImage("interface/map/map_grass.png");
                                break;
                            case FOREST:
                                mapImg = getLoadedImage("interface/map/map_forest.png");
                                break;
                            case MOUNTAIN:
                                mapImg = getLoadedImage("interface/map/map_mount.png");
                                break;
                            case DESERT:
                                mapImg = getLoadedImage("interface/map/map_desert.png");
                                break;
                            case SWAMP:
                                mapImg = getLoadedImage("interface/map/map_swamp.png");
                                break;
                            case TUNDRA:
                                mapImg = getLoadedImage("interface/map/map_tundra.png");
                                break;
                            case STRONGHOLD:
                            case CHURCH:
                                mapImg = getLoadedImage("interface/map/map_floor.png");
                                break;
                        }
                        g.drawImage(mapImg, dX+(x*sqW), dY+(y*sqH), null);
                        
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

                        for (int h = 0; h < 12; h++)
                        {
                            int wX = dX+(x*sqW)+cr+(h*3);
                            if (drawnScene.getWallSection(Dir.N)[h] && (drawnScene.isNeighborDiscovered(Dir.N) || Global.TestMode))
                                g.drawLine(wX, dY+(y*sqH), wX+2, dY+(y*sqH));
                            if (drawnScene.getWallSection(Dir.S)[h] && (drawnScene.isNeighborDiscovered(Dir.S) || Global.TestMode))
                                g.drawLine(wX, dY+(y*sqH)+(sqH-1), wX+2, dY+(y*sqH)+(sqH-1));
                        }
                        for (int v = 0; v < 10; v++)
                        {
                            int wY = dY+(y*sqH)+cr+(v*2);
                            if (drawnScene.getWallSection(Dir.W)[v] && (drawnScene.isNeighborDiscovered(Dir.W) || Global.TestMode))
                                g.drawLine(dX+(x*sqW), wY, dX+(x*sqW), wY+1);
                            if (drawnScene.getWallSection(Dir.E)[v] && (drawnScene.isNeighborDiscovered(Dir.E) || Global.TestMode))
                                g.drawLine(dX+(x*sqW)+(sqW-1), wY, dX+(x*sqW)+(sqW-1), wY+1);
                        }

                        if (drawnScene.getCornerWallSection(Dir.NW))
                        {
                            if (drawnScene.isNeighborDiscovered(Dir.N) || drawnScene.isNeighborDiscovered(Dir.W) || Global.TestMode)                            
                                Global.drawDot(g, dX+(x*sqW), dY+(y*sqH));
                        }
                        if (drawnScene.getCornerWallSection(Dir.NE))
                        {
                            if (drawnScene.isNeighborDiscovered(Dir.N) || drawnScene.isNeighborDiscovered(Dir.E) || Global.TestMode)                            
                                Global.drawDot(g, dX+(x*sqW)+(sqW-1), dY+(y*sqH));
                        }
                        if (drawnScene.getCornerWallSection(Dir.SW))
                        {     
                            if (drawnScene.isNeighborDiscovered(Dir.S) || drawnScene.isNeighborDiscovered(Dir.W) || Global.TestMode)                            
                                Global.drawDot(g, dX+(x*sqW), dY+(y*sqH)+(sqH-1));                        
                        }
                        if (drawnScene.getCornerWallSection(Dir.SE))
                        {    
                            if (drawnScene.isNeighborDiscovered(Dir.S) || drawnScene.isNeighborDiscovered(Dir.E) || Global.TestMode)                            
                                Global.drawDot(g, dX+(x*sqW)+(sqW-1), dY+(y*sqH)+(sqH-1));
                        }
                    }
                    // If the Scene represents an objective:
                    if (drawnScene.isObjectified())
                    {
                        g.drawImage(getLoadedImage("interface/map/map_objective.png"), dX+(x*sqW), dY+(y*sqH), null);
                    }

                    // If the Scene is the current one:
                    if (ourCurrentScene == drawnScene)//ourCurrentMap.getScene(x,y, ourCurrentScene.getFloor()))
                    {                            
                        g.drawImage(getLoadedImage("interface/map/map_current.png"), dX+(x*sqW), dY+(y*sqH), null);
                    }
                }
            }
        }        
        @Override
        public void keyTyped(KeyEvent ke) {
            
        }
        @Override
        public void keyPressed(KeyEvent ke) {
            
        }
        @Override
        public void keyReleased(KeyEvent ke) {
            
        }
        @Override
        public void switchTo() {
            
        }
        @Override
        public void switchFrom() {            
        }
    };
    public final static GameMode       BattleMode = new GameMode() {
        private BufferedImage bgImg = null;
        @Override
        public String getName() {
            return "Battle";
        }
        @Override
        public void switchTo() {
            if (ourCurrentScene.getTerrain() == Terrain.STRONGHOLD)
                bgImg = getLoadedImage("bg/stronghold_enc_bg.png");
            if (ourCurrentScene.getTerrain() == Terrain.PLAIN)
                bgImg = getLoadedImage("bg/grass_enc_bg.png");
            if (ourCurrentScene.getTerrain() == Terrain.FOREST)
                bgImg = getLoadedImage("bg/forest_enc_bg.png");
            if (ourCurrentScene.getTerrain() == Terrain.DESERT)
                bgImg = getLoadedImage("bg/desert_enc_bg.png");
            if (ourCurrentScene.getTerrain() == Terrain.MOUNTAIN)
                bgImg = getLoadedImage("bg/mount_enc_bg.png");
            if (ourCurrentScene.getTerrain() == Terrain.TUNDRA)
                bgImg = getLoadedImage("bg/tundra_enc_bg.png");
            if (ourCurrentScene.getTerrain() == Terrain.SWAMP)
                bgImg = getLoadedImage("bg/swamp_enc_bg.png");
            ourEnemyPortrait.setVisible(true);
            
            ourCurrentComponentGroups.remove(ourMainComponents);
            ourCurrentComponentGroups.add(ourBattleComponents);
            ourCurrentComponentGroups.add(ourBattleReferenceComponents);
            ourCurrentComponentGroups.add(ourBattleEvaluationComponents);            
            ourBattleReferenceComponents.setVisible(false);
            ourVerseTextLabel.setVisible(false);
            
            Global.setBackgroundMusic("onward");
            
            doEnemyBanter();
        }
        @Override
        public void switchFrom() {   
            ourCurrentComponentGroups.remove(ourBattleComponents);
            ourCurrentComponentGroups.remove(ourBattleReferenceComponents);
            ourCurrentComponentGroups.remove(ourBattleEvaluationComponents);
            ourCurrentComponentGroups.add(ourMainComponents);
        }
        @Override
        public void cycle() {

        }
        @Override
        public void draw(Graphics2D g) {  
            g.drawImage(bgImg, 0, 0, null);
        }
        @Override
        public void keyTyped(KeyEvent ke) {
            
        }
        @Override
        public void keyPressed(KeyEvent ke) {
            
        }
        @Override
        public void keyReleased(KeyEvent ke) {
            
        }
    };
    private static GameMode            ourCurrentGameMode = NullMode;
    

    public static void initGameplay()
    {
        Global.initGlobalSettings();
        
        // The gift book must be withdrawn before the others are randomly
        // withdrawn during world creation.
        ourGiftBook = Global.withdrawBookByName(STARTING_BOOK);
        
        ourWorldMap = new WorldMap(); 
        
        changeGameMode(TitleMode);        
    }
    
    public static ArrayList<ComponentGroup> getCurrentComponents()
    {
        return ourCurrentComponentGroups;
    }
    
    public static JFrame getMainFrame()
    {
        return ourMainFrame;
    }

    public static ScreenManager getScreenManager()
    {
        return ourScreenManager;
    }

    public static SoundManager getSoundManager()
    {
        return ourSoundManager;
    }

    public static void loadImageSet(String filename)
    {
        File setFile = getFile("gfx/sets/" + filename + ".set");
        String[] entries = ReadWriteTextFile.getContents(setFile).replace("\r", "").split("\n");
        for (int e = 0; e < entries.length; e++)
        {            
            if (!loadImageFromFile(entries[e]))
            {
                System.err.println("Failed to load image '" + entries[e] + "'.");
            }
        }
    }
    public static BufferedImage getLoadedImage(String filename)
    {
        if (ourLoadedImages.containsKey(filename))
        {
            return ourLoadedImages.get(filename);
        }
        else
        {
            System.err.println("Image '" + filename + "' is not loaded.");
            return null;
        }
    }
    public static boolean loadImageFromFile(String filename)
    {      
        if (!ourLoadedImages.containsKey(filename))
        {
            BufferedImage image = getImageFromFile(filename);
            ourLoadedImages.put(filename, image);
        }
        return true;
    }
    
    public static BufferedImage getImageFromFile(String filename)
    {
        try
        {
            return ImageIO.read(getFile("gfx/" + filename));
        } catch (IOException ex)
        {
            Logger.getLogger(GoodFight.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    public static BufferedImage getImageFromFile(File file)
    {
        try {
            return ImageIO.read(file);
        } catch (IOException ex) {
            Logger.getLogger(GoodFight.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public static Font getFontFile(String filename)
    {
        try {
            return Font.createFont(Font.PLAIN, getFile("fonts/" + filename));
        } catch (FontFormatException | IOException ex) {
            Logger.getLogger(GoodFight.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public static File getFile(String filename)
    {
        try {
            return new File("extern/" + filename);
        } catch (Exception ex){
            Logger.getLogger(GoodFight.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public static FileInputStream getFileInputStream(String filename)
    {
        try {
            return new FileInputStream(getFile(filename));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(GoodFight.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public static GameDifficulty getDifficulty()
    {
        return ourGameDifficulty;
    }
    
    public static Scene getCurrentScene()
    {
        return ourCurrentScene;
    }

    public static void addDrawing(Drawing d)
    {
        ourDrawings.add(d);
    }
    public static void removeDrawing(Drawing d)
    {
        ourDrawings.remove(d);
    }
    public static ArrayList<Drawing> getDrawings()
    {
        return ourDrawings;
    }

    public static void addSprite(Sprite s)
    {
        ourSprites.add(s);
    }
    public static void removeSprite(Sprite s)
    {
        ourSprites.remove(s);
    }
    public static ArrayList<Sprite> getSprites()
    {
        return ourSprites;
    }
    public static Sprite getActiveSpriteByScriptLabel(String scriptLabel)
    {
        String label = null;
        for (int s = 0; s < ourSprites.size(); s++)
        {
            if (ourSprites.get(s) instanceof Person)
            {
                Sprite sprite = ourSprites.get(s); 
                label = sprite.getScriptLabel();
                if (label != null && label.equalsIgnoreCase(scriptLabel))
                {
                    return sprite;
                }
            }
        }
        return null;
    }    
    
    public static Hero getSubject()
    {
        return ourHero;
    }

    public static Person getCompanion()
    {
        return ourCompanion;
    }
    
    public static void setPaused(boolean paused)
    {
        ourPaused = paused;
        //ourMidiPlayer.setPaused(ourPaused);        
        Global.pauseBackgroundMusic(ourPaused);
        if (ourPaused)
        {            
            playSoundEffect("pause");            
        }
        else
        {
            for (int s = 0; s < ourSprites.size(); s++)
            {
                ourSprites.get(s).unpause();
            }
        }
    }
    public static boolean isPaused()
    {
        return ourPaused;
    }

    public static void main(String[] args)
    {              
        try
        {
            init();
            initGameplay();
            mainLoop();            
            ourScreenManager.restoreScreen();
            TimingRegister.listAllEntries();
            System.exit(0);
        } catch (Exception ex)
        {
            Logger.getLogger(GoodFight.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


    private static void mainLoop()
    {
        long startTime = 0;
        long maxTime = 10;
        long elpTime = 0;        
        do
        {
            startTime = System.currentTimeMillis();
            mainCycle();
            elpTime = System.currentTimeMillis()-startTime;
            if (ourMainFrame.getCursor() == ourCursor)
            {
                ourMouseTimeout -= elpTime;
                if (ourMouseTimeout <= 0)
                {
                    ourMouseTimeout = 0;
                    ourMainFrame.setCursor(ourBlankCursor);
                }
            }
            if (elpTime < maxTime)
                Global.delay(maxTime-elpTime);
        }while (!ourStop);
    }

    private static void mainCycle()
    {
        // Get full-screen Graphics context:
        Graphics2D g = ourScreenManager.getGraphics();
        
        // Clear the screen, because we need to re-draw everything:
        clearScreen(g);
        
        // The current GameMode will cycle unless interrupted:
        // it is interrupted if there is a current special event that disables the normal cycle, or if there is a dialog event.
        boolean interrupted = (ourCurrentSpecialEvent == null) ? false : ourCurrentSpecialEvent.disablesNormalCycle();
        if (ourCurrentDialogEvent != null) interrupted = true;
        
        // Perform the custom cycle of the current GameMode:        
        if (!ourPaused && !interrupted)
        {
            ourCurrentGameMode.cycle();
        }     
        else if (!ourPaused)
        {
            for (int r = 0; r < ourSprites.size(); r++)
            {                         
                ourSprites.get(r).updateSpeech();
            }
        }
        
        // Perform a single iteration of every active SpecialEffect:
        for (int e = 0; e < ourSpecialEffects.size(); e++)
        {
            ourSpecialEffects.get(e).doIteration();
        }
        // If there is an active Dialog Event, it takes precedence over
        // any normal SpecialEvent. We will still draw the SpecialEvent later, but
        // we won't do its iteration here.
        if (ourCurrentDialogEvent != null)
        {
            ourCurrentDialogEvent.doIteration();
        }
        else
        {
            // If there is a SpecialEvent active, perform a single iteration of it:
            if (ourCurrentSpecialEvent != null)
            {
                ourCurrentSpecialEvent.doIteration();
            }
        }
        
        // Draw the current GameMode:
        ourCurrentGameMode.draw(g);
        // Draw every active SpecialEffect:
        for (int e = 0; e < ourSpecialEffects.size(); e++)
        {
            ourSpecialEffects.get(e).draw(g);
        }
        
        // Draw the current group of interface components:
        drawCurrentComponents(g);
        
        // If there is a universal fade, draw it here:
        if (ourUniversalFade > 0f)
        {
            g.setColor(new Color(0, 0, 0, ourUniversalFade * 255));
            g.fillRect(0, 0, SCREEN_W, SCREEN_H);
        }
        
        // If prayer mode is on, show only the conversation bubbles:
        if (ourPrayerMode)
        {
            g.setColor(Color.BLACK);
            g.fillRect(SCENE_X, SCENE_Y, SCENE_W, SCENE_H);
            for (int s = 0; s < ourSprites.size(); s++)
                ourSprites.get(s).renderSpeech((Graphics2D)g);
        }
        
        // If there is a SpecialEvent active, draw the SpecialEvent:
        if (ourCurrentSpecialEvent != null)
        {
            ourCurrentSpecialEvent.draw(g);
        }    
        
        // If there is a DialogEvent active, draw the DialogEvent:
        if (ourCurrentDialogEvent != null)
        {
            ourCurrentDialogEvent.draw(g);
        }
        
        // If there is an active special event that has finished...        
        if (ourCurrentSpecialEvent != null && ourCurrentSpecialEvent.isFinished())
        {
            // If the normal "Adventure" cycle has been disabled due to the current
            // SpecialEvent, all Sprites need to be unpaused so they don't go nuts.
            if (ourCurrentGameMode == AdventureMode && ourCurrentSpecialEvent.disablesNormalCycle() && ourCurrentDialogEvent == null)
            {
                // Treat the end of the event as an "unpause", which effectively
                // resets the timers on all Sprites.
                for (int s = 0; s < ourSprites.size(); s++)
                {
                    ourSprites.get(s).unpause();
                }
            }
            // Call the onFinish method of the current special event:
            ourCurrentSpecialEvent.onFinish();
            // Check isFinished() again before clearing to null, because the onFinish() method
            // may have triggered a new SpecialEvent which is now the current one. In that case,
            // we don't want to cancel the new one before it gets its first iteration!
            if (ourCurrentSpecialEvent.isFinished())
                ourCurrentSpecialEvent = null;
        }        
        
        // If there is an active dialog event that has finished...
        if (ourCurrentDialogEvent != null && ourCurrentDialogEvent.isFinished())
        {
            // If the normal "Adventure" cycle has been disabled due to the current
            // SpecialEvent, all Sprites need to be unpaused so they don't go nuts.
            if (ourCurrentGameMode == AdventureMode && (ourCurrentSpecialEvent == null || !ourCurrentSpecialEvent.disablesNormalCycle()))
            {
                // Treat the end of the event as an "unpause", which effectively
                // resets the timers on all Sprites.
                for (int s = 0; s < ourSprites.size(); s++)
                {
                    ourSprites.get(s).unpause();
                }
            }
            // Call the onFinish method of the current special event:
            ourCurrentDialogEvent.onFinish();
            // Check isFinished() again before clearing to null, because the onFinish() method
            // may have triggered a new DialogEvent which is now the current one. In that case,
            // we don't want to cancel the new one before it gets its first iteration!
            if (ourCurrentDialogEvent.isFinished())
                ourCurrentDialogEvent = null;
        }
        
        if (Global.TestMode)
        {
            int mb = 1024*1024;
            Runtime runtime = Runtime.getRuntime();
            long used = (runtime.totalMemory() - runtime.freeMemory()) / mb;
            long free = runtime.freeMemory() / mb;
            long total = runtime.totalMemory() / mb;
            long max = runtime.maxMemory() / mb;
            g.setColor(Color.WHITE);
            g.setFont(new Font("Lucida Console", Font.PLAIN, 12));
            g.drawString(used + "/" + total + " of " + max, SCENE_LEFT, SCENE_TOP + (SCENE_H-100));
        }       
        
        // If the game is paused, draw a Pause indicator over the top of everything:
        if (ourPaused)
        {
            drawPaused(g);
        }
        
        // Dispose the Graphics context:
        g.dispose();
        
        // Update the screen with everything we just did:
        ourScreenManager.update();
    }

    public static void clearScreen(Graphics2D g)
    {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, SCREEN_W, SCREEN_H);
    }
    
    public static void changeGameMode(GameMode newMode)
    {
        // Call any custom "switch from" code of the currently-running mode:
        ourCurrentGameMode.switchFrom();        
        // Call any custom "switch to" code of the requested mode:
        newMode.switchTo();        
        // Assign the current GameMode pointer to the requested mode:
        ourCurrentGameMode = newMode;        
        Global.log("Change GameMode to " + newMode.getName());
    }
    
    public static void setSpecialEvent(SpecialEvent event)
    {
        ourCurrentSpecialEvent = event;
        ourCurrentSpecialEvent.start();
    }
    public static void setDialogSpecialEvent(SpecialEvent dialogEvent)
    {
        clearKeyMap();
        ourCurrentDialogEvent = dialogEvent;
        ourCurrentDialogEvent.start();
    }    
    public static boolean isDialogOpen()
    {
        return ourCurrentDialogEvent != null;
    }
    
    // SPECIAL EVENT METHODS ===================================================
    
    // Game Start:
    public static void doMainMenu()
    {
        setDialogSpecialEvent(new SpecialEvent() {
            String[] choices = {"New Game", "Restore Game", "Duel Mode", "Top Scores", "Concept Glossary", "Options", "Credits", "Quit"};
            String[] descriptions = {"Begin a new game as our hero, Adam Cesar.", "Continue a previously saved game.", "Share a friendly head-to-head scripture battle with a friend.", "View the list of top 10 scores.", "See how game concepts were derived from the Bible.", "Configure options for audio, text, and gameplay.", "View a list of people who worked hard to create this game.", "The grace of our Lord Jesus Christ be with your spirit. Amen."};
            Menu mainMenu = new Menu("Main Menu", choices, descriptions);
            @Override
            public void start() {                  
                ourSpecialEventComponents.addComponent(mainMenu);
                ourCurrentComponentGroups.add(ourSpecialEventComponents);
                Global.setBackgroundMusic("saints");
            }
            @Override
            public void doIteration() {
                
            }
            @Override
            public boolean isFinished() {
                return mainMenu.getChoice() != null;
            }
            @Override
            public boolean disablesNormalCycle() {
                return true;
            }
            @Override
            public void draw(Graphics g) {
                
            }   
            @Override
            public void onFinish() {                
                playSoundEffect("success");
                ourCurrentComponentGroups.remove(ourSpecialEventComponents);
                ourSpecialEventComponents.clear();
                switch((Integer)mainMenu.getChoice())
                {
                    case 0:                                                
                        Global.delay(500);
                        doDifficultyMenu();
                        break;
                    case 7:                                                
                        ourStop = true;
                        break;
                }
            }

            @Override
            public boolean useKeyPress(KeyEvent e) {
                return false;
            }            
        });
    }
    public static void doDifficultyMenu()
    {
        setDialogSpecialEvent(new SpecialEvent() {
            String[] choices = {"Babe", "Disciple", "Soldier"};
            String[] descriptions = {"Out of the mouth of babes and sucklings thou hast perfected praise", "If ye continue in my word, then are ye my disciples indeed", "Thou therefore endure hardness, as a good soldier of Jesus Christ"};
            Menu diffMenu = new Menu("Select Difficulty:", choices, descriptions);
            @Override
            public void start() {                  
                ourSpecialEventComponents.addComponent(diffMenu);
                ourCurrentComponentGroups.add(ourSpecialEventComponents);
            }
            @Override
            public void doIteration() {
                
            }
            @Override
            public boolean isFinished() {
                return diffMenu.getChoice() != null;
            }
            @Override
            public boolean disablesNormalCycle() {
                return true;
            }
            @Override
            public void draw(Graphics g) {
                
            }   
            @Override
            public void onFinish() {                
                switch((Integer)diffMenu.getChoice())
                {
                    case 0:                                                
                        ourGameDifficulty = BABE_DIFFICULTY;
                        playSoundEffect("amen2");
                        break;
                    case 1:                                                
                        ourGameDifficulty = DISCIPLE_DIFFICULTY;
                        playSoundEffect("amen1");
                        break;
                    case 2:                                                
                        ourGameDifficulty = SOLDIER_DIFFICULTY;
                        playSoundEffect("amen3");
                        break;
                }
                Global.initializeEnemyList();
                doMenuFade(new CodeDelegate(){
                    @Override
                    public void run() {
                        ourCurrentComponentGroups.remove(ourSpecialEventComponents);
                        ourSpecialEventComponents.clear();
                        Global.delay(2000);
                        changeGameMode(AdventureMode);
                        doOpeningCutscene();
                    }
                });
            }

            @Override
            public boolean useKeyPress(KeyEvent e) {
                return false;
            }            
        });
    }
    public static void doMenuFade(final CodeDelegate postCode)
    {
        setSpecialEvent(new SpecialEvent(){
            private double fade = 0;
            private double inc = .1;
            @Override
            public void start() {
                
            }

            @Override
            public void onFinish() {
                TimingRegister.remove("fade_menu");  
                postCode.run();
            }

            @Override
            public void doIteration() {
                Global.fadeBackgroundMusic(-.12f);
                if (inc > 0)
                {
                    fade = TimingRegister.update("fade_menu", fade, inc);
                    if (fade >= 255)
                    {
                        fade = 255;
                        inc = 0;
                    }
                }
            }

            @Override
            public boolean isFinished() {
                return inc == 0;
            }

            @Override
            public boolean disablesNormalCycle() {
                return true;
            }

            @Override
            public boolean useKeyPress(KeyEvent e) {
                return false;
            }
            @Override
            public void draw(Graphics g) {
                g.setColor(new Color(0, 0, 0, (int)fade));
                g.fillRect(0, 0, SCREEN_W, SCREEN_H);
            }
        });
    }
    public static void doOpeningCutscene()
    {        
        Church startingChurch = Global.getStartingChurch();
        ourCurrentMap = startingChurch.getMap();        
        ourHero = new Hero();//ExpHero();
        ourHero.setControlled(false);
        addSprite(ourHero);    
        setCurrentScene(startingChurch.getBuilding().getEntranceRoomScene(), new Point(543, 535), new Point(SCENE_CTR_X+30, SCENE_CTR_Y));
        ourHero.faceDirection(Dir.N);
        ourHero.setMode(Person.PersonMode.STILL);  
        startingChurch.assignToSeat(ourHero, Church.ReservedSeatNum);
        startingChurch.prepareForService();
        
        // TEST CODE:
        String[] lines = {"$`Adam`Hi!", "$`Conversant`This is a specially-queued conversation. But hello, anyway."};
        Person testTalker = startingChurch.getRandomMember();
        testTalker.queueUpConversation(new Conversation(lines, null, testTalker));
        System.out.println("***** " + testTalker.getFullName() + " has a queued test conversation.");
        //ourHero.addExp(6);
        // END TEST CODE.
        
        setSpecialEvent(new SpecialEvent() {
            private double inc = .2;
            private double fade = 0;            
            @Override
            public void start() {                
            }
            @Override
            public void doIteration() {
                if (inc > 0)
                {
                    fade = TimingRegister.update("fade_in", fade, inc);
                    if (fade >= 255)
                    {
                        fade = 255;
                        inc = 0;
                        
                    }
                }
            }
            @Override
            public boolean isFinished() {
                return inc == 0;
            }
            @Override
            public boolean disablesNormalCycle() {
                return true;
            }
            @Override
            public void draw(Graphics g) {
                g.setColor(new Color(0, 0, 0, 255-((int)fade)));
                g.fillRect(SCENE_X, SCENE_Y, SCENE_W, SCENE_H);
            }   
            @Override
            public void onFinish() {
                TimingRegister.remove("fade_in");
                doStartConversation("opening", new CodeDelegate() {
                    @Override
                    public void run() {
                        doOpeningPrayer();
                    }
                });
            }
            @Override
            public boolean useKeyPress(KeyEvent e) {
                return false;
            }
        });  
    }    
    public static void doOpeningPrayer()
    {
        setSpecialEvent(new SpecialEvent() {
            private double inc = .2;
            private double fade = 0;            
            @Override
            public void start() {                
            }
            @Override
            public void doIteration() {
                if (inc > 0)
                {
                    fade = TimingRegister.update("fade_out", fade, inc);                    
                    if (fade >= 255)
                    {
                        fade = 255;
                        inc = 0;                        
                    }
                }
            }
            @Override
            public boolean isFinished() {
                return inc == 0;
            }
            @Override
            public boolean disablesNormalCycle() {
                return true;
            }
            @Override
            public void draw(Graphics g) {
                g.setColor(new Color(0, 0, 0, (int)fade));
                g.fillRect(SCENE_X, SCENE_Y, SCENE_W, SCENE_H);
            }   
            @Override
            public void onFinish() {                
                TimingRegister.remove("fade_out");
                doStartConversation("prayer", new CodeDelegate() {
                    @Override
                    public void run() {
                        doPrologueCutscene();
                    }
                });
            }
            @Override
            public boolean useKeyPress(KeyEvent e) {
                return false;
            }
        }); 
    }   
    public static void doPrologueCutscene()
    {        
        Global.getStartingChurch().clearSeats();
        ourHero.shutUp();
        stopPrayer();        
        ourHero.faceDirection(Dir.E);
        ourHero.setCenterPoint(new Point(497, 500));

        Person thePreacher = (Person)getActiveSpriteByScriptLabel("Preacher");
        thePreacher.setCenterPoint(new Point(525, 500));
        thePreacher.faceDirection(Dir.W);
        
        setSpecialEvent(new SpecialEvent() {
            private double paused = 1500;
            private double inc = .1;
            private double fade = 0;            
            @Override
            public void start() {                
            }
            @Override
            public void doIteration() {
                if (paused > 0)
                {
                    paused = TimingRegister.update("paused", paused, -1);
                }
                else if (inc > 0)
                {
                    fade = TimingRegister.update("fade_in", fade, inc);
                    if (fade >= 255)
                    {
                        fade = 255;
                        inc = 0;
                    }
                }
                
            }
            @Override
            public boolean isFinished() {
                return inc == 0;
            }
            @Override
            public boolean disablesNormalCycle() {
                return true;
            }
            @Override
            public void draw(Graphics g) {
                g.setColor(new Color(0, 0, 0, 255-((int)fade)));
                g.fillRect(SCENE_X, SCENE_Y, SCENE_W, SCENE_H);
            }   
            @Override
            public void onFinish() {
                TimingRegister.remove("fade_in");
                TimingRegister.remove("paused");
                Global.delay(800);
                doStartConversation("prologue", new CodeDelegate() {
                    @Override
                    public void run() {                        
                        doGameplayStart();
                    }
                });
            }
            @Override
            public boolean useKeyPress(KeyEvent e) {
                return false;
            }
        });  
    }    
    public static void doAwardSword()
    {
        new Sword().onAcquire();
    }
    public static void doAwardGiftBook()
    {        
        ourGiftBook.onAcquire();
        setBookEnabled(STARTING_BOOK, true); 
    }    
    public static void doGameplayStart()
    {  
        ourHero.setControlled(true);
        setSpecialEvent(new SpecialEvent() {
            private double inc = .5;
            private double fade = 0;           
            @Override
            public void start() {           
            }
            @Override
            public void doIteration() {
                if (inc > 0)
                {
                    fade = TimingRegister.update("scene_trans", fade, inc);
                    if (fade >= 255)
                    {
                        // Ensure that the fade is a valid value:
                        fade = 255;
                        
                        // Halfway through - this is where the scene actually changes:
                        Global.getStartingChurch().closeService();
                        Point p = Global.getStartingChurch().getBuilding().getOuterPortal().getExitPosition();
                        setCurrentScene(Global.getStartingChurch().getBuilding().getContainingScene(), p, null); 
                        objectifyNearestPremiumChest();
                        ourHero.setControlled(true);
                        ourHero.faceDirection(Dir.S);
                        // Reverse the increment to fade back in:
                        inc = -.5;
                    }
                }
                else if (inc < 0)
                {
                    fade = TimingRegister.update("scene_trans", fade, inc);
                    if (fade <= 0)
                    {
                        fade = 0;
                        inc = 0;
                    }
                }
            }
            @Override
            public boolean isFinished() {
                return inc == 0;
            }
            @Override
            public boolean disablesNormalCycle() {
                return true;
            }
            @Override
            public void draw(Graphics g) {
                g.setColor(new Color(0, 0, 0, (int)fade));
                g.fillRect(SCENE_X, SCENE_Y, SCENE_W, SCENE_H);
            }   
            @Override
            public void onFinish() {  
                TimingRegister.remove("scene_trans");
            }
            @Override
            public boolean useKeyPress(KeyEvent e) {
                return false;
            }
        });       
    }
    
    // Level up:
    public static void doLevelUp()
    {
        ourCurrentComponentGroups.remove(ourSpecialEventComponents);
        ourSpecialEventComponents.clear();

        setSpecialEvent(new SpecialEvent() {
            private double inc = .2;
            private double fade = 0;           
            @Override
            public void start() {   
                playSoundEffect("hallelujah");
            }
            @Override
            public void doIteration() {
                if (inc > 0)
                {
                    fade = TimingRegister.update("fade", fade, inc);
                    if (fade >= 255)
                    {
                        // Ensure that the fade is a valid value:
                        fade = 255;                        
                        // Increase the player level:
                        ourHero.increaseLevel();                        
                        // Reverse the increment to fade back in:
                        inc = -inc;
                    }
                }
                else if (inc < 0)
                {
                    fade = TimingRegister.update("fade", fade, inc);
                    if (fade <= 0)
                    {
                        fade = 0;
                        inc = 0;
                    }
                }
            }
            @Override
            public boolean isFinished() {
                return inc == 0;
            }
            @Override
            public boolean disablesNormalCycle() {
                return true;
            }
            @Override
            public void draw(Graphics g) {
                g.setColor(new Color(255, 255, 255, (int)fade));
                g.fillRect(SCENE_X, SCENE_Y, SCENE_W, SCENE_H);
            }   
            @Override
            public void onFinish() {  
                TimingRegister.remove("fade");
                setDialogSpecialEvent(new SpecialEvent() {
                    private boolean amen = false;                    
                    private Popup pop = Popup.makeSimplePopup("You are now level " + ourHero.getLevel() + "!", "Level Up!");
                    private SimpleButton button = new SimpleButton(new Point(256, 120), "Amen!", null) {
                        @Override
                        protected void mouseReleaseAction(MouseEvent e, boolean within) {
                            super.mouseReleaseAction(e, within);
                            amen = true;                                       
                        }           
                    };           
                    @Override
                    public void start() {  
                        pop.addComponent(button);
                        ourSpecialEventComponents.addComponent(pop);
                        ourCurrentComponentGroups.add(ourSpecialEventComponents);
                        playSoundEffect("hallelujah");
                    }
                    @Override
                    public void doIteration() {

                    }
                    @Override
                    public boolean isFinished() {
                        return amen;
                    }
                    @Override
                    public boolean disablesNormalCycle() {
                        return true;
                    }
                    @Override
                    public void draw(Graphics g) {

                    }   
                    @Override
                    public void onFinish() {
                        int a = Global.getRandomInt(1, 3); 
                        playSoundEffect("amen" + a + "");
                        ourCurrentComponentGroups.remove(ourSpecialEventComponents);
                        ourSpecialEventComponents.clear();
                    }
                    @Override
                    public boolean useKeyPress(KeyEvent e) {
                        if (e.getKeyCode() == KeyEvent.VK_ENTER)
                        {
                            amen = true;
                            return true;
                        }
                        else
                            return false;
                    }
                });
            }
            @Override
            public boolean useKeyPress(KeyEvent e) {
                return false;
            }
        });
    }
    
    // Conversation:
    public static void doStartConversation(final String convName, CodeDelegate postCode)
    {        
        // This method is for scripted Conversations.
        Conversation script = new Conversation(convName, postCode); 
        setSpecialEvent(script.getNextConversationEvent());
    }
    public static void doStartConversation(Conversation conv, CodeDelegate postCode)
    {
        // This method is for random or custom conversations.
        conv.setPostCode(postCode);
        setSpecialEvent(conv.getNextConversationEvent());
    }
    public static void doStartRandomSermon()
    {        
        String sermonName = (String)Global.getRandomFromList(Global.Sermons);
        ChurchSermon script = new ChurchSermon(sermonName); 
        setSpecialEvent(script.getNextConversationEvent());
    }
    
    // Scene fading:
    public static void doFadeInOpeningScene()
    {
        setSpecialEvent(new SpecialEvent() {
            private double inc = .5;
            private double fade = 0;            
            @Override
            public void start() {                
            }
            @Override
            public void doIteration() {
                if (inc > 0)
                {
                    fade = TimingRegister.update("fade_in", fade, inc);
                    if (fade >= 255)
                    {
                        fade = 255;
                        inc = 0;
                    }
                }
            }
            @Override
            public boolean isFinished() {
                return inc == 0;
            }
            @Override
            public boolean disablesNormalCycle() {
                return true;
            }
            @Override
            public void draw(Graphics g) {
                g.setColor(new Color(0, 0, 0, 255-((int)fade)));
                g.fillRect(SCENE_X, SCENE_Y, SCENE_W, SCENE_H);
            }   
            @Override
            public void onFinish() {
                TimingRegister.remove("fade_in");
            }
            @Override
            public boolean useKeyPress(KeyEvent e) {
                return false;
            }
        });        
    }
    public static void doTransitionToScene(final Scene scene, final Point playerEntryPoint, final Point companionEntryPoint)
    {
        setSpecialEvent(new SpecialEvent() {
            private double inc = .5;
            private double fade = 0;           
            @Override
            public void start() {           
            }
            @Override
            public void doIteration() {
                if (inc > 0)
                {
                    fade = TimingRegister.update("scene_trans", fade, inc);
                    if (fade >= 255)
                    {
                        // Ensure that the fade is a valid value:
                        fade = 255;
                        
                        // Halfway through - this is where the scene actually changes:
                        setCurrentScene(scene, playerEntryPoint, companionEntryPoint);
                        
                        // Reverse the increment to fade back in:
                        inc = -.5;
                    }
                }
                else if (inc < 0)
                {
                    fade = TimingRegister.update("scene_trans", fade, inc);
                    if (fade <= 0)
                    {
                        fade = 0;
                        inc = 0;
                    }
                }
            }
            @Override
            public boolean isFinished() {
                return inc == 0;
            }
            @Override
            public boolean disablesNormalCycle() {
                return true;
            }
            @Override
            public void draw(Graphics g) {
                g.setColor(new Color(0, 0, 0, (int)fade));
                g.fillRect(SCENE_X, SCENE_Y, SCENE_W, SCENE_H);
            }   
            @Override
            public void onFinish() {  
                TimingRegister.remove("scene_trans");
            }
            @Override
            public boolean useKeyPress(KeyEvent e) {
                return false;
            }
        });        
    }
    public static void doEnterPortal(final Portal portal)
    {
        setSpecialEvent(new SpecialEvent() {
            private double inc = .5;
            private double fade = 0;           
            @Override
            public void start() {           
            }
            @Override
            public void doIteration() {
                if (inc > 0)
                {
                    fade = TimingRegister.update("scene_trans", fade, inc);
                    if (fade >= 255)
                    {
                        // Ensure that the fade is a valid value:
                        fade = 255;
                        
                        // Halfway through - this is where the scene actually changes:
                        ourHero.faceDirection(portal.getExitPortal().getExitDirection());
                        setCurrentScene(portal.getExitPortal().getScene(), portal.getExitPortal().getExitPosition(), portal.getExitPortal().getExitPosition());
                        
                        // Reverse the increment to fade back in:
                        inc = -.5;
                    }
                }
                else if (inc < 0)
                {
                    fade = TimingRegister.update("scene_trans", fade, inc);
                    if (fade <= 0)
                    {
                        fade = 0;
                        inc = 0;
                    }
                }
            }
            @Override
            public boolean isFinished() {
                return inc == 0;
            }
            @Override
            public boolean disablesNormalCycle() {
                return true;
            }
            @Override
            public void draw(Graphics g) {
                g.setColor(new Color(0, 0, 0, (int)fade));
                g.fillRect(SCENE_X, SCENE_Y, SCENE_W, SCENE_H);
            }   
            @Override
            public void onFinish() {  
                TimingRegister.remove("scene_trans");
                if (ourCurrentScene.getTerrain() == Terrain.CHURCH)
                {
                    doEnterChurch();
                }
            }
            @Override
            public boolean useKeyPress(KeyEvent e) {
                return false;
            }
        });        
    }
    public static void doEnterChurch()
    {
        final Church church = ((ChurchMap)ourCurrentScene.getMap()).getBuilding().getChurch();
        if (!church.isInService())
        {
            // No service:
        }
        else
        {            
            // In service: take a seat and begin the sermon:
            ourHero.setControlled(false);
            setSpecialEvent(new SpecialEvent() {
                private boolean done = false;
                private double delay = 1000.0;
                private WalkCommand cmd1 = null;
                private WalkCommand cmd2 = null;
                @Override
                public void start() {   
                    cmd1 = new WalkCommand(ourHero, 83, Dir.N)
                    {                       
                        @Override
                        public void onStop() {
                            ourHero.stop();
                            cmd2 = new WalkCommand(ourHero, 43, Dir.E)
                            {
                                @Override
                                public void onStop() {
                                    ourHero.stop();
                                    ourHero.faceDirection(Dir.N);
                                    church.assignToSeat(ourHero, Church.ReservedSeatNum);
                                }                          
                            };
                            ourHero.setCommand(cmd2);
                        }                          
                    };
                    ourHero.setCommand(cmd1);
                }
                @Override
                public void doIteration() {
                    
                    if (Global.getBackgroundMusicVolume()>-10f)
                    {
                        Global.fadeBackgroundMusic(-.08f);
                    }
                    else
                    {
                        Global.pauseBackgroundMusic(true);
                        delay = TimingRegister.update("delay", delay, -1.0);
                    }
                    done = (cmd2 != null && cmd2.isDone() && delay <= 0);
                }
                @Override
                public boolean isFinished() {
                    return done;
                }
                @Override
                public boolean disablesNormalCycle() {
                    return false;
                }
                @Override
                public void draw(Graphics g) {
                    
                }   
                @Override
                public void onFinish() {          
                    TimingRegister.remove("delay");
                    doStartRandomSermon();
                }
                @Override
                public boolean useKeyPress(KeyEvent e) {
                    return false;
                }
            });   
        }
    }
    public static void doExitChurch()
    {
        Church church = ((ChurchMap)ourCurrentScene.getMap()).getBuilding().getChurch();
        ourHero.setControlled(true);
        doEnterPortal(church.getBuilding().getInnerPortal());        
    }
    
    
    // Encounter events:
    public static void doEncounterEnemy(final Enemy enemy)
    {                
        setSpecialEvent(new SpecialEvent() {            
            private BufferedImage img = null;
            private boolean reverseW = false;
            private boolean reverseH = false;
            private double timeUntilCenter = 1500;
            private double wRotations = 5.0;           
            private double rotateWInc = 1.0 / (int)(timeUntilCenter/wRotations);
            private double rotateWidth = 0;
            private double hRotations = 4.0;           
            private double rotateHInc = 1.0 / (int)(timeUntilCenter/hRotations);
            private double rotateHeight = 1;
            
            private double scale = 0;
            private double xCtr = enemy.getCtrX();
            private double yCtr = enemy.getCtrY();
            private double scaleInc = (1.0 - scale) / timeUntilCenter;
            private double xInc = ((SCREEN_W/2) - xCtr) / timeUntilCenter;
            private double yInc = ((SCREEN_H/2) - yCtr) / timeUntilCenter;
            // A "lastUpdate" variable is normally unnecessary because of
            // TimingRegister class; but this rotation case requires special handling:
            private long lastUpdate = System.currentTimeMillis();
            
            @Override
            public void start() {  
                if (ourCurrentScene.getTerrain() == Terrain.STRONGHOLD)
                    img = getLoadedImage("bg/stronghold_enc_bg.png");
                if (ourCurrentScene.getTerrain() == Terrain.PLAIN)
                    img = getLoadedImage("bg/grass_enc_bg.png");
                if (ourCurrentScene.getTerrain() == Terrain.FOREST)
                    img = getLoadedImage("bg/forest_enc_bg.png");
                if (ourCurrentScene.getTerrain() == Terrain.DESERT)
                    img = getLoadedImage("bg/desert_enc_bg.png");
                if (ourCurrentScene.getTerrain() == Terrain.MOUNTAIN)
                    img = getLoadedImage("bg/mount_enc_bg.png");
                if (ourCurrentScene.getTerrain() == Terrain.TUNDRA)
                    img = getLoadedImage("bg/tundra_enc_bg.png");
                if (ourCurrentScene.getTerrain() == Terrain.SWAMP)
                    img = getLoadedImage("bg/swamp_enc_bg.png");                
                
                ourCurrentEnemy = enemy;
                setCombatActionText("Encountered " + ourCurrentEnemy.getName() + " the " + ourCurrentEnemy.getClass().getSimpleName() + "!");
                Global.pauseBackgroundMusic(true);
                clearCombatActionText();
                playSoundEffect("encounter");                
            }
            @Override
            public void doIteration() {
                long now = System.currentTimeMillis();
                long elapsed = now - lastUpdate;
                xCtr = TimingRegister.update("xCtr", xCtr, xInc);
                yCtr = TimingRegister.update("yCtr", yCtr, yInc);
                scale = TimingRegister.update("scale", scale, scaleInc);
                  
                rotateWidth += (rotateWInc*elapsed);
                if (rotateWInc > 0 && rotateWidth >= 1.0)
                {
                    rotateWidth = 1.0 - (rotateWidth-1.0);
                    rotateWInc = -rotateWInc;
                }
                else if (rotateWInc < 0 && rotateWidth <= 0.0)
                {
                    reverseW = !reverseW;
                    rotateWidth = 0.0 + (0-rotateWidth);
                    rotateWInc = -rotateWInc;
                }                    
                
                rotateHeight += (rotateHInc*elapsed);
                if (rotateHInc > 0 && rotateHeight >= 1.0)
                {
                    rotateHeight = 1.0 - (rotateHeight-1.0);
                    rotateHInc = -rotateHInc;
                }
                else if (rotateHInc < 0 && rotateHeight <= 0.0)
                {
                    reverseH = !reverseH;
                    rotateHeight = 0.0 + (0-rotateHeight);
                    rotateHInc = -rotateHInc;
                }                    
                
                lastUpdate = now;
            }
            @Override
            public boolean isFinished() {                
                return scale >= 1.0;
            }
            @Override
            public boolean disablesNormalCycle() {
                return true;
            }
            @Override
            public void draw(Graphics g) {
                int w = (int)((img.getWidth()*scale)*rotateWidth);
                int h = (int)((img.getHeight()*scale)*rotateHeight);
                int x = (int)(xCtr-(w/2));
                int y = (int)(yCtr-(h/2));
                if (reverseW)
                {
                    x = (int)(xCtr+(w/2));
                    w = -w;                    
                }
                if (reverseH)
                {
                    y = (int)(yCtr+(h/2));
                    h = -h;                    
                }
                g.drawImage(img, x, y, w, h, null);
            }   
            @Override
            public void onFinish() {
                TimingRegister.remove("xCtr");
                TimingRegister.remove("yCtr");
                TimingRegister.remove("scale");
                GoodFight.changeGameMode(GoodFight.BattleMode);
            }
            @Override
            public boolean useKeyPress(KeyEvent e) {
                return false;
            }
        });
    }
    public static void doEnemyBanter()
    {
        if (true)
            setSpecialEvent(new SpecialEvent() {            
                private double time = 1000.0;
                @Override
                public void start() {
                }
                @Override
                public void doIteration() {
                    time = TimingRegister.update("time", time, -1.0);
                }
                @Override
                public boolean isFinished() {                
                    return time <= 0;
                }
                @Override
                public boolean disablesNormalCycle() {
                    return true;
                }
                @Override
                public void draw(Graphics g) {

                }   
                @Override
                public void onFinish() {
                    TimingRegister.remove("time");
                    doServeRandomVerseFromEnabledBooks();
                }
                @Override
                public boolean useKeyPress(KeyEvent e) {
                    return false;
                }
            }); // Just a 1-sec delay.
        else
            setSpecialEvent(new SpecialEvent() {
            private double textDuration = 2000;
            private double wiggleUpdate = 0;
            private double enemyTaunt = 0.0;
            private double playerRetort = 0.0;
            private WigglingText enemyText = null;
            private WigglingText playerText = null;
            
            @Override
            public void start() {      
                enemyText = new WigglingText((String)Global.getRandomFromList(Global.EnemyTaunts), 1);
                playerText = new WigglingText((String)Global.getRandomFromList(Global.PlayerRetorts), 1);
            }
            @Override
            public void doIteration() {
                wiggleUpdate = TimingRegister.update("wiggle", wiggleUpdate, 1);
                if (wiggleUpdate >= 40)
                {
                    TimingRegister.remove("wiggle");
                    wiggleUpdate = 0;
                }
                if (enemyTaunt < textDuration)
                {
                    enemyTaunt = TimingRegister.update("enemy_taunt", enemyTaunt, 1);
                    if (wiggleUpdate == 0)
                        enemyText.wiggle();
                }
                else
                {
                    playerRetort = TimingRegister.update("player_retort", playerRetort, 1);
                    if (wiggleUpdate == 0)
                        playerText.wiggle();
                }
            }
            @Override
            public boolean isFinished() {                
                return playerRetort >= textDuration;
            }
            @Override
            public boolean disablesNormalCycle() {
                return true;
            }
            @Override
            public void draw(Graphics g) {
                Graphics2D g2 = (Graphics2D)g;
                g.setFont(Global.GreekMedFont);
                g.setColor(Color.WHITE);
                if (enemyTaunt < textDuration)
                {
                    enemyText.draw(800, 260, g2);
                }
                else
                {
                    playerText.draw(20, 260, g2);
                }
            }   
            @Override
            public void onFinish() {
                TimingRegister.remove("enemy_taunt");
                TimingRegister.remove("player_retort");
                TimingRegister.remove("wiggle");
                doServeRandomVerseFromEnabledBooks();
            }
            @Override
            public boolean useKeyPress(KeyEvent e) {
                return false;
            }
        }); // A trial "banter" routine.
    }
    public static void doDefeatEnemy()
    {
        // Remove the current Enemy from ourSprites:
        ourSprites.remove(ourCurrentEnemy);
        setSpecialEvent(new SpecialEvent() {
            private double inc = .5;
            private double fade = 0;           
            @Override
            public void start() {           
            }
            @Override
            public void doIteration() {
                if (inc > 0)
                {
                    fade = TimingRegister.update("fade", fade, inc);
                    if (fade >= 255)
                    {
                        // Ensure that the fade is a valid value:
                        fade = 255;
                        
                       // Change the GameMode back to AdventureMode so the game can continue:
                        GoodFight.changeGameMode(AdventureMode);
                                        
                        // Give the Hero credit for the win:
                        ourHero.addExp(ourCurrentEnemy.getLevel());

                        // Level up the enemy, so he'll be stronger next time we meet him:
                        ourCurrentEnemy.levelUp();
                        ourCurrentEnemy.restoreResistance();

                        // Add the Enemy back to the Global pool:
                        Global.addEnemy(ourCurrentEnemy);

                        // Clear the current enemy:
                        ourCurrentEnemy = null;
                        
                        // Reverse the increment to fade back in:
                        inc = -.5;
                    }
                }
                else if (inc < 0)
                {
                    fade = TimingRegister.update("fade", fade, inc);
                    if (fade <= 0)
                    {
                        fade = 0;
                        inc = 0;
                    }
                }
            }
            @Override
            public boolean isFinished() {
                return inc == 0;
            }
            @Override
            public boolean disablesNormalCycle() {
                return true;
            }
            @Override
            public void draw(Graphics g) {
                g.setColor(new Color(0, 0, 0, (int)fade));
                if (inc > 0)
                    g.fillRect(0, 0, SCREEN_W, SCREEN_H);
                else
                    g.fillRect(SCENE_X, SCENE_Y, SCENE_W, SCENE_H);
            }   
            @Override
            public void onFinish() {  
                TimingRegister.remove("fade");
            }
            @Override
            public boolean useKeyPress(KeyEvent e) {
                return false;
            }
        });              
    }
    public static void doDefeatHero()
    {
        // Remove the current Enemy from ourSprites:
        ourSprites.remove(ourCurrentEnemy);
        setSpecialEvent(new SpecialEvent() {
            private double inc = .5;
            private double fade = 0;           
            @Override
            public void start() {           
            }
            @Override
            public void doIteration() {
                if (inc > 0)
                {
                    fade = TimingRegister.update("fade", fade, inc);
                    if (fade >= 255)
                    {
                        // Ensure that the fade is a valid value:
                        fade = 255;
                        
                        // Change the GameMode back to AdventureMode so the game can continue:
                        GoodFight.changeGameMode(AdventureMode);

                        // Restore the enemy's resistance for the next fight:
                        ourCurrentEnemy.restoreResistance();

                        // Add the Enemy back to the Global pool:
                        Global.addEnemy(ourCurrentEnemy);

                        // Clear the current enemy:
                        ourCurrentEnemy = null;
                        
                        // Reverse the increment to fade back in:
                        inc = -.5;
                    }
                }
                else if (inc < 0)
                {
                    fade = TimingRegister.update("fade", fade, inc);
                    if (fade <= 0)
                    {
                        fade = 0;
                        inc = 0;
                    }
                }
            }
            @Override
            public boolean isFinished() {
                return inc == 0;
            }
            @Override
            public boolean disablesNormalCycle() {
                return true;
            }
            @Override
            public void draw(Graphics g) {
                g.setColor(new Color(0, 0, 0, (int)fade));
                if (inc > 0)
                    g.fillRect(0, 0, SCREEN_W, SCREEN_H);
                else
                    g.fillRect(SCENE_X, SCENE_Y, SCENE_W, SCENE_H);
            }   
            @Override
            public void onFinish() {  
                TimingRegister.remove("fade");
            }
            @Override
            public boolean useKeyPress(KeyEvent e) {
                return false;
            }
        });        
        
        
        setSpecialEvent(new SpecialEvent() {
            private double inc = .5;
            private double fade = 0;           
            @Override
            public void start() {           
            }
            @Override
            public void doIteration() {
                if (inc > 0)
                {
                    fade = TimingRegister.update("fade", fade, inc);
                    if (fade >= 255)
                    {
                        // Ensure that the fade is a valid value:
                        fade = 255;
                        
                        // Change the GameMode back to AdventureMode so the game can continue:
                        GoodFight.changeGameMode(AdventureMode);

                        // Restore the enemy's resistance for the next fight:
                        ourCurrentEnemy.restoreResistance();

                        // Add the Enemy back to the Global pool:
                        Global.addEnemy(ourCurrentEnemy);

                        // Clear the current enemy:
                        ourCurrentEnemy = null;
                        
                        // Reverse the increment to fade back in:
                        inc = -.5;
                    }
                }
                else if (inc < 0)
                {
                    fade = TimingRegister.update("fade", fade, inc);
                    if (fade <= 0)
                    {
                        fade = 0;
                        inc = 0;
                    }
                }
            }
            @Override
            public boolean isFinished() {
                return inc == 0;
            }
            @Override
            public boolean disablesNormalCycle() {
                return true;
            }
            @Override
            public void draw(Graphics g) {
                g.setColor(new Color(0, 0, 0, (int)fade));
                if (inc > 0)
                    g.fillRect(0, 0, SCREEN_W, SCREEN_H);
                else
                    g.fillRect(SCENE_X, SCENE_Y, SCENE_W, SCENE_H);
            }   
            @Override
            public void onFinish() {  
                TimingRegister.remove("fade");
            }
            @Override
            public boolean useKeyPress(KeyEvent e) {
                return false;
            }
        });                           
    }
    
    // Player attack sequence:
    public static void doServeRandomVerseFromEnabledBooks()
    {            
        Element vsElmt = BibleOps.getRandomVerseElementByBook(getEnabledBookList());
        ourServedReference = new BibleReference(vsElmt);            
        // Override with longest verse (for testing):
//            ourServedReference = new BibleReference("Esther 8:9");
//            vsElmt = BibleOps.getScriptureElement(ourServedReference);
        ourVerseTextLabel.setText(vsElmt.getTextTrim());

        final BufferedImage scrollImage = new BufferedImage(684, 150, BufferedImage.TYPE_INT_ARGB);
        ourVerseTextLabel.paintIntoRelativeGraphics(scrollImage.createGraphics(), 43, 12);

        setSpecialEvent(new SpecialEvent(){
            double widthToShow = 0;
            double inc = .8;
            @Override
            public void start() {
                playSoundEffect("scroll");
            }
            @Override
            public void onFinish() {
                TimingRegister.remove("scroll_open");
                ourVerseTextLabel.setVisible(true);
                ourBookSelector.resetToBooklist(getEnabledBookList());
                ourBookSelector.setVisible(false); // Invisible until button is pressed.
                ourBookSelectButton.setText("<select book>");
                ourChapterTextEntry.setText("");
                ourVerseTextEntry.setText("");
                setCombatActionText("Enter reference:");
                ourBattleReferenceComponents.setAlphaComposite(1f); 
                ourBattleReferenceComponents.setVisible(true); 
            }
            @Override
            public void doIteration() {
                widthToShow = TimingRegister.update("scroll_open", widthToShow, inc);
            }
            @Override
            public boolean isFinished() {
                return widthToShow >= scrollImage.getWidth();
            }
            @Override
            public boolean disablesNormalCycle() {
                return true;
            }
            @Override
            public boolean useKeyPress(KeyEvent e) {
                return false;
            }
            @Override
            public void draw(Graphics g) {
                int w = (int)widthToShow;
                if (w % 2 == 1)
                    w--;
                int dX = (SCENE_LEFT+(SCENE_W/2)-(w/2))-1;
                int dY = ourVerseTextLabel.getY()-12;
                int sX = (scrollImage.getWidth()-w)/2;
                g.drawImage(scrollImage, dX, dY, dX+w, dY+scrollImage.getHeight(), sX, 0, sX+w, scrollImage.getHeight(), null);
                int sL = dX-42;
                int sR = (dX+w)-16;
                if (sL < ourVerseTextLabel.getX()-43)
                    sL = ourVerseTextLabel.getX()-43;
                if (sR > ourVerseTextLabel.getX()+593)
                    sR = ourVerseTextLabel.getX()+593;
                g.drawImage(getLoadedImage("interface/left_roll.png"), sL, dY, null);
                g.drawImage(getLoadedImage("interface/right_roll.png"), sR, dY, null);                    
            }              
        });                                    
    }
    public static void doFadeReferenceControlsToSword() // Fades out the reference controls while fading in the sword graphic.
    {
        setSpecialEvent(new SpecialEvent() {
            private double inc = .5;
            private double fade = 0;            
            @Override
            public void start() {                
            }
            @Override
            public void doIteration() {
                if (inc > 0)
                {
                    fade = TimingRegister.update("fade_to_sword", fade, inc);
                    if (fade >= 255)
                    {
                        fade = 255;
                        inc = 0;
                    }
                }
            }
            @Override
            public boolean isFinished() {
                return inc == 0;
            }
            @Override
            public boolean disablesNormalCycle() {
                return true;
            }
            @Override
            public void draw(Graphics g) {
                int x = SCENE_LEFT+312;
                int y = SCENE_TOP+294;
                Graphics2D g2 = (Graphics2D)g;
                float tsp = (float)fade/255f; 
                ourBattleReferenceComponents.setAlphaComposite(1f-tsp);
              
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, tsp));            
                g2.drawImage(getLoadedImage("sprites/effect_sprites/big_sword.png"), x, y, null);
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
            }   
            @Override
            public void onFinish() {        
                TimingRegister.remove("fade_to_sword");
                doReferenceSwordBackswing();
            }
            @Override
            public boolean useKeyPress(KeyEvent e) {
                return false;
            }
        });        
    }
    public static void doReferenceSwordBackswing() // Does a sword backswing and determines whether a hit or miss will occur.
    {
        setSpecialEvent(new SpecialEvent() {
            private double maxVel = -.2;   
            private double maxDist = 100;
            private int startX = SCENE_LEFT+312;
            private double x = startX;

            @Override
            public void start() {      
                playSoundEffect("unsheath");
            }
            @Override
            public void doIteration() {
                double dist = startX - x;
                double pct = (maxDist - (dist/maxDist))/100;
                double vel = maxVel*pct;
                if (vel < 0)
                {
                    x = TimingRegister.update("sword_pos", x, vel);
                }
            }
            @Override
            public boolean isFinished() {                
                return x <= startX-maxDist;
            }
            @Override
            public boolean disablesNormalCycle() {
                return true;
            }
            @Override
            public void draw(Graphics g) {
                int y = SCENE_TOP+294;
                Graphics2D g2 = (Graphics2D)g;                                       
                g2.drawImage(getLoadedImage("sprites/effect_sprites/big_sword.png"), (int)x, y, null);
            }   
            @Override
            public void onFinish() {
                TimingRegister.remove("sword_pos");     
                int score = evaluateGuess();
                if (score > -1)
                    doReferenceSwordThrust(score);
                else
                    doReferenceSwordMiss();
            }
            @Override
            public boolean useKeyPress(KeyEvent e) {
                return false;
            }
        });        
    }
    public static void doReferenceSwordThrust(final int score) // Does a full thrust animation to hit the enemy.
    {        
        setSpecialEvent(new SpecialEvent() {
            private double vel = 1.2;   
            private double maxDist = 300;
            private int startX = SCENE_LEFT+212;
            private double x = startX;

            @Override
            public void start() {  
                playSoundEffect("thrust");
            }
            @Override
            public void doIteration() {                
                x = TimingRegister.update("sword_pos", x, vel);
                if (x > startX + maxDist)
                    x = startX + maxDist;
            }
            @Override
            public boolean isFinished() {                
                return x >= startX+maxDist;
            }
            @Override
            public boolean disablesNormalCycle() {
                return true;
            }
            @Override
            public void draw(Graphics g) {
                int y = SCENE_TOP+294;
                Graphics2D g2 = (Graphics2D)g;                                       
                g2.drawImage(getLoadedImage("sprites/effect_sprites/big_sword.png"), (int)x, y, null);
            }   
            @Override
            public void onFinish() {
                TimingRegister.remove("sword_pos");                
                doReferenceSwordWound(score/10);
            }
            @Override
            public boolean useKeyPress(KeyEvent e) {
                return false;
            }
        });        
    }
    public static void doReferenceSwordMiss() // Does a short thrust which fades the sword before reaching the enemy.
    {        
        setSpecialEvent(new SpecialEvent() {
            private double vel = 1.0;   
            private double maxDist = 200;
            private int startX = SCENE_LEFT+212;
            private double x = startX;

            @Override
            public void start() {  
                playSoundEffect("miss");
            }
            @Override
            public void doIteration() {                
                x = TimingRegister.update("sword_pos", x, vel);
                if (x > startX + maxDist)
                    x = startX + maxDist;
            }
            @Override
            public boolean isFinished() {                
                return x >= startX+maxDist;
            }
            @Override
            public boolean disablesNormalCycle() {
                return true;
            }
            @Override
            public void draw(Graphics g) {
                int y = SCENE_TOP+294;
                int dist = (int)x - startX;
                float alpha = 1.0f - ((float)dist/(float)maxDist);
                Graphics2D g2 = (Graphics2D)g;   
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
                g2.drawImage(getLoadedImage("sprites/effect_sprites/big_sword.png"), (int)x, y, null);
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
            }   
            @Override
            public void onFinish() {
                TimingRegister.remove("sword_pos");   
                doEndPlayerCombatTurn();
            }
            @Override
            public boolean useKeyPress(KeyEvent e) {
                return false;
            }
        });        
    }
    public static void doReferenceSwordWound(final int totalDamage) // Does a SpiritBall animation and damages the enemy.
    {        
        setSpecialEvent(new SpecialEvent() {
            private ArrayList<SpiritBall> spiritBalls = new ArrayList<>();
            private int x = SCENE_LEFT+920;
            private int y = SCENE_TOP+344;
            private int damage = totalDamage;

            @Override
            public void start() {  
                playSoundEffect("splooge");
                int balls = totalDamage / 20;
                if (balls <= 0) balls = 1;
                for (int b = 0; b < balls; b++)
                {
                    spiritBalls.add(new SpiritBall(x, y));
                }
            }
            @Override
            public void doIteration() {  
                if (damage > 0)
                {
                    ourCurrentEnemy.hurt(1);
                    damage--;
                }
                if (ourCurrentEnemy.isDefeated())
                {
                    damage = 0;
                }
                for (int b = 0; b < spiritBalls.size(); b++)
                {
                    spiritBalls.get(b).update();
                }
            }
            @Override
            public boolean isFinished() { 
                boolean done = damage <= 0;
                for (int b = 0; b < spiritBalls.size(); b++)
                {
                    if (!spiritBalls.get(b).isDone())
                        done = false;
                }
                return done;
            }
            @Override
            public boolean disablesNormalCycle() {
                return true;
            }
            @Override
            public void draw(Graphics g) {
                Graphics2D g2 = (Graphics2D)g;
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
                g2.drawImage(getLoadedImage("sprites/effect_sprites/big_sword.png"), SCENE_LEFT+512, SCENE_TOP+294, null);
                for (int b = 0; b < spiritBalls.size(); b++)
                {
                    spiritBalls.get(b).paint(g2);
                }
            }   
            @Override
            public void onFinish() {  
                if (ourCurrentEnemy.isDefeated())
                {
                    doEnemyFlee();
                }
                else
                {
                    doEndPlayerCombatTurn();
                }
            }
            @Override
            public boolean useKeyPress(KeyEvent e) {
                return false;
            }
        });     
    }
    public static void doEnemyFlee() // Turns off the music and plays a celebratory theme as the enemy flees.
    {       
        setSpecialEvent(new SpecialEvent() {
            private double vel = .6;
            private int y = SCENE_TOP+200;
            private double x = SCENE_LEFT+824;
            @Override
            public void start() {                  
                Global.pauseBackgroundMusic(true);
                playSoundEffect("victory");
                ourEnemyPortrait.setVisible(false);
            }
            @Override
            public void doIteration() {                                
                if (x >= SCENE_RIGHT)
                    x = SCENE_RIGHT;
                else
                    x = TimingRegister.update("enemy_portrait", x, vel);
            }
            @Override
            public boolean isFinished() { 
                return x >= SCENE_RIGHT;
            }
            @Override
            public boolean disablesNormalCycle() {
                return true;
            }
            @Override
            public void draw(Graphics g) {
                g.drawImage(getLoadedImage("sprites/devil_large.png"), (int)x, y, null);
            }   
            @Override
            public void onFinish() {  
                TimingRegister.remove("enemy_portrait");
                ourBattleEvaluationComponents.setVisible(false);
                doEndPlayerCombatTurn();
            }
            @Override
            public boolean useKeyPress(KeyEvent e) {
                return false;
            }
        });
    }
    public static void doEndPlayerCombatTurn() // Shows the result of the guess and waits for Enter to be pressed.
    {
        setSpecialEvent(new SpecialEvent() {
            private boolean done = false;
            @Override
            public void start() {  
                ourBattleEvaluationComponents.setVisible(true);
            }
            @Override
            public void doIteration() {                
                
            }
            @Override
            public boolean isFinished() { 
                return done;
            }
            @Override
            public boolean disablesNormalCycle() {
                return true;
            }
            @Override
            public void draw(Graphics g) {
                
            }   
            @Override
            public void onFinish() {  
                clearCombatActionText();
                ourVerseTextLabel.setVisible(false);
                ourBattleEvaluationComponents.setVisible(false);
                if (ourCurrentEnemy.isDefeated())
                    doDefeatEnemy();
                else
                    doStartEnemyCombatTurn();
            }
            @Override
            public boolean useKeyPress(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER)
                {
                    done = true;
                    return true;
                }
                else
                    return false;
            }
        });        
    }
    
    // Enemy turn sequence:
    public static void doStartEnemyCombatTurn()
    {
        ourCurrentEnemy.decrementAllAttackCooldowns();
        final EnemyAttack attack = ourCurrentEnemy.getRandomAttack();
        setSpecialEvent(new SpecialEvent() {
            double wait = 500.0;
            @Override
            public void start() {
                setCombatActionText(attack.getDescription().replace("_", ourCurrentEnemy.getName()));                
            }

            @Override
            public void onFinish() {
                TimingRegister.remove("wait");
                setSpecialEvent(new SpecialEvent() {                    
                    @Override
                    public void start() {
                        attack.useAttack();
                    }

                    @Override
                    public void onFinish() {                        
                        attack.onFinish();
                        Armour armour = ourHero.getBlockingArmour(attack, ourCurrentEnemy);
                        if (armour == null)
                        {
                            doEnemyAttackWound(attack.getAttackDamage(ourCurrentEnemy));
                        }
                        else
                        {
                            doBlockEnemyAttack(armour);
                        }
                    }

                    @Override
                    public void doIteration() {
                        if (!attack.isFinished())
                        {                    
                            attack.doAttackIteration(ourCurrentEnemy, ourHero);
                        }
                    }

                    @Override
                    public boolean isFinished() {
                        return attack.isFinished();
                    }

                    @Override
                    public boolean disablesNormalCycle() {
                        return true;
                    }

                    @Override
                    public boolean useKeyPress(KeyEvent e) {
                        return false;
                    }

                    @Override
                    public void draw(Graphics g) {                
                        attack.drawAttack((Graphics2D)g);
                    }
                });
            }

            @Override
            public void doIteration() {
                wait = TimingRegister.update("wait", wait, -1.0);               
            }

            @Override
            public boolean isFinished() {
                return wait <= 0;
            }

            @Override
            public boolean disablesNormalCycle() {
                return true;
            }

            @Override
            public boolean useKeyPress(KeyEvent e) {
                return false;
            }

            @Override
            public void draw(Graphics g) {                                
            }
        });
    }
    public static void doEnemyAttackWound(final int totalDamage) // Does a SpiritBall animation and damages the hero.
    {        
        setSpecialEvent(new SpecialEvent() {
            private ArrayList<SpiritBall> spiritBalls = new ArrayList<>();
            private int x = SCENE_LEFT+104;
            private int y = SCENE_TOP+344;
            private int damage = totalDamage;

            @Override
            public void start() {  
                playSoundEffect("splooge");
                int balls = totalDamage / 20;
                if (balls <= 0) balls = 1;
                for (int b = 0; b < balls; b++)
                {
                    spiritBalls.add(new SpiritBall(x, y));
                }
            }
            @Override
            public void doIteration() {  
                if (damage > 0)
                {
                    ourHero.doubtFaith(1);
                    damage--;
                }
                if (ourHero.getCurrentFaith() <= 0)
                {
                    damage = 0;
                }
                for (int b = 0; b < spiritBalls.size(); b++)
                {
                    spiritBalls.get(b).update();
                }
            }
            @Override
            public boolean isFinished() { 
                boolean done = damage <= 0;
                for (int b = 0; b < spiritBalls.size(); b++)
                {
                    if (!spiritBalls.get(b).isDone())
                        done = false;
                }
                return done;
            }
            @Override
            public boolean disablesNormalCycle() {
                return true;
            }
            @Override
            public void draw(Graphics g) {
                Graphics2D g2 = (Graphics2D)g;
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
                for (int b = 0; b < spiritBalls.size(); b++)
                {
                    spiritBalls.get(b).paint(g2);
                }
            }   
            @Override
            public void onFinish() {  
                doEndEnemyCombatTurn();                
            }
            @Override
            public boolean useKeyPress(KeyEvent e) {
                return false;
            }
        });     
    }
    public static void doBlockEnemyAttack(final Armour armour) // Blocks the Enemy's attack with the designated Armour.
    {        
        setSpecialEvent(new SpecialEvent() {
            private int x = SCENE_LEFT+124;
            private int y = SCENE_TOP+344;
            private double blockAnim = 0.0;
            @Override
            public void start() { 
                setCombatActionText(armour.getDefendText());
                playSoundEffect(armour.getSoundName());
            }
            @Override
            public void doIteration() {  
                blockAnim = TimingRegister.update("block", blockAnim, 1.0);
            }
            @Override
            public boolean isFinished() { 
                return blockAnim >= 2000.0;
            }
            @Override
            public boolean disablesNormalCycle() {
                return true;
            }
            @Override
            public void draw(Graphics g) {
                Graphics2D g2 = (Graphics2D)g;
                int f = (int)(blockAnim/100)+1;
                if (f > 5)
                {
                    f = 0;
                }
                if (f > 0)
                    g2.drawImage(getLoadedImage("sprites/effect_sprites/spark-" + f + ".png"), x-100, y-100, null);                
            }   
            @Override
            public void onFinish() {  
                TimingRegister.remove("block");
                doEndEnemyCombatTurn();
            }
            @Override
            public boolean useKeyPress(KeyEvent e) {
                return false;
            }
        });     
    }
    public static void doEndEnemyCombatTurn()
    {        
        clearCombatActionText();
        setSpecialEvent(new SpecialEvent() {
            double wait = 500.0;
            @Override
            public void start() {
            }

            @Override
            public void onFinish() {
                TimingRegister.remove("wait");
                if (ourHero.isDefeated())
                    doDefeatHero();
                else
                    doServeRandomVerseFromEnabledBooks();
            }

            @Override
            public void doIteration() {
                wait = TimingRegister.update("wait", wait, -1.0);     
            }

            @Override
            public boolean isFinished() {
                return wait <= 0;
            }

            @Override
            public boolean disablesNormalCycle() {
                return true;
            }

            @Override
            public boolean useKeyPress(KeyEvent e) {
                return false;
            }

            @Override
            public void draw(Graphics g) {
                
            }
        });
    }
    
    // Popup windows:
    public static void doShowItemPopup(final Item item)
    {
        setDialogSpecialEvent(new SpecialEvent() {
            private boolean amen = false;
            private Popup pop = Popup.makeFindItemPopup(item);
            private SimpleButton button = new SimpleButton(new Point(256, 226), "Amen!", null) {
                @Override
                protected void mouseReleaseAction(MouseEvent e, boolean within) {
                    super.mouseReleaseAction(e, within);
                    amen = true;                                       
                }           
            };           
            @Override
            public void start() {  
                pop.addComponent(button);
                ourSpecialEventComponents.addComponent(pop);
                ourCurrentComponentGroups.add(ourSpecialEventComponents);
                playSoundEffect("fanfare");
            }
            @Override
            public void doIteration() {
                
            }
            @Override
            public boolean isFinished() {
                return amen;
            }
            @Override
            public boolean disablesNormalCycle() {
                return true;
            }
            @Override
            public void draw(Graphics g) {
                
            }   
            @Override
            public void onFinish() {
                int a = Global.getRandomInt(1, 3); 
                playSoundEffect("amen" + a + "");
                ourCurrentComponentGroups.remove(ourSpecialEventComponents);
                ourSpecialEventComponents.clear();
            }
            @Override
            public boolean useKeyPress(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER)
                {
                    amen = true;
                    return true;
                }
                else
                    return false;
            }
        });        
    }        
    public static void doShowPopup(final String text)
    {
        setDialogSpecialEvent(new SpecialEvent() {
            private boolean amen = false;
            private Popup pop = new Popup(300, 200); 
            private SimpleButton button = new SimpleButton(new Point(150, 170), "Amen!", null) {
                @Override
                protected void mouseReleaseAction(MouseEvent e, boolean within) {
                    super.mouseReleaseAction(e, within);
                    amen = true;                                       
                }   
                @Override
                protected void mousePressAction(MouseEvent e) {
                    super.mousePressAction(e);                    
                }                
            };           
            @Override
            public void start() {  
                pop.addComponent(button);
                ourSpecialEventComponents.addComponent(pop);
                ourCurrentComponentGroups.add(ourSpecialEventComponents);
            }
            @Override
            public void doIteration() {
                
            }
            @Override
            public boolean isFinished() {
                return amen;
            }
            @Override
            public boolean disablesNormalCycle() {
                return true;
            }
            @Override
            public void draw(Graphics g) {
                
            }   
            @Override
            public void onFinish() {
                int a = Global.getRandomInt(1, 3); 
                playSoundEffect("amen" + a + "");
                ourCurrentComponentGroups.remove(ourSpecialEventComponents);
                ourSpecialEventComponents.clear();
            }
            @Override
            public boolean useKeyPress(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER)
                {
                    amen = true;
                    return true;
                }
                else
                    return false;
            }
        });        
    }
    public static void doShowQuitPopup()
    {
        setDialogSpecialEvent(new SpecialEvent() {
            private String[] choices = {"Yes", "No"};
            private Popup pop = Popup.makeMultiChoicePopup(choices, "Do you really want to quit?", "Quit Game");
            
            @Override
            public void start() {  
                ourSpecialEventComponents.addComponent(pop);
                ourCurrentComponentGroups.add(ourSpecialEventComponents);
            }
            @Override
            public void doIteration() {
                
            }
            @Override
            public boolean isFinished() {
                return pop.getReturnObject() != null;
            }
            @Override
            public boolean disablesNormalCycle() {
                return true;
            }
            @Override
            public void draw(Graphics g) {
                
            }   
            @Override
            public void onFinish() {                
                switch((Integer)pop.getReturnObject())
                {
                    case 0:
                        ourStop = true;
                        break;
                    case 1:
                        ourCurrentComponentGroups.remove(ourSpecialEventComponents);
                        ourSpecialEventComponents.clear();
                        break;                        
                }
            }
            @Override
            public boolean useKeyPress(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_Y)
                {
                    pop.setReturnObject(0);
                    return true;
                }
                else if (e.getKeyCode() == KeyEvent.VK_N)
                {
                    pop.setReturnObject(1);
                    return true;
                }
                else
                    return false;
            }
        });    
    }
    
    // END SPECIAL EVENT METHODS ===============================================
    
    
    public static void setCurrentScene(Scene scene, Point playerEntryPoint, Point companionEntryPoint)
    {
        if ((ourCurrentScene == null || ourCurrentScene.getTerrain() != Terrain.CHURCH) && scene.getTerrain() == Terrain.CHURCH)
        {
            Global.setBackgroundMusic("words");
        }
        else if ((ourCurrentScene == null || ourCurrentScene.getTerrain() == Terrain.CHURCH) && scene.getTerrain() != Terrain.CHURCH)
        {
            Global.setBackgroundMusic("togodglory");
        }
        
        
        // Close the church service if the previous scene was inside a church:
        if (ourCurrentScene != null && ourCurrentScene.getTerrain() == Terrain.CHURCH)
        {
            ((ChurchMap)ourCurrentScene.getMap()).getBuilding().getChurch().closeService();
        }
        // Toggle all church services except when entering a church scene:
        if (scene.getTerrain() != Terrain.CHURCH)
        {
            Global.toggleChurchServices();
        }
        ourCurrentMap = scene.getMap();
        ourCurrentScene = scene;
        
        // Discover the Scene:
        ourCurrentScene.discover();
                
        // Remove sprites from the previous scene:
        for (int s = 0; s < ourSprites.size(); s++)
        {
            if (ourSprites.get(s) instanceof Enemy)
            {
                Global.addEnemy((Enemy)ourSprites.get(s));
            }
            
            // Below code is ok for now. Once random people are added to unsafe scenes,
            // they will be spawned and dealt with similar to enemies: taken from a Global
            // pool, spawned, and returned to the pool when the scene is exited.
            if (ourSprites.get(s) instanceof Person)
            {
                ((Person)ourSprites.get(s)).resetAlreadyTalked();
            }
        }
        ourSprites.clear();

        // Place player and companion:
        addSprite(ourHero);
        ourHero.setLocation(playerEntryPoint);
        if (ourCompanion != null)
        {
            addSprite(ourCompanion);
            ourCompanion.setLocation(companionEntryPoint);
        }

        // Add temporary Sprites:
        addTemporarySprites();
    }
    
    public static void addEnemiesToCurrentScene()
    {
        if (!ourCurrentScene.isSafe())
        {
            int enemyCount = ourGameDifficulty.getRandomEnemyCountForUnsafeScene();
            Global.log("Spawning " + enemyCount + " enemies...");
            for (int e = 0; e < enemyCount; e++)
            {
                // Enemies must be withdrawn from the Global pool so that we don't use the same enemy twice.
                // Don't worry; when the scene is exited, they'll be added back into the Global pool.
                Enemy enemy = Global.withdrawRandomEnemy();
                int attempts = 0;
                boolean failed = false;
                do
                {                                    
                    failed = !ourCurrentScene.placeRandomlyIfPossible(enemy, ourCurrentScene.getSpriteLayer(SpriteLayer.TYPE.ACTION), 5, true, true); 
                    attempts++;
                } while((failed || enemy.getDistanceFromPoint(GoodFight.getSubject().getBasePoint()) <= Enemy.DETECT_DISTANCE) && attempts <= SPAWN_ATTEMPTS);
                if (attempts <= SPAWN_ATTEMPTS)
                {
                    addSprite(enemy);
                    Global.log("Enemy added!");
                }
                else
                {
                    Global.addEnemy(enemy); // Add it back to the pool
                    Global.log("Enemy couldn't be added!");
                }
            }
        }
    }
    
    public static void addNeighborsToCurrentScene()
    {
        if (!ourCurrentScene.isSafe())
        {
            ArrayList<Person> neighbors = new ArrayList<Person>();
            Church church = Global.getNearestChurch(ourCurrentScene);
            int neighborCount = Global.getRandomInt(0, 3);
            Global.log("Spawning " + neighborCount + " neighbors...");
            for (int n = 0; n < neighborCount; n++)
            {
                Person neighbor = church.getRandomNeighbor();
                
                if (!neighbors.contains(neighbor))
                {
                    int attempts = 0;
                    boolean failed = false;
                    do
                    {                                    
                        failed = !ourCurrentScene.placeRandomlyIfPossible(neighbor, ourCurrentScene.getSpriteLayer(SpriteLayer.TYPE.ACTION), 5, true, true); 
                        attempts++;
                    } while(failed || neighbor.getDistanceFromPoint(GoodFight.getSubject().getBasePoint()) <= Enemy.DETECT_DISTANCE && attempts <= SPAWN_ATTEMPTS);
                    if (attempts <= SPAWN_ATTEMPTS)
                    {
                        addSprite(neighbor);
                        neighbor.setMode(Person.PersonMode.MOSEY);
                        neighbors.add(neighbor);
                        Global.log("Neighbor added!");
                    }
                }
            }
        }
    }
    
    public static void addChurchMembersToCurrentScene()
    {
        // Make a church service if it should be in service:
        if (ourCurrentMap instanceof ChurchMap)
        {
            Church church = ((ChurchMap)ourCurrentMap).getBuilding().getChurch();
            if (church.isInService())
            {
                church.prepareForService();
            }
        }
        // Add church members if we're outside a church:
        else if (ourCurrentScene.getBuilding() instanceof ChurchBuilding)
        {
            Church church = ((ChurchBuilding)ourCurrentScene.getBuilding()).getChurch();
            if (!church.isInService())
            {
                for (int p = 0; p < church.getMembers().size(); p++)
                {
                    Person member = church.getMembers().get(p);
                    member.setMode(Person.PersonMode.MOSEY);
                    if (ourCurrentScene.placeRandomlyIfPossible(member, ourCurrentScene.getSpriteLayer(SpriteLayer.TYPE.ACTION), 5, true, true))
                        addSprite(member);
                }
            }
        }
    }
    
    public static void addTemporarySprites()
    {        
        // Place the scene's perma-Chest, if there is one:
        TreasureChest chest = ourCurrentScene.getPermaChest();
        if (chest != null)
        {
            addSprite(chest);
        }

        // 30% chance to spawn a random chest (if in an unsafe area):
        if (!ourCurrentScene.isSafe())
        {
            int c = Global.getRandomInt(1, 100);
            if (c <= 2) // 1-2
            {
                System.out.println("2% - Sermon Chest");
            }
            else if (c <= 6) // 3-6
            {
                System.out.println("4% - Seed Chest");
            }
            else if (c <= 10) // 7-10
            {
                System.out.println("4% - Strong Meat Chest");
                chest = new TreasureChest(0, 0, new StrongMeat(), false);
                if (ourCurrentScene.placeRandomlyIfPossible(chest, ourCurrentScene.getSpriteLayer(SpriteLayer.TYPE.ACTION), 30, true, false))
                    addSprite(chest);
            }
            else if (c <= 18) // 11-18
            {
                System.out.println("8% - Meat Chest");
                chest = new TreasureChest(0, 0, new Meat(), false);
                if (ourCurrentScene.placeRandomlyIfPossible(chest, ourCurrentScene.getSpriteLayer(SpriteLayer.TYPE.ACTION), 30, true, false))
                    addSprite(chest);
            }
            else if (c <= 30) // 19-30
            {
                System.out.println("12% - Milk Chest");
                chest = new TreasureChest(0, 0, new Milk(), false);
                if (ourCurrentScene.placeRandomlyIfPossible(chest, ourCurrentScene.getSpriteLayer(SpriteLayer.TYPE.ACTION), 30, true, false))
                    addSprite(chest);
            }
        }    
        
        // Add any enemies for this scene:
        addEnemiesToCurrentScene();
        
        // Add any lost people (neighbors) for this scene:
        addNeighborsToCurrentScene();
        
        // Add any church members for this scene:
        addChurchMembersToCurrentScene();
        
        // Add neighbors, if there is no building:
        if (ourCurrentMap == ourWorldMap && ourCurrentScene.getBuilding() == null)
        {
            
        }
    }
    
    public static boolean overlapsSprite(Thing thing)
    {
        for (int s = 0; s < ourSprites.size(); s++)
        {
            if (ourSprites.get(s).intersectsPhysicallyWith(thing))
                return true;
        }
        return false;
    }
    
    public static void checkSceneEdgeTravel()
    {
        // Test for special occurrances based on movement:
        if (getSubject().getPhysicalLeft() <= LEFT_BOUND && (!ourCurrentScene.isWall(Dir.W)) && Global.isFacingWest(getSubject().getDirection()))
        {
            Scene nextScene = ourCurrentScene.getNeighbor(Dir.W);
            if (nextScene != null)
            {             
                Point playerPt = new Point(TV_RIGHT_BOUND-(getSubject().getWidth()-getSubject().getRightMargin()), getSubject().getY());
                Point compPt;
                if (ourCompanion != null)
                    compPt = new Point(playerPt.x, (playerPt.y <= SCENE_CTR_Y) ? ourHero.getBottom() : ourHero.getTop() - ourCompanion.getHeight());
                else
                    compPt = null;
                doTransitionToScene(nextScene, playerPt, compPt);
            }
            
        }
        if (getSubject().getPhysicalRight() >= RIGHT_BOUND && (!ourCurrentScene.isWall(Dir.E)) && Global.isFacingEast(getSubject().getDirection()))
        {
            Scene nextScene = ourCurrentScene.getNeighbor(Dir.E);
            if (nextScene != null)
            {
                Point playerPt = new Point(TV_LEFT_BOUND - getSubject().getLeftMargin(), getSubject().getY());
                Point compPt;
                if (ourCompanion != null)
                    compPt = new Point(playerPt.x, (playerPt.y <= SCENE_CTR_Y) ? ourHero.getBottom() : ourHero.getTop() - ourCompanion.getHeight());
                else
                    compPt = null;
                doTransitionToScene(nextScene, playerPt, compPt);
            }
        }
        if (getSubject().getPhysicalTop() <= TOP_BOUND && (!ourCurrentScene.isWall(Dir.N)) && Global.isFacingNorth(getSubject().getDirection()))
        {
            Scene nextScene = ourCurrentScene.getNeighbor(Dir.N);
            if (nextScene != null)
            {
                Point playerPt = new Point(getSubject().getX(), TV_BOTTOM_BOUND-(getSubject().getHeight()-getSubject().getBottomMargin()));
                Point compPt;
                if (ourCompanion != null)
                    compPt = new Point((playerPt.x <= SCENE_CTR_X) ? ourHero.getRight() : ourHero.getLeft() - ourCompanion.getWidth(), playerPt.y);
                else
                    compPt = null;
                doTransitionToScene(nextScene, playerPt, compPt);
            }
        }
        if (getSubject().getPhysicalBottom() >= BOTTOM_BOUND && (!ourCurrentScene.isWall(Dir.S)) && Global.isFacingSouth(getSubject().getDirection()))
        {
            Scene nextScene = ourCurrentScene.getNeighbor(Dir.S);
            if (nextScene != null)
            {
                Point playerPt = new Point(getSubject().getX(), TV_TOP_BOUND - getSubject().getTopMargin());
                Point compPt;
                if (ourCompanion != null)
                    compPt = new Point((playerPt.x <= SCENE_CTR_X) ? ourHero.getRight() : ourHero.getLeft() - ourCompanion.getWidth(), playerPt.y);
                else
                    compPt = null;
                doTransitionToScene(nextScene, playerPt, compPt);
            }
        }
    }
    

    
    public static void drawDrawings(Graphics2D g)
    {
        for (int r = ourDrawings.size()-1; r >= 0; r--)
        {
            ourDrawings.get(r).render(g);
        }
    }

    public static void drawCurrentComponents(Graphics2D g)
    {
        for (int cg = 0; cg < ourCurrentComponentGroups.size(); cg++)
        {
            ourCurrentComponentGroups.get(cg).paint(g);
        }        
    }
    
    public static void drawPaused(Graphics2D g)
    {
        String text = "PAUSED";
        g.setColor(Color.WHITE);
        g.setFont(Global.GreekFont.deriveFont(Font.PLAIN, 36));
        g.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON );
        int width = g.getFontMetrics().stringWidth(text);
        int height = g.getFontMetrics().getHeight();
        int x = (SCREEN_W/2)-(width/2);
        int y = (SCREEN_H/2)-(height/2);
        g.drawString(text, x, y);
    }

    public static void init() throws IOException
    {                
        // Initialize graphics stuff:
        
        loadImageSet("interface");
        loadImageSet("sprites");
        loadImageSet("animations");
        loadImageSet("items");
        loadImageSet("backgrounds");     
        loadImageSet("test");
        
        NullRepaintManager.install();
        ourScreenManager = new ScreenManager();
        DisplayMode[] modes = ourScreenManager.getCompatibleDisplayModes();
        DisplayMode selectedMode = new DisplayMode(SCREEN_W, SCREEN_H, 32, DisplayMode.REFRESH_RATE_UNKNOWN);
        for (DisplayMode m: modes)
        {
            if (ourScreenManager.displayModesMatch(m, selectedMode))
            {
                selectedMode = m;
                Global.log("{" + m.getWidth() + "," + m.getHeight() + " " + m.getBitDepth() + "-bit @" + m.getRefreshRate() + "} selected.");
            }
        }
        ourScreenManager.setFullScreen(selectedMode);
        ourMainFrame = ourScreenManager.getFullScreenWindow();   
        if (System.getProperty("os.name").toUpperCase().contains("MAC"))
        {
            ourMainFrame.setVisible(false);
            ourMainFrame.setVisible(true);
        }

        BufferedImage cursorImg = getLoadedImage("interface/cursor.png");
        ourCursor = Toolkit.getDefaultToolkit().createCustomCursor(cursorImg, new Point(0,0), "GFF Cursor");
        ourBlankCursor = Toolkit.getDefaultToolkit().createCustomCursor(new BufferedImage(1,1,BufferedImage.TYPE_4BYTE_ABGR), new Point(0,0), "Blank Cursor");
         

        // Initialize sound stuff:
        
        ourSoundManager = new SoundManager(20);
        ourMidiPlayer = new MidiPlayer();        

        // Initialize input stuff:
        
        ourMainFrame.setCursor(ourBlankCursor);
        ourMainFrame.addMouseMotionListener(ourMouseHandler);
        ourMainFrame.addMouseListener(ourMouseHandler);
        ourMainFrame.addKeyListener(ourKeyHandler);
        try {
            ourRobot = new Robot();
        } catch (AWTException ex) {
            Logger.getLogger(GoodFight.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        // Initialize interface stuff:
        
        ourSmallHeroMeter = new InterfaceComponent() {
            @Override
            protected void paintContent(Graphics2D gfx) {
                Graphics2D g = (Graphics2D)gfx;
                int x = 78;
                int y = 32;
                int h = 16;
                float pct = (float)ourHero.getCurrentFaith()/(float)ourHero.getMaxFaith();
                int w = (int)(pct*150);
                g.setColor(Color.RED);
                g.fillRect(x, y, w, h);
                g.drawImage(getLoadedImage("interface/small_meter.png"), 5, 2, null);  
                // Set the font to use:
                g.setFont(Global.GreekMedFont);
                g.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON );
                // Draw the level String:                
                g.setColor(Color.WHITE);
                String heroLevel = "Adam: Level " + ourHero.getLevel();                
                Rectangle2D levelBounds = g.getFontMetrics().getStringBounds(heroLevel, g);
                g.drawString(heroLevel, 153-(int)(levelBounds.getWidth()/2), 12+(int)levelBounds.getHeight());                
                // Draw the faith String:
                g.setColor(Color.RED);
                String faithRatio = "Faith: " + ourHero.getCurrentFaith() + "/" + ourHero.getMaxFaith();
                Rectangle2D ratBounds = g.getFontMetrics().getStringBounds(faithRatio, g);
                g.drawString(faithRatio, 153-(int)(ratBounds.getWidth()/2), 52+(int)ratBounds.getHeight());
            }            
        };
        ourMainComponents.addComponent(new InterfaceComponent() {
            @Override
            protected void paintContent(Graphics2D g) {
                g.drawImage(getLoadedImage("interface/icon_bar.png"), 0, 0, null);                
            } 
        });
        ourMainComponents.addComponent(ourSmallHeroMeter);
        int iconX = 242;
        ourMainComponents.addComponent(new GameIcon("Status", 1, getLoadedImage("interface/status_icon.png"), new Point(iconX, 2))); iconX+=79;
        ourMainComponents.addComponent(new GameIcon("Books", 2, getLoadedImage("interface/books_icon.png"), new Point(iconX, 2))); iconX+=79;
        ourMainComponents.addComponent(new GameIcon("Bible", 3, getLoadedImage("interface/bible_icon.png"), new Point(iconX, 2))); iconX+=79;
        ourMainComponents.addComponent(new GameIcon("Churches", 4, getLoadedImage("interface/church_icon.png"), new Point(iconX, 2))); iconX+=79;
        ourMainComponents.addComponent(new GameIcon("Map", 5, getLoadedImage("interface/map_icon.png"), new Point(iconX, 2))); iconX+=79;
        ourMainComponents.addComponent(new GameIcon("Statistics", 6, getLoadedImage("interface/statistics_icon.png"), new Point(iconX, 2))); iconX+=79;
        ourMainComponents.addComponent(new GameIcon("Save", 7, getLoadedImage("interface/save_icon.png"), new Point(iconX, 2))); iconX+=79;
        ourMainComponents.addComponent(new GameIcon("Load", 8, getLoadedImage("interface/load_icon.png"), new Point(iconX, 2))); iconX+=79;        
        ourMainComponents.addComponent(new GameIcon("Help", 9, getLoadedImage("interface/help_icon.png"), new Point(iconX, 2))); iconX+=79;    
        ourMainComponents.addComponent(new GameIcon("Options", 10, getLoadedImage("interface/options_icon.png"), new Point(iconX, 2))); iconX+=79;
        ourCurrentComponentGroups.add(ourMainComponents);
        
        initBookListComponents();
        initBattleComponents();
        
        // Initialize text stuff:
        BibleOps.connectXML();
    }

    public static void initBookListComponents()
    {      
        Color TITLES = new Color (20, 20, 20);
        
        int CAT_GAP = 190;
        int TEST_GAP = 220;
        int INIT_BOOKS_X = 40;
        int INIT_BOOKS_Y = 266;
        int BOOK_GAP = 20;

        int x, y;
        Label titleLabel = new Label(0, 0, "Books of the Bible", Global.GreekFont.deriveFont(Font.BOLD, 64f), TITLES, null);
        x = SCENE_CTR_X - (titleLabel.getWidth()/2);
        y = SCENE_TOP + 20;
        titleLabel.setLocation(new Point(x, y));
        ourBookListComponents.addComponent(titleLabel);
        
        Label otLabel = new Label(0, 0, "Old Testament", Global.GreekFont.deriveFont(Font.BOLD, 30f), TITLES, null);
        x = 180;
        y+=100;
        otLabel.setLocation(new Point(x, y));
        ourBookListComponents.addComponent(otLabel);
        
        Label ntLabel = new Label(0, 0, "New Testament", Global.GreekFont.deriveFont(Font.BOLD, 30f), TITLES, null);
        x = 690;
        ntLabel.setLocation(new Point(x, y));
        ourBookListComponents.addComponent(ntLabel);
        
        x = INIT_BOOKS_X; y = INIT_BOOKS_Y;
        ourBookListComponents.addComponent(new Label(x, y, "Law of Moses", Global.GreekMedFont, TITLES, null));
        y+=BOOK_GAP; ourBookListComponents.addComponent(new CheckItem(x, y, "Genesis", true, false));
        y+=BOOK_GAP; ourBookListComponents.addComponent(new CheckItem(x, y, "Exodus", true, false));
        y+=BOOK_GAP; ourBookListComponents.addComponent(new CheckItem(x, y, "Leviticus", true, false));
        y+=BOOK_GAP; ourBookListComponents.addComponent(new CheckItem(x, y, "Numbers", true, false));
        y+=BOOK_GAP; ourBookListComponents.addComponent(new CheckItem(x, y, "Deuteronomy", true, false));
        
        y+=(BOOK_GAP*2); ourBookListComponents.addComponent(new Label(x, y, "History", Global.GreekMedFont, TITLES, null));
        y+=BOOK_GAP; ourBookListComponents.addComponent(new CheckItem(x, y, "Joshua", true, false));
        y+=BOOK_GAP; ourBookListComponents.addComponent(new CheckItem(x, y, "Judges", true, false));
        y+=BOOK_GAP; ourBookListComponents.addComponent(new CheckItem(x, y, "Ruth", true, false));
        y+=BOOK_GAP; ourBookListComponents.addComponent(new CheckItem(x, y, "1 Samuel", true, false));
        y+=BOOK_GAP; ourBookListComponents.addComponent(new CheckItem(x, y, "2 Samuel", true, false));
        y+=BOOK_GAP; ourBookListComponents.addComponent(new CheckItem(x, y, "1 Kings", true, false));
        y+=BOOK_GAP; ourBookListComponents.addComponent(new CheckItem(x, y, "2 Kings", true, false));
        y+=BOOK_GAP; ourBookListComponents.addComponent(new CheckItem(x, y, "1 Chronicles", true, false));
        y+=BOOK_GAP; ourBookListComponents.addComponent(new CheckItem(x, y, "2 Chronicles", true, false));
        y+=BOOK_GAP; ourBookListComponents.addComponent(new CheckItem(x, y, "Ezra", true, false));
        y+=BOOK_GAP; ourBookListComponents.addComponent(new CheckItem(x, y, "Nehemiah", true, false));
        y+=BOOK_GAP; ourBookListComponents.addComponent(new CheckItem(x, y, "Esther", true, false));      
        
        x+=190; y = 266;     
        ourBookListComponents.addComponent(new Label(x, y, "Poetry/Wisdom", Global.GreekMedFont, TITLES, null));
        y+=BOOK_GAP; ourBookListComponents.addComponent(new CheckItem(x, y, "Job", true, false));
        y+=BOOK_GAP; ourBookListComponents.addComponent(new CheckItem(x, y, "Psalms", true, false));
        y+=BOOK_GAP; ourBookListComponents.addComponent(new CheckItem(x, y, "Proverbs", true, false));
        y+=BOOK_GAP; ourBookListComponents.addComponent(new CheckItem(x, y, "Ecclesiastes", true, false));
        y+=BOOK_GAP; ourBookListComponents.addComponent(new CheckItem(x, y, "Song of Solomon", true, false));
        
        y+=(BOOK_GAP*2); ourBookListComponents.addComponent(new Label(x, y, "Major Prophets", Global.GreekMedFont, TITLES, null));
        y+=BOOK_GAP; ourBookListComponents.addComponent(new CheckItem(x, y, "Isaiah", true, false));
        y+=BOOK_GAP; ourBookListComponents.addComponent(new CheckItem(x, y, "Jeremiah", true, false));
        y+=BOOK_GAP; ourBookListComponents.addComponent(new CheckItem(x, y, "Lamentations", true, false));
        y+=BOOK_GAP; ourBookListComponents.addComponent(new CheckItem(x, y, "Ezekiel", true, false));
        y+=BOOK_GAP; ourBookListComponents.addComponent(new CheckItem(x, y, "Daniel", true, false));
        
        x+=190; y = 266;     
        ourBookListComponents.addComponent(new Label(x, y, "Minor Prophets", Global.GreekMedFont, TITLES, null));
        y+=BOOK_GAP; ourBookListComponents.addComponent(new CheckItem(x, y, "Hosea", true, false));
        y+=BOOK_GAP; ourBookListComponents.addComponent(new CheckItem(x, y, "Joel", true, false));
        y+=BOOK_GAP; ourBookListComponents.addComponent(new CheckItem(x, y, "Amos", true, false));
        y+=BOOK_GAP; ourBookListComponents.addComponent(new CheckItem(x, y, "Obadiah", true, false));
        y+=BOOK_GAP; ourBookListComponents.addComponent(new CheckItem(x, y, "Jonah", true, false));
        y+=BOOK_GAP; ourBookListComponents.addComponent(new CheckItem(x, y, "Micah", true, false));
        y+=BOOK_GAP; ourBookListComponents.addComponent(new CheckItem(x, y, "Nahum", true, false));
        y+=BOOK_GAP; ourBookListComponents.addComponent(new CheckItem(x, y, "Habakkuk", true, false));
        y+=BOOK_GAP; ourBookListComponents.addComponent(new CheckItem(x, y, "Zephaniah", true, false));
        y+=BOOK_GAP; ourBookListComponents.addComponent(new CheckItem(x, y, "Haggai", true, false));
        y+=BOOK_GAP; ourBookListComponents.addComponent(new CheckItem(x, y, "Zechariah", true, false));
        y+=BOOK_GAP; ourBookListComponents.addComponent(new CheckItem(x, y, "Malachi", true, false));  
        
        x+=220; y = 266;     
        ourBookListComponents.addComponent(new Label(x, y, "Gospels", Global.GreekMedFont, TITLES, null));
        y+=BOOK_GAP; ourBookListComponents.addComponent(new CheckItem(x, y, "Matthew", true, false));
        y+=BOOK_GAP; ourBookListComponents.addComponent(new CheckItem(x, y, "Mark", true, false));
        y+=BOOK_GAP; ourBookListComponents.addComponent(new CheckItem(x, y, "Luke", true, false));
        y+=BOOK_GAP; ourBookListComponents.addComponent(new CheckItem(x, y, "John", true, false));
        
        y+=(BOOK_GAP*2); ourBookListComponents.addComponent(new Label(x, y, "History", Global.GreekMedFont, TITLES, null));
        y+=BOOK_GAP; ourBookListComponents.addComponent(new CheckItem(x, y, "Acts", true, false));
        
        y+=(BOOK_GAP*2); ourBookListComponents.addComponent(new Label(x, y, "Church Epistles", Global.GreekMedFont, TITLES, null));
        y+=BOOK_GAP; ourBookListComponents.addComponent(new CheckItem(x, y, "Romans", true, false));
        y+=BOOK_GAP; ourBookListComponents.addComponent(new CheckItem(x, y, "1 Corinthians", true, false));
        y+=BOOK_GAP; ourBookListComponents.addComponent(new CheckItem(x, y, "2 Corinthians", true, false));
        y+=BOOK_GAP; ourBookListComponents.addComponent(new CheckItem(x, y, "Galatians", true, false));
        y+=BOOK_GAP; ourBookListComponents.addComponent(new CheckItem(x, y, "Ephesians", true, false));
        y+=BOOK_GAP; ourBookListComponents.addComponent(new CheckItem(x, y, "Philippians", true, false));
        y+=BOOK_GAP; ourBookListComponents.addComponent(new CheckItem(x, y, "Colossians", true, false));
        y+=BOOK_GAP; ourBookListComponents.addComponent(new CheckItem(x, y, "1 Thessalonians", true, false));
        y+=BOOK_GAP; ourBookListComponents.addComponent(new CheckItem(x, y, "2 Thessalonians", true, false));
        
        x+=190; y = 266;     
        ourBookListComponents.addComponent(new Label(x, y, "Personal Epistles", Global.GreekMedFont, TITLES, null));
        y+=BOOK_GAP; ourBookListComponents.addComponent(new CheckItem(x, y, "1 Timothy", true, false));
        y+=BOOK_GAP; ourBookListComponents.addComponent(new CheckItem(x, y, "2 Timothy", true, false));
        y+=BOOK_GAP; ourBookListComponents.addComponent(new CheckItem(x, y, "Titus", true, false));
        y+=BOOK_GAP; ourBookListComponents.addComponent(new CheckItem(x, y, "Philemon", true, false));
        
        y+=(BOOK_GAP*2); ourBookListComponents.addComponent(new Label(x, y, "General Epistles", Global.GreekMedFont, TITLES, null));
        y+=BOOK_GAP; ourBookListComponents.addComponent(new CheckItem(x, y, "Hebrews", true, false));
        y+=BOOK_GAP; ourBookListComponents.addComponent(new CheckItem(x, y, "James", true, false));
        y+=BOOK_GAP; ourBookListComponents.addComponent(new CheckItem(x, y, "1 Peter", true, false));
        y+=BOOK_GAP; ourBookListComponents.addComponent(new CheckItem(x, y, "2 Peter", true, false));
        y+=BOOK_GAP; ourBookListComponents.addComponent(new CheckItem(x, y, "1 John", true, false));
        y+=BOOK_GAP; ourBookListComponents.addComponent(new CheckItem(x, y, "2 John", true, false));
        y+=BOOK_GAP; ourBookListComponents.addComponent(new CheckItem(x, y, "3 John", true, false));
        y+=BOOK_GAP; ourBookListComponents.addComponent(new CheckItem(x, y, "Jude", true, false));
        
        y+=(BOOK_GAP*2); ourBookListComponents.addComponent(new Label(x, y, "Prophecy", Global.GreekMedFont, TITLES, null));
        y+=BOOK_GAP; ourBookListComponents.addComponent(new CheckItem(x, y, "Revelation", true, false));
        
        y = 686;
        SimpleButton allOTBtn = new SimpleButton(new Point(270, y), "All", null, Color.GRAY, Color.BLUE, Color.LIGHT_GRAY, TITLES, TITLES) { 
            @Override
            protected void mousePressAction(MouseEvent e) {
                super.mousePressAction(e);
                ArrayList<InterfaceComponent> comps = ourBookListComponents.getComponents();
                for (int b = 0; b < comps.size(); b++)
                {
                    if (comps.get(b) instanceof CheckItem)
                    {
                        CheckItem bookCheck = (CheckItem)comps.get(b);                        
                        if (bookCheck.isEnabled() && new BibleReference(bookCheck.getText()).getTestamentNo()==1)
                        {
                            bookCheck.setChecked(true);
                            playSoundEffect("upper");
                        }
                    }
                }
            }                
        }; 
        ourBookListComponents.addComponent(allOTBtn);
        SimpleButton noOTBtn = new SimpleButton(new Point(330, y), "None", null, Color.GRAY, Color.BLUE, Color.LIGHT_GRAY, TITLES, TITLES) { 
            @Override
            protected void mousePressAction(MouseEvent e) {
                super.mousePressAction(e);
                ArrayList<InterfaceComponent> comps = ourBookListComponents.getComponents();
                for (int b = 0; b < comps.size(); b++)
                {
                    if (comps.get(b) instanceof CheckItem)
                    {
                        CheckItem bookCheck = (CheckItem)comps.get(b);
                        if (bookCheck.isEnabled() && new BibleReference(bookCheck.getText()).getTestamentNo()==1)
                        {
                            bookCheck.setChecked(false);
                            playSoundEffect("downer");
                        }
                    }
                }
            }                
        };        
        ourBookListComponents.addComponent(noOTBtn);
        SimpleButton allNTBtn = new SimpleButton(new Point(770, y), "All", null, Color.GRAY, Color.BLUE, Color.LIGHT_GRAY, TITLES, TITLES) { 
            @Override
            protected void mousePressAction(MouseEvent e) {
                super.mousePressAction(e);
                ArrayList<InterfaceComponent> comps = ourBookListComponents.getComponents();
                for (int b = 0; b < comps.size(); b++)
                {
                    if (comps.get(b) instanceof CheckItem)
                    {
                        CheckItem bookCheck = (CheckItem)comps.get(b);
                        if (bookCheck.isEnabled() && new BibleReference(bookCheck.getText()).getTestamentNo()==2)
                        {
                            bookCheck.setChecked(true);
                            playSoundEffect("upper");
                        }
                    }
                }
            }                
        }; 
        ourBookListComponents.addComponent(allNTBtn);
        SimpleButton noNTBtn = new SimpleButton(new Point(830, y), "None", null, Color.GRAY, Color.BLUE, Color.LIGHT_GRAY, TITLES, TITLES) { 
            @Override
            protected void mousePressAction(MouseEvent e) {
                super.mousePressAction(e);
                ArrayList<InterfaceComponent> comps = ourBookListComponents.getComponents();
                for (int b = 0; b < comps.size(); b++)
                {
                    if (comps.get(b) instanceof CheckItem)
                    {
                        CheckItem bookCheck = (CheckItem)comps.get(b);
                        if (bookCheck.isEnabled() && new BibleReference(bookCheck.getText()).getTestamentNo()==2)
                        {
                            bookCheck.setChecked(false);
                            playSoundEffect("downer");
                        }
                    }
                }
            }                
        };
        ourBookListComponents.addComponent(noNTBtn);
        
        ArrayList<InterfaceComponent> comps = ourBookListComponents.getComponents();
        for (int b = 0; b < comps.size(); b++)
        {
            if (comps.get(b) instanceof CheckItem)
            {
                CheckItem bookCheck = (CheckItem)comps.get(b);               
                bookCheck.setEnabled(false);                
            }
        }
    }
    
    public static void initBattleComponents()
    {         
        ourHeroPortrait = new InterfaceComponent() {
            @Override
            protected void paintContent(Graphics2D g) {
                g.drawImage(getLoadedImage("sprites/knight_large.png"), SCENE_LEFT, SCENE_TOP+200, null);
            }
        };
        ourBattleComponents.addComponent(ourHeroPortrait);
        
        ourEnemyPortrait = new InterfaceComponent() {
            @Override
            protected void paintContent(Graphics2D g) {
                g.drawImage(getLoadedImage("sprites/devil_large.png"), SCENE_LEFT+824, SCENE_TOP+200, null);
            }
        };
        ourBattleComponents.addComponent(ourEnemyPortrait);
        
        ourLargeHeroMeter = new InterfaceComponent() {
            @Override
            protected void paintContent(Graphics2D gfx) {
                Graphics2D g = (Graphics2D)gfx;
                int x = 147;
                int y = 63;
                int h = 24;
                float pct = (float)ourHero.getCurrentFaith()/(float)ourHero.getMaxFaith();
                int w = (int)(pct*300);
                g.setColor(Color.RED);
                g.fillRect(x, y, w, h);
                g.drawImage(getLoadedImage("interface/meter_circle.png"), 23, 33, null);
                g.drawImage(getLoadedImage("interface/hero_meter.png"), 0, 5, null);  
                
                g.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON );
                
                // Draw the name String:   
                g.setFont(Global.GreekMedFont.deriveFont(22f));               
                g.setColor(Color.WHITE);                                            
                g.drawString("Adam", 170, 51);
                
                // Draw the level String:
                g.setFont(Global.GreekMedFont); 
                String level = "" + ourHero.getLevel();
                Rectangle2D levelBounds = g.getFontMetrics().getStringBounds(level, g);
                g.drawString(level, 80 - (int)(levelBounds.getWidth()/2), 23);

                // Draw the faith String:                
                String faithRatio = "Faith: " + ourHero.getCurrentFaith() + "/" + ourHero.getMaxFaith();
                Rectangle2D ratBounds = g.getFontMetrics().getStringBounds(faithRatio, g);
                g.drawString(faithRatio, 297-(int)(ratBounds.getWidth()/2), 65+(int)ratBounds.getHeight());
            }            
        };
        ourBattleComponents.addComponent(ourLargeHeroMeter);
        
        ourLargeEnemyMeter = new InterfaceComponent() {
            @Override
            protected void paintContent(Graphics2D gfx) {
                Graphics2D g = (Graphics2D)gfx;
                int x = (SCENE_RIGHT-147)-300;
                int y = 63;
                int h = 24;
                float pct = (float)ourCurrentEnemy.getResistance()/(float)ourCurrentEnemy.getMaxResistance();
                int w = (int)(pct*300);
                g.setColor(Color.RED);
                g.fillRect(x, y, w, h);
                g.drawImage(getLoadedImage("interface/meter_circle.png"), (SCENE_RIGHT-23)-114, 33, null);
                g.drawImage(getLoadedImage("interface/enemy_meter.png"), SCENE_RIGHT-450, 5, null);  
                
                g.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON );
                
                // Draw the name String:   
                g.setFont(Global.GreekMedFont.deriveFont(22f));               
                g.setColor(Color.WHITE);              
                Rectangle2D nameBounds = g.getFontMetrics().getStringBounds(ourCurrentEnemy.getName(), g);
                g.drawString(ourCurrentEnemy.getName(), (SCENE_RIGHT-170)-(int)nameBounds.getWidth(), 51);
                
                // Draw the level String:
                g.setFont(Global.GreekMedFont); 
                String level = "" + ourCurrentEnemy.getLevel();
                Rectangle2D levelBounds = g.getFontMetrics().getStringBounds(level, g);
                g.drawString(level, (SCENE_RIGHT-80) - (int)(levelBounds.getWidth()/2), 23);

                // Draw the faith String:                
                String faithRatio = "Resistance: " + ourCurrentEnemy.getResistance() + "/" + ourCurrentEnemy.getMaxResistance();
                Rectangle2D ratBounds = g.getFontMetrics().getStringBounds(faithRatio, g);
                g.drawString(faithRatio, (x+150)-(int)(ratBounds.getWidth()/2), 65+(int)ratBounds.getHeight());
            }            
        };
        ourBattleComponents.addComponent(ourLargeEnemyMeter);
        
        ourBattleComponents.addComponent(new InterfaceComponent() {
            @Override
            protected void paintContent(Graphics2D gfx) {
                Graphics2D g = (Graphics2D)gfx;
                int x = 450 + 12;
                int y = 30;
                g.drawImage(getLoadedImage("interface/vs.png"), x, y, null); 
            }
        });
        
        
        final FinalBypasser<SimpleButton> finalBookButton = new FinalBypasser<>();
        final FinalBypasser<TextEntryBox> finalChapterBox = new FinalBypasser<>();   
        final FinalBypasser<TextEntryBox> finalVerseBox = new FinalBypasser<>();           
        
        ourActionLabel = new AreaLabel(SCENE_LEFT+212, SCENE_TOP+195, 600, 40, "", Global.GreekFont.deriveFont(32f), Color.WHITE, null);
        ourBattleComponents.addComponent(ourActionLabel);
        
        ourVerseTextLabel = new AreaLabel(SCENE_LEFT+212, SCENE_TOP+48, 600, 124, null, Global.PrintFont.deriveFont(Font.BOLD, 14f), new Color(100,60,0), null){
            @Override
            protected void paintContent(Graphics2D g) {
                g.drawImage(getLoadedImage("interface/parchment.png"), getX()-43, getY()-12, null);
                super.paintContent(g);
            }            
        };               
        ourBattleComponents.addComponent(ourVerseTextLabel);  
        ourVerseTextLabel.setVisible(false);                
        
        // Book/Chapter/Verse Labels:
        ourBattleReferenceComponents.addComponent(new AreaLabel(SCENE_LEFT+602, SCENE_TOP+313, 100, 20, "Verse:", Global.SimpleSmFont.deriveFont(Font.BOLD), Color.WHITE, null));
        ourBattleReferenceComponents.addComponent(new AreaLabel(SCENE_LEFT+462, SCENE_TOP+313, 100, 20, "Chapter:", Global.SimpleSmFont.deriveFont(Font.BOLD), Color.WHITE, null));
        ourBattleReferenceComponents.addComponent(new AreaLabel(SCENE_LEFT+322, SCENE_TOP+313, 100, 20, "Book:", Global.SimpleSmFont.deriveFont(Font.BOLD), Color.WHITE, null));
        
        ourVerseTextEntry = new TextEntryBox(SCENE_LEFT+602, SCENE_TOP+333, 100, 20, "", Global.SimpleSmFont, Color.BLACK, Color.WHITE, Color.GRAY) {
            @Override
            protected void mousePressAction(MouseEvent e) {
                super.mousePressAction(e);
                this.setEditing(true);
            }
            @Override
            public void enterPressed() {
                super.enterPressed();
                if (!finalVerseBox.get().getText().isEmpty())
                {
                    // This ends the player's choice (turn).
                    setCombatActionText("Adam wields the book of " + ourBookSelector.getSelectedBookName() + "!");
                    ourGuessedReference = new BibleReference(ourBookSelector.getSelectedBookName() + " " + finalChapterBox.get().getText() + ":" + finalVerseBox.get().getText());
                    doFadeReferenceControlsToSword();
                }
            }            
        };
        ourVerseTextEntry.setEditing(false);
        ourBattleReferenceComponents.addComponent(ourVerseTextEntry);
                         
        ourChapterTextEntry = new TextEntryBox(SCENE_LEFT+462, SCENE_TOP+333, 100, 20, "", Global.SimpleSmFont, Color.BLACK, Color.WHITE, Color.GRAY) {
            @Override
            protected void mousePressAction(MouseEvent e) {
                super.mousePressAction(e);
                this.setEditing(true);
            }
            @Override
            public void enterPressed() {
                super.enterPressed();
                if (!finalChapterBox.get().getText().isEmpty())
                    finalVerseBox.get().setEditing(true);
            }   
            @Override
            public void tabPressed() {
                super.tabPressed();
                if (!finalChapterBox.get().getText().isEmpty())
                    finalVerseBox.get().setEditing(true);
            }
            
        };
        ourChapterTextEntry.setEditing(false);
        ourBattleReferenceComponents.addComponent(ourChapterTextEntry);
        
        // There is a cyclic relationship here: BookSelector->BookButton->BookSelector; both objects require the other to be defined already.
        // The FinalBypasser allows us to reference a SimpleButton that does not exist yet as if it were already declared as "final".               
        ourBookSelector = new BookSelector(new Point(SCENE_LEFT+372, SCENE_TOP+343)) {
            @Override
            protected void mouseReleaseAction(MouseEvent e, boolean within) {
                super.mouseReleaseAction(e, within);
                finalBookButton.get().setText(this.getSelectedBookName());                
                finalBookButton.get().setVisible(true);
                finalBookButton.get().mouseExited(e);
                finalChapterBox.get().setEditing(true);
            }            
        };
        ourBookSelector.setGlobalMouseRelease(true); // Should close if the mouse is released anywhere.
        ourBookSelector.setVisible(false);
        ourBattleReferenceComponents.addComponent(ourBookSelector);           
        ourBookSelectButton = new SimpleButton(SCENE_LEFT+322, SCENE_TOP+333, 100, 20, "<select book>", Global.SimpleSmFont, Color.LIGHT_GRAY, Color.BLUE, Color.LIGHT_GRAY, Color.BLACK, Color.BLACK) { 
            @Override
            protected void mousePressAction(MouseEvent e) {
                super.mousePressAction(e);
                this.setVisible(false);
                ourBookSelector.setVisible(true);
                ourBookSelector.mouseEntered(e);
            }                
        };        
        finalBookButton.set(ourBookSelectButton);
        finalChapterBox.set(ourChapterTextEntry);
        finalVerseBox.set(ourVerseTextEntry);
        ourBattleReferenceComponents.addComponent(ourBookSelectButton);
                
        ourBattleEvaluationComponents.addComponent(new InterfaceComponent() {
            @Override
            protected void paintContent(Graphics2D gfx) {
                Graphics2D g = (Graphics2D)gfx;
                int x = 330;
                int y = 460;
                g.drawImage(getLoadedImage("interface/eval_back.png"), x, y, null); 
            }
        });
        
        ourGuessCaptionLabel = new AreaLabel(SCENE_LEFT+312, SCENE_TOP+402, 400, 20, "Guessed Reference:", Global.GreekFont.deriveFont(16f), COLOR_GOLD.darker(), null);
        ourBattleEvaluationComponents.addComponent(ourGuessCaptionLabel);        
        
        ourGuessReferenceLabel = new AreaLabel(SCENE_LEFT+312, SCENE_TOP+422, 400, 20, "", Global.GreekFont.deriveFont(20f), COLOR_GOLD.brighter(), null);
        ourBattleEvaluationComponents.addComponent(ourGuessReferenceLabel);
        
        ourActualCaptionLabel = new AreaLabel(SCENE_LEFT+312, SCENE_TOP+448, 400, 20, "Actual Reference:", Global.GreekFont.deriveFont(16f), COLOR_GOLD.darker(), null);
        ourBattleEvaluationComponents.addComponent(ourActualCaptionLabel);     
        
        ourActualReferenceLabel = new AreaLabel(SCENE_LEFT+312, SCENE_TOP+468, 400, 20, "", Global.GreekFont.deriveFont(20f), COLOR_GOLD.brighter(), null);
        ourBattleEvaluationComponents.addComponent(ourActualReferenceLabel);
        
        ourChapterScoreLabel = new Label(SCENE_LEFT+560, SCENE_TOP+502, "0", Global.GreekFont.deriveFont(Font.PLAIN, 16f), Color.WHITE, null);
        ourBattleEvaluationComponents.addComponent(ourChapterScoreLabel);
        ourBattleEvaluationComponents.addComponent(new Label(SCENE_LEFT+412, SCENE_TOP+502, "Chapter Score:", Global.GreekFont.deriveFont(Font.PLAIN, 16f), Color.WHITE, null));
        
        ourVerseScoreLabel = new Label(SCENE_LEFT+560, SCENE_TOP+522, "0", Global.GreekFont.deriveFont(Font.PLAIN, 16f), Color.WHITE, null);
        ourBattleEvaluationComponents.addComponent(ourVerseScoreLabel);
        ourBattleEvaluationComponents.addComponent(new Label(SCENE_LEFT+412, SCENE_TOP+522, "Verse Score:", Global.GreekFont.deriveFont(Font.PLAIN, 16f), Color.WHITE, null));
        
        ourBooksMultiplierLabel = new Label(SCENE_LEFT+560, SCENE_TOP+542, "0", Global.GreekFont.deriveFont(Font.PLAIN, 16f), Color.WHITE, null);
        ourBattleEvaluationComponents.addComponent(ourBooksMultiplierLabel);
        ourBattleEvaluationComponents.addComponent(new Label(SCENE_LEFT+412, SCENE_TOP+542, "Books Multiplier:", Global.GreekFont.deriveFont(Font.PLAIN, 16f), Color.WHITE, null));
        
        ourBonusScoreLabel = new Label(SCENE_LEFT+560, SCENE_TOP+562, "0", Global.GreekFont.deriveFont(Font.BOLD, 16f), Color.WHITE, null);
        ourBattleEvaluationComponents.addComponent(ourBonusScoreLabel);
        ourBattleEvaluationComponents.addComponent(new Label(SCENE_LEFT+412, SCENE_TOP+562, "Bonus Score:", Global.GreekFont.deriveFont(Font.PLAIN, 16f), Color.WHITE, null));
        
        ourFinalScoreLabel = new Label(SCENE_LEFT+560, SCENE_TOP+582, "0", Global.GreekFont.deriveFont(Font.BOLD, 20f), new Color(220,0,255), null);
        ourBattleEvaluationComponents.addComponent(ourFinalScoreLabel);
        ourBattleEvaluationComponents.addComponent(new Label(SCENE_LEFT+412, SCENE_TOP+582, "Final Score:", Global.GreekFont.deriveFont(Font.PLAIN, 20f), new Color(220,0,255), null));
        ourBattleEvaluationComponents.setVisible(false);
    }        
    
    public static void enableBookCheckbox(String bookName)
    {
        ArrayList<InterfaceComponent> comps = ourBookListComponents.getComponents();
        for (int b = 0; b < comps.size(); b++)
        {
            if (comps.get(b) instanceof CheckItem)
            {
                CheckItem bookCheck = (CheckItem)comps.get(b);
                if (bookCheck.getText().equalsIgnoreCase(bookName))
                {
                    bookCheck.setEnabled(true);
                }
            }
        }
    }
    
    public static void setBookEnabled(String bookName, boolean enabled)
    {
        ArrayList<InterfaceComponent> comps = ourBookListComponents.getComponents();
        for (int b = 0; b < comps.size(); b++)
        {
            if (comps.get(b) instanceof CheckItem)
            {
                CheckItem bookCheck = (CheckItem)comps.get(b);
                if (bookCheck.getText().equalsIgnoreCase(bookName))
                {
                    bookCheck.setChecked(enabled);
                }
            }
        }
    }
    
    public static ArrayList<String> getEnabledBookList()
    {
        ArrayList<String> bookList = new ArrayList<>();
        ArrayList<InterfaceComponent> comps = ourBookListComponents.getComponents();
        for (int b = 0; b < comps.size(); b++)
        {
            if (comps.get(b) instanceof CheckItem)
            {
                CheckItem bookCheck = (CheckItem)comps.get(b);
                if (bookCheck.isChecked())
                {
                    bookList.add(bookCheck.getText());
                }
            }
        }
        return bookList;
    }
    
    public static void clearKeyMap()
    {
        for (int k = 0; k < ourKeyMap.length; k++)
            ourKeyMap[k] = false;
    }
    
    public static File randomFileFromFolder(String folderName)
    {
        Random rand = new Random();
        File folder = new File("extern/" + folderName);
        File[] files = folder.listFiles();

        int index = rand.nextInt(files.length);
        return files[index];
    }

    public static void excludeOtherEditingControls(KeyboardEditable exclusiveEditor)
    {
        // For each component group:
            for (int g = 0; g < ourCurrentComponentGroups.size(); g++)
            {
                // For each component in the component group:
                for (int c = 0; c < ourCurrentComponentGroups.get(g).getComponentCount(); c++)
                {
                    InterfaceComponent comp = ourCurrentComponentGroups.get(g).getComponents().get(c);
                    if (comp instanceof KeyboardEditable)
                    {
                        KeyboardEditable keyEd = (KeyboardEditable)comp;
                        if (keyEd != exclusiveEditor)
                            keyEd.setEditing(false);
                    }
                }
            }
    }
    
    public static void clearCombatActionText()
    {
        ourActionLabel.setVisible(false);
        ourActionLabel.setText("");
    }
    public static void setCombatActionText(String text)
    {
        ourActionLabel.setVisible(true);
        ourActionLabel.setText(text);
    }
    
    public static void objectifyScene(Scene scene)
    {
        ourObjectifiedScene = scene;
    }
    public static Scene getObjectifiedScene()
    {
        return ourObjectifiedScene;
    }
    public static boolean objectifyNearestPremiumChest()
    {
        Point here = ourCurrentScene.getLocation();        
        ArrayList<Scene> scenes = ourWorldMap.getPremiumChestScenes();
        Scene nearestScene = null;
        double shortestDistance = Double.MAX_VALUE;
        for (int s = 0; s < scenes.size(); s++)
        {
            Point there = scenes.get(s).getLocation();
            double distance = Global.getDistance(here, there);
            if (distance < shortestDistance)
            {
                shortestDistance = distance;
                nearestScene = scenes.get(s);
            }
        }
        if (nearestScene != null)
        {
            objectifyScene(nearestScene);
            return true;
        }
        else
        {            
            return false;
        }
    }
    
    
    public static int evaluateGuess()
    {
        Element bookEl = BibleOps.getScriptureElement(new BibleReference(ourServedReference.getStartBookName()));
        List<Element> chapters = bookEl.getChildren("chapter");        
        
        int actualChap = ourServedReference.getStartChapterNo();
        int actualVerse = ourServedReference.getStartVerseNo();                
        
        int guessChap = ourGuessedReference.getStartChapterNo();
        int guessVs = ourGuessedReference.getStartVerseNo();
        
        int numChap = chapters.size();
        int numVs = chapters.get(actualChap-1).getChildren("verse").size();
        
        int chapDif = Math.abs(actualChap-guessChap);
        int vsDif = Math.abs(actualVerse-guessVs);
        
        double chapStep = 100.0/numChap;
        double vsStep = 100.0/numVs;
        
        double chapBase = 100-(chapDif*chapStep);
        double vsBase = 100-(vsDif*vsStep);
        
        if (chapBase < 0) chapBase = 0;
        if (vsBase < 0) vsBase = 0;
        
        double subTotal = chapBase + ((chapBase/100.0)*vsBase);
        double books = getEnabledBookList().size();
        int finalScore = (int)(subTotal*books);
        
        boolean hit = (ourServedReference.getStartBookNo() == ourGuessedReference.getStartBookNo());
        
        ourGuessCaptionLabel.setText("Guessed Reference:");
        ourGuessReferenceLabel.setText(ourGuessedReference.getReference());        
        ourActualCaptionLabel.setText("Actual reference:"); 
        ourActualReferenceLabel.setText(ourServedReference.getReference());
        
        if (hit)
        {
            if (ourGuessedReference.equals(ourServedReference))
            {
                // Wow, a direct hit! :)
                ourGuessCaptionLabel.setText("Reference correct: MAXIMUM SCORE!");
                ourGuessCaptionLabel.setFgColor(COLOR_GOLD.darker());
                ourGuessReferenceLabel.setFgColor(COLOR_GOLD.brighter());
                playSoundEffect("success");
            }
            else
            {
                // At least we got the book right...                
                ourGuessCaptionLabel.setFgColor(Color.CYAN.darker());
                ourGuessReferenceLabel.setFgColor(Color.CYAN.brighter().brighter());       
            }
            ourChapterScoreLabel.setText("" + (int)chapBase);
            ourVerseScoreLabel.setText((int)vsBase + " x " + ((int)chapBase) + "%");
            ourBooksMultiplierLabel.setText((int)subTotal + " x " + (int)books);
            ourBonusScoreLabel.setText("0");
            ourFinalScoreLabel.setText("" + finalScore);
        }
        else
        {
            // We totally missed the book; nothing else matters now :(
            ourGuessCaptionLabel.setFgColor(Color.RED.darker());
            ourGuessReferenceLabel.setFgColor(Color.RED.brighter());
            setCombatActionText("Adam missed!");
            ourChapterScoreLabel.setText("0");
            ourVerseScoreLabel.setText("0");
            ourBooksMultiplierLabel.setText("0 x " + (int)books + "");
            ourBonusScoreLabel.setText("0");
            ourFinalScoreLabel.setText("0");
        }
                        
        return hit ? finalScore : -1;
    }

    public static boolean isSubscreenMode(GameMode mode)
    {
        return mode == BookListMode || mode == MapMode;
    }
    public static boolean isAuxiliaryMode(GameMode mode)
    {
        return mode == TitleMode || mode == NullMode;
    }
    
    public static void playSoundEffect(Sound sound)
    {
        getSoundManager().playSound(sound);        
    }
    
    public static void playSoundEffect(String soundName)
    {
        getSoundManager().playSound(new Sound("extern/sound_fx/" + soundName + ".wav"));
    }
    
    public static Person getNearestFacingPerson(Person subject)
    {
        double TALK_DIST = 65;
        double shortestDistance = Double.MAX_VALUE;
        Person nearestPerson = null;
        for (int s = 0; s < ourSprites.size(); s++)
        {
            if (ourSprites.get(s) instanceof Person)
            {
                Person person = (Person)ourSprites.get(s);
                Point sbjBase = subject.getBasePoint();
                Point perBase = person.getBasePoint();
                int difX = Math.abs(sbjBase.x - perBase.x);
                int difY = Math.abs(sbjBase.y - perBase.y);
                double thisDist = Global.getDistance(sbjBase, perBase);
                switch(subject.getDirection())
                {
                    case N:
                        if (thisDist <= shortestDistance && perBase.y < sbjBase.y && difX < (TALK_DIST))
                        {
                            nearestPerson = person;
                            shortestDistance = thisDist;
                        }
                        break;
                    case E:
                        if (thisDist <= shortestDistance && perBase.x > sbjBase.x && difY < (TALK_DIST))
                        {
                            nearestPerson = person;
                            shortestDistance = thisDist;
                        }
                        break;
                    case S:
                        if (thisDist <= shortestDistance && perBase.y > sbjBase.y && difX < (TALK_DIST))
                        {
                            nearestPerson = person;
                            shortestDistance = thisDist;
                        }
                        break;
                    case W:
                        if (thisDist <= shortestDistance && perBase.x < sbjBase.x && difY < (TALK_DIST))
                        {
                            nearestPerson = person;
                            shortestDistance = thisDist;
                        }
                        break;
                }
            }
        }
        if (shortestDistance > 50)
            return null;
        else
            return nearestPerson;
    }
    
    public static void startPrayer()
    {
        ourPrayerMode = true;
    }
    public static void stopPrayer()
    {
        ourPrayerMode = false;
    }
    public static void setUniversalFade(float fadePct)
    {
        ourUniversalFade = fadePct;
    }
}

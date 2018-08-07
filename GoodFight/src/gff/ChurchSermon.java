/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gff;

import gff.maps.ChurchMap;
import gff.spritecommands.SpriteCommand;
import gff.GoodFight.Dir;
import gff.objects.Person;
import gff.objects.Sprite;
import gff.util.FinalBypasser;
import gff.util.ReadWriteTextFile;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.io.File;
import java.lang.reflect.Method;

/**
 *
 * @author Raymond
 */
public class ChurchSermon extends Conversation {

    public ChurchSermon(String sermonName) {
        File scriptFile = new File("extern/text/sermons/" + sermonName + ".scp");
        String script = ReadWriteTextFile.getContents(scriptFile);
        if (script == null)
        {
            Global.log("Script \"" + scriptFile.getName() + "\" could not be read from 'sermons'.");            
        }
        else
        {
            lines = script.replace("\r", "").split("\\^")[1].trim().split("\n");
        }
        delegate = new CodeDelegate() {
            @Override
            public void run() {
                GoodFight.doExitChurch();
            }
        };
    }
    
    @Override
    public SpecialEvent getNextConversationEvent()
    {
        SpecialEvent event = null;        
        final String[] eventLineParts = lines[index].split("`");
        if (eventLineParts[0].equals("$")) // Speech event:
        {
            final Sprite speaker = GoodFight.getActiveSpriteByScriptLabel(eventLineParts[1]);
            event = new SpecialEvent()
            {
                private boolean done = false;
                @Override
                public void start() {  
                    speaker.say(eventLineParts[2]);
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
                    return false;
                }
                @Override
                public void draw(Graphics g) {

                }   
                @Override
                public void onFinish() {  
                    speaker.shutUp();
                    if (isOver())
                        doPostCode();
                    else                        
                    {
                        SpecialEvent nextEvent = getNextConversationEvent();
                        GoodFight.setSpecialEvent(nextEvent);
                    }
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
            };        
        }
        else if (eventLineParts[0].equals("*")) // Action event:
        {
            final FinalBypasser<SpriteCommand> command = new FinalBypasser<>();
            event = new SpecialEvent()
            {                
                private boolean done = false;
                @Override
                public void start() {  
                    if (eventLineParts[1].equalsIgnoreCase("STOP_MUSIC"))
                    {
                        Global.pauseBackgroundMusic(true);
                    }
                    else if (eventLineParts[1].equalsIgnoreCase("SET_MUSIC"))
                    {
                        Global.setBackgroundMusic(eventLineParts[2]);
                    }
                    else if (eventLineParts[1].equalsIgnoreCase("FACE"))
                    {
                        final Sprite facer = GoodFight.getActiveSpriteByScriptLabel(eventLineParts[2]);
                        final Dir dir = GoodFight.Dir.valueOf(eventLineParts[3].toUpperCase());                        
                        command.set(new SpriteCommand(){
                            private boolean done = false;
                            @Override
                            public void onStart() {
                                facer.faceDirection(dir);
                                done = true;
                            }
                            @Override
                            public void onStop() {
                                facer.stop();
                            }
                            @Override
                            public boolean isDone() {                                
                                return done;
                            }                            
                        });
                        facer.setCommand(command.get());
                    }
                    else if (eventLineParts[1].equalsIgnoreCase("WALK"))
                    {
                        final Sprite walker = GoodFight.getActiveSpriteByScriptLabel(eventLineParts[2]);
                        final Dir dir = GoodFight.Dir.valueOf(eventLineParts[3].toUpperCase());
                        final Point dest = Global.move(walker.getLocation(), dir, Integer.parseInt(eventLineParts[4]));
                        
                        final boolean pastXIsGreater = dest.x > walker.getLocation().x;
                        final boolean pastYIsGreater = dest.y > walker.getLocation().y;
                        
                        command.set(new SpriteCommand(){
                            @Override
                            public void onStart() {
                                walker.setDirection(dir);
                                walker.go();
                            }
                            @Override
                            public void onStop() {
                                walker.stop();
                            }
                            @Override
                            public boolean isDone() {                                
                                boolean reachX = ((pastXIsGreater && walker.getLocation().x >= dest.x) ||
                                                  (!pastXIsGreater && walker.getLocation().x <= dest.x));
                                boolean reachY = ((pastYIsGreater && walker.getLocation().y >= dest.y) ||
                                                  (!pastYIsGreater && walker.getLocation().y <= dest.y));
                                return reachX && reachY;                                
                            }                            
                        });
                        walker.setCommand(command.get());
                    }
                    else if (eventLineParts[1].equalsIgnoreCase("WALK_AND_WAIT"))
                    {
                        final Sprite walker = GoodFight.getActiveSpriteByScriptLabel(eventLineParts[2]);
                        final Dir dir = GoodFight.Dir.valueOf(eventLineParts[3].toUpperCase());
                        final Point dest = Global.move(walker.getLocation(), dir, Integer.parseInt(eventLineParts[4]));
                        
                        final boolean pastXIsGreater = dest.x > walker.getLocation().x;
                        final boolean pastYIsGreater = dest.y > walker.getLocation().y;
                        
                        command.set(new SpriteCommand(){
                            @Override
                            public void onStart() {
                                walker.setDirection(dir);
                                walker.go();
                            }
                            @Override
                            public void onStop() {
                                walker.stop();
                            }
                            @Override
                            public boolean isDone() {                                
                                boolean reachX = ((pastXIsGreater && walker.getLocation().x >= dest.x) ||
                                                  (!pastXIsGreater && walker.getLocation().x <= dest.x));
                                boolean reachY = ((pastYIsGreater && walker.getLocation().y >= dest.y) ||
                                                  (!pastYIsGreater && walker.getLocation().y <= dest.y));
                                return reachX && reachY;                                
                            }                            
                        });
                        walker.setCommand(command.get());
                    }
                    else if (eventLineParts[1].equalsIgnoreCase("WALK_TO"))
                    {
                        final Sprite walker = GoodFight.getActiveSpriteByScriptLabel(eventLineParts[2]);
                        
                    }
                    else if (eventLineParts[1].equalsIgnoreCase("WALK_TO_AND_WAIT"))
                    {
                        final Sprite walker = GoodFight.getActiveSpriteByScriptLabel(eventLineParts[2]);
                        
                    }    
                    else if (eventLineParts[1].equalsIgnoreCase("CALL_METHOD"))
                    {
                        try {
                            Method method = GoodFight.class.getMethod(eventLineParts[2]);
                            method.invoke(null);
                        } catch (Exception e)
                        {}
                    }
                    else if (eventLineParts[1].equalsIgnoreCase("CALL_DIALOG_METHOD"))
                    {
                        // This will call a method, but not allow this SpecialEvent
                        // to finish until there is no dialog open.
                        try {
                            Method method = GoodFight.class.getMethod(eventLineParts[2]);
                            method.invoke(null);
                        } catch (Exception e)
                        {}
                    }
                }
                @Override
                public void doIteration() {  
                    if (eventLineParts[1].toUpperCase().contains("DIALOG"))
                    {
                        done = !GoodFight.isDialogOpen();
                    }
                    if (eventLineParts[1].toUpperCase().contains("WAIT"))
                    {
                        // If this is a WAIT action, we can only set done to true when the command is done.
                        done = command.get().isDone();
                    }
                    else
                    {
                        done = true;
                    }
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
                    if (isOver())
                        doPostCode();
                    else
                    {
                        SpecialEvent nextEvent = getNextConversationEvent();
                        GoodFight.setSpecialEvent(nextEvent);
                    }
                }
                @Override
                public boolean useKeyPress(KeyEvent e) {
                    return false;
                }
            };                
        }
        
        // If something has been said already, trigger a chance for a random amen:
        if (index > 0)
        {
            int randomAmen = Global.getRandomInt(1, 3);
            if (randomAmen == 3)
            {
                return new SpecialEvent()
                {
                    private boolean done = false;
                    private Person amenPerson = null;
                    @Override
                    public void start() {
                        Church church = ((ChurchMap)GoodFight.getCurrentScene().getMap()).getBuilding().getChurch();
                        do
                        {
                            amenPerson = church.getRandomMember();
                        } while ((amenPerson.getScriptLabel() != null && amenPerson.getScriptLabel().equalsIgnoreCase("Preacher")));
                        amenPerson.say((String)Global.getRandomFromList(Global.Amens));
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
                        return false;
                    }
                    @Override
                    public void draw(Graphics g) {

                    }   
                    @Override
                    public void onFinish() {  
                        amenPerson.shutUp();
                        if (!isOver())               
                        {
                            SpecialEvent nextEvent = getNextConversationEvent();
                            GoodFight.setSpecialEvent(nextEvent);
                        }
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
                };
            }
        }
        
        index++;
        return event;
    }
}

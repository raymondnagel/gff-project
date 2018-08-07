/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gff;

import gff.spritecommands.SpriteCommand;
import gff.GoodFight.Dir;
import gff.objects.Person;
import gff.objects.Sprite;
import gff.spritecommands.WalkCommand;
import gff.util.FinalBypasser;
import gff.util.ReadWriteTextFile;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 *
 * @author Raymond
 */
public class Conversation {
    
    /*
        There are basically 3 types of Conversations: Scripted, Custom, and Random.
        
        Scripted Conversations are loaded from a .scp file in extern/test/converse,
        using the (convName, postCode) constructor. They are ideal for cutscenes.
        Every line is given a specific scriptLabel, making group conversations easy.
        Scripted Conversations may or may not be initiated by the player.
    
        Custom Conversations are created with the (lines[], postCode, conversant)
        constructor. These are meant for 1-to-1 conversations that are explicitly
        programmed for special occasions. These are the Conversations that are
        most often queued up on a person. (Note that if you haven't met someone before,
        a queued conversation will defer to a random one, just so introductions can
        be made.) Custom Conversations are always initiated by the player.
        Custom conversations may include:
        - Tips for playing the game (starting church only)
        - Introduce you to a fruit of the Spirit (each starts off at level 0; you choose one to level up each time you level up)
        - They do some iron-sharpening fellowship with you (gives you an exp bonus, 10-40% of what's needed for level up)
        - They give you a seed or sermon.
    
        Random Conversations are the defaults, created by createRandomConversation().
        When the player begins a 1-on-1 Conversation with someone, and nothing is queued,
        then a random Conversation is created based on the person. If player is talking
        to the person for the very first time, they will introduce themselves; if
        they have met before, a greeting will suffice. Random Conversations are always
        initiated by the player.
        Following the greeting, saved people (church members) may do one of the following:
        - Give you some information about the world (nearest church, nearest stronghold, nearest treasure chest, terrain region, etc.)
        - Give you a verse of the day (verse of the day will be selected randomly when you enter the scene; everyone will share it)
        - Offer to join you as your companion. (requires familiarity >= 4)
    
        Also, EVERY Conversation you have with someone increases their "Familiarity". 
    */
   
    protected String[] lines = null;
    protected int index = 0;    
    protected CodeDelegate delegate = null;
    protected Person conversant = null;
    
    // Overriden in subclasses; not instantiable externally.
    protected Conversation(){}
    
    public Conversation(String[] lines, CodeDelegate postCode, Person conversant)
    {
        this.delegate = postCode;
        this.lines = lines;        
        this.conversant = conversant;
    }
    
    public Conversation(String convName, CodeDelegate postCode) {
        this.delegate = postCode;
        File scriptFile = new File("extern/text/converse/" + convName + ".scp");
        String script = ReadWriteTextFile.getContents(scriptFile);
        if (script == null)
        {
            Global.log("Script \"" + scriptFile.getName() + "\" could not be read from 'converse'.");            
        }
        else
        {
            lines = script.replace("\r", "").split("\\^")[1].trim().split("\n");
        }
    }

    protected boolean isOver()
    {
        return index >= lines.length;
    }
    
    
    protected void setPostCode(CodeDelegate postCode)
    {
        delegate = postCode;
    }
    protected void doPostCode()
    {
        if (delegate != null)
            delegate.run();
        if (conversant != null)
            conversant.setScriptLabel(null);
    }
    
    public SpecialEvent getNextConversationEvent()
    {
        // Note that conversation events CANNOT disable the normal cycle,
        // because Action events may occur in the Conversation.
        SpecialEvent event = null;        
        final String[] eventLineParts = lines[index].split("`");
        if (eventLineParts[0].equals("$")) // Speech event:
        {
            // Get the speaker by scriptlabel, unless the speaker is supposed to be the generic "Conversant" (person Adam is talking to in a custom conversation)
            final Sprite speaker = (conversant != null && eventLineParts[1].equalsIgnoreCase("Conversant")) ? conversant : GoodFight.getActiveSpriteByScriptLabel(eventLineParts[1]);
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
        else if (eventLineParts[0].equals("#")) // Thought event:
        {
            final Sprite thinker = GoodFight.getActiveSpriteByScriptLabel(eventLineParts[1]);
            event = new SpecialEvent()
            {
                private boolean done = false;
                @Override
                public void start() {  
                    thinker.think(eventLineParts[2]);
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
                    thinker.shutUp();
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
                    else if (eventLineParts[1].equalsIgnoreCase("WALK") || eventLineParts[1].equalsIgnoreCase("WALK_AND_WAIT"))
                    {
                        Sprite walker = GoodFight.getActiveSpriteByScriptLabel(eventLineParts[2]);
                        Dir dir = GoodFight.Dir.valueOf(eventLineParts[3].toUpperCase());                       
                        command.set(new WalkCommand(walker, Integer.parseInt(eventLineParts[4]), dir));
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
        
        index++;
        return event;
    }
    
    
    
    
    
    
    public static Conversation createRandomConversation(Person person, CodeDelegate postCode)
    {
        ArrayList<String> lines = new ArrayList<>();
        if (person.getFamiliarity() <= 0)
        {
            // Introductions are in order...
            lines.add(getIntroductionTo(person));
            lines.add(getIntroductionFrom(person));
        }
        else
        {
            lines.add(getGreetingTo(person));
            lines.add(getGreetingFrom(person));
        }
        return new Conversation(lines.toArray(new String[lines.size()]), postCode, person);        
    }
    
    private static String getIntroductionTo(Person person)
    {                
        String[] pre = {"Hi!", "Hello, $sex.", "Hey there!", "Good day, $sex.", "Greetings.", "Greetings, $sex."};
        String usedPre = pre[Global.getRandomInt(0, pre.length-1)].replace("$sex", person.getSex()==GoodFight.Sex.MALE ? "sir" : "madam");
        
        String[] intro = {"My name is Adam.", "My name is Adam Cesar.", "I'm Adam."};
        String usedIntro = intro[Global.getRandomInt(0, intro.length-1)];
        
        String[] post = {"It's nice to meet you.", "Nice to meet you.", "How are you?", "How's it going?"};
        String usedPost = post[Global.getRandomInt(0, post.length-1)];
        
        return "$`Adam`" + usedPre + " " + usedIntro + " " + usedPost;
    }    
    private static String getIntroductionFrom(Person person)
    {                    
        // Mood is basically determined by how willing the person is to talk about the gospel,
        // together with a random modifier reflecting the chance influences of the day/moment.
        float flex = ((float)(Global.getRandomInt(0, 30)-15))/100f;
        float mood = person.getWillingness() + flex;
        
        String usedPre;
        if (mood <= -0.25 && !person.isSaved())
        {
            String[] pre = {"What do you want?", "Oh no...", "Oh, here we go...", "Yes?", "Hmph."};
            usedPre = pre[Global.getRandomInt(0, pre.length-1)];
            return "$`Conversant`" + usedPre;
        }       
        else if (mood >= 0.25)
        {
            String[] pre = {"Hi Adam.", "Hello.", "Hello, Adam.", "Hey.", "Greetings, Adam."};
            usedPre = pre[Global.getRandomInt(0, pre.length-1)];
        }
        else
        {
            String[] pre = {"Hi!", "Hi Adam!", "Hey Adam! How's it going?", "Hi! How are you?", "Hey, what's up?"};
            usedPre = pre[Global.getRandomInt(0, pre.length-1)];
        }

        String[] intro = {"My name is $name.", "I'm $name."};
        String usedIntro = intro[Global.getRandomInt(0, intro.length-1)].replace("$name", person.getFullName());

        return "$`Conversant`" + usedPre + " " + usedIntro;
    }
    
    private static String getGreetingTo(Person person)
    {
        String[] greetings = {"Hi $name!", "Hello $name!", "Hey $name!"};  
        String greeting = greetings[Global.getRandomInt(0, greetings.length-1)].replace("$name", person.getAppropriateNameToCall());
        return "$`Adam`" + greeting;
    }
    private static String getGreetingFrom(Person person)
    {
        // Mood is basically determined by how willing the person is to talk about the gospel,
        // together with a random modifier reflecting the chance influences of the day/moment.
        float flex = ((float)(Global.getRandomInt(0, 30)-15))/100f;
        float mood = person.getWillingness() + flex;
        
        String usedPre;
        if (mood <= -0.25 && !person.isSaved())
        {
            String[] pre = {"Oh, hello again.", "Uh... you again... hi.", "Oh, here we go...", "Yes??", "Hmph. I remember you."};
            usedPre = pre[Global.getRandomInt(0, pre.length-1)];
            return "$`Conversant`" + usedPre;
        }       
        else if (mood >= 0.25)
        {
            String[] pre = {"Hi Adam.", "Hello.", "Hello, Adam.", "Hey.", "Hey Adam."};
            usedPre = pre[Global.getRandomInt(0, pre.length-1)];
                        
            String[] post = {"", "How's it going?", "What's up?"};
            String usedPost = post[Global.getRandomInt(0, post.length-1)];
            return "$`Conversant`" + usedPre + " " + usedPost;
        }
        else
        {
            String[] pre = {"Hi!", "Hi Adam!", "Hey Adam!", "Hello again!"};
            usedPre = pre[Global.getRandomInt(0, pre.length-1)];
            
            String[] post = {"How are you?", "How are you doing?", "How's it going?", "It's nice to see you again.", "What's up?"};
            String usedPost = post[Global.getRandomInt(0, post.length-1)];
            return "$`Conversant`" + usedPre + " " + usedPost;
        }
    }
}

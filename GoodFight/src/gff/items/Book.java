/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gff.items;

import gff.GoodFight;
import gff.bible.BibleOps;
import gff.bible.BibleReference;
import org.jdom2.Element;

/**
 *
 * @author Ray
 */
public class Book extends Item {
    Element myBookElement = null;
    BibleReference myBibleReference = null;
    String myTitle = null;

    public Book(String bookName) {           
        super(bookName, null, GoodFight.getLoadedImage("items/book.png"));
        myBibleReference = new BibleReference(bookName);
        myBookElement = BibleOps.getScriptureElement(myBibleReference);  
        myTitle = myBookElement.getAttributeValue("title");
        setDescription(getDescriptionOfBook(bookName));
    }
    
    public String getTitle()
    {
        return myTitle;
    }
    
    public BibleReference getReference()
    {
        return myBibleReference;
    }
    
    public Element getElement()
    {
        return myBookElement;
    }

    @Override
    public void onAcquire() {
        super.onAcquire();
        GoodFight.getSubject().acquireBook(this);
    }
    
    public static String getDescriptionOfBook(String name)
    {
        if (name.equalsIgnoreCase("Genesis"))
            return "The first book of Moses, Genesis recounts God's creation of the heavens and the earth, the fall of man, the great flood, the dispersion of the Gentiles from Babel, and God's election of the nation of Israel.";
        if (name.equalsIgnoreCase("Exodus"))
            return "The second book of Moses, Exodus explains how God delivered Israel from bondage in Egypt, the receiving of God's law on Sinai, and the construction of the tabernacle.";
        if (name.equalsIgnoreCase("Leviticus"))
            return "The third book of Moses, Leviticus is an instructional book for the Levitical priesthood describing offerings and ordinances.";
        if (name.equalsIgnoreCase("Numbers"))
            return "The fourth book of Moses, Numbers recounts the forty year wilderness march of the nation of Israel.";
        if (name.equalsIgnoreCase("Deuteronomy"))
            return "The fifth book of Moses, Deuteronomy is Moses' final message to the nation of Israel before his death.";         
        
        
        if (name.equalsIgnoreCase("Matthew"))
            return "The first of the four gospels, Matthew focuses on the life of Jesus Christ as King of the Jews, heir to the throne of David.";
        if (name.equalsIgnoreCase("Mark"))
            return "The gospel according to Mark portrays Jesus Christ as servant and workman, and focuses on the miracles which Jesus performed.";
        if (name.equalsIgnoreCase("Luke"))
            return "Luke writes to a Gentile man, focusing especially on Jesus Christ's authority and wisdom as he taught.";
        if (name.equalsIgnoreCase("John"))
            return "Often considered the most important book in scripture, John's gospel presents the clearest picture of Jesus Christ's person, work, and exclusive power to save.";
        else
            return "";
    }
}

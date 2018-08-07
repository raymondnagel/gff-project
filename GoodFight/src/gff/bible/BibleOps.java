/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gff.bible;

import gff.util.XMLHelper;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.jdom2.Element;

/**
 *
 * @author Raymond
 */
public class BibleOps {
    private static XMLHelper ourXMLHelper = null;
    private static char[] Psalm119Chars = {'א', 'ב', 'ג', 'ד', 'ה', 'ו', 'ז', 'ח', 'ט', 'י', 'כ',
 'ל', 'מ', 'נ', 'ס', 'ע', 'פ', 'צ', 'ק', 'ר', 'ש', 'ת'};
    private static String[] Psalm119Letters = {"Aleph", "Beth", "Gimel", "Daleth", "He", 
                                        "Vau", "Zain", "Cheth", "Teth", "Jod",
                                        "Caph", "Lamed", "Mem", "Nun", "Samech",
                                        "Ain", "Pe", "Tzaddi", "Koph", "Resh",
                                        "Schin", "Tau"};
    
    public static void connectXML()
    {        
        ourXMLHelper = new XMLHelper(new File("extern/text/kjv.xml"));            
        ourXMLHelper.loadDocument();     
    }
    
    public static Element getScriptureElement(BibleReference ref)
    {
        //SAMPLE QUERY: "/KJV/scripture//book[@name='John']/chapter[@no='3']/verse[@no='16']"
        StringBuilder xPathQuery = new StringBuilder("/KJV/scripture");
        if (ref.getStartBookNo() > 0)
        {
            xPathQuery.append("/testament[").append(ref.getTestamentNo()).append("]");
            xPathQuery.append("/book[").append(ref.getStartBookNoInTestament()).append("]");
            if (ref.getStartChapterNo() > 0)
            {
                xPathQuery.append("/chapter[").append(ref.getStartChapterNo()).append("]");
                if (ref.getStartVerseNo() > 0)
                {
                    xPathQuery.append("/verse[").append(ref.getStartVerseNo()).append("]");
                }
            }
            return ourXMLHelper.xPathElement(xPathQuery.toString());
        }
        return null;
    }
    
    public static Element getRandomVerseElementByVerse(ArrayList<String> bookList)
    {
        StringBuilder xPathQuery = new StringBuilder("/KJV/scripture//book[");
        for (int b = 0; b < bookList.size(); b++)
        {
            if (b > 0)
                xPathQuery.append(" or ");
            xPathQuery.append("@name='").append(bookList.get(b)).append("'");
        }
        xPathQuery.append("]//verse");
        List<Element> list = ourXMLHelper.xPathList(xPathQuery.toString());        
        int rnd = new Random(System.nanoTime()).nextInt(list.size());
        return list.get(rnd);
    }
    
    public static Element getRandomVerseElementByBook(ArrayList<String> bookList)
    {
        if (bookList == null || bookList.isEmpty())
        {
            System.err.println("The book selection is empty.");
            return null;
        }
            
        int rnd = new Random(System.nanoTime()).nextInt(bookList.size());        
        StringBuilder xPathQuery = new StringBuilder("/KJV/scripture//book[");
        xPathQuery.append("@name='").append(bookList.get(rnd)).append("'");        
        xPathQuery.append("]//verse");
        List<Element> list = ourXMLHelper.xPathList(xPathQuery.toString());        
        rnd = new Random(System.nanoTime()).nextInt(list.size());
        return list.get(rnd);
    }
    
}

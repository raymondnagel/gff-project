/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gff.bible;

import gff.util.IntegerHelper;
import gff.util.StringHelper;
import org.jdom2.Element;

/**
 *
 * @author rnagel
 */
public class BibleReference 
{
    private int[] myBook = new int[2];
    private int[] myChapter = new int[2];
    private int[] myVerse = new int[2];
    private enum PART_TYPES {UNKNOWN, BOOK, CHAPTER, VERSE, COLON, DASH};
    
    public final static String[] BookNames = {"Genesis", "Exodus", "Leviticus", "Numbers", 
    "Deuteronomy", "Joshua", "Judges", "Ruth", "1 Samuel", "2 Samuel", "1 Kings",
    "2 Kings", "1 Chronicles", "2 Chronicles", "Ezra", "Nehemiah", "Esther", "Job", 
    "Psalms", "Proverbs", "Ecclesiastes", "Song of Solomon", "Isaiah", "Jeremiah", 
    "Lamentations", "Ezekiel", "Daniel", "Hosea", "Joel", "Amos", "Obadiah", "Jonah", 
    "Micah", "Nahum", "Habakkuk", "Zephaniah", "Haggai", "Zechariah", "Malachi",
    "Matthew", "Mark", "Luke", "John", "Acts", "Romans", "1 Corinthians", 
    "2 Corinthians", "Galatians", "Ephesians", "Philippians", "Colossians", 
    "1 Thessalonians", "2 Thessalonians", "1 Timothy", "2 Timothy", "Titus", 
    "Philemon", "Hebrews", "James", "1 Peter", "2 Peter", "1 John", "2 John", 
    "3 John", "Jude", "Revelation"};
    public final static String[] ShortNames = {"Gen", "Exd", "Lev", "Num", "Deut", "Josh",
    "Judg", "Ruth", "1Sam", "2Sam", "1Kng", "2Kng", "1Chr", "2Chr", "Ezra", "Neh",
    "Est", "Job", "Pslm", "Prov", "Eccl", "Song", "Isa", "Jer", "Lam", "Ezk", "Dan",
    "Hos", "Joel", "Amos", "Obad", "Jon", "Mic", "Nah", "Hab", "Zeph", "Hag", "Zech",
    "Mal", "Mat", "Mark", "Luke", "John", "Acts", "Rom", "1Cor", "2Cor", "Gal", 
    "Eph", "Phlp", "Col", "1Ths", "2Ths", "1Tim", "2Tim", "Tit", "Phle", "Heb", 
    "Jam", "1Pet", "2Pet", "1Jon", "2Jon", "3Jon", "Jude", "Rev"};
    public final static String[] NameWords = {"Genesis", "Exodus", "Leviticus", "Numbers", 
    "Deuteronomy", "Joshua", "Judges", "Ruth", "Samuel","Kings", "Chronicles", "Ezra", 
    "Nehemiah", "Esther", "Job", "Psalms", "Proverbs", "Ecclesiastes", "Song of Solomon",
    "Isaiah", "Jeremiah", "Lamentations", "Ezekiel", "Daniel", "Hosea", "Joel", 
    "Amos", "Obadiah", "Jonah", "Micah", "Nahum", "Habakkuk", "Zephaniah", "Haggai",
    "Zechariah", "Malachi", "Matthew", "Mark", "Luke", "John", "Acts", "Romans", 
    "Corinthians", "Galatians", "Ephesians", "Philippians", "Colossians", 
    "Thessalonians", "Timothy", "Titus", "Philemon", "Hebrews", "James", "Peter",
    "Jude", "Revelation"};
    public final static String[] ShortWords = {"Gen", "Exd", "Lev", "Num", "Deut", "Josh",
    "Judg", "Ruth", "Sam", "Kng", "Chr", "Ezra", "Neh", "Est", "Job", "Pslm", "Prov", 
    "Eccl", "Song", "Isa", "Jer", "Lam", "Ezk", "Dan", "Hos", "Joel", "Amos", "Obad",
    "Jon", "Mic", "Nah", "Hab", "Zeph", "Hag", "Zech", "Mal", "Mat", "Mark", "Luke",
    "John", "Acts", "Rom", "Cor", "Gal", "Eph", "Phlp", "Col", "Ths", "Tim", "Tit",
    "Phle", "Heb", "Jam", "Pet", "Jude", "Rev"};
    public final static String[] ShortBooks = {"Obad", "Phle", "2Jon", "3Jon", "Jude"};
   
    
    public void setReference(String reference) throws NumberFormatException
    {
        for (int refIdx = 0; refIdx <= 1; refIdx++)
        {
            myBook[refIdx] = -1;
            myChapter[refIdx] = -1;
            myVerse[refIdx] = -1;
        }
        boolean hasError = false;
        try
        {
            StringBuilder buffer = new StringBuilder("");
            char[] chars = reference.toLowerCase().replaceAll("iii", "3").replaceAll("ii", "2").toCharArray();
            int lastCharType = Character.UNASSIGNED;
            for (char c:chars)
            {
                if (lastCharType != Character.UNASSIGNED && lastCharType != Character.getType(c) && c != ' ')
                {
                    buffer.append(' ');
                }
                if (c != ' ') buffer.append(c);
                lastCharType = Character.getType(c);
            }

            //Resolve book titles
            String[] parts = buffer.toString().split(" ");
            for (int pIdx = 0; pIdx < parts.length; pIdx++)
            {
                String bookName = resolveBookName(parts[pIdx]);
                if (bookName != null) parts[pIdx] = bookName;
            }

            //Unify numbered book titles
            String ref2 = StringHelper.join(parts, " ");
            for (int bIdx = 0; bIdx < BookNames.length; bIdx++)
            {
                ref2 = ref2.replaceAll(BookNames[bIdx], ShortNames[bIdx]);
            } 
            if (StringHelper.countOccurences(ref2, "-") > 1)
            {
                hasError = true;
            }
            String[] references = ref2.split("-");

            for (int refIdx = 0; refIdx < references.length; refIdx++)
            {     
                parts = references[refIdx].trim().split(" ");
                PART_TYPES[] types = new PART_TYPES[parts.length];

                for (int pI = 0; pI < parts.length; pI++)
                {
                    if (confirmShortTitle(parts[pI]))
                    {
                        types[pI] = PART_TYPES.BOOK;
                    }
                    else if (parts[pI].equalsIgnoreCase(":"))
                    {
                        types[pI] = PART_TYPES.COLON;
                        types[pI-1] = PART_TYPES.CHAPTER;
                        types[pI+1] = PART_TYPES.VERSE;
                    }
                    else if (IntegerHelper.isAnInteger(parts[pI]))
                    {
                        //Must be either a chapter # or verse #
                        if (pI < 1) 
                        {
                            if (refIdx==0) hasError = true;
                            //If refIdx is 1, book and chapter can be optionally ommitted.
                            //Call it a verse for now; if a colon is present later, it will
                            //change to a chapter.
                            types[pI] = PART_TYPES.VERSE;
                        }

                        //Is it a chapter #?
                        else if (types[pI-1] == PART_TYPES.BOOK && !confirm1ChapterBook(parts[pI-1]))
                        {
                            types[pI] = PART_TYPES.CHAPTER;
                        }

                        //Is it a verse #?
                        else if (types[pI-1] == PART_TYPES.COLON || confirm1ChapterBook(parts[pI-1]))
                        {
                            types[pI] = PART_TYPES.VERSE;
                        }
                    }
                }  
                for (int pI = 0; pI < parts.length; pI++)
                {
                    switch (types[pI])
                    {
                        case UNKNOWN:
                            hasError = true; break;
                        case BOOK:
                            myBook[refIdx] = getShortTitleIndex(parts[pI]); break;
                        case CHAPTER:
                            myChapter[refIdx] = Integer.parseInt(parts[pI]); break;
                        case VERSE:
                            myVerse[refIdx] = Integer.parseInt(parts[pI]); break;
                    }
                }   
            }
            if (myVerse[1] == -1) myVerse[1] = myVerse[0];
            if (myChapter[1] == -1) myChapter[1] = myChapter[0];
            if (myBook[1] == -1) myBook[1] = myBook[0];
        }
        catch (Exception ex)
        {
            hasError = true;
        }
        finally
        {
            if (hasError) throw new NumberFormatException();
        }
    }
    public void setReference(int book)
    {
        myBook[0] = book;
        myBook[1] = book;
    }
    public void setReference(int book, int chapter)
    {
        setReference(book);
        myChapter[0] = chapter;
        myChapter[1] = chapter;
    }
    public void setReference(int book, int chapter, int verse)
    {
        setReference(book, chapter);
        myVerse[0] = verse;
        myVerse[1] = verse;
    }
    public void setReference(int book, int chapter, int startVerse, int endVerse)
    {
        setReference(book, chapter);
        myVerse[0] = startVerse;
        myVerse[1] = endVerse;
    }
    public void setReference(int book, int startChapter, int endChapter, int startVerse, int endVerse)
    {
        setReference(book);
        myChapter[0] = startChapter;
        myChapter[1] = endChapter;
        myVerse[0] = startVerse;
        myVerse[1] = endVerse;
    }
    public void setReference(int startBook, int endBook, int startChapter, int endChapter, int startVerse, int endVerse)
    {
        myBook[0] = startBook;
        myBook[1] = endBook;
        myChapter[0] = startChapter;
        myChapter[1] = endChapter;
        myVerse[0] = startVerse;
        myVerse[1] = endVerse;
    } 
    public String getReference()
    {
        StringBuilder ref = new StringBuilder("");
        if (myBook[0] != -1)
        {
            ref.append(BookNames[myBook[0]-1]);
            if (myChapter[0] != -1 || myVerse[0] != -1)
                ref.append(" ");
        }
        if (myChapter[0] != -1)
        {
            ref.append(myChapter[0]);
            if (myVerse[0] != -1)
                ref.append(":");
        }
        if (myVerse[0] != -1)
        {
            ref.append(myVerse[0]);
        }
        if (myBook[1] != myBook[0] || myChapter[1] != myChapter[0] || myVerse[1] != myVerse[0])
        {
            ref.append("-");
            if (myBook[1] != myBook[0])
            {
                ref.append(BookNames[myBook[1]]);
                if (myChapter[1] != -1 || myVerse[1] != -1)
                    ref.append(" ");
            }
            if (myChapter[1] != -1 && (myBook[1] != myBook[0] || myChapter[1] != myChapter[0]))
            {
                ref.append(myChapter[1]);
                if (myVerse[1] != -1)
                    ref.append(":");
            }
            if (myVerse[1] != -1)
            {
                ref.append(myVerse[1]);
            }
        }    
        return ref.toString();
    }
    public String getTestamentName()
    {
        if (myBook[0] >= 1 && myBook[0] <= 39)
            return "Old Testament";
        else if (myBook[0] >= 40 && myBook[0] <= 66)
            return "New Testament";
        else
            return null;
    }
    public int getTestamentNo()
    {
        if (myBook[0] >= 1 && myBook[0] <= 39)
            return 1;
        else
            return 2;
    }
    public String getStartBookName()
    {
        if (myBook[0] == -1)
            return null;
        else
            return BookNames[myBook[0]-1];
    }
    public String getEndBookName()
    {
        if (myBook[1] == -1)
            return null;
        else
            return BookNames[myBook[1]-1];
    }
    public int getStartBookNo()
    {
        return myBook[0];
    }
    public int getEndBookNo()
    {
        return myBook[1];
    }
    public int getStartBookNoInTestament()
    {
        if (myBook[0] >= 1 && myBook[0] <= 39)
            return myBook[0];
        else
            return myBook[0]-39;
    }
    public int getEndBookNoInTestament()
    {
        if (myBook[1] >= 1 && myBook[1] <= 39)
            return myBook[1];
        else
            return myBook[1]-39;
    }
    public int getStartChapterNo()
    {
        return myChapter[0];
    }
    public int getEndChapterNo()
    {
        return myChapter[1];
    }
    public int getStartVerseNo()
    {
        return myVerse[0];
    }
    public int getEndVerseNo()
    {
        return myVerse[1];
    }
    
    
    public static int getShortTitleIndex(String shortTitle)
    {
        for (int t = 0; t < ShortNames.length; t++)
        {
            if (shortTitle.equalsIgnoreCase(ShortNames[t])) return t+1;
        }
        return -1;
    }
    public static int getFullTitleIndex(String fullTitle)
    {
        for (int t = 0; t < BookNames.length; t++)
        {
            if (fullTitle.equalsIgnoreCase(BookNames[t])) return t+1;
        }
        return -1;
    }
    public static String getShortTitleAt(int index)
    {
        return ShortNames[index-1];
    }
    public static String getFullTitleAt(int index)
    {
        return BookNames[index-1];
    }
    
    
    private String resolveBookName(String bookName)
    {
        if (IntegerHelper.isAnInteger(bookName)) return null;
        if (bookName.trim().length() == 1)
        {
            if (!Character.isLetter(bookName.trim().charAt(0))) return null;
        }

        //Easiest match: does the bookName match the name of a book or an abbreviation?
        for (int bkI = 0; bkI < NameWords.length; bkI++)
        {          
            if (bookName.equalsIgnoreCase(NameWords[bkI]) ||
                bookName.equalsIgnoreCase(ShortWords[bkI]))
            {
                return NameWords[bkI];
            }   
        }
        //More difficult match: can we find a book to match the characters in sequence?
        char[] chars = bookName.toLowerCase().toCharArray();
        int[] scores = new int[NameWords.length];
        for (int bkI = 0; bkI < NameWords.length; bkI++)
        {    
            String book = NameWords[bkI].toLowerCase();
            int lastFoundChar = -1;
            int foundChar = -1;
            for (int cI = 0; cI < chars.length; cI++)
            {
                foundChar = book.indexOf(chars[cI], lastFoundChar+1);
                if (foundChar != -1)
                {
                    scores[bkI]++;
                    lastFoundChar = foundChar;
                }
            }
        }
        
        int highScore = 0;
        int highIndex = -1;
        for (int bkI = 0; bkI < NameWords.length; bkI++)
        {   
            if (highScore < scores[bkI])
            {
                highScore = scores[bkI];
                highIndex = bkI;
            }
        }
        
        if (highIndex == -1)
            return null;
        else
            return NameWords[highIndex];
    }
    
    private boolean confirmShortTitle(String bookTitle)
    {
        for (String title: ShortNames)
        {
            if (bookTitle.equalsIgnoreCase(title)) return true;
        }
        return false;
    }
    
    public static boolean confirm1ChapterBook(String bookTitle)
    {
        for (String title: ShortBooks)
        {
            if (bookTitle.equalsIgnoreCase(title)) return true;
        }
        return false;
    }
    
    public BibleReference()
    {
        
    }
    
    public BibleReference(int book, int chapter, int verse)
    {
        setReference(book, chapter, verse);
    }
    
    public BibleReference(String reference)
    {
        setReference(reference);
    }
    
    public BibleReference(Element verseEl)
    {
        Element chapterEl = verseEl.getParentElement();
        int verse = Integer.parseInt(verseEl.getAttributeValue("no"));
        int chapter = Integer.parseInt(chapterEl.getAttributeValue("no"));
        String book = chapterEl.getParentElement().getAttributeValue("name");
        setReference(book + " " + chapter + ":" + verse);
    }
    
    @Override
    public String toString()
    {
        return getReference();
    }
    
    @Override
    public boolean equals(Object reference)
    {
        if (reference instanceof BibleReference)
        {
            BibleReference ref = (BibleReference)reference;
            return (ref.getStartBookNo() == this.getStartBookNo() &&
                    ref.getStartChapterNo() == this.getStartChapterNo() &&
                    ref.getStartVerseNo() == this.getStartVerseNo() &&
                    ref.getEndBookNo() == this.getEndBookNo() &&
                    ref.getEndChapterNo() == this.getEndChapterNo() &&
                    ref.getEndVerseNo() == this.getEndVerseNo());
        }
        else
            return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + (this.myBook != null ? this.myBook.hashCode() : 0);
        hash = 23 * hash + (this.myChapter != null ? this.myChapter.hashCode() : 0);
        hash = 23 * hash + (this.myVerse != null ? this.myVerse.hashCode() : 0);
        return hash;
    }
    
}

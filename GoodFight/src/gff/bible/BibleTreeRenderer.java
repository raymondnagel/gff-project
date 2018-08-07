/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gff.bible;

import gff.util.IntegerHelper;
import java.awt.Component;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

/**
 *
 * @author rnagel
 */
public class BibleTreeRenderer extends DefaultTreeCellRenderer {
    ImageIcon BibleClosedIcon = null;
    ImageIcon BibleOpenIcon = null;
    ImageIcon OTClosedIcon = null;
    ImageIcon OTOpenIcon = null;
    ImageIcon NTClosedIcon = null;
    ImageIcon NTOpenIcon = null;
    ImageIcon BookClosedIcon = null;
    ImageIcon BookOpenIcon = null;
    ImageIcon ChapterClosedIcon = null;
    ImageIcon ChapterOpenIcon = null;
    ImageIcon VerseIcon = null;

    public BibleTreeRenderer() {
        BibleClosedIcon = new ImageIcon("res/icons/bible_closed.png");
        BibleOpenIcon = new ImageIcon("res/icons/bible_open.png");
        OTClosedIcon = new ImageIcon("res/icons/lamb.png");
        OTOpenIcon = new ImageIcon("res/icons/lamb.png");
        NTClosedIcon = new ImageIcon("res/icons/cross.png");
        NTOpenIcon = new ImageIcon("res/icons/cross.png");
        BookClosedIcon = new ImageIcon("res/icons/book_closed.png");
        BookOpenIcon = new ImageIcon("res/icons/book_open.png");
        ChapterClosedIcon = new ImageIcon("res/icons/chapter_closed.png");
        ChapterOpenIcon = new ImageIcon("res/icons/chapter_open.png");
        VerseIcon = new ImageIcon("res/icons/verse.png");
    }

    @Override
    public Component getTreeCellRendererComponent(
                        JTree tree,
                        Object value,
                        boolean sel,
                        boolean expanded,
                        boolean leaf,
                        int row,
                        boolean hasFocus) {
        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
        if (value.toString().toLowerCase().indexOf("bible") != -1)
        {
            //Must be bible
            if (expanded) setIcon(BibleOpenIcon); else setIcon(BibleClosedIcon);
        }
        else if (value.toString().toLowerCase().indexOf("old testament") != -1)
        {
            //Must be OT
            if (expanded) setIcon(OTOpenIcon); else setIcon(OTClosedIcon);
        }
        else if (value.toString().toLowerCase().indexOf("new testament") != -1)
        {
            //Must be NT
            if (expanded) setIcon(NTOpenIcon); else setIcon(NTClosedIcon);
        }
        else if (!IntegerHelper.isAnInteger(value.toString()))
        {
            //Must be book
            if (expanded) setIcon(BookOpenIcon); else setIcon(BookClosedIcon);
        } 
        else if (!leaf && IntegerHelper.isAnInteger(value.toString()))
        {
            //Must be chapter
            if (expanded) setIcon(ChapterOpenIcon); else setIcon(ChapterClosedIcon);
        }
        else if (leaf && IntegerHelper.isAnInteger(value.toString()))
        {
            //Must be verse
            setIcon(VerseIcon);
        }
        return this;
    }

}

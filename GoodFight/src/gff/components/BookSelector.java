/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gff.components;

import gff.Global;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

/**
 *
 * @author Ray
 */
public class BookSelector extends InterfaceComponent{
    private final static int COL_WIDTH = 100;
    private final static int ROW_HEIGHT = 20;
    
    private int highlightedBook = -1;
    private int rows = 0;
    private int columns = 0;
    private Point centerLocation = null;
    private ArrayList<String> bookList = null;

    public BookSelector(Point centerLocation) { 
        this.centerLocation = centerLocation;
    }

    public String getSelectedBookName()
    {
        if (highlightedBook == -1)
            return "?";
        else
            return bookList.get(highlightedBook);
    }
    
    public void resetToBooklist(ArrayList<String> bookList)
    {
        this.bookList = bookList;
        if (bookList.size() <= 10)
            columns = 1;
        else if (bookList.size() <= 20)
            columns = 2;
        else
            columns = 3;
        
        rows = bookList.size()/columns;        
        if (rows < ((float)bookList.size()/(float)columns))
        {
            rows ++;
        }
                
        setSize(new Dimension(columns * COL_WIDTH, rows * ROW_HEIGHT));
        setLocation(new Point(this.centerLocation.x-(getWidth()/2), this.centerLocation.y-(getHeight()/2)));
    }
    
    private int getBookIndexForPoint(Point p)
    {
        int bookIndex = -1;
        int col = (p.x - getX()) / COL_WIDTH;
        int row = (p.y - getY()) / ROW_HEIGHT;
        bookIndex = (col*rows)+row;
        
        if (bookIndex < 0 || bookIndex >= bookList.size())
            return -1;
        else
            return bookIndex;
    }

    @Override
    protected void mouseMoveAction(MouseEvent e) {
        super.mouseMoveAction(e);
        highlightedBook = getBookIndexForPoint(e.getPoint());
    }

    @Override
    protected void mouseReleaseAction(MouseEvent e, boolean within) {
        super.mouseReleaseAction(e, within);
        setVisible(false);
    }

    @Override
    protected void mouseEnterAction(MouseEvent e) {
        super.mouseEnterAction(e);    
        myMouseDown = true;        
        highlightedBook = getBookIndexForPoint(e.getPoint());
    }
    
    @Override
    protected void mouseExitAction(MouseEvent e) {
        super.mouseExitAction(e);
        highlightedBook = -1; // getBookIndexForPoint() would return -1 anyway
    }
    
    @Override
    protected void paintContent(Graphics2D g) {
        super.paintContent(g);        
        g.setColor(Color.YELLOW.brighter().brighter().brighter());
        g.fillRect(getX(), getY(), getWidth(), getHeight());                

        g.setColor(Color.YELLOW);
        g.drawRect(getX(), getY(), getWidth()-1, getHeight()-1);

        int bookIndex = 0;
        for (int x = 0; x < columns; x++)
            for (int y = 0; y < rows; y++)
            {
                paintCell(x, y, bookIndex, bookIndex==highlightedBook, g);
                bookIndex++;
                if (bookIndex >= bookList.size())
                    return;
            }
    }
    
    private void paintCell(int col, int row, int book, boolean isHighlighted, Graphics g)
    {
        String name = bookList.get(book);
        g.setFont(Global.SimpleFont.deriveFont(12f));
        int halfWidth = (int)(g.getFontMetrics().getStringBounds(name, g).getWidth()/2.0);

        if (isHighlighted)
        {
            g.setColor(Color.BLUE);
            g.fillRect(getX()+(col*COL_WIDTH), getY()+(row*ROW_HEIGHT), COL_WIDTH, ROW_HEIGHT);
            g.setColor(Color.WHITE);
        }
        else
            g.setColor(Color.BLACK);
        
        int x = getX() + (col*COL_WIDTH) + ((COL_WIDTH/2) - halfWidth);
        int y = (getY() + (row*ROW_HEIGHT) + g.getFontMetrics().getAscent()) + 1;
        g.drawString(name,  x, y);
    }
    
}

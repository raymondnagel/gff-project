/**
 * XMLHelper.java
 * Created on February 14, 2007, 11:18 AM
 * @author rnagel
 */

package gff.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.filter.Filter;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.jdom2.xpath.XPath;

/**
 * Convenience class for using an XML document via JDom. Contains
 * methods for loading a document and using XPath to query it.
 * Also contains <code>drillDownForData</code> and <code>drillDownForElements</code>
 * methods, which operate much faster than
 * XPath for finding simple matches in a small range.
 * There should be an instance of this class for every XML document to be open
 * simultaneously.
 * 
 * @author   rnagel
 * @version  1.0
 * @see      JDom
 * @since    JDK1.6.1
 */
public final class XMLHelper
{
    //____________________PRIVATE FIELDS____________________//
    
    private SAXBuilder      mySAXBuilder = new SAXBuilder();
    private Document        myDocument;
    private URL             myDocumentURL = null;
    private XPath           myXPathQuery;
    
    //____________________PROPERTY METHODS____________________//
    
   /**
    * Returns the <code>URL</code> of the currently loaded XML document.
    * 
    * @return    the <code>URL</code> of the XML document, or <code>null</code>
    *            if a document has not been loaded.
    * @see       #setDocument<code>URL</code>(<code>URL</code>)
    * @see       #loadDocument()
    */
    public URL getDocumentURL()
    {
        return myDocumentURL;
    }
    
   /**
    * Sets the <code>URL</code> of the XML document.
    * {@link #loadDocument()} must still be called before this instance
    * can use the document.
    * 
    * @param     documentURL    the <code>URL</code> of an XML document
    *            to be loaded later
    * @see       #loadDocument()
    */
    public void setDocumentURL(URL documentURL)
    {
        myDocumentURL = documentURL;
    }
    
   /**
    * Returns the XML document (JDom) used by this instance.
    * 
    * @return    the JDom <code>Document</code> used by this instance.
    * @see       #loadDocument()
    */
    public Document getDocument()
    {
        return myDocument;
    }

    public String getDocumentString()
    {
        if (myDocument != null)
        {
            XMLOutputter xout = new XMLOutputter(Format.getPrettyFormat());
            return xout.outputString(myDocument);
        }
        else return null;
    }

    //____________________CONSTRUCTOR METHODS____________________//

    public XMLHelper()
    {

    }

    public XMLHelper(Element rootElement)
    {
        makeNewDocument(rootElement);
    }

    public XMLHelper(String rootElementName)
    {
        makeNewDocument(rootElementName);
    }
    
   /**
    * Creates a new XMLHelper and specifies the <code>URL</code> for its XML document.
    * 
    * @see       #XMLHelper(File)
    * @see       #loadDocument()
    */
    public XMLHelper(URL documentURL)
    {
        setDocumentURL(documentURL);
    }
    
   /**
    * Creates a new XMLHelper and specifies the file for its XML document.
    * 
    * @see       #XMLHelper(URL)
    * @see       #loadDocument()
    */
    public XMLHelper(File docFile)
    {
        try {setDocumentURL(docFile.toURI().toURL());}catch(Exception ex){}
    }
    
    //____________________PUBLIC INTERFACE METHODS____________________//

    public void makeNewDocument(String rootElementName)
    {
        makeNewDocument(new Element(rootElementName));
    }

    public void makeNewDocument(Element rootElement)
    {
        myDocument = new Document(rootElement);
    }

   /**
    * Loads the XML document from the URL that has been set with setDocumentURL(URL).
    * The document will remain in memory until
    * this instance is destroyed or it loads a different document.
    * 
    * @return    <code>true</code> if the document was loaded successfully,
    *            <code>false</code> if the document was not loaded.
    */
    public boolean loadDocument()
    {
        try
        {
            myDocument = mySAXBuilder.build(myDocumentURL);
            return true;
        }
        catch(JDOMException | IOException ex)
        {
            return false;
        }
    }
    
    public boolean saveDocument(File aFile)
    {
        XMLOutputter xOut = new XMLOutputter();
        if (!aFile.exists()) 
            try
            {
                aFile.createNewFile();
            } catch (IOException ex)
            {
                ex.printStackTrace();
            }
        try
        {
            xOut.setFormat(Format.getPrettyFormat());
            ReadWriteTextFile.setContents(aFile, xOut.outputString(myDocument));
        } catch (FileNotFoundException ex)
        {
            ex.printStackTrace();
            return false;
        } catch (IOException ex)
        {
            ex.printStackTrace();
            return false;
        }
        return true;
    }   
    
    
   /**
    * Returns an XPath result as a single JDom <code>Element</code>.
    * This method should be used to execute an XPath that will return
    * only 1 result <code>Element</code>. If the XPath contained
    * multiple results, only the first will be returned.
    * 
    * @param     xPathQuery    the XPath query to execute
    * @return    the resulting <code>Element</code>; if there are multiple
    *            results, returns only the first one
    * @see       #xPathElement(String, Object)
    */
    public Element xPathElement(String xPathQuery)
    {
        return xPathElement(xPathQuery, myDocument);
    }
       
   /**
    * Returns an XPath result as a single JDom <code>Element</code>.
    * This method should be used to execute an XPath that will return
    * only 1 result <code>Element</code>. If the XPath contained
    * multiple results, only the first will be returned. Begins searching for
    * results at the specified <code>startContext</code>.
    * 
    * @param     xPathQuery    the XPath query to execute
    * @param     startContext  the node at which to begin searching for results
    * @return    the resulting <code>Element</code>; if there are multiple
    *            results, returns only the first one
    * @see       #xPathElement(String)
    */
    public Element xPathElement(String xPathQuery, Object startContext)
    {
        try
        {
            myXPathQuery = XPath.newInstance(xPathQuery);
            Element result = (Element)myXPathQuery.selectSingleNode(startContext);
            return result;
        }
        catch(Exception ex)
        {
            System.err.println(ex.toString());
            return null;
        }
    }
    
   /**
    * Returns an XPath result as a <code>List</code> of JDom <code>Element</code>s.
    * This method can be used to execute an XPath that will return
    * any number of result <code>Element</code>s.
    * 
    * @param     xPathQuery    the XPath query to execute
    * @return    a <code>List</code> containing the resulting Elements.
    * @see       #xPathList(String, Object)
    */
    public List<Element> xPathList(String xPathQuery)
    {
        return xPathList(xPathQuery, myDocument);
    }
    
   /**
    * Returns an XPath result as a <code>List</code> of JDom <code>Element</code>s.
    * This method can be used to execute an XPath that will return
    * any number of result <code>Element</code>s. Begins searching
    * for results at the specified <code>startContext</code>.
    * 
    * @param     xPathQuery    the XPath query to execute
    * @param     startContext  the node at which to begin searching
    *            for results
    * @return    a <code>List</code> containing the resulting Elements.
    * @see       #xPathList(String)
    */
    public List<Element> xPathList(String xPathQuery, Object startContext)
    {
        try
        {
            myXPathQuery = XPath.newInstance(xPathQuery);
            List<Element> result = (List<Element>)myXPathQuery.selectNodes(startContext);
            return result;
        }
        catch(Exception ex)
        {
            System.err.println(ex.toString());
            return null;
        }
    }
    
   /**
    * Returns an XPath result as a single <code>String</code>.
    * This method should be used to execute an XPath that will result
    * in only 1 text value. If the XPath contained multiple results,
    * only the first will be returned.
    * 
    * @param     xPathQuery    the XPath query to execute
    * @return    the resulting text value; if there are multiple
    *            results, returns only the first one
    * @see       #xPathStringResult(String, Object)
    */
    public String xPathStringResult(String xPathQuery)
    {
        return xPathStringResult(xPathQuery, myDocument);
    }
   
    /**
    * Returns an XPath result as a single <code>String</code>.
    * This method should be used to execute an XPath that will result
    * in only 1 text value. If the XPath contained multiple results,
    * only the first will be returned. Begins searching for
    * results at the specified <code>startContext</code>.
    * 
    * @param     xPathQuery    the XPath query to execute
    * @param     startContext  the node at which to begin searching
    *            for results
    * @return    the resulting text value; if there are multiple
    *            results, returns only the first one
    * @see       #xPathStringResult(String)
    */
    public String xPathStringResult(String xPathQuery, Object startContext)
    {
        try
        {
            myXPathQuery = XPath.newInstance(xPathQuery);
            return ((Element)myXPathQuery.selectSingleNode(startContext)).getText();
        }
        catch(Exception ex)
        {
            System.err.println(ex.toString());
            return null;
        }
    }
    
   /**
    * Returns an XPath result as a <code>List</code> of <code>String</code>s.
    * This method can be used to execute an XPath that will result
    * in any number of text values.
    * 
    * @param     xPathQuery    the XPath query to execute
    * @return    a <code>List</code> containing the resulting text
    *            values.
    * @see       #xPathList(String, Object)
    */
    public List<String> xPathStringList(String xPathQuery)
    {
        return xPathStringList(xPathQuery, myDocument);
    }
    
   /**
    * Returns an XPath result as a <code>List</code> of <code>String</code>s.
    * This method can be used to execute an XPath that will result
    * in any number of text values. Begins searching for results at
    * the specified <code>startContext</code>.
    * 
    * @param     xPathQuery    the XPath query to execute
    * @param     startContext  the node at which to begin searching
    *            for results
    * @return    a <code>List</code> containing the resulting text
    *            values.
    * @see       #xPathList(String)
    */
    public List<String> xPathStringList(String xPathQuery, Object startContext)
    {
        try
        {
            myXPathQuery = XPath.newInstance(xPathQuery);
            List<String> result = (List<String>)myXPathQuery.selectNodes(startContext);
            return result;
        }
        catch(Exception ex)
        {
            System.err.println(ex.toString());
            return null;
        }
    }  
    
   /**
    * Returns an XPath result that translates into an integer value.
    * This method should not be used for an XPath that is designed
    * to return data FROM the document (<code>Element</code> or
    * text node), but rather ABOUT the document (such as a count()
    * or sum() function).
    * 
    * @param     xPathQuery    the XPath query to execute
    * @return    an <code>Integer</code> object containing the value
    *            returned by the XPath
    */
    public Integer xPathIntResult(String xPathQuery)
    {
        return xPathIntResult(xPathQuery, myDocument);
    }
    
   /**
    * Returns an XPath result that translates into an integer value.
    * This method should not be used for an XPath that is designed
    * to return data FROM the document (<code>Element</code> or
    * text node), but rather ABOUT the document (such as a count()
    * or sum() function).
    * 
    * @param     xPathQuery    the XPath query to execute
    * @param     startContext  the node at which to begin
    * @return    an <code>Integer</code> object containing the value
    *            returned by the XPath
    */
    public Integer xPathIntResult(String xPathQuery, Object startContext)
    {
        try
        {
            myXPathQuery = XPath.newInstance(xPathQuery);
            return ((Double)myXPathQuery.selectSingleNode(startContext)).intValue();
        }
        catch(Exception ex)
        {
            System.err.println(ex.toString());
            return null;
        }
    }    
    
   /**
    * Returns an <code>ArrayList</code> of <code>String</code>s containing
    * the text values of every <code>Element</code> with a
    * certain name that is descended from the specified starting
    * <code>Element</code>. Much faster than an XPath query for small
    * ranges.
    * 
    * @param     startElement     the JDom <code>Element</code> at
    *            which to begin searching
    * @param     finalElementName the name of <code>Element</code>s
    *            to search for
    * @return    an <code>ArrayList</code> of <code>String</code>s
    *            containing the text items found
    */
    public ArrayList<String> drillDownForData(Element startElement, final String finalElementName)
    {
        ArrayList<String> finalElements = new ArrayList<String>(0);
        Filter crit = new Filter(){
            @Override
            public boolean matches(Object obj)
            {   
                if(!(obj instanceof Element))
                    return false;
                if(!((Element)obj).getName().equals(finalElementName))
                    return false;
                return true;}

            @Override
            public List filter(List content) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public Object filter(Object content) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public Filter negate() {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public Filter or(Filter filter) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public Filter and(Filter filter) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public Filter refine(Filter filter) {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        };
        Iterator walker = startElement.getDescendants(crit);
        if (!walker.hasNext()) finalElements.add("");
        while(walker.hasNext())
        {
            Element desc = (Element)walker.next();
            finalElements.add(desc.getTextTrim());
        }
        return finalElements;
    }
    
}

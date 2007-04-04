import java.io.IOException;
import java.util.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * @author The Blue Overdose Project
 * E-mail: blueover AT gmail com
 * Phlooder Website: http://code.google.com/p/phlooder
 * */
class PhishTank{
	/**
	 * Loads all the PhishTank XML data of the online, valid  
	 * phishing sites, and put the first 10  into the checkBoxes 
	 * ArrayList
	 * */
	public static ArrayList<URIBox> getPhishTank()
    {
    	Document document=null;
    	ArrayList<URIBox> checkBoxes=new ArrayList<URIBox>();
        
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setValidating(false);   
        factory.setNamespaceAware(false);
        try {
           DocumentBuilder builder = factory.newDocumentBuilder();
           //document = builder.parse("http://localhost/shared/index.xml");
           document = builder.parse("http://data.phishtank.com/data/online-valid/index.php");
        } catch (SAXException sxe) {
           Exception  x = sxe;
           if (sxe.getException() != null)
               x = sxe.getException();
           x.printStackTrace();
           System.out.println("Error generated during parsing!");

        } catch (ParserConfigurationException pce) { 
            System.out.println("Parser configuration error!");
            checkBoxes.clear();
            return null;

        } catch (IOException ioe) {
        	System.out.println("I/O error!");
        	checkBoxes.clear();
        	return null;
          }
        NodeList entryList = document.getElementsByTagName("entry");
        Node entry;
        
        for (int i=0; (i<entryList.getLength()) && (i<10); i++) {
            entry = entryList.item(i);
            Element e=(Element)entry;
            NodeList URLList=e.getElementsByTagName("url");
            Element URLElement=(Element)URLList.item(0);
            NodeList textFNList=URLElement.getChildNodes();
            
            checkBoxes.add(new URIBox(((Node)textFNList.item(0)).getNodeValue()));
        }
       
        return checkBoxes;
    }
}
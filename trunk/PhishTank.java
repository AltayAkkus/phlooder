/**
 * @author The Blue Overdose Project
 * E-mail: blueover AT gmail com
 * Phlooder Website: http://code.google.com/p/phlooder
 * */

import java.io.*;
import java.util.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.net.*;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.util.DateParser;
import org.xml.sax.SAXException;

/**
 * This class contains the methods used to fetch information 
 * from PhishTank.
 * */
class PhishTank{
	/**
	 * Downloads the fresh XML and updates the lock.
	 * Automatically creates the <code>config/</code> directory 
	 * Thanks:http://schmidt.devlib.org/java/file-download.html
	 * */
	private static void cache(){
		OutputStream out = null;
		OutputStream lock =null;
		URLConnection conn = null;
		InputStream  in = null;
		String lastUpdate=DateParser.getIsoDate(new Date());
		File cacheDir=new File("cache");
		if (!cacheDir.isDirectory()) cacheDir.mkdir();
		try {
			URL url = new URL("http://data.phishtank.com/data/online-valid/");
			//For testing purposes:
			//URL url = new URL("http://localhost/PhlooderFunctionTest/index.xml"); 
			out = new BufferedOutputStream(
				new FileOutputStream("cache/phishtank.xml"));
			lock = new BufferedOutputStream(
					new FileOutputStream("cache/update.lock"));
			
			conn = url.openConnection();
			in = conn.getInputStream();
			byte[] buffer = new byte[1024];
			int numRead;
			while ((numRead = in.read(buffer)) != -1) {
				out.write(buffer, 0, numRead);
			}
			lock.write(lastUpdate.getBytes(),0,lastUpdate.length());
		} catch (Exception exception) {
			exception.printStackTrace();
			System.out.println("Cannot cache data!");
			System.exit(-1);
		} finally {
			try {
				if (in != null) {
					in.close();
				}
				if (out != null) {
					out.close();
				}
				if (lock != null) {
					lock.close();
				}
			} catch (IOException ioe) {
				
			}
		}
	}
	/**
	 * Tries to find an up-to-date cached version of the PhishTank XML.
	 * It downloads and caches the online version.
	 * @param isTest
	 * Describes the location of test script while in test mode 
	 * NULL in other cases
	 * @param checkBoxes
	 * Output parameter
	 * @return
	 * Document object representing the document to parse or NULL 
	 * if an error occured.
	 * */
	private static Document getData(String isTest,ArrayList<URIBox> checkBoxes){
    	Document document=null;
        boolean isCached=false;
        BufferedReader updateReader=null;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setValidating(false);   
        factory.setNamespaceAware(false);
        try {
           DocumentBuilder builder = factory.newDocumentBuilder();
           if (isTest!=null)
        	   document = builder.parse(isTest);
           else{
        	   try{
        		   updateReader=new BufferedReader(new InputStreamReader(new FileInputStream("cache/update.lock")));
        		   String strDate=updateReader.readLine();
        		   //System.out.println(strDate);
        		   Date updateDate=DateParser.parse(strDate);
        		   
        		   Calendar outOfDate=Calendar.getInstance();
        		   System.out.println("Last download:"+updateDate.toString());
        		   outOfDate.set(Calendar.HOUR_OF_DAY,outOfDate.get(Calendar.HOUR_OF_DAY)-1);
        		   System.out.println("Out-of-date time:"+outOfDate.getTime().toString());
        		   if (updateDate.after(outOfDate.getTime())){ 
        			   System.out.println("Cache is up-to-date!");
        			   document = builder.parse(new FileInputStream("cache/phishtank.xml"));
        			   isCached=true;
        		   }
        	   }
        	   catch(Exception fnf){	
        		   System.out.println("Exception caught.");
        	   }finally{
        		   if (!isCached){
        			   System.out.println("Cannot load XML from cache. Downloading...");
        			   cache();
        			   document = builder.parse(new FileInputStream("cache/phishtank.xml"));
        		   }
        		   if (updateReader!=null)
        			   updateReader.close();
        	   }
           }
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
		return document;
	}
	/**
	 * Loads all the PhishTank XML data of the online, valid  
	 * phishing sites, and put the first 10  into the checkBoxes 
	 * ArrayList
	 * */
	public static ArrayList<URIBox> getPhishTank(String isTest)
    {
    	ArrayList<URIBox> checkBoxes=new ArrayList<URIBox>();
		Document document=getData(isTest,checkBoxes);
		if (document==null){
			System.out.println("Couldn't load XML!");
			System.exit(-1);
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
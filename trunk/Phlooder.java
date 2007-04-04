import javax.xml.parsers.DocumentBuilder; 
import javax.xml.parsers.DocumentBuilderFactory;   
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;  
import java.io.*;
import java.util.*;
import java.util.regex.*;
import org.w3c.dom.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.List;
import java.net.*;

/**This is the main class.
 * */
public class Phlooder extends Frame{
	
	/* This class is needed because we dont want to 
	 * show the URI-s in their full length, so we have 
	 * to assign the checkbox labels with the actual URIs 
	 * */
	private class URIBox{
		private String uri;
		private String boxLabel;
		private int maxLength=40;
		
		/**
		 * @param u
		 * The URI
		 * @param l
		 * Label of the box. This appears if URI is too long. 
		 * */
		URIBox(String u, String l){
			uri=u;
			boxLabel=l;
		}
		/**
		 * @param u
		 * The URI 
		 * */
		URIBox (String u){
			uri=u;
			if (uri.length()>maxLength){
				int partLength=(int)Math.ceil((maxLength-3)/2);
				boxLabel=u.substring(0,partLength)+"..."+u.substring(u.length()-partLength-1);		
			}else{
				boxLabel=u;
			}
		}
		public String getURI(){
			return uri;
		} 
		
		public String getLabel(){
			return boxLabel;
		}
		public void setURI(String u){
			uri=u;
		}
		public void setLabel(String l){
			boxLabel=l;
		}
	}
	/**
	 * @return
	 * The URI of the <code>URIBox</code>
	 * */
	private String getURIFromCheckbox(String label, ArrayList list){
		Iterator i=list.iterator();
		while(i.hasNext()){
			URIBox ub=(URIBox)i.next();
			if (ub.getLabel().equals(label)){
				return ub.getURI(); 
			}
			
		}
		return null;
	}
	
	ArrayList checkBoxes=new ArrayList();


	//int thread_check=0;

	Form form=null;
	
	/**
	 * Generates the Flood Configuration Fields :)
	 */
	private void putActionPanel(Form form){
		actionContainer.removeAll();
		URIContainer.setVisible(true);
		actionContainer.setLayout(new GridLayout(form.getInputCount()+2,4));
		
		// Titles
		Label title1=new Label("Field Name");
		title1.setFont(new Font("title",Font.BOLD,13));
		actionContainer.add(title1);
		Label title2=new Label("Type");
		title2.setFont(new Font("title",Font.BOLD,13));
		actionContainer.add(title2);
		Label title3=new Label("Flood Value");
		title3.setFont(new Font("title",Font.BOLD,13));;
		actionContainer.add(title3);
		Label title4=new Label("Flood Length");
		title4.setFont(new Font("title",Font.BOLD,13));;
		actionContainer.add(title4);
		Label title5=new Label("Default Value");
		title5.setFont(new Font("title",Font.BOLD,13));;
		actionContainer.add(title5);
		
		//Flood Configuration Fields
		for(int i=0;i<form.getInputCount();i++){
			final FormField f=form.getFieldByIndex(i);
			actionContainer.add(new Label(f.name));
			actionContainer.add(new Label(f.type));
			
			final List l=new List();
			l.add("Default value");
			l.add("Integer");
			l.add("String");
			l.add("E-mail");
			l.add("Selected");
			switch (f.flood_type.type){
			case Flood.NOP:
				l.select(0);
				break;
			case Flood.INT:
				l.select(1);
				break;
			case Flood.EMAIL:
				l.select(3);
				break;
			case Flood.SELECT:
				l.select(4);
				break;
			default:
				l.select(2);
				break;
			}
			l.addItemListener(new ItemListener(){
				public void itemStateChanged(ItemEvent e){
					if (l.getSelectedIndex()==0){
						f.flood_type.type=Flood.INT;
					}else if (l.getSelectedIndex()==1){ 
						f.flood_type.type=Flood.STR;
					}else if (l.getSelectedIndex()==2){ 
						f.flood_type.type=Flood.EMAIL;
					}
				}		
			});
			actionContainer.add(l);
			
			final TextField t=new TextField("4");
			t.addTextListener(new TextListener(){
				public void textValueChanged(TextEvent a){
					try{
						f.flood_type.length=java.lang.Integer.parseInt(t.getText());
						//System.out.println(f.name+" was set to:"+java.lang.Integer.parseInt(t.getText()));
					}catch(NumberFormatException nfe){
						try{
						t.setText(t.getText().substring(0,t.getText().length()-1));
						System.out.println("Invalid char!");
						}catch(StringIndexOutOfBoundsException siob){
							t.setText("0");
						}
					}
					
				}
			});
			actionContainer.add(t);		
			actionContainer.add(new Label(f.value));

		}
		final Button start=new Button("Phlood it!");
		final Button stop=new Button("Pause");
		stop.setEnabled(false);
		final PhlooderThread ph_thread=new PhlooderThread(form);
		start.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				System.out.println("--Phlood starts!--");
				if(!ph_thread.isStarted())
					ph_thread.start();
				else 
					ph_thread.restart();
				start.setEnabled(false);
				stop.setEnabled(true);
				URIContainer.setVisible(false);
			}
			
		});
		stop.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				ph_thread.pause(); 
				System.out.println("--Phlood stoped!--");
				start.setEnabled(true);
				stop.setEnabled(false);
				URIContainer.setVisible(true);
			}
			
		});
		
		actionContainer.add(start);
		actionContainer.add(stop);
		scroller.setSize(new Dimension(500,600));
		pack();
		repaint();
	}
	
	/**
	 * Loads all the PhishTank XML data of the online, valid  
	 * phishing sites, and put the first 10  into the checkBoxes 
	 * ArrayList
	 * */
	private void getPhishTank()
    {
    	Document document=null;
        
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
            return;

        } catch (IOException ioe) {
        	System.out.println("I/O error!");
        	checkBoxes.clear();
        	return;
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
       
        
    }

    Container actionContainer;
    Container URIContainer;
	ScrollPane scroller;

	/**
	 * Add an INPUT field to the specified Form.
     * @param in
     * The (X)HTML code of the INPUT field.
     * @param form
     * The Form to attach to.
     * */
    private void addInputToForm(String in,Form form){
    	Pattern namePattern=Pattern.compile("name=\"?[^>\"]*\"?");
		Pattern valuePattern=Pattern.compile("value=\"?[^>\"]*\"?");
		Pattern typePattern=Pattern.compile("type=\"?[^>\"]*\"?");
		Pattern attrPattern=Pattern.compile("\"[^\">]*\"|=[^\"][\\S]*[\\s>$]",Pattern.CASE_INSENSITIVE);
    	String name=null;
		String value=null;
		String type=null;
		Matcher nameMatcher=namePattern.matcher(in);
		Matcher valueMatcher=valuePattern.matcher(in);
		Matcher typeMatcher=typePattern.matcher(in);
		if (valueMatcher.find()){
			value=valueMatcher.group(0);
			Matcher tmp;
			tmp=attrPattern.matcher(value);
			if (tmp.find()){
				value=tmp.group(0).substring(1,tmp.group(0).length()-1);
			}else{
				value=null;
			}
		}
		if (typeMatcher.find()){
			type=typeMatcher.group(0);
			Matcher tmp;
			tmp=attrPattern.matcher(type);
			if (tmp.find()){
				type=tmp.group(0).substring(1,tmp.group(0).length()-1);
			}
		}
		if (nameMatcher.find()){
			name=nameMatcher.group(0);
			Matcher tmp;
			tmp=attrPattern.matcher(name);
			if (tmp.find()){
				name=tmp.group(0).substring(1,tmp.group(0).length()-1);				
			}
		}
		nameMatcher.reset();
		typeMatcher.reset();
		if (nameMatcher.find() && typeMatcher.find()){
			if((value!=null) && (type.toLowerCase().equals("hidden") || type.toLowerCase().equals("radio") || type.toLowerCase().equals("checkbox") || type.toLowerCase().equals("submit"))){
				form.addField(new FormField(type,value,name,new Flood(Flood.NOP,4)));
			}else if (name.toLowerCase().equals("email") || name.toLowerCase().equals("mail")){
				form.addField(new FormField(type,value,name,new Flood(Flood.EMAIL,25)));
			}else if(type.toLowerCase().equals("select")){
				form.addField(new FormField(type,value,name,new Flood(Flood.SELECT,25)));
			}
			else
				form.addField(new FormField(type,value,name, new Flood()));
		}
    	
    }
    
    /**
     * Scans the given URI for HTML forms. Loads the first one
     * into form for further use.
     * @param uri
     * The URI to scan. This is converted to URL.
     * @return 
     * TRUE if a form was successfully loaded, FALSE if no form 
     * was found or an error occured. 
     * */
    private boolean scanForms(String uri){
    	System.out.println(uri);
    	String s;
    	form=new Form();
    	boolean formFound=false;
		
		URIContainer.setVisible(false);
		pack();
		repaint();
    	try{
    		URI ui=new URI(uri);
    		//URI ui=new URI("http://localhost/shared/phishing.html");
    		URL ul=ui.toURL();
    		InputStream is=ul.openStream();
    		BufferedReader br=new BufferedReader(new InputStreamReader(new BufferedInputStream(is)));
    		actionContainer.removeAll();
    		actionContainer.add(new Label("Fetching information...please wait!"));
    		pack();
    		repaint();
    		
			while((s=br.readLine())!=null){
    			
				if (s.indexOf('<')!=-1){
					String s2;
					while((s.indexOf('>')==-1) && ((s2=br.readLine())!=null)){
						s=s.concat(s2);
					}
				}
				Pattern pat=Pattern.compile("<form[^>]*>",Pattern.CASE_INSENSITIVE);
				Matcher formMatcher=pat.matcher(s);
				pat=Pattern.compile("</form>",Pattern.CASE_INSENSITIVE);
				Matcher formEndMatcher=pat.matcher(s);
				pat=Pattern.compile("<input[^>]*>",Pattern.CASE_INSENSITIVE);
				Matcher inputMatcher=pat.matcher(s);
				pat=Pattern.compile("<select[^>]*>",Pattern.CASE_INSENSITIVE);
				Matcher selectMatcher=pat.matcher(s);
				pat=Pattern.compile("<textarea[^>]*>",Pattern.CASE_INSENSITIVE);
				Matcher textareaMatcher=pat.matcher(s);
				
				Pattern methodPattern=Pattern.compile("method=\"?[^>\"]*\"?");
				Pattern actionPattern=Pattern.compile("action=\"?[^>\"]*\"?");
				Pattern attrPattern=Pattern.compile("\"[^\">]*\"|=[^\"][\\S]*[\\s>$]",Pattern.CASE_INSENSITIVE);
    			if (formMatcher.find()){
    				
    				String f=formMatcher.group();
    				Matcher methodMatcher=methodPattern.matcher(f);
    				Matcher actionMatcher=actionPattern.matcher(f);
    				if (methodMatcher.find() && actionMatcher.find()){
    					formFound=true;
    					Matcher tmp;
    					
    					String method=methodMatcher.group();
    					tmp=attrPattern.matcher(method);
    					if (tmp.find()){
    						method=tmp.group(0).substring(1,tmp.group(0).length()-1);
    					}else formFound=false;
    					String action=actionMatcher.group();
    					tmp=attrPattern.matcher(action);
    					if (tmp.find()){
    						action=tmp.group(0).substring(1,tmp.group(0).length()-1);
    					}else formFound=false;
    					form.setURI(uri);
    					form.setMethod(method);
    					form.setAction(action);
    					
    				}
    			}
    			if (formFound){
    				while(inputMatcher.find()){
    						addInputToForm(inputMatcher.group(),form);    						
    				}
    				while (textareaMatcher.find()){
    					addInputToForm(textareaMatcher.group().concat(" type=\"textarea\" "),form);
    					//TODO Getting the default value
    				}
    				while (selectMatcher.find()){
    					
    					String options=s;
    					String values=new String();
    					String s2=new String();
    					String name=new String();
    					Pattern endPattern=Pattern.compile("</select>",Pattern.CASE_INSENSITIVE);
    					Pattern optionPattern=Pattern.compile("<option[^>]*>",Pattern.CASE_INSENSITIVE);
    					Matcher nameMatcher=Pattern.compile("name=\"?[^\">]*\"?",Pattern.CASE_INSENSITIVE).matcher(s);
    					if (nameMatcher.find()){
    						name=nameMatcher.group();
    					}else{
    						break;
    					}
    					
    					while(((s2=br.readLine())!=null) && !(endPattern.matcher(options).find()))
    					{
    						options=options.concat(s2);
    					}
    					Matcher optionMatcher=optionPattern.matcher(options);
    					options="";
    					while(optionMatcher.find()){
    						options=options.concat(optionMatcher.group());
    					}
    					Matcher valueMatcher=Pattern.compile("value=\"?[^\">]*\"?",Pattern.CASE_INSENSITIVE).matcher(options);
    					while(valueMatcher.find()){
    						Matcher attrMatcher=Pattern.compile("\"[^\">]*\"|=[^\"][\\S]*[>/$\\s]",Pattern.CASE_INSENSITIVE).matcher(valueMatcher.group());
    						if (attrMatcher.find()){
    							String value=attrMatcher.group();
    							if(value.charAt(0)=='"'){
    								values=values.concat(value.substring(1,value.length()-1)+"#");
    							}else
    								values=values.concat(value+"#");
    						}
    					}
    					addInputToForm("<input type=\"select\" value=\""+values+"\" "+name+"/>",form);  
    				}
    			}
    			if (formFound && formEndMatcher.find())	
    				break;
    		}
			try{
    			is.close();
    			
    		}catch(IOException ioe2){
    			actionContainer.removeAll();
        		System.out.println("Cannot close stream!");
    		}
    	}catch(URISyntaxException use){
    		actionContainer.removeAll();
    		URIContainer.setVisible(true);
    		actionContainer.add(new Label("Invalid URI!"));
    		return false;
    	}catch(MalformedURLException mue){
    		actionContainer.removeAll();
    		URIContainer.setVisible(true);
    		actionContainer.add(new Label("Cannot convert URI!"));
    		return false;
    	}catch(IOException ioe){
    		actionContainer.removeAll();
    		URIContainer.setVisible(true);
    		actionContainer.add(new Label("Cannot read location!"));
    		return false;
    	}
    	
		pack();
		repaint();
		if (formFound) return true;
		else{
			form=null;
			URIContainer.setVisible(true);
			return false;
		}
    }
    
	CheckboxGroup urls_select=new CheckboxGroup();
    
    Phlooder(String name){

    	setTitle(name);
    	addWindowListener(new WindowAdapter(){
    		public void windowClosing(WindowEvent e){
    			System.exit(-1);
    		}
    	});
    	URIContainer=new Container();
    	actionContainer=new Container();
    	scroller=new ScrollPane();
    	URIContainer.setLayout(new GridLayout(10,1));
    	actionContainer.setLayout(new GridLayout(1,1));
    	getPhishTank();
    	Iterator i=checkBoxes.iterator();
    	setLayout(new GridLayout(1,2));

    	while(i.hasNext()){
    		Checkbox ch=new Checkbox(((URIBox)i.next()).getLabel(),urls_select,false);
    		ch.addItemListener(new ItemListener(){
    			public void itemStateChanged(ItemEvent i)
    			{
    				Checkbox tmp=(Checkbox)i.getSource();
    				if (tmp.getState()){
    					if(scanForms(getURIFromCheckbox(tmp.getLabel(),checkBoxes)) && form!=null){
    						putActionPanel(form);
    					}else{
    						actionContainer.add(new Label("Cannot load form! Sorry, try another site!"));
    						pack();
    						repaint();
    						
    					}
 
    				}	
    			}
    		});
    	   	URIContainer.add(ch);
    		
    		add(URIContainer);
    		add(scroller);
    		scroller.add(actionContainer);
    	}	
    }
    /**
     * Main entry point.
     * */
    public static void main(String args[]){
    	Phlooder p=new Phlooder("Phlooder");
    	
    	p.pack();
    	p.show();	
    }
}
/**
 * @author The Blue Overdose Project
 * E-mail: blueover AT gmail com
 * Phlooder Website: http://code.google.com/p/phlooder
 * */
import java.io.*;
import java.util.*;
import java.util.regex.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.List;
import java.net.*;

/**This is the main class.
 * */
public class Phlooder extends Frame{
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
	
	ArrayList<URIBox> checkBoxes=new ArrayList<URIBox>();


	//int thread_check=0;

	//Form form=null;
	
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
	
    CheckboxGroup urls_select=new CheckboxGroup();
    
    Phlooder(String name,String isTest){
    	Welcome welcome=new Welcome();
    	welcome.pack();
    	welcome.setVisible(true);
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
    	welcome.setLoad("Fetching PhishTank Data");
    	checkBoxes=PhishTank.getPhishTank(isTest);
    	if (checkBoxes.isEmpty()){
    		System.out.println("No phishers found!");
    		System.exit(-1);
    	}
    	Iterator i=checkBoxes.iterator();
    	setLayout(new GridLayout(1,2));
    	welcome.setLoad("Scanning forms");
    	while(i.hasNext()){
    		Checkbox ch=new Checkbox(((URIBox)i.next()).getLabel(),urls_select,false);
    		ch.addItemListener(new ItemListener(){
    			public void itemStateChanged(ItemEvent i)
    			{
    				Checkbox tmp=(Checkbox)i.getSource();
    				if (tmp.getState()){
    					FormParser parser=new FormParser(getURIFromCheckbox(tmp.getLabel(),checkBoxes));
    					Form myform=parser.loadForm();
    					if(myform!=null){
    						putActionPanel(myform);
    					}else{
    						actionContainer.add(new Label("Cannot load form! Sorry, try another site!"));
    						pack();
    						repaint();
    						
    					}
 
    				}	
    			}
    		});
    		welcome.setLoad("Building UI");
    	   	URIContainer.add(ch);
    		
    		add(URIContainer);
    		add(scroller);
    		scroller.add(actionContainer);
    		welcome.dispose();
    	}	
    }
    /**
     * Main entry point.
     * */
    
    public static void main(String args[]){
    	String isTest=null;
    	if (args.length>=1 ){
    		isTest=new String(args[0]);
    	}
    	Phlooder p=new Phlooder("Phlooder",isTest);
    	
    	p.pack();
    	p.setVisible(true);	
    	
    	//FormParser fp =new FormParser("http://localhost:8080/PhlooderFunctionTest/phishing.php");
    	//fp.loadForm();
    }
}
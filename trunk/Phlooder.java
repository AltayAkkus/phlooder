/**
 * @author The Blue Overdose Project
 * @version 0.2
 * E-mail: blueover AT gmail com
 * Phlooder Website: http://code.google.com/p/phlooder
 * */
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.List;

/**
 * This is the main class. This class is responsible for the building 
 * and showing the UI and the handling of user activities (Controll and View). 
 * TODO: Migrate to Swing
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
				//if(!ph_thread.isStarted())
					ph_thread.start();
				//else 
				//	ph_thread.restart();
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
     * Main entry point, manual testing interface.
     * @param args
     * Command-line arguments can be used for testing purposes.
     * If args[0] is set the program tries to get the XML file containing 
     * the phishing sites information from the URL given in this argument 
     * instead of the original PhishTank location.
     * The PhlooderFunctionTest package contains a sample XML file and a tiny
     * test script that can be used for testing.
     * For more information see the ReadMe located in <code>PhlooderFunctionTest/</code>!
     * */
    
    public static void main(String args[]){
    	String isTest=null;
    	if (args.length>=1 ){
    		isTest=new String(args[0]);
    	}
    	Phlooder p=new Phlooder("Phlooder",isTest);
    	
    	p.pack();
    	p.setVisible(true);	
    }
}
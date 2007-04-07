/**
 * @author The Blue Overdose Project
 * @version 0.3
 * E-mail: blueover AT gmail com
 * Phlooder Website: http://code.google.com/p/phlooder
 * */
import javax.swing.*;          
import java.awt.*;
import java.awt.event.*;
import java.util.*;

/**
 * This is the main class. This class is responsible for the building 
 * and showing the UI and the handling of user activities (Controll and View). 
 * */
class Phlooder extends JPanel{
    static JFrame frame;
	JPanel selectPanel; /// Panel of the site selection
    JPanel configPanel; /// Panel for the Flood Configuration Fields
    JPanel testPanel; /// Panel for testing
    JComboBox phishSelect;
    JCheckBox isTest;
    JTextField testXML;
    
    ArrayList<URIBox> sites;
    
    //Specify the look and feel to use.  Valid values:
    //null (use the default), "Metal", "System", "Motif", "GTK+"
    final static String LOOKANDFEEL = null;
    
    /**
	 * Generates the Flood Configuration Fields :)
	 */
	private void putActionPanel(Form form){
		configPanel.removeAll();
		selectPanel.setVisible(true);
		configPanel.setLayout(new GridLayout(form.getInputCount()+2,4));
		
		// Titles
		JLabel title1=new JLabel("Field Name");
		title1.setFont(new Font("title",Font.BOLD,13));
		configPanel.add(title1);
		JLabel title2=new JLabel("Type");
		title2.setFont(new Font("title",Font.BOLD,13));
		configPanel.add(title2);
		JLabel title3=new JLabel("Flood Value");
		title3.setFont(new Font("title",Font.BOLD,13));;
		configPanel.add(title3);
		JLabel title4=new JLabel("Flood Length");
		title4.setFont(new Font("title",Font.BOLD,13));;
		configPanel.add(title4);
		JLabel title5=new JLabel("Default Value");
		title5.setFont(new Font("title",Font.BOLD,13));;
		configPanel.add(title5);
		
		//Flood Configuration Fields
		for(int i=0;i<form.getInputCount();i++){
			final FormField f=form.getFieldByIndex(i);
			configPanel.add(new JLabel(f.name));
			configPanel.add(new JLabel(f.type));
			
			final JComboBox l=new JComboBox();
			l.addItem("Default value");
			l.addItem("Integer");
			l.addItem("String");
			l.addItem("E-mail");
			l.addItem("Selected");
			switch (f.flood_type.type){
			case Flood.NOP:
				l.setSelectedIndex(0);
				break;
			case Flood.INT:
				l.setSelectedIndex(1);
				break;
			case Flood.EMAIL:
				l.setSelectedIndex(3);
				break;
			case Flood.SELECT:
				l.setSelectedIndex(4);
				break;
			default:
				l.setSelectedIndex(2);
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
			configPanel.add(l);
			
			final JTextField t=new JTextField("4");
			t.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent a){
					try{
						f.flood_type.length=java.lang.Integer.parseInt(t.getText());
						System.out.println(f.name+" was set to:"+java.lang.Integer.parseInt(t.getText()));
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
			configPanel.add(t);		
			configPanel.add(new Label(f.value));

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
				selectPanel.setVisible(false);
			}
			
		});
		stop.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				ph_thread.pause(); 
				System.out.println("--Phlood stoped!--");
				start.setEnabled(true);
				stop.setEnabled(false);
				selectPanel.setVisible(true);
			}
			
		});
		
		configPanel.add(start);
		configPanel.add(stop);
		configPanel.validate();
		frame.pack();
		frame.repaint();

	}
	private String getURIFromCheckBox(String label, ArrayList list){
		Iterator i=list.iterator();
		while(i.hasNext()){
			URIBox ub=(URIBox)i.next();
			if (ub.getLabel().equals(label)){
				return ub.getURI(); 
			}
			
		}
		return null;
	}
    private class siteSelectListener implements ActionListener{
    	public void actionPerformed(ActionEvent e){
    		JComboBox box=(JComboBox)e.getSource();
    		
    		if (box.getItemCount()==0 || box.getSelectedIndex()==0) return;
    		System.out.println(e.paramString());
    		configPanel.removeAll();
    		configPanel.validate();
    		FormParser parser=new FormParser(getURIFromCheckBox(box.getSelectedItem().toString(),sites));
    		Form myForm=parser.loadForm();
    		if (myForm!=null){
    			putActionPanel(myForm);
    		}
    	}
    }
    private class TestListener implements ActionListener{
    	public void actionPerformed(ActionEvent e){
    		if (isTest.isSelected()){
    			sites=PhishTank.getPhishTank(testXML.getText());
    			if (sites.isEmpty()){
    	    		System.out.println("No phishers found!");
    	    	}else{
    	    		phishSelect.removeAllItems();
    	    		phishSelect.addItem("-- Select One --");
    	    		for(Iterator i=sites.iterator();i.hasNext();){
    	    			phishSelect.addItem(((URIBox)i.next()).getLabel());
    	    		}
    	    	}
    		}else {
    			sites=PhishTank.getPhishTank(null);
    			if (sites.isEmpty()){
    	    		System.out.println("No phishers found!");
    	    	}else{
    	    		phishSelect.removeAllItems();
    	    		phishSelect.addItem("-- Select One --");
    	    		for(Iterator i=sites.iterator();i.hasNext();){
    	    			phishSelect.addItem(((URIBox)i.next()).getLabel());
    	    		}
    	    	}
    		}
    			
    	}
    }
    Phlooder(JFrame frame) {
    	super(new BorderLayout());
        
        selectPanel=new JPanel(new FlowLayout());
        configPanel=new JPanel(new GridLayout(1,1));
        testPanel=new JPanel(new FlowLayout());
        selectPanel.setBorder(BorderFactory.createTitledBorder("Phishing site"));
        configPanel.setBorder(BorderFactory.createTitledBorder("Flood Configuration Fields"));
		testPanel.setBorder(BorderFactory.createTitledBorder("Test"));
		
		// Seting up selectPanel
        phishSelect=new JComboBox();
        sites=PhishTank.getPhishTank(null);
        phishSelect.addItem("-- Select One --");
    	if (sites.isEmpty()){
    		System.out.println("No phishers found!");
    		System.exit(-1);
    	}
        
        for(Iterator i=sites.iterator();i.hasNext();){
        	phishSelect.addItem(((URIBox)i.next()).getLabel());
        }
        phishSelect.addActionListener(new siteSelectListener());
        selectPanel.add(phishSelect);
        
        // Seting up testPanel
        testXML=new JTextField("http://localhost/PhlooderFunctionTest/index.xml");
        isTest=new JCheckBox();
        isTest.addActionListener(new TestListener());
        testPanel.add(isTest);
        testPanel.add(testXML);
        //JPanel pane = new JPanel(new GridLayout(2, 1));
        add(selectPanel,BorderLayout.NORTH);
        add(configPanel,BorderLayout.CENTER);
        add(testPanel,BorderLayout.SOUTH);
        setBorder(BorderFactory.createEmptyBorder(
                                        30, //top
                                        30, //left
                                        10, //bottom
                                        30) //right
                                        );

        //return pane;
    }

	private static void createAndShowGUI(){
        //Make sure we have nice window decorations.
        JFrame.setDefaultLookAndFeelDecorated(true);
        JDialog.setDefaultLookAndFeelDecorated(true);
       
        //Create and set up the window.
        frame = new JFrame("Phlooder");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 

        //Set up the content pane.
        Container contentPane = frame.getContentPane();
        contentPane.setLayout(new GridLayout(1,1));
        contentPane.add(new Phlooder(frame));

        //Display the window.
        frame.pack();
        frame.setVisible(true);
	}

	/**
	 * Main entry point
	 * */
	public static void main(String args[]){

        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
	}
}
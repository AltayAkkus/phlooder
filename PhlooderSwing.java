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
 * TODO: Migrate to Swing
 * */
class PhlooderSwing extends JPanel{
    JPanel selectPanel; /// Panel of the site selection
    JPanel configPanel; /// Panel for the Flood Configuration Fields
    JComboBox phishSelect;
    static JFrame frame;
    Label name;
    
    ArrayList<URIBox> sites;
    
    //Specify the look and feel to use.  Valid values:
    //null (use the default), "Metal", "System", "Motif", "GTK+"
    final static String LOOKANDFEEL = null;
    
    /**
	 * Generates the Flood Configuration Fields :)
	 */
	private void putActionPanel(Form form){
		System.out.println("Puting...");
		configPanel.removeAll();
		selectPanel.setVisible(true);
		
		
		configPanel.setLayout(new GridLayout(form.getInputCount()+2,4));
		
		// Titles
		Label title1=new Label("Field Name");
		title1.setFont(new Font("title",Font.BOLD,13));
		configPanel.add(title1);
		Label title2=new Label("Type");
		title2.setFont(new Font("title",Font.BOLD,13));
		configPanel.add(title2);
		Label title3=new Label("Flood Value");
		title3.setFont(new Font("title",Font.BOLD,13));;
		configPanel.add(title3);
		Label title4=new Label("Flood Length");
		title4.setFont(new Font("title",Font.BOLD,13));;
		configPanel.add(title4);
		Label title5=new Label("Default Value");
		title5.setFont(new Font("title",Font.BOLD,13));;
		configPanel.add(title5);
		
		//Flood Configuration Fields
		for(int i=0;i<form.getInputCount();i++){
			final FormField f=form.getFieldByIndex(i);
			configPanel.add(new Label(f.name));
			configPanel.add(new Label(f.type));
			
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
    		
    		if (box.getSelectedIndex()==0) return;
    		
    		FormParser parser=new FormParser(getURIFromCheckBox(box.getSelectedItem().toString(),sites));
    		Form myForm=parser.loadForm();
    		if (myForm!=null){
    			putActionPanel(myForm);
    		}
    	}
    }
    PhlooderSwing(JFrame frame,String isTest) {
    	super(new BorderLayout());
        
        selectPanel=new JPanel(new FlowLayout());
        configPanel=new JPanel(new GridLayout(1,1));
        
        selectPanel.setBorder(BorderFactory.createTitledBorder("Phishing site"));
        configPanel.setBorder(BorderFactory.createTitledBorder("Flood Configuration Fields"));
		name=new Label("Field Name");
		name.setFont(new Font("title",Font.BOLD,13));
		configPanel.add(name);
		
        phishSelect=new JComboBox();
        sites=PhishTank.getPhishTank(isTest); 
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
        //JPanel pane = new JPanel(new GridLayout(2, 1));
        add(selectPanel,BorderLayout.NORTH);
        add(configPanel,BorderLayout.CENTER);
        setBorder(BorderFactory.createEmptyBorder(
                                        30, //top
                                        30, //left
                                        10, //bottom
                                        30) //right
                                        );

        //return pane;
    }

	private static void createAndShowGUI(String isTest){
        //Make sure we have nice window decorations.
        JFrame.setDefaultLookAndFeelDecorated(true);
        JDialog.setDefaultLookAndFeelDecorated(true);
       
        //Create and set up the window.
        frame = new JFrame("Phlooder");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 

        //Set up the content pane.
        Container contentPane = frame.getContentPane();
        contentPane.setLayout(new GridLayout(1,1));
        contentPane.add(new PhlooderSwing(frame,isTest));

        //Display the window.
        frame.pack();
        frame.setVisible(true);
	}

	/**
	 * Main entry point
	 * TODO: Command line arguments for manual testing
	 * */
	public static void main(String args[]){

        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI("http://localhost/PhlooderFunctionTest/index.xml");
            }
        });
	}
}
/**
 * @author The Blue Overdose Project
 * E-mail: blueover AT gmail com
 * Phlooder Website: http://code.google.com/p/phlooder
 * */

import java.awt.*;

/**
 * Class of the welcome screen.
 * TODO: Welcome image
 * */
class Welcome extends Frame{
	Label l;
	public Welcome(){
		setTitle("Loading Phlooder...");
		setUndecorated(true);
		setLocationRelativeTo(null);
		Container c=new Container();
		c.setLayout(new GridLayout(1,2));
		l=new Label("");
		c.add(new Label("Phlooder is loading..."));
		c.add(l);
		add(c);
		toFront();
	}
	
	/**
	 * Sets the actual activity.
	 * @param s
	 * Description of the activity
	 * */
	public void setLoad(String s){
		l.setText(s);
		repaint();
	}
}
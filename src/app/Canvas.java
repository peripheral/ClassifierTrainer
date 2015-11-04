package app;

import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.JPanel;

public class Canvas extends JPanel{
	
	private Pose pose = new Pose();
	public final static int DEFAULT_WIDTH = 400;
	public final static int DEFAULT_HEIGHT = 800;
	public Canvas(){
		super();
		setPreferredSize(new Dimension(DEFAULT_WIDTH,DEFAULT_HEIGHT));
	}

	public void setPose(Pose pose) {
		this.pose = pose;
	}
	
	@Override
	public void paint(Graphics g){
		super.paint(g);
		pose.draw(g);
	}
	
}

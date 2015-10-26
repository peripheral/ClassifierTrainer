package ui;

import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JFrame;

import app.Canvas;
import app.Pose;

public class UserInterface extends JFrame {
	public final static int DEFAULT_WIDTH = 400;
	public final static int DEFAULT_HEIGHT = 800;
	private Canvas canvas = new Canvas();
	private Pose pose = new Pose();
	
	public UserInterface(){
		super();
		setPreferredSize(new Dimension(DEFAULT_WIDTH,DEFAULT_HEIGHT));	
		pack();	
		setLayout(new FlowLayout());
		add(new JButton("Print training set"));
		add(canvas);
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	public UserInterface(int width, int height){
		super();
		setPreferredSize(new Dimension(width,height));
		setLayout(new FlowLayout());
		add(new JButton("Print training set"));
		add(canvas);
		pack();
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	public Canvas getCanvas(){
		return canvas;
	}

	public Pose getPose() {
		return pose;
	}

}

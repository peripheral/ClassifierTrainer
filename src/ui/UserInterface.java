package ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;





import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.glu.GLU;

import com.jogamp.opengl.util.FPSAnimator;






import com.jogamp.opengl.util.gl2.GLUT;

import app.PoseCanvas;
import app.FeatureVectorExport;
import app.Pose;
import app.SetGenerator;

public class UserInterface extends JFrame implements GLEventListener,KeyListener,MouseWheelListener {
	public final static int DEFAULT_WIDTH = 400;
	public final static int DEFAULT_HEIGHT = 800;
	private static final Dimension PREFFERED_FRAME_SIZE=new Dimension(400,800);
	//private PoseCanvas canvas = new PoseCanvas();
	private Pose pose = new Pose();
	private GLCanvas canvas;
	private PoseCanvas poseCanvas = new PoseCanvas();
	private int Rx=90,Ry=0,Dist=300;
	
	public UserInterface(){
		super("Window");
		GLCapabilities capabilities = new GLCapabilities(null);
 		canvas = new GLCanvas(capabilities);
 		canvas.addGLEventListener(this);
 		canvas.addKeyListener(this);
 		canvas.addMouseWheelListener(this);
		setPreferredSize(new Dimension(DEFAULT_WIDTH,DEFAULT_HEIGHT));	
		pack();	
		JButton btn = new JButton("Print training set");
		btn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				FeatureVectorExport exp = new FeatureVectorExport("input.csv","target.csv");
				SetGenerator setGen = new SetGenerator("input.csv","target.csv");
				setGen.rotateVariation(pose, 90, 0, 360);
				setGen.completeProcedure();
			}
		});
		add(btn,BorderLayout.NORTH);
		JPanel controPanel = new JPanel();
		add(controPanel,BorderLayout.WEST);
		setVisible(true);
		addWindowListener(new WindowAdapter(){
 			public void windowClosing(WindowEvent e){
 				System.exit(0);
 			}
 		});	
	
 		add(canvas,BorderLayout.CENTER);
 		FPSAnimator animator = new FPSAnimator( 5);			//Animator reloads display 10 times/sec normal 60  fps
 		animator.add(canvas);
 		animator.start();
	}
	
	public UserInterface(int width, int height){
		super("Window");
		setPreferredSize(new Dimension(width,height));
		setLayout(new FlowLayout());
		add(new JButton("Print training set"));
		pack();
		setVisible(true);
		addWindowListener(new WindowAdapter(){
 			public void windowClosing(WindowEvent e){
 				System.exit(0);
 			}
 		});	
		GLCapabilities capabilities = new GLCapabilities(null);
 		canvas = new GLCanvas(capabilities);
 		canvas.addGLEventListener(this);
 		canvas.addKeyListener(this);
 		canvas.addMouseWheelListener(this);
 		add(canvas,BorderLayout.CENTER);

 		FPSAnimator animator = new FPSAnimator(canvas, 25);			//Animator reloads display 10 times/sec normal 60  fps
 		animator.start();
	}
	
	public Pose getPose() {
		return pose;
	}
	public Dimension getPrefferedSize(){
 		return PREFFERED_FRAME_SIZE;
 	}

	@Override
 	public void mouseWheelMoved(MouseWheelEvent m) {
 		Dist+=m.getWheelRotation();
 		
 	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

 	@Override
 	public void keyPressed(KeyEvent e)
 	{
 		//	  System.out.println("key press");
 		if (e.getKeyCode() == KeyEvent.VK_ESCAPE){

 			System.exit(0);}

 		if (e.getKeyCode() == KeyEvent.VK_W){
 			Ry=Ry+2;   }
 		if (e.getKeyCode() == KeyEvent.VK_S){
 			Ry=Ry-2;}

 		if (e.getKeyCode() == KeyEvent.VK_A){
 			Rx=Rx-1;   }
 		if (e.getKeyCode() == KeyEvent.VK_D){
 			Rx=Rx+1;}

 		if (e.getKeyCode() == KeyEvent.VK_L){
 			//showLines = !showLines;
 		}
 	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
 	public void display(GLAutoDrawable drawable) {
 		GL2 gl = (GL2)drawable.getGL();
 		
 		final GLU glu = new GLU();
 		
 		gl.glViewport(0, 0, this.getWidth(),this.getHeight());					// size of the window
 		gl.glMatrixMode(GL2.GL_PROJECTION);
 		gl.glLoadIdentity();
 		
 		int width =300,height=300;
 		glu.gluPerspective(70,(width*1.0f)/height,1,1000);
 		//45 = field of view ,width/height = aspect ratio , 1 = near clipping plane 20 = far clipping plane 
 		//http://pyopengl.sourceforge.net/documentation/ref/glu/perspective.html
 		gl.glEnable(GL2.GL_CULL_VERTEX_EYE_POSITION_EXT);
 		gl.glEnable(GL.GL_DEPTH_TEST);
 		gl.glDepthFunc(GL.GL_LESS);
 		
 		float[] eyePoint = newEyePoint(Dist);
 		glu.gluLookAt(eyePoint[0],eyePoint[1],eyePoint[2],0.0f,200f,0.0f,0.0f,1.0f,0.0f);
 		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
 		gl.glClearColor(1,1, 1, 1);
 		/* X axel blue */
 		gl.glColor3d(0, 0, 1);
 		gl.glBegin(GL2.GL_LINE_STRIP);
 		gl.glVertex3d(-100, 0, 0);
 		gl.glVertex3d(100, 0, 0);
 		gl.glEnd();

 		/* Y axel green */
 		gl.glColor3d(0, 1, 0);
 		gl.glBegin(GL2.GL_LINE_STRIP);
 		gl.glVertex3d(0, -100, 0);
 		gl.glVertex3d(0,100, 0);
 		gl.glEnd();

 		/* Z axel red */
 		gl.glColor3d(1, 0, 0);
 		gl.glBegin(GL2.GL_LINE_STRIP);
 		gl.glVertex3d(0, 0, -100);
 		gl.glVertex3d(0, 0, 100);
 		gl.glEnd();
 		
 		gl.glColor3d(0.8,0.8,0.8);
 		gl.glBegin(GL.GL_TRIANGLE_FAN);
 		gl.glVertex3d(-1,-1,0);
 		gl.glVertex3d(-1,1,0);
 		gl.glVertex3d(1,1,0);
 		gl.glVertex3d(1,-1,0);
 		gl.glEnd();
 		poseCanvas.drawPose(drawable);
 	}

	@Override
	public void dispose(GLAutoDrawable arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void init(GLAutoDrawable arg0) {
		// TODO Auto-generated method stub
		
	}

	private float[] newEyePoint(int r) {
 		float[] points= new float[3];
 		points[0] = (float) (r*Math.cos(Math.toRadians(Rx))*Math.cos(Math.toRadians(Ry)));
 		points[2] = (float) (r*Math.sin(Math.toRadians(Rx))*Math.cos(Math.toRadians(Ry)));
 		points[1] = (float) (r*Math.sin(Math.toRadians(Ry)));
 		System.out.println("x="+Rx+" y="+Ry+" z="+points[2]);
 		return points;
 	}

 	@Override
 	public void reshape(GLAutoDrawable drawable, int x, int y, int width,
 			int height) { 
 		canvas.setPreferredSize(this.getSize());
 		GL2 gl = drawable.getGL().getGL2();
 		
 		gl.glViewport(0, 0, width, height);
 		gl.glMatrixMode(GL2.GL_PROJECTION);
 		gl.glLoadIdentity();
 		final GLU glu = new GLU();
 		//glu.gluOrtho2D(0.0, 450.0, 0.0, 375.0);	
 		glu.gluPerspective(70,(width*1.0f)/height,1,1000);
 		float[] v= newEyePoint(Dist);
 		glu.gluLookAt(v[0],v[1],v[2],0.0f,200f,0.0f,0.0f,1.0f,0.0f);
 		//glu.gluOrtho2D(0.0, 20.0, 0.0, 40.0);
 	}	
}

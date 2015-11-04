package app;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.swing.JPanel;

import com.jogamp.opengl.util.gl2.GLUT;

public class PoseCanvas extends JPanel{
	
	private Pose pose = new Pose();
	public final static int DEFAULT_WIDTH = 400;
	public final static int DEFAULT_HEIGHT = 800;
	/* Conversion to screen pixel ratio */
	public double ratio = 0.3;
	/*Joint is presented as square of pixel size */
	public int SQUARE_HEIGHT = 10;
	/* Square radius as constant */
	public int SQUARE_RADIUS = 5;
	
	public PoseCanvas(){
		super();
		setPreferredSize(new Dimension(DEFAULT_WIDTH,DEFAULT_HEIGHT));
	}

	public void setPose(Pose pose) {
		this.pose = pose;
	}
	
	@Override
	public void paint(Graphics g){
		super.paint(g);
		drawPose(g);
	}
	

	/**
	 * Scaling  x and y coordinates to fit screen 
	 * @param coords - coordinate
	 * @return - converted coords
	 */
	private int[] scale(double[] coords) {	
		int temp[] =  new int[]{(int)(coords[0]*ratio),
				(int)(coords[1]*ratio),(int)(coords[2]*ratio)};
		return temp;
	}
	
	public void drawPose(Graphics g){
			g.setColor(Color.GREEN);
			/* Scaling the value to fit the view */
			int head[] = scale(pose.getHead());
			/*Drawing head square */
			g.fillRect(head[0]-SQUARE_RADIUS, head[1]-SQUARE_RADIUS,SQUARE_HEIGHT,SQUARE_HEIGHT);
			/* Scaling the value to fit the view */
			int sc[] = scale(pose.getShoulder_center());
			g.setColor(Color.BLUE);
			/* Drawing shoulder center square */
			g.fillRect(sc[0]-SQUARE_RADIUS, sc[1]-SQUARE_RADIUS,SQUARE_HEIGHT,SQUARE_HEIGHT);
			/*Drawing line between head and shoulder center */
			g.drawLine(head[0], head[1],sc[0],sc[1]);
			/* Drawing spine square */
			g.setColor(Color.ORANGE);
			int s[] = scale(pose.getSpine());
			g.fillRect(s[0]-SQUARE_RADIUS, s[1]-SQUARE_RADIUS,SQUARE_HEIGHT,SQUARE_HEIGHT);
			/*Drawing line between shoulder center and spine */
			g.drawLine(sc[0], sc[1],s[0],s[1]);
			/* Drawing left hip square */
			g.setColor(Color.ORANGE);
			int hl[] = scale(pose.getHip_left());
			g.fillRect(hl[0]-SQUARE_RADIUS, hl[1]-SQUARE_RADIUS,SQUARE_HEIGHT,SQUARE_HEIGHT);
			/*Drawing line between spine and left heap */
			g.drawLine(s[0], s[1],hl[0],hl[1]);
			/* Drawing right hip square */
			g.setColor(Color.ORANGE);
			int hr[] = scale(pose.getHip_right());
			g.fillRect(hr[0]-SQUARE_RADIUS, hr[1]-SQUARE_RADIUS,SQUARE_HEIGHT,SQUARE_HEIGHT);
			/*Drawing line between spine and right heap */
			g.drawLine(s[0], s[1],hr[0],hr[1]);
			/* Drawing the left knee */
			/* Scaling coordinates */
			int kl[] =scale(pose.getHip_left());
			g.setColor(Color.GRAY);
			g.fillRect(kl[0]-SQUARE_RADIUS, kl[1]-SQUARE_RADIUS,SQUARE_HEIGHT,SQUARE_HEIGHT);
			/* Drawing line from left hip to left knee */
			g.drawLine(hl[0], hl[1],kl[0],kl[1]);
			/* Drawing the right knee */
			/* Scaling coordinates */
			int kr[] =scale(pose.getKnee_right());
			g.setColor(Color.GRAY);
			g.fillRect(kr[0]-SQUARE_RADIUS, kr[1]-SQUARE_RADIUS,SQUARE_HEIGHT,SQUARE_HEIGHT);
			/* Drawing line from right hip to right knee */
			g.drawLine(hr[0], hr[1],kr[0],kr[1]);
			/* Drawing the right ankle */
			/* Scaling coordinates */
			int ar[] =scale(pose.getAnkle_right());
			g.setColor(Color.GRAY);
			g.fillRect(ar[0]-SQUARE_RADIUS, ar[1]-SQUARE_RADIUS,SQUARE_HEIGHT,SQUARE_HEIGHT);
			/* Drawing line from right knee to right ankle */
			g.drawLine(kr[0], kr[1],ar[0],ar[1]);
			/* Drawing the right ankle */
			/* Scaling coordinates */
			int al[] =scale(pose.getAnkle_left());
			g.setColor(Color.GRAY);
			g.fillRect(al[0]-SQUARE_RADIUS, al[1]-SQUARE_RADIUS,SQUARE_HEIGHT,SQUARE_HEIGHT);
			/* Drawing line from right knee to right ankle */
			g.drawLine(kl[0], kl[1],al[0],al[1]);
			/* Drawing the right foot */
			/* Scaling coordinates */
			int fr[] =scale(pose.getElbow_right());
			g.setColor(Color.GRAY);
			g.fillRect(fr[0]-SQUARE_RADIUS, fr[1]-SQUARE_RADIUS,SQUARE_HEIGHT,SQUARE_HEIGHT);
			/* Drawing line from right knee to right ankle */
			g.drawLine(ar[0], ar[1],fr[0],fr[1]);
			/* Drawing the left foot */
			/* Scaling coordinates */
			int fl[] =scale(pose.getFoot_left());
			g.setColor(Color.GRAY);
			g.fillRect(fl[0]-SQUARE_RADIUS, fl[1]-SQUARE_RADIUS,SQUARE_HEIGHT,SQUARE_HEIGHT);
			/* Drawing line from right knee to right ankle */
			g.drawLine(al[0], al[1],fl[0],fl[1]);
			/*Drawing left shoulder */
			/* Scaling left shoulder coordinates */
			int sl[] = scale(pose.getShoulder_right());
			/* Drawing the left shoulder */
			g.drawRect(sl[0]-SQUARE_RADIUS, sl[1]-SQUARE_RADIUS,SQUARE_HEIGHT,SQUARE_HEIGHT);
			/* Drawing line from shoulder center to left shoulder */
			g.drawLine(sc[0], sc[1],sl[0],sl[1]);
			/*Drawing left elbow */
			/* Scaling left elbow coordinates */
			int el[] = scale(pose.getElbow_left());
			g.setColor(Color.MAGENTA);
			g.drawRect(el[0]-SQUARE_RADIUS, el[1]-SQUARE_RADIUS,SQUARE_HEIGHT,SQUARE_HEIGHT);
			/* Drawing line from left shoulder to left elbow */
			g.drawLine(sl[0], sl[1],el[0],el[1]);
			/*Scaling */
			int sr[] = scale(pose.getShoulder_right());
			/* Drawing the right shoulder */
			g.fillRect(sr[0]-SQUARE_RADIUS, sr[1]-SQUARE_RADIUS,SQUARE_HEIGHT,SQUARE_HEIGHT);
			g.drawLine(sc[0], sc[1],sr[0],sr[1]);
			/* Scaling coordinates */
			int er[] =scale(pose.getElbow_right());
			/* Drawing the right elbow */
			g.setColor(Color.CYAN);
			g.fillRect(er[0]-SQUARE_RADIUS, er[1]-SQUARE_RADIUS,SQUARE_HEIGHT,SQUARE_HEIGHT);
			/* Drawing line from right shoulder to right elbow */
			g.drawLine(sr[0], sr[1],er[0],er[1]);
			/* Drawing the right wrist */
			/* Scaling coordinates */
			int wr[] =scale(pose.getWrist_right());
			g.setColor(Color.GRAY);
			g.fillRect(wr[0]-SQUARE_RADIUS, wr[1]-SQUARE_RADIUS,SQUARE_HEIGHT,SQUARE_HEIGHT);
			/* Drawing line from right elbow to right wrist */
			g.drawLine(er[0], er[1],wr[0],wr[1]);
			/* Drawing the left wrist */
			/* Scaling coordinates */
			int wl[] =scale(pose.getWrist_left());
			g.setColor(Color.GRAY);
			g.fillRect(wl[0]-SQUARE_RADIUS, wl[1]-SQUARE_RADIUS,SQUARE_HEIGHT,SQUARE_HEIGHT);
			/* Drawing line from right elbow to right wrist */
			g.drawLine(el[0], el[1],wl[0],wl[1]);

	}
	public void drawPose(GLAutoDrawable drawable){
		GL2 gl = (GL2)drawable.getGL();
		GLUT glut = new GLUT();
		gl.glColor3d(0,1,0);
		/* Scaling the value to fit the view */
		int head[] = scale(pose.getHead());
		System.out.println(head[0]+" "+head[1]);
		/*Drawing head square */
		gl.glPushMatrix();
		gl.glTranslatef(head[0],head[1], head[2]);
		//g.fillRect(head[0]-SQUARE_RADIUS, head[1]-SQUARE_RADIUS,SQUARE_HEIGHT,SQUARE_HEIGHT);
		glut.glutSolidCube(SQUARE_HEIGHT);
		gl.glPopMatrix();
		/* Scaling the value to fit the view */
		int sc[] = scale(pose.getShoulder_center());
		//g.setColor(Color.BLUE);
		gl.glPushMatrix();
		gl.glColor3d(0,0,1);
		/* Drawing shoulder center square */
		//g.fillRect(sc[0]-SQUARE_RADIUS, sc[1]-SQUARE_RADIUS,SQUARE_HEIGHT,SQUARE_HEIGHT);
		gl.glTranslatef(sc[0],sc[1], sc[2]);
		glut.glutSolidCube(SQUARE_HEIGHT);
		gl.glPopMatrix();
		/*Drawing line between head and shoulder center */
		//g.drawLine(head[0], head[1],sc[0],sc[1]);
		gl.glBegin(GL2.GL_LINE_STRIP);
		gl.glVertex3iv(head, 0);
		gl.glVertex3iv(sc, 0);
		gl.glEnd();
		/* Drawing spine square */
		//g.setColor(Color.ORANGE);
		int s[] = scale(pose.getSpine());
		gl.glPushMatrix();
		gl.glTranslated(s[0], s[1], s[2]);
		glut.glutSolidCube(SQUARE_HEIGHT);
		gl.glPopMatrix();
		//g.fillRect(s[0]-SQUARE_RADIUS, s[1]-SQUARE_RADIUS,SQUARE_HEIGHT,SQUARE_HEIGHT);
		/*Drawing line between shoulder center and spine */
		//g.drawLine(sc[0], sc[1],s[0],s[1]);
		gl.glBegin(GL2.GL_LINE_STRIP);
		gl.glVertex3iv(sc, 0);
		gl.glVertex3iv(s, 0);
		gl.glEnd();
		/* Drawing left hip square */
		//g.setColor(Color.ORANGE);
		int hl[] = scale(pose.getHip_left());
		//g.fillRect(hl[0]-SQUARE_RADIUS, hl[1]-SQUARE_RADIUS,SQUARE_HEIGHT,SQUARE_HEIGHT);
		gl.glPushMatrix();
		gl.glColor3f(1,0, 0);
		gl.glTranslated(hl[0], hl[1], hl[2]);
		glut.glutSolidCube(SQUARE_HEIGHT);
		gl.glPopMatrix();
		gl.glColor3f(0,0, 1);
		/*Drawing line between spine and left heap */
		//g.drawLine(s[0], s[1],hl[0],hl[1]);
		gl.glBegin(GL2.GL_LINE_STRIP);
		gl.glVertex3iv(s, 0);
		gl.glVertex3iv(hl, 0);
		gl.glEnd();
		/* Drawing right hip square */
		//g.setColor(Color.ORANGE);
		int hr[] = scale(pose.getHip_right());
		//g.fillRect(hr[0]-SQUARE_RADIUS, hr[1]-SQUARE_RADIUS,SQUARE_HEIGHT,SQUARE_HEIGHT);
		gl.glPushMatrix();
		gl.glTranslated(hr[0], hr[1], hr[2]);
		glut.glutSolidCube(SQUARE_HEIGHT);
		gl.glPopMatrix();
		/*Drawing line between spine and right heap */
		//g.drawLine(s[0], s[1],hr[0],hr[1]);
		gl.glBegin(GL2.GL_LINE_STRIP);
		gl.glVertex3iv(s, 0);
		gl.glVertex3iv(hr, 0);
		gl.glEnd();
		/* Drawing the left knee */
		/* Scaling coordinates */
		int kl[] =scale(pose.getKnee_left());
		//g.setColor(Color.GRAY);
		//g.fillRect(kl[0]-SQUARE_RADIUS, kl[1]-SQUARE_RADIUS,SQUARE_HEIGHT,SQUARE_HEIGHT);
		gl.glPushMatrix();
		gl.glTranslated(kl[0], kl[1], kl[2]);
		glut.glutSolidCube(SQUARE_HEIGHT);
		gl.glPopMatrix();
		/* Drawing line from left hip to left knee */
		//g.drawLine(hl[0], hl[1],kl[0],kl[1]);gl.glBegin(GL2.GL_LINE_STRIP);
		gl.glBegin(GL2.GL_LINE_STRIP);
		gl.glVertex3iv(hl, 0);
		gl.glVertex3iv(kl, 0);
		gl.glEnd();
		/* Drawing the right knee */
		/* Scaling coordinates */
		int kr[] =scale(pose.getKnee_right());
		//g.setColor(Color.GRAY);
		//g.fillRect(kr[0]-SQUARE_RADIUS, kr[1]-SQUARE_RADIUS,SQUARE_HEIGHT,SQUARE_HEIGHT);
		gl.glPushMatrix();
		gl.glTranslated(kr[0], kr[1], kr[2]);
		glut.glutSolidCube(SQUARE_HEIGHT);
		gl.glPopMatrix();
		/* Drawing line from right hip to right knee */
		//g.drawLine(hr[0], hr[1],kr[0],kr[1]);
		gl.glBegin(GL2.GL_LINE_STRIP);
		gl.glVertex3iv(hr, 0);
		gl.glVertex3iv(kr, 0);
		gl.glEnd();
		/* Drawing the right ankle */
		/* Scaling coordinates */
		int ar[] =scale(pose.getAnkle_right());
		//g.setColor(Color.GRAY);
		//g.fillRect(ar[0]-SQUARE_RADIUS, ar[1]-SQUARE_RADIUS,SQUARE_HEIGHT,SQUARE_HEIGHT);gl.glPushMatrix();
		gl.glPushMatrix();
		gl.glTranslated(ar[0], ar[1], ar[2]);
		glut.glutSolidCube(SQUARE_HEIGHT);
		gl.glPopMatrix();
		/* Drawing line from right knee to right ankle */
		//g.drawLine(kr[0], kr[1],ar[0],ar[1]);
		gl.glBegin(GL2.GL_LINE_STRIP);
		gl.glVertex3iv(kr, 0);
		gl.glVertex3iv(ar, 0);
		gl.glEnd();
		/* Drawing the right ankle */
		/* Scaling coordinates */
		int al[] =scale(pose.getAnkle_left());
		//g.setColor(Color.GRAY);
		//g.fillRect(al[0]-SQUARE_RADIUS, al[1]-SQUARE_RADIUS,SQUARE_HEIGHT,SQUARE_HEIGHT);
		gl.glPushMatrix();
		gl.glTranslated(al[0], al[1], al[2]);
		glut.glutSolidCube(SQUARE_HEIGHT);
		gl.glPopMatrix();
		/* Drawing line from right knee to right ankle */
		//g.drawLine(kl[0], kl[1],al[0],al[1]);
		gl.glBegin(GL2.GL_LINE_STRIP);
		gl.glVertex3iv(kl, 0);
		gl.glVertex3iv(al, 0);
		gl.glEnd();
		/* Drawing the right foot */
		/* Scaling coordinates */
		int fr[] =scale(pose.getFoot_right());
		//g.setColor(Color.GRAY);
		//g.fillRect(fr[0]-SQUARE_RADIUS, fr[1]-SQUARE_RADIUS,SQUARE_HEIGHT,SQUARE_HEIGHT);
		gl.glPushMatrix();
		gl.glTranslated(fr[0], fr[1], fr[2]);
		glut.glutSolidCube(SQUARE_HEIGHT);
		gl.glPopMatrix();
		/* Drawing line from right knee to right ankle */
		//g.drawLine(ar[0], ar[1],fr[0],fr[1]);
		gl.glBegin(GL2.GL_LINE_STRIP);
		gl.glVertex3iv(ar, 0);
		gl.glVertex3iv(fr, 0);
		gl.glEnd();
		/* Drawing the left foot */
		/* Scaling coordinates */
		int fl[] =scale(pose.getFoot_left());
		//g.setColor(Color.GRAY);
		//g.fillRect(fl[0]-SQUARE_RADIUS, fl[1]-SQUARE_RADIUS,SQUARE_HEIGHT,SQUARE_HEIGHT);
		gl.glPushMatrix();
		gl.glTranslated(fl[0], fl[1], fl[2]);
		glut.glutSolidCube(SQUARE_HEIGHT);
		gl.glPopMatrix();
		/* Drawing line from right knee to right ankle */
		//g.drawLine(al[0], al[1],fl[0],fl[1]);
		gl.glBegin(GL2.GL_LINE_STRIP);
		gl.glVertex3iv(al, 0);
		gl.glVertex3iv(fl, 0);
		gl.glEnd();
		/*Drawing left shoulder */
		/* Scaling left shoulder coordinates */
		int sl[] = scale(pose.getShoulder_left());
		/* Drawing the left shoulder */
		//g.drawRect(sl[0]-SQUARE_RADIUS, sl[1]-SQUARE_RADIUS,SQUARE_HEIGHT,SQUARE_HEIGHT);
		gl.glPushMatrix();
		gl.glTranslated(sl[0], sl[1], sl[2]);
		glut.glutSolidCube(SQUARE_HEIGHT);
		gl.glPopMatrix();
		/* Drawing line from shoulder center to left shoulder */
		//g.drawLine(sc[0], sc[1],sl[0],sl[1]);
		gl.glBegin(GL2.GL_LINE_STRIP);
		gl.glVertex3iv(sc, 0);
		gl.glVertex3iv(sl, 0);
		gl.glEnd();
		/*Drawing left elbow */
		/* Scaling left elbow coordinates */
		int el[] = scale(pose.getElbow_left());
		//g.setColor(Color.MAGENTA);
		//g.drawRect(el[0]-SQUARE_RADIUS, el[1]-SQUARE_RADIUS,SQUARE_HEIGHT,SQUARE_HEIGHT);
		gl.glPushMatrix();
		gl.glTranslated(el[0], el[1], el[2]);
		glut.glutSolidCube(SQUARE_HEIGHT);
		gl.glPopMatrix();
		/* Drawing line from left shoulder to left elbow */
		//g.drawLine(sl[0], sl[1],el[0],el[1]);
		gl.glBegin(GL2.GL_LINE_STRIP);
		gl.glVertex3iv(sl, 0);
		gl.glVertex3iv(el, 0);
		gl.glEnd();
		/*Scaling */
		int sr[] = scale(pose.getShoulder_right());
		/* Drawing the right shoulder */
		//g.fillRect(sr[0]-SQUARE_RADIUS, sr[1]-SQUARE_RADIUS,SQUARE_HEIGHT,SQUARE_HEIGHT);
		gl.glPushMatrix();
		gl.glTranslated(sr[0], sr[1], sr[2]);
		glut.glutSolidCube(SQUARE_HEIGHT);
		gl.glPopMatrix();
		//g.drawLine(sc[0], sc[1],sr[0],sr[1]);
		gl.glBegin(GL2.GL_LINE_STRIP);
		gl.glVertex3iv(sc, 0);
		gl.glVertex3iv(sr, 0);
		gl.glEnd();
		/* Scaling coordinates */
		int er[] =scale(pose.getElbow_right());
		/* Drawing the right elbow */
		//g.setColor(Color.CYAN);
		//g.fillRect(er[0]-SQUARE_RADIUS, er[1]-SQUARE_RADIUS,SQUARE_HEIGHT,SQUARE_HEIGHT);
		gl.glPushMatrix();
		gl.glTranslated(er[0], er[1], er[2]);
		glut.glutSolidCube(SQUARE_HEIGHT);
		gl.glPopMatrix();
		/* Drawing line from right shoulder to right elbow */
		//g.drawLine(sr[0], sr[1],er[0],er[1]);
		gl.glBegin(GL2.GL_LINE_STRIP);
		gl.glVertex3iv(sr, 0);
		gl.glVertex3iv(er, 0);
		gl.glEnd();
		/* Drawing the right wrist */
		/* Scaling coordinates */
		int wr[] =scale(pose.getWrist_right());
		//g.setColor(Color.GRAY);
		//g.fillRect(wr[0]-SQUARE_RADIUS, wr[1]-SQUARE_RADIUS,SQUARE_HEIGHT,SQUARE_HEIGHT);
		gl.glPushMatrix();
		gl.glTranslated(wr[0], wr[1], wr[2]);
		glut.glutSolidCube(SQUARE_HEIGHT);
		gl.glPopMatrix();
		/* Drawing line from right elbow to right wrist */
		//g.drawLine(er[0], er[1],wr[0],wr[1]);
		gl.glBegin(GL2.GL_LINE_STRIP);
		gl.glVertex3iv(er, 0);
		gl.glVertex3iv(wr, 0);
		gl.glEnd();
		/* Drawing the left wrist */
		/* Scaling coordinates */
		int wl[] =scale(pose.getWrist_left());
		//g.setColor(Color.GRAY);
		//g.fillRect(wl[0]-SQUARE_RADIUS, wl[1]-SQUARE_RADIUS,SQUARE_HEIGHT,SQUARE_HEIGHT);
		gl.glPushMatrix();
		gl.glTranslated(wl[0], wl[1], wl[2]);
		glut.glutSolidCube(SQUARE_HEIGHT);
		gl.glPopMatrix();
		/* Drawing line from right elbow to right wrist */
		//g.drawLine(el[0], el[1],wl[0],wl[1]);
		gl.glBegin(GL2.GL_LINE_STRIP);
		gl.glVertex3iv(el, 0);
		gl.glVertex3iv(wl, 0);
		gl.glEnd();

}
	
}

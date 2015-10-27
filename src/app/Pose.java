package app;

import java.awt.Color;
import java.awt.Graphics;

/**
 * Class representing pose.
 * Coordinate alignment x,y z - depth 
 * Resolution of the Kinect depth camera - 640 x 480.
 * Body model based on idealistic type
 *  http://www.creativecomicart.com/uploads/4/1/8/5/4185864/9470541_orig.gif?228
 *  Foot size based on
 *  http://design.tutsplus.com/tutorials/human-anatomy-fundamentals-advanced-facial-features--cms-20683
 * @author Artur Vitt
 *
 */
public class Pose {

	private int head[] = null;
	private int shoulder_left[] = null;
	private int shoulder_right[] = null;
	private int shoulder_center[] = null;
	private int elbow_left[] = null;
	private int elbow_right[] = null;
	private int hand_left[] = null;
	private int hand_right[] = null;
	private int wrist_left[] = null;
	private int wrist_right[] = null;
	private int spine[] = null;
	private int hip_center[] = null;
	private int hip_left[] = null;
	private int hip_right[] = null;
	private int knee_left[] = null;
	private int knee_right[] = null;
	private int ankle_left[] = null;
	private int ankle_right[] = null;
	private int foot_left[] = null;
	private int foot_right[] = null;
	/* HEad dimensions, HH - head height, HW - head width */
	public int HH = 225;
	public int HW = 200;
	public static final int DEFAULT_DISTANCE_TO_BODY = 2000;
	public static int SHOULDER_RATIO = 3;
	/* tan(35) * 2000 , with field view of 70 degrees at distance of 2000mm */
	public static int VIEW_CENTER= 9500;
	public static int VIEW_HEIGHT= 2000;
	/* Kinect depth camera resolution in pix */
	public static int KINECT_WIDTH_RESOLUTION= 640;
	public static int KINECT_HEIGHT_RESOLUTION= 480;
	/* Pixel to length to mm ratio in x dim */
	public static double PIXEL_TO_MM_RATIO_X=640/19000.0;
	/* Pixel to length to mm ratio in x dim */
	public static double PIXEL_TO_MM_RATIO_Y= 480/2000.0;
	public int X_OFFSET = 0;
	public int Y_OFFSET = 0;
	public int Z_OFFSET = 0;
	/* Conversion to screen pixel ratio */
	public double ratio = 0.3;

	/*Joint square size */
	public int SQUARE_HEIGHT = 10;
	/* square radius */
	public int SQUARE_RADIUS = 5;
	private int[] targetArray = {0,0,0};

	public Pose(){
		/* Total height is 9 heads, position of the head
		 *  is in the last 9 section*/
		head = new int[3];
		head[0] = X_OFFSET + VIEW_CENTER;
		head[1] = Y_OFFSET + HH*8 - HH/2;
		head[2] = Z_OFFSET + DEFAULT_DISTANCE_TO_BODY;

		shoulder_center = new int[3];
		shoulder_center[0] = X_OFFSET + VIEW_CENTER;
		shoulder_center[1] = Y_OFFSET + HH*7 - HH/8 ;
		shoulder_center[2] = Z_OFFSET + DEFAULT_DISTANCE_TO_BODY;

		shoulder_left = new int [3];
		shoulder_left[0] = X_OFFSET + VIEW_CENTER + HW*3/4;
		shoulder_left[1] = Y_OFFSET + HH*7 - HH/3;
		shoulder_left[2] = Z_OFFSET + DEFAULT_DISTANCE_TO_BODY;

		shoulder_right = new int [3];
		shoulder_right[0] = X_OFFSET + VIEW_CENTER - HW*3/4;
		shoulder_right[1] = Y_OFFSET + HH*7 - HH/3;
		shoulder_right[2] = Z_OFFSET + DEFAULT_DISTANCE_TO_BODY;

		elbow_right = new int [3];
		elbow_right[0] = X_OFFSET + VIEW_CENTER - ( HW +HW/5 );
		elbow_right[1] = Y_OFFSET + HH*6 - HH*4/5;
		elbow_right[2] = Z_OFFSET + DEFAULT_DISTANCE_TO_BODY;

		elbow_left = new int [3];
		elbow_left[0] = X_OFFSET + VIEW_CENTER + (HW + HW/5);
		elbow_left[1] = Y_OFFSET + HH*6 - HH*4/5;
		elbow_left[2] = Z_OFFSET + DEFAULT_DISTANCE_TO_BODY;

		hip_left = new int [3];
		hip_left[0] = X_OFFSET + VIEW_CENTER + HW/2;
		hip_left[1] = Y_OFFSET + HH*5 - HH/3;
		hip_left[2] = Z_OFFSET + DEFAULT_DISTANCE_TO_BODY;

		spine = new int [3];
		spine[0] = X_OFFSET + VIEW_CENTER;
		spine[1] = Y_OFFSET + HH*5;
		spine[2] = Z_OFFSET + DEFAULT_DISTANCE_TO_BODY;

		hip_right = new int [3];
		hip_right[0] = X_OFFSET + VIEW_CENTER - HW/2;
		hip_right[1] = Y_OFFSET + HH*5 - HH/3;
		hip_right[2] = Z_OFFSET + DEFAULT_DISTANCE_TO_BODY;

		wrist_right = new int[3];
		wrist_right[0] = X_OFFSET + VIEW_CENTER - HW;
		wrist_right[1] = Y_OFFSET + HH*4 - HH/2;
		wrist_right[2] = Z_OFFSET + DEFAULT_DISTANCE_TO_BODY;

		wrist_left = new int[3];
		wrist_left[0] = X_OFFSET + VIEW_CENTER + HW ;
		wrist_left[1] = Y_OFFSET + HH*4 - HH/2;
		wrist_left[2] = Z_OFFSET + DEFAULT_DISTANCE_TO_BODY;

		knee_left = new int[3];
		knee_left[0] = X_OFFSET + VIEW_CENTER + HW/2 ;
		knee_left[1] = Y_OFFSET + HH*3 - HH/2;
		knee_left[2] = Z_OFFSET + DEFAULT_DISTANCE_TO_BODY;

		knee_right = new int[3];
		knee_right[0] = X_OFFSET + VIEW_CENTER - HW/2 ;
		knee_right[1] = Y_OFFSET + HH*3 - HH/2;
		knee_right[2] = Z_OFFSET + DEFAULT_DISTANCE_TO_BODY;

		ankle_left = new int[3];
		ankle_left[0] = X_OFFSET + VIEW_CENTER + HW/3 ;
		ankle_left[1] = Y_OFFSET + HH/4;
		ankle_left[2] = Z_OFFSET + DEFAULT_DISTANCE_TO_BODY;

		ankle_right = new int[3];
		ankle_right[0] = X_OFFSET + VIEW_CENTER - HW/3 ;
		ankle_right[1] = Y_OFFSET + HH/4;
		ankle_right[2] = Z_OFFSET + DEFAULT_DISTANCE_TO_BODY;

		foot_left = new int[3];
		foot_left[0] = X_OFFSET + VIEW_CENTER + HW/2;
		foot_left[1] = Y_OFFSET + HH/7;
		foot_left[2] = Z_OFFSET + DEFAULT_DISTANCE_TO_BODY - HH - HH/10;

		foot_right = new int[3];
		foot_right[0] = X_OFFSET + VIEW_CENTER - HW/2 ;
		foot_right[1] = Y_OFFSET + HH/7;
		foot_right[2] = Z_OFFSET + DEFAULT_DISTANCE_TO_BODY - HH - HH/10;
	}

	public void draw(Graphics g) {
		g.setColor(Color.GREEN);
		/* Scaling the value to fit the view */
		int head[] = scale(this.head);
		/*Drawing head square */
		g.fillRect(head[0]-SQUARE_RADIUS, head[1]-SQUARE_RADIUS,SQUARE_HEIGHT,SQUARE_HEIGHT);
		/* Scaling the value to fit the view */
		int sc[] = scale(shoulder_center);
		g.setColor(Color.BLUE);
		/* Drawing shoulder center square */
		g.fillRect(sc[0]-SQUARE_RADIUS, sc[1]-SQUARE_RADIUS,SQUARE_HEIGHT,SQUARE_HEIGHT);
		/*Drawing line between head and shoulder center */
		g.drawLine(head[0], head[1],sc[0],sc[1]);
		/* Drawing spine square */
		g.setColor(Color.ORANGE);
		int s[] = scale(this.spine);
		g.fillRect(s[0]-SQUARE_RADIUS, s[1]-SQUARE_RADIUS,SQUARE_HEIGHT,SQUARE_HEIGHT);
		/*Drawing line between shoulder center and spine */
		g.drawLine(sc[0], sc[1],s[0],s[1]);
		/* Drawing left hip square */
		g.setColor(Color.ORANGE);
		int hl[] = scale(hip_left);
		g.fillRect(hl[0]-SQUARE_RADIUS, hl[1]-SQUARE_RADIUS,SQUARE_HEIGHT,SQUARE_HEIGHT);
		/*Drawing line between spine and left heap */
		g.drawLine(s[0], s[1],hl[0],hl[1]);
		/* Drawing right hip square */
		g.setColor(Color.ORANGE);
		int hr[] = scale(hip_right);
		g.fillRect(hr[0]-SQUARE_RADIUS, hr[1]-SQUARE_RADIUS,SQUARE_HEIGHT,SQUARE_HEIGHT);
		/*Drawing line between spine and right heap */
		g.drawLine(s[0], s[1],hr[0],hr[1]);
		/* Drawing the left knee */
		/* Scaling coordinates */
		int kl[] =scale(knee_left);
		g.setColor(Color.GRAY);
		g.fillRect(kl[0]-SQUARE_RADIUS, kl[1]-SQUARE_RADIUS,SQUARE_HEIGHT,SQUARE_HEIGHT);
		/* Drawing line from left hip to left knee */
		g.drawLine(hl[0], hl[1],kl[0],kl[1]);
		/* Drawing the right knee */
		/* Scaling coordinates */
		int kr[] =scale(knee_right);
		g.setColor(Color.GRAY);
		g.fillRect(kr[0]-SQUARE_RADIUS, kr[1]-SQUARE_RADIUS,SQUARE_HEIGHT,SQUARE_HEIGHT);
		/* Drawing line from right hip to right knee */
		g.drawLine(hr[0], hr[1],kr[0],kr[1]);
		/* Drawing the right ankle */
		/* Scaling coordinates */
		int ar[] =scale(ankle_right);
		g.setColor(Color.GRAY);
		g.fillRect(ar[0]-SQUARE_RADIUS, ar[1]-SQUARE_RADIUS,SQUARE_HEIGHT,SQUARE_HEIGHT);
		/* Drawing line from right knee to right ankle */
		g.drawLine(kr[0], kr[1],ar[0],ar[1]);
		/* Drawing the right ankle */
		/* Scaling coordinates */
		int al[] =scale(ankle_left);
		g.setColor(Color.GRAY);
		g.fillRect(al[0]-SQUARE_RADIUS, al[1]-SQUARE_RADIUS,SQUARE_HEIGHT,SQUARE_HEIGHT);
		/* Drawing line from right knee to right ankle */
		g.drawLine(kl[0], kl[1],al[0],al[1]);
		/* Drawing the right foot */
		/* Scaling coordinates */
		int fr[] =scale(foot_right);
		g.setColor(Color.GRAY);
		g.fillRect(fr[0]-SQUARE_RADIUS, fr[1]-SQUARE_RADIUS,SQUARE_HEIGHT,SQUARE_HEIGHT);
		/* Drawing line from right knee to right ankle */
		g.drawLine(ar[0], ar[1],fr[0],fr[1]);
		/* Drawing the left foot */
		/* Scaling coordinates */
		int fl[] =scale(foot_left);
		g.setColor(Color.GRAY);
		g.fillRect(fl[0]-SQUARE_RADIUS, fl[1]-SQUARE_RADIUS,SQUARE_HEIGHT,SQUARE_HEIGHT);
		/* Drawing line from right knee to right ankle */
		g.drawLine(al[0], al[1],fl[0],fl[1]);
		/*Drawing left shoulder */
		/* Scaling left shoulder coordinates */
		int sl[] = scale(shoulder_left);
		/* Drawing the left shoulder */
		g.drawRect(sl[0]-SQUARE_RADIUS, sl[1]-SQUARE_RADIUS,SQUARE_HEIGHT,SQUARE_HEIGHT);
		/* Drawing line from shoulder center to left shoulder */
		g.drawLine(sc[0], sc[1],sl[0],sl[1]);
		/*Drawing left elbow */
		/* Scaling left elbow coordinates */
		int el[] = scale(elbow_left);
		g.setColor(Color.MAGENTA);
		g.drawRect(el[0]-SQUARE_RADIUS, el[1]-SQUARE_RADIUS,SQUARE_HEIGHT,SQUARE_HEIGHT);
		/* Drawing line from left shoulder to left elbow */
		g.drawLine(sl[0], sl[1],el[0],el[1]);
		/*Scaling */
		int sr[] = scale(shoulder_right);
		/* Drawing the right shoulder */
		g.fillRect(sr[0]-SQUARE_RADIUS, sr[1]-SQUARE_RADIUS,SQUARE_HEIGHT,SQUARE_HEIGHT);
		g.drawLine(sc[0], sc[1],sr[0],sr[1]);
		/* Scaling coordinates */
		int er[] =scale(elbow_right);
		/* Drawing the right elbow */
		g.setColor(Color.CYAN);
		g.fillRect(er[0]-SQUARE_RADIUS, er[1]-SQUARE_RADIUS,SQUARE_HEIGHT,SQUARE_HEIGHT);
		/* Drawing line from right shoulder to right elbow */
		g.drawLine(sr[0], sr[1],er[0],er[1]);
		/* Drawing the right wrist */
		/* Scaling coordinates */
		int wr[] =scale(wrist_right);
		g.setColor(Color.GRAY);
		g.fillRect(wr[0]-SQUARE_RADIUS, wr[1]-SQUARE_RADIUS,SQUARE_HEIGHT,SQUARE_HEIGHT);
		/* Drawing line from right elbow to right wrist */
		g.drawLine(er[0], er[1],wr[0],wr[1]);
		/* Drawing the left wrist */
		/* Scaling coordinates */
		int wl[] =scale(wrist_left);
		g.setColor(Color.GRAY);
		g.fillRect(wl[0]-SQUARE_RADIUS, wl[1]-SQUARE_RADIUS,SQUARE_HEIGHT,SQUARE_HEIGHT);
		/* Drawing line from right elbow to right wrist */
		g.drawLine(el[0], el[1],wl[0],wl[1]);
	}

	/**
	 * Scaling  x and y coordinates to fit screen 
	 * @param coords - coordinate
	 * @return - converted coords
	 */
	private int[] scale(int[] coords) {	
		int temp[] =  new int[]{(int)((coords[0]-9000)*ratio),
				(int)((8*HH -coords[1])*ratio),(int)(coords[2]*ratio)};
		return temp;
	}
	
	/**
	 * Scaling coordinates to represent kinect coordinates
	 * @param coords
	 * @return converted coords
	 */
	public int[] convertToKinectCoordinates(int[] coords){
		int[] temp = {(int)(coords[0]*PIXEL_TO_MM_RATIO_X),(int)(coords[1]*PIXEL_TO_MM_RATIO_Y),coords[2]};
		
		return temp;
	}

	public int[] getHead() {
		return head;
	}

	public void setHead(int[] head) {
		this.head = head;
	}

	public int[] getShoulder_left() {
		return shoulder_left;
	}

	public void setShoulder_left(int[] shoulder_left) {
		this.shoulder_left = shoulder_left;
	}

	public int[] getShoulder_right() {
		return shoulder_right;
	}

	public void setShoulder_right(int[] shoulder_right) {
		this.shoulder_right = shoulder_right;
	}

	public int[] getShoulder_center() {
		return shoulder_center;
	}

	public void setShoulder_center(int[] shoulder_center) {
		this.shoulder_center = shoulder_center;
	}

	public int[] getElbow_left() {
		return elbow_left;
	}

	public void setElbow_left(int[] elbow_left) {
		this.elbow_left = elbow_left;
	}

	public int[] getElbow_right() {
		return elbow_right;
	}

	public void setElbow_right(int[] elbow_right) {
		this.elbow_right = elbow_right;
	}

	public int[] getHand_left() {
		return hand_left;
	}

	public void setHand_left(int[] hand_left) {
		this.hand_left = hand_left;
	}

	public int[] getHand_right() {
		return hand_right;
	}

	public void setHand_right(int[] hand_right) {
		this.hand_right = hand_right;
	}

	public int[] getWrist_left() {
		return wrist_left;
	}

	public void setWrist_left(int[] wrist_left) {
		this.wrist_left = wrist_left;
	}

	public int[] getWrist_right() {
		return wrist_right;
	}

	public void setWrist_right(int[] wrist_right) {
		this.wrist_right = wrist_right;
	}

	public int[] getSpine() {
		return spine;
	}

	public void setSpine(int[] spine) {
		this.spine = spine;
	}

	public int[] getHip_center() {
		return hip_center;
	}

	public void setHip_center(int[] hip_center) {
		this.hip_center = hip_center;
	}

	public int[] getHip_left() {
		return hip_left;
	}

	public void setHip_left(int[] hip_left) {
		this.hip_left = hip_left;
	}

	public int[] getHip_right() {
		return hip_right;
	}

	public void setHip_right(int[] hip_right) {
		this.hip_right = hip_right;
	}

	public int[] getKnee_left() {
		return knee_left;
	}

	public void setKnee_left(int[] knee_left) {
		this.knee_left = knee_left;
	}

	public int[] getKnee_right() {
		return knee_right;
	}

	public void setKnee_right(int[] knee_right) {
		this.knee_right = knee_right;
	}

	public int[] getAnkle_left() {
		return ankle_left;
	}

	public void setAnkle_left(int[] ankle_left) {
		this.ankle_left = ankle_left;
	}

	public int[] getAnkle_right() {
		return ankle_right;
	}

	public void setAnkle_right(int[] ankle_right) {
		this.ankle_right = ankle_right;
	}

	public int[] getFoot_left() {
		return foot_left;
	}

	public void setFoot_left(int[] foot_left) {
		this.foot_left = foot_left;
	}

	public int[] getFoot_right() {
		return foot_right;
	}

	public void setFoot_right(int[] foot_right) {
		this.foot_right = foot_right;
	}

	public int[] getTargetArray() {		
		return targetArray;
	}
}

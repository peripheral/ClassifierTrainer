package com.app.utility;

import com.app.entities.Posture;

public class CameraModel {
	/* Pixel to length to mm ratio in x dim */
	public static double PIXEL_TO_MM_RATIO_X=640/19000.0;
	/* Pixel to length to mm ratio in x dim */
	public static double PIXEL_TO_MM_RATIO_Y= 480/2000.0;
	
	/* Kinect depth camera resolution in pix */
	public static int KINECT_WIDTH_RESOLUTION= 640;
	public static int KINECT_HEIGHT_RESOLUTION= 480;
	
	public static double ASPECT_RATIO = 640/480.0;
	
	
	
	
	/**
	 * Scaling coordinates to represent kinect coordinates
	 * @param coords
	 * @return converted coords
	 */
	public static int[] convertToKinectCoordinates(Posture p,double[] coords){
		coords = scale(p, coords);
/*		int[] temp = {(int)((p.X_OFFSET+coords[0])*PIXEL_TO_MM_RATIO_X),(int)((p.Y_OFFSET+coords[1])*PIXEL_TO_MM_RATIO_Y),
				(int)(p.Z_OFFSET+coords[2])};*/
		int temp[] = new int[]{(int)coords[0],(int)coords[1],(int)coords[2]};
		return temp;
	}
	
	
	/**
	 * Scaling based on distance to the camera
	 * @return
	 */
	public static double[] scale(Posture p,double[] coords){
		double[] newCoords = new double[3];
		newCoords[0] = coords[0]/(ASPECT_RATIO*p.getZOffset()*Math.tan(Math.toRadians(54/2)));
		newCoords[1] = coords[1]/(p.getZOffset()*Math.tan(Math.toRadians(45/2)));
		return coords;		
	}
}

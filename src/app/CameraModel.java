package app;

public class CameraModel {
	/* Pixel to length to mm ratio in x dim */
	public static double PIXEL_TO_MM_RATIO_X=640/19000.0;
	/* Pixel to length to mm ratio in x dim */
	public static double PIXEL_TO_MM_RATIO_Y= 480/2000.0;
	
	/* Kinect depth camera resolution in pix */
	public static int KINECT_WIDTH_RESOLUTION= 640;
	public static int KINECT_HEIGHT_RESOLUTION= 480;
	
	
	/**
	 * Scaling coordinates to represent kinect coordinates
	 * @param coords
	 * @return converted coords
	 */
	public static int[] convertToKinectCoordinates(Pose p,double[] coords){
		int[] temp = {(int)((p.X_OFFSET+coords[0])*PIXEL_TO_MM_RATIO_X),(int)((p.Y_OFFSET+coords[1])*PIXEL_TO_MM_RATIO_Y),
				(int)(p.Z_OFFSET+coords[2])};
		return temp;
	}
}

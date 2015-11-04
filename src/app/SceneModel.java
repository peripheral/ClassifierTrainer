package app;

import java.util.LinkedList;

public class SceneModel {
	/* tan(35) * 2000 , with field view of 70 degrees at distance of 2000mm */
	public static int VIEW_CENTER= 9500;
	public static int VIEW_HEIGHT= 2000;
	/* Default distance from camera to the body*/
	public static int DEFAULT_DISTANCE_TO_BODY = 2000;
	private double pose_x_offset;
	private double pose_y_offset;
	private double pose_z_offset;
	
	public SceneModel(){
		pose_x_offset = VIEW_CENTER;
		setPose_y_offset(0);
		setPose_z_offset(DEFAULT_DISTANCE_TO_BODY);
	}
	
	public void applyPositionOnScene(Pose p) {
		p.setXOffset(pose_x_offset);
		p.setYOffset(pose_y_offset);
		p.setZOffset(pose_z_offset);
	}

	public double getPose_y_offset() {
		return pose_y_offset;
	}

	public void setPose_y_offset(int pose_y_offset) {
		this.pose_y_offset = pose_y_offset;
	}

	public double getPose_z_offset() {
		return pose_z_offset;
	}

	public void setPose_z_offset(int pose_z_offset) {
		this.pose_z_offset = pose_z_offset;
	}
	
	public double getPose_x_offset() {
		return pose_x_offset;
	}

	public void setPose_x_offset(int pose_x_offset) {
		this.pose_x_offset = pose_x_offset;
	}

	public void movePose3d(double[] vector) {
		pose_x_offset = pose_x_offset + vector[0];
		pose_y_offset = pose_y_offset + vector[1];
		pose_z_offset = pose_z_offset + vector[2];		
	}
}

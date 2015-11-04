package app;

/** Provides model of the body */
public class BodyModel {

	/* HEad dimensions, HH - head height, HW - head width */
	public static int HH = 225;
	public static int HW = 200;

	/* Center of the skeleton */
	public static int CENTER= 0;

	/* Distance */
	private double head_to_center = 0;
	private double shoulder_center_to_shoulder = 0;
	private double shoulder_to_elbow = 0;
	private double elbow_to_wrist = 0;
	public double getHead_to_center() {
		return head_to_center;
	}

	public void setHead_to_center(double head_to_center) {
		this.head_to_center = head_to_center;
	}

	public double getShoulder_center_to_shoulder() {
		return shoulder_center_to_shoulder;
	}

	public void setShoulder_center_to_shoulder(double shoulder_center_to_shoulder) {
		this.shoulder_center_to_shoulder = shoulder_center_to_shoulder;
	}

	public double getShoulder_to_elbow() {
		return shoulder_to_elbow;
	}

	public void setShoulder_to_elbow(double shoulder_to_elbow) {
		this.shoulder_to_elbow = shoulder_to_elbow;
	}

	public double getElbow_to_wrist() {
		return elbow_to_wrist;
	}

	public void setElbow_to_wrist(double elbow_to_wrist) {
		this.elbow_to_wrist = elbow_to_wrist;
	}

	public double getHip_to_knee() {
		return hip_to_knee;
	}

	public void setHip_to_knee(double hip_to_knee) {
		this.hip_to_knee = hip_to_knee;
	}

	public double getKnee_to_ankle() {
		return knee_to_ankle;
	}

	public void setKnee_to_ankle(double knee_to_ankle) {
		this.knee_to_ankle = knee_to_ankle;
	}

	public double getAnkle_to_foot() {
		return ankle_to_foot;
	}

	public void setAnkle_to_foot(double ankle_to_foot) {
		this.ankle_to_foot = ankle_to_foot;
	}

	private double shoulder_center_to_spine = 0;
	private double spine_to_hip = 0;
	private double hip_to_knee = 0;
	private double knee_to_ankle = 0;
	private double ankle_to_foot = 0;

	private double head[] = null;
	private double shoulder_left[] = null;
	private double shoulder_right[] = null;
	private double shoulder_center[] = null;
	private double elbow_left[] = null;
	private double elbow_right[] = null;
	private double hand_left[] = null;
	private double hand_right[] = null;
	private double wrist_left[] = null;
	private double wrist_right[] = null;
	private double spine[] = null;
	private double hip_left[] = null;
	private double hip_right[] = null;
	private double knee_left[] = null;
	private double knee_right[] = null;
	private double ankle_left[] = null;
	private double ankle_right[] = null;
	private double foot_left[] = null;
	private double foot_right[] = null;
	public static enum JOINT_TAG{HEAD,SHOULDER_LEFT,SHOULDER_RIGHT,SHOULDER_CENTER,
		ELBOW_LEFT,ELBOW_RIGHT,HAND_LEFT,HAND_RIGHT,WRIST_LEFT,WRIST_RIGHT,SPINE,
		HIP_CENTER,HIP_LEFT,HIP_RIGHT,KNEE_LEFT,KNEE_RIGHT,ANKLE_LEFT,ANKLE_RIGHT,
		FOOT_LEFT,FOOT_RIGHT};

		public BodyModel(){
			/* Total height is 9 heads, position of the head
			 *  is in the last 9 section*/
			head = new double[3];
			head[0] = CENTER;
			head[1] = BodyModel.HH*8 - BodyModel.HH/2;
			head[2] = 0;

			shoulder_center = new double[3];
			shoulder_center[0] =  CENTER;
			shoulder_center[1] =  BodyModel.HH*7 - BodyModel.HH/8 ;
			shoulder_center[2] =  0;

			head_to_center = MathUtils.getDistance(head,shoulder_center);

			shoulder_left = new double [3];
			shoulder_left[0] =  CENTER + BodyModel.HW*3/4;
			shoulder_left[1] =  BodyModel.HH*7 - BodyModel.HH/3;
			shoulder_left[2] =  0;

			shoulder_center_to_shoulder = MathUtils.getDistance(shoulder_left,shoulder_center);

			shoulder_right = new double [3];
			shoulder_right[0] =  CENTER - BodyModel.HW*3/4;
			shoulder_right[1] =  BodyModel.HH*7 - BodyModel.HH/3;
			shoulder_right[2] =  0;


			elbow_right = new double [3];
			elbow_right[0] =  CENTER - ( BodyModel.HW +BodyModel.HW/5 );
			elbow_right[1] =  BodyModel.HH*6 - BodyModel.HH*4/5;
			elbow_right[2] =  0;

			shoulder_to_elbow = MathUtils.getDistance(shoulder_right,elbow_right);

			elbow_left = new double [3];
			elbow_left[0] =  CENTER + (BodyModel.HW + BodyModel.HW/5);
			elbow_left[1] =  BodyModel.HH*6 - BodyModel.HH*4/5;
			elbow_left[2] =  0;

			hip_left = new double [3];
			hip_left[0] =  CENTER + BodyModel.HW/2;
			hip_left[1] =  BodyModel.HH*5 - BodyModel.HH/3;
			hip_left[2] =  0;

			spine = new double [3];
			spine[0] =  CENTER;
			spine[1] =  BodyModel.HH*5;
			spine[2] =  0;
			shoulder_center_to_spine = MathUtils.getDistance(spine,shoulder_center);
			spine_to_hip = MathUtils.getDistance(spine,hip_left);

			hip_right = new double [3];
			hip_right[0] =  CENTER - BodyModel.HW/2;
			hip_right[1] =  BodyModel.HH*5 - BodyModel.HH/3;
			hip_right[2] =  0;

			wrist_right = new double[3];
			wrist_right[0] =  CENTER - BodyModel.HW;
			wrist_right[1] =  BodyModel.HH*4 - BodyModel.HH/2;
			wrist_right[2] =  0;

			elbow_to_wrist = MathUtils.getDistance(elbow_left,wrist_left);

			wrist_left = new double[3];
			wrist_left[0] =  CENTER + BodyModel.HW ;
			wrist_left[1] =  BodyModel.HH*4 - BodyModel.HH/2;
			wrist_left[2] =  0;

			knee_left = new double[3];
			knee_left[0] =  CENTER + BodyModel.HW/2 ;
			knee_left[1] =  BodyModel.HH*3 - BodyModel.HH/2;
			knee_left[2] =  0;

			hip_to_knee = MathUtils.getDistance(hip_left,knee_left);

			knee_right = new double[3];
			knee_right[0] =  CENTER - BodyModel.HW/2 ;
			knee_right[1] =  BodyModel.HH*3 - BodyModel.HH/2;
			knee_right[2] =  0;

			ankle_left = new double[3];
			ankle_left[0] =  CENTER + BodyModel.HW/3 ;
			ankle_left[1] =  BodyModel.HH/4;
			ankle_left[2] =  0;

			knee_to_ankle = MathUtils.getDistance(knee_left,ankle_left);

			ankle_right = new double[3];
			ankle_right[0] =  CENTER - BodyModel.HW/3 ;
			ankle_right[1] =  BodyModel.HH/4;
			ankle_right[2] =  0;

			foot_left = new double[3];
			foot_left[0] =  CENTER + BodyModel.HW/2;
			foot_left[1] =  BodyModel.HH/7;
			foot_left[2] =  0 - BodyModel.HH - BodyModel.HH/10;

			ankle_to_foot = MathUtils.getDistance(ankle_left,foot_left);

			foot_right = new double[3];
			foot_right[0] =  CENTER - BodyModel.HW/2 ;
			foot_right[1] =  BodyModel.HH/7;
			foot_right[2] =  0 - BodyModel.HH - BodyModel.HH/10;
		}

		public double getSpine_to_hip() {
			return spine_to_hip;
		}

		public void setSpine_to_hip(double spine_to_hip) {
			this.spine_to_hip = spine_to_hip;
		}

		public double getShoulder_center_spine() {
			return shoulder_center_to_spine;
		}

		public void setShoulder_center_spine(double shoulder_center_spine) {
			this.shoulder_center_to_spine = shoulder_center_spine;
		}

}

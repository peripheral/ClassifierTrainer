package com.app.graphics;

import com.app.graphics.body_model.BodyModelImpl;
import com.app.graphics.body_model.IBodyModel;

public class Scene {
	private PoseRenderer expectedPose = new PoseRenderer();
	private PoseRenderer observedPose = new PoseRenderer();
	
	/**
	 * Returns array of floats, first half contains observed configuration of body
	 * @return
	 */
	public float[] getFeatureVector() {
		IBodyModel bm = new BodyModelImpl();
		float[] vector = new float[38];
		vector[0] = (float)bm.getA1Angle(observedPose.getPose());
		vector[1] = (float)bm.getA2Angle(observedPose.getPose());
		vector[2] = (float)bm.getA3Angle(observedPose.getPose());
		vector[3] = (float)bm.getA4Angle(observedPose.getPose());
		vector[4] = (float)bm.getA5Angle(observedPose.getPose());
		vector[5] = (float)bm.getA6Angle(observedPose.getPose());
		vector[6] = (float)bm.getA7Angle(observedPose.getPose());
		vector[7] = (float)bm.getA8Angle(observedPose.getPose());
		vector[8] = (float)bm.getA9Angle(observedPose.getPose());
		vector[9] = (float)bm.getA10Angle(observedPose.getPose());
		vector[10] = (float)bm.getA11Angle(observedPose.getPose());
		vector[11] = (float)bm.getA12Angle(observedPose.getPose());
		vector[12] = (float)bm.getA13Angle(observedPose.getPose());
		vector[13] = (float)bm.getA14Angle(observedPose.getPose());
		vector[14] = (float)bm.getA15Angle(observedPose.getPose());
		vector[15] = (float)bm.getA16Angle(observedPose.getPose());
		vector[16] = (float)bm.getA17Angle(observedPose.getPose());
		vector[17] = (float)bm.getA18Angle(observedPose.getPose());
		vector[18] = (float)bm.getA19Angle(observedPose.getPose());
		vector[19] = (float)bm.getA1Angle(expectedPose.getPose());
		vector[20] = (float)bm.getA2Angle(expectedPose.getPose());
		vector[21] = (float)bm.getA3Angle(expectedPose.getPose());
		vector[22] = (float)bm.getA4Angle(expectedPose.getPose());
		vector[23] = (float)bm.getA5Angle(expectedPose.getPose());
		vector[24] = (float)bm.getA6Angle(expectedPose.getPose());
		vector[25] = (float)bm.getA7Angle(expectedPose.getPose());
		vector[26] = (float)bm.getA8Angle(expectedPose.getPose());
		vector[27] = (float)bm.getA9Angle(expectedPose.getPose());
		vector[28] = (float)bm.getA10Angle(expectedPose.getPose());
		vector[29] = (float)bm.getA11Angle(expectedPose.getPose());
		vector[30] = (float)bm.getA12Angle(expectedPose.getPose());
		vector[31] = (float)bm.getA13Angle(expectedPose.getPose());
		vector[32] = (float)bm.getA14Angle(expectedPose.getPose());
		vector[33] = (float)bm.getA15Angle(expectedPose.getPose());
		vector[34] = (float)bm.getA16Angle(expectedPose.getPose());
		vector[35] = (float)bm.getA17Angle(expectedPose.getPose());
		vector[36] = (float)bm.getA18Angle(expectedPose.getPose());
		vector[37] = (float)bm.getA19Angle(expectedPose.getPose());
		return vector;
	}
	public PoseRenderer getExpectedPose() {
		return expectedPose;
	}
	public void setExpectedPose(PoseRenderer expectedPose) {
		this.expectedPose = expectedPose;
	}
	public PoseRenderer getObservedPose() {
		return observedPose;
	}
	public void setObservedPose(PoseRenderer givenPose) {
		this.observedPose = givenPose;
	}
	public PoseRenderer getExpectedPoseCanvas() {
		// TODO Auto-generated method stub
		return null;
	}
	
}

package app;

import java.util.Map;
import java.util.Set;

public class SetGenerator {
	private FeatureVectorExport exp;
	private SceneModel scene;

	public SetGenerator(String input,String target){
		exp = new FeatureVectorExport(input,target);
		scene = new SceneModel();
	}
	public void generateSet(Pose p){

	}

	public void translateVariation(Pose p,double[] vector,int times){
		Pose pose = new Pose();
		for(int i=0; i< times;i++){
			scene.movePose3d(vector);
			scene.applyPositionOnScene(pose);
			exp.export(pose);
		}
	}
	/**
	 * 
	 * @param p - pose
	 * @param step - step to divide
	 * @param start - must be less than end, in degrees
	 * @param end - must be larger than start, in degrees
	 */
	public void rotateVariation(Pose p,int step,int start,int end){
		Pose pose = new Pose();
		Map<String,double[]> map = p.getJointList(); 
		for(int i=start; i< end;i+=step){
			Set<String>keys = map.keySet();
			for(String key:keys){
				pose.setJoinLocation(key,rotateAroundY(i,map.get(key)));
			}
			scene.applyPositionOnScene(pose);
			exp.export(pose);
		}
	}

	/**
	 * Multiplication with rotation matrix around Y axis, used for rotation of poses
	 * @param angdeg -angle degrees
	 * @param point
	 * @return
	 */
	public double[] rotateAroundY(double angdeg,double[] point){
		double[] pointR = new double[3];
		pointR[0] = point[0]*Math.cos(Math.toRadians(angdeg))+ point[1]*0 + point[2]*Math.sin(Math.toRadians(angdeg));
		pointR[1] = point[0]*0+ point[1]*1 + point[2]*0;
		pointR[2] = point[0]*(-Math.sin(Math.toRadians(angdeg)))+ point[1]*0 + point[2]*Math.cos(Math.toRadians(angdeg));
		System.out.println("Given"+point[0]+" "+point[2]+" Result"+ pointR[0]+" "+ pointR[2]);
		return pointR;
	}

	/**
	 * Closing the export stream, if missed to call completeProcedure(), nothing is written to file
	 */
	public void completeProcedure(){
		exp.closeStream();
	}
}

package app;

public class MathUtils {

	public static double getDistance(double[] p1,double[] p2){
		double result= Math.sqrt(Math.pow(p1[0] - p2[0],2) +
				Math.pow(p1[1] - p2[1],2) +Math.pow(p1[2] - p2[2],2));
		return result;
	}
}

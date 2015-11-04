package app;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FeatureVectorExport {
	private FileWriter outI = null;
	private FileWriter outT = null;
	File in = null;
	File tar = null;
	
	public FeatureVectorExport(String fileName,String target){
		tar =  new File(target);
		in = new File(fileName);
		if(!in.exists()){
			try {
				in.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
		}	
		try {
			outI = new FileWriter(in,true);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(!tar.exists()){
			try {
				tar.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
		}	
		try {
			outT = new FileWriter(tar,true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void export(Pose pose){	
		try {
			/* Writing down head */
			int head[] = CameraModel.convertToKinectCoordinates(pose,pose.getHead());
			outI.append(head[0]+",");outI.append(head[1]+",");outI.append(head[2]+",");
			/* Writing down Shoulder center */
			int sc[] = CameraModel.convertToKinectCoordinates(pose,pose.getShoulder_center());
			outI.append(sc[0]+",");outI.append(sc[1]+",");outI.append(sc[2]+",");
			/* Writing down right shoulder */
			int sr[] = CameraModel.convertToKinectCoordinates(pose,pose.getShoulder_right());
			outI.append(sr[0]+",");outI.append(sr[1]+",");outI.append(sr[2]+",");
			/* Writing down left shoulder */
			int sl[] = CameraModel.convertToKinectCoordinates(pose,pose.getShoulder_left());
			outI.append(sl[0]+",");outI.append(sl[1]+",");outI.append(sl[2]+",");
			/* Writing down spine */
			int s[] = CameraModel.convertToKinectCoordinates(pose,pose.getSpine());
			outI.append(s[0]+",");outI.append(s[1]+",");outI.append(s[2]+",");
			/* Writing down left elbow*/
			int el[] = CameraModel.convertToKinectCoordinates(pose,pose.getElbow_left());
			outI.append(el[0]+",");outI.append(el[1]+",");outI.append(el[2]+",");
			/* Writing down elbow right*/
			int er[] = CameraModel.convertToKinectCoordinates(pose,pose.getElbow_right());
			outI.append(er[0]+",");outI.append(er[1]+",");outI.append(er[2]+",");
			/* Writing down left wrist */
			int wl[] = CameraModel.convertToKinectCoordinates(pose,pose.getWrist_left());
			outI.append(wl[0]+",");outI.append(wl[1]+",");outI.append(wl[2]+",");
			/* Writing down right wrist */
			int wr[] = CameraModel.convertToKinectCoordinates(pose,pose.getWrist_right());
			outI.append(wr[0]+",");outI.append(wr[1]+",");outI.append(wr[2]+",");
			/* Writing down left hip */
			int hl[] = CameraModel.convertToKinectCoordinates(pose,pose.getHip_left());
			outI.append(hl[0]+",");outI.append(hl[1]+",");outI.append(hl[2]+",");
			/* Writing down right hip */
			int hr[] = CameraModel.convertToKinectCoordinates(pose,pose.getHip_right());
			outI.append(hr[0]+",");outI.append(hr[1]+",");outI.append(hr[2]+",");
			/* Writing down head */
			int kl[] = CameraModel.convertToKinectCoordinates(pose,pose.getKnee_left());
			outI.append(kl[0]+",");outI.append(kl[1]+",");outI.append(kl[2]+",");
			/* Writing down head */
			int kr[] = CameraModel.convertToKinectCoordinates(pose,pose.getKnee_right());
			outI.append(kr[0]+",");outI.append(kr[1]+",");outI.append(kr[2]+",");
			/* Writing down head */
			int al[] = CameraModel.convertToKinectCoordinates(pose,pose.getAnkle_left());
			outI.append(al[0]+",");outI.append(al[1]+",");outI.append(al[2]+",");
			/* Writing down head */
			int ar[] = CameraModel.convertToKinectCoordinates(pose,pose.getAnkle_right());
			outI.append(ar[0]+",");outI.append(ar[1]+",");outI.append(ar[2]+",");
			/* Writing down head */
			int fl[] = CameraModel.convertToKinectCoordinates(pose,pose.getFoot_left());
			outI.append(fl[0]+",");outI.append(fl[1]+",");outI.append(fl[2]+",");
			/* Writing down head */
			int fr[] = CameraModel.convertToKinectCoordinates(pose,pose.getFoot_right());
			outI.append(fr[0]+",");outI.append(fr[1]+",");outI.append(fr[2]+"");
			outI.append("\n");
		} catch (IOException e) {
			System.err.println("Failed to export pose");
		}

		try {
			for(int i:pose.getTargetArray()){
				outT.append(i+",");
			}
			outT.append("\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Closes the output stream
	 */
	public void closeStream(){
		try {
			outI.close();
			outT.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Failed to close");
			return;
		}
		System.out.println("Data was successfull outputed to:"+in+","+tar);
	}
}

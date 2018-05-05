package to_throw;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Random;
import java.util.Stack;

import com.app.entities.Posture;
import com.app.graphics.CameraModel;
import com.app.graphics.body_model.BodyModelImpl;
import com.app.graphics.body_model.IBodyModel;

public class TrainingSetGenerator {
	private FileWriter outI = null;
	private FileWriter outT = null;
	private FileWriter outT1 = null;
	File in = null;
	File tar = null;

	public TrainingSetGenerator(String fileName,String target){
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
			outT1 = new FileWriter(new File("targetAngles.csv"),true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	private static Object lock3 = new Object();
	private static void printCategoryVector(int[] tCategoryVector, FileWriter tWriterNNVector)
			throws IOException {
		synchronized(lock3){
			int counter = 0;
			for(int value:tCategoryVector){
				if(counter < tCategoryVector.length-1){
					tWriterNNVector.append(value+",");
				}else{
					tWriterNNVector.append(value+"\n");
				}
				counter++;
			}
		}
	}
	private static Stack<String> log = new Stack<>();
	private static Object lock1 = new Object();
	private static void printIVectorAngles(  double[] expectedPoseData, double[] observedPoseData
			, FileWriter iWriter) throws IOException {
		synchronized(lock1){
			String log ="";
			for(double value:expectedPoseData){
				iWriter.append(value+",");
				log= log+value+",";
			}
			int counter = 0;
			for(double value:observedPoseData){
				if(counter<expectedPoseData.length-1){
					iWriter.append(value+",");
					log= log+value+",";
				}else{
					iWriter.append(value+"\n");
					log= log+value+".";
				}
				counter++;
			}
			if(TrainingSetGenerator.log.size()<2){
				TrainingSetGenerator.log.push(log);
			}
			log ="";
		}
	}
	private static Object lock2 = new Object();
	private static void printTVectorNumericPercentages(double[] tVectorNumericPercentages,
			FileWriter tWriterVectorPercentages) throws IOException {
		synchronized(lock2){
			int counter = 0;
			for(double v:tVectorNumericPercentages){
				if(!(++counter==tVectorNumericPercentages.length)){
					tWriterVectorPercentages.append((int)v+",");
				}else{
					tWriterVectorPercentages.append((int)v+"\n");
				}
			}	
		}
	}

	private static String getTargetLabelSetA(double[] expectedPoseData,
			double[] observedPoseData,int degreeMax,int edge) {
		double rand = getTargetValueSetA(expectedPoseData, observedPoseData,degreeMax,edge);
		if(rand<=40){			
			return	"\"0-40%\"";
		}
		if(rand<=50){			
			return	"\"40-50%\"";
		}
		if(rand<=60){
			return	"\"50-60%\"";
		}
		if(rand<=80){
			return	"\"60-80%\"";
		}
		if(rand<=100){
			return	"\"80-100%\"";
		}
		return "\"NA\"";
	}

	/**
	 * Total percentage in double, 1-100
	 * @param expectedPoseData - expected array
	 * @param observedPoseData - provided array
	 * @return total matching percentage
	 */
	public static double getTargetValueSetA(double[] expectedPoseData,
			double[] observedPoseData,int degreeMax,int edge) {
		double[] targetVector = new double[expectedPoseData.length];
		double sum = 0;
		double max = 100 * expectedPoseData.length;
		for(int i = 0; i < targetVector.length;i++){
			targetVector[i] = 0;
			targetVector[i] = expectedPoseData[i] - observedPoseData[i];
			if(targetVector[i] <0 ){
				targetVector[i]*=-1;	
			}
			if(targetVector[i] > edge){
				targetVector[i] = 0;
			}else{
				targetVector[i] = 100-(targetVector[i]/edge)*100;
			}	
			sum = sum +targetVector[i];
		}
		return (int)((sum/max)*100);
	}
	
	/**
	 * Total percentage in double, 1-100
	 * @param expectedPoseData - expected array
	 * @param observedPoseData - provided array
	 * @return total matching percentage
	 */
	public static int getTargetValue(float[] expectedPoseData,
			float[] observedPoseData,int degreeMax,int edge) {
		double[] targetVector = new double[expectedPoseData.length];
		double sum = 0;
		double max = 100 * expectedPoseData.length;
		for(int i = 0; i < targetVector.length;i++){
			targetVector[i] = 0;
			targetVector[i] = expectedPoseData[i] - observedPoseData[i];
			if(targetVector[i] <0 ){
				targetVector[i]*=-1;	
			}
			if(targetVector[i] > edge){
				targetVector[i] = 0;
			}else{
				targetVector[i] = 100-(targetVector[i]/edge)*100;
			}	
			sum = sum +targetVector[i];
		}
		return (int)((sum/max)*100);
	}


	/**
	 * Returns vector with percentages, describing difference of each angle
	 * @param expectedPoseData
	 * @param observedPoseData
	 * @param degreeMax - maximal degree of freedom used to mark maximal difference between 2 angles
	 * @return
	 */
	private static double[] getTargetVectorSetA(double[] expectedPoseData,
			double[] observedPoseData,int degreeMax,int edge) {
		double[] targetVector = new double[expectedPoseData.length];
		for(int i = 0; i < targetVector.length;i++){
			targetVector[i] = 0;
			targetVector[i] = (expectedPoseData[i] - observedPoseData[i]);
			if(targetVector[i] <0 ){
				targetVector[i]*=-1;	
			}
			if(targetVector[i] > edge){
				targetVector[i] = 0;
			}else{
				targetVector[i] = 100-(targetVector[i]/edge)*100;
			}			
		}
		return targetVector;
	}

	private static int signum(double nextDouble) {
		if(nextDouble>=0.5){
			return 1;
		}else
			return 0;
	}

	/**
	 * Returns vector presenting categories. 
	 * @param rand
	 * @return
	 */
	private static int[] getCategoryVector(int rand) {
		int[] targetV = {0,0,0,0,0};
		if(rand<40){
			targetV[0] = 1;
			return	targetV;
		}
		if(rand<50){
			targetV[1] = 1;
			return	targetV;
		}
		if(rand<60){
			targetV[2] = 1;
			return	targetV;
		}
		if(rand<80){
			targetV[3] = 1;
			return	targetV;
		}
		if(rand<100){
			targetV[4] = 1;
			return	targetV;
		}
		return targetV;
	}

	/**
	 * Mapping from percentages into labels (0-40%) (40-50%)
	 */


	/**
	 * 
	 * @param degreeMax - degree of freedom of a joint, 
	 * maximum of difference of expected and given angle. This increases the sensitivity to angles
	 * @param randomIterations
	 * @param stepSize
	 * @param inner_limit 
	 * @param sparsityParam2 
	 */
	public static void printSet(int degreeMax,double sparsity, int inner_limit,
			double sparsityParam2,File fileIAngels, File fileTPercentages,
			File fileTPercentagesTree,File fileTLabels, File fileTVectorsANN){
		FileWriter iWriter = null;
		FileWriter tWriterVectorPercentages = null;
		/* For tree classifiers */
		FileWriter tWriterNumericValue = null;
		/* String labels */
		FileWriter tWriterLabel = null;
		/* Array of ones and zeros, as target array for ANN */
		FileWriter tWriterNNVector = null;
		try {
			iWriter =  new FileWriter(fileIAngels);
			tWriterVectorPercentages =  new FileWriter(fileTPercentages);
			tWriterNumericValue =  new FileWriter(fileTPercentagesTree);
			tWriterLabel =  new FileWriter(fileTLabels);
			tWriterNNVector = new FileWriter(fileTVectorsANN);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		double[] expectedPoseData = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
		double[] observedPoseData = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
		int offset = expectedPoseData.length+observedPoseData.length;
		try{
			populate( expectedPoseData,observedPoseData, degreeMax, inner_limit
					, tWriterVectorPercentages, iWriter, tWriterNNVector
					, tWriterNumericValue, tWriterLabel,sparsity,offset,sparsityParam2);
		}catch(Exception e){
			e.printStackTrace();
			System.err.println("Failed to write");
		}
		File f = new File("iAnglesSetE.csv");
		while(Thread.activeCount()>0){
			System.out.println("Active threads:"+Thread.activeCount());
			try {
				Thread.sleep(1000);
				System.out.println(log.pop()+"Used files space in bytes:"+f.length());
				if(f.length()>10000000000L){
					System.exit(-1);
				}
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println("Active threads:"+Thread.activeCount());
		try {
			iWriter.close();
			tWriterVectorPercentages.close();
			tWriterNumericValue.close();
			tWriterLabel.close();
			tWriterNNVector.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	private static int threadCount = 0;
	private static void populate(double[] expectedPoseData,double[] observedPoseData,int degreeMax,int edge
			,FileWriter tWriterVectorPercentages,FileWriter iWriter,FileWriter tWriterNNVector,FileWriter tWriterNumericValue
			,FileWriter tWriterLabel, double sparsity, int offset, double sparsityParam2) throws IOException {
		if(offset <0){
			return;
		}
		double delta = degreeMax*sparsity;
		for(int i = 1;i<degreeMax;i+=delta){			

			if(offset > 36){
				System.out.println("Complete:"+i);
			}
			if(offset <19){
				expectedPoseData[offset]=i;

			}else{
				observedPoseData[offset%19]=i;
			}
			if(offset%sparsityParam2 == 0){
				if(offset > 30){
					System.out.println("Modul offset%sparsityParam2: "+(offset%sparsityParam2)+" Offset:"+offset+" Modul:"+sparsityParam2);
				}
				if(threadCount<4){
					new Thread(){
						@Override
						public void run() {
							super.run();
							threadCount++;
							try {
								double[] exp = Arrays.copyOf(expectedPoseData, expectedPoseData.length);
								double[] obs =  Arrays.copyOf(observedPoseData, observedPoseData.length);

								populate( exp,obs, degreeMax, edge, tWriterVectorPercentages
										, iWriter, tWriterNNVector, tWriterNumericValue, tWriterLabel, sparsity, offset-1,sparsityParam2);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							threadCount--;
						}
					}.start();
				}else{
					populate( expectedPoseData,observedPoseData, degreeMax, edge, tWriterVectorPercentages
							, iWriter, tWriterNNVector, tWriterNumericValue, tWriterLabel, sparsity, offset-1,sparsityParam2);

				}
				return;
			}
			double[] tVectorNumericPercentages  = getTargetVectorSetA(expectedPoseData,observedPoseData,degreeMax,edge);
			double tNumericTotalPercentage = getTargetValueSetA(expectedPoseData, observedPoseData,degreeMax,edge);
			int[] tCategoryVector = getCategoryVector((int)tNumericTotalPercentage);
			String tLabel = getTargetLabelSetA(expectedPoseData, observedPoseData,degreeMax,edge);
			printTVectorNumericPercentages(tVectorNumericPercentages,tWriterVectorPercentages);
			printIVectorAngles(expectedPoseData,observedPoseData,iWriter);
			printCategoryVector(tCategoryVector,tWriterNNVector);
			tWriterNumericValue.append(tNumericTotalPercentage+"\n");
			tWriterLabel.append(tLabel+"\n");
			if(offset > 0){
				populate(expectedPoseData,observedPoseData,  degreeMax, edge, tWriterVectorPercentages
						, iWriter, tWriterNNVector, tWriterNumericValue, tWriterLabel, sparsity, offset-1,sparsityParam2);
			}
		}
		clear(expectedPoseData,observedPoseData,offset);
	}

	private static void clear(double[] expectedPoseData, double[] observedPoseData,  int offset) {
		for(int i = 0;i<offset+1;i++){
			if(i <19){
				expectedPoseData[i]=0;
			}else{
				observedPoseData[i%19]=0;
			}
		}
	}

	public static void printSetB(int degreeMax){
		FileWriter inputWriter = null;
		FileWriter outputWriter = null;
		FileWriter treeTargetVector = null;
		try {
			inputWriter =  new FileWriter(new File("iAnglesSetB.csv"),true);
			outputWriter =  new FileWriter(new File("tPercentagesSetB.csv"),true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		double[] expectedPoseData = {107};
		double[] observedPoseData = {107};
		double[] targetVector ;
		Random rm = new Random();

		try{
			for(int inputId = 0; inputId <expectedPoseData.length;inputId++){	
				for(int degree = 0;degree <360;degree++){
					for(int i = 0;i<expectedPoseData.length;i++){
						expectedPoseData[i] = 360;
						observedPoseData[i] = 360;
					}

					expectedPoseData[inputId] = degree;
					observedPoseData[inputId] = 360-degree;				

					targetVector  = getTargetVectorSetA(expectedPoseData,observedPoseData,degreeMax,360);
					for(double v:targetVector){
						outputWriter.append((int)v+",");
					}
					outputWriter.append("\n");
					for(double value:expectedPoseData){
						inputWriter.append(value+",");
					}
					for(double value:observedPoseData){
						inputWriter.append(value+",");
					}
					inputWriter.append("\n");
				}
			}
			for(int inputId = 0; inputId <expectedPoseData.length;inputId++){
				for(int reps = 0; reps < 200;reps++){
					for(int i = 0;i<expectedPoseData.length;i++){
						expectedPoseData[i] = 0;
						observedPoseData[i] = 0;
					}

					expectedPoseData[inputId] = rm.nextInt(361);
					observedPoseData[inputId] = rm.nextInt(361);				

					targetVector  = getTargetVectorSetA(expectedPoseData,observedPoseData,degreeMax,360);
					for(double v:targetVector){
						outputWriter.append((int)v+",");
					}
					outputWriter.append("\n");
					for(double value:expectedPoseData){
						inputWriter.append(value+",");
					}
					for(double value:observedPoseData){
						inputWriter.append(value+",");
					}
					inputWriter.append("\n");
				}
			}
			for(int reps = 0; reps < 200;reps++){
				for(int i = 0;i<expectedPoseData.length;i++){
					expectedPoseData[i] = 0;
				}
				for(int j = 0;j < expectedPoseData.length;j++){
					expectedPoseData[j] = rm.nextInt(361);
					observedPoseData[j] = rm.nextInt(361);				
				}
				targetVector  = getTargetVectorSetA(expectedPoseData,observedPoseData,degreeMax,360);
				for(double v:targetVector){
					outputWriter.append((int)v+",");
				}
				outputWriter.append("\n");
				for(double value:expectedPoseData){
					inputWriter.append(value+",");
				}
				for(double value:observedPoseData){
					inputWriter.append(value+",");
				}
				inputWriter.append("\n");
			}
		}catch(Exception e){
			e.printStackTrace();
			System.err.println("Failed to write");
		}
		try {
			inputWriter.close();
			outputWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void printSetC(){
		FileWriter inputWriter = null;
		FileWriter outputWriter = null;
		FileWriter treeTargetVector = null;
		try {
			inputWriter =  new FileWriter(new File("iPercentagesSetC.csv"),true);
			outputWriter =  new FileWriter(new File("tLabelsSetC.csv"),true);
			treeTargetVector = new FileWriter(new File("tTreeLablesSetC.csv"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		double[] initialValues = {107};

		int[] targetVector = {0};
		int max = 19*100;
		Random rm = new Random();

		try{
			for(int percent = 0; percent < 100;percent++){

				for(int i = 0; i< 19;i++){
					for(int idx = 0;idx<initialValues.length;idx++){
						initialValues[idx] = 0 ;
					}
					for(int j = i;j < (19*(percent/100.0))+i;j++){
						initialValues[j%initialValues.length] = 100;
					}
					for(double value:initialValues){
						inputWriter.append(value+",");
					}
					inputWriter.append("\n");

					targetVector  = getCategoryVector(percent);
					for(int v:targetVector){
						outputWriter.append(v+",");
					}
					outputWriter.append("\n");
				}			
			}
			for(int reps = 0; reps <20;reps ++){
				for(int percent = 0; percent < 100;percent++){				
					for(int idx = 0;idx<initialValues.length;idx++){
						initialValues[idx] = 0 ;
					}
					for(int j = 0;j < initialValues.length;j++){
						initialValues[j] = percent;
					}
					for(double value:initialValues){
						inputWriter.append(value+",");
					}
					inputWriter.append("\n");

					targetVector  = getCategoryVector(percent);
					for(int v:targetVector){
						outputWriter.append(v+",");
					}
					outputWriter.append("\n");			
				}
			}
			double sum = 0;
			max = 19*100;
			for(int reps = 0; reps <200;reps ++){
				for(int percent = 0; percent < 100;percent++){
					sum = 0;
					for(int idx = 0;idx<initialValues.length;idx++){
						initialValues[idx] = 0 ;
					}
					for(int j = 0;j < initialValues.length;j++){
						initialValues[j] = rm.nextInt(101);
						sum = sum+initialValues[j];
					}
					for(double value:initialValues){
						inputWriter.append(value+",");
					}
					inputWriter.append("\n");

					targetVector  = getCategoryVector((int)((sum/max)*100));
					for(int v:targetVector){
						outputWriter.append(v+",");
					}
					outputWriter.append("\n");			
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			System.err.println("Failed to write");
		}
		try {
			inputWriter.close();
			outputWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public void export(Posture pose){	
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

	/*	try {
			for(int i:pose.getTargetArray()){
				outT.append(i+",");
			}
			outT.append("\n");
		} catch (IOException e) {
			e.printStackTrace();
		}*/
		try {
			NumberFormat nf = NumberFormat.getIntegerInstance();

			IBodyModel b = new BodyModelImpl();
			outT1.append(nf.format(Math.toDegrees(b.getA1Angle(pose)))+",");
			outT1.append(nf.format(Math.toDegrees(b.getA2Angle(pose)))+",");
			outT1.append(nf.format(Math.toDegrees(b.getA3Angle(pose)))+",");
			outT1.append(nf.format(Math.toDegrees(b.getA4Angle(pose)))+",");
			outT1.append(nf.format(Math.toDegrees(b.getA5Angle(pose)))+",");
			outT1.append(nf.format(Math.toDegrees(b.getA6Angle(pose)))+",");
			outT1.append(nf.format(Math.toDegrees(b.getA7Angle(pose)))+",");
			outT1.append(nf.format(Math.toDegrees(b.getA8Angle(pose)))+",");
			outT1.append(nf.format(Math.toDegrees(b.getA9Angle(pose)))+",");
			outT1.append(nf.format(Math.toDegrees(b.getA10Angle(pose)))+",");
			outT1.append(nf.format(Math.toDegrees(b.getA11Angle(pose)))+",");
			outT1.append(nf.format(Math.toDegrees(b.getA12Angle(pose)))+",");
			outT1.append(nf.format(Math.toDegrees(b.getA13Angle(pose)))+",");
			outT1.append(nf.format(Math.toDegrees(b.getA14Angle(pose)))+",");
			outT1.append(nf.format(Math.toDegrees(b.getA15Angle(pose)))+",");
			outT1.append(nf.format(Math.toDegrees(b.getA16Angle(pose)))+",");
			outT1.append(nf.format(Math.toDegrees(b.getA17Angle(pose)))+",");
			outT1.append(nf.format(Math.toDegrees(b.getA18Angle(pose)))+",");
			outT1.append(nf.format(Math.toDegrees(b.getA19Angle(pose)))+",");
			outT1.append("\n");
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
			outT1.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Failed to close");
			return;
		}
		System.out.println("Data was successfull outputed to:"+in+","+tar);
	}


	public static void printSetD() {
		FileWriter inputWriter = null;
		FileWriter outputWriter = null;
		FileWriter treeTargetVector = null;
		try {
			inputWriter =  new FileWriter(new File("iAnglesSetD.csv"),true);
			outputWriter =  new FileWriter(new File("tLabelsSetD.csv"),true);
			treeTargetVector = new FileWriter(new File("tTreeLablesSetD.csv"),true);
			treeTargetVector = new FileWriter(new File("tPercentagesSetD.csv"),true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param degreeMax - degree of freedom of a joint, 
	 * maximum of difference of expected and given angle. This increases the sensitivity to angles
	 * @param randomIterations
	 * @param stepSize
	 * @param edge 
	 */
	public static void printSetA(int degreeMax,int randomIterations,double stepSize, int edge){
		FileWriter iWriter = null;
		FileWriter tWriterVectorPercentages = null;
		/* For tree classifiers */
		FileWriter tWriterNumericValue = null;
		/* String labels 0-40% */
		FileWriter tWriterLabel = null;
		/* String labels 0-40% */
		FileWriter tWriterNNVector = null;
		try {
			iWriter =  new FileWriter(new File("iAnglesSetA.csv"));
			tWriterVectorPercentages =  new FileWriter(new File("tPercentagesSetA.csv"));
			tWriterNumericValue =  new FileWriter(new File("tForTreeClasPercentagesSetA.csv"));
			tWriterLabel =  new FileWriter(new File("tLabelsSetA.csv"));
			tWriterNNVector = new FileWriter(new File("tNNVectorSetA.csv"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		double[] expectedPoseData = {107,107,107,73,73,90,	90,	127,90,	90,	90,	90,	90
				,	90,	176,96,	96,	81,	99};
		double[] observedPoseData = {107,107,107,73,73,90,	90,	127,90,	90,	90,	90,	90
				,	90,	176,96,	96,	81,	99};
		double[] tVectorNumericPercentages ;
		int[] tCategoryVector;
		double tNumericTotalPercentage;
		String tLabel = "";
		Random rm = new Random();

		try{
			for(int degree = 0;degree <degreeMax;degree++){
				for(int i = 0;i<expectedPoseData.length;i++){
					expectedPoseData[i] = degreeMax-degree;
					observedPoseData[i] = degree;
				}

				tVectorNumericPercentages  = getTargetVectorSetA(expectedPoseData,observedPoseData,degreeMax,edge);
				tNumericTotalPercentage = getTargetValueSetA(expectedPoseData, observedPoseData,degreeMax,edge);
				tCategoryVector = getCategoryVector((int)tNumericTotalPercentage);
				tLabel = getTargetLabelSetA(expectedPoseData, observedPoseData,degreeMax,edge);
				printTVectorNumericPercentages(tVectorNumericPercentages,tWriterVectorPercentages);
				printIVectorAngles(expectedPoseData,observedPoseData,iWriter);
				printCategoryVector(tCategoryVector,tWriterNNVector);
				tWriterNumericValue.append(tNumericTotalPercentage+"\n");
				tWriterLabel.append(tLabel+"\n");
			}
			for(int inputId = 0; inputId <19;inputId++){	
				for(int degree = 0;degree <degreeMax;degree++){
					for(int i = 0;i<expectedPoseData.length;i++){
						expectedPoseData[i] = degreeMax;
						observedPoseData[i] = 0;
					}
					expectedPoseData[inputId] = degree;
					observedPoseData[inputId] = degreeMax-degree;	

					tVectorNumericPercentages  = getTargetVectorSetA(expectedPoseData,observedPoseData,degreeMax,edge);
					tNumericTotalPercentage = getTargetValueSetA(expectedPoseData, observedPoseData,degreeMax,edge);
					tCategoryVector = getCategoryVector((int)tNumericTotalPercentage);
					tLabel = getTargetLabelSetA(expectedPoseData, observedPoseData,degreeMax,edge);
					printTVectorNumericPercentages(tVectorNumericPercentages,tWriterVectorPercentages);
					printIVectorAngles(expectedPoseData,observedPoseData,iWriter);
					printCategoryVector(tCategoryVector,tWriterNNVector);
					tWriterNumericValue.append(tNumericTotalPercentage+"\n");
					tWriterLabel.append(tLabel+"\n");
				}
			}
			for(double range = 0;range <1;range+=stepSize){
				for(int inputId = 0; inputId <19;inputId++){	
					for(int degree = 0;degree <degreeMax;degree++){
						for(int i = 0;i<expectedPoseData.length;i++){
							expectedPoseData[i] = degreeMax;
							observedPoseData[i] = 0;
						}
						expectedPoseData[inputId] = degree;
						observedPoseData[inputId] = degreeMax-degree;	
						for(int i = inputId+1;i<expectedPoseData.length*range+inputId+1;i++){
							expectedPoseData[i%expectedPoseData.length] = degree;
							observedPoseData[i%expectedPoseData.length] = degree;
						}
						tVectorNumericPercentages  = getTargetVectorSetA(expectedPoseData,observedPoseData,degreeMax,edge);
						tNumericTotalPercentage = getTargetValueSetA(expectedPoseData, observedPoseData,degreeMax,edge);
						tCategoryVector = getCategoryVector((int)tNumericTotalPercentage);
						tLabel = getTargetLabelSetA(expectedPoseData, observedPoseData,degreeMax,edge);
						printTVectorNumericPercentages(tVectorNumericPercentages,tWriterVectorPercentages);
						printIVectorAngles(expectedPoseData,observedPoseData,iWriter);
						printCategoryVector(tCategoryVector,tWriterNNVector);
						tWriterNumericValue.append(tNumericTotalPercentage+"\n");
						tWriterLabel.append(tLabel+"\n");
					}
				}
			}

			for(int inputId = 0; inputId <19;inputId++){
				for(int reps = 0; reps < randomIterations;reps++){
					for(int i = 0;i<expectedPoseData.length;i++){
						expectedPoseData[i] = 0;
						observedPoseData[i] = 0;
					}
					expectedPoseData[inputId] = rm.nextInt(degreeMax);
					observedPoseData[inputId] = rm.nextInt(degreeMax);				
					tVectorNumericPercentages  = getTargetVectorSetA(expectedPoseData,observedPoseData,degreeMax,edge);
					tNumericTotalPercentage = getTargetValueSetA(expectedPoseData, observedPoseData,degreeMax,edge);
					tCategoryVector = getCategoryVector((int)tNumericTotalPercentage);
					tLabel = getTargetLabelSetA(expectedPoseData, observedPoseData,degreeMax,edge);
					printTVectorNumericPercentages(tVectorNumericPercentages,tWriterVectorPercentages);
					printIVectorAngles(expectedPoseData,observedPoseData,iWriter);
					printCategoryVector(tCategoryVector,tWriterNNVector);
					tWriterNumericValue.append(tNumericTotalPercentage+"\n");
					tWriterLabel.append(tLabel+"\n");
				}
			}
			for(int reps = 0; reps < randomIterations;reps++){
				for(int j = 0;j < 19;j++){
					expectedPoseData[j] = rm.nextInt(degreeMax);
					observedPoseData[j] = rm.nextInt(degreeMax);				
				}			
				tVectorNumericPercentages  = getTargetVectorSetA(expectedPoseData,observedPoseData,degreeMax,edge);
				tNumericTotalPercentage = getTargetValueSetA(expectedPoseData, observedPoseData,degreeMax,edge);
				tCategoryVector = getCategoryVector((int)tNumericTotalPercentage);
				tLabel = getTargetLabelSetA(expectedPoseData, observedPoseData,degreeMax,edge);
				printTVectorNumericPercentages(tVectorNumericPercentages,tWriterVectorPercentages);
				printIVectorAngles(expectedPoseData,observedPoseData,iWriter);
				printCategoryVector(tCategoryVector,tWriterNNVector);
				tWriterNumericValue.append(tNumericTotalPercentage+"\n");
				tWriterLabel.append(tLabel+"\n");
			}
		}catch(Exception e){
			System.err.println("Failed to write");
		}
		try {
			iWriter.close();
			tWriterVectorPercentages.close();
			tWriterNumericValue.close();
			tWriterLabel.close();
			tWriterNNVector.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	private static int[] getTargetArray(float[] inputMatrix, int offset,int rowLength,int outerlimit,int innerlimit) {
		float[] expectedPoseData = Arrays.copyOfRange(inputMatrix, offset, offset+(rowLength)/2);
		float[] observedPoseData = Arrays.copyOfRange(inputMatrix, offset, offset+rowLength);
		return getCategoryVector((int)getTargetValue(expectedPoseData, observedPoseData, outerlimit, innerlimit));
	}
	
	public static float[][] generateNextRandomTrainingSetANN(int rows,int outerlimit,int innerlimit){
		int inputColumns = 38;
		int outputColumns = 5;
		float[] inputMatrix = new float[rows*inputColumns];
		float[] outputMatrix = new float[rows*outputColumns];
		Random rm = new Random();
		for(int i = 0;i < rows;i++){
			for(int j = 0; j<inputColumns; j ++){
				inputMatrix[i*inputColumns+j] = rm.nextInt(outerlimit);
			}
			int[] targetArr = getTargetArray(inputMatrix,i*inputColumns,outputColumns,outerlimit,innerlimit);
			for(int j = 0; j<outputColumns;j++){
				outputMatrix[i*outputColumns+j] = targetArr[j];				
			}
		}
		float[][] result = new float[2][];
		result[0] = inputMatrix;
		result[1] = outputMatrix;
		return result;
	}
	
	public static void main(String[] args){
		File fileIAngels = new File("iAnglesSetE.csv");
		File fileTPercentages = new File("tPercentagesSetE.csv");
		File fileTPercentagesTree = new File("tForTreeClasPercentagesSetE.csv");
		File fileTLabels = new File("tLabelsSetE.csv");
		File fileTVectorsANN = new File("tNNVectorSetE.csv");
		//Maximal angle of freedom
		int degreeOfFreedom = 190;
		int randomRepetitions = 300;
		int edge = 45;
		double sparsity = 0.3;
		int sparsityParam2 = 2;
		//FeatureVectorExport.printSetA(degreeOfFreedom,randomRepetitions,stepSize,edge);
		//FeatureVectorExport.printSetB(degreeOfFreedom);
		//FeatureVectorExport.printSetC();
		//FeatureVectorExport.printSetD();
		printSet(degreeOfFreedom, sparsity, edge,sparsityParam2, fileIAngels,
				fileTPercentages, fileTPercentagesTree,fileTLabels, fileTVectorsANN);
		System.out.println("Done");
	}

}

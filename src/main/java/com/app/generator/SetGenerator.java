package com.app.generator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;
import java.util.Vector;

import com.app.classifiers.FeatureVectorModel;
import com.app.entities.Posture;
import com.app.generator.SetGenerator.TAG;
import com.app.graphics.SceneModel;
import com.app.graphics.body_model.BodyModelImpl;
import com.app.graphics.body_model.IBodyModel;
import com.app.utility.CameraModel;
import to_throw.TrainingSetGenerator;

public class SetGenerator {
	private FileWriter angleWriter = null;
	private FileWriter percentageWriter = null;
	private FileWriter AnnTarVecWriter = null;
	private FileWriter KNNTarWriter = null;
	private FileWriter RTreeTarWriter = null;
	private TrainingSetGenerator exp;
	private SceneModel scene;
	/**
	 * Destination to print input(X) part of training data
	 */
	private String inputDest;
	/**
	 * Destination to print input(X) which is deviations part of training data
	 */
	private String inputDevDest;
	/**
	 * Destination to print input(X) which is angles part of training data
	 */
	private String inputAngleDest;
	/**
	 * Destination to print target(Y) part of training data, for ANN
	 */
	private String tarAnnOutFile;
	/**
	 * Destination to print target(Y) part of training data, for K-NN
	 */
	private String tarKnnOutFile;
	/**
	 * Destination to print target(Y) part of training data, for Random Trees
	 */
	private String tarRTrees;
	/**
	 * Not implemented
	 */
	private int sparcity;

	/**
	 * Size of examples in training data
	 */
	private int size;
	public enum TAG{SPARCITY, ROWS};

	/**
	 * Constructor initiates set generator to create input and target vector for ANN. 
	 * Where input vector is consisting of angles and target vectors consists of
	 *  zeros and once.
	 * @param inAngleFileName - 
	 * @param AnnTarVecFileName
	 */
	public SetGenerator(String inAngleFileName,String AnnTarVecFileName,
			String inPercentagesFileName,String TreeTargetLabelsFileName){
		File ANNTarVecFile =  new File(AnnTarVecFileName);
		File inAngleFile = new File(inAngleFileName);
		if(testFileNameArgument(inAngleFileName)){
			try {
				inAngleFile.createNewFile();
				angleWriter = new FileWriter(inAngleFile,true);
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
		}	
		if(testFileNameArgument(AnnTarVecFileName)){
			try {
				ANNTarVecFile.createNewFile();
				AnnTarVecWriter = new FileWriter(ANNTarVecFile,true);
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
		}
		if(testFileNameArgument(inPercentagesFileName)){
			try {
				ANNTarVecFile.createNewFile();
				AnnTarVecWriter = new FileWriter(ANNTarVecFile,true);
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
		}
		if(testFileNameArgument(AnnTarVecFileName)){
			try {
				ANNTarVecFile.createNewFile();
				AnnTarVecWriter = new FileWriter(ANNTarVecFile,true);
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
		}
		if(testFileNameArgument(AnnTarVecFileName)){
			try {
				ANNTarVecFile.createNewFile();
				AnnTarVecWriter = new FileWriter(ANNTarVecFile,true);
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
		}

	}

	/**
	 * Tests if given file name is not null and such file doesn't exist
	 * @param fileName
	 * @return
	 */
	private boolean testFileNameArgument(String fileName) {
		if(fileName != null){
			if(!new File(fileName).isFile()){
				return true;
			}
		}
		return false;
	}

	private static void printArray(int[] array, FileWriter writer)
			throws IOException {
		int counter = 0;
		for(int value:array){
			if(counter < array.length-1){
				writer.append(value+",");
			}else{
				writer.append(value+"\n");
			}
			counter++;
		}

	}

	private static Stack<String> log = new Stack<>();
	private static Object lock1 = new Object();
	private static void printVectors(  double[] expectedPoseData, double[] observedPoseData
			, FileWriter writer) throws IOException {
		synchronized(lock1){
			String log ="";
			for(double value:expectedPoseData){
				writer.append(value+",");
				log= log+value+",";
			}
			int counter = 0;
			for(double value:observedPoseData){
				if(counter<expectedPoseData.length-1){
					writer.append(value+",");
					log= log+value+",";
				}else{
					writer.append(value+"\n");
					log= log+value+".";
				}
				counter++;
			}
			if(SetGenerator.log.size()<2){
				SetGenerator.log.push(log);
			}
			log ="";
		}
	}
	private static Object lock2 = new Object();
	private static void printArray(double[] tVectorNumericPercentages,
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
	 * Returns vector with percentages, describing difference of each angle
	 * @param postureDescriptorExpected - posture descriptor of expected posture,  angle measurements
	 * @param postureDescriptorObserved - posture descriptor of observed posture, angle measurements
	 * @param degreeMax - maximal degree of freedom used to mark maximal difference between 2 angles
	 * @return
	 */
	private static double[] getPercentages(double[] postureDescriptorExpected,
			double[] postureDescriptorObserved,int degreeMax,int edge) {
		double[] targetVector = new double[postureDescriptorExpected.length];
		for(int i = 0; i < targetVector.length;i++){
			targetVector[i] = 0;
			targetVector[i] = (postureDescriptorExpected[i] - postureDescriptorObserved[i]);
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
	 *  {0,0,0,0,1} - 80% and above
	 * {0,0,0,1,0} - 60% - 80% , 
	 * {0,0,1,0,0} - 50% - 60%
	 * {0,1,0,0,0} - 40% - 50%
	 * {1,0,0,0,0} - 0% - 40%
	 * @param percentage
	 * @return
	 */
	private static int[] getAnnTargVector(int percentage) {
		int[] targetV = {0,0,0,0,0};
		if(percentage<40){
			targetV[0] = 1;
			return	targetV;
		}
		if(percentage<50){
			//targetV[0] = 1;
			targetV[1] = 1;
			return	targetV;
		}
		if(percentage<60){
		//	targetV[0] = 1;
			//targetV[1] = 1;
			targetV[2] = 1;
			return	targetV;
		}
		if(percentage<80){
		//	targetV[0] = 1;
		//	targetV[1] = 1;
		//	targetV[2] = 1;
			targetV[3] = 1;
			return	targetV;
		}
		if(percentage<=100){
		//	targetV[0] = 1;
		//	targetV[1] = 1;
		//	targetV[2] = 1;
		//	targetV[3] = 1;
			targetV[4] = 1;
			return	targetV;
		}
		return targetV;
	}



	private static int threadCount = 0;
	/**
	 * Populate datafiles with input data for
	 * classifiers and respective target data
	 * @param expectedPoseData
	 * @param observedPoseData
	 * @param outer_limit
	 * @param inner_limit
	 * @param tWriterVectorPercentages
	 * @param iWriter
	 * @param tWriterNNVector
	 * @param tWriterNumericValue
	 * @param tWriterLabel
	 * @param sparsity
	 * @param offset
	 * @param sparsityParam2
	 * @throws IOException
	 */
	private static void populate(double[] expectedPoseData,double[] observedPoseData,int outer_limit,int inner_limit
			,FileWriter tWriterVectorPercentages,FileWriter iWriter,FileWriter tWriterNNVector,FileWriter tWriterNumericValue
			,FileWriter tWriterLabel, double sparsity, int offset, double sparsityParam2) throws IOException {
		if(offset <0){
			return;
		}
		double delta = outer_limit*sparsity;
		for(int i = 1;i<outer_limit;i+=delta){			

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

								populate( exp,obs, outer_limit, inner_limit, tWriterVectorPercentages
										, iWriter, tWriterNNVector, tWriterNumericValue, tWriterLabel, sparsity, offset-1,sparsityParam2);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							threadCount--;
						}
					}.start();
				}else{
					populate( expectedPoseData,observedPoseData, outer_limit, inner_limit, tWriterVectorPercentages
							, iWriter, tWriterNNVector, tWriterNumericValue, tWriterLabel, sparsity, offset-1,sparsityParam2);

				}
				return;
			}
			double[] tVectorNumericPercentages  = getPercentages(expectedPoseData,observedPoseData,outer_limit,inner_limit);
			double tNumericTotalPercentage = getTargetValueSetA(expectedPoseData, observedPoseData,outer_limit,inner_limit);
			int[] tCategoryVector = getAnnTargVector((int)tNumericTotalPercentage);
			String tLabel = getTargetLabelSetA(expectedPoseData, observedPoseData,outer_limit,inner_limit);
			printArray(tVectorNumericPercentages,tWriterVectorPercentages);
			printVectors(expectedPoseData,observedPoseData,iWriter);
			printArray(tCategoryVector,tWriterNNVector);
			tWriterNumericValue.append(tNumericTotalPercentage+"\n");
			tWriterLabel.append(tLabel+"\n");
			if(offset > 0){
				populate(expectedPoseData,observedPoseData,  outer_limit, inner_limit, tWriterVectorPercentages
						, iWriter, tWriterNNVector, tWriterNumericValue, tWriterLabel, sparsity, offset-1,sparsityParam2);
			}
		}
		clear(expectedPoseData,observedPoseData,offset);
	}

	/**
	 * Clear array till given offset
	 * @param expectedPoseData
	 * @param observedPoseData
	 * @param offset value between 0 to size sum of expect and observed data
	 */
	private static void clear(double[] expectedPoseData, double[] observedPoseData,  int offset) {
		for(int i = 0;i<offset+1;i++){
			if(i <19){
				expectedPoseData[i]=0;
			}else{
				observedPoseData[i%19]=0;
			}
		}
	}

	/**
	 * 
	 * @param outer_limit - degree of freedom of a joint, the maximum size of angle that
	 *  variable can take. 
	 * @param randomIterations 
	 * @param stepSize
	 * @param edge 
	 * @param sparsityParam2 
	 */
	public static void printSetE(int outer_limit,double sparsity, int edge, double sparsityParam2){
		FileWriter iWriter = null;
		FileWriter tWriterVectorPercentages = null;
		/* For tree classifiers */
		FileWriter tWriterNumericValue = null;
		/* String labels 0-40% */
		FileWriter tWriterLabel = null;
		/* String labels 0-40% */
		FileWriter tWriterNNVector = null;
		try {
			iWriter =  new FileWriter(new File("iAnglesSetE.csv"));
			tWriterVectorPercentages =  new FileWriter(new File("tPercentagesSetE.csv"));
			tWriterNumericValue =  new FileWriter(new File("tForTreeClasPercentagesSetE.csv"));
			tWriterLabel =  new FileWriter(new File("tLabelsSetE.csv"));
			tWriterNNVector = new FileWriter(new File("tNNVectorSetE.csv"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		double[] expectedPoseData = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
		double[] observedPoseData = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
		int offset = expectedPoseData.length+observedPoseData.length;
		try{
			populate( expectedPoseData,observedPoseData, outer_limit, edge
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

					targetVector  = getAnnTargVector(percent);
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

					targetVector  = getAnnTargVector(percent);
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

					targetVector  = getAnnTargVector((int)((sum/max)*100));
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

					targetVector  = getPercentages(expectedPoseData,observedPoseData,degreeMax,360);
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

					targetVector  = getPercentages(expectedPoseData,observedPoseData,degreeMax,360);
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
				targetVector  = getPercentages(expectedPoseData,observedPoseData,degreeMax,360);
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

				tVectorNumericPercentages  = getPercentages(expectedPoseData,observedPoseData,degreeMax,edge);
				tNumericTotalPercentage = getTargetValueSetA(expectedPoseData, observedPoseData,degreeMax,edge);
				tCategoryVector = getAnnTargVector((int)tNumericTotalPercentage);
				tLabel = getTargetLabelSetA(expectedPoseData, observedPoseData,degreeMax,edge);
				printArray(tVectorNumericPercentages,tWriterVectorPercentages);
				printVectors(expectedPoseData,observedPoseData,iWriter);
				printArray(tCategoryVector,tWriterNNVector);
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

					tVectorNumericPercentages  = getPercentages(expectedPoseData,observedPoseData,degreeMax,edge);
					tNumericTotalPercentage = getTargetValueSetA(expectedPoseData, observedPoseData,degreeMax,edge);
					tCategoryVector = getAnnTargVector((int)tNumericTotalPercentage);
					tLabel = getTargetLabelSetA(expectedPoseData, observedPoseData,degreeMax,edge);
					printArray(tVectorNumericPercentages,tWriterVectorPercentages);
					printVectors(expectedPoseData,observedPoseData,iWriter);
					printArray(tCategoryVector,tWriterNNVector);
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
						tVectorNumericPercentages  = getPercentages(expectedPoseData,observedPoseData,degreeMax,edge);
						tNumericTotalPercentage = getTargetValueSetA(expectedPoseData, observedPoseData,degreeMax,edge);
						tCategoryVector = getAnnTargVector((int)tNumericTotalPercentage);
						tLabel = getTargetLabelSetA(expectedPoseData, observedPoseData,degreeMax,edge);
						printArray(tVectorNumericPercentages,tWriterVectorPercentages);
						printVectors(expectedPoseData,observedPoseData,iWriter);
						printArray(tCategoryVector,tWriterNNVector);
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
					tVectorNumericPercentages  = getPercentages(expectedPoseData,observedPoseData,degreeMax,edge);
					tNumericTotalPercentage = getTargetValueSetA(expectedPoseData, observedPoseData,degreeMax,edge);
					tCategoryVector = getAnnTargVector((int)tNumericTotalPercentage);
					tLabel = getTargetLabelSetA(expectedPoseData, observedPoseData,degreeMax,edge);
					printArray(tVectorNumericPercentages,tWriterVectorPercentages);
					printVectors(expectedPoseData,observedPoseData,iWriter);
					printArray(tCategoryVector,tWriterNNVector);
					tWriterNumericValue.append(tNumericTotalPercentage+"\n");
					tWriterLabel.append(tLabel+"\n");
				}
			}
			for(int reps = 0; reps < randomIterations;reps++){
				for(int j = 0;j < 19;j++){
					expectedPoseData[j] = rm.nextInt(degreeMax);
					observedPoseData[j] = rm.nextInt(degreeMax);				
				}			
				tVectorNumericPercentages  = getPercentages(expectedPoseData,observedPoseData,degreeMax,edge);
				tNumericTotalPercentage = getTargetValueSetA(expectedPoseData, observedPoseData,degreeMax,edge);
				tCategoryVector = getAnnTargVector((int)tNumericTotalPercentage);
				tLabel = getTargetLabelSetA(expectedPoseData, observedPoseData,degreeMax,edge);
				printArray(tVectorNumericPercentages,tWriterVectorPercentages);
				printVectors(expectedPoseData,observedPoseData,iWriter);
				printArray(tCategoryVector,tWriterNNVector);
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




	public static float[] inputMatrix;
	public static float[] outputMatrix;

	/***
	 * 
	 * @param matrix 
	 * @param rows
	 * @param degreeOfFreedom
	 * @param edge
	 * @return 2 dimensional array of floats. where first dimesions [0][] is inputMatrix while [1][] is outputMatrix
	 */
	public static float[][] generateNextRandomTrainingSetANN(float[][] matrix, int rows,int degreeOfFreedom,int edge
			,int inputColumns,int outputColumns){

		int inputIdx = 0;
		int outputIdx = 1;
		Random rm = new Random();
		for(int i = 0;i < rows;i++){
			for(int j = 0; j<inputColumns ; j ++){
				matrix[inputIdx][i*inputColumns+j] = rm.nextInt(degreeOfFreedom);
				if(matrix[inputIdx][i*inputColumns+j] ==0){
					matrix[inputIdx][i*inputColumns+j] =1;
				}
			}
			int[] targetArr = getTargetArray(matrix[inputIdx],i*inputColumns,outputColumns,edge);
			for(int j = 0; j<outputColumns;j++){
				matrix[outputIdx][i*outputColumns+j] = targetArr[j];				
			}
		}

		return matrix;
	}

	private  int angle = 0;
	/***
	 * 
	 * @param matrix 
	 * @param rows
	 * @param degreeOfFreedom
	 * @param edge
	 * @return 2 dimensional array of floats. where first dimesions [0][] is inputMatrix while [1][] is outputMatrix
	 */
	public float[][] generateNextMonotonTrainingSetANN(float[][] matrix, int rows,int degreeOfFreedom,int edge
			,int inputColumns,int outputColumns){

		int inputIdx = 0;
		int outputIdx = 1;
		Random rm = new Random();
		for(int i = 0;i < rows;i++){
			angle++;
			if(angle < 0){
				angle = 0;
			}
			for(int j = 0; j<inputColumns ; j ++){
				matrix[inputIdx][i*inputColumns+j] = angle%degreeOfFreedom;
				if(matrix[inputIdx][i*inputColumns+j] ==0){
					matrix[inputIdx][i*inputColumns+j] =1;
				}
			}
			int[] targetArr = getTargetArray(matrix[inputIdx],i*inputColumns,outputColumns,edge);
			for(int j = 0; j<outputColumns;j++){
				matrix[outputIdx][i*outputColumns+j] = targetArr[j];				
			}
		}

		return matrix;
	}

	private static int[] getTargetArray(float[] inputMatrix, int offset,int rowLength,int edge) {
		float[] expectedPoseData = Arrays.copyOfRange(inputMatrix, offset, offset+(rowLength)/2);
		float[] observedPoseData = Arrays.copyOfRange(inputMatrix, offset+(rowLength)/2, offset+rowLength);
		int targetVal =(int)getTargetValue(expectedPoseData, observedPoseData, edge);
		return getAnnTargVector(targetVal);
	}

	/**
	 * Total percentage in double, 1-100
	 * @param expectedPoseData - expected array
	 * @param observedPoseData - provided array
	 * @return total matching percentage
	 */
	public static int getTargetValue(float[] expectedPoseData,
			float[] observedPoseData,int edge) {
		double[] percentageVector = new double[expectedPoseData.length];
		double sum = 0;
		double max = 100 * expectedPoseData.length;
		for(int i = 0; i < percentageVector.length;i++){
			percentageVector[i] = 0;
			percentageVector[i] = expectedPoseData[i] - observedPoseData[i];
			if(percentageVector[i] <0 ){
				percentageVector[i]*=-1;	
			}
			if(percentageVector[i] > edge){
				percentageVector[i] = 0;
			}else{
				percentageVector[i] = 100-(percentageVector[i]/edge)*100;
			}	
			sum = sum +percentageVector[i];
		}
		return (int)((sum/max)*100);
	}

	private float[] inputArray = new float[]{	1,1,1,1,1,1,1,1,1,1,
			1,1,1,1,1,1,1,1,1,1,
			1,1,1,1,1,1,1,1,1,1,
			1,1,1,1,1,1,1,1}; 
	public float[][] generateNextContinuosTrainingSetANN(float[][] matrix, int rows,int degreeOfFreedom,int edge,long delta,int inputColumns,
			int outputColumns ){	
		int intputIdx = 0;
		int outputIdx = 1;
		int offset = 0;
		float[] inputMatrix = new float[rows*inputColumns];
		float[] outputMatrix = new float[rows*outputColumns];
		for(int i = 0;i < rows;i++){
			increase(inputArray,delta,degreeOfFreedom,offset);
			for(int j = 0; j<inputColumns; j ++){
				inputMatrix[i*inputColumns+j] = inputArray[j];
			}
			int[] targetArr = getTargetArray(inputMatrix,i*inputColumns,inputColumns-offset,edge);
			for(int j = 0; j<outputColumns;j++){
				outputMatrix[i*outputColumns+j] = targetArr[j];				
			}
		}
		for(int i = 0;i<inputMatrix.length;i++){
			matrix[intputIdx][i] = inputMatrix[i]; 
		}
		for(int i = 0;i<outputMatrix.length;i++){
			matrix[outputIdx][i] = outputMatrix[i]; 
		}
		return matrix;
	}

	private void increase(float[] inputArray2,long delta,int degreeOfFreedom, int offset) {
		long sum = 0;
		for(int i = inputArray2.length-1-offset;i>=0;i--){
			sum = (long) (delta +inputArray2[i]);
			if(sum > degreeOfFreedom){
				inputArray2[i] = 1;	
				if(i == 0){
					for(int j=0; j<inputArray2.length;j++){
						inputArray2[j] = 1;
					}
					return;
				}
			}else{
				inputArray2[i] = sum;
				return ;
			}
		}
	}

	public void startAt(float[] featureVector1) {
		inputArray = featureVector1;		
	}

	public float[] getCurrentPosition() {
		return inputArray;
	}

	/**
	 * Produces target vector from feature vector wich consists of angles
	 * @param featureVector
	 * @param edge
	 * @param colsOutput
	 * @return
	 */
	public static float[] getTargetVector(float[] featureVector,int edge,int colsOutput) {
		float[] expectedPoseData = Arrays.copyOfRange(featureVector, 0, (featureVector.length/2)-1);
		float[] observedPoseData = Arrays.copyOfRange(featureVector, (featureVector.length)/2, featureVector.length-1);
		int totalPercent = getTargetValue(expectedPoseData, observedPoseData, edge);
		float[] result = new float[colsOutput];
		int[] arr = getAnnTargVector((int)totalPercent);
		for(int i = 0; i< colsOutput;i++){
			result[i] = arr[i];
		}
		return result;
	}

	/**
	 * Returns 2 dimensional array, where [0][] is matrix for inputs, row length is 38, [1][] is array of classes. 
	 * Classes counted ascending 1 - 0-40, 2 -40-50
	 * @param matrix2 
	 * @param rows
	 * @param degreeOfFreedom
	 * @return
	 */
	public static float[][] generateNextRandomTrainingsetTree(float[][] matrix, int rows,int degreeOfFreedom,int edge) {
		int inputCols = 38;

		Random rm = new Random();
		for(int i = 0;i< rows;i++){
			for(int j = 0;j<inputCols;j++){
				matrix[0][i*inputCols+j] = rm.nextInt(degreeOfFreedom);
			}
			matrix[1][i] = getTargetClass(Arrays.copyOfRange(matrix[0], i*inputCols,i*inputCols+inputCols-1),inputCols,edge);
		}
		return matrix;
	}

	/**
	 * Returns 2 dimensional array, where [0][] is matrix for inputs, row length is 38, [1][] is array of classes. 
	 * Classes counted ascending 1 - 0-40, 2 -40-50
	 * @param matrix2 
	 * @param rows
	 * @param degreeOfFreedom
	 * @return
	 */
	public static float[][] generateNextRandomTrainingsetTreeEven(float[][] matrix, int rows,int degreeOfFreedom,int edge) {
		int inputCols = 38;
		Random rm = new Random();
		int[] classCounter = new int[]{0,0,0,0,0};
		int classIdx = -1;
		int size = rows/5;
		int increment = 0;
		int base = 0;
		for(int i = 0;i< rows;i++){
			increment = 0;
			base = rm.nextInt(degreeOfFreedom);
			/*		do{*/

			for(int j = 0;j<inputCols;j++){
				matrix[0][i*inputCols+j] =base+ rm.nextInt(degreeOfFreedom-base - increment);
			}
			matrix[1][i] = getTargetClass(Arrays.copyOfRange(matrix[0], i*inputCols,i*inputCols+inputCols-1),inputCols,edge);
			classIdx = (int) matrix[1][i];
			/*				increment++;
			}
			while(!vectorAccepted(classCounter,classIdx,size,rows));*/
		}
		return matrix;
	}

	private static boolean vectorAccepted(int[] classCounter,int classIdx,int size,int rows) {
		if(classIdx <0){
			return true;
		}
		if(classCounter[classIdx-1]<=size){
			classCounter[classIdx-1] ++;
			return true;
		}
		return false;
	}

	private static float getTargetClass(float[] featureVector,int rowLength,int edge) {
		float[] expectedPoseData = Arrays.copyOfRange(featureVector, 0, (rowLength)/2 );
		float[] observedPoseData = Arrays.copyOfRange(featureVector, (rowLength)/2,rowLength);
		int targetVal =(int)getTargetValue(expectedPoseData, observedPoseData, edge);
		return getLabelAsInt(targetVal);
	}

	/**
	 * Maps total percentage to a label
	 * @param totalPercentage - 
	 * @return
	 */
	private static int getLabelAsInt(int totalPercentage) {
		if(totalPercentage<=40){
			return	1;
		}
		if(totalPercentage<=50){
			return	2;
		}
		if(totalPercentage<=60){
			return	3;
		}
		if(totalPercentage<=80){
			return	4;
		}
		if(totalPercentage<=100){
			return	5;
		}
		return 0;
	}

	public void generateNextContinuosTrainingsetTree(float[][] matrix, int rows, int degreeOfFreedom, int edge,long delta,int classCount,boolean equallyDistributed) {
		int inputColumns = 38;
		int outputColumns = 1;
		int inputIdx = 0;
		int outputIdx = 1;
		int offset=0;
		int attempts = 0;
		int maxClassOccurance = rows/classCount;
		int[] classOccuranceCounter = new int[]{0,0,0,0,0};
		float[] inputMatrix = new float[rows*inputColumns];
		int targetValue = 0;
		boolean vectorAccepted = false;
		for(int i = 0;i < rows;i++){
			while(!vectorAccepted && equallyDistributed){

				increase(inputArray,delta,degreeOfFreedom,offset);
				offset =0;

				targetValue = getTargetValue(Arrays.copyOfRange(inputArray, 0, (inputArray.length/2))
						,Arrays.copyOfRange(inputArray, (inputArray.length/2),inputArray.length), edge);
				targetValue = (int)getLabelAsInt(targetValue);
				int binIdx = (int) (targetValue-1);
				if(classOccuranceCounter[binIdx]<maxClassOccurance){
					classOccuranceCounter[binIdx]++;
					vectorAccepted = true;
				}else{
					vectorAccepted = false;
					if(attempts >100){
						if(offset<14){
							offset = new Random().nextInt(38);
						}
						attempts = 0;
					}
					attempts++;
				}
			}
			offset = 0;
			attempts = 0;
			vectorAccepted = false;
			for(int j = 0; j<inputColumns; j ++){
				inputMatrix[i*inputColumns+j] = inputArray[j];
			}
			matrix[outputIdx][i] = targetValue;
		}
		for(int i = 0;i<inputMatrix.length;i++){
			matrix[inputIdx][i] = inputMatrix[i]; 
		}
	}
	private int position = 0;
	public void generateNextContinuosTrainingSetANN1(float[][] matrix, int rows, int degreeOfFreedom, int edge,
			long delta,	int inputColumns,	int outputColumns ) {

		int intputIdx = 0;
		int outputIdx = 1;
		int offset = 0;
		float[] inputMatrix = new float[rows*inputColumns];
		float[] outputMatrix = new float[rows*outputColumns];
		for(int i = 0;i < rows;i++){
			for(int j = 0 ; j < inputArray.length;j++){
				inputArray[j] = 1;
			}
			inputArray[position] = edge;
			position++;
			if(position >= inputColumns){
				position = 0;
			}
			for(int j = 0; j<inputColumns; j ++){
				inputMatrix[i*inputColumns+j] = inputArray[j];
			}
			int[] targetArr = getTargetArray(inputMatrix,i*inputColumns,inputColumns,edge);
			for(int j = 0; j<outputColumns;j++){
				outputMatrix[i*outputColumns+j] = targetArr[j];				
			}
		}
		for(int i = 0;i<inputMatrix.length;i++){
			matrix[intputIdx][i] = inputMatrix[i]; 
		}
		for(int i = 0;i<outputMatrix.length;i++){
			matrix[outputIdx][i] = outputMatrix[i]; 
		}	
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

	public void export(Posture posture){	
		try {
			/* Writing down head */
			int head[] = CameraModel.convertToKinectCoordinates(posture,posture.getHead());
			angleWriter.append(head[0]+",");angleWriter.append(head[1]+",");angleWriter.append(head[2]+",");
			/* Writing down Shoulder center */
			int sc[] = CameraModel.convertToKinectCoordinates(posture,posture.getShoulder_center());
			angleWriter.append(sc[0]+",");angleWriter.append(sc[1]+",");angleWriter.append(sc[2]+",");
			/* Writing down right shoulder */
			int sr[] = CameraModel.convertToKinectCoordinates(posture,posture.getShoulder_right());
			angleWriter.append(sr[0]+",");angleWriter.append(sr[1]+",");angleWriter.append(sr[2]+",");
			/* Writing down left shoulder */
			int sl[] = CameraModel.convertToKinectCoordinates(posture,posture.getShoulder_left());
			angleWriter.append(sl[0]+",");angleWriter.append(sl[1]+",");angleWriter.append(sl[2]+",");
			/* Writing down spine */
			int s[] = CameraModel.convertToKinectCoordinates(posture,posture.getSpine());
			angleWriter.append(s[0]+",");angleWriter.append(s[1]+",");angleWriter.append(s[2]+",");
			/* Writing down left elbow*/
			int el[] = CameraModel.convertToKinectCoordinates(posture,posture.getElbow_left());
			angleWriter.append(el[0]+",");angleWriter.append(el[1]+",");angleWriter.append(el[2]+",");
			/* Writing down elbow right*/
			int er[] = CameraModel.convertToKinectCoordinates(posture,posture.getElbow_right());
			angleWriter.append(er[0]+",");angleWriter.append(er[1]+",");angleWriter.append(er[2]+",");
			/* Writing down left wrist */
			int wl[] = CameraModel.convertToKinectCoordinates(posture,posture.getWrist_left());
			angleWriter.append(wl[0]+",");angleWriter.append(wl[1]+",");angleWriter.append(wl[2]+",");
			/* Writing down right wrist */
			int wr[] = CameraModel.convertToKinectCoordinates(posture,posture.getWrist_right());
			angleWriter.append(wr[0]+",");angleWriter.append(wr[1]+",");angleWriter.append(wr[2]+",");
			/* Writing down left hip */
			int hl[] = CameraModel.convertToKinectCoordinates(posture,posture.getHip_left());
			angleWriter.append(hl[0]+",");angleWriter.append(hl[1]+",");angleWriter.append(hl[2]+",");
			/* Writing down right hip */
			int hr[] = CameraModel.convertToKinectCoordinates(posture,posture.getHip_right());
			angleWriter.append(hr[0]+",");angleWriter.append(hr[1]+",");angleWriter.append(hr[2]+",");
			/* Writing down head */
			int kl[] = CameraModel.convertToKinectCoordinates(posture,posture.getKnee_left());
			angleWriter.append(kl[0]+",");angleWriter.append(kl[1]+",");angleWriter.append(kl[2]+",");
			/* Writing down head */
			int kr[] = CameraModel.convertToKinectCoordinates(posture,posture.getKnee_right());
			angleWriter.append(kr[0]+",");angleWriter.append(kr[1]+",");angleWriter.append(kr[2]+",");
			/* Writing down head */
			int al[] = CameraModel.convertToKinectCoordinates(posture,posture.getAnkle_left());
			angleWriter.append(al[0]+",");angleWriter.append(al[1]+",");angleWriter.append(al[2]+",");
			/* Writing down head */
			int ar[] = CameraModel.convertToKinectCoordinates(posture,posture.getAnkle_right());
			angleWriter.append(ar[0]+",");angleWriter.append(ar[1]+",");angleWriter.append(ar[2]+",");
			/* Writing down head */
			int fl[] = CameraModel.convertToKinectCoordinates(posture,posture.getFoot_left());
			angleWriter.append(fl[0]+",");angleWriter.append(fl[1]+",");angleWriter.append(fl[2]+",");
			/* Writing down head */
			int fr[] = CameraModel.convertToKinectCoordinates(posture,posture.getFoot_right());
			angleWriter.append(fr[0]+",");angleWriter.append(fr[1]+",");angleWriter.append(fr[2]+"");
			angleWriter.append("\n");
		} catch (IOException e) {
			System.err.println("Failed to export pose");
		}

		/*try {
			for(int i:posture.getTargetArray()){
				AnnTarVecWriter.append(i+",");
			}
			AnnTarVecWriter.append("\n");
		} catch (IOException e) {
			e.printStackTrace();
		}*/
		/*	try {
			NumberFormat nf = NumberFormat.getIntegerInstance();

			IBodyModel b = new ImplBodyModel();
			outT1.append(nf.format(Math.toDegrees(b.getA1Angle(posture)))+",");
			outT1.append(nf.format(Math.toDegrees(b.getA2Angle(posture)))+",");
			outT1.append(nf.format(Math.toDegrees(b.getA3Angle(posture)))+",");
			outT1.append(nf.format(Math.toDegrees(b.getA4Angle(posture)))+",");
			outT1.append(nf.format(Math.toDegrees(b.getA5Angle(posture)))+",");
			outT1.append(nf.format(Math.toDegrees(b.getA6Angle(posture)))+",");
			outT1.append(nf.format(Math.toDegrees(b.getA7Angle(posture)))+",");
			outT1.append(nf.format(Math.toDegrees(b.getA8Angle(posture)))+",");
			outT1.append(nf.format(Math.toDegrees(b.getA9Angle(posture)))+",");
			outT1.append(nf.format(Math.toDegrees(b.getA10Angle(posture)))+",");
			outT1.append(nf.format(Math.toDegrees(b.getA11Angle(posture)))+",");
			outT1.append(nf.format(Math.toDegrees(b.getA12Angle(posture)))+",");
			outT1.append(nf.format(Math.toDegrees(b.getA13Angle(posture)))+",");
			outT1.append(nf.format(Math.toDegrees(b.getA14Angle(posture)))+",");
			outT1.append(nf.format(Math.toDegrees(b.getA15Angle(posture)))+",");
			outT1.append(nf.format(Math.toDegrees(b.getA16Angle(posture)))+",");
			outT1.append(nf.format(Math.toDegrees(b.getA17Angle(posture)))+",");
			outT1.append(nf.format(Math.toDegrees(b.getA18Angle(posture)))+",");
			outT1.append(nf.format(Math.toDegrees(b.getA19Angle(posture)))+",");
			outT1.append("\n"); 
		} catch (IOException e) {
			e.printStackTrace();
		}*/
	}

	private static int[] getTargetArray(float[] inputMatrix, int offset,int rowLength,int outerlimit,int innerlimit) {
		float[] expectedPoseData = Arrays.copyOfRange(inputMatrix, offset, offset+(rowLength)/2);
		float[] observedPoseData = Arrays.copyOfRange(inputMatrix, offset, offset+rowLength);
		return getAnnTargVector((int)getTargetValue(expectedPoseData, observedPoseData, outerlimit, innerlimit));
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

	/**
	 * Closes the output stream
	 */
	public void closeStream(){
		try {
			angleWriter.close();
			AnnTarVecWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Failed to close"); 
			return;
		}
		System.out.println("Data was successfull outputed to:"+angleWriter+","+AnnTarVecWriter);
	}

	/**
	 * Print joint coordinate from given posture as perceived from kinect camera.
	 *  The printing
	 * @param posture
	 * @throws IOException - throws exception if writer is not initialized, specify output files
	 */
	public void exportPostureAsKinect(Posture posture,FileWriter writer) throws IOException{	
		/* Writing down head */
		int head[] = CameraModel.convertToKinectCoordinates(posture,posture.getHead());
		writer.append(head[0]+",");writer.append(head[1]+",");writer.append(head[2]+",");
		/* Writing down Shoulder center */
		int sc[] = CameraModel.convertToKinectCoordinates(posture,posture.getShoulder_center());
		writer.append(sc[0]+",");writer.append(sc[1]+",");writer.append(sc[2]+",");
		/* Writing down right shoulder */
		int sr[] = CameraModel.convertToKinectCoordinates(posture,posture.getShoulder_right());
		writer.append(sr[0]+",");writer.append(sr[1]+",");writer.append(sr[2]+",");
		/* Writing down left shoulder */
		int sl[] = CameraModel.convertToKinectCoordinates(posture,posture.getShoulder_left());
		writer.append(sl[0]+",");writer.append(sl[1]+",");writer.append(sl[2]+",");
		/* Writing down spine */
		int s[] = CameraModel.convertToKinectCoordinates(posture,posture.getSpine());
		writer.append(s[0]+",");writer.append(s[1]+",");writer.append(s[2]+",");
		/* Writing down left elbow*/
		int el[] = CameraModel.convertToKinectCoordinates(posture,posture.getElbow_left());
		writer.append(el[0]+",");writer.append(el[1]+",");writer.append(el[2]+",");
		/* Writing down elbow right*/
		int er[] = CameraModel.convertToKinectCoordinates(posture,posture.getElbow_right());
		writer.append(er[0]+",");writer.append(er[1]+",");writer.append(er[2]+",");
		/* Writing down left wrist */
		int wl[] = CameraModel.convertToKinectCoordinates(posture,posture.getWrist_left());
		writer.append(wl[0]+",");writer.append(wl[1]+",");writer.append(wl[2]+",");
		/* Writing down right wrist */
		int wr[] = CameraModel.convertToKinectCoordinates(posture,posture.getWrist_right());
		writer.append(wr[0]+",");writer.append(wr[1]+",");writer.append(wr[2]+",");
		/* Writing down left hip */
		int hl[] = CameraModel.convertToKinectCoordinates(posture,posture.getHip_left());
		writer.append(hl[0]+",");writer.append(hl[1]+",");writer.append(hl[2]+",");
		/* Writing down right hip */
		int hr[] = CameraModel.convertToKinectCoordinates(posture,posture.getHip_right());
		writer.append(hr[0]+",");writer.append(hr[1]+",");writer.append(hr[2]+",");
		/* Writing down head */
		int kl[] = CameraModel.convertToKinectCoordinates(posture,posture.getKnee_left());
		writer.append(kl[0]+",");writer.append(kl[1]+",");writer.append(kl[2]+",");
		/* Writing down head */
		int kr[] = CameraModel.convertToKinectCoordinates(posture,posture.getKnee_right());
		writer.append(kr[0]+",");writer.append(kr[1]+",");writer.append(kr[2]+",");
		/* Writing down head */
		int al[] = CameraModel.convertToKinectCoordinates(posture,posture.getAnkle_left());
		writer.append(al[0]+",");writer.append(al[1]+",");writer.append(al[2]+",");
		/* Writing down head */
		int ar[] = CameraModel.convertToKinectCoordinates(posture,posture.getAnkle_right());
		writer.append(ar[0]+",");writer.append(ar[1]+",");writer.append(ar[2]+",");
		/* Writing down head */
		int fl[] = CameraModel.convertToKinectCoordinates(posture,posture.getFoot_left());
		writer.append(fl[0]+",");writer.append(fl[1]+",");writer.append(fl[2]+",");
		/* Writing down head */
		int fr[] = CameraModel.convertToKinectCoordinates(posture,posture.getFoot_right());
		writer.append(fr[0]+",");writer.append(fr[1]+",");writer.append(fr[2]+"");
		writer.append("\n");
	}

	public void exportTagetVector(Posture posture,FileWriter writer){
		/*try{
			for(int i:posture.getTargetArray()){
				writer.append(i+",");
			}
			writer.append("\n");

		}catch(IOException ex){
			ex.printStackTrace();
		}*/
	}

	public void exportAngles(Posture posture, FileWriter writer){

		NumberFormat nf = NumberFormat.getIntegerInstance();

		IBodyModel b = new BodyModelImpl();
		try{
			writer.append(nf.format(Math.toDegrees(b.getA1Angle(posture)))+",");
			writer.append(nf.format(Math.toDegrees(b.getA2Angle(posture)))+",");
			writer.append(nf.format(Math.toDegrees(b.getA3Angle(posture)))+",");
			writer.append(nf.format(Math.toDegrees(b.getA4Angle(posture)))+",");
			writer.append(nf.format(Math.toDegrees(b.getA5Angle(posture)))+",");
			writer.append(nf.format(Math.toDegrees(b.getA6Angle(posture)))+",");
			writer.append(nf.format(Math.toDegrees(b.getA7Angle(posture)))+",");
			writer.append(nf.format(Math.toDegrees(b.getA8Angle(posture)))+",");
			writer.append(nf.format(Math.toDegrees(b.getA9Angle(posture)))+",");
			writer.append(nf.format(Math.toDegrees(b.getA10Angle(posture)))+",");
			writer.append(nf.format(Math.toDegrees(b.getA11Angle(posture)))+",");
			writer.append(nf.format(Math.toDegrees(b.getA12Angle(posture)))+",");
			writer.append(nf.format(Math.toDegrees(b.getA13Angle(posture)))+",");
			writer.append(nf.format(Math.toDegrees(b.getA14Angle(posture)))+",");
			writer.append(nf.format(Math.toDegrees(b.getA15Angle(posture)))+",");
			writer.append(nf.format(Math.toDegrees(b.getA16Angle(posture)))+",");
			writer.append(nf.format(Math.toDegrees(b.getA17Angle(posture)))+",");
			writer.append(nf.format(Math.toDegrees(b.getA18Angle(posture)))+",");
			writer.append(nf.format(Math.toDegrees(b.getA19Angle(posture)))+",");
			writer.append("\n");
		}catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void exportAnglesDeviations(Posture posture,Posture posture2, FileWriter writer){

		NumberFormat nf = NumberFormat.getIntegerInstance();

		IBodyModel b = new BodyModelImpl();
		try{
			writer.append(nf.format(Math.toDegrees(b.getA1Angle(posture)))+",");
			writer.append(nf.format(Math.toDegrees(b.getA2Angle(posture)))+",");
			writer.append(nf.format(Math.toDegrees(b.getA3Angle(posture)))+",");
			writer.append(nf.format(Math.toDegrees(b.getA4Angle(posture)))+",");
			writer.append(nf.format(Math.toDegrees(b.getA5Angle(posture)))+",");
			writer.append(nf.format(Math.toDegrees(b.getA6Angle(posture)))+",");
			writer.append(nf.format(Math.toDegrees(b.getA7Angle(posture)))+",");
			writer.append(nf.format(Math.toDegrees(b.getA8Angle(posture)))+",");
			writer.append(nf.format(Math.toDegrees(b.getA9Angle(posture)))+",");
			writer.append(nf.format(Math.toDegrees(b.getA10Angle(posture)))+",");
			writer.append(nf.format(Math.toDegrees(b.getA11Angle(posture)))+",");
			writer.append(nf.format(Math.toDegrees(b.getA12Angle(posture)))+",");
			writer.append(nf.format(Math.toDegrees(b.getA13Angle(posture)))+",");
			writer.append(nf.format(Math.toDegrees(b.getA14Angle(posture)))+",");
			writer.append(nf.format(Math.toDegrees(b.getA15Angle(posture)))+",");
			writer.append(nf.format(Math.toDegrees(b.getA16Angle(posture)))+",");
			writer.append(nf.format(Math.toDegrees(b.getA17Angle(posture)))+",");
			writer.append(nf.format(Math.toDegrees(b.getA18Angle(posture)))+",");
			writer.append(nf.format(Math.toDegrees(b.getA19Angle(posture)))+",");
			writer.append("\n");
		}catch (IOException e) {
			e.printStackTrace();
		}
	}


	public SetGenerator(String input,String target){
		exp = new TrainingSetGenerator(input,target);
		scene = new SceneModel();
	}
	public SetGenerator() {	}

	public void generateSet(Posture p){
		translateVariation(p, new double[]{0,0,50}, 20);
		rotateVariation(p, 5, 0, 90);
		rotateVariation(p, 5, -90, 0);
		translateVariation(p, new double[]{0,0,-50}, 40);
	}

	public void translateVariation(Posture p,double[] vector,int times){
		Posture pose = p.copy();
		for(int i=0; i< times;i++){
			scene.movePosePositionOnScene3d(vector);
			//scene.applyPositionOnScene(pose);
			//exp.export(pose);
			rotateVariation(p, 5, 0, 90);
			rotateVariation(p, 5, -90, 0);
		}
	}

	/**
	 * Exports final values directly to input.csv file
	 * @param p - pose
	 * @param step - step to divide
	 * @param start - must be less than end, in degrees
	 * @param end - must be larger than start, in degrees
	 */
	public void rotateVariation(Posture p,int step,int start,int end){
		Posture pose = p.copy();
		Map<String,double[]> map = p.getJointMap(); 
		for(int i=start; i< end;i+=step){
			Set<String>keys = map.keySet();
			for(String key:keys){
				pose.setJoinLocation(key,rotateAroundY(i,map.get(key)));
			}
			//scene.applyPositionOnScene(pose);
			//exp.export(pose);
		}
	}

	public void resizePose(Posture p,double scale){
		Posture pose = new Posture();
		BodyModelImpl bm = new BodyModelImpl();
		pose = bm.resizePose(p,scale);	
		//scene.applyPositionOnScene(pose);
		//exp.export(pose);
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
		return pointR;
	}

	/**
	 * Closing the export stream, if missed to call completeProcedure(), nothing is written to file
	 */
	public void completeProcedure(){
		exp.closeStream();
	}
	
	/**
	 * Sets filename to which input vectors of percentages describing the deviations
	 * is printed
	 * @param filename
	 */
	public void setInputDestFile(String filename) {
		inputDest = filename;
	}

	public void setTargAnnFileOut(String text) {
		tarAnnOutFile = text;

	}
	public void setTargKnnFileOut(String text) {
		tarKnnOutFile = text;

	}
	public void setTargRTrees(String text) {
		tarRTrees = text;		
	}
	public void setParams(Map<String, Object> params) throws NumberFormatException{
		sparcity = Integer.parseInt(params.get(TAG.SPARCITY.name())+"");	
		size = Integer.parseInt(params.get(TAG.ROWS.name())+"");	
	}

	private int attributeCount = 19;
	private int[] percentages = new int[attributeCount];
	private int[] deviations = new int[attributeCount];
	private int[] angles;
	private FileWriter deviationWriter;
	private FileWriter anglesWriter;

	/**
	 * Method that is printing training sets
	 */
	public void printTrainingSetsMethod1() {
		generatorMethod1();
	}
	
	/**
	 * Generates and prints specified number of feature vectors with equal portion of rows
	 *  for each class. Each scalar value in a vector represents match in percents.
	 * The unique difference compared to method1 is how target generated. The target is
	 * id which represents the highest mismatch
	 */
	public void generatorMethod2(){
		/*Initiate output writers for angle vectors , deviation vectors, percentage vectors,
		target vector for KNN,ANN, RTrees*/
		try {
			anglesWriter = new FileWriter(inputAngleDest);
			deviationWriter = new FileWriter(inputDevDest);
			percentageWriter = new FileWriter(inputDest);
			KNNTarWriter = new FileWriter(tarKnnOutFile);
			AnnTarVecWriter = new FileWriter(tarAnnOutFile);
			RTreeTarWriter = new FileWriter(tarRTrees);
		} catch (IOException e) {
			e.printStackTrace();
		}

		int[] annTarget = {0,0,0,0,0};
		int[] kNNLabel = new int[1];
		int[] rTreesLabel = new int[1];
		// Number of classes
		int classCount = 5;
		//Number of folds
		int folds = 3;
		//Size of one fold
		int delta = size/(folds*classCount);
		//bound limiting of generation of entries belonging to current class
		int bound = delta;
		//Current class category to which entities adhere 
		int desiredClass = 1;
		//Maximal permitted value in the bin
		int base = 100; /* 0-100 range of percentual value */

		float sensitivity = 2;
		for(int i = 0; i < size;i++){
			if(bound<i){
					desiredClass = ((desiredClass+1)%classCount)+1;
					bound+=delta;			
			}
			
			//Generate percentage vector
			percentages  = generateRandomPercentagesVectorMethod1(percentages.length, desiredClass,base);
			//Generate deviation vector from percentages
			deviations = generateDeviations(sensitivity,percentages);
			//Generate angle vector from deviations
			angles = generateAngles(deviations);
			
			kNNLabel[0] = getLabelAsInt(getLowestPercentage(percentages));
			rTreesLabel[0] = getLabelAsInt(getTotalPercentage(percentages));
			annTarget = getAnnTargVector(getTotalPercentage(percentages));
			
			//Print all the generated input and target arrays
			try {
				printArray(angles, anglesWriter);
				printArray(deviations, deviationWriter);
				printArray(percentages, percentageWriter);
				printArray(kNNLabel,KNNTarWriter );
				printArray(annTarget,AnnTarVecWriter );
				printArray(rTreesLabel,RTreeTarWriter );
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			anglesWriter.close();
			deviationWriter.close();
			percentageWriter.close();
			KNNTarWriter.close();
			AnnTarVecWriter.close();
			RTreeTarWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public int getLowestPercentage(int[] percentages) {
		int lowest = percentages[0];
		for(int p:percentages){
			if(lowest > p){
				lowest = p;
			}
		}
		return lowest;
	}

	/**
	 * Generates and prints specified number of feature vectors with equal portion of rows
	 *  for each class. Each scalar value in a vector represents match in percents.
	 * 
	 */
	public void generatorMethod1(){
		/*Initiate output writers for angle vectors , deviation vectors, percentage vectors,
		target vector for KNN,ANN, RTrees*/
		try {
			anglesWriter = new FileWriter(inputAngleDest);
			deviationWriter = new FileWriter(inputDevDest);
			percentageWriter = new FileWriter(inputDest);
			KNNTarWriter = new FileWriter(tarKnnOutFile);
			AnnTarVecWriter = new FileWriter(tarAnnOutFile);
			RTreeTarWriter = new FileWriter(tarRTrees);
		} catch (IOException e) {
			e.printStackTrace();
		}

		int[] annTarget = {0,0,0,0,0};
		int[] kNNLabel = new int[1];
		int[] rTreesLabel = new int[1];
		// Number of classes
		int classCount = 5;
		//Number of folds
		int folds = 3;
		//Size of one fold
		int delta = size/(folds*classCount);
		//bound limiting of generation of entries belonging to current class
		int bound = delta;
		//Current class category to which entities adhere 
		int desiredClass = 1;
		//Maximal permitted value in the bin
		int base = 100; /* 0-100 range of percentual value */

		float sensitivity = 2;
		for(int i = 0; i < size;i++){
			if(bound<i){
					desiredClass = ((desiredClass+1)%classCount)+1;
					bound+=delta;			
			}
			
			//Generate percentage vector
			percentages  = generateRandomPercentagesVectorMethod1(percentages.length, desiredClass,base);
			//Generate deviation vector from percentages
			deviations = generateDeviations(sensitivity,percentages);
			//Generate angle vector from deviations
			angles = generateAngles(deviations);
			
			kNNLabel[0] = getLabelAsInt(getTotalPercentage(percentages));
			rTreesLabel[0] = getLabelAsInt(getTotalPercentage(percentages));
			annTarget = getAnnTargVector(getTotalPercentage(percentages));
			
			//Print all the generated input and target arrays
			try {
				printArray(angles, anglesWriter);
				printArray(deviations, deviationWriter);
				printArray(percentages, percentageWriter);
				printArray(kNNLabel,KNNTarWriter );
				printArray(annTarget,AnnTarVecWriter );
				printArray(rTreesLabel,RTreeTarWriter );
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			anglesWriter.close();
			deviationWriter.close();
			percentageWriter.close();
			KNNTarWriter.close();
			AnnTarVecWriter.close();
			RTreeTarWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Creates deviations from percentages
	 * @param percents
	 * @return
	 */
	private int[] generateDeviations(float sensitivity,int[] percents) {
		BodyModelImpl bm = new BodyModelImpl();
		int[] deviations = new int[percents.length];
		List<Integer> outerLimit = bm.getUpperLimit();
		for(int i = 0; i<percents.length;i++){
			deviations[i] =(int)(outerLimit.get(i)/sensitivity*(100-percents[i])/100.0);
		}
		return deviations;
	}
	
	/**
	 * Create array of angles with length as double as long, then of array containing
	 * deviations provided
	 * In the array idx = [ 0;19) represents observed angles, idx =  [ 19;38) represents 
	 * expected angles
	 * @param deviation - generated deviations
	 * @return
	 */
	private int[] generateAngles(int[] deviation) {
		/* Get body model instance*/
		BodyModelImpl bm = new BodyModelImpl();
		/*Get joint angle upper limits */ 
		List<Integer> list = bm.getUpperLimit();
		int[] angles = new int[deviation.length*2];
		Random rm = new Random();
	
		for(int i = 0; i<angles.length/2;i++){
			angles[i] = rm.nextInt(list.get(i));
			/* If the sum of angle and deviation is above upper limit for joint
			 * the expected angle set by subtracting the deviation from observed angle
			 * else set expected as with sum of observed angle and deviation
			 */
			if(angles[i]+deviation[i] <= list.get(i)){
				angles[i+19] = angles[i]+deviation[i];
			}else{
				angles[i+19] = angles[i]-deviation[i];
			}
				
		}
		return angles;
	}

	private static int getTotalPercentage(int[] percentages) {
		double sum = 0;
		for(int v: percentages){
			sum+=v;
		}
		double result = sum/(percentages.length*100);
		return (int)(result *100);
	}

	/**
	 * 
	 * @param summand1 vector to which second summand is added
	 * @param summand2 vector which is added to summand1
	 * @param base - maximal values of 
	 */
	private void sum(int[] summand1,int[] summand2,int base){
		int index1 = summand1.length-1;
		int temp = 0;
		int extra = 0;
		for(int i = summand2.length-1; i > -1; i--){
			temp = summand1[index1]+summand2[i]+extra;
			extra = 0;
			if(temp > base){
				summand1[index1] = temp%base;
				extra = 1;
			}else{
				summand1[index1] = temp;
			}						
		}
	}

	private int[] generateNextRandomPercentVectorMethod1(int vectorLength,int desiredClass){
		int base = 100;
		int[] vectorV = new int[vectorLength];
		while(desiredClass != getLabelAsInt(getTotalPercentage(generateVector(vectorV,base)))){};	
		return vectorV;
	}

	private int[] generateVector(int[] vector,int bound) {
		Random rm = new Random();
		for(int i = 0; i < vector.length;i ++){
			vector[i] = rm.nextInt(bound+1);
		}
		return vector;
	}

	/**
	 * Produces a vector with randomly selected values in range specified by base
	 * @param vectorLength - length of vector
	 * @param desiredClass - constraints possible vector to belong to class
	 * @param base - specifies range, 0(included) - base(included)
	 * @return
	 */
	private int[] generateRandomPercentagesVectorMethod1(int vectorLength,int desiredClass,int base){
		//Lowest and higher bound for a desired class
		int[] magnitudBounds = getLowerUpperMagnitudeBounds(vectorLength,desiredClass,base);
		int[] percentages = new int[vectorLength];
		//Generating 
		generateConstrainedRandomPercentageVectorVector(percentages,magnitudBounds,desiredClass,base);				
		return percentages;
	}

	/**
	 * Sets values of passed array(percentages)
	 * @param percentages - passed array
	 * @param bounds - lower and upper bounds
	 * @param desiredClass - class specification for vector
	 */
	private void generateConstrainedRandomPercentageVectorVector(int[] percentages, int[] bounds,int desiredClass,int base) {
		int pool = bounds[1];	
		int depth = 100;//getDepth(desiredClass);
		int difference = bounds[1] - bounds[0];
		int next = 0;
		Random rm = new Random();
		for(int i = 0; i< percentages.length;i++){
			next = rm.nextInt(depth);
			if(pool - next >=0){
				percentages[i] = next;
			}else {
				break;
			}
			pool-=percentages[i];
		}
		int portion = 0;
		int tmp = 0;
		int randomStart = 0;
		while(pool > 0){
			portion = (int)((double)pool/percentages.length);
			pool = 0;
			randomStart = rm.nextInt(percentages.length+1);
			for(int i = randomStart; i < percentages.length+randomStart;i++){
				percentages[i%percentages.length]+=portion;
				if(percentages[i%percentages.length] >base){
					tmp = percentages[i%percentages.length]%base;
					pool+=tmp;
					percentages[i%percentages.length]-=tmp;
				}
			}
		}
	}

	private int getDepth(int desiredClass) {
		switch(desiredClass){
		case 1:
			return 40;
		case 2:
			return 50;
		case 3:
			return 60;
		case 4:
			return 80;
		case 5:
			return 100;
		default:
			return 0;
		}
	}

	/**
	 * Returns in order lower bound at index 0 and upper bound at index 1, of total sum of vector elements. 
	 * @param vectorLength - length of vector
	 * @param desiredClass - class of produced vector, affects decimal in function that 
	 * produces bound
	 * @param base - used as mutiplier, vectorLength*base*decimal  
	 * @return
	 */
	private int[] getLowerUpperMagnitudeBounds(int vectorLength,int desiredClass,int base) {
		switch(desiredClass){
		case 1:
			return new int[]{0,(int)(vectorLength*base*0.4)};
		case 2:
			return new int[]{(int)(vectorLength*base*0.4),(int)(vectorLength*base*0.5)};
		case 3:
			return new int[]{(int)(vectorLength*base*0.5),(int)(vectorLength*base*0.6)};
		case 4:
			return new int[]{(int)(vectorLength*base*0.6),(int)(vectorLength*base*0.80)};
		case 5:
			return new int[]{(int)(vectorLength*base*0.80),(int)(vectorLength*base*1)};
		default:
			return new int[]{0,0};
		}
	}

	public String getInputDevDest() {
		return inputDevDest;
	}

	/**
	 * Sets output file for vectors of angle deviation between expected and observed 
	 * posture
	 * @param inputDevDest
	 */
	public void setInputDevDest(String inputDevDest) {
		this.inputDevDest = inputDevDest;
	}

	/**
	 * Sets output file for vectors of angles from observed and expected posture
	 * @return
	 */
	public String getInputAngleDest() {
		return inputAngleDest;
	}

	public void setInputAngleDest(String inputAngleDest) {
		this.inputAngleDest = inputAngleDest;
	}
	
	public static void main(String[] args){
		SetGenerator setGenerator = new SetGenerator();
		if(args.length <8){
			System.out.println("Not enough provided arguments. \n"
					+ "arguments should be provided in following order. \n"
					+ "destination for vectors of deviations, \n"
					+ "destination for vectors of angles, \n"
					+ "destination for ann target vector, \n"
					+ "destination for knn target vector, \n"
					+ "destination for Random trees target, \n"
					+ "number of desired examples, \n"
					+ "euclidean distance between examples");
			System.exit(-1);			
		}
		setGenerator.setInputDevDest(args[0]);
		setGenerator.setInputAngleDest(args[1]);
		setGenerator.setInputDestFile(args[2]);
		setGenerator.setTargAnnFileOut(args[3]);
		setGenerator.setTargKnnFileOut(args[4]);
		setGenerator.setTargRTrees(args[5]);
		Map<String, Object> params = new TreeMap<>();
		params .put(TAG.ROWS.name(),args[6]);
		params.put(TAG.SPARCITY.name(),args[7] );
		setGenerator.setParams(params);
		setGenerator.printTrainingSetsMethod1();
	}
}

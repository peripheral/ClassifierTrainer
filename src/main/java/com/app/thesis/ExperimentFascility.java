package com.app.thesis;

import static org.bytedeco.javacpp.opencv_core.CV_32F;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import org.bytedeco.javacpp.FloatPointer;
import org.bytedeco.javacpp.Pointer;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_ml.ANN_MLP;

import com.app.classifiers.wrappers.AnnClassifierWrapper;
import com.app.classifiers.wrappers.AnnClassifierWrapper.CLASSIFIER_TYPE;
import com.app.classifiers.wrappers.IClassifierWrapper;
import com.app.classifiers.wrappers.KnnClassifierWrapper;
import com.app.classifiers.wrappers.RandomTreesClassifierWrapper;

public class ExperimentFascility {
	private String measurementOutput = "trainingStatsAnnOnly.csv";
	private String trainingDataPercents = "trainingDataP.dat";
	private String trainingDataAngles = "trainingDataA.dat";
	private String trainingDataDevs = "trainingDataD.dat";
	private String tarAnn = "trainingDataTarAnn.dat";
	private String tarKnn = "trainingDataTarKnn.dat";
	private String tarRTrees = "trainingDataTarRTrees.dat";
	private String trainedAnnAngles ="annTrainedA.ml";
	private String trainedAnnPercentages ="annTrainedP.ml";
	private String trainedAnnDevs ="annTrainedD.ml";
	private String trainedKnnAngles ="knnTrainedA.ml";
	private String trainedKnnPercentages ="knnTrainedP.ml";
	private String trainedKnnDevs ="knnTrainedD.ml";
	private String trainedRTreesAngles ="rTreesTrainedA.ml";
	private String trainedRTreesPercentages ="rTreesTrainedP.ml";
	private String trainedRTreesDevs ="rTreesTrainedD.ml";
	private String testDataA = "testDataA.dat";
	private String testDataD = "testDataD.dat";
	private String testDataP = "testDataP.dat";
	private String testTarAnn = "testTarAnn.dat";
	private String testTarKnn = "testTarKnn.dat";
	private String testTarRTrees = "testTarRTrees.dat";

	public static void main(String[] args){
		ExperimentFascility ef = new ExperimentFascility();
		ef.replace(".", "100000.");
	//	ef.generateTestSets(100000/10,100000);
		//ef.runExperiment();
		System.out.println("First part complete");
		ef.replace("100000.", "800000.");
	//	ef.generateTestSets(800000/20,800000);
	//	ef.runExperiment();
		System.out.println("Second part complete");
		ef.replace("800000.", "1000000.");
	//	ef.generateTestSets(1000000/30,1000000);
	//	ef.runExperiment();
		System.out.println("Third part complete");	
	}

	private void generateTestSets(int setSize,int totalRows) {
		int[] indices = generateSortedIndices(setSize,totalRows);	
		System.out.println("Indices generated .");
		copy(indices,trainingDataAngles,testDataA);
		copy(indices,trainingDataDevs,testDataD);
		copy(indices,trainingDataPercents,testDataP);
		System.out.println("Training data copy complete.");
		copy(indices,tarAnn,testTarAnn);
		copy(indices,tarKnn,testTarKnn);
		copy(indices,tarRTrees,testTarRTrees);
		System.out.println("Copy complete.");
	}

	private int[] generateSortedIndices(int setSize, int totalRows) {
		int[] indicies = new int[setSize];
		int tmp = totalRows;
		Random rm = new Random();
		for(int i = 0; i < setSize;i++){
			indicies[i] =  rm.nextInt(tmp--);
		}
		Arrays.sort(indicies);
		return indicies;
	}


	private void copy(int[] indices,String from,String to) {
		List<String> list = new LinkedList<>();
		Scanner fromSc = null;
		FileWriter toFw = null;
		try {
			fromSc = new Scanner(new File(from));
			toFw = new FileWriter(new File(to));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		loadFileIntoList(fromSc,list);

		for(int i = indices.length-1; i >= 0;i--){
			write(list.get(indices[i])+"\n",toFw);
			list.remove(indices[i]);
		}

		try {
			fromSc.close();
			toFw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void loadFileIntoList(Scanner fromSc, List<String> list) {
		while(fromSc.hasNext()){
			list.add(fromSc.nextLine());
		}

	}

	private void write(String value, FileWriter writer) {
		try {
			writer.write(value);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void runExperiment(){
		FileWriter fw = null;		
		try {
			fw = new FileWriter(measurementOutput);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		runExperimentAnn(fw);
		runExperimentKnn(fw);
		runExperimentRTrees(fw);
		
		

		try {
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void runExperimentRTrees(FileWriter fw) {
		long trainingTime = 0;
		RandomTreesClassifierWrapper rTreesWrap = new RandomTreesClassifierWrapper();
		rTreesWrap.setTrainingDataInputFile(trainingDataAngles);
		rTreesWrap.setTrainingDataTargetFile(tarRTrees);
		rTreesWrap.setTestDataInput(testDataA);
		rTreesWrap.setTestDataTarget(testTarRTrees);
		rTreesWrap.setRTreesOutputFile(trainedRTreesAngles);
		rTreesWrap.setInputVectorLength(38);
		rTreesWrap.initiate();
		trainingTime = measureTraining(rTreesWrap);
		writeStats(fw,trainingTime,rTreesWrap.getClassificationRatio(),CLASSIFIER_TYPE.RANDOMTREES.name(),"ex1");
		rTreesWrap = new RandomTreesClassifierWrapper();
		rTreesWrap.setTrainingDataInputFile(trainingDataDevs);
		rTreesWrap.setTrainingDataTargetFile(tarRTrees);
		rTreesWrap.setTestDataInput(testDataD);
		rTreesWrap.setTestDataTarget(testTarRTrees);
		rTreesWrap.setRTreesOutputFile(trainedRTreesDevs);
		rTreesWrap.setInputVectorLength(19);
		rTreesWrap.initiate();
		trainingTime = measureTraining(rTreesWrap);
		writeStats(fw,trainingTime,rTreesWrap.getClassificationRatio(),CLASSIFIER_TYPE.RANDOMTREES.name(),"ex2");
		rTreesWrap = new  RandomTreesClassifierWrapper();
		rTreesWrap.setTrainingDataInputFile(trainingDataPercents);
		rTreesWrap.setTrainingDataTargetFile(tarRTrees);
		rTreesWrap.setTestDataInput(testDataP);
		rTreesWrap.setTestDataTarget(testTarRTrees);
		rTreesWrap.setRTreesOutputFile(trainedRTreesPercentages);
		rTreesWrap.setInputVectorLength(19);
		rTreesWrap.initiate();
		trainingTime = measureTraining(rTreesWrap);
		writeStats(fw,trainingTime,rTreesWrap.getClassificationRatio(),CLASSIFIER_TYPE.RANDOMTREES.name(),"ex3");
		
	}

	private void runExperimentKnn(FileWriter fw) {
		long trainingTime = 0;
		KnnClassifierWrapper knnWrap = new KnnClassifierWrapper();
		knnWrap.setTrainingDataInputFile(trainingDataAngles);
		knnWrap.setTrainingDataTargetFile(tarKnn);
		knnWrap.setTestDataInput(testDataA);
		knnWrap.setTestDataTarget(testTarKnn);
		knnWrap.setImageOutDest(trainedKnnAngles);
		knnWrap.setInputLength(38);
		knnWrap.initiate();
		trainingTime = measureTraining(knnWrap);
		writeStats(fw,trainingTime,knnWrap.getClassificationRatio(),CLASSIFIER_TYPE.KNN.name(),"ex1");
		knnWrap = new KnnClassifierWrapper();
		knnWrap.setTrainingDataInputFile(trainingDataDevs);
		knnWrap.setTrainingDataTargetFile(tarKnn);
		knnWrap.setTestDataInput(testDataD);
		knnWrap.setTestDataTarget(testTarKnn);
		knnWrap.setImageOutDest(trainedKnnDevs);
		knnWrap.setInputLength(19);
		knnWrap.initiate();
		trainingTime = measureTraining(knnWrap);
		writeStats(fw,trainingTime,knnWrap.getClassificationRatio(),CLASSIFIER_TYPE.KNN.name(),"ex2");
		knnWrap = new KnnClassifierWrapper();
		knnWrap.setTrainingDataInputFile(trainingDataPercents);
		knnWrap.setTrainingDataTargetFile(tarKnn);
		knnWrap.setTestDataInput(testDataP);
		knnWrap.setTestDataTarget(testTarKnn);
		knnWrap.setImageOutDest(trainedKnnPercentages);
		knnWrap.setInputLength(19);
		knnWrap.initiate();
		trainingTime = measureTraining(knnWrap);
		writeStats(fw,trainingTime,knnWrap.getClassificationRatio(),CLASSIFIER_TYPE.KNN.name(),"ex3");
		
	}

	private void runExperimentAnn(FileWriter fw) {
		long trainingTime = 0;
		AnnClassifierWrapper annWrap = new AnnClassifierWrapper();
		annWrap.setTrainingDataInputFile(trainingDataAngles);
		annWrap.setTrainingDataTargetFile(tarAnn);
		annWrap.setTestDataInput(testDataA);
		annWrap.setTestDataTarget(testTarAnn);
		annWrap.setImageOutDest(trainedAnnAngles);
		annWrap.setInputLength(38);
		annWrap.initiate();
		trainingTime = measureTraining(annWrap);
		writeStats(fw,trainingTime,annWrap.getClassificationRatio(),CLASSIFIER_TYPE.ANN.name(),"ex1");
		annWrap = new AnnClassifierWrapper();
		annWrap.setTrainingDataInputFile(trainingDataDevs);
		annWrap.setTrainingDataTargetFile(tarAnn);
		annWrap.setTestDataInput(testDataD);
		annWrap.setTestDataTarget(testTarAnn);
		annWrap.setImageOutDest(trainedAnnDevs);	
		annWrap.setInputLength(19);
		annWrap.initiate();
		trainingTime = measureTraining(annWrap);
		writeStats(fw,trainingTime,annWrap.getClassificationRatio(),CLASSIFIER_TYPE.ANN.name(),"ex2");
		annWrap = new AnnClassifierWrapper();
		annWrap.setTrainingDataInputFile(trainingDataPercents);
		annWrap.setTrainingDataTargetFile(tarAnn);
		annWrap.setTestDataInput(testDataP);
		annWrap.setTestDataTarget(testTarAnn);
		annWrap.setImageOutDest(trainedAnnPercentages);
		annWrap.setInputLength(19);
		annWrap.initiate();

		trainingTime = measureTraining(annWrap);
		writeStats(fw,trainingTime,annWrap.getClassificationRatio(),CLASSIFIER_TYPE.ANN.name(),"ex3");

	}

	public void replace(String previous,String id){
		measurementOutput=measurementOutput.replace(previous,id);
		//INputs
		trainingDataAngles=trainingDataAngles.replace(previous,id);
		trainingDataPercents=trainingDataPercents.replace(previous,id); 	
		trainingDataDevs=trainingDataDevs.replace(previous,id);
		//Targets
		tarAnn=tarAnn.replace(previous,id); 
		tarKnn=tarKnn.replace(previous,id); 
		tarRTrees=tarRTrees.replace(previous,id); 
		//Images to output
		trainedAnnAngles=trainedAnnAngles.replace(previous,id); 
		trainedAnnDevs=trainedAnnDevs.replace(previous,id); 
		trainedAnnPercentages=trainedAnnPercentages.replace(previous,id); 

		trainedKnnAngles=trainedKnnAngles.replace(previous,id); 
		trainedKnnPercentages=trainedKnnPercentages.replace(previous,id); 
		trainedKnnDevs=trainedKnnDevs.replace(previous,id); 

		trainedRTreesPercentages=trainedRTreesPercentages.replace(previous,id); 
		trainedRTreesAngles=trainedRTreesAngles.replace(previous,id); 
		trainedRTreesDevs=trainedRTreesDevs.replace(previous,id);

		testDataA = testDataA.replace(previous, id);
		testDataD = testDataD.replace(previous, id);
		testDataP = testDataP.replace(previous, id);
		testTarKnn = testTarKnn.replace(previous, id);
		testTarAnn = testTarAnn.replace(previous, id);
		testTarRTrees = testTarRTrees.replace(previous, id);		
	}

	private static  long measureTraining(IClassifierWrapper annWrap) {
		long lastTimestamp = System.currentTimeMillis();
		annWrap.train();
		long trainingTime = System.currentTimeMillis() - lastTimestamp;
		return trainingTime;
	}

	private static void writeStats(FileWriter fw, long trainingTime, double classificationRatio,
			String classifierName, String postfix) {
		System.out.println(classifierName+" ratio:" +" "+classificationRatio);
		try {
			fw.write(classifierName+"-"+postfix+",");
			fw.write(trainingTime/1000+",");
			fw.write(classificationRatio+"\n");
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
}

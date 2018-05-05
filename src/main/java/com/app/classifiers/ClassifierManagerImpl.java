package com.app.classifiers;
import com.app.classifiers.wrappers.AnnClassifierWrapper;
import com.app.classifiers.wrappers.AnnClassifierWrapper.CLASSIFIER_TYPE;
import com.app.graphics.body_model.BodyModelImpl;
import com.app.classifiers.wrappers.KnnClassifierWrapper;
import com.app.classifiers.wrappers.RandomTreesClassifierWrapper;

public class ClassifierManagerImpl implements IClassifierManager{
	private RandomTreesClassifierWrapper rTreesWrapper = null;
	private KnnClassifierWrapper knnWrapper = null;
	private AnnClassifierWrapper annWrapper = null;
	private int refreshRate = 10;
	private String annImageFile = "annTrained.ml";
	private String knnImageFile = "knnTrained.ml";
	private String rTreesImageFile = "rTreesTrained.ml";

	public ClassifierManagerImpl(){
		rTreesWrapper = new RandomTreesClassifierWrapper();
		knnWrapper = new KnnClassifierWrapper();		
		annWrapper = new AnnClassifierWrapper();		
	}

	public void subcribeTo(CLASSIFIER_TYPE type,IClassifierCallbacks callbacks){
		switch(type){
		case ANN: 
			startAnn(callbacks);			
			break;
		case KNN:
			startKNN(callbacks);
			break;
		case RANDOMTREES:
			startRTrees(callbacks);
			break;

		}
	}

	private void startKNN(IClassifierCallbacks callbacks) {
		new Thread(){
			@Override
			public void run() {
				super.run();
				knnWrapper.initiate(knnImageFile);
				knnWrapper.start(refreshRate,callbacks);
				System.out.println("K-NN Started");
			}
		}.start();

	}

	private void startRTrees(IClassifierCallbacks callbacks) {
		Thread th = new Thread(){
			@Override
			public void run() {
				super.run();
				rTreesWrapper.initiate(rTreesImageFile);
				rTreesWrapper.start(refreshRate, callbacks);
				System.out.println("Random Forest started");
			}
		};
		th.start();		
	}
	
	private void startAnn(IClassifierCallbacks callbacks) {
		Thread th = new Thread(){
			@Override
			public void run() {
				super.run();
				annWrapper.initiate(annImageFile);
				annWrapper.start(refreshRate, callbacks);
				System.out.println("ANN started");
			}
		};
		th.start();		
	}

	public int classifySampleAsId(float[] percentages) {
		return TargetModel.getLabelId(getTotalPercentage(percentages));
	}

	private double[] getPercentages(float[] featureVector,int edge) {
		double[] targetVector = new double[featureVector.length/2];
		for(int i = 0; i < targetVector.length;i++){
			targetVector[i] = 0;
			targetVector[i] = featureVector[i+19] - featureVector[i];
			if(targetVector[i] <0 ){
				targetVector[i]*=-1;	
			}
			if(targetVector[i] >=edge){
				targetVector[i] =0;
			}else{
				targetVector[i] = targetVector[i]/edge;
				targetVector[i] = 100-targetVector[i];
			}

		}
		return targetVector;
	}

	public void unsubscribeAll(IClassifierCallbacks ea) {
		knnWrapper.stop(ea);
		annWrapper.stop(ea);
		rTreesWrapper.stop(ea);
	}

	@Override
	public void unsubscribe(CLASSIFIER_TYPE type, IClassifierCallbacks callbacks) {
		switch(type){
		case ANN:
			annWrapper.stop(callbacks);
			break;
		case KNN:
			knnWrapper.stop(callbacks);
			break;
		case RANDOMTREES:
			rTreesWrapper.stop(callbacks);
			break;
		default:
			System.err.println("Uknown classifier type");
		}
	}

	@Override
	public String classifySampleAsLabel(float[] percentages) {
		return TargetModel.getLabel(TargetModel.getLabelId(getTotalPercentage(percentages)));
	}
	
	
	public String classifyAsLabelFromAngleArray(float[] featureVector) {
		float[] angleDeviations = getFeatureVectorAngleDeviation(featureVector);
		float[] percentages = getFeatureVectorPercentages(angleDeviations);
		return TargetModel.getLabel(TargetModel.getLabelId(getTotalPercentage(percentages)));
	}
	
	/**
	 * Returns array of floats, contains deviation according to formular
	 * observed - expected = result
	 * @return
	 */
	private float[] getFeatureVectorAngleDeviation(float[] vectorAngles) {
		float[] result = new float[vectorAngles.length/2];
		for(int i = 0; i< result.length;i++){
			result[i] = Math.abs(vectorAngles[i+result.length] - vectorAngles[i]);
		}
		return result;
	}
	
	/**
	 * Returns array of floats, contains deviations presented as percentage
	 * innerLimit - a variable used during conversion from deviation to percentage.
	 * It defines a value of of deviation at it maximum. lowering the value of innerlimit 
	 * will affect performance evaluation to require higher precision for higher
	 * success classification.
	 * @return array that contains deviation as percentage
	 */
	public float[] getFeatureVectorPercentages(float[] angleDeviations ) {
		BodyModelImpl bm = new BodyModelImpl();
		float[] innerLimit = bm.getInnerLimit();
		float[] result = new float[angleDeviations.length];
		for(int i = 0; i< angleDeviations.length;i++){
			if(innerLimit[i]-angleDeviations[i] <0){
				result[i] = 0;
			}else{
				result[i] = ((innerLimit[i]-angleDeviations[i])/innerLimit[i])*100;
			}		
		}
		return result;
	}
	
	public String classifyAngleDeviationsArray(float[] percentages) {
		return TargetModel.getLabel(TargetModel.getLabelId(getTotalPercentage(percentages)));
	}
	
	public double getTotalPercentage(float[] percentages){
		double tot = percentages.length*100;
		double sum = 0;
		for(double d:percentages){
			sum+=d;
		}
		return ((sum/tot)*100);
	}

	public void setAnnClassifierWrapper(AnnClassifierWrapper annWrapper) {
		this.annWrapper = annWrapper;		
	}
	public void setKnnClassifierWrapper(KnnClassifierWrapper knnWrapper) {
		this.knnWrapper = knnWrapper;		
	}
	public void setRTreesClassifierWrapper(RandomTreesClassifierWrapper rTreesWrapper) {
		this.rTreesWrapper = rTreesWrapper;		
	}

	public AnnClassifierWrapper getAnnCWrapper() {
		return annWrapper;
	}

	public KnnClassifierWrapper getKnnCWrapper() {
		return knnWrapper;		
	}

	public RandomTreesClassifierWrapper getRTreesCWrapper() {
		return rTreesWrapper;		
	}

	public String getAnnImageFile() {
		return annImageFile;
	}

	public void setAnnImageFile(String annImageFile) {
		this.annImageFile = annImageFile;
	}

	public String getKnnImageFile() {
		return knnImageFile;
	}

	public void setKnnImageFile(String knnImageFile) {
		this.knnImageFile = knnImageFile;
	}

	public String getrTreesImageFile() {
		return rTreesImageFile;
	}

	public void setrTreesImageFile(String rTreesImageFile) {
		this.rTreesImageFile = rTreesImageFile;
	}

	public String classifySampleAsLabelFromAngleDevs(float[] featureVector) {
		float[] percentages = getFeatureVectorPercentages(featureVector);
		return TargetModel.getLabel(TargetModel.getLabelId(getTotalPercentage(percentages)));
	}

	public String classifyAsLabelFromPercentsArray(float[] featureVector) {
		return TargetModel.getLabel(TargetModel.getLabelId(getTotalPercentage(featureVector)));
	}
	
}

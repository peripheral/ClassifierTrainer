package com.app.classifiers;

import com.app.classifiers.wrappers.AnnClassifierWrapper.CLASSIFIER_TYPE;
import com.app.graphics.Scene;

public interface ClassifierCallbacks {

	public Scene getScene();

	public void setClassificationResult(CLASSIFIER_TYPE ann, String label);

	public int getDegreeOfFreedom();

	public int getEDGE();

}

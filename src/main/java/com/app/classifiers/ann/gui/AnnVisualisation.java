package com.app.classifiers.ann.gui;

import javax.swing.JPanel;

import org.bytedeco.javacpp.opencv_ml.ANN_MLP;

public class AnnVisualisation extends JPanel{
	
	private ANN_MLP ann;

	public AnnVisualisation(ANN_MLP ann){
		this.ann = ann;
	}
}

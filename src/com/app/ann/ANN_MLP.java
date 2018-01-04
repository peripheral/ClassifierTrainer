package com.app.ann;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;

import org.bytedeco.javacpp.avformat.AVOutputFormat.Get_output_timestamp_AVFormatContext_int_LongPointer_LongPointer;

import com.app.ml.nn.mynn.ConstantsEnums.ActivationFuctionType;
import com.app.ml.nn.mynn.ConstantsEnums.NeuronType;

public class ANN_MLP {
	private NeuronLayer[] layers;
	private int iterations = 1;
	private float epsilon = 0.1f;

	/**
	 * 
	 * @param layers - number of hidden layers
	 * @param layerSize - size of a hidden layer
	 * @param inputSize - number of features
	 * @param outputSize - number of output neurons
	 */
	public ANN_MLP(int[] layerSizes,ActivationFuctionType aFType){
		layers = new NeuronLayer[layerSizes.length];
		for(int i = 0; i < layerSizes.length;i++){
			if(i == 0){
				layers[i] = new NeuronLayer(NeuronType.INPUT, aFType, layerSizes[i]);
				layers[i].setId(i+1);
			}else if(i == layerSizes.length-1){
				layers[i] = new NeuronLayer(NeuronType.OUTPUT, aFType, layerSizes[i]);
				layers[i].setId(i+1);
			}else{
				layers[i] = new NeuronLayer(NeuronType.HIDDEN, aFType, layerSizes[i]);
				layers[i].setId(i+1);
			}
			if(i >0 ){
				layers[i-1].setUpperLayer(layers[i]);
			}
		}	
	}

	public  void printAnn() {
		for(int l = 0; l < layers.length;l++){
			System.out.println("Layer:"+ (l+1)+" Weight array size:"+layers[l].getWeights().length);
			layers[l].printWeights();
		}
	}

	public void train(File in,File target){
		if(test(in,target)){
			System.out.println("Incorrect number or rows");
			System.exit(-1);
		}
		Scanner input = null;
		Scanner tar = null;
		try {
			input = new Scanner(in);
			tar = new Scanner(target);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		String s ;
		String result;
		while(input.hasNextLine()){
			s = input.nextLine();
			result = tar.nextLine();
			runNetwork(getFloats(s.split(",")),getFloats(result.split(",")));
			backPropagate();
		}
	}

	private void backPropagate() {
			
	}

	private float[] getFloats(String[] split) {
		float[] result = new float[split.length];
		for(int i = 0;i < split.length;i++){
			result[i] = Float.parseFloat(split[i]);
		}
		return result;
	}

	private void runNetwork(float[] input, float[] output) {
		layers[0].setNetInput(input);
		layers[0].printInput();
		layers[0].execute();
		layers[layers.length-1].printOutput();
	}

	private boolean test(File in, File target) {
		Scanner input = null;
		Scanner tar = null;
		try {
			input = new Scanner(in);
			tar = new Scanner(target);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		while(input.hasNextLine()){
			if(!tar.hasNextLine()){
				return false;
			}
			input.nextLine();
			tar.nextLine();
		}
		if(!tar.hasNextLine()){
			return false;
		}		
		return true;
	}

	public static void main(String[] args){
		int[] layers = new int[]{19,19,19,5};
		ANN_MLP ann = new ANN_MLP(layers,ActivationFuctionType.SIGMOID);
		File in = new File("iAnglesSetA.csv");
		File target = new File("tNNVectorSetA.csv");
		//GUIANN ui = new GUIANN();
		//ui.setAnn(ann);
		//ann.train(in, target,1);
		float[] input = new float[19];
		float[] result = new float[5];
		for(int i = 0; i< input.length;i++){
			input[i]  = 80;
		}
		ann.predict(input, result);
		System.out.print("Result:");
		for(float f:result){
			System.out.print(f+",");
		}
		System.out.println();
	}

	public int layerCount() {		
		return layers.length;
	}

	/**
	 * Returns size of particular layer, layer is index in the array.
	 *  Starts with 0
	 * @param layer
	 * @return
	 */
	public int getLayerSize(int layer) {		
		return layers[layer].getSize();
	}

	public float getWeight(int layer, int i) {
		if(layers[layer].getWeights().length > i){
			return layers[layer].getWeights()[i];
		}
		else return 1;
	}

	public float getNormalizedWeight(int layer, int i) {
		if(layers[layer].isOutputLayer()){
			return 0;
		}
		if(getMaximalWeight(layer) == 0){
			return 0;
		}
		return getWeight(layer, i)/getMaximalWeight(layer);
	}

	private float getMaximalWeight(int layer) {
		float large = 0;
		for(float weight:layers[layer].getWeights()){
			if(Math.abs(large)<Math.abs(weight)){
				large = weight;
			}
		}
		return large;
	}
	
	public void predict(float[] input,float[] result){
		float[] res = layers[0].setNetInput(input).execute();
		for(int i = 0;i< result.length;i++){
			result[i] = res[i];
			System.out.print(res[i]+" ");
		}
		System.out.println();
	}

	public int getIterations() {
		return iterations;
	}

	public void setIterations(int iterations) {
		this.iterations = iterations;
	}

	public float getEpsilon() {
		return epsilon;
	}

	public void setEpsilon(float epsilon) {
		this.epsilon = epsilon;
	}
}

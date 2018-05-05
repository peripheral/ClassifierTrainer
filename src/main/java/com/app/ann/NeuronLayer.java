package com.app.ann;

import java.util.Scanner;

import com.app.ml.nn.mynn.ConstantsEnums.ActivationFuctionType;
import com.app.ml.nn.mynn.ConstantsEnums.NeuronType;

public class NeuronLayer {
	private float[] weights= new float[1];   /* Weights for this layer,that is between this and proceeding layer */
	private float treshold = 1; /* Treshold not used currently */
	private NeuronType nType;
	private ActivationFuctionType aFType;
	private float[] output;
	private float[] netInput;
	private NeuronLayer upperLayer;
	private int id = -1;
	public NeuronLayer(NeuronType nType,ActivationFuctionType aFType,int size){
		netInput = new float[size];
		output = new float[size];
		this.setnType(nType);
		this.setaFType(aFType);
	}
	public NeuronLayer(ActivationFuctionType aFType,int size){
		this(NeuronType.HIDDEN,aFType,size);
	}

	public float[] getWeights() {
		return weights;
	}
	public void setWeights(float[] weights) {
		this.weights = weights;
	}

	public ActivationFuctionType getaFType() {
		return aFType;
	}

	public void setaFType(ActivationFuctionType aFType) {
		this.aFType = aFType;
	}

	public NeuronType getnType() {
		return nType;
	}

	public void setnType(NeuronType nType) {
		this.nType = nType;
	}

	public float getTreshold() {
		return treshold;
	}

	public void setTreshold(float treshold) {
		this.treshold = treshold;
	}

	public float[] getNetInput() {
		return netInput;
	}

	/**
	 * Set input to the layer
	 * @param netInput
	 * @return
	 */
	public NeuronLayer setNetInput(float[] netInput) {
		for(int i = 0; i < netInput.length;i++){
			this.netInput[i] = netInput[i];
		}
		return this;
	}

	public float[] execute() {
		System.out.println("Executed layer is:"+this);
		printInput();
		System.out.print("Output:");
		for(int i = 0; i < netInput.length; i ++){
			output[i] = new Float(Formula.compute(netInput[i],aFType));	
			System.out.print(output[i]+",");
		}
		System.out.println();
		if(isOutputLayer()){
			return output;
		}
		propagate(upperLayer);
		return upperLayer.execute();
	}

	public void propagate(NeuronLayer layer){
		System.out.println("Propagating to:"+layer);
		if(isOutputLayer()){
			return;
		}
		System.out.print("Propagated:");
		for(int in = 0;in< layer.getSize(); in++){
			layer.netInput[in] = 0;
			for(int out = 0;out < output.length; out++){
				layer.netInput[in]+=output[out]*weights[(out*in)];
			}
			System.out.print(layer.netInput[in]+",");
		}
		System.out.println();
	}

	public boolean isOutputLayer() {
		if(nType == NeuronType.OUTPUT){
			return true;
		}
		else {
			return false;
		}

	}

	/**
	 * Returns number of neurons in the layer
	 * @return - an integer
	 */
	public int getSize() {
		return output.length;
	}

	public float[] getOutput() {
		return output;
	}

	public void setOutput(float[] output) {
		this.output = output;
	}

	public void printOutput() {
		for(int i = 0; i < output.length;i++){
			if(i<output.length-1){
				System.out.print(output[i]+",");
			}else{
				System.out.println(output[i]);
			}
		}		
	}

	public void printInput() {
		System.out.print("Printing input: ");
		for(int i = 0; i < netInput.length;i++){
			if(i<netInput.length-1){
				System.out.print(netInput[i]+",");
			}else{
				System.out.println(netInput[i]);
			}			
		}	
	}

	public void setUpperLayer(NeuronLayer neuronLayer) {
		if(neuronLayer == null){
			System.out.println("given upper layer is null"+neuronLayer);
			System.out.println("");
			Scanner sc = new Scanner(System.in);
			sc.nextLine();
			return;
		}
		this.upperLayer = neuronLayer;
		weights = new float[upperLayer.netInput.length*output.length];
		for(int i = 0; i < weights.length;i ++){
			weights[i] = 1;
		}
	}

	public void printWeights() {
		System.out.print("[");	
		for(int i = 0; i < weights.length ; i++){
			if(i < weights.length-1 ){
				System.out.print(weights[i]+",");
			}
			else{
				System.out.print(weights[i]);
			}
		}
		System.out.println("]");	
	}
	
	public String toString(){
		return "[Layer type:"+nType+",Layer number:"+id +", size:"+netInput.length+",weightCount:"+weights.length+"]";
	}
	
	public void setId(int id){
		this.id = id;
	}
	public int getId(){
		return id;
	}
}

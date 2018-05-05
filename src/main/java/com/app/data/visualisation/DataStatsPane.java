package com.app.data.visualisation;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import javax.swing.JPanel;

public class DataStatsPane extends JPanel {
	private int[] percentageChannels = new int[101];
	private int[][] percentageChannelsPerClass = new int[5][101];
	private int[] classChannel = new int[5];
	private Dimension preferredSize = new Dimension(300,100);

	public DataStatsPane(){
		setPreferredSize(preferredSize);
		setBackground(Color.WHITE);
	}

	public void loadPercentageData(String percentageFile,String rTreesTarFile){
		Scanner sc = null;
		Scanner sc1 = null;
		try {
			sc = new Scanner(new File(percentageFile));
			sc1 = new Scanner(new File(rTreesTarFile));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		String line = ""; int value = 0;
		while(sc.hasNextLine()){
			line = sc.nextLine();
			value = Integer.valueOf(sc1.nextLine());
			String[] values  = line.split(",");
			
			for(String v:values){
				percentageChannels[Integer.decode(v)]++;
				percentageChannelsPerClass[value-1][Integer.decode(v)]++;
			}
		}
	}

	/**
	 * File containing one column with classes.
	 * first index in classChannel contains occurrence 5th class
	 * the following indexes match the class 1-1,2-2
	 * @param classFile
	 */
	public void loadClassData(String classFile){
		Scanner sc = null;
		try {
			sc = new Scanner(new File(classFile));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		while(sc.hasNextLine()){
			String line = sc.nextLine();
			if(Integer.decode(line)%5 == 0) {
				classChannel[4]++;
			}else {
				classChannel[Integer.decode(line)%5-1]++;
			}
		}
	}

	@Override
	public void paintComponent(Graphics gx){
		drawProcentChannels(gx);
		drawClassChannels(gx);		
	}

	private void drawClassChannels(Graphics gx) {
		int width = getWidth()/2;
		int height = getHeight();
		int largest = getLargest(classChannel);
		int stepY = 3;
		int startY = 0;
		int counter = 2;
		int startX = 5;
		gx.setColor(Color.BLUE);
		for(int p:classChannel){

			gx.fillRect(startX, startY, (int)(((double)p/largest)*width),2 );
			startY+=stepY;
		}				
	}

	private void drawProcentChannels(Graphics gx) {
		int width = getWidth();
		int height = (getHeight()-17-(6))/5;
		int startY = getHeight();
		int largest = getLargest(percentageChannels);
		System.out.println("Largest:"+largest);
		int stepX = (int)(width/100.0);
		int startX = 0;
		gx.setColor(new Color(0,255,0));
		/*for(int p:percentageChannels){		
			System.out.println(p+" "+(height-(p/(double)largest)*height));
			gx.fillRect(startX, startY, stepX, (int)(-((double)p/largest)*height));
			startX+=stepX;
		}	*/	
		for(int i = 0; i < 5;i++){
			largest = getLargest(percentageChannelsPerClass[i]);
			for(int p:percentageChannelsPerClass[i]){		
				gx.fillRect(startX, startY, stepX, (int)(-((double)p/largest)*height));
				startX+=stepX;
			}
			startX = 0;
			startY =startY-height-2;
		}
	}

	private int getLargest(int[] array) {
		int largest = 1;
		for(int v: array){
			if(largest <v){
				largest = v;
			}
		}
		return largest;

	}
}

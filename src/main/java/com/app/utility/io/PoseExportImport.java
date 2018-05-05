package com.app.utility.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Set;

import javax.swing.JFileChooser;

import com.app.entities.Posture;

public class PoseExportImport {


	public void export(Posture pose,String fName){	
		FileWriter out = null;
		try {
			out = new FileWriter(new File(fName));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		Map<String,double[]> jointMap = pose.getJointMap();
		Set<String> keys = jointMap.keySet();
		double[] coords;
		try {
			for(String key:keys){
				coords = jointMap.get(key);
				out.write(coords[0]+" ");
				out.write(coords[1]+" ");
				out.write(coords[2]+" ");
			}
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void importFromFile(String file){	
		Posture p = new Posture();
		Scanner scanner = null;
		try {
			scanner = new Scanner(new File(file));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			double coords[] = new double[3];
			Map<String,double[]> jointMap = p.getJointMap();
			Set<String> keys = jointMap.keySet();
			for(String key:keys){
				coords[0] = new Double(scanner.next());
				coords[1] = new Double(scanner.next());
				coords[2] = new Double(scanner.next());
				p.setJoinLocation(key, coords);
			}
		} catch (NoSuchElementException e) {
			System.err.println("Incorect file format");
		}
	}
	public Posture importFromFile(File file){	
		Posture p = new Posture();
		Scanner scanner = null;
		try {
			scanner = new Scanner(file);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			double coords[] = new double[3];
			Map<String,double[]> jointMap = p.getJointMap();
			Set<String> keys = jointMap.keySet();
			for(String key:keys){
				coords[0] = new Double(scanner.next());
				coords[1] = new Double(scanner.next());
				coords[2] = new Double(scanner.next());
				p.setJoinLocation(key, coords);
			}
			return p;
		} catch (NoSuchElementException e) {
			System.err.println("Incorect file format");
			scanner.close();
			return null;
		}
	}
	
	public void export(Posture p) {
		JFileChooser fc = new JFileChooser();
		//fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int returnVal = fc.showSaveDialog(null);
		
		 if (returnVal == JFileChooser.APPROVE_OPTION) {
	            File file = fc.getSelectedFile();	        
	            try {
					System.out.println(file.getCanonicalPath());
					export(p,file );
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        } else {
	            
	        }
	}
	
	public Posture importPose() {
		JFileChooser fc = new JFileChooser();
		//fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int returnVal = fc.showOpenDialog(null);
		
		 if (returnVal == JFileChooser.APPROVE_OPTION) {
	            File file = fc.getSelectedFile();	        
	            try {
					System.out.println(file.getCanonicalPath());
					return importFromFile(file );
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        } else {
	            
	        }
		return null;
	}

	private void export(Posture pose, File file) {
		FileWriter out = null;
		try {
			out = new FileWriter(file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		Map<String,double[]> jointMap = pose.getJointMap();
		Set<String> keys = jointMap.keySet();
		double[] coords;
		try {
			for(String key:keys){
				coords = jointMap.get(key);
				out.write(coords[0]+" ");
				out.write(coords[1]+" ");
				out.write(coords[2]+" ");
			}
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

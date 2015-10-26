package app;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FeatureVectorExport {
	
	public void export(String fileName, Pose pose){
		File f = new File(fileName);
		if(!f.exists()){
			try {
				f.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
		}
		try {
			FileWriter fw = new FileWriter(f);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

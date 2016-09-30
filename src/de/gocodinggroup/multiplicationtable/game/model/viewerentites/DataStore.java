package de.gocodinggroup.multiplicationtable.game.model.viewerentites;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;


public class DataStore {
	public static void write (String filename, float[] xyz) throws IOException{
		  BufferedWriter outputWriter = null;
		  outputWriter = new BufferedWriter(new FileWriter(filename, true)); 
		  outputWriter.write(Arrays.toString(xyz));
		  outputWriter.newLine();
		  outputWriter.flush();  
		  outputWriter.close();  
		}
}

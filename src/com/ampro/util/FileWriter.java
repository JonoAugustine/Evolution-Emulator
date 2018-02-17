/**
 * 
 */
package com.ampro.util;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;

import com.ampro.Evolution.Dna.Codon;

/**
 * Contains 2 methods for writing and appending to txt files
 */
public class FileWriter {
	
	public static Path workingDir = Paths.get(".").toAbsolutePath().normalize();
	private final Path file;
	
	
	public FileWriter(String fileName) {
		
		file = writeFile(workingDir.toString(), fileName, fileName.toUpperCase(), "+++++++++++++++");
		
	}
	
	/**
	 * Writes strings to .txt file to file_location with name fileName </br>
	 * Writes string lines in order of ArrayList index
	 * @param file_location String
	 * @param fileName String
	 * @param lines ArrayList
	 * @return 
	 */
	public static Path writeFile(String file_location, String fileName, ArrayList<String> lines){
		if(file_location.charAt(file_location.length()-1) != '/' || file_location.charAt(file_location.length()-1) != '\\')
			file_location += "/";
		Path file = Paths.get(file_location + fileName +".txt");
		try {
			Files.write(file, lines, Charset.forName("UTF-8"));
		} catch (IOException e) {
			System.out.println("File Write failed");
			e.printStackTrace();
			assert false;
		}
		return file;
	}
	
	/**
	 * Writes strings to .txt file to file_location with name fileName </br>
	 * Writes string lines in order of Array index
	 * @param file_location String
	 * @param fileName String
	 * @param lines
	 * @return 
	 */
	public static Path writeFile(String file_location, String fileName, String...lines){
		ArrayList<String> linesList = new ArrayList<>();
		for(int i=0; i < lines.length; i++)
			linesList.add(lines[i]);
		
		if(file_location.charAt(file_location.length()-1) != '/' || file_location.charAt(file_location.length()-1) != '\\')
			file_location += "/";
		Path file = Paths.get(file_location + fileName +".txt");
		try {
			Files.write(file, linesList, Charset.forName("UTF-8"));
		} catch (IOException e) {
			System.out.println("File Write failed");
			e.printStackTrace();
			assert false;
		}
		
		return file;
	}
	
	/**
	 * Appends strings to .txt file to file_location with name fileName </br>
	 * Writes string lines in order of ArrayList index
	 * @param file_location String
	 * @param fileName String
	 * @param lines ArrayList
	 */
	public static void appendFile(String file_location, String fileName, ArrayList<String> lines){
		Path file = Paths.get(file_location + fileName +".txt");
		try {
			Files.write(file, lines, Charset.forName("UTF-8"), StandardOpenOption.APPEND);
		} catch (IOException e) {
			System.out.println("File Write failed");
			e.printStackTrace();
			assert false;
		}
	}

	/**
	 * Appends strings to .txt file to file_location with name fileName </br>
	 * Writes string lines in order of
	 * @param file_location String
	 * @param fileName String
	 * @param lines ArrayList
	 */
	public void addLines(String...lines){
		ArrayList<String> linesList = new ArrayList<>();
		for(int i=0; i < lines.length; i++)
			linesList.add(lines[i]);
			
		try {
			Files.write(this.file, linesList, Charset.forName("UTF-8"), StandardOpenOption.APPEND);
		} catch (IOException e) {
			System.out.println("File Write failed");
			e.printStackTrace();
			assert false;
		}
	}

	/**
	 * Appends strings to .txt file to file_location with name fileName </br>
	 * Writes string lines in order of
	 * @param ArrayList
	 *
	public void addLines(ArrayList<T> t) {
		ArrayList<String> linesList = new ArrayList<>();
		for(int i=0; i < t.size(); i++)
			linesList.add(t.get(i).toString);
			
		try {
			Files.write(this.file, linesList, Charset.forName("UTF-8"), StandardOpenOption.APPEND);
		} catch (IOException e) {
			System.out.println("File Write failed");
			e.printStackTrace();
			assert false;
		}
	}*/
}
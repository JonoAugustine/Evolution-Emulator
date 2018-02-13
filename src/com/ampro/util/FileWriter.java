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

/**
 * Contains 2 methods for writing and appending to txt files
 */
public class FileWriter {
	
	/**
	 * Writes strings to .txt file to file_location with name fileName </br>
	 * Writes string lines in order of ArrayList index
	 * @param file_location String
	 * @param fileName String
	 * @param lines ArrayList
	 */
	public static void writeFile(String file_location, String fileName, ArrayList<String> lines){
		if(file_location.charAt(file_location.length()-1) != '/' || file_location.charAt(file_location.length()-1) != '\\')
			file_location += "/";
		Path file = Paths.get(file_location + fileName +".txt");
		try {
			Files.write(file, lines, Charset.forName("UTF-8"));
		} catch (IOException e) {
			System.out.println("File Write failed");
			e.printStackTrace();
		}
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
			}
	}

}

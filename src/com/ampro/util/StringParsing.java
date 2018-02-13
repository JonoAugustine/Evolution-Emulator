package com.ampro.util;

import java.util.ArrayList;

import com.ampro.main.EvolutionRunner;

public class StringParsing{

	/**
	 * Parses through a given string and returns each instance of a digit string
	 * @param data String
	 * @param debugPrint
	 * @author Jonathan Augustine
	 */
	public static ArrayList<ArrayList<Integer>> parse(String...data){
		if(EvolutionRunner.DEBUG){
			System.out.println("Parsing Data for errors. \nData: \n[");
			for(String d : data)
				System.out.println(d + ",");
			System.out.println("]");
		}
		ArrayList<ArrayList<Integer>>  errors = new ArrayList<>();
		for(String g : data){
			if(EvolutionRunner.DEBUG)System.out.println("Parsing Data for errors. \nData: "+ g);
			ArrayList<Integer> thisErrors = new ArrayList<>();
			/* String "mod" is the current string with a single space appended
			 * The space is added to insure the number-finding loop does not
			 * return to already-discovered errors (presence of a number)*/
			String mod = g + " ";
			for(int i=0; i < mod.length(); i++){
				String temp = "";
				String character = mod.substring(i, i+1);
				if(isInteger(character)){
					temp += character;
					//This loop starts at index i and looks for adjacent numbers to add to the "temp" string
					for(int j=i+1; j < mod.length(); j++){
						String nextCharacter = mod.substring(j, j+1);
						//If the next single substring is an integer it is added to the string "temp"
						if(isInteger(nextCharacter))
							temp += nextCharacter;
						//else: the greater loop is continued at the end of the end of the current sequence of numbers
						else{
							i = j;
							break;
						}
					}
					thisErrors.add(Integer.parseInt(temp));
				}
			}
			if(EvolutionRunner.DEBUG) System.out.println("Errors: \n" + thisErrors.toString());
			errors.add(thisErrors);
		}
		if(EvolutionRunner.DEBUG){
			System.out.println("All errors:");
			for(ArrayList<Integer> i : errors)
				System.out.println(i);
		}
		return errors;
	}

	/**
	 * Removes all digits from any number of strings </br>
	 * 1.) Strips each input-string and places individual string characters into an ArrayList "strings" </br>
	 * 2.) for-loops through each string in "strings" and appends each non-integer character to a temp string</br>
	 * 3.) adds temp string to ArrayList returnStrings
	 * 4.) Repeats for each string in method input untill each string has had its numbers removed
	 * @param data N number of Strings
	 * @return ArrayList<\String> Input strings with numbers removed
	 */
	public static ArrayList<String> intRemoval(String...data){
		/**ArrayList to be returned*/
		ArrayList<String> returnStrings = new ArrayList<>();
		/*For-loop to separate and reconstruct each String*/
		for(String a : data){
			/* Temp ArrayList to hold each String character*/
			ArrayList<String> strings = new ArrayList<>();
			/* Checks each single character substring to see if it is an integer*/
			for(int i=0; i < a.length(); i++)
				/* Adds each non-Integer substring to the temp ArrayList strings*/
				if(!isInteger(a.substring(i, i+1))) //if current substring is not an Integer
					strings.add(a.substring(i, i+1));
			String temp = "";
			/*Reconstructs the original string from the temp ArrayList "strings" without numbers
			 * by appending each substring to the temproray string, "temp"
			 */
			for(String s : strings)
				temp += s;
			/*Adds the reconstructed string "temp" to the returnString ArrayList*/
			returnStrings.add(temp);
		}
		return returnStrings;
	}

	/**
	 * Removes all letters from the input String and returns the resulting number sequence
	 * @param input String
	 * @return int
	 */
	public static int letterRemoval(String input){
		String retuPre = "";
		String[] inpu = input.split("");
		ArrayList<String> in = new ArrayList<>();
		for (String element : inpu)
			in.add(element);
		for(int a=0; a< in.size(); a++)
			if(!isInteger(in.get(a))){
				in.remove(a);
				a--;
			}
			else
				retuPre += in.get(a);

		try{
			return Integer.parseInt(retuPre);
		}catch (NumberFormatException e) {
			return -1;
		}
	}

	/**
	 * Takes a string and returns true if the string is a digit
	 * @param input
	 * @return boolean
	 */
	private static boolean isInteger( String input ){
		try{
			Integer.parseInt( input );
			return true;
		}
		catch( Exception e){
			return false;
		}
	}

}
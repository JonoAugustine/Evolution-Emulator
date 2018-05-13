/**
 *
 */
package com.ampro.main;

import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

import com.ampro.Evolution.Dna.Codon;

/**
 *	Contains all constants for each class
 * @author Jonathan Augustine
 *
 */
public class BioConstants
{

	///---------------------------------------General------------------------
	/** The standard US alphabet in upper case */
	public static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	/** "arabic" numerals 0 to 9 */
	public static final String NUMERALS = "0123456789";
	//--------------------------------------------------
	///-------------------------------------------------DNA------------------------///
	public final static String[] DNA = new String[] { "A", "T", "C", "G" };
	public final static String[] RNA = new String[] { "A", "U", "C", "G" };

	//-----CODON--------------

	public static ArrayList<Codon> allPossibleCodons = new ArrayList<>();
	/**The length of all codons {@value}*/
	public static int CODON_LENGTH;
	/**The minimum number of codons any gene can be*/
	public static int SCORE_RANGE[] = new int[2]; // -100 - 100?

	private static int START_CODONS;
	public static int STOP_CODONS;

	//-----------------------

	//-----GENE--------------
	public static int MINIMUM_GENE_LENGTH;

	//-----------------------

	//-----Trait-------------
	//-----------------------


	//--------------------------------------------------------Chromatid---------
	public static int[] CHROMATID_LENGTH_RANGE = new int[2];
	//TODO $Make it so each organism has an array of chromatid length ranges
	//------------------------------

	//-----Chromosome--------
	public static int CHROMATID_per_CHROMOSOME = 2;
	//TODO $Come up with a way to use odd numbers
	//->(Problem: Which parent to take which chromatid from???)

	public static int NUMBER_STARTING_DEFAULT_CHROMOSOME;
	//-----------------------


	//---------------------------------------------------------DNA Reader--------
	//-----------------------

	///----------------------------------------------------///

	///--------------------------------------------------------Organism---------------------

	//-----Organism----------
	/** Length of all Organism object DNA Sequences
	 * </br> Current value: {@value} */
	public static int DNA_LENGTH = 60003;
	//-----------------------------Naming Related Variables--------
	/**The current highest first index letter for Organism name generation*/
	public static int NEXT_NAME_LETTER_INDEX = 0;
	public static int NEXT_NAME__INDEX = 0;
	/**The current highest number for Organism name generation*/
	public static int NEXT_NAME_NUMBER= 0;
	//---------------------------------------

	//------------------------------------------------------------Population-----------
	/**The current highest first index letter for Population name generation*/
	public static int P_NEXT_NAME_LETTER_INDEX = 0;
	public static int P_NEXT_NAME__INDEX = 0;
	/**The current highest number for Population name generation*/
	public static int P_NEXT_NAME_NUMBER= 0;
	//-----------------------



	///----------------------------------------------------///
	private static boolean setUp = false;



	/**
	 * Sets up all constants
	 */
	@SuppressWarnings("unused")
	public static void setUp(){

		Scanner s;
		if(!setUp){
			s = new Scanner(System.in);

			System.out.println("Default? (true/1)>>");
			if( true ){//s.nextInt() == 1){

				System.out.println("Generating possible Codons");
				CODON_LENGTH = 3;
				allPossibleCodonsGenerator(DNA, CODON_LENGTH);

				System.out.println(allPossibleCodons);

				System.out.println("Generating Start Codon");
				START_CODONS = 1;
				for(int i=0; i < START_CODONS; i++){
					int temp = new Random().nextInt(allPossibleCodons.size());
					if(!allPossibleCodons.get(temp).isStart() && !allPossibleCodons.get(temp).isStop())
						allPossibleCodons.get(temp).setFunction(1);
					System.out.println(allPossibleCodons.get(temp));
				}

				System.out.println("Generating Stop Codons");
				STOP_CODONS = 3;
				for(int i=0; i < STOP_CODONS; i++){
					int temp = new Random().nextInt(allPossibleCodons.size());
					if(!allPossibleCodons.get(temp).isStart() && !allPossibleCodons.get(temp).isStop())
						allPossibleCodons.get(temp).setFunction(-1);
					System.out.println(allPossibleCodons.get(temp));
				}

				NUMBER_STARTING_DEFAULT_CHROMOSOME = 3;
				System.out.println("NUMBER_STARTING_DEFAULT_CHROMOSOME>>" + NUMBER_STARTING_DEFAULT_CHROMOSOME);

				MINIMUM_GENE_LENGTH = 10;
				System.out.println("Minimum Gene Length (in codons)>>" + MINIMUM_GENE_LENGTH);

				SCORE_RANGE[0] = -10;
				System.out.println("Codon Score Minimum>>" + SCORE_RANGE[0]);

				SCORE_RANGE[1] = 10;
				System.out.println("Codon Score Minimum>>" + SCORE_RANGE[1]);

				CHROMATID_LENGTH_RANGE[0] = 100;
				System.out.println("Chromatid minimum length (in codons)>>" + CHROMATID_LENGTH_RANGE[0]);

				CHROMATID_LENGTH_RANGE[1] = 200;
				System.out.println("Chromatid Maximum length (in codons)>>" + CHROMATID_LENGTH_RANGE[1]);

				CHROMATID_per_CHROMOSOME = 2;
				System.out.println("Number of Chromatids per Chromosome>>" + CHROMATID_per_CHROMOSOME);

				/*
				System.out.println("Input Organism DNA Length (in codons)>>");
				DNA_LENGTH = s.nextInt();
				 */

				//System.out.println("Input >>");

				setUp = true;
				s.close();
				System.out.println("Set up Done\n");
			}else{
				System.out.println("Input Starting Codon Length>>");
				CODON_LENGTH = s.nextInt();
				allPossibleCodonsGenerator(DNA, CODON_LENGTH);

				System.out.println("Input Number of Start Codons>>");
				START_CODONS = s.nextInt();
				for(int i=0; i < START_CODONS; i++){
					int temp = new Random().nextInt(allPossibleCodons.size());
					if(!allPossibleCodons.get(temp).isStart() && !allPossibleCodons.get(temp).isStop())
						allPossibleCodons.get(temp).setFunction(1);
				}

				System.out.println("Input Number of Stop Codons>>");
				STOP_CODONS = s.nextInt();
				for(int i=0; i < START_CODONS; i++){
					int temp = new Random().nextInt(allPossibleCodons.size());
					if(!allPossibleCodons.get(temp).isStart() && !allPossibleCodons.get(temp).isStop())
						allPossibleCodons.get(temp).setFunction(-1);
				}

				System.out.println("Input Starting Default Organism Number of Chromosomes>>");
				NUMBER_STARTING_DEFAULT_CHROMOSOME= s.nextInt();

				System.out.println("Input Minimum Gene Length (in codons)>>");
				MINIMUM_GENE_LENGTH = s.nextInt();

				System.out.println("Input Codon Score Minimum>>");
				SCORE_RANGE[0] = s.nextInt();

				System.out.println("Input Codon Score Maximum>>");
				SCORE_RANGE[1] = s.nextInt();

				System.out.println("Input Chromatid minimum length (in codons)>>");
				CHROMATID_LENGTH_RANGE[0] = s.nextInt();

				System.out.println("Input Chromatid maximum length (in codons)>>");
				CHROMATID_LENGTH_RANGE[1] = s.nextInt();

				System.out.println("Input Number of Chromatids per Chromosome>>");
				CHROMATID_per_CHROMOSOME = s.nextInt();

				System.out.println("Input Organism DNA Length (in codons)>>");
				DNA_LENGTH = s.nextInt();


				//System.out.println("Input >>");

				setUp = true;
				s.close();
				System.out.println("Set up Done\n");
			}
		}

	}

	/**
	 *
	 * @param arr
	 * @param k
	 */
	private static void allPossibleCodonsGenerator(String[] arr, int k) {
		int n = arr.length;
		int[] idx = new int[k];
		String[] perm = new String[k];
		while (true) {
			for (int i = 0; i < k; i++)
				perm[i] = arr[idx[i]];
			allPossibleCodons.add(new Codon(String.join("", perm)));
			// generate the next permutation
			int i = idx.length - 1;
			for (; i >= 0; i--) {
				idx[i]++;
				if (idx[i] < n)
					break;
				idx[i] = 0;
			}
			// if the first index wrapped around then we're done
			if (i < 0)
				break;
		}
	}


}

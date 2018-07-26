package com.ampro.Evolution.Dna;

import java.util.Comparator;
import java.util.List;
import java.util.Random;

import com.ampro.evemu.DNA;
import com.ampro.main.BioConstants;
import com.ampro.main.EvolutionRunner;
import com.ampro.util.ToolBox;

/**
 * A codon is a sequence of DNA bases
 * that is CODON_LENGTH bases long
 *
 * @author Jonathan Augustine
 */
public class Codon extends BioConstants{

	private String bases;
	private float score; //Score from -100?/0? to 100
	protected int function = 0; // 0 if neither, -1 if stop, 1 is start

	public static Comparator<Codon> scoreComparator = new Comparator<Codon>(){
		@Override
		public int compare(Codon o1, Codon o2){
			return (int) (100000000*(o1.getScore() - o2.getScore()));
		}
	};

	/**Based on order ATCG*/
	public static Comparator<Codon> baseComparator = new Comparator<Codon>(){
		@Override
		public int compare(Codon o1, Codon o2){
			int retu = 0;
			String[] b1 = o1.getBases().trim().split("");
			String[] b2 = o2.getBases().trim().split("");
			for(int i=0; i < b1.length; i++){
				retu = this.compareBase(b1[i], b2[i]);
				if(retu != 0)
					return retu;
			}
			return retu;
		}

		/**
		 * Returns -1,0,1 Based on order ATCG
		 * @return
		 */
		public int compareBase(String s1, String s2){
			if(s1.length() > 1 || s2.length() > 1)
				ToolBox.systmError("Comparator Failure", s1 + " vs " + s2);
			int retu =0;
			if(s1.equals("A")){
				if(s2.equals("A"))
					retu = 0;
				if(s2.equals("T"))
					retu = -1;
				if(s2.equals("C"))
					retu = -1;
				if(s2.equals("G"))
					retu = -1;
			}else if(s1.equals("T")){
				if(s2.equals("A"))
					retu = 1;
				if(s2.equals("T"))
					retu = 0;
				if(s2.equals("C"))
					retu = -1;
				if(s2.equals("G"))
					retu =  -1;
			}else if(s1.equals("C")){
				if(s2.equals("A"))
					retu =  1;
				if(s2.equals("T"))
					retu =  1;
				if(s2.equals("C"))
					retu =  0;
				if(s2.equals("G"))
					retu =  -1;
			}else if(s1.equals("G")){
				if(s2.equals("A"))
					retu = 1;
				if(s2.equals("T"))
					retu = 1;
				if(s2.equals("C"))
					retu = 1;
				if(s2.equals("G"))
					retu = 0;
			}else{
				ToolBox.systmError("Comparator Failure", s1 + " vs " + s2);
				retu = 0;
			}
			return retu;

		}
	};


	/**
	 * Constructs a Codon object with the bases of the input string
	 * @param bases
	 */
	public Codon(String bases){
		if(bases.length() == BioConstants.CODON_LENGTH)
			this.bases = bases;
		else
			ToolBox.systmError("Codon must have "
					                   + BioConstants.CODON_LENGTH + " bases", bases);
	}

	/**
	 * Returns true if the input String or Codon
	 * has equivalent base sequence
	 */
	@Override
	public boolean equals(Object input){
		if(input instanceof Codon){
			if(this.bases.trim().equals(((Codon) input).getBases().trim()))
				return true;
		}else
			if(input instanceof String)
				if(this.bases.trim().equals(((String) input).trim()))
					return true;
		return false;
	}

	/**
	 * Changes one base randomly
	 */
	public void mutate() {
		while(true){
			String[] prev = this.bases.split("");

			Random random = new Random();
			int randtoMute = random.nextInt(prev.length);
			int randGetBase = new Random().nextInt(DNA.length);

			if(!prev[randtoMute].contains(DNA[randGetBase])){
				prev[randtoMute] = DNA[randGetBase];
				this.bases = String.join("", prev);
				System.out.println(this.bases);
				break;
			}
		}

	}

	public static void listingPrint(List<Codon> input){
		for(Codon c : input)
			System.out.println(c);
	}

	public String getBases()
	{
		return this.bases;
	}

	public void setBases(String bases)
	{
		this.bases = bases;
	}

	public float getScore()
	{
		return this.score;
	}

	public void setScore(float score)
	{
		this.score = score;
	}

	/**
	 * @param startStop -1,0,1
	 */
	public void setFunction(int startStop){
		this.function = startStop;
		if(startStop != 0)
			this.score = 0;

	}

	public boolean isStart(){
		if(this.function == 1)
			return true;
		else return false;
	}

	public boolean isStop(){
		if(this.function == -1)
			return true;
		else return false;
	}

	/**
	 * @return Exact copy of codon
	 */
	public Codon copy(){
		Codon retu = new Codon(this.bases);
		retu.setFunction(this.function);
		return retu;
	}

	@Override
	public String toString(){
		if(EvolutionRunner.DEBUG_CODON) return "{"+this.bases +"-"+ this.score+"}";
		else 					  return this.bases;
	}

}

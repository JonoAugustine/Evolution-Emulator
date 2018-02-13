package com.ampro.Evolution.Dna;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import com.ampro.main.BioConstants;
import com.ampro.main.EvolutionRunner;
import com.ampro.util.ToolBox;

/**
 * A Gene is a sequence of codons.
 * These codons of length CODON_LENGTH conjoin to make
 * a gene object's sequence
 * @author Jonathan Augustine
 *
 */
public class Gene extends BioConstants implements Iterable<Codon>{

	/**The minimum number of codons any gene can be*/
	private List<Codon> codons;
	private float score;
	private float[] influences;

	/**
	 * Constructs a Gene object with a list of scored codons.<br>
	 * Each gene has randomly assigned influences for each index in it's codon List.
	 * @param codons List
	 */
	public Gene(List<Codon> codons){
		if(codons.size() < MINIMUM_GENE_LENGTH)
			ToolBox.systmError("Gene object must have "+MINIMUM_GENE_LENGTH+" codons"
					, codons.toString());
		this.codons = codons;
		this.influences = new float[this.codons.size()];
		this.generateInfluences();
	}

	/**
	 * Genrates random influences pertaining to each codon's index
	 */
	private void generateInfluences(){
		float remaining = 1;
		if(EvolutionRunner.DEBUG_GENE)System.out.println("Remaining::influence[X]");
		this.influences[0] = (float) new Random().nextInt(100)/100;
		remaining -= this.influences[0];
		if(EvolutionRunner.DEBUG_GENE)System.out.println(remaining +"::"+this.influences[0]);
		for(int i=1; i < this.influences.length; i++){

			this.influences[i] = new Random().nextFloat();
			/*
			if(remaining *100 < 0.1f)
				break;
			if(i == this.influences.length-1)
				this.influences[i] = remaining;
			else
				try{//TODO !Gene Index Influence
					this.influences[i] = (float) new Random().nextInt(
							(int)(remaining*100))/100;
				}catch (Exception e) {
					break;
				}
			remaining -= this.influences[i];
			 */
			if(EvolutionRunner.DEBUG_GENE)System.out.println(remaining + "::" + this.influences[i]);
		}
	}

	/**
	 *
	 * @return the Gene's List of codons, Start to stop (inclusive)
	 */
	public List<Codon> getCodons(){
		return this.codons;
	}

	public void setCodons(ArrayList<Codon> codons){
		this.codons = codons;
	}

	public static void listingPrint(List<Gene> input){
		for(Gene c : input)
			System.out.println(c);
	}

	public float getScore() {
		return this.score;
	}


	public void setScore(float score) {
		this.score = score;
	}


	public float[] getInfluences() {
		return this.influences;
	}

	@Override public String toString(){
		return this.codons.toString();
	}

	@Override
	public Iterator<Codon> iterator() {
		return this.codons.iterator();
	}

}

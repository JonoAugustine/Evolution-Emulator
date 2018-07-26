package com.ampro.Evolution.Dna;

import java.util.ArrayList;
import java.util.List;

import com.ampro.main.BioConstants;
import com.ampro.util.ToolBox;

public class Chromatid extends BioConstants{

	private List<Codon> codons;
	private List<Gene> genes;
	private float score;

	/**
	 *
	 * @param sequence | List<\Codon> or String
	 */
	@SuppressWarnings("unchecked")
	public Chromatid(Object sequence){
		if(sequence instanceof String){
			this.codons = new ArrayList<>();
			if(((String) sequence).length() % CODON_LENGTH == 0)
				for(int i=0; i < ((String) sequence).length(); i+= CODON_LENGTH)
					this.codons.add(new Codon(((String) sequence).substring(i, i+CODON_LENGTH)));
		}else if(sequence instanceof ArrayList<?>){
			for(Object s : (List<?>) sequence) if(!(s instanceof Codon))
				ToolBox.systmError("Attempted Chromatid construction with non Codon List", sequence, s);
			this.codons = (List<Codon>) sequence;
		}
		//Empty Gene list to be built later
		this.genes = new ArrayList<>();
	}

	/**
	 * @return the codons
	 */
	public List<Codon> getCodons() {
		return this.codons;
	}

	/**
	 * @param codons the codons to set
	 */
	public void setCodons(List<Codon> codons) {
		this.codons = codons;
	}

	/**
	 * @return the score
	 */
	public float getScore() {
		return this.score;
	}

	/**
	 * @param score the score to set
	 */
	public void setScore(float score) {
		this.score = score;
	}

	/**
	 *
	 * @return
	 */
	public List<Gene> getGenes(){
		return this.genes;
	}

	public void setGenes(List<Gene> genes){
		this.genes = genes;
	}

	@Override
	public String toString(){
		if(this.genes.isEmpty()){
			String s = "";
			for(Codon c : this.codons)
				s += c.getBases();
			return s;
		}
		else
			return this.genes.toString();
	}

}

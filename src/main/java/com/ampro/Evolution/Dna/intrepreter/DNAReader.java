package com.ampro.Evolution.Dna.intrepreter;

import java.util.ArrayList;
import java.util.List;

import com.ampro.Evolution.Dna.Chromatid;
import com.ampro.Evolution.Dna.Chromosome;
import com.ampro.Evolution.Dna.Codon;
import com.ampro.Evolution.Dna.Gene;
import com.ampro.Evolution.Populations.Organisms.Organism;
import com.ampro.main.BioConstants;
import com.ampro.main.EvolutionRunner;
import com.ampro.util.ToolBox;

/**
 * Takes list of scored codons<br>
 * Each dnaReader pertains to a single species <p>
 *
 * Constructor:	public DNAReader(ArrayList<\Codon> scoredCodons)<br><br>
 * Methods: <br><br>
 * ->findStarts(Organism) : Returns an Array of each start codon's String index
 * in the Organism's genetic code.<br><br>
 *
 * ->disectCodons(Organism) : Returns a List of each codon in the DNA Sequence<br><br>
 *
 * ->findCodon(String or Codon) : Returns the index of the input codon
 * in the DNAReader's scoredCodons List <br><br>
 *
 * ->getCodon(String or Codon) : Returns the corresponding codon from the
 * Reader's coredCodons List<br><br>
 *
 * ->geneBuilder(Chromatid) : Parses through a chromatid's disected codons
 * and builds the chromatid's gene List
 *
 * ->geneScorer(Organism) :  Gives each gene in the input Organism gene list
 * a score based on the scores and influences of each codon in each gene
 *
 * @author Jonathan Augustine
 *
 *
 */
public class DNAReader extends BioConstants{

	protected List<Codon> scoredCodons;

	/**
	 * Constructs a DNA Reader<br>
	 * The input Codon array of scored codons is the reference
	 * for determining which codons are start or stop
	 * and the score for each codon is Organism gene scoring
	 */
	public DNAReader(List<Codon> scoredCodons){
		this.scoredCodons = scoredCodons;
		this.scoredCodons.sort(Codon.baseComparator);
	}

	/**
	 * @param Organism
	 * @return the highest possible score the organism can achieve
	 * , based on the DNAReader's scored codons
	 */
	protected double perfctFitness(Organism orgo){

		//TODO !!!perfctFitness method

		double retu = 0;


		return retu;
	}

	/**
	 * Returns a String List of the organism's genetic sequence
	 *
	 * @return ArrayList<\String>
	 */
	public static ArrayList<String> disectBases(Organism orgo) {
		ArrayList<String> sequence = new ArrayList<>();

		for(Chromosome co : orgo.getChromosomes())
			for(Chromatid ca : co.getChromatids())
				if(ca.getCodons().isEmpty())
					for(Gene g : ca.getGenes())
						for(Codon cn : g.getCodons())
							for(int a=0; a<CODON_LENGTH; a++)
								try{
									sequence.add(cn.getBases().substring(a,a+1));
								}catch (IndexOutOfBoundsException e) {
									sequence.add(cn.getBases().substring(a));
								}
				else
					for(Codon cn : ca.getCodons())
						for(int a=0; a<CODON_LENGTH; a++)
							try{
								sequence.add(cn.getBases().substring(a,a+1));
							}catch (IndexOutOfBoundsException e) {
								sequence.add(cn.getBases().substring(a));
							}

		return sequence;
	}

	/**
	 * Returns a List of each Codon in the organism's dna sequence
	 * in order of appearance
	 *
	 * TODO make codon diessection based on chromosomes
	 *
	 * @return ArrayList<\Codon>
	 */
	public static ArrayList<Codon> disectCodons(Object input){
		ArrayList<Codon> retu = new ArrayList<>();

		if(input instanceof Organism)
			return retu;
		else if(input instanceof Chromatid){
			//If the genes have been built
			if(((Chromatid) input).getCodons().isEmpty())
				for(Gene g :((Chromatid) input).getGenes())
					for(Codon c: g)
						retu.add(c);
			else{

			}
			return retu;
		}

		return retu;

	}

	/**
	 * Returns the index of the input codon in this.codons List
	 * @param input Codon or String
	 * @return index of equivalent codon in this.scoredCodons
	 */
	protected int findCodon(Object input){
		if(input instanceof Codon){
			for(int i=0; i < this.scoredCodons.size(); i++)
				if(((Codon) input).equals(this.scoredCodons.get(i)))
					return i;
		}else if(input instanceof String)
			for(int i=0; i < this.scoredCodons.size(); i++)
				if(((String) input).equals(this.scoredCodons.get(i)))
					return i;
		ToolBox.systmError("Codon "+input+" not found in "+ this.scoredCodons, input);
		return -1;
	}

	/**
	 * Returns the codon within this.codons that has the base equivalent
	 * of the input codon
	 * @param Codon or String
	 * @return
	 */
	protected Codon getCodon(Object input){
		if(input instanceof Codon || input instanceof String)
			return this.scoredCodons.get(this.findCodon(input));
		else ToolBox.systmError("getCodon must take a String or Codon parameter", input);
		return null;
	}

	/**
	 * Takes in an Chromatid object and parses the DNA sequence to build
	 * a List of the organism's genes
	 * @param orgo Organism
	 */
	public void geneBuilder(Chromatid chroma){
		List<Codon> codons = chroma.getCodons();
		ArrayList<Gene> genes = new ArrayList<>();
		//Cycles through Organism's disected codon List
		for(int i=0; i< codons.size(); i++)
			//if the current Codon in the List is a start codon
			if(codons.get(i).isStart()){
				//temp codon list to have current start-codon and proceeding codons
				//added to the next gene
				List<Codon> temp = new ArrayList<>();
				//Secondary cyle to add all codons to the temp array until a stop codon is reached
				for(int k=i; k < codons.size(); k++){
					temp.add(this.getCodon(codons.get(k)));
					i=k;
					if(codons.get(i).isStop()) break;
				}
				if(temp.size() >= Gene.MINIMUM_GENE_LENGTH)
					genes.add(new Gene(temp));
			}
		chroma.setGenes(genes);
		if(!genes.isEmpty())
			chroma.setCodons(null);
	}

	public void organismScorer(Organism orgo){
		for(Chromosome c : orgo.getChromosomes())
			for(Chromatid h : c){
				for(Gene x : h.getGenes())
					if(x.getScore() != 0)
						x.setScore(0);
				if(h.getScore() != 0)
					h.setScore(0);
			}
		double fitness = 0;
		for(Chromosome chromo : orgo.getChromosomes()){
			this.chromosomeScorer(chromo);
			for(int i=0; i < chromo.getChromatids().length; i++)
				fitness += chromo.getChromatids()[i].getScore();
		}
		orgo.setFitness(fitness);
	}

	/**
	 * Gives each gene in the input Chromatid's gene list a score based on
	 * the scores and influences of each codon in each gene
	 *
	 * @param Organism
	 */
	protected void geneScorer(Chromatid chroma){
		//Cycle through the genes
		/////
		if(EvolutionRunner.DEBUG_READER)
			System.out.println("A "+chroma.getGenes().size());
		/////
		for(int a=0; a < chroma.getGenes().size(); a++){
			////
			if(EvolutionRunner.DEBUG_READER)System.out.println("B "+a);
			////
			float tempScore = 0;
			//Cycle Through the codons in this gene
			for(int b=0; b < chroma.getGenes().get(a).getCodons().size();b++ ){
				////
				if(EvolutionRunner.DEBUG_READER){
					System.out.println();System.out.print(tempScore + " + ");
				}
				////
				tempScore += this.getCodon(chroma.getGenes().get(a).getCodons().get(b)).getScore()*
						chroma.getGenes().get(a).getInfluences()[b];
				////
				if(EvolutionRunner.DEBUG_READER)
					System.out.print(this.getCodon(chroma.getGenes().get(a).getCodons().get(b)).getScore()
							+ " x "+ chroma.getGenes().get(a).getInfluences()[b] +" = "+tempScore);
				////
			}
			chroma.getGenes().get(a).setScore(tempScore);
			////
			if(EvolutionRunner.DEBUG_READER) System.out.println(chroma.getGenes().get(a).getScore());
			////
		}
	}

	protected void chromatidScorer(Chromatid chroma){
		this.geneScorer(chroma);
		float temp = 0;
		for(Gene g : chroma.getGenes())
			temp += g.getScore();
		chroma.setScore(temp);
	}

	protected void chromosomeScorer(Chromosome chromo){
		for(int i=0; i < chromo.getChromatids().length; i++)
			this.chromatidScorer(chromo.getChromatids()[i]);
	}

	public void ChromosomeBuilder(Organism orgo){
		ArrayList<Chromosome> chromosomes = new ArrayList<>();

		//TODO ChromosomeBuilder?

		orgo.setChromosomes(chromosomes);

	}

	public static String rnaConversion(Organism orgo) {
		ArrayList<String> sequ = DNAReader.disectBases(orgo);
		String retu = "";
		for(String t : sequ)
			switch (t.toUpperCase()) {
				case "A":
					retu += "U";
					break;
				case "T":
					retu += "A";
					break;
				case "C":
					retu += "G";
					break;
				case "G":
					retu += "C";
					break;
			}
		return retu;
	}

	public static String dnaComplement(Organism orgo) {
		ArrayList<String> sequ = DNAReader.disectBases(orgo);
		String retu = "";
		for(String t : sequ)
			switch (t.toUpperCase()) {
				case "A":
					retu += "T";
					break;
				case "T":
					retu += "A";
					break;
				case "C":
					retu += "G";
					break;
				case "G":
					retu += "C";
					break;
			}
		return retu;
	}

	@Override
	public String toString(){
		return this.scoredCodons.toString();
	}

	public DNAReader copy() {
		return new DNAReader(this.scoredCodons);
	}

}

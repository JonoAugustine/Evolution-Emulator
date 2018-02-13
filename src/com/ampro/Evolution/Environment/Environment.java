package com.ampro.Evolution.Environment;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import com.ampro.Evolution.Dna.Codon;
import com.ampro.Evolution.Dna.intrepreter.DNAReader;
import com.ampro.Evolution.Populations.Population;
import com.ampro.Evolution.Populations.Organisms.Organism;
import com.ampro.Evolution.Populations.Organisms.Predators.Predator;
import com.ampro.Evolution.Populations.Organisms.producers.Producer;
import com.ampro.main.BioConstants;

/**
 * A base object for different types of environments<p>
 * An environment contains the resource availabilities
 * and a "difficulty" level
 * defining how often cataclismic die-off events occur
 * <br>
 *
 * @author Jonathan Augustine
 *
 */
public class Environment extends BioConstants implements Iterable<Population>{

	private ArrayList<Population> populations;

	private List<Codon> defaultScoredCodon = allPossibleCodons;
	private List<Codon> producerScoredCodon = codonScorer();
	private List<Codon> predatorScoredCodon = codonScorer();
	/** Default, prod, pred*/
	DNAReader[] DnaReaders;


	public Environment(ArrayList<Population> populations){
		this.populations = populations;
	}

	public Environment(){
		this.populations = new ArrayList<>();
	}

	private static ArrayList<Codon> codonScorer(){//int[] scoreProbabilites) {

		ArrayList<Codon> retu = new ArrayList<>();
		for(Codon codon : allPossibleCodons)
			retu.add(codon.copy());

		float[] scores = new float[Math.abs(SCORE_RANGE[0]) + SCORE_RANGE[1]];
		for(int i=0; i < scores.length; i++)
			scores[i] = SCORE_RANGE[0] + i + new Random().nextFloat();
		for (Codon t : retu)
			t.setScore(scores[new Random().nextInt(scores.length)]);

		return retu;
	}

	/**
	 * Returns the environment's appropriate DNAReader for the Organism
	 * @param {@link Organism}
	 * @return {@link DNAReader}
	 */
	public DNAReader reader(Organism orgo){
		if(orgo instanceof Predator)
			return this.DnaReaders[2];
		if(orgo instanceof Producer)
			return this.DnaReaders[1];
		else
			return this.DnaReaders[0];
	}

	public void cataclism(int scale){
		//TODO Create Cataclism method
	}

	public void setReaders(DNAReader...readers){
		this.DnaReaders = readers;
	}

	public void setReader(int index, DNAReader reader){
		this.DnaReaders[index] = reader;
	}


	/**
	 *
	 * @return ArrayList of the environments populations
	 */
	public ArrayList<Population> getPopulations(){
		return this.populations;
	}

	public void setPopulations(ArrayList<Population> populations){

	}

	public void add(Population population){
		this.populations.add(population);
	}

	/**
	 * @return Number of populations
	 */
	public int size() {
		return this.populations.size();
	}

	/**
	 * @return the producerScoredCodon
	 */
	public List<Codon> getProducerScoredCodon() {
		return this.producerScoredCodon;
	}



	/**
	 * @return the predatorScoredCodon
	 */
	public List<Codon> getPredatorScoredCodon() {
		return this.predatorScoredCodon;
	}

	public void resetScore(){

	}


	@Override
	public Iterator<Population> iterator() {
		return this.populations.iterator();
	}

	@Override
	public String toString(){
		return this.populations.toString();
	}



}

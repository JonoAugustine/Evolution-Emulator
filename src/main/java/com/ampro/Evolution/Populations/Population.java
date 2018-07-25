package com.ampro.Evolution.Populations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import com.ampro.Evolution.SimpleEvolutionEmulator;
import com.ampro.Evolution.Dna.Chromatid;
import com.ampro.Evolution.Dna.Chromosome;
import com.ampro.Evolution.Populations.Organisms.Organism;
import com.ampro.Evolution.Populations.Organisms.Predators.Predator;
import com.ampro.Evolution.Populations.Organisms.producers.Producer;
import com.ampro.main.BioConstants;
import com.ampro.main.EvolutionRunner;
import com.ampro.util.ToolBox;

/**
 * This class defines the object Population for use in the
 * {@link SimpleEvolutionEmulator} </br>
 *
 * A population consists of a group of {@link Organism}s
 *
 * @author Jonathan Augustine
 *
 */
public class Population extends BioConstants implements Iterable<Organism>{
	private ArrayList<Organism> population;
	private String generation;
	private String name;
	/**What organism is this population made of*/
	private Class<? extends Organism> populationType;

	/**
	 * Compares organism in priority <br>
	 *
	 * isAlive<br>
	 * getGeneration<br>
	 * getAge<br>
	 * getName<br>
	 * getFitness<br>
	 */
	public static Comparator<Organism> truePopulationComparator = new Comparator<Organism>(){
		@Override public int compare(Organism o1, Organism o2){
			return o1.compareTo(o2);
		}
	};

	/**
	 * Constructs a new Population object
	 *
	 * @param population
	 *            ArrayList<\Organism>
	 */
	public Population(String name, String generation, ArrayList<Organism> population) {
		if(name == null) this.generateName();
		this.generation = generation;
		this.population = new ArrayList<>();
		for (Organism orgo : population){
			orgo.setGeneration(generation);
			this.population.add(orgo);
		}
		if(!population.isEmpty()) this.population.sort(truePopulationComparator);
		try{
			this.populationType = population.get(0).getClass();
		}catch(IndexOutOfBoundsException e){}
	}

	/**
	 * Returns a array of Organisms produced by combining the chromatids of 2 parent organisms
	 * @param numOffspring
	 * @param maxChildrenPerPair
	 * @return ArrayList<\Organism>
	 */
	public ArrayList<Organism> matingSeason(int numOffspring, int maxChildrenPerPair, int minAge) {

		//New List of organisms to be produced through reproduction
		ArrayList<Organism> offspring = new ArrayList<>();
		//Add current population to temp List for manipulation
		ArrayList<Organism> parents = new ArrayList<>();
		for (Organism o : this.population)
			if(o.getAge() >= minAge) parents.add(o.copy());

		List<int[]> pairs = new ArrayList<>();

		for(int i=0; i < ToolBox.permute(this.population.size(), 2); i++)
			for(int k=i+1; k < this.population.size(); k++)
				pairs.add(new int[]{i,k});

		if(maxChildrenPerPair != 0){
			List<int[]> tempPairs = new ArrayList<>();
			for(int i=0; i < maxChildrenPerPair; i++)
				for(int k=0; k < pairs.size(); k++)
					tempPairs.add(new int[]{pairs.get(k)[0], pairs.get(k)[1]});
			pairs.addAll(tempPairs);
		}


		if(EvolutionRunner.DEBUG_MATING){
			for(int[] i : pairs)
				System.out.println("Mating Pair  "+i[0] +"|"+i[1]);
			System.out.println(pairs.size() + " pairs");
		}

		for(int i=0; i < numOffspring; i++){

			/*
			if(pairs.isEmpty()){
				for(int A=0; A < ToolBox.permute(this.population.size(), 2); A++)
					for(int k=A+1; k < this.population.size(); k++)
						pairs.add(new int[]{A,k});
				if(maxChildrenPerPair != 0){
					List<int[]> tempPairs = new ArrayList<>();
					for(int B=0; B < maxChildrenPerPair; B++)
						for(int k=0; k < pairs.size(); k++)
							tempPairs.add(new int[]{pairs.get(k)[0], pairs.get(k)[1]});
					pairs.addAll(tempPairs);
				}

				if(EvolutionRunner.DEBUG_MATING){
					for(int[] V : pairs)
						System.out.println("Mating Pairs  "+V[0] +"|"+V[1]);
					System.out.println(pairs.size() + " pairs");
				}
			}
			 */
			//get pair
			int[] pair;
			int index;
			try{
				index = new Random().nextInt(pairs.size());
				pair = pairs.get(index);
			}catch (IllegalArgumentException e) {
				continue;
			}
			Organism parent_1 = this.population.get(pair[0]);
			Organism parent_2 = this.population.get(pair[1]);
			//if they aren't sexual
			if(!parent_1.isSexual() || !parent_2.isSexual()
					|| parent_1.getAge() < minAge || parent_2.getAge() < minAge){
				pairs.remove(index);
				if(i!=0) i--;
				else 	 i=0;
				continue;
			}
			//If they dont have the same number of chromosomes they cant breed
			if(parent_1.getChromosomes().size() != parent_2.getChromosomes().size()){
				if(i!=0) i--;
				else 	 i=0;
				continue;
			}

			//generate child chromosomes from random parent chromatids
			ArrayList<Chromosome> zygote = new ArrayList<>();
			for(int a=0; a < parent_1.getChromosomes().size(); a++){

				Chromatid[] tempCha = new Chromatid[CHROMATID_per_CHROMOSOME];

				for(int k=0; k < CHROMATID_per_CHROMOSOME; k+=2){
					tempCha[k] = parent_1.getChromosomes().get(a).getChromatids()[new Random().nextInt(CHROMATID_per_CHROMOSOME)];
					tempCha[k+1] = parent_2.getChromosomes().get(a).getChromatids()[new Random().nextInt(CHROMATID_per_CHROMOSOME)];
				}

				zygote.add(new Chromosome(tempCha));
			}

			//Setting up the child
			Organism child = null;
			//Generation
			int generation = 0;
			if(parent_1.getGeneration() != null && parent_1.getGeneration() != null)
				generation = Math.max(parent_1.getGenNum(), parent_2.getGenNum()) + 1;

			//Construct child Organism
			if(parent_1 instanceof Producer)
				child = new Producer("F"+generation, null, zygote, true);
			else if(parent_1 instanceof Predator)
				child = new Predator("F"+generation, null, zygote, true);
			else
				child = new Organism("F"+generation, null, zygote, true);

			//set parents
			child.setParents(parent_1, parent_2);

			//add organism to return
			offspring.add(child);

		}

		return offspring;

	}

	/**
	 * Removes all dead organisms from the Population
	 */
	public void clean(){
		this.population.sort(truePopulationComparator);
		for(int i=0; i < this.population.size();)
			if(!this.population.get(i).isAlive())
				this.population.remove(i);
			else break;
	}

	private float  averageFitness() {
		float sum = 0;
		for(Organism orgo : this.population)
			sum+=orgo.getFitness();
		return sum/this.population.size();
	}

	private float standardDeviation(){
		float sum = 0;
		for(int i=0; i < this.population.size(); i++)
			sum += (this.population.get(i).getFitness() - this.averageFitness())
			*(this.population.get(i).getFitness() - this.averageFitness());
		float varience = sum / this.population.size();
		float deviation = (float) Math.sqrt(varience);
		return deviation;
	}

	/**
	 * @return The Organism List of the population
	 */
	public ArrayList<Organism> getPopulation() {
		return this.population;
	}

	public void setPopulation(ArrayList<Organism> population) {
		this.population = population;
	}

	public void add(Organism orgo) {
		this.population.add(orgo);
	}

	public void addAll(Collection<? extends Organism> population){
		this.population.addAll(population);
		this.population.sort(truePopulationComparator);
	}

	public Organism get(int index){
		return this.population.get(index);
	}

	public void sort(Comparator<? super Organism> comparator){
		this.population.sort(comparator);
	}

	/**
	 * @return name
	 */
	public String getName() {
		return this.name;
	}

	public String getGeneration() {
		return this.generation;
	}

	public void setGeneration(String generation) {
		this.generation = generation;
	}

	/**
	 * @return The number of the generation
	 */
	public int getGenNum(){
		return StringParsing.letterRemoval(this.generation);
	}

	public float getAverageFitness() {
		return this.averageFitness();
	}

	/**
	 * @return Standard deviation of the population's fitness scorings
	 */
	public float getScoreDeviation() {
		return this.standardDeviation();
	}

	public int size() {
		return this.population.size();
	}

	//TODO Move the generate name method to Environment class so that each Enviro names it's population independently
	/**
	 * Generates a string in for "A_01234" to use as a name for Populations not
	 * given names during construction
	 *
	 * @return "name" String
	 */
	protected void generateName()
	{
		String name = "";
		if (this.generation != null)
			name += this.generation + "-";
		try{
			name += ALPHABET.substring(P_NEXT_NAME_LETTER_INDEX, P_NEXT_NAME_LETTER_INDEX + 1);
		} catch (IndexOutOfBoundsException e){
			name += ALPHABET.substring(P_NEXT_NAME_LETTER_INDEX);
			P_NEXT_NAME_LETTER_INDEX = 0;
			try	{
				name += "_" + ALPHABET.substring(P_NEXT_NAME_LETTER_INDEX, P_NEXT_NAME_LETTER_INDEX + 1);
				;
			} catch (IndexOutOfBoundsException e2){
				name += "_" + ALPHABET.substring(P_NEXT_NAME_LETTER_INDEX);
				P_NEXT_NAME_LETTER_INDEX = 0;
				P_NEXT_NAME_NUMBER = 0;
			}
		}

		name += "_";
		for (int i = 5; i > new String("" + P_NEXT_NAME_NUMBER + "").length(); i--)
			name += "0";
		name += NEXT_NAME_NUMBER++;
		if (P_NEXT_NAME_NUMBER == 99999){
			P_NEXT_NAME_NUMBER = 0;
			P_NEXT_NAME_LETTER_INDEX++;
		}

		this.name = "Population:" + name;
	}

	@Override
	public Iterator<Organism> iterator() {
		return this.population.iterator();
	}

	public void listPrint(){
		for(Organism o : this.population)
			System.out.println(o);
	}

	@Override
	public String toString() {
		return this.name + " |Size=" +this.size()
		+"|AvgScore="+this.getAverageFitness()
		+"|ScoreDev="+this.getScoreDeviation()
		;

	}

	/**
	 * @return true if the population has no organisms
	 */
	public boolean isEmpty() {
		return this.population.isEmpty() || this.population.size() == 0;
	}


}

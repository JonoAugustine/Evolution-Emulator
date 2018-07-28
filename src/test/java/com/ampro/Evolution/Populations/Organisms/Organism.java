package com.ampro.Evolution.Populations.Organisms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

import com.ampro.Evolution.Dna.Chromatid;
import com.ampro.Evolution.Dna.Chromosome;
import com.ampro.Evolution.Dna.Codon;
import com.ampro.Evolution.Dna.Gene;
import com.ampro.Evolution.Populations.Organisms.descriptors.Type.Type;
import com.ampro.main.BioConstants;

/**
 * This class defines the object Organism <br><br>
 *
 * An Organism contains variables:<br>
 *
 * <h2>------Naming Related------</h2>
 *
 *	String name: lowerCase name of the Organism
 * *
 *	<h2>-----Genetics Related Variables----</h2>
 *	String geneticCode: upperCase gene sequence of the Organism <br>
 *	static int chromosomesNum: <br>
 *	ArrayList<Chromosome> chromosomes: The collection of the Organism's Chromosomes
 *
 *	<br> final boolean sexual: Whether the organism reproduces asexually or not <br>
 *
 *	<h2>------High Level environment interaction variables------</h2>
 *
 *	String generation: The generation delegation of the organism <br>
 *  double fitness: The organism's fitness rating (likely-hood of survival) <br>
 * 	boolean alive: Whether the organism is considered dead or alive <br>
 *	private int age; <br>
 *	protected Type type
 *
 * @author Jonathan Augustine
 *
 */
public class Organism extends BioConstants
{
	// -------Naming Related Variables------
	/** A lowerCase name for the Organism */
	protected String name;
	// -------------------------------------

	// --------Genetics Related Variables----
	/** An upperCase gene sequence for the Organism */
	protected static int chromosomesNum = NUMBER_STARTING_DEFAULT_CHROMOSOME;
	protected static int[] chromatidLength = CHROMATID_LENGTH_RANGE;
	protected ArrayList<Chromosome> chromosomes = new ArrayList<>();
	// ------------------------
	/** Whether the organism reproduces asexually or not */
	private final boolean sexual;
	// ------High Level environment interaction variables------
	/** The generation delegation of the organism */
	protected String generation;
	/** The organism's fitness rating (likely-hood of survival) */
	protected double fitness;
	protected double perfectFitness;
	/** Whether the organism is considered dead or alive */
	protected boolean alive;
	private int age;
	protected Type type;
	private Organism[] parents;

	/**
	 *  * Constructs an Organism object with name "name" and genetics "geneticCode"
	 * </br>
	 * If the name parameter is given as null, the Organism's name will be
	 * randomly generated </br>
	 * the generated name will be in the form "AB_01234"
	 *
	 * @param generation
	 * @param name
	 * @param chromosomes
	 * @param sexual
	 */
	public Organism(String generation, String name, ArrayList<Chromosome> chromosomes,
	                Boolean sexual) {
		this.alive = true;
		this.age = 0;
		this.generation = generation;
		if (name != null)
			this.name = name;
		else
			this.name = this.generateName();
		if (chromosomes != null)
			this.chromosomes = chromosomes;
		else
			this.generateChromosomes();
		this.sexual = sexual;
	}

	/**
	 * Constructs an organism with
	 * this.alive = true;
	 * this.age = 0;
	 * this.name = this.generateName();
	 * Sequential name this.generation = null;
	 * this.geneticCode = this.generateCode();
	 * Random generation random genetic code,
	 * null generation
	 * sexuallyProduced = false
	 */
	public Organism(){
		this.alive = true;
		this.age = 0;
		this.name = this.generateName();
		this.generation = null;
		this.generateChromosomes();
		this.sexual = false;
	}

	/**
	 * Generates a random List of chromosomes<br>
	 * Each with chromatids of length within CHROMATID_LENGTH_RANGE[0]
	 *
	 * @return ArrayList<\Chromosome>
	 */
	protected void generateChromosomes() {

		ArrayList<Chromosome> chromos = new ArrayList<>();

		for(int i=0; i <chromosomesNum; i++){
			Chromatid[] tempCha = new Chromatid[CHROMATID_per_CHROMOSOME];

			for(int k=0; k < CHROMATID_per_CHROMOSOME; k++){
				ArrayList<Codon> tempCod = new ArrayList<>();
				for(int v=0; v < chromatidLength[0] + new Random().nextInt(chromatidLength[1]+1); v++)
					tempCod.add(allPossibleCodons.get(new Random().nextInt(allPossibleCodons.size())).copy());
				for(Codon c : allPossibleCodons)
					if(c.isStop()) {
						tempCod.add(allPossibleCodons.get(new Random().nextInt(allPossibleCodons.size())).copy()) ;
						break;
					}

				tempCha[k] = new Chromatid(tempCod);
			}
			chromos.add(new Chromosome(tempCha));
		}

		this.chromosomes = chromos;
	}

	/**
	 * Generates a string in for "A_01234" to use as a name for Organisms not
	 * given names during construction
	 *
	 * @return "name" String
	 */
	protected String generateName()
	{
		String name = "";
		if (this.generation != null)
			name += this.generation + "-";
		try{
			name += ALPHABET.substring(NEXT_NAME_LETTER_INDEX, NEXT_NAME_LETTER_INDEX + 1);
		} catch (IndexOutOfBoundsException e){
			name += ALPHABET.substring(NEXT_NAME_LETTER_INDEX);
			NEXT_NAME_LETTER_INDEX = 0;
			try	{
				name += "_" + ALPHABET.substring(NEXT_NAME_LETTER_INDEX, NEXT_NAME_LETTER_INDEX + 1);
				;
			} catch (IndexOutOfBoundsException e2){
				name += "_" + ALPHABET.substring(NEXT_NAME_LETTER_INDEX);
				NEXT_NAME_LETTER_INDEX = 0;
				NEXT_NAME_NUMBER = 0;
			}
		}

		name += "_";
		for (int i = 5; i > new String("" + NEXT_NAME_NUMBER + "").length(); i--)
			name += "0";
		name += NEXT_NAME_NUMBER++;
		if (NEXT_NAME_NUMBER == 99999){
			NEXT_NAME_NUMBER = 0;
			NEXT_NAME_LETTER_INDEX++;
		}

		return name;
	}

	/**
	 * Sets the organism's alive status to false
	 */
	public void die(){
		this.alive = false;
	}

	public boolean isAlive(){
		if (this.alive)
			return true;
		else
			return false;
	}

	/**
	 * Returns an exact copy of the Organism
	 * @return Organisms
	 */
	public Organism copy(){
		return new Organism(this.generation, this.name, this.chromosomes, this.sexual);
	}

	public double getFitness(){
		return this.fitness;
	}

	public void setFitness(double d)
	{
		this.fitness = d;
	}

	public String getName(){
		return this.name;
	}

	public void setName(String name){
		this.name = name;
	}

	public boolean isSexual(){
		return this.sexual;
	}

	public Type getType(){
		return this.type;
	}

	public ArrayList<Chromosome> getChromosomes(){
		return this.chromosomes;
	}

	public void setChromosomes(ArrayList<Chromosome> chromosomes){
		this.chromosomes = chromosomes;
	}

	/**
	 * @return the age
	 */
	public int getAge(){
		return this.age;
	}

	/**
	 * @param age
	 *            the age to set
	 */
	public void setAge(int age){
		this.age = age;
	}

	/**
	 * Set's the organism's parent organisms
	 * @param parents
	 */
	public void setParents(Organism...parents){
		this.parents = parents;
	}

	/**
	 * @return the generation
	 */
	public String getGeneration(){
		return this.generation;
	}

	/**
	 * @return The number of the generation
	 */
	public int getGenNum(){
		return StringParsing.letterRemoval(this.generation);
	}

	/**
	 * @param generation
	 *            the generation to set
	 */
	public void setGeneration(String generation)
	{
		this.generation = generation;
	}

	/**
	 * @return
	 */
	public String getGeneticCode() {
		String retu = "";

		for(Chromosome cro : this.chromosomes)
			for(Chromatid cra : Arrays.asList(cro.getChromatids()))
				if(cra.getGenes() != null && !cra.getGenes().isEmpty())
					retu += cra.toString();
				else
					for(Codon cd : cra.getCodons())
						retu += cd;

		return retu;
	}

	@Override
	public String toString(){
		return this.name + "|Alive:" + this.alive + "|Age:" + this.age + "|Type:" + this.type + "|Sexual:"
				+ this.sexual + "|Fitness:" + this.fitness + "[" + this.chromosomes + "]";
	}

	/**
	 * Compares organism in priority <br>
	 *
	 * isAlive<br>
	 * getGeneration<br>
	 * getAge<br>
	 * getName<br>
	 * getFitness<br>
	 */
	public int compareTo(Organism o) {
		return Comparator.comparing(Organism::isAlive)
				.thenComparing(Organism::getGeneration)
				.thenComparingInt(Organism::getAge)
				.thenComparing(Organism::getName)
				.thenComparingDouble(Organism::getFitness)
				.compare(this, o);
	}

	public int baseCount(){
		int retu = 0;

		for(Chromosome chromo : this.chromosomes)
			for(Chromatid chroma : chromo)
				if(chroma.getGenes().isEmpty() || chroma.getGenes() == null)
					for(Codon codon : chroma.getCodons())
						retu += codon.getBases().length();
				else
					for(Gene gene : chroma.getGenes())
						for(Codon codon : gene)
							retu += codon.getBases().length();
		return retu;
	}

	/**
	 * @return
	 */
	public boolean isPerfect() {
		if((int)this.perfectFitness == (int)this.fitness) return true;
		else return false;
	}


}

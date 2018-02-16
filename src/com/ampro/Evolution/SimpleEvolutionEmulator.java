package com.ampro.Evolution;

import java.util.ArrayList;
import java.util.Random;

import com.ampro.Evolution.Dna.Chromatid;
import com.ampro.Evolution.Dna.Chromosome;
import com.ampro.Evolution.Dna.Codon;
import com.ampro.Evolution.Dna.intrepreter.DNAReader;
import com.ampro.Evolution.Environment.Environment;
import com.ampro.Evolution.Populations.Population;
import com.ampro.Evolution.Populations.Organisms.Organism;
import com.ampro.main.BioConstants;
import com.ampro.util.Timer;
import com.ampro.util.*;

/**
 * A class containing Essential data for Evolution Emulation
 * <p>
 *
 * 4 main methods:<br>
 * mutate(boolean DNA, String input, int seconds): <br>
 * geneAffectGenerator:<br>
 * rnaConversion:<br>
 *
 * @author Jonathan Augustine
 */
public class SimpleEvolutionEmulator extends BioConstants implements Runnable {

	Environment environment;

	Timer fullTimer;
	Timer modTimer;
	
	FileWriter Log;

	//DataDisplay[] displays;


	@Override
	public void run() {
		//for (DataDisplay display : this.displays)
		//this.displays[0].display();
		
		this.Log.addLines("Initiallizing Timers");
		this.fullTimer = new Timer();
		this.modTimer = new Timer();

		this.Log.addLines("Generating Codon Scores");
		codonScorer();
		
		System.out.println(allPossibleCodons);
		System.out.println(this.environment.getProducerScoredCodon());
		System.out.println(this.environment.getPredatorScoredCodon());
		ToolBox.sleep(3);
		
		this.Log.addLines("Creating new DNA Reader");
		DNAReader reader = new DNAReader(allPossibleCodons);

		this.modTimer.reset();
		
		this.Log.addLines("Building Initial Population genes and scoring");
		for (Population p : this.environment){
			this.modTimer.reset();
			for (Organism o : p) {
				for (Chromosome s : o.getChromosomes())
					for (Chromatid c : s)
						reader.geneBuilder(c);
				reader.organismScorer(o);

			}

			System.out.println("\n"+p);
			for(Organism o: p)
				System.out.println(o);
			
			String timer = this.modTimer.reset();
			this.Log.addLines("\t Population " + p.getName() + " | Duration " + timer);
			System.out.println("Cycle Duration>>>"+timer);
			System.out.println("Full Run Duration>>>"+this.fullTimer);
			System.out.println();

		}
		
		this.Log.addLines("Initial populations built and scored.", "\t Run time at: " + this.fullTimer);

		ToolBox.sleep(10);
		//--
		long[] cycleDurations = new long [100];
		ArrayList<Long> populationCycleDurations = new ArrayList<>();

		Timer cycleTimer = new Timer();
		this.modTimer.reset();
		
		this.Log.addLines("Beginig environment emulation...");
		/** The Meat and bones. F1 generations onwards */
		for (int i = 0; i < 100; i++) {
			
			this.Log.addLines("\tYear " + (i+1) );
			System.out.println("Cycle " + (i+1) + "\n");
			
			//For each population, mate and score all new organisms 
			for (Population p : this.environment) {
				this.Log.addLines("\t\tPopulation " + p + "...");
				this.modTimer.reset();
				if(p.isEmpty()) {
					this.Log.addLines("\t\tPupulation is empty" );
					continue;
				}
				if(p.size() < 2){
					for(Organism o : p)
						o.die();
					this.Log.addLines("\t\tPupulation died out" );
					continue;
				}
				System.out.println(p);
				
				float preMate = p.getAverageFitness();
				this.Log.addLines("\t\tAverage Fitness (before mating): " + preMate);
				this.Log.addLines("\t\tMating Season beginning..."
									, "\t\t\tNumber of Offspring = " + (p.size()/2)
									, "\t\t\tChildren per pair = " + 4
									, "\t\t\tMin Age of Parent = " + 5
								);
				p.addAll(p.matingSeason(p.size()/2, 4, 5));
				this.Log.addLines("\t\tMating Season Completed.");
				
				float postMate = p.getAverageFitness();
				this.Log.addLines("\t\tAverage Fitness (after mating): " + postMate);
				p.sort(Population.truePopulationComparator);
				for (Organism o : p) {
					reader.organismScorer(o);
					//System.out.println(o);
					if(		   o.getFitness() < p.getAverageFitness() - 3*p.getScoreDeviation()
							|| o.getFitness() > p.getAverageFitness() + 3*p.getScoreDeviation()
							|| o.getAge() >= 20
							|| o.getFitness() <= 0)
						o.die();
					o.setAge(o.getAge()+1);
				}
				p.clean();
				this.Log.addLines("\t\t" + p);
				System.out.println(p.getName() + "\nsize><><" + p.size()
				+"\nAvrage Fitness: \npre mating>>"+preMate);
				System.out.println("post mating>>"+postMate);
				System.out.println("post cull>>"+p.getAverageFitness());
				populationCycleDurations.add(this.modTimer.getElapsedTime());
				System.out.println("Avreage Population Cycle duration<<<<>>>>" + Timer.average(populationCycleDurations));
				System.out.println(p.getName() + " Cycle "+i+" Duration<<<<>>>>"+this.modTimer.reset());
				System.out.println("Full Run Duration<<<>>>>"+this.fullTimer);
				System.out.println("Current Time<<<<>>>>" + ToolBox.getCurrntTime());
				System.out.println();
				ToolBox.sleep(1);
				//this.displays[0].update(p.getName(), i+1, new double[]{.3*p.size(),p.size()});
			}
			cycleDurations[i] = cycleTimer.getElapsedTime();
			System.out.println("Cycle " + (i+1) + " duration<<<<>>>>" + cycleTimer.reset());
			System.out.println("Avreage Cycle duration<<<<>>>>" + Timer.average(cycleDurations));
			System.out.println("Full Run Duration<<<>>>>"+this.fullTimer);
			System.out.println("Current Time<<<<>>>>" + ToolBox.getCurrntTime());
			System.out.println();
		}

		this.Log.addLines("Full Run Time : " + this.fullTimer);
		System.out.println("\n\nDONE");
		System.exit(0);
		///////////////////// TODO !!Write the process//////////////////////////

		//
		//
		//

		//TODO What to do if population in environment is empty?
		for(Population population : this.environment)
			if(population.isEmpty()){
			}

		ArrayList<Organism> bestOrganisms = new ArrayList<>();

		for(int i=0; i < this.environment.getPopulations().size(); i++){
			Organism tempBest = new Organism();
			tempBest.setFitness(SCORE_RANGE[0]-1);
			bestOrganisms.add(tempBest);
		}

		ArrayList<Boolean> seeking = new ArrayList<>();

		for(int i=0; i < this.environment.getPopulations().size(); i++)
			seeking.add(true);

		//
		for(boolean searching=true; searching;)
			for(int i=0; i < this.environment.getPopulations().size(); i++){
				//Skips this population if the perfect being has already been found
				if(!seeking.get(i))
					continue;

				//Set current population to something a bit more handy
				Population population = this.environment.getPopulations().get(i);

				//Reset DNAReaders to represent a change in what makes a gene fit
				if (i % 100 + i == 0) {
					System.out.println("Generating New DNA Readers");
					this.environment.setReaders(new DNAReader[]{new DNAReader(allPossibleCodons)
							, new DNAReader(this.environment.getProducerScoredCodon())
							, new DNAReader(this.environment.getPredatorScoredCodon())
					});
					System.out.println();
				}


				//

				//Mating Season
				population.addAll(population.matingSeason(population.size()/2, 10, 10));

				//Mutate the population
				for(int k=0; k < population.size(); k++){
					Organism temp = population.get(new Random().nextInt(population.size()));
					mutate(temp,true,new Random().nextInt((int) (temp.baseCount()*0.05)), false);
				}

				//DNAReader Fitness Scoring
				for(Organism orgo: population)
					this.environment.reader(orgo).organismScorer(orgo);

				//
				//
				//

				//
				//
				//

				//

				//Set best Organism
				for(Organism orgo : population){
					if(orgo.getFitness() > bestOrganisms.get(population.getGenNum()).getFitness())
						bestOrganisms.set(population.getGenNum(), orgo);
					//If perfect Orgo is found
					if(orgo.isPerfect())
						//Set this population's seeking to false to skip
						seeking.set(i, false);
				}

				//Age all organisms by one unit
				for(Organism orgo : population)
					orgo.setAge(orgo.getAge()+1);

				//Kill any organism too old
				for(Organism orgo : population)
					if(orgo.getAge() > 20)
						orgo.die();
				//				else if(/*TODO #Culling Parameters*/){}

				population.clean();
			}


	}

	/**
	 *
	 */
	public SimpleEvolutionEmulator(Environment enviro) {
		this.environment = enviro;
		this.Log = new FileWriter("EmulatorLog");
		//this.displays = new DataDisplay[this.environment.size()];
		//this.displays[0] = new DataDisplay("Population Size");
		//this.displays[0].setUp("Year", "Size (Organism)", this.environment.getPopulations().get(0).getName()
			//	, new double[]{0,1}, new double[]{0,this.environment.getPopulations().get(0).size()});
		//for(int i=1; i < this.environment.size(); i++)
			//this.displays[0].addGraph(this.environment.getPopulations().get(i).getName(), new double[]{
				//	0//				this.environment.getPopulations().get(i).size()
			//});
	}

	private void set() {

	}


	// TODO !Should we separate each stage of the simulation into different methods
	// centered around the parameterized environment?
	private void todo() {

		// DNA Reader

		//

		// Innoculate
		this.environment.getPopulations().add(new Population(null, "P", introduce(100)));

		//mutate Parents
		for (Population p : this.environment.getPopulations())
			for (Organism orgo : p.getPopulation())
				mutate(p.getPopulation().get(new Random().nextInt(p.size())),
						true,
						new Random().nextInt(
								orgo.getChromosomes().size() * new Random()
								.nextInt(orgo.getChromosomes().size())),
						false);

		Organism best = new Organism();
		best.setFitness(0);


		boolean searchingforPerfectBeing = true;
		outerloop:
			for (int i = 1; searchingforPerfectBeing; i++) {
				//dna readers
				if (i % 100 + i == 0) {
					System.out.println("Generating New DNA Readers");
					// Generate new Readers
					System.out.println();
				}

				//alter dna length
				if (i % 1000 == 0) {
					DNA_LENGTH = DNA_LENGTH
							+ new Random().nextInt(DNA_LENGTH / 10)
							- new Random().nextInt(DNA_LENGTH / 10);
					System.out.println(
							"New Genetic Sequence Length>> " + DNA_LENGTH + "\n");
				}
				Population filial = null;
				String generation = "F" + i;

				System.out
				.println("\nGenerating Filial Populations\n" + generation);
				if (i == 1) {
					// Generate First Filial populations
				} else {
					// Generate Filial populations
				}

				// Print Pre-Mutation averages
				System.out.println("\nPre-Mutation averages ");

				System.out.println("\nMutating Populations Organisms\n");
				for (int k = 0; k < filial.getPopulation().size(); k++)
					SimpleEvolutionEmulator.mutate(
							filial.getPopulation()
							.get(new Random().nextInt(
									filial.getPopulation().size())),
							true, DNA_LENGTH / 6, true);

				System.out.println("\nPre-Culling average " + generation
						+ " Organism Score>>" + filial.getAverageFitness());
				System.out.println("\nCulling " + filial.getGeneration()
				+ "\nPopulation Size before culling>>"
				+ filial.getPopulation().size());
				if (i % 1000 == 0) {
					int numToCull = filial.getPopulation().size()
							/ (5 + new Random().nextInt(11));
					float cutOff = filial.getAverageFitness()
							- 1f * filial.getScoreDeviation();
					System.out.println(
							"\nCulling " + numToCull + " Below>>>" + cutOff);
					Die_Off.cull(filial, numToCull, cutOff);
				} else {
					int numToCull = filial.getPopulation().size()
							/ (20 + new Random().nextInt(21));
					float cutOff = filial.getAverageFitness()
							- 2.5f * filial.getScoreDeviation();
					System.out
					.println("Culling " + numToCull + " Below>>>" + cutOff);
					Die_Off.cull(filial, numToCull, cutOff);
				}
				System.out.println("Population Size after culling>>"
						+ filial.getPopulation().size());
				System.out.println("\nChecking Best Specimen of Generation "
						+ generation + "\n");
				Organism generationBest = null;
				for (Organism orgo : filial.getPopulation()) {
					if (generationBest == null
							|| generationBest.getFitness() < orgo.getFitness())
						generationBest = orgo;
					if (best.getFitness() < orgo.getFitness()) {
						System.out.println("\nNew Best::" + "\nGeneration>>"
								+ generation + "\nName>>" + orgo.getName()
								+ "\nCode Length>>" + orgo.getGeneticCode().length()
								+ "\nCode>>" + orgo.getGeneticCode() + "\nFitness>>"
								+ orgo.getFitness() + "\n\n");
						best.setName(orgo.getName());
						best.setChromosomes(orgo.getChromosomes());
						best.setFitness(orgo.getFitness());
						// if (best.getFitness() >= (int) tempIterpreter.getMaxFitness() - 10)
						// {
						System.out.println("\nFinal Best::\n" + generation + ""
								+ best + "\n" + orgo.getFitness());
						break outerloop;
						// }
					}
				}
				System.out.println("Generation " + generation + " Best: "
						+ generationBest.getName() + "\nCode>>>"
						+ generationBest.getGeneticCode() + "\nFitness>>>"
						+ generationBest.getFitness() + "\nCurrent Best Fitness>>>"
						+ best.getFitness() + "\nPopulation Average Fitness>>>"
						+ filial.getAverageFitness()
						+ "\nPopulation Fitness Deviation>>>"
						+ filial.getScoreDeviation());
				//prev = filial;
			}

	}

	/**
	 * Creates a pool of Traits and scores them each randomly based on the scoreProb
	 * parameter to be used as natural selectors Basically: generates a score for every
	 * possible combination of N bases and gives each a score with the probability of
	 * getting a score of X is determined by the param in form [0 , 1, 2, ...] randomly
	 * assign a score to each 2-Base Trait, Higher the score the better
	 *
	 * @param scoreProbabilites
	 *            must sum to 100
	 */
	protected static void codonScorer() {// int[] scoreProbabilites) {

		// TODO Make it so that each organism type get the codons scored for that species

		float[] scores = new float[Math.abs(SCORE_RANGE[0]) + SCORE_RANGE[1]];
		for (int i = 0; i < scores.length; i++)
			scores[i] = SCORE_RANGE[0] + i + new Random().nextFloat();
		for (Codon t : allPossibleCodons)
			t.setScore(scores[new Random().nextInt(scores.length)]);

	}

	/**
	 * Mutates random codons in random genes on random chromatids on random chromosomes
	 *
	 * @param input
	 * @param DNA
	 *            (true if input is DNA, false if input is RNA
	 * @param mutations
	 * @param repeatableMutations
	 * @return
	 */
	public static void mutate(
			Organism orgo, boolean DNA, int mutations,
			boolean repeatableMutations ){

		ArrayList<int[]> used = new ArrayList<>();

		outerloop:
			for (int a = 0; a < mutations; a++) {

				Random rand = new Random();

				int randomChromosome = rand.nextInt();
				int randomChromatid = rand.nextInt();
				int randomGene = rand.nextInt();
				int randomCodon = rand.nextInt();

				if (!repeatableMutations)
					for (int[] check : used)
						if (check[0] == randomChromosome
						&& check[1] == randomChromatid
						&& check[2] == randomGene
						&& check[3] == randomCodon) {
							if (a != 0)
								a--;
							else
								a = 0;
							continue outerloop;
						}

				//Try to Mutate chromatid
				try{
					// Find random chromosome
					orgo.getChromosomes().get(randomChromosome)
					// Random chromatid
					.getChromatids()[randomChromatid]
							// random codon
							.getCodons().get(randomCodon)
							// mutate random base
							.mutate();
				}catch(NullPointerException e){
					//Find random chromosome
					orgo.getChromosomes() .get(randomChromosome)
					//Random chromatid
					.getChromatids()[randomChromatid]
							//Random gene
							.getGenes().get(randomGene)
							//random codon
							.getCodons().get(randomCodon)
							//mutate random base
							.mutate();
				}

				//set the check ints for next mutation
				if (!repeatableMutations)
					used.add(new int[]{randomChromosome, randomChromatid,
							randomCodon});

			}

	}

	/**
	 * Create the parent Generation
	 *
	 * @return ArrayList<\Organism>
	 */
	private static ArrayList<Organism> introduce(int populationSize) {
		ArrayList<Organism> genP = new ArrayList<>();
		for (int i = 0; i < populationSize; i++)
			genP.add(new Organism());
		return genP;
	}


}

package com.ampro.main;

import java.util.ArrayList;

import com.ampro.Evolution.SimpleEvolutionEmulator;
import com.ampro.Evolution.Environment.Environment;
import com.ampro.Evolution.Populations.Population;
import com.ampro.Evolution.Populations.Organisms.Organism;

import jdk.internal.org.objectweb.asm.tree.analysis.Interpreter;

/**
 * This Class is a simple emulation of DNA mutation and evolution.</br>
 * It intends to show how a fully random sequence of mutations might create
 * advantageous traits (represented by DNA sequences). </br>
 * <h2>Current Plan</h2>
 * 1.) A randomly generated population is introduced to
 * the environment </br>
 * 2.) The population goes through 1 mating season of random mating pairs <br>
 * 2.a) The population of offspring should be Y organisms larger than the parent
 * population<br>
 * 3.) A die-off event occurs and Y organisms are killed based off their
 * "fitness" (deemed by the {@link Interpreter} class) <br>
 * 4.opt-1) Repeat steps 2-3 Z number of times, adding each offspring generation
 * to the F1 population <br>
 * 5) After Z additions to genF1, go through a die-off event <br>
 * 6.) Repeat steps 2-5 with each successive Fn generation until K number of
 * organisms exhibit the "perfect" or near-perfect sequence 6.1.opt-2) Change
 * the Trait scores after V number of Fn generations <br>
 * <h2>TODO Overall Additions to make <br>
 * -=-Convert anything with dna sequences to work for choromosomes
 * <br> -Male Female? <br>
 * - "Inept" modifier <br>
 * - Modular Trait base length <br>
 * - Environment Object: Containing an interpreter and "natural resources"<br>
 * ->> The resources will determine the avalibity of certain traits <br>
 * - Start and end codons<br>
 * - Add depth to traits (What does what when where and negitive traits)<br>
 * - Create whole environments with <be>
 * ->>predator organisms that need to eat(kill)
 * X number of lower/weaker organisms to survive <br>
 *  -Change traits to a combination of codons. <br>
 * ->> Each trait will serve different purposes (diet, sight, height, etc) <br>
 * ->> Each codon will be in different availabilities based on the Environment? <br>
 * - Sexual Selection? Maybe through types
 *  - Mutate -> Add diferent types of mutations</br>
 * - Create a population DONE?</br>
 * - Reproduction {@link Population}</br>
 * - Define a trait </br>
 * - Create a changing environment in which certain traits may change from good
 * to neutral or bad
 * {@linkplain Environment}
 * </h2>
 *
 * @author Jonathan Augustine
 */
public class EvolutionRunner extends SimpleEvolutionEmulator{


	public EvolutionRunner() {super(null);}


	public static final boolean DEBUG = true;
	public static final boolean DEBUG_CODON = true;
	public static final boolean DEBUG_GENE = true;
	public static final boolean DEBUG_READER = false;
	public static final boolean DEBUG_MATING = false;

	private static Population prev;


	public static void main(String[] args) {

		BioConstants.setUp();

		Environment environment_A = new Environment();
		for(int i=0; i < 9; i++)
			environment_A.add(new Population(null, null, new ArrayList<Organism>()));

		for(Population p : environment_A.getPopulations())
			for(int i=0; i < 10; i++)
				p.add(new Organism("P", null, null, true));

		Environment environment_B = new Environment();
		for(int i=0; i < 5; i++)
			environment_B.add(new Population(null, null, new ArrayList<Organism>()));

		for(Population p : environment_B.getPopulations())
			for(int i=0; i < 5; i++)
				p.add(new Organism("P", null, null, true));

		Thread thr = new Thread(new SimpleEvolutionEmulator(environment_A));
		Thread thr2 = new Thread(new SimpleEvolutionEmulator(environment_B));
		thr.run();
		//System.out.println("HIIHIIHHI");
		//thr2.run();


	}



}

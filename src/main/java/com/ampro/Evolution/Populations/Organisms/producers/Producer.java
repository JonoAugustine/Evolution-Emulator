package com.ampro.Evolution.Populations.Organisms.producers;

import java.util.ArrayList;

import com.ampro.Evolution.Dna.Chromosome;
import com.ampro.Evolution.Populations.Organisms.Organism;
import com.ampro.Evolution.Populations.Organisms.descriptors.Type.Type;

/**
 * This class defines the object Producer <br><br>
 *
 * An Producer contains variables:<br>
 *
 * <h2>------Naming Related------</h2>
 *
 *	String name: lowerCase name of the Organism
 *
 *	<br>-------------------------------------</br>
 *
 *	<h2>-----Genetics Related Variables----</h2>
 *	String geneticCode: upperCase gene sequence of the Organism <br>
 *	static int chromosomesNum: <br>
 *	ArrayList<Chromosome> chromosomes: The collection of the Organism's Chromosomes
 *	<br>------------------------<br>
 *
 *	<br> final boolean sexual: Whether the organism reproduces asexually or not <br>
 *
 *	<h2>------High Level environment interaction variables------</h2>
 *
 *	String generation: The generation delegation of the organism <br>
 *  double fitness: The organism's fitness rating (likely-hood of survival) <br>
 * 	boolean alive: Whether the organism is considered dead or alive <br>
 *	private int age; <br>
 *
 * @author Jonathan Augustine
 *
 */
public class Producer extends Organism{

	protected static int chromosomesNum = NUMBER_STARTING_DEFAULT_CHROMOSOME;
	protected static int chromatidLength;


	public Producer(
			String generation, String name, ArrayList<Chromosome> chromosomes,
			Boolean sexual)
	{
		super(generation, name, chromosomes, sexual);
	}


	public Producer() {
		super();
		this.name = this.generateName();
	}

	@Override
	protected String generateName() {
		return "Prod" + super.generateName();
	}

	@Override
	public void die() {
		super.die();
	}
	@Override
	public Organism copy() {
		return new Producer(this.generation, this.name, this.chromosomes, this.isSexual());
	}

	@Override
	public Type getType(){
		return super.getType();
	}

	@Override
	public int compareTo(Organism o) {
		return super.compareTo(o);
	}


}

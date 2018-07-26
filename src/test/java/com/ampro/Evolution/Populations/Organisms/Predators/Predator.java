package com.ampro.Evolution.Populations.Organisms.Predators;

import java.util.ArrayList;
import java.util.Random;

import com.ampro.Evolution.Dna.Chromosome;
import com.ampro.Evolution.Populations.Organisms.Organism;
import com.ampro.util.ToolBox;

public class Predator extends Organism{

	protected static int chromosomesNum = NUMBER_STARTING_DEFAULT_CHROMOSOME;
	protected static int chromatidLength;


	protected int numOrgosEaten=0;

	public Predator(String generation, String name, ArrayList<Chromosome> chromosomes, Boolean sexual){
		super(generation, name, chromosomes, sexual);
	}

	public  Predator(){
		super();
		this.name = this.generateName();
	}

	public boolean attack(Organism orgo){
		if(!orgo.isAlive()) ToolBox.systmError("Organism to attack is already dead", orgo);
		//Method should set orgo's dead state to true
		//With probability of success changing relative to the
		//Different in this preditor's fitness and orgos's
		if( (int) this.fitness > (new Random().nextInt((int)orgo.getFitness()/new Random().nextInt((int)orgo.getFitness()))+new Random().nextFloat())*orgo.getFitness() )
			this.kill(orgo);
		if(orgo.isAlive()) return false;
		else return true;
	}

	public void kill(Organism orgo){
		orgo.die();
	}

	@Override
	protected String generateName(){
		return "Preda-"+super.generateName();
	}

	@Override
	public Organism copy(){
		return new Predator(this.generation, this.name, this.chromosomes, this.isSexual());
	}

}

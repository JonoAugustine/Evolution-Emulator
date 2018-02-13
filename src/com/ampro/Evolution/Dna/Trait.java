package com.ampro.Evolution.Dna;

import java.util.ArrayList;

public class Trait{

	private String name;
	private ArrayList<Gene> genes;
	private String sequence;

	public Trait(ArrayList<Gene> genes){
		this.genes = genes;
		for(Gene g : genes)
			this.sequence += g.toString();
	}

	@Override
	public String toString(){
		return this.sequence;
	}

}

package com.ampro.Evolution.Dna;

import java.util.Arrays;
import java.util.Iterator;

/**
 * An Array containing two
 *
 * @author Jonathan Augustine
 *
 */
public class Chromosome implements Iterable<Chromatid>{

	private Chromatid[] chromosome;
	private int score;

	public Chromosome(Chromatid...chromatid){
		this.chromosome = chromatid;
	}

	public Chromatid[] getChromatids(){
		return this.chromosome;
	}

	@Override
	public String toString(){
		return Arrays.asList(this.chromosome).toString();
	}

	@Override
	public Iterator<Chromatid> iterator() {
		return Arrays.asList(this.chromosome).iterator();
	}

}

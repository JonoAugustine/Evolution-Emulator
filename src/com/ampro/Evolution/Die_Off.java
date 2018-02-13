package com.ampro.Evolution;

import java.util.ArrayList;
import java.util.Random;

import com.ampro.Evolution.Populations.Population;
import com.ampro.Evolution.Populations.Organisms.Organism;

public class Die_Off {

	public Die_Off() {
	}

	public static void cull(
			Population population, int numToKill, float fitnessCutoff
			) {
		// Pull all organisms that may be killed and place in temp array, kill Random
		// orgos,
		// Put remaining orgos in temp array back into population
		ArrayList<Organism> temp = new ArrayList<>();
		for (int i = 0; i < population.size(); i++)
			if (population.getPopulation().get(i)
					.getFitness() < fitnessCutoff)
				temp.add(population.getPopulation().remove(i));
		for (int j = 0; j < numToKill; j++)
			if (temp.size() != 0)
				temp.remove(new Random().nextInt(temp.size()));
			else
				break;
		population.getPopulation().addAll(temp);
	}

}

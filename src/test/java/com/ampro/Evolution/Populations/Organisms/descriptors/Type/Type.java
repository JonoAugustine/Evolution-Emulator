/**
 *
 *
 */
package com.ampro.Evolution.Populations.Organisms.descriptors.Type;

import java.util.Random;

/**
 * TODO $Figure out Types
 *	Defines a characteristic of an organism
 * @author Götten Töter
 *
 */
public class Type{
	/**Number of type descriptors for what this Type organism can eat*/
	protected  static int numberOfFoodTypes = 2;
	/**Number of type descriptors for what organisms eat this type*/
	protected static int numberOfEdibleToTypes = 2;

	public Type(){}

	public static Type randomFoodType(){
		int choo = new Random().nextInt(numberOfFoodTypes);
		return new FoodSourceType(choo);

	}

}

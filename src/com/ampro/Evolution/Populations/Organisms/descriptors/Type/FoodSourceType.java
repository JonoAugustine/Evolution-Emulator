/**
 *
 */
package com.ampro.Evolution.Populations.Organisms.descriptors.Type;

import com.ampro.util.ToolBox;

/**
 * A description of what the organism needs to eat
 * @author Götten Töter
 *
 */
public class FoodSourceType extends Type{

	private int needsToEatThis;
	private String foodSourceName;

	/**
	 *
	 */
	public FoodSourceType(int needsToEatThis){
		if(needsToEatThis <= Type.numberOfFoodTypes)
			this.needsToEatThis = needsToEatThis;
		else ToolBox.systmError("There are only "+Type.numberOfFoodTypes+" Food Types", needsToEatThis);
		switch(needsToEatThis){

			case 0: this.foodSourceName = "Alpha";
			case 1: this.foodSourceName = "Beta";
			case 2: this.foodSourceName = "Gamma";

		}
	}

}

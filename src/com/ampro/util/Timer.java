/**
 *
 */
package com.ampro.util;

import java.util.List;

/**
 *
 * A Timer object contains a start-time (long) that is defined upon creation.<br>
 * The method getTime() returns the current duration of the timer's run time in seconds.
 *
 * @author Götten Töter
 *
 */
public class Timer {

	private long startTime = System.nanoTime();

	/**
	 * Constructing a Timer object starts
	 */
	public Timer(){}

	/**
	 * @return The current duration of the timer's run time in HH:MMM:SS
	 */
	public String formattedTime(){
		double seconds = this.getElapsedTime() / Math.pow(10, 9);
		int hours=0;
		int min=0;
		if(seconds > 60)
			for(int i=0; i < seconds; i++)
				if(i==60){
					min++;
					seconds -= 60;
					i=0;
					if(min==60){
						hours++;
						min -= 60;
					}
				}
		return "" + hours + ":" + min + ":" + seconds;
	}

	/**
	 * Resets the starting time and returns the previous duration
	 * @return
	 */
	public String reset(){
		String lastTime = this.formattedTime();
		this.startTime = System.nanoTime();
		return lastTime;
	}

	/**
	 *
	 * @param List<\Timers> or Timer[]
	 * @return
	 */
	public static String average(Object obj){

		long AverageNanoTime = 0;
		if(obj instanceof Timer[]){
			for(int i=0; i < ((Timer[]) obj).length; i++)
				AverageNanoTime += ((Timer[]) obj)[i].getElapsedTime();
			AverageNanoTime /= ((Timer[]) obj).length;
		}
		else if(obj instanceof List<?>){
			if( ((List<?>)obj).get(0) instanceof Timer){
				for(int i=0; i < ((List<?>) obj).size(); i++)
					AverageNanoTime += ((Timer) ((List<?>) obj).get(i)).getElapsedTime() ;
				AverageNanoTime /= ((List<?>) obj).size();
			}else if(((List<?>)obj).get(0) instanceof Long){
				for(int i=0; i < ((List<?>) obj).size(); i++)
					AverageNanoTime += (Long) ((List<?>) obj).get(i);
				AverageNanoTime /= ((List<?>) obj).size();
			}
		}
		else if(obj instanceof long[]){
			for(int i=0; i < ((long[]) obj).length; i++)
				AverageNanoTime += ((long[]) obj)[i];
			AverageNanoTime /= ((long[]) obj).length;
		}

		double seconds = AverageNanoTime/ Math.pow(10, 9);
		int hours=0;
		int min=0;
		if(seconds > 60)
			for(int i=0; i < seconds; i++)
				if(i==60){
					min++;
					seconds -= 60;
					i=0;
					if(min==60){
						hours++;
						min -= 60;
					}
				}
		return "" + hours + ":" + min + ":" + seconds;
	}

	public long getElapsedTime(){
		return System.nanoTime() - this.startTime;
	}

	@Override
	public String toString(){
		return this.formattedTime();
	}

}

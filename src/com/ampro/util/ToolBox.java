package com.ampro.util;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Random;



public class ToolBox
{

	public ToolBox(){}

	public static void sleep(double d){
		try {
			Thread.sleep((long) (1000*d));
		}catch (Exception e){
			System.err.println("Sleep Failed");
			System.err.println( Thread.currentThread().getStackTrace()[2].getClassName());
			System.err.println(Thread.currentThread().getStackTrace()[2].getLineNumber());
			System.err.println(Thread.currentThread().getStackTrace()[2].getMethodName());
		}
	}

	public static void systmError(String messege, Object...errSource){
		System.err.println(messege);
		if(errSource != null)
			for (Object element : errSource)
				System.err.println(element);
		for(int i=2; i < Thread.currentThread().getStackTrace().length; i++){
			System.err.println(Thread.currentThread().getStackTrace()[i].getClassName());
			System.err.println(Thread.currentThread().getStackTrace()[i].getLineNumber());
			System.err.println(Thread.currentThread().getStackTrace()[i].getMethodName());
		}
		System.exit(0);
	}

	public static String randomStringCombination(Object source, int leng){
		String product = "";

		if(source instanceof List<?>)
			//For loop of leng repetitions
			for(int i=0; i < leng; i++)
				//Add random string from letters
				product += (String) ((List<?>) source).get(new Random().nextInt(((List<?>) source).size()));
		if(source instanceof String[]){
			String[] temp = (String[]) source;
			for(int i=0; i < leng; i++)
				product += temp[new Random().nextInt(temp.length)];
		}
		return product;
	}

	/**
	 * Returns the number of permutations of the pool of size "size"
	 * @param size
	 * @param i
	 * @return number of permutations
	 */
	public static int permute(int pool, int size) {
		if(size == 0) return 0;
		try{
			return factorial(pool).divide(factorial(pool - size)).intValue();
		}catch(ArithmeticException e){
			return 0;
		}
	}

	private static BigInteger factorial(int i){
		BigInteger retu = BigInteger.valueOf(i);
		for(int k=1; k <= i; k++)
			retu = retu.multiply(BigInteger.valueOf(k));
		return retu;
	}

	public static String getCurrntTime(){
		return new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
	}

}

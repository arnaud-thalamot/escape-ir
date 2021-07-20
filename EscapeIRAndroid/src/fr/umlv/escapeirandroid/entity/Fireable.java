/******************************************************
 * 
 * ESIPE - IR 2012/2013 - EscapeIR project for Android
 * 
 * @author THALAMOT Arnaud
 * @author VASSEUR Simon
 * 
 * All rights reserved
 * 
 ******************************************************/
package fr.umlv.escapeirandroid.entity;

/**
 * The Interface Fireable defines the behavior of something that can be fired. It need to implements the method fire()
 */
public interface Fireable {

	/**
	 * A method to call to fire something
	 */
	public void fire();
}
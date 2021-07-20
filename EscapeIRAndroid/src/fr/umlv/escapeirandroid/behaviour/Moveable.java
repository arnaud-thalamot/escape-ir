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

package fr.umlv.escapeirandroid.behaviour;

import org.jbox2d.dynamics.Body;

/**
 * The Interface Moveable describes the comportement of something that moves
 * 
 * @author THALAMOT Arnaud
 * @author VASSEUR Simon
 * @version $Revision: 1.0 $
 */
public interface Moveable {
	
	/**
	 * Moves the given body
	 *
	 * @param body the body
	 */
	public void move(Body body);	
}

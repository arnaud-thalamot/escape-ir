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
package fr.umlv.escapeirandroid.game;

import java.util.ArrayList;

import org.jbox2d.callbacks.QueryCallback;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.Fixture;

/**
 * The Class MyQueryCallback offers to keep track of the bodies that aren't in the display area anymore
 * @see org.jbox2d.callbacks.QueryCallback#reportFixture(org.jbox2d.dynamics.Fixture)
 */
public class MyQueryCallback implements QueryCallback {

	/** A list of bodies in the physic world that aren't in the display area anymore */
	private ArrayList<Body> foundBodies = new ArrayList<Body>();

	/**
	 * Add a body of the physic world in the list of bodies that aren't in the display area
	 *
	 * @param fixture the fixture of the body to add in the list
	 */

	@Override
	public boolean reportFixture(Fixture fixture) {
		foundBodies.add(fixture.getBody());
		return true;
	}

	/**
	 * Take an index, retrieves the body at this index in the list of bodies and desactivate it's body
	 *
	 * @param i index of the body in the list of the found bodies
	 */
	public void setInactive(int i){
		this.foundBodies.get(i).setActive(false);
	}

	/**
	 * Returns the size() of the found bodies list
	 *
	 * @return the size of the list
	 */
	public int size(){
		return foundBodies.size();
	}
}

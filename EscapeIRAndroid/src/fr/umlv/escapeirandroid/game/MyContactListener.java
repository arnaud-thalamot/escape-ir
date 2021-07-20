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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.dynamics.contacts.Contact;

import fr.umlv.escapeirandroid.entity.Entity;

//
/**
 * The class MyContactListener offers to keep track of the collisions happening between two bodies in the physic world, during a step.
 *
 *NOTE : endContact,postSolve,preSolve methods HAVE to be declared and are called during the management of the collisions. At the moment, those methods doesn't perform any treatment at all.
 *
 */

public class MyContactListener implements ContactListener{

	/** List of the collisions that occurred. */
	private HashMap<Entity,Entity> collisions = new HashMap<Entity, Entity>();

	/**
	 * Returns an Iterator on the list of collisions
	 *
	 * @return the iterator
	 */
	public Iterator<Map.Entry<Entity, Entity>> iterator(){
		return collisions.entrySet().iterator();
	}

	/**
	 * Clears the list of collisions
	 */
	public void clear(){
		this.collisions.clear();
	}

	/**
	 * This method retrieves the two bodies involved in a collision and put them in the collision list as a SET
	 */
	@Override
	public void beginContact(Contact contact) {

		Object body1 = contact.getFixtureA().getBody().getUserData();
		Object body2 = contact.getFixtureB().getBody().getUserData();
		this.collisions.put((Entity)body1,(Entity)body2);
	}

	@Override
	public void endContact(Contact contact) {
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
	}
}
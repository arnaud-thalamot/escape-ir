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

import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;

/**
 * The Class BodyDescription provides a way to store information needed to create an ennemy body;
 */
public class BodyDescription implements Comparable<BodyDescription>{

	/** The seconds to step factor to convert seconds in a number of steps*/
	private static float SECONDS_TO_STEP_FACTOR = 60;
	private final BodyDef bodydef;
	private float time;
	private final String enemyTypeName;

	/**
	 * Instantiates a new body description, describing where the enemy ship will be loaded (x,y), when it will be loaded (time), and its type
	 *
	 * @param x the x
	 * @param y the y
	 * @param time the time
	 * @param type the type
	 */
	public BodyDescription(float x, float y, float time,String type) {
		this.bodydef = new BodyDef();
		this.bodydef.fixedRotation = true;
		this.bodydef.type = BodyType.DYNAMIC;
		this.bodydef.position.set(x,y);
		this.time = time*SECONDS_TO_STEP_FACTOR;
		this.enemyTypeName = type;
	}

	@Override
	public int compareTo(BodyDescription another) {
		return(int)(time-another.time);
	}

	/**
	 * Gets the time.
	 *
	 * @return the time
	 */
	public float getTime() {
		return time;
	}
	/**
	 * Gets the bodydef.
	 *
	 * @return the bodydef
	 */
	public BodyDef getBodydef() {
		return bodydef;
	}

	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	public String getEnemyTypeName() {
		return enemyTypeName;
	}

	/**
	 * Gets the x position.
	 *
	 * @return the x position
	 */
	public int getX(){
		return (int)bodydef.position.x;
	}
}

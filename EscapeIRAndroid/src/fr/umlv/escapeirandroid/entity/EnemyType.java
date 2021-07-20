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

import android.graphics.Bitmap;

/**
 * The Class EnemyType describe an enemy ship, name, health point etc..
 */
public class EnemyType {

	private String name;
	private final Bitmap picture;
	private final int hp;
	private final int rythm;
	public String behaviour;
	private final int nbMissile;
	private final int nbFireball;
	private final int nbShiboleet;
	private final int nbTriforce;

	/**
	 * Instantiates a new enemy description.
	 *
	 * @param name the name
	 * @param pict the image
	 * @param hp the health point the image path
	 * @param behaviour the behavior
	 * @param r the rhythm of fire
	 * @param nbMiss the number of Missile
	 * @param nbFire the number of Fireball
	 * @param nbShi the number of Shiboleet
	 * @param nbTri the number of Triforce
	 */
	public EnemyType(String name,Bitmap pict,int hp,int r,String behaviour,int nbMiss,int nbFire,int nbShi,int nbTri) {
		this.name = name;
		this.picture = pict;
		this.hp = hp;
		this.rythm = r;
		this.behaviour = behaviour;
		this.nbMissile = nbMiss;
		this.nbFireball = nbFire;
		this.nbShiboleet = nbShi;
		this.nbTriforce = nbTri;
	}

	/**
	 * Gets the name of the enemy description
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Gets the picture
	 *
	 * @return the picture for this enemy description
	 */
	public Bitmap getPicture() {
		return picture;
	}

	/**
	 * Gets the health points
	 *
	 * @return the hp
	 */
	public int getHp() {
		return hp;
	}

	/**
	 * Gets the rhythm of fire
	 *
	 * @return the rhythm of fire
	 */
	public int getRythm() {
		return rythm;
	}

	/**
	 * Gets the name of behavior
	 *
	 * @return the name of behavior
	 */
	public String getBehaviour() {
		return behaviour;
	}

	/**
	 * Gets the number of Missile munition
	 *
	 * @return the number of Missile munition
	 */
	public int getNbMissile() {
		return nbMissile;
	}

	/**
	 * Gets the number of Fireball munition
	 *
	 * @return the number of Fireball munition
	 */
	public int getNbFireball() {
		return nbFireball;
	}

	/**
	 * Gets the number of Shiboleet munition
	 *
	 * @return number of Shiboleet munition
	 */
	public int getNbShiboleet() {
		return nbShiboleet;
	}

	/**
	 * Gets the number of Triforce munition
	 *
	 * @return the number of Triforce munition
	 */
	public int getNbTriforce() {
		return nbTriforce;
	}

	/**
	 * Set the name of the enemy description
	 *
	 * @param s the name
	 */
	public void setName(String s){
		this.name = s;
	}
	
	/**
	 * Set the name of the behaviour type
	 *
	 * @param s the name
	 */
	public void setBehaviour(String s){
		this.behaviour = s;
	}
}
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
import java.util.concurrent.PriorityBlockingQueue;

import android.graphics.Bitmap;
import fr.umlv.escapeirandroid.behaviour.BehaviourType;
import fr.umlv.escapeirandroid.entity.BodyDescription;
import fr.umlv.escapeirandroid.entity.EnemyShip;
import fr.umlv.escapeirandroid.entity.EnemyType;
import fr.umlv.escapeirandroid.entity.FactoryEnemy;

/**
 * The Class GameBoard describes of what is composed a level
 */
public class GameBoard {

	/** Factor to convert seconds in number of steps with a theoric framerate of 60fps */
	private static int SECONDS_TO_STEP_FACTOR = 60;

	/** The different behavior available */
	private final HashMap<String,BehaviourType> behaviourType;

	/** The different enemy available */
	private final HashMap<String,EnemyType> enemyType;

	/** List of the description of the enemies that will be created during the level*/
	private PriorityBlockingQueue<BodyDescription> enemies;

	/** The background of the level */
	private final Bitmap background;

	/** The duration of the level */
	private final float levelDuration;

	/** His name */
	private final String name;

	/**
	 * Instantiates a new game board.
	 *
	 * @param levelDuration how much time a level is scrolling
	 * @param enemies the description of the enemies
	 * @param backgroundPath the name of the picture to represent the level
	 * @param name the name of the level
	 */
	public GameBoard(String name,Bitmap background,float levelDuration,HashMap<String,BehaviourType> b,HashMap<String,EnemyType> e,PriorityBlockingQueue<BodyDescription> enemies) {

		this.name = name;
		this.levelDuration = levelDuration*SECONDS_TO_STEP_FACTOR;
		this.behaviourType = b;
		this.enemyType = e;
		this.enemies = enemies;
		this.background = background;
	}

	/**
	 * Gets the level duration.
	 *
	 * @return the level duration
	 */
	public float getLevelDuration() {
		return levelDuration;
	}

	/**
	 * Gets the number of enemies.
	 *
	 * @return the number of enemies
	 */

	public int getNbEnemies() {
		return enemies.size();
	}

	/**
	 * Gets the enemies.
	 *
	 * @return the enemies
	 */
	public PriorityBlockingQueue<BodyDescription> getEnemies() {
		return enemies;
	}

	/**
	 * Gets the background.
	 *
	 * @return the background
	 */
	public Bitmap getBackground() {
		return background;
	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Gets the different description of Behavior available in this level
	 *
	 * @return the different behavior
	 */
	public HashMap<String, BehaviourType> getBehaviourType() {
		return behaviourType;
	}

	/**
	 * Gets the different description of Enemy available in this level
	 *
	 * @return the different enemy description
	 */
	public HashMap<String, EnemyType> getEnemyType() {
		return enemyType;
	}

	/**
	 * Pop the next enemy on the board and remove it from the list
	 * 
	 * @return the enemy
	 */
	public EnemyShip popNextEnemyShip() {

		BodyDescription bd = enemies.peek();
		EnemyType en = enemyType.get(bd.getEnemyTypeName());
		BehaviourType bt = behaviourType.get(en.getBehaviour());
		enemies.remove();
		return FactoryEnemy.createEnemy(bd,en,bt);
	}
}

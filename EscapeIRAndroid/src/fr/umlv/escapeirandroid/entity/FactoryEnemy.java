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

import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.joints.MouseJointDef;

import fr.umlv.escapeirandroid.behaviour.BehaviourType;
import fr.umlv.escapeirandroid.behaviour.GeneralBehaviour;
import fr.umlv.escapeirandroid.game.Level;
import fr.umlv.escapeirandroid.game.WorldManager;

/**
 * The Class FactoryEnemy provides a way to instantiate EnemyShip objects providing a BodyDescription object
 */
public class FactoryEnemy {

	private static final String MISSILE = "Missile";
	private static final String FIREBALL = "Fireball";
	private static final String SHIBOLEET = "Shiboleet";
	private static final String TRIFORCE = "Triforce";
	/**
	 * Creates the enemy according to the information in the given BodyDescription
	 *
	 * @param bd a BodyDescription containing information to create the EnnemyShip object
	 * @param e the type of enemy
	 * @param b his behavior
	 * @return a reference to a newly instantiated EnemyShip
	 */
	public static EnemyShip createEnemy(BodyDescription bd,EnemyType e,BehaviourType b) {

		EnemyShip enemy = null;
		Body ship = Level.createBody(bd.getBodydef());
		MouseJointDef mjd = new MouseJointDef();
		mjd.bodyA = WorldManager.createGroundBody();
		mjd.bodyB = ship;
		mjd.dampingRatio = 0f;
		mjd.frequencyHz = 100000000;
		mjd.maxForce = (100000000000000000.0f * ship.getMass());
		mjd.collideConnected= true;
		mjd.target.set(ship.getWorldCenter());

		enemy = new EnemyShip(ship,e.getHp(),e.getPicture(),new GeneralBehaviour(b.getPoints(),WorldManager.createMouseJoint(mjd),ship.getWorldCenter()),e.getRythm());
		enemy.addMunitions(MISSILE,e.getNbMissile());
		enemy.addMunitions(FIREBALL,e.getNbFireball());
		enemy.addMunitions(SHIBOLEET,e.getNbShiboleet());
		enemy.addMunitions(TRIFORCE,e.getNbTriforce());
		return enemy;
	}
}

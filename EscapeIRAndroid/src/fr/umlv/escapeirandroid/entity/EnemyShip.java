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

import java.util.Iterator;
import java.util.Random;
import java.util.Set;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;

import android.graphics.Bitmap;

import fr.umlv.escapeirandroid.behaviour.Moveable;
import fr.umlv.escapeirandroid.game.Level;

/**
 * The Class EnemyShip defines methods and fields in common with any enemy ship
 */
public class EnemyShip extends AbstractShip {

	private final int WEAPON_BULLET_SPEED_MULTIPLIER = 200;
	private int delayBeforeFire;
	private int currentDelay;	
	private final int rhythm;
	private Random rand = new Random(this.hashCode());

	/**
	 * Instantiates a new enemy ship.
	 *
	 * @param body the body
	 * @param healthPoint the health point
	 * @param imagePath the image path
	 * @param behaviour the behavior
	 * @param rhythm the rhythm of fire
	 */
	public EnemyShip(Body body,int healthPoint,Bitmap imagePath,Moveable behaviour,int rhythm) {
		super(body,healthPoint,imagePath,entityCategory.SHIP_ENEMY.getValue(),entityCategory.SHIP_HERO.getValue()+entityCategory.WEAPON_HERO.getValue());
		this.behaviour = behaviour;
		this.rhythm = rhythm;
		this.delayBeforeFire = rand.nextInt(this.rhythm);
	}

	/**
	 * Loads randomly a weapon.
	 */
	private void loadWeapon(){

		Set<String> set = weapons.keySet();
		Iterator<String> it = set.iterator();

		String current = null;

		for (int i = 0; i <= rand.nextInt(weapons.size()); i++) {
			current = it.next();
		}

		if(weapons.get(current) > 0){
			weaponSelected = FactoryWeapon.createWeapon(current);
			weaponSelected.load();
		}
	}

	/**
	 * The method fires the newly loaded weapon on the heroship.
	 */

	@Override
	public void fire(){

		if(currentDelay==delayBeforeFire){
			if(weaponSelected == null){
				loadWeapon();
				return;
			}
			if(!weaponSelected.isLoaded){
				weaponSelected.load();
				return;
			}
			else{
				Vec2 vec = new Vec2(-this.body.getWorldCenter().x,-this.body.getWorldCenter().y);
				Vec2 resvec = vec.add(new Vec2(Level.getHeroShipPosition().x,Level.getHeroShipPosition().y));
				resvec.normalize();
				resvec=resvec.mul(WEAPON_BULLET_SPEED_MULTIPLIER);
				weaponSelected.launch(this.body.getWorldCenter(),resvec,false);
				String name = (this.weaponSelected.getClass().getSimpleName());
				this.weapons.put(name,this.weapons.get(name)-1);
				this.weaponSelected = null;
				this.delayBeforeFire = rand.nextInt(this.rhythm);
				this.currentDelay = 0;
			}
		}
		currentDelay++;
	}
}

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

import java.util.ArrayList;
import java.util.Random;

import android.util.Log;

import fr.umlv.escapeirandroid.game.FactoryLevel;

/**
 * The Class FactoryWeapon provides a way to instantiate AbstractWeapon providing the name of the weapon.
 * It keeps a track of all instantiated weapon. 
 */
public class FactoryWeapon {

	/** The created weapon. */
	private static ArrayList<String> createdWeapon = new ArrayList<String>();	
	private static final String TAG = FactoryLevel.class.getSimpleName();

	/**
	 * Creates the weapon providing a the name of the weapon
	 *
	 * @param name the name of the weapon
	 * @return the AbstractWeapon corresponding 
	 */
	public static AbstractWeapon createWeapon(String name) {
		try {
			if(!createdWeapon.contains(name)) createdWeapon.add(name);
			try {
				return (AbstractWeapon) Class.forName((AbstractWeapon.class.getPackage()).getName()+"."+name).newInstance();
			} catch (IllegalAccessException e) {
				Log.d(TAG,"Unable to create weapon "+name);
			} catch (ClassNotFoundException e) {
				Log.d(TAG,"Unable to find the class "+name);
			}
		} catch (InstantiationException e) {
			throw new IllegalArgumentException("Cannot instanciate class : "+name);
		}
		return null;
	}

	/**
	 * Returns one of the already instantiated weapons, randomly
	 *
	 * @return the string
	 */
	public static String oneOfCreatedWeapon(){
		Random rand = new Random(System.currentTimeMillis());
		return createdWeapon.get(rand.nextInt(createdWeapon.size()));
	}
}

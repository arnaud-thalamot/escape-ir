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

import org.jbox2d.common.Vec2;

/**
 * The Interface Launchable defines the comportement of something launchable. A launchable object must implement the method launch.
 */
public interface Launchable {
	
	/**
	 * Launch.
	 *
	 * @param origin the origin of the launch
	 * @param destination the destination of the launch
	 * @param isLaunchedByHeroSHip tells if it was launched by the hero ship
	 */
	public void launch(Vec2 origin, Vec2 destination, boolean isLaunchedByHeroSHip);
}

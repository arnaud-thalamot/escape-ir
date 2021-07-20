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
package fr.umlv.escapeirandroid.utils;

import fr.umlv.escapeirandroid.game.EscapeIR;
//
/**
 * The Class PositionConverter provides methods to converts pixel position into physic world Jbox2D positions, and vice versa
 */
public class PositionConverter {

	/**
	 * Convert the given abscissa pixel position into a position in the Jbox2D world, proportionnally to the WIDTH of the window
	 *
	 * @param x the abscissa of a point in pixel
	 * @return the abcissa of the point represented in the physic world
	 */
	public static float worldToScreenX(float x){
		return (EscapeIR.CURRENT_WIDTH*x)/100.0f;
	}

	/**
	 * Convert the given ordonate pixel position into a position in the Jbox2D world, proportionnally to the HEIGHT of the window
	 *
	 * @param y the ordonate of a point in pixel
	 * @return the ordonate of the point represented in the physic world
	 */
	public static float worldToScreenY(float y){
		return EscapeIR.CURRENT_HEIGHT - ((EscapeIR.CURRENT_HEIGHT*y)/100.0f);
	}

	/**
	 * Convert the given abscissa in the physic world into a position in pixel, proportionnally to the WIDTH of the window
	 *
	 * @param x the abscissa in meter in the physic world
	 * @return the abscissa representation in pixel
	 */
	public static float screenToWorldX(float x){
		return (x*100.0f)/EscapeIR.CURRENT_WIDTH;
	}

	/**
	 * Convert the given ordonate in the physic world into a position in pixel, proportionnally to the HEIGHT of the window
	 *
	 * @param y the ordonnate in meter in the physic world
	 * @return the ordonate represnetation in pixel
	 */
	public static float screenToWorldY(float y){
		return 100.0f - ((y*100)/EscapeIR.CURRENT_HEIGHT);
	}
}

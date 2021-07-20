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

import android.graphics.Canvas;

/**
 * The Interface Displayable defines the behavior of something drawable, it means it has to implements the following methods
 */
public interface Displayable {

	/**
	 * Draws the object with a Graphics2D object
	 *
	 * @param graphics the graphics
	 */
	public void draw(Canvas graphics);
}

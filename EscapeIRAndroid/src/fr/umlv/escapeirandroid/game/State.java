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

/**
 * The Class State provides descriptions of different states during the game
 */
public class State {

	/**
	 * The different state during the game
	 */
	public enum gameState{

		/** State activated when the level is in progress*/
		IN_PROGRESS,

		/** State activated when the display between two level is performed */
		BETWEEN_TWO_LEVEL,

		/** State activated when the Hero is dead */
		HERO_DEAD,

		/** State activated when the game is finished */
		FINISHED,
	}

	/**
	 * The state of the scrolling
	 */
	public enum scrollingState{

		/** State activated when the background is scrolling */
		SCROLLING,

		/** State activated when the background don't have to be scrolled */
		STOPPED,
	}	
}

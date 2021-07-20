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

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import android.util.Log;
import fr.umlv.escapeirandroid.utils.FileManager;

/**
 * The Class FactoryLevel provides a way instantiate a level with the name of the board corresponding
 *
 * @author THALAMOT Arnaud
 * @author VASSEUR Simon
 * @version $Revision: 1.0 $
 */
public class FactoryLevel {

	/**
	 * Field TAG.
	 */
	private static final String TAG = FactoryLevel.class.getSimpleName();

	/**
	 * Loads the gameBoard and instantiate the object Level corresponding
	 *
	 * @param gameBoardName the game board name to load


	 * @return the reference to the newly created Level * @throws SAXException  */
	public static Level createLevel(String gameBoardName){

		Level level = null;
		GameBoard gameBoard = null;
		try {
			gameBoard = FileManager.getInstance().loadGameBoard(gameBoardName);
			level = new Level(gameBoard);
			return level;
		} catch (FileNotFoundException e) {
			Log.d(TAG, gameBoardName+" : File not found"+e);
			return null;
		} catch (IOException e) {
			Log.d(TAG, "Unable to read file "+gameBoardName+" "+e);
			return null;
		} catch (ParserConfigurationException e) {
			Log.d(TAG, "The gameboard config file "+gameBoardName+" does not have the appropriate syntax for an XML file "+e);
			return null;
		} catch (SAXException e) {
			Log.d(TAG, "The gameboard config file "+gameBoardName+" does not have the appropriate syntax for an XML file "+e);
			return null;
		}
	}
}

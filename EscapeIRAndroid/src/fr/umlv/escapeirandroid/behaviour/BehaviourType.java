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
package fr.umlv.escapeirandroid.behaviour;

import java.util.ArrayList;

import android.gesture.GesturePoint;
import android.graphics.PointF;
import fr.umlv.escapeirandroid.utils.PositionConverter;

/**
 * The Class BehaviourType describes a behavior assignable to an enemy ship. The way it moves.
 * 
 * @author THALAMOT Arnaud
 * @author VASSEUR Simon
 * @version $Revision: 1.0 $
 */
public class BehaviourType{

	public String name;
	private final ArrayList<PointF> pointsOrigin;
	private final ArrayList<GesturePoint> points;
	private final ArrayList<GesturePoint> pointsOnScreen;

	/**
	 * Instantiates a behavior.
	 *
	 * @param name the name of the behavior
	 * @param p the arraylist of points
	 */
	public BehaviourType(String name,ArrayList<PointF> p) {
		this.name = name;
		this.pointsOrigin = p;
		this.points = new ArrayList<GesturePoint>();

		for(int i=0;i<p.size();i++){
			this.points.add(new GesturePoint(p.get(i).x,p.get(i).y,i/60));
		}

		this.pointsOnScreen = new ArrayList<GesturePoint>();
		for(int i=0;i<p.size();i++){
			this.pointsOnScreen.add(new GesturePoint(PositionConverter.worldToScreenX(p.get(i).x),PositionConverter.worldToScreenY(p.get(i).y),i/60));
		}
	}

	/**
	 * Get the points of the gesture
	 * 
	 * @return the points of the gesture
	 */
	public ArrayList<GesturePoint> getPoints() {
		return points;
	}

	/**
	 * Get the points of the gesture converted in position on screen
	 * 
	 * @return the gesture's points converted on to screen position
	 */
	public ArrayList<GesturePoint> getPointsOnScreen(){
		return pointsOnScreen;
	}
	
	/**
	 * Get the points that created this gesture
	 * 
	 * @return the gesture's points converted on to screen position
	 */

	public ArrayList<PointF> getPointsOrigin(){
		return pointsOrigin;
	}
	
	/**
	 * Get the name of the gesture
	 * 
	 * @return the name
	 */
	public String getName(){
		return this.name;
	}

	/**
	 * Set the name of the gesture
	 * 
	 * @param String the name
	 */
	public void setName(String newName){
		this.name = newName;
	}
}

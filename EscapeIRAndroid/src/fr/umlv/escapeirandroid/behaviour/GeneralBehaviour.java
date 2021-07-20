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

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.joints.MouseJoint;

import android.gesture.GesturePoint;

/**
 * The Class GeneralBehaviour moves an enemy ship following the gesturePoint
 * 
 * @author THALAMOT Arnaud
 * @author VASSEUR Simon
 * @version $Revision: 1.0 $
 */
public class GeneralBehaviour implements Moveable{

	private final ArrayList<GesturePoint> gesturePoint;
	private final MouseJoint mouseJoint;
	private int currentPosPath = 0;
	private GesturePoint currentDest;
	private float cycleDifferentialX = 0;
	private float cycleDifferentialY = 0;
	private float startX;
	private float startY;
	private Vec2 vec = new Vec2(0,0);

	/**
	 * Instantiates a behavior.
	 *
	 * @param path the arralist of gesture points
	 * @param mouseJoint the Jbox2d mousejoint (the ground)
	 * @param origin the Jbox2d body on which the behavior is applied
	 */
	public GeneralBehaviour(ArrayList<GesturePoint> path, MouseJoint mouseJoint,Vec2 origin) {

		this.gesturePoint = changeOrigin(path, origin);
		this.mouseJoint = mouseJoint;
		this.currentDest = this.gesturePoint.get(0);
		this.startX = currentDest.x;
		this.startY = currentDest.y;
	}

	@Override
	public void move(Body body) {
		Vec2 currentPos = body.getWorldCenter();

		if((Float.compare(currentPos.x,(currentDest.x+cycleDifferentialX)) == 0) && (Float.compare(currentPos.y,(currentDest.y+cycleDifferentialY))) ==0){

			if(currentPosPath == gesturePoint.size()-1){
				currentPosPath = 0;
				cycleDifferentialX = currentPos.x - startX;
				cycleDifferentialY = currentPos.y - startY;
			}
			else{
				currentPosPath++;
			}
			currentDest = gesturePoint.get(currentPosPath);
			vec.set(currentDest.x+cycleDifferentialX,currentDest.y+cycleDifferentialY);
			mouseJoint.setTarget(vec);
		}
	}

	/**
	 * Translate the vec2 origin of the behaviour
	 */
	private ArrayList<GesturePoint> changeOrigin(ArrayList<GesturePoint> path, Vec2 origin){

		ArrayList<GesturePoint> result = new ArrayList<GesturePoint>(); 

		float startX = path.get(0).x;
		float startY = path.get(0).y;
		GesturePoint gp = null;

		for (int i = 0; i < path.size(); i++) {
			gp = path.get(i);
			result.add(new GesturePoint((origin.x-startX)+gp.x, (origin.y-startY)+gp.y, gp.timestamp));
		}		
		return result;
	}
}

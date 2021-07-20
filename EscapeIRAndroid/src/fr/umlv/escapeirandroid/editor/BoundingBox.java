/**ESIPE - IR 2012/2013 - EscapeIR project for Android**/

package fr.umlv.escapeirandroid.editor;

import android.graphics.Canvas;
import android.graphics.RectF;

/**
 * The Class BoundingBox provides a moveable bounding box for a Dragable Enemy and contrains it to implement draw and updatePostion
 *
 * @author THALAMOT Arnaud
 * @author VASSEUR Simon
 * @version $Revision: 1.0 $
 */

public abstract class BoundingBox{

	/**
	 * RectF representing the bounding box
	 */
	
	final RectF rect;
	
	public BoundingBox(float left, float top, float right, float bottom){
		this.rect  = new RectF(left, top, right, bottom);
	}
	
	/**
	 * Moves the bounding box to the given coordinates
	 * @param x
	 * @param y
	 */
	
	public void move(float x, float y){
		rect.set(rect.left+x, rect.top+y, rect.right+x, rect.bottom+y); 
	}
	
	public abstract void draw(Canvas canvas);
	public abstract void updatePosition();
	
}

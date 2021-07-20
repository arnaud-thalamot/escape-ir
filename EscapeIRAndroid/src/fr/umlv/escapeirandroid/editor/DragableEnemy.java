/**ESIPE - IR 2012/2013 - EscapeIR project for Android**/

package fr.umlv.escapeirandroid.editor;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * The Class DragableEnemy is used to move, drag and drop bitmaps representing an enemy in the game
 *
 * @author THALAMOT Arnaud
 * @author VASSEUR Simon
 * @version $Revision: 1.0 $
 */

public class DragableEnemy extends BoundingBox implements Parcelable{
	
	/**
	 * Defines colors to draw the bounding selected or not
	 */
	
	private static final int SELECTED_COLOR_VALUE = Color.GREEN;
	private static final int NOT_SELECTED_COLOR_VALUE = Color.RED;
	
	final Bitmap sprite;
	
	/**
	 * Coordinates X Y where the ennemy has to move on the map
	 */
	
	private float destX;
	private float destY;
	
	/**
	 * Current paint of the bounding box
	 */
	
	private Paint currentSelectedPaint;
	String ennemyType;
	
	/**
	 * Constructor
	 * 
	 * Creates the bounding box around the bitmap
	 * Stores the sprite
	 * Initialize destination coordinates to special value -1 so the enemy don't move
	 * Set the paint to the notSelected paint
	 * 
	 * @param bitmap
	 * @param x
	 * @param y
	 * @param ennemyType
	 */
	
	public DragableEnemy(Bitmap bitmap,float x,float y,String ennemyType){
		super(x, y, x+bitmap.getWidth(), y+bitmap.getHeight());
		this.sprite = bitmap;
		currentSelectedPaint = new Paint();
		destX = -1;
		destY = -1;
		this.ennemyType = ennemyType;
		currentSelectedPaint.setColor(NOT_SELECTED_COLOR_VALUE);
		currentSelectedPaint.setStyle(Paint.Style.STROKE);
	}
	
	/**
	 * Used to create an enemy from a Parcel, useful on restore instance state of the view
	 * Does the same job a the constructor but loading data from a parcel
	 * @param in parcel containing the data the create the enemy
	 */
	
	public DragableEnemy(Parcel in){
		super(in.readFloat(), in.readFloat(), in.readFloat(), in.readFloat());
		sprite = Bitmap.CREATOR.createFromParcel(in);
		ennemyType = in.readString();
		destX = -1;
		destY = -1;
		currentSelectedPaint.setColor(NOT_SELECTED_COLOR_VALUE);
		currentSelectedPaint.setStyle(Paint.Style.STROKE);
	}

	/**
	 * Draws the sprite and its bounding box
	 */
	
	@Override
	public void draw(Canvas canvas) {
		canvas.drawBitmap(sprite, null, rect, null);
		canvas.drawRect(rect, currentSelectedPaint);
	}
	
	/**
	 * Sets the destination coordinates X Y
	 * @param x
	 * @param y
	 */
	
	public void moveTo(float x, float y){
		this.destX = x;
		this.destY = y;
	}
	
	/**
	 * Moves the bounding box according to the destination coordinates
	 */
	
	public void updatePosition(){
		if((destX != -1)&&(destY != -1)){
		move(destX - rect.centerX(),destY - rect.centerY());
		}
	}
	
	/**
	 * Makes the bounding box color selected
	 */
	
	public void select(){
		currentSelectedPaint.setColor(SELECTED_COLOR_VALUE);
	}
	
	/**
	 * Makes the bounding box color unselected
	 */
	
	public void unSelect(){
		currentSelectedPaint.setColor(NOT_SELECTED_COLOR_VALUE);
	}
	
	/**
	 * Need to be overridden to be Parcelable
	 */

	@Override
	public int describeContents() {
		return 0;
	}

	/**
	 * Saves the enemy current state into a Parcel, useful in on save instance state in the view
	 * also useful to keep the position of the enemies on the map when the view is destroyed
	 */
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeFloat(rect.left);
		dest.writeFloat(rect.top);
		dest.writeFloat(rect.right);
		dest.writeFloat(rect.bottom);
		sprite.writeToParcel(dest, 4);
		dest.writeString(ennemyType);
	}
}

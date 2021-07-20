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

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.dynamics.Body;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import fr.umlv.escapeirandroid.utils.ImageManager;
import fr.umlv.escapeirandroid.utils.PositionConverter;

/**
 * The Class AbstractWeapon defines methods and fields common to any Weapon
 */
public abstract class AbstractWeapon extends Entity implements Launchable{

	protected int loadingTimeStep;
	protected int currentLoadingStep;
	protected boolean isLoaded = false;
	protected boolean isLoading = false;
	protected Matrix transform;
	protected float desiredAngle;

	/**
	 * Instantiates a new abstract weapon.
	 *
	 * @param imagePath the image path
	 * @param hp the hp
	 * @param loadingTime the loading time
	 */
	public AbstractWeapon(int imagePath,int hp,int loadingTime){
		super(ImageManager.getInstance().loadImage(imagePath),hp);
		this.loadingTimeStep = loadingTime;
		this.transform = new Matrix();
	}

	/**
	 * Gets the shape.
	 *
	 * @return the shape
	 */
	public PolygonShape getShape(){
		return this.shape;
	}

	/**
	 * Loads the weapon
	 */
	public void load(){
		if(!isLoading) this.isLoading = true;
		if(currentLoadingStep >= loadingTimeStep){
			this.isLoaded = true;
		}
		else{
			currentLoadingStep++;
		}
	}

	/**
	 * Sets the body.
	 *
	 * @param body the body
	 * @param categoryBit the category bit
	 * @param maskBit the mask bit
	 */
	public void setBody(Body body,int categoryBit,int maskBit){
		if(body!=null){
			this.body = body;
			this.fixtureDef.filter.categoryBits = categoryBit;
			this.categoryBit = categoryBit;
			this.fixtureDef.filter.maskBits = maskBit;
			this.body.createFixture(fixtureDef);
			this.body.setUserData(this);
		}
	}

	/**
	 * Draws the weapon
	 * @param graphics the graphics2D to draw with
	 */
	public void draw(Canvas graphics) {
		if(!this.body.isActive()){
			drawExplosing(graphics);
			return;
		}

		transform.reset();
		final int dx = (int)PositionConverter.worldToScreenX(body.getWorldCenter().x);
		final int dy = (int)PositionConverter.worldToScreenY(body.getWorldCenter().y);

		transform.setTranslate(dx, dy);
		transform.postRotate((float) Math.toDegrees(body.getAngle()+desiredAngle),dx,dy);
		transform.postTranslate(-this.img.getWidth()/2,-this.img.getHeight()/2);
		graphics.drawBitmap(this.img, transform, null);
	}

	/**
	 * Draw loading.
	 *
	 * @param graphics the graphics
	 * @param shipBody the ship body
	 * @param shipImage the ship image
	 * @param isLaunchByHeroShip the is launch by hero ship
	 */
	public abstract void drawLoading(Canvas graphics,Body shipBody,Bitmap shipImage, boolean isLaunchByHeroShip);
}

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
import org.jbox2d.dynamics.Body;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import fr.umlv.escapeirandroid.R;
import fr.umlv.escapeirandroid.game.Level;
import fr.umlv.escapeirandroid.utils.PositionConverter;

/**
 * The Class Missile defines the behavior of a shiboleet weapon, providing a method to launch the weapon and to display it
 */
public class Missile extends AbstractWeapon{

	/** The path of image when the weapon is loaded*/
	private static int imagePath = R.drawable.missile;

	private static final int WEAPON_HP_DAMAGE = 1;
	private static final int WEAPON_LOADING_TIME = 0;
	/**
	 * Instantiates a  missile.
	 */
	public Missile() {
		super(imagePath,WEAPON_HP_DAMAGE,WEAPON_LOADING_TIME);
	}

	/**
	 * This method takes an origin, the ship launching, a destination the targeted ship, and a boolean to determine if the weapon is launched by the heroship
	 *@param origin the origin of the weapon launch
	 *@param destination the target of the weapon
	 *@param isLaunchedByHeroShip true if it is launched by the heroship, else false
	 */
	@Override
	public void launch(Vec2 origin, Vec2 destination, boolean isLaunchedByHeroShip){
		if(isLoaded){
			if(Level.createWeapon(origin,this,isLaunchedByHeroShip)){
				if(destination != null){
					desiredAngle = (float) Math.atan2(destination.x, destination.y);
					if(!isLaunchedByHeroShip) desiredAngle = -desiredAngle;
					if(body!=null){
						body.setLinearVelocity(destination);
						isLoaded = false;
						isLoading = false;
						currentLoadingStep = 0;
					}
				}
			}
		}
	}

	/**
	 * Draws how the weapon loads and where
	 * @param graphics the graphics to display
	 * @param shipBody where to display loading sequence
	 * @param shipImage to know where to display the loading sequence according to launcher image
	 * @param isLaunchByHeroShip true if it was launched by the hero, else false
	 */
	@Override
	public void drawLoading(Canvas graphics,Body shipBody,Bitmap shipImage, boolean isLaunchByHeroShip) {
		if(isLoading){

			int dx = (int)PositionConverter.worldToScreenX(shipBody.getWorldCenter().x);
			int dy = (int)PositionConverter.worldToScreenY(shipBody.getWorldCenter().y);
			transform.reset();

			if(!isLaunchByHeroShip){
				transform.preRotate(180);
				transform.setTranslate(dx-this.img.getWidth()/2,dy-shipImage.getHeight()/2-this.img.getHeight()/2);
			}
			else transform.setTranslate(dx-this.img.getWidth()/2,dy-shipImage.getHeight()/2-this.img.getHeight());

			graphics.drawBitmap(this.img,this.transform,this.paint);
		}
	}
}

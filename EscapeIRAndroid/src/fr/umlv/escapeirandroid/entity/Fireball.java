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
 * The Class Fireball defines the comportment of a shiboleet weapon, providing a method to launch the weapon and to display it
 */
public class Fireball extends AbstractWeapon{

	/** The resource of image when the weapon is loaded*/
	private static final int imagePath = R.drawable.fireball;

	private static final int WEAPON_HP_DAMAGE = 2;
	private static final int WEAPON_LOADING_TIME = 90;
	/**
	 * Instantiates a new fireball.
	 */
	public Fireball() {
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
				body.setLinearVelocity(destination);
				isLoaded = false;
				isLoading = false;
				currentLoadingStep = 0;
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
	public void drawLoading(Canvas graphics,Body shipBody,Bitmap shipImage,boolean isLaunchByHeroShip) {
		if(isLoading){

			int dx = (int)PositionConverter.worldToScreenX(shipBody.getWorldCenter().x);
			int dy = (int)PositionConverter.worldToScreenY(shipBody.getWorldCenter().y);
			transform.reset();
			if(!isLaunchByHeroShip) transform.setTranslate(dx-this.img.getWidth()/2*(float)currentLoadingStep/loadingTimeStep,dy+shipImage.getHeight()/2);
			else transform.setTranslate(dx-this.img.getWidth()/2*(float)currentLoadingStep/loadingTimeStep,dy-shipImage.getHeight()/2-this.img.getHeight());
			transform.preScale((float)currentLoadingStep/loadingTimeStep,(float)currentLoadingStep/loadingTimeStep);
			graphics.drawBitmap(this.img, transform,this.paint);
		}
	}
}

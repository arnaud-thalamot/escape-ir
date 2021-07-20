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
import fr.umlv.escapeirandroid.utils.ImageManager;
import fr.umlv.escapeirandroid.utils.PositionConverter;

/**
 * The Class Shiboleet defines the behavior of a shiboleet weapon, providing a method to launch the weapon and to display it
 */
public class Shiboleet extends AbstractWeapon{

	/** The path of image when the weapon is loaded*/
	private static final int imagePathBig = R.drawable.shiboleetbig;

	/** The path of the image when the weapon is not loaded */
	private static final int imagePath = R.drawable.shiboleet;

	/** The BufferedIMage corresponding to the image loaded */
	private Bitmap imgBig;

	private static final int WEAPON_HP_DAMAGE = 3;
	private static final int WEAPON_LOADING_TIME = 180;

	/** Vec to set LinearVelocity of bodies */
	private Vec2 vec = new Vec2();

	/**
	 * Instantiates a new shiboleet.
	 */
	public Shiboleet() {
		super(imagePath,WEAPON_HP_DAMAGE,WEAPON_LOADING_TIME);
		this.imgBig = ImageManager.getInstance().loadImage(imagePathBig);
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
			for(int i=0;i<32;i++){
				Shiboleet shiboleet = new Shiboleet();
				if(Level.createWeapon(origin,shiboleet,isLaunchedByHeroShip)){
					vec.x = (float)Math.cos(i)*100;
					vec.y = (float)Math.sin(i)*100;
					shiboleet.body.setLinearVelocity(vec);
				}
			}
			isLoaded = false;
			isLoading = false;
			currentLoadingStep = 0;
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
			final float dx = PositionConverter.worldToScreenX(shipBody.getWorldCenter().x);
			final float dy = PositionConverter.worldToScreenY(shipBody.getWorldCenter().y);
			transform.reset();
			transform.setTranslate(dx-this.imgBig.getWidth()/2*(float)currentLoadingStep/loadingTimeStep,dy-this.imgBig.getHeight()/2);
			transform.preScale((float)currentLoadingStep/loadingTimeStep,(float)currentLoadingStep/loadingTimeStep);
			graphics.drawBitmap(this.imgBig,this.transform,this.paint);
		}
	}
}

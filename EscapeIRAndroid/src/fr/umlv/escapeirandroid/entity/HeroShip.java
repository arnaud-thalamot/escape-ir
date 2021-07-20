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

import java.util.Iterator;
import java.util.Set;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import fr.umlv.escapeirandroid.R;
import fr.umlv.escapeirandroid.game.WorldManager;
import fr.umlv.escapeirandroid.utils.ImageManager;
import fr.umlv.escapeirandroid.utils.PositionConverter;

/**
 * The Class HeroShip defines the specifications of the ship used by the player 
 */
public class HeroShip extends AbstractShip{

	private final int WEAPON_BULLET_SPEED_MULTIPLIER = 200;
	private final int GESTURE_SPEED_MULTIPLIER = 3;
	private enum Direction{LEFT,RIGHT};

	private final static int SHIP_IMAGE_FILENAME = R.drawable.heroship;

	/** The rotation images filenames to display the looping effect */
	private final int[] rotationImagesFilenames = {
			R.drawable.heroship, R.drawable.heroship_rotation1, R.drawable.heroship_rotation2,
			R.drawable.heroship_rotation3, R.drawable.heroship_rotation4, R.drawable.heroship_rotation5,
			R.drawable.heroship_rotation6, R.drawable.heroship_rotation7, R.drawable.heroship,
	};

	/** The rotation images */
	private Bitmap[] rotationImages;

	private boolean islooping = false;
	private Direction loopingDirection;
	private int frameNumber;
	private int delay;
	private boolean isLoadAsked = false;

	/** Vec to set move heroship body */
	private Vec2 vec = new Vec2();

	/** Vec to set the target */
	private Vec2 target = new Vec2();

	/**
	 * Instantiates a new hero ship providing a body in the physic world and his health points
	 *
	 * @param body the body
	 * @param healthPoint the health point
	 */
	public HeroShip(Body body,int healthPoint) {

		super(body,healthPoint,ImageManager.getInstance().loadImage(SHIP_IMAGE_FILENAME),entityCategory.SHIP_HERO.getValue(),entityCategory.SHIP_ENEMY.getValue() + entityCategory.WALL.getValue()  + entityCategory.WEAPON_ENEMY.getValue() + entityCategory.BONUS.getValue());
		this.rotationImages = new Bitmap[this.rotationImagesFilenames.length];
		for (int i = 0; i<this.rotationImagesFilenames.length; i++){
			this.rotationImages[i] = ImageManager.getInstance().loadImage(this.rotationImagesFilenames[i]);			
		}
	}

	/**
	 * Checks if is load of a weapon has been asked.
	 *
	 * @return true, if is a weapon has been asked to load for the weapon
	 */
	public boolean isLoadAsked() {
		return isLoadAsked;
	}

	/**
	 * Sets the load asked.
	 *
	 * @param isLoadAsked the new load asked value
	 */
	public void setLoadAsked(boolean isLoadAsked) {
		this.isLoadAsked = isLoadAsked;
	}

	/**
	 * Draws the heroship according to its different status related to looping
	 */
	@Override
	public void draw(Canvas graphics) {
		if(!this.body.isActive()){
			drawExplosing(graphics);
			return;
		}
		if(weaponSelected != null){
			if(weaponSelected.isLoading) weaponSelected.drawLoading(graphics,this.body,this.img,true);
		}
		if(this.islooping){
			if(loopingDirection == Direction.RIGHT){
				graphics.drawBitmap(rotationImages[frameNumber],PositionConverter.worldToScreenX(body.getWorldCenter().x)-img.getWidth()/2,PositionConverter.worldToScreenY(body.getWorldCenter().y)-img.getHeight()/2,this.paint);
			}
			else{
				graphics.drawBitmap(rotationImages[rotationImages.length-1-frameNumber],PositionConverter.worldToScreenX(body.getWorldCenter().x)-img.getWidth()/2,PositionConverter.worldToScreenY(body.getWorldCenter().y)-img.getHeight()/2,this.paint);
			}
			delay++;
			if(delay == 3){
				frameNumber++;
				delay=0;
			}
			if(frameNumber>=rotationImages.length){
				frameNumber = 0;
				this.islooping = false;
				this.shape.setAsBox(PositionConverter.worldToScreenX(img.getWidth()/2),PositionConverter.worldToScreenY(img.getHeight()/2));
				WorldManager.JointHeroShip();
			}
		}
		else{
			graphics.drawBitmap(img,PositionConverter.worldToScreenX(body.getWorldCenter().x)-img.getWidth()/2,PositionConverter.worldToScreenY(body.getWorldCenter().y)-img.getHeight()/2,this.paint);
		}
	}

	/**
	 * Applies a gesture to heroship, providing a direction, a strength and telling if it is a looping or not.
	 *
	 * @param vec the direction of the gesture
	 * @param length the strength of the gesture
	 */
	public void applyLooping(Vec2 vector){

		this.islooping = true;
		WorldManager.unJointHeroShip();
		vec.x = 0;
		vec.y = 0;
		this.body.setLinearVelocity(vec);
		this.body.setLinearVelocity(vector);
		this.shape.setAsBox(1,1);
		if(vector.x < 0){
			this.loopingDirection = Direction.LEFT;
		}
		else{
			this.loopingDirection = Direction.RIGHT;
		}
		this.body.setLinearDamping((float)(GESTURE_SPEED_MULTIPLIER));
	}

	/**
	 * Sets the target.
	 *
	 * @param x the x
	 * @param y the y
	 */
	public void setTarget(float x,float y){
		target.x = x;
		target.y = y;
	}

	/**
	 * Tries to select a given weapon for the heroship. If the weapon has munitions then it is selectioned and returns true, else false
	 *
	 * @param weapon the name of the weapon to load
	 * @return true, if the weapon has been selectionned, else false
	 */
	public boolean canSelectWeapon(String weapon){

		if(weapons.get(weapon) > 0){
			weaponSelected = FactoryWeapon.createWeapon(weapon);
			return true;
		}
		return false;
	}

	/**
	 * Definies the sequence to fire a weapon for the heroship. He can fire only when a weapon is selected and loaded.
	 */
	@Override
	public void fire() {
		if(weaponSelected != null){			
			if(weaponSelected.isLoaded){

				vec.x = -this.body.getWorldCenter().x;
				vec.y = -this.body.getWorldCenter().y;
				Vec2 resvec = vec.add(target);
				resvec.normalize();
				resvec=resvec.mul(WEAPON_BULLET_SPEED_MULTIPLIER);
				weaponSelected.launch(this.body.getWorldCenter(),resvec,true);
				String name = (weaponSelected.getClass().getSimpleName());
				weapons.put(name,weapons.get(name)-1);
				weaponSelected = null;
				isLoadAsked = false;

				if(!canSelectWeapon(name)){

					Set<String> set = weapons.keySet();
					Iterator<String> it = set.iterator();
					String current;

					while(it.hasNext()){
						current = it.next();
						if(weapons.get(current) > 0){
							weaponSelected = FactoryWeapon.createWeapon(current);
							return;
						}                                       
					}
				}
			}
		}
	}

	/**
	 * Begin the loading sequence of the selected weapon.
	 */
	public void loadWeapon(){
		if(weaponSelected != null){
			if(!weaponSelected.isLoaded) weaponSelected.load();
		}
	}

	/**
	 * Gets the weapon munition providing a weapon name
	 *
	 * @param weapon the weapon name
	 * @return the related weapon's munition
	 */
	public int getWeaponMunition(String weapon){
		return weapons.get(weapon);
	}
}

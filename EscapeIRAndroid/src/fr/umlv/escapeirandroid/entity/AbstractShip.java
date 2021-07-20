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

import java.util.HashMap;

import org.jbox2d.dynamics.Body;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import fr.umlv.escapeirandroid.behaviour.Moveable;
import fr.umlv.escapeirandroid.utils.PositionConverter;

/**
 * The Class AbstractShip defines methods and fields common to any Ship
 */
public abstract class AbstractShip extends Entity implements Fireable{

	/** The Hashmap containing the weapons munitions */
	protected HashMap<String,Integer> weapons;

	/** The weapon currently selected */
	protected AbstractWeapon weaponSelected;

	/** The behavior for thie ship */
	protected Moveable behaviour;

	/**
	 * Instantiates a new abstract ship.
	 *
	 * @param body the body
	 * @param healthPoint the health point
	 * @param imagePath the image path
	 * @param categoryBit the category bit
	 * @param maskBit the mask bit
	 */
	public AbstractShip(Body body,int healthPoint,Bitmap imagePath,int categoryBit,int maskBit){
		super(imagePath,healthPoint);
		this.body = body;
		this.fixtureDef.filter.categoryBits = categoryBit;
		this.categoryBit = categoryBit;
		this.fixtureDef.filter.maskBits = maskBit;		
		this.body.createFixture(fixtureDef);
		this.body.setUserData(this);
		this.weapons = new HashMap<String, Integer>();
	}

	/**
	 * Moves the ship
	 */
	public void move(){
		this.behaviour.move(this.body);
	}

	/**
	 * Draws the ship with the graphics2D
	 * @param graphics the graphics2D to draw with
	 */
	public void draw(Canvas graphics) {
		if(!this.body.isActive()){
			drawExplosing(graphics);
			return;
		}
		if(weaponSelected != null){
			if(weaponSelected.isLoading) weaponSelected.drawLoading(graphics,this.body,this.img,false);
		}
		graphics.drawBitmap(this.img,PositionConverter.worldToScreenX(body.getWorldCenter().x)-this.img.getWidth()/2,PositionConverter.worldToScreenY(body.getWorldCenter().y)-this.img.getHeight()/2,this.paint);
	}

	/**
	 * Adds the munitions to a given weapon
	 *
	 * @param weapon the weapon
	 * @param munitions the munitions
	 */
	public void addMunitions(String weapon,int munitions){
		this.weapons.put(weapon,munitions);
	}

	/**
	 * Checks if a weapon is loaded.
	 *
	 * @return true, if a weapon is loaded, else false
	 */
	public boolean isWeaponLoaded() {
		if(weaponSelected == null) return false;
		return weaponSelected.isLoaded;
	}

	/**
	 * Gets the munitions of a given weapon name
	 *
	 * @param weapon the weapon of the weapon
	 * @return the munitions
	 */
	public int getMunitions(String weapon){
		return this.weapons.get(weapon);
	}

	/**
	 * 
	 *Fires a weapon 
	 */
	public abstract void fire();
}

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

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import fr.umlv.escapeirandroid.R;
import fr.umlv.escapeirandroid.game.EscapeIR;
import fr.umlv.escapeirandroid.utils.ImageManager;
import fr.umlv.escapeirandroid.utils.PositionConverter;

/**
 * The Class Bonus describes the specification of a bonus object
 */
public class Bonus extends Entity {

	/** The resource of image when the bonus is displayed */
	private static final int imagePath = R.drawable.bonus;
	private static final int DEFAULT_TEXT_SIZE = 30;

	/** Apply a force from top to bottom */
	private final Vec2 linearImpulse = new Vec2(0,-15);

	private final Typeface tf = Typeface.create("Serif",Typeface.BOLD);
	private Paint valuePaint = new Paint();
	private String weapon;

	/**
	 * Instantiates a new bonus.
	 *
	 * @param body the body
	 * @param unit the unit
	 * @param weapon the weapon
	 */
	public Bonus(Body body,int unit,String weapon){
		super(ImageManager.getInstance().loadImage(imagePath),unit);
		this.body = body;
		this.weapon = weapon;
		this.valuePaint.setTextSize(DEFAULT_TEXT_SIZE*EscapeIR.RATIO_WIDTH);
		this.valuePaint.setTypeface(tf);
		this.valuePaint.setARGB(255,12,142,9);
		this.valuePaint.setFlags(Paint.ANTI_ALIAS_FLAG);
		this.valuePaint.setFlags(Paint.FILTER_BITMAP_FLAG);
		this.valuePaint.setFlags(Paint.DITHER_FLAG);
		this.fixtureDef.filter.categoryBits = entityCategory.BONUS.getValue();
		this.fixtureDef.filter.maskBits = entityCategory.SHIP_HERO.getValue();
		this.categoryBit = entityCategory.BONUS.getValue();
		this.body.createFixture(fixtureDef);
		this.body.setUserData(this);
		this.body.setLinearVelocity(linearImpulse);
		this.body.setActive(true);
	}

	/**
	 * Draws the bonus with the graphics
	 * @param graphics the graphics to draw with
	 */
	@Override
	public void draw(Canvas graphics) {
		graphics.drawBitmap(this.img,PositionConverter.worldToScreenX(body.getWorldCenter().x)-this.img.getWidth()/2,PositionConverter.worldToScreenY(body.getWorldCenter().y)-this.img.getHeight()/2,this.paint);
		graphics.drawText(String.valueOf(healthPoint),PositionConverter.worldToScreenX(body.getWorldCenter().x),PositionConverter.worldToScreenY(body.getWorldCenter().y),this.valuePaint);
	}

	/**
	 * Gets the weapon.
	 *
	 * @return the weapon
	 */
	public String getWeapon(){
		return weapon;
	}
}

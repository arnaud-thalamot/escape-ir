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
import org.jbox2d.dynamics.FixtureDef;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import fr.umlv.escapeirandroid.R;
import fr.umlv.escapeirandroid.game.EscapeIR;
import fr.umlv.escapeirandroid.utils.ImageManager;
import fr.umlv.escapeirandroid.utils.PositionConverter;

/**
 * The Class Entity defines the specification of any object in the physic world that need anykind of treatment
 */
public abstract class Entity implements Displayable{

	public static final int EXPLOSING_LIMIT = 30;

	private static final int WALL_MASK = 1;
	private static final int SHIP_HERO_MASK = 2;
	private static final int SHIP_ENEMY_MASK = 4;
	private static final int WEAPON_ENEMY_MASK = 8;
	private static final int WEAPON_HERO_MASK = 16;
	private static final int BONUS_MASK = 32;
	private static final float FIXTUREDEF_DENSITY = 0.0f;
	private static final float FIXTUREDEF_FRICTION = 0.0f;
	private static final float FIXTUREDEF_RESTITUTION = -20f;

	protected Body body;
	protected Bitmap img;
	protected PolygonShape shape = new PolygonShape();
	protected FixtureDef fixtureDef = new FixtureDef();
	protected Paint paint = new Paint();
	protected SpriteAnimator explosion;
	protected int healthPoint;
	protected int explosingTime;
	protected int cpt;
	protected int categoryBit;
	protected final float imgWidthBy2;
	protected final float imgHeightBy2;

	/**
	 * The Enum entityCategory describing mask between Jbox2d bodies.
	 */
	public enum entityCategory {
		WALL(WALL_MASK),
		SHIP_HERO(SHIP_HERO_MASK),
		SHIP_ENEMY(SHIP_ENEMY_MASK),
		WEAPON_ENEMY(WEAPON_ENEMY_MASK),
		WEAPON_HERO(WEAPON_HERO_MASK),
		BONUS(BONUS_MASK);

		private final int value;

		/**
		 * Instantiates a new entity category.
		 *
		 * @param value the value
		 */
		private entityCategory(int value) {
			this.value = value;
		}

		/**
		 * Gets the value of the entity
		 *
		 * @return the value
		 */
		public int getValue() {
			return this.value;
		}
	};

	public enum EntityState {
		CREATED,
		ACTIVE,
		EXPLODED,
		TO_REMOVE,
	}

	public Entity(Bitmap imagePath,int hp){
		this.img = imagePath;
		this.healthPoint = hp;
		this.paint.setFlags(Paint.ANTI_ALIAS_FLAG);
		this.paint.setFlags(Paint.FILTER_BITMAP_FLAG);
		this.paint.setFlags(Paint.DITHER_FLAG);
		this.fixtureDef.shape = shape;
		this.fixtureDef.density = FIXTUREDEF_DENSITY;
		this.fixtureDef.friction = FIXTUREDEF_FRICTION;
		this.fixtureDef.restitution = FIXTUREDEF_RESTITUTION;
		this.imgWidthBy2 = (float)this.img.getWidth()/2;
		this.imgHeightBy2 = (float)this.img.getHeight()/2;
		this.explosion = new SpriteAnimator(ImageManager.getInstance().loadImage(R.drawable.explosion), 0, 0, 256, 256, 60, 40);
		shape.setAsBox(PositionConverter.screenToWorldX(this.imgWidthBy2),PositionConverter.screenToWorldY(EscapeIR.CURRENT_HEIGHT-imgHeightBy2));
	}

	/**
	 * Draws an explosion
	 *
	 * @param graphics the graphics
	 */
	public void drawExplosing(Canvas graphics){
		if(explosingTime<EXPLOSING_LIMIT){
			explosion.x = (int)(PositionConverter.worldToScreenX(body.getWorldCenter().x)-explosion.spriteWidth/2);
			explosion.y = (int)(PositionConverter.worldToScreenY(body.getWorldCenter().y)-explosion.spriteHeight/2);
			explosion.update(System.currentTimeMillis());
			explosion.draw(graphics,paint);
			explosingTime++;
		}
	}

	/**
	 * Gets the category bit.
	 *
	 * @return the category bit
	 */
	public int getCategoryBit(){
		return this.categoryBit;
	}

	/**
	 * Gets the explosing time.
	 *
	 * @return the explosing time
	 */
	public int getExplosingTime(){
		return this.explosingTime;
	}

	/**
	 * Gets the health point.
	 *
	 * @return the health point
	 */
	public int getHealthPoint() {
		return this.healthPoint;
	}

	/**
	 * Sets the health point.
	 *
	 * @param hp the new health point
	 */
	public void setHealthPoint(int hp) {
		this.healthPoint = hp;
	}

	/**
	 * Gets the body.
	 *
	 * @return the body
	 */
	public Body getBody(){
		return this.body;
	}

	/**
	 * Gets the user data.
	 *
	 * @return the user data
	 */
	public Object getUserData(){
		return this.body.getUserData();
	}
}

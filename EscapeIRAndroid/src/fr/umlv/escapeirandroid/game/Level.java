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
package fr.umlv.escapeirandroid.game;

import java.util.ArrayList;
import java.util.concurrent.PriorityBlockingQueue;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import fr.umlv.escapeirandroid.entity.AbstractShip;
import fr.umlv.escapeirandroid.entity.AbstractWeapon;
import fr.umlv.escapeirandroid.entity.BodyDescription;
import fr.umlv.escapeirandroid.entity.Bonus;
import fr.umlv.escapeirandroid.entity.Displayable;
import fr.umlv.escapeirandroid.entity.Entity;
import fr.umlv.escapeirandroid.entity.Entity.entityCategory;
import fr.umlv.escapeirandroid.entity.FactoryWeapon;
import fr.umlv.escapeirandroid.entity.HeroShip;
import fr.umlv.escapeirandroid.utils.PositionConverter;

/**
 * The Class Level.
 */

public class Level implements Displayable {

	/** The world manager. */
	final WorldManager worldManager;

	/** The game board. */
	private final GameBoard gameBoard;

	/** The heroship. */
	private static HeroShip heroship;

	/** The entities. */
	private static ArrayList<Entity> entities;

	/** The current step. */
	private int currentStep=0;

	/** The level duration. */
	private float levelDuration;

	/** The name. */
	private String name;

	private final int enemyShipMask;

	private static final String MISSILE = "Missile";
	private static final String FIREBALL = "Fireball";
	private static final String SHIBOLEET = "Shiboleet";
	private static final String TRIFORCE = "Triforce";
	private static final int HEROSHIP_START_HP = 30;
	private static final int HEROSHIP_START_NB_MISSILE = 100;
	private static final int HEROSHIP_START_NB_FIREBALL = 10;
	private static final int HEROSHIP_START_NB_SHIBOLEET = 5;
	private static final int HEROSHIP_START_NB_TRIFORCE = 2;

	/**
	 * Instantiates a new level.
	 *
	 * @param gameboard the gameboard
	 */
	public Level(GameBoard gameboard) {
		this.name = gameboard.getName();
		this.worldManager = new WorldManager();
		this.worldManager.createWorld();
		this.addHeroShip();
		this.gameBoard = gameboard;
		Level.entities = new ArrayList<Entity>();
		this.levelDuration = gameboard.getLevelDuration();
		this.enemyShipMask = Entity.entityCategory.SHIP_ENEMY.getValue();
	}

	/**
	 * Gets the background.
	 *
	 * @return the background
	 */
	public Bitmap getBackground(){
		return gameBoard.getBackground();
	}

	/**
	 * Step level.
	 */
	public void stepLevel(){

		addEnemies();
		int len = entities.size();
		Entity e;

		for(int i=0;i<len;i++){

			e = entities.get(i);

			if(e.getCategoryBit() == enemyShipMask){
				((AbstractShip) e).move();
				((AbstractShip) e).fire();
			}

			if(e.getExplosingTime()>=Entity.EXPLOSING_LIMIT){
				if(e.getCategoryBit() == enemyShipMask){
					if(System.currentTimeMillis()%2==0){
						createBonus(e.getBody().getWorldCenter(),(currentStep%5)+1,FactoryWeapon.oneOfCreatedWeapon());
					}
				}
				entities.remove(i);
				len--;
			}
		}

		len = entities.size();
		for(int i=0;i<len;i++){
			e = entities.get(i);
			if(e.getCategoryBit() == Entity.entityCategory.BONUS.getValue()){
				if(!e.getBody().isActive()){
					entities.remove(i);
					len--;
				}
			}
		}

		if((heroship != null) && (!heroship.isWeaponLoaded()) && (heroship.isLoadAsked())){heroship.loadWeapon();}

		if(heroship != null && (heroship.getExplosingTime()>=Entity.EXPLOSING_LIMIT)){
			heroship = null;
			return;
		}
		currentStep++;
		if(levelDuration>0){levelDuration--;}
		worldManager.checkCollision();
		return;
	}

	/**
	 * Checks if is level completed.
	 *
	 * @return true, if is level completed
	 */
	public boolean isLevelCompleted(){
		if((gameBoard.getEnemies().size()==0)&&(entities.size()==0)){
			return true;
		}
		return false;
	}

	/**
	 * Gets the level duration.
	 *
	 * @return the level duration
	 */
	public float getLevelDuration(){
		return levelDuration;
	}

	/**
	 * Draw all the entities in this level
	 * 
	 * @param graphics the canvas
	 */
	public void draw(Canvas graphics) {

		if(heroship != null){
			heroship.draw(graphics);
		}

		int len = entities.size();
		for(int i=0;i<len;i++){
			entities.get(i).draw(graphics);
		}
	}

	/**
	 * Apply gesture.
	 *
	 * @param vec the vec
	 * @param length the length
	 * @param looping the looping
	 */
	public static void applyLooping(Vec2 vec){
		if(heroship != null){
			heroship.applyLooping(vec);
		}
	}

	/**
	 * Creates the weapon.
	 *
	 * @param creatorPosition the creator position
	 * @param weapon the weapon
	 * @param isLaunchedByHeroShip the is launched by hero ship
	 * @return true, if successful
	 */
	public static boolean createWeapon(Vec2 creatorPosition,AbstractWeapon weapon,boolean isLaunchedByHeroShip){

		BodyDef bodydef = new BodyDef();
		bodydef.type = BodyType.DYNAMIC;
		bodydef.position.set(creatorPosition.x,creatorPosition.y);
		if(!isLaunchedByHeroShip) bodydef.angle = (float) Math.toRadians(180);		
		Body body = WorldManager.createBody(bodydef);
		if(body!=null){
			if(isLaunchedByHeroShip){
				weapon.setBody(body,entityCategory.WEAPON_HERO.getValue(),
						entityCategory.SHIP_ENEMY.getValue()+entityCategory.WEAPON_ENEMY.getValue());
			}
			else{
				weapon.setBody(body,entityCategory.WEAPON_ENEMY.getValue(),
						entityCategory.SHIP_HERO.getValue()+entityCategory.WEAPON_HERO.getValue());
			}
			entities.add(weapon);
			return true;
		}
		return false;
	}

	/**
	 * Creates the bonus.
	 *
	 * @param creatorPosition the creator position
	 * @param unit the unit
	 * @param weapon the weapon
	 */
	private void createBonus(Vec2 creatorPosition,int unit,String weapon){

		BodyDef bodydef = new BodyDef();
		bodydef.type = BodyType.DYNAMIC;
		bodydef.position.set(creatorPosition.x,creatorPosition.y);
		Body bonusBody = WorldManager.createBody(bodydef);
		if(bonusBody!=null){
			Bonus bonus = new Bonus(bonusBody,unit,weapon);
			entities.add(bonus);
		}
	}

	/**
	 * Gets the hero ship position.
	 *
	 * @return the hero ship position
	 */
	public static Vec2 getHeroShipPosition(){
		if(heroship!=null) return heroship.getBody().getWorldCenter();
		return new Vec2(0,0);
	}

	/**
	 * Sets the hero target.
	 *
	 * @param x the x
	 * @param y the y
	 */
	public static void setHeroTarget(float x,float y){
		if(heroship!=null)heroship.setTarget(x,y);
	}

	/**
	 * Can set hero weapon.
	 *
	 * @param weapon the weapon
	 * @return true, if successful
	 */
	public static boolean canSetHeroWeapon(String weapon){
		return heroship.canSelectWeapon(weapon);
	}

	/**
	 * Hero fire.
	 */
	public static void heroFire(){
		heroship.fire();
	}

	/**
	 * Gets the hero ship health point.
	 *
	 * @return the hero ship health point
	 */
	public static int getHeroShipHealthPoint(){
		if(heroship== null) return 0;
		return heroship.getHealthPoint();
	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Checks if is hero ship dead.
	 *
	 * @return true, if is hero ship dead
	 */
	public boolean isHeroShipDead(){
		return heroship==null;
	}

	/**
	 * Adds the life to hero.
	 *
	 * @param i the i
	 */
	public static void addLifeToHero(int i) {
		heroship.setHealthPoint(heroship.getHealthPoint()+i);
	}

	/**
	 * Creates the body.
	 *
	 * @param bodydef the bodydef
	 * @return the body
	 */
	public static Body createBody(BodyDef bodydef) {
		return WorldManager.createBody(bodydef);
	}

	/**
	 * Gets the hero munition.
	 *
	 * @param weaponName the weapon name
	 * @return the hero munition
	 */
	public static int getHeroMunition(String weaponName){
		if(heroship!=null){
			return heroship.getMunitions(weaponName);
		}
		return 0;
	}

	/**
	 * Load hero weapon.
	 */
	public static void loadHeroWeapon(){
		if(heroship!=null) heroship.loadWeapon();
	}

	/**
	 * Checks if is hero weapon loaded.
	 *
	 * @return true, if is hero weapon loaded
	 */
	public boolean isHeroWeaponLoaded(){
		return heroship.isWeaponLoaded();
	}

	/**
	 * Sets the hero loading.
	 *
	 * @param loadAsked the new hero loading
	 */
	public static void setHeroLoading(boolean loadAsked){
		if(heroship!=null) heroship.setLoadAsked(loadAsked);
	}

	/**
	 * Adds the hero ship to the level.
	 */
	private void addHeroShip(){

		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.DYNAMIC;
		bodyDef.position.set(PositionConverter.screenToWorldX(EscapeIR.CURRENT_WIDTH/2),PositionConverter.screenToWorldY(EscapeIR.CURRENT_HEIGHT)+15);
		bodyDef.fixedRotation = true;
		Body heroshipBody = WorldManager.createBody(bodyDef);

		heroship = new HeroShip(heroshipBody,HEROSHIP_START_HP);
		heroship.addMunitions(MISSILE,HEROSHIP_START_NB_MISSILE);
		heroship.addMunitions(FIREBALL,HEROSHIP_START_NB_FIREBALL);
		heroship.addMunitions(SHIBOLEET,HEROSHIP_START_NB_SHIBOLEET);
		heroship.addMunitions(TRIFORCE,HEROSHIP_START_NB_TRIFORCE);
		heroship.canSelectWeapon(MISSILE);

		worldManager.registerHeroShip(heroship);
	}

	/**
	 * Adds enemies to the level by peeking the queue of the gameboard
	 */
	private void addEnemies(){

		PriorityBlockingQueue<BodyDescription> enemies = gameBoard.getEnemies();
		BodyDescription current = enemies.peek();

		while((current!=null)&&(current.getTime()<=currentStep)){
			entities.add(gameBoard.popNextEnemyShip());
			current = enemies.peek();
		}
	}
}

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

import java.util.Iterator;
import java.util.Map;

import org.jbox2d.collision.AABB;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.joints.MouseJoint;
import org.jbox2d.dynamics.joints.MouseJointDef;

import fr.umlv.escapeirandroid.entity.Bonus;
import fr.umlv.escapeirandroid.entity.Entity;
import fr.umlv.escapeirandroid.entity.HeroShip;
import fr.umlv.escapeirandroid.utils.PositionConverter;

/**
 * The Class WorldManager describes the world and interact with Jbox2d world
 */
public class WorldManager {

	/** The world. */
	private static World world;

	/** The hero ship. */
	private static HeroShip heroShip;

	private static MouseJointDef heroshipMouseJointDef = new MouseJointDef();
	private static MouseJoint heroshipMouseJoint;

	/** The my contact listener instance. */
	private final MyContactListener myContactListenerInstance = new MyContactListener();

	/** The query callback. */
	private final MyQueryCallback queryCallback = new MyQueryCallback();

	/** The time step. */
	private static float timeStep = 1/60f;

	/** Bottom AABB for clean the world */
	private final AABB aabbBottom = new AABB(new Vec2(-60,-50),new Vec2(160,-50));

	/** Top AABB for clean the world */
	private final AABB aabbTop = new AABB(new Vec2(-60,150),new Vec2(160,150));

	/** Left AABB for clean the world */
	private final AABB aabbLeft = new AABB(new Vec2(-50,-60),new Vec2(-50,160));

	/** Right AABB for clean the world */
	private final AABB aabbRight = new AABB(new Vec2(150,-60),new Vec2(150,160));

	private final static float WALL_RESTITUTION = -20.0f;

	private final static float MOUSEJOINT_DAMPING_RATIO = 0f;

	private final static float MOUSEJOINT_FREQUENCY = 100000000f;

	private final static float MOUSEJOINT_MAXFORCE = 100000000000000000.0f;

	/**
	 * Creates the body.
	 *
	 * @param bodydef the bodydef
	 * @return the body
	 */
	public static Body createBody(BodyDef bodydef){
		return world.createBody(bodydef);
	}

	/**
	 * Register hero ship.
	 *
	 * @param h the hero
	 */
	public void registerHeroShip(HeroShip h){
		heroShip = h;
		heroshipMouseJoint = createJointOnHeroShip();
		GameView.setHeroData(heroshipMouseJoint);
	}

	/**
	 * Checks if is on hero ship.
	 *
	 * @param x the x
	 * @param y the y
	 * @return true, if is on hero ship
	 */
	public static boolean isOnHeroShip(int x,int y){

		Vec2 posHeroShip = heroShip.getBody().getWorldCenter();

		if((PositionConverter.screenToWorldX(x)< (posHeroShip.x+4)) && (PositionConverter.screenToWorldX(x)> (posHeroShip.x-4))
				&& (PositionConverter.screenToWorldY(y)< (posHeroShip.y+4)) && (PositionConverter.screenToWorldY(y)> (posHeroShip.y-4))){
			return true;
		}
		return false;
	}

	/**
	 * Creates the world.
	 */
	public void createWorld(){
		world = new World(new Vec2(0,0),true);
		world.setContactListener(myContactListenerInstance);
		setWall();
	}

	/**
	 * Check collision.
	 */
	public void checkCollision(){

		Entity[] entities = new Entity[2];		
		Iterator<Map.Entry<Entity, Entity>> it = myContactListenerInstance.iterator();

		while(it.hasNext()){

			Map.Entry<Entity,Entity> current = it.next();
			Entity currentKey = current.getKey();
			Entity currentValue = current.getValue();

			if(currentKey != null){

				Bonus b = null;
				int bonusValue = Entity.entityCategory.BONUS.getValue();

				if(currentKey.getCategoryBit() == bonusValue){
					b = (Bonus) currentKey.getUserData();
				}
				if(currentValue.getCategoryBit() == bonusValue){
					b = (Bonus) currentValue.getUserData();
				}
				if(b!=null){
					heroShip.addMunitions(b.getWeapon(),heroShip.getWeaponMunition(b.getWeapon())+b.getHealthPoint());
					b.getBody().setActive(false);
					continue;
				}

				entities[0] = currentKey;
				entities[1] = currentValue;

				int e0 = entities[0].getHealthPoint();
				int e1 = entities[1].getHealthPoint();

				entities[0].setHealthPoint(e0-e1);
				entities[1].setHealthPoint(e1-e0);

				for(int i=0;i<entities.length;i++){
					if((entities[i].getCategoryBit() == Entity.entityCategory.WEAPON_ENEMY.getValue())
							|| (entities[i].getCategoryBit() == Entity.entityCategory.WEAPON_HERO.getValue())
							|| (entities[i].getHealthPoint() <= 0)){
						if(entities[i].getCategoryBit() == Entity.entityCategory.SHIP_ENEMY.getValue()) GameView.addToScore(100);
						entities[i].getBody().setActive(false);
					}
				}
			}
		}

		myContactListenerInstance.clear();
		cleanWorld();
		world.step(timeStep,8,3);
		world.clearForces();
	}

	/** Remove the mousejoint of the heroship */
	public static void unJointHeroShip(){
		world.destroyJoint(heroshipMouseJoint);
	}

	/** Add mousejoint on the Heroship */
	public static void JointHeroShip(){
		heroshipMouseJointDef.target.set(heroShip.getBody().getWorldCenter());
		heroshipMouseJoint = (MouseJoint)world.createJoint(heroshipMouseJointDef);
		GameView.setHeroData(heroshipMouseJoint);
	}

	/** Create groundbody for mousejoint
	 * 
	 * @return the groundbody
	 */
	public static Body createGroundBody(){
		BodyDef bd = new BodyDef();
		bd.fixedRotation=true;
		bd.type = BodyType.STATIC;
		return world.createBody(bd);
	}

	/** Creates mousjoint thanks to the mousjointdef
	 * 
	 * @param mjd
	 * @return the mousejoint
	 */
	public static MouseJoint createMouseJoint(MouseJointDef mjd){
		return (MouseJoint) world.createJoint(mjd);
	}

	/**
	 * Sets the wall.
	 */
	private void setWall(){

		PolygonShape shape = new PolygonShape();
		BodyDef bodyDef = new BodyDef();
		FixtureDef fixtureDef = new FixtureDef();

		bodyDef.type = BodyType.STATIC;
		fixtureDef.shape = shape;
		fixtureDef.restitution = WALL_RESTITUTION;
		fixtureDef.filter.categoryBits = Entity.entityCategory.WALL.getValue();

		float widthRatio = EscapeIR.CURRENT_WIDTH/2;
		float heightRatio =0;

		/* Wall at the bottom */
		bodyDef.position.set(PositionConverter.screenToWorldX(widthRatio),0);
		shape.setAsBox(PositionConverter.screenToWorldX(widthRatio),0.1f);
		world.createBody(bodyDef).createFixture(fixtureDef);

		/* Wall at the top */
		bodyDef.position.set(PositionConverter.screenToWorldX(widthRatio),PositionConverter.screenToWorldY(heightRatio));
		shape.setAsBox(PositionConverter.screenToWorldX(widthRatio),0.1f);
		world.createBody(bodyDef).createFixture(fixtureDef);

		/* Wall at the left */
		bodyDef.position.set(0,PositionConverter.screenToWorldY(heightRatio)/2);
		shape.setAsBox(0.1f,PositionConverter.screenToWorldY((heightRatio))/2);
		world.createBody(bodyDef).createFixture(fixtureDef);

		/* Wall at the right */
		bodyDef.position.set(PositionConverter.screenToWorldX(EscapeIR.CURRENT_WIDTH),PositionConverter.screenToWorldY(heightRatio)/2);
		shape.setAsBox(0.1f,PositionConverter.screenToWorldY(heightRatio)/2);
		world.createBody(bodyDef).createFixture(fixtureDef);
	}

	/**
	 * Clean world.
	 */
	private void cleanWorld(){

		world.queryAABB(queryCallback,aabbTop);
		world.queryAABB(queryCallback,aabbBottom);
		world.queryAABB(queryCallback,aabbLeft);
		world.queryAABB(queryCallback,aabbRight);

		for (int i = 0; i < queryCallback.size(); i++) {
			queryCallback.setInactive(i);
		}

		Body body = world.getBodyList();
		while(body != null ){
			if (!body.isActive()) {
				WorldManager.world.destroyBody(body);
			}
			body = body.getNext();
		}
	}

	/** Create the mousejoint for heroship
	 * 
	 * @return the mousejoint
	 */
	private static MouseJoint createJointOnHeroShip(){
		heroshipMouseJointDef.bodyA = createGroundBody();
		heroshipMouseJointDef.bodyB = heroShip.getBody();
		heroshipMouseJointDef.dampingRatio = MOUSEJOINT_DAMPING_RATIO;
		heroshipMouseJointDef.frequencyHz = MOUSEJOINT_FREQUENCY;
		heroshipMouseJointDef.maxForce = (MOUSEJOINT_MAXFORCE * heroShip.getBody().getMass());
		heroshipMouseJointDef.collideConnected= true;
		heroshipMouseJointDef.target.set(heroShip.getBody().getWorldCenter());
		return (MouseJoint) world.createJoint(heroshipMouseJointDef);
	}
}

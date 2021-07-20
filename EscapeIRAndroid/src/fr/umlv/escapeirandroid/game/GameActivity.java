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

import org.jbox2d.common.Vec2;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.gesture.Gesture;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.GestureOverlayView.OnGesturePerformedListener;
import android.gesture.Prediction;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;
import fr.umlv.escapeirandroid.R;
import fr.umlv.escapeirandroid.utils.FileManager;
import fr.umlv.escapeirandroid.utils.PositionConverter;
import fr.umlv.escapeirandroid.utils.Scaler;

/**
 * This class launch the game, manage click event and gesture event
 *
 * @author THALAMOT Arnaud
 * @author VASSEUR Simon
 * @version $Revision: 1.0 $
 */
public class GameActivity extends Activity implements OnClickListener{

	private static final String TAG = GameActivity.class.getSimpleName();
	
	private static final String MISSILE = "Missile";
	private static final String FIREBALL = "Fireball";
	private static final String SHIBOLEET = "Shiboleet";
	private static final String TRIFORCE = "Triforce";
	private static final String MEDIC_GESTURE_NAME_IN_ASSET = "medic";
	private static final String LOOPINGLEFT_GESTURE_NAME_IN_ASSET = "loopingLeft";
	private static final String LOOPINGRIGHT_GESTURE_NAME_IN_ASSET = "loopingRight";
	private static final int GESTURE_SCORE_PREDICTION = 3;
	private static final int MAXIMUM_HEROSHIP_HP = 30;
	private static final int HP_TO_BE_ADDED_TO_HEROSHIP = 2;
	private static final int MINIMUM_THRESHOLD_ACCELERATION = 300;

	private GestureOverlayView gestureOverlayView;
	private GestureLibrary gestureLibrary;
	private GestureDetector gestureScanner;
	private GameView gameview;
	private final Vec2 loopingLeftforce = new Vec2((float)(-150*Math.cos(Math.toRadians(0))),0);
	private final Vec2 loopingRightforce = new Vec2((float)(-150*Math.cos(Math.toRadians(180))),0);

	/** This method launch the game loop and manage gesture with a gesture overlay view
	 * 
	 * @param savedInstanceState the Bundle
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		/** Launch the layout with the menubar and the surfaceview */
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.game);

		/** Scale the gameview */
		Scaler.scaleGameView(this,EscapeIR.RESOURCES);
		gameview = (GameView) findViewById(R.id.game);
		gameview.setLevel(getIntent().getStringExtra(EscapeIR.LEVEL));
		
		/** Create the gesture overlay that will manage gesture */
		gestureOverlayView = (GestureOverlayView)findViewById(R.id.gestures);
		gestureOverlayView.setGestureVisible(false);
		gestureOverlayView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return gestureScanner.onTouchEvent(event);
			}
		});

		gestureLibrary = FileManager.getInstance().loadGestureLibrary();
		gestureOverlayView.addOnGesturePerformedListener(addGesturePerformedListener());
		gestureScanner = new GestureDetector(this,addSimpleOnGestureListener());
		Log.d(TAG,"GameActivity created");
	}

	/** Manage the event of user click on the menubar and load weapon
	 * 
	 * @param v the view
	 */
	@Override
	public void onClick(View v) {
		boolean res = false;
		switch(v.getId()){

		case R.id.missile_button:
			res = Level.canSetHeroWeapon(MISSILE);
			break;
		case R.id.fireball_button:
			res = Level.canSetHeroWeapon(FIREBALL);
			break;
		case R.id.shiboleet_button:
			res = Level.canSetHeroWeapon(SHIBOLEET);
			break;
		case R.id.triforce_button:
			res = Level.canSetHeroWeapon(TRIFORCE);
			break;
		}
		if(!res){
			Level.loadHeroWeapon();
		}
	}

	/** Block return button */
	@Override
	public void onBackPressed() {
		// Do nothing
	}

	/** Pass the touch screen motion event down to the target view, or this view if it is the target.
	 *
	 * @param : event	The motion event to be dispatched.
	 * @return : True if the event was handled by the view, false otherwise.
	 */
	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		gameview.dispatchTouchEvent(event);
		return super.dispatchTouchEvent(event);
	}

	/** Watch if a gesture has been made and manage attached event */
	private OnGesturePerformedListener addGesturePerformedListener(){

		return new OnGesturePerformedListener(){

			@Override
			public void onGesturePerformed(GestureOverlayView view, final Gesture gesture) {

				ArrayList<Prediction> prediction = gestureLibrary.recognize(gesture);
				if(prediction.size() > 0){
					Prediction p = prediction.get(0);

					if (p.score > GESTURE_SCORE_PREDICTION) {

						if(p.name.equals(MEDIC_GESTURE_NAME_IN_ASSET)){
							if(Level.getHeroShipHealthPoint()<MAXIMUM_HEROSHIP_HP){
								Level.addLifeToHero(HP_TO_BE_ADDED_TO_HEROSHIP);
							}
						}

						if(p.name.equals(LOOPINGLEFT_GESTURE_NAME_IN_ASSET)){
							Level.applyLooping(loopingLeftforce);
						}

						if(p.name.equals(LOOPINGRIGHT_GESTURE_NAME_IN_ASSET)){
							Level.applyLooping(loopingRightforce);
						}

						Toast toast = Toast.makeText(getApplicationContext(),"Gesture : "+p.name, Toast.LENGTH_SHORT);
						toast.show();
						Bitmap bg = gesture.toBitmap((int)gesture.getBoundingBox().width(),(int)gesture.getBoundingBox().height(), 0, Color.GREEN);
						gameview.drawGesture(bg,gesture.getBoundingBox().left,gesture.getBoundingBox().top);
					}
				}
			}
		};
	}

	/** Manage the fire event, if a acceleration has been made, the heroship launch the weapon */
	private GestureDetector.SimpleOnGestureListener addSimpleOnGestureListener(){

		return new GestureDetector.SimpleOnGestureListener() {

			public boolean onDown(MotionEvent event) {
				return true;
			}

			public boolean onFling(MotionEvent event1, MotionEvent event2, float velocityX, float velocityY) {

				if((Math.abs(velocityX)>=MINIMUM_THRESHOLD_ACCELERATION)&&(Math.abs(velocityY)>=MINIMUM_THRESHOLD_ACCELERATION)){

					Vec2 heroPos = Level.getHeroShipPosition();
					float destx = event2.getX() - event1.getX() + PositionConverter.worldToScreenX(heroPos.x);
					float desty = event2.getY() - event1.getY() + PositionConverter.worldToScreenY(heroPos.y);
					Level.setHeroTarget(PositionConverter.screenToWorldX(destx), PositionConverter.screenToWorldY(desty));
					Level.heroFire();
					return true;
				}
				return false;
			}
		};
	}
}

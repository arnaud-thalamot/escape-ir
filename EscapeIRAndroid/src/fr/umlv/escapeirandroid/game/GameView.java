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

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.joints.MouseJoint;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import fr.umlv.escapeirandroid.R;
import fr.umlv.escapeirandroid.utils.ImageManager;
import fr.umlv.escapeirandroid.utils.PositionConverter;

/**
 * The Class GameView is the main game view
 */
public class GameView extends SurfaceView {

	private static final String TAG = GameView.class.getSimpleName();

	/** A reference to the gamethread */
	private GameThread gameLoop;

	/** The time reference for the calculation loop */
	protected float time = 0.0f;	

	/** The time differential between two steps, initialized to correspond to 60fps */
	protected float timeDifferential = 1/60f;

	/** The level name*/
	private String levelName;

	/** The current time of the loop iteration */
	protected long currentTime = System.currentTimeMillis();

	protected float accumulator = 0.0f;

	/** The frame counter used to display something during a given duration */
	private int frameCounter = 0;

	/** The background position at the moment */
	private int backgroundPosition = 0;

	/** A reference to the background of the current level */
	private Bitmap background;

	private static int STEP_BACKGROUND = EscapeIR.CURRENT_HEIGHT/600;

	private static int NB_FRAME_END_DISPLAYING = 256;
	private static final String MISSILE = "Missile";
	private static final String FIREBALL = "Fireball";
	private static final String SHIBOLEET = "Shiboleet";
	private static final String TRIFORCE = "Triforce";

	private float previousX = 0;
	private float previousY = 0;
	private float currentDestX = 0;
	private float currentDestY = 0;
	private Vec2 newDest = new Vec2();
	private static MouseJoint mj;
	private int backgroundHeight;
	private final SurfaceHolder holder;
	private Level level;
	private int frames = 0;
	private Paint paint = new Paint();
	private int stepBackground;
	private Bitmap end;
	public static int score = 0;
	protected State.gameState gameState;
	private State.scrollingState scrollingState;
	private final Context context;

	private Button missileStock ;
	private Button fireballStock;
	private Button shiboleetStock;
	private Button triforceStock;
	private TextView scoreText;
	private ProgressBar lifebar;

	public static String fps = "";	
	private long fpsTime;

	/**
	 * Instantiates the game view
	 *
	 * @param context the context
	 * @param attrs the attributes
	 */
	public GameView(Context context,AttributeSet attrs) {
		super(context,attrs);
		this.holder = getHolder();
		this.holder.setFormat(0x00000004);
		this.context = context;
		this.fpsTime = System.currentTimeMillis();
		this.gameState = State.gameState.BETWEEN_TWO_LEVEL;
		this.scrollingState = State.scrollingState.SCROLLING;
		this.frameCounter = 0;
		this.paint.setColor(Color.BLACK);
		callback();
	}

	/** Manage the click event on this surface view
	 * 
	 * @param event the pool of event
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {

		float currentX = event.getX();
		float currentY = event.getY();

		if(level!=null){

			if(event.getAction()==MotionEvent.ACTION_DOWN){

				Vec2 heroPos = Level.getHeroShipPosition();
				if(heroPos!=null){
					previousX = currentX;
					previousY = currentY;
					currentDestX = PositionConverter.worldToScreenX(heroPos.x);
					currentDestY = PositionConverter.worldToScreenY(heroPos.y);

					newDest.x = heroPos.x;
					newDest.y = heroPos.y;

					mj.setTarget(newDest);
					return true;
				}
			}

			if(event.getAction()==MotionEvent.ACTION_MOVE){

				float destx = currentX - previousX + currentDestX;
				float desty = currentY - previousY + currentDestY;

				newDest.x = PositionConverter.screenToWorldX(destx);
				newDest.y = PositionConverter.screenToWorldY(desty);

				if(mj!=null) mj.setTarget(newDest);

				currentDestX = destx;
				currentDestY = desty;
				previousX = currentX;
				previousY = currentY;
				return true;
			}

			if(event.getAction()==MotionEvent.ACTION_UP){
				previousX = 0;
				previousY = 0;
				currentDestX = 0;
				currentDestY = 0;
				mj.setTarget(Level.getHeroShipPosition());
				return true;
			}
		}
		return false;
	}

	/** Set the name of the current level
	 * 
	 * @param levelName the name
	 */
	public void setLevel(String levelName){
		this.levelName = levelName;
	}

	/** Update the level */
	public void update(){
		level.stepLevel();
	}

	/**
	 * Displays the game graphic part according to the state of the game
	 *
	 *@see State.actionState
	 * @param currentTime the current time
	 * @param graphics the graphics
	 * @param context the context
	 */
	public void render(Canvas graphics,long currentTime){

		switch (gameState) {
		case BETWEEN_TWO_LEVEL:
			displayBetweenTwoLevel();
			break;
		case IN_PROGRESS:
			Level.loadHeroWeapon();
			Level.setHeroLoading(true);
			drawEventsDuringGame(graphics,currentTime);
			break;		
		case FINISHED:
			displayEndGame(graphics,true);
			break;
		case HERO_DEAD:
			displayEndGame(graphics,false);
			break;
		default:
			break;
		}
	}

	/**
	 * Adds the to score.
	 *
	 * @param value the value
	 */
	public static void addToScore(int value){
		score += value;
	}

	/**
	 * Update the time of the calculation loop adding a value
	 *
	 * @param timeUpdate the time update
	 */
	public void updateTime(float timeUpdate) {
		this.time += timeUpdate ;
	}

	/**
	 * Draw events during game.
	 *
	 * @param graphics the graphics
	 * @param currentTime the current time
	 */
	public void drawEventsDuringGame(Canvas graphics, long currentTime){

		if(level.isHeroShipDead()){
			this.gameLoop.stop();
			gameState = State.gameState.HERO_DEAD;
			return;
		}

		if(level.getLevelDuration()==0){scrollingState = State.scrollingState.STOPPED;}

		manageFps(currentTime);
		drawBackground(graphics);
		level.draw(graphics);
		((Activity)context).runOnUiThread(refreshWeaponBar);

		if(level.isLevelCompleted()){
			gameState = State.gameState.FINISHED;
		}
	}

	/** Display the gesture on the screen
	 * 
	 * @param b the bitmap
	 * @param left the position left of the bitmap
	 * @param top the position top of the bitmap
	 */
	public void drawGesture(Bitmap b,float left,float top){
		Canvas canvas = holder.lockCanvas();		
		canvas.drawBitmap(b,left,top, null);
		holder.unlockCanvasAndPost(canvas);
	}

	/** Set the mousejoint to the hero (groundbody)
	 * 
	 * @param mouseJoint the Jbox2d mousejoint
	 */
	public static void setHeroData(MouseJoint mouseJoint){
		mj = mouseJoint;
	}

	/** Update the menubar */
	private final Runnable refreshWeaponBar= new Runnable() {

		@Override
		public void run() {
			missileStock.setText(Integer.toString(Level.getHeroMunition(MISSILE)));
			fireballStock.setText(Integer.toString(Level.getHeroMunition(FIREBALL)));
			shiboleetStock.setText(Integer.toString(Level.getHeroMunition(SHIBOLEET)));
			triforceStock.setText(Integer.toString(Level.getHeroMunition(TRIFORCE)));
			scoreText.setText(EscapeIR.RESOURCES.getString(R.string.score_string)+Integer.toString(score));
			lifebar.setProgress((Level.getHeroShipHealthPoint()));
		}
	};

	/** This function launch the gamethread */
	private void callback(){
		getHolder().addCallback(new Callback() {

			@Override
			public void surfaceChanged(SurfaceHolder holder, int format, int width,int height) {
				Log.d(TAG, "GameView changed");
			}

			@Override
			public void surfaceCreated(SurfaceHolder holder) {
				Log.d(TAG, "GameView created");
				stepBackground = STEP_BACKGROUND;
				gameLoop = new GameThread(getHolder(),GameView.this);
				gameLoop.start();
			}

			@Override
			public void surfaceDestroyed(SurfaceHolder holder) {
				Log.d(TAG, "GameView is being destroyed");
				gameLoop.stop();
			}
		});
	}

	/**
	 * Reset display manager.
	 *
	 * @param startTime the start time
	 */
	private void resetDisplayManager(long startTime){
		fpsTime = startTime;
		gameState = State.gameState.BETWEEN_TWO_LEVEL;
		scrollingState = State.scrollingState.SCROLLING;
	}

	/**
	 * Display end game.
	 *
	 * @param graphics the graphics
	 * @param hasWon the has won
	 */
	private void displayEndGame(Canvas graphics,boolean hasWon){

		if(frameCounter==0){
			this.gameLoop.stop();
			((Activity)context).runOnUiThread(new Runnable() {
				@Override
				public void run() {
					((Activity)context).findViewById(R.id.menubar).setVisibility(View.GONE);
				}
			});

			this.end = ImageManager.getInstance().loadImage(R.drawable.end);	
			paint.setColor(Color.LTGRAY);
			paint.setTextSize(30);
		}

		else if(frameCounter<NB_FRAME_END_DISPLAYING){
			graphics.drawBitmap(end, 0,0,null);
			this.level = null;
			if(hasWon){
				graphics.drawText("CONGRATULATIONS ! ", 50, 200,paint);
				graphics.drawText("You finished level "+levelName+" !",50,255,paint);
				graphics.drawText("Your score : "+score, 50, 310,paint);
			}
			else{
				graphics.drawText("Game Over ! ", 50, 200,paint);
				graphics.drawText("Your hero died alone",50,255,paint);
				graphics.drawText("in the empty space !",50,300,paint);
				graphics.drawText("Your score was: "+score, 50, 400,paint);
			}
		}

		else{finish();}
		frameCounter++;
	}

	/**
	 * Display between two level.
	 *
	 * @param graphics the graphics
	 */
	private void displayBetweenTwoLevel(){

		if(frameCounter == 0){
			level = null;
			resetDisplayManager(System.currentTimeMillis());
			level = FactoryLevel.createLevel(levelName);
			if(level == null){
				((Activity)context).runOnUiThread(new Runnable() {

					@Override
					public void run() {
						Toast.makeText(getContext(),"Unable to load level : "+levelName, Toast.LENGTH_LONG).show();
					}
				});
				finish();
			}
			else {
				this.background = level.getBackground();
				this.backgroundHeight = background.getHeight();
				this.missileStock = ((Button)((Activity)context).findViewById(R.id.missile_button));
				this.fireballStock = ((Button)((Activity)context).findViewById(R.id.fireball_button));
				this.shiboleetStock = ((Button)((Activity)context).findViewById(R.id.shiboleet_button));
				this.triforceStock = ((Button)((Activity)context).findViewById(R.id.triforce_button));
				this.scoreText = ((TextView)((Activity)context).findViewById(R.id.score));
				this.lifebar = ((ProgressBar)((Activity)context).findViewById(R.id.life));
			}
		}
		frameCounter++;

		if(frameCounter>30){
			gameState = State.gameState.IN_PROGRESS;

			((Activity)context).runOnUiThread(new Runnable() {

				@Override
				public void run() {
					((Activity)context).findViewById(R.id.menubar).setVisibility(View.VISIBLE);
					((Chronometer)((Activity)context).findViewById(R.id.chronometer)).setBase(SystemClock.elapsedRealtime());
					((Chronometer)((Activity)context).findViewById(R.id.chronometer)).start();
					Toast.makeText(getContext(),"Level : "+level.getName(), Toast.LENGTH_SHORT).show();
				}
			});
			frameCounter = 0;
		}
	}

	/**
	 * Draws the background according to the scrolling state
	 *
	 * @see State.scrollingState
	 * @param graphics the graphics
	 */
	private void drawBackground(Canvas graphics){
		switch (scrollingState) {
		case SCROLLING:
			if(backgroundPosition == 0){
				graphics.drawBitmap(background,0,0,paint);
				backgroundPosition+=stepBackground;
				return;
			}

			if(backgroundPosition>=EscapeIR.CURRENT_HEIGHT){
				backgroundPosition = backgroundPosition-backgroundHeight;
			}

			graphics.drawBitmap(background,0,backgroundPosition,paint);
			graphics.drawBitmap(background,0,(backgroundPosition-backgroundHeight),paint);
			backgroundPosition+=stepBackground;
			break;
		case STOPPED:
			graphics.drawBitmap(background,0,0,null);
			break;
		default:
			break;
		}
	}

	/** Finish the activity */
	private void finish(){
		gameLoop.stop();
		((Activity) context).finish();
	}


	private void manageFps(long currentTime){

		frames++;
		if(currentTime - fpsTime>1000){

			fps = (frames+"FPS");
			frames = 0;
			fpsTime = currentTime;
		}
		//		System.out.println(fps);
	}
}

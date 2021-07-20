/**ESIPE - IR 2012/2013 - EscapeIR project for Android**/

package fr.umlv.escapeirandroid.editor;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.Toast;
import fr.umlv.escapeirandroid.R;
import fr.umlv.escapeirandroid.editor.EditorActivity.onLevelChangedListener;
import fr.umlv.escapeirandroid.game.EscapeIR;

/**
 * The Class MapEditorView provides a view to move, to drag and drop, to delete dragable enemies, useful to build a game map
 *
 * @author THALAMOT Arnaud
 * @author VASSEUR Simon
 * @version $Revision: 1.0 $
 */

public class MapEditorView extends View{

	/**
	 * Listener to push data to the activity, e.g events on the list dragables enemies
	 *
	 */
	
	public interface MapEditorListener {
		public void onAddedEnnemy(DragableEnemy enemy);
		public void onDeletedEnemy(DragableEnemy enemy);
	}
	
	/**
	 * Constant to avoid calculation on float approximation
	 */
	
	private static final float EPSILON_FLOAT = 0.001f;
	
	/**
	 * Name tags of the objects saved and restores from bundle
	 */
	
	private static final String BUNDLE_SAVED_LIST = "dragables";
	private static final String BUNDLE_SAVED_MAP_DURATION = "mapDuration";
	private static final String BUNDLE_SAVED_HEIGTH = "heigth";
	private static final String BUNDLE_SAVED_BACKGROUND = "background";
	private static final String BUNDLE_SAVED_SUPER = "super";
	
	private Paint timeLinePaint;
	
	private ArrayList<DragableEnemy> dragablesEnemyList;
	private BitmapDrawable levelBackground;
	private DragableEnemy currentDragged = null;
	private GestureDetector longClickGestureDetector;
	private boolean isDragging = false;
	private MapEditorListener customListener;
	private float mapDuration;
	private int height;
	
	/**
	 * Set a listener on the activity to get the list of enemies from the activity
	 */
	
	private final onLevelChangedListener onLevelChangedListener = new onLevelChangedListener() {
		
		@Override
		public void onLevelLoaded(ArrayList<DragableEnemy> ennemies,
				float duration, Bitmap background) {
			dragablesEnemyList = new ArrayList<DragableEnemy>(ennemies);
			
			levelBackground = new BitmapDrawable(getResources(),background);
			levelBackground.setTileModeXY(TileMode.REPEAT, TileMode.REPEAT);
			
			if((Math.abs(mapDuration - 0f) < EPSILON_FLOAT)){
				resizeHeight(duration);
				return;
			}
			
			if(!(Math.abs(duration - mapDuration) < EPSILON_FLOAT)){
			resizeMap(duration);
			return;
			}
			resizeHeight(duration);
		}
	};
	

	/**
	 * Used to detect LongClick and DoubleTap event
	 */
	
	GestureDetector.SimpleOnGestureListener longClickListener = new GestureDetector.SimpleOnGestureListener() {

		//Detects the longPress event and allow to drag the selected enemy
		
		public void onLongPress(MotionEvent event) {

			final float x = event.getX();
			final float y = event.getY();

			for (int i = 0; i < dragablesEnemyList.size(); i++) {
				DragableEnemy dragableEnemy = dragablesEnemyList.get(i);
				if (dragableEnemy.rect.contains(x, y)) {
					currentDragged = dragableEnemy;
					customListener.onDeletedEnemy(dragableEnemy);
					currentDragged.moveTo(x, y);
					currentDragged.select();
					invalidate();
					isDragging = true;
					Context context = getContext();
					Toast.makeText(context,context.getResources().getString(R.string.map_editor_toast_selected), Toast.LENGTH_SHORT).show();
				}
			}
		}
		
		//Detects the DoubleTap event, deletes the enemy "doubletaped", and tells the activity to do the same via the listener

		public boolean onDoubleTap(MotionEvent event) {
			
			final float x = event.getX();
			final float y = event.getY();

			for (int i = 0; i < dragablesEnemyList.size(); i++) {
				DragableEnemy dragableEnemy = dragablesEnemyList.get(i);
				if (dragableEnemy.rect.contains(x, y)) {
					dragablesEnemyList.remove(dragableEnemy);
					customListener.onDeletedEnemy(dragableEnemy);
					invalidate();
					Context context = getContext();
					Toast.makeText(context,context.getResources().getString(R.string.map_editor_toast_deleted), Toast.LENGTH_SHORT).show();
					return true;
				}
			}
			return false;
		};
	};
	
	public MapEditorView(Context context,AttributeSet attrs) {
		super(context,attrs);
		((EditorActivity)context).setOnLevelChangedListener(onLevelChangedListener);
		timeLinePaint = new Paint();
		timeLinePaint.setColor(Color.LTGRAY);
		timeLinePaint.setTextSize(20);
		longClickGestureDetector = new GestureDetector(context, longClickListener);
	}
	
	/**
	 * Moves the current dragged enemy
	 */
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {

		longClickGestureDetector.onTouchEvent(event);

		if(isDragging){

			final float x = event.getX();
			final float y = event.getY();

			switch (event.getAction()) {

			case MotionEvent.ACTION_MOVE:
				currentDragged.moveTo(x, y);
				invalidate();
				return true;
			case MotionEvent.ACTION_UP:
				customListener.onAddedEnnemy(currentDragged);
				currentDragged.unSelect();
				currentDragged = null;
				isDragging = false;
				invalidate();
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Draws enemies and the background
	 */

	@Override
	protected void onDraw(Canvas canvas) {
		drawBackground(canvas);
		drawTimeLine(canvas);
		for (int i = 0; i < dragablesEnemyList.size(); i++) {
			DragableEnemy d = dragablesEnemyList.get(i);
			d.updatePosition();
			d.draw(canvas);
			super.onDraw(canvas);
		}
		if(currentDragged!=null){
			currentDragged.updatePosition();
			currentDragged.draw(canvas);
		}
	}
	
	/**
	 * Draws the background accordingly to the canvas size
	 * @param canvas canvas where to draw the background
	 */

	private void drawBackground(Canvas canvas) {
		levelBackground.setBounds(canvas.getClipBounds());
		levelBackground.draw(canvas);
	}
	
	/**
	 * Draws the red timeline, scaled on the device size
	 * @param canvas canvas where to draw the timeline
	 */

	private void drawTimeLine(Canvas canvas){
		int time = 1;
		for (int i = height-EscapeIR.BACKGROUND_STEP; i > 0 ; i-=EscapeIR.BACKGROUND_STEP) {
			canvas.drawText(String.valueOf(time), 10, i, timeLinePaint);
			canvas.drawLine(0, i, canvas.getWidth(), i, timeLinePaint);
			time++;
		}
	}
	
	/**
	 * Called when an enemy is dropped on the map, create a new enemy and adds it to the current list, then tells it to the activity via the listener
	 * @param bitmap sprite of the enemy
	 * @param x X coordinate of the enemy
	 * @param y Y coordinate of the enemy
	 * @param ennemyType string representing the enemyType of the enemy
	 */

	public void onDropped(Bitmap bitmap,int x,int y,String ennemyType){
		DragableEnemy newDragableEnemy = new DragableEnemy(bitmap, x, y,ennemyType);
		dragablesEnemyList.add(newDragableEnemy);
		customListener.onAddedEnnemy(newDragableEnemy);
		invalidate();
	}

	/**
	 * Saves the list of enemies, the duration and the heigth in the bundle
	 */
	
	@Override
	protected Parcelable onSaveInstanceState() {
		Bundle bundle = new Bundle();
		final Parcelable superState = super.onSaveInstanceState();
		bundle.putParcelable(BUNDLE_SAVED_SUPER, superState);
		bundle.putParcelableArrayList(BUNDLE_SAVED_LIST, dragablesEnemyList);
		bundle.putFloat(BUNDLE_SAVED_MAP_DURATION, mapDuration);
		bundle.putInt(BUNDLE_SAVED_HEIGTH, height);
		bundle.putParcelable(BUNDLE_SAVED_BACKGROUND, levelBackground.getBitmap());
		return bundle;
	}

	/**
	 * Restore the list of enemies, the duration and the heigth from a bundle
	 */
	
	@Override
	protected void onRestoreInstanceState(Parcelable state) {
		if (state instanceof Bundle) {
			final Bundle bundle = (Bundle) state;
			dragablesEnemyList = new ArrayList<DragableEnemy>();
			ArrayList<Parcelable> mapEditorViewState = bundle.getParcelableArrayList(BUNDLE_SAVED_LIST);
			for (int i = 0; i < mapEditorViewState.size(); i++) {
				dragablesEnemyList.add((DragableEnemy)mapEditorViewState.get(i));
			}
			mapDuration = bundle.getFloat(BUNDLE_SAVED_MAP_DURATION);
			height = bundle.getInt(BUNDLE_SAVED_HEIGTH);
			levelBackground = new BitmapDrawable(getResources(),(Bitmap)bundle.getParcelable(BUNDLE_SAVED_BACKGROUND));
			levelBackground.setTileModeXY(TileMode.REPEAT, TileMode.REPEAT);
			longClickGestureDetector = new GestureDetector(getContext(), longClickListener);
			timeLinePaint = new Paint();
			timeLinePaint.setColor(Color.LTGRAY);
			timeLinePaint.setTextSize(20);
			resizeHeight(mapDuration);
			super.onRestoreInstanceState(bundle.getParcelable(BUNDLE_SAVED_SUPER));
			return;
		}
		super.onRestoreInstanceState(BaseSavedState.EMPTY_STATE);
	}

	/**
	 * Called to resize the map height and the position of enemies when the duration changed
	 * @param levelDuration new duration of the level
	 */
	
	private void resizeMap(float levelDuration){

		int oldHeight = height;
		int newHeight = resizeHeight(levelDuration);
		int deltaHeight = newHeight - oldHeight;

		for (int i = 0; i < dragablesEnemyList.size(); i++) {
			DragableEnemy current = dragablesEnemyList.get(i);
			current.rect.top = current.rect.top + (deltaHeight);
			current.rect.bottom = current.rect.bottom + (deltaHeight);
		}
	}
	
	/**
	 * Called to resize the map height when the duration changed
	 * @param levelDuration new duration of the level
	 */
	
	private int resizeHeight(float levelDuration) {

		LayoutParams params = this.getLayoutParams();
		int newHeight = (int)(EscapeIR.BACKGROUND_STEP*levelDuration);
		this.setLayoutParams(new FrameLayout.LayoutParams(params.width,newHeight));
		mapDuration = levelDuration;
		height = newHeight;
		return newHeight;
	}
	
	/**
	 * Called to listen add and delete enemy events on this view
	 * @param mapEditorListener
	 */

	public void setMapEditorListener(MapEditorListener mapEditorListener){
		customListener = mapEditorListener;
	}
}

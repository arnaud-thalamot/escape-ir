/**ESIPE - IR 2012/2013 - EscapeIR project for Android**/

package fr.umlv.escapeirandroid.editor;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;
import fr.umlv.escapeirandroid.R;

/**
 * The Class VerticalScrollView extends ScrollView and provides a vertical scrolling for the MapEditorView
 *
 * @author THALAMOT Arnaud
 * @author VASSEUR Simon
 * @version $Revision: 1.0 $
 */

public class VerticalScrollView extends ScrollView{

	/**
	 * Tags used when the view'instance state is resumed or saved
	 */
	
	private final static String BUNDLE_SAVED_SCROLL_POSTION_NAME = "scrollPosition";
	private final static String BUNDLE_SAVED_PARENT_NAME = "super";

	/**
	 * Last position of the scrollY in the MapEditorView, -1 special value to scroll to the bottom of the view at the fist load 
	 */
	
	private int savedScrollYPosition;
	private MapEditorView mapEditorView;
	
	/**
	 * Runnable to post to this view to scroll at savedScrollYPosition or to the bottom if it is the first load
	 */
	
	private int currentScrollY;
	private int scaledCurrentScrollY;
	
	private final Runnable upScrollPositionAndChildViewRunnable = new Runnable() {            
		@Override
		public void run() {
			mapEditorView = (MapEditorView)VerticalScrollView.this.findViewById(R.id.map_editor_view);
			if(savedScrollYPosition == -1){
				savedScrollYPosition = VerticalScrollView.this.mapEditorView.getHeight();
			}
			VerticalScrollView.this.scrollTo(0,savedScrollYPosition);
		}
	};

	public VerticalScrollView(Context context, AttributeSet attrs){
		super(context, attrs);
		savedScrollYPosition = -1;
		this.post(upScrollPositionAndChildViewRunnable);
	}

	/**
	 * Overrides dispatch to convert event coordinates corresponding in the MapEditorView
	 */
	
	//We have to return super.dispatch in order to have an efficient scrolling
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		duplicateScaledEvent(event);
		return super.dispatchTouchEvent(event);
	}

	/**
	 * Creates an new MotionEvent converting the Y coordinate corresponding in the MapEditorView
	 * @param event unscaled motion event
	 */
	
	private void duplicateScaledEvent(MotionEvent event){
		currentScrollY = getScrollY();
		scaledCurrentScrollY = currentScrollY+ (int)event.getY();
		MotionEvent newEvent = MotionEvent.obtain(event.getDownTime(), event.getEventTime(), event.getAction(), event.getX(),scaledCurrentScrollY, event.getMetaState());
		mapEditorView.dispatchTouchEvent(newEvent);
		newEvent.recycle();
	}

	/**
	 * Saves the value of last scrollY value on save instance
	 */
	
	@Override
	protected Parcelable onSaveInstanceState() {
		Bundle bundle = new Bundle();
		final Parcelable superState = super.onSaveInstanceState();
		bundle.putParcelable(BUNDLE_SAVED_PARENT_NAME, superState);
		bundle.putInt(BUNDLE_SAVED_SCROLL_POSTION_NAME, getScrollY());
		return bundle;
	}

	/**
	 * Restores the value of last scrollY value on save instance
	 */
	
	@Override
	protected void onRestoreInstanceState(Parcelable state) {
		if (state instanceof Bundle) {
			final Bundle bundle = (Bundle) state;
			savedScrollYPosition = bundle.getInt(BUNDLE_SAVED_SCROLL_POSTION_NAME);
			super.onRestoreInstanceState(bundle.getParcelable(BUNDLE_SAVED_PARENT_NAME));
			return;
		}
		super.onRestoreInstanceState(BaseSavedState.EMPTY_STATE);
	}
}

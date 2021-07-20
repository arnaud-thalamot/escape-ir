/**ESIPE - IR 2012/2013 - EscapeIR project for Android**/

package fr.umlv.escapeirandroid.editor.menu;

import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;

/**
 * The Class CollapseAnimation provides an animation to collapse a view on its width
 *
 * @author THALAMOT Arnaud
 * @author VASSEUR Simon
 * @version $Revision: 1.0 $
 */


public class CollapseAnimation extends CustomAnimation{
	
	/**
	 * Counts repetitions to prevent the view from having a too low width and so to be out of the layout
	 */
	
	private int repetitionCounter;
	
	public CollapseAnimation(View v, int fromWidth, int toWidth, int duration) {

		this.view = v;
		this.fromWidth = fromWidth;
		setDuration(ANIMATION_DURATION);
		setRepeatCount(REPEAT_COUNT);
		setFillAfter(false);
		setInterpolator(new AccelerateInterpolator());
		setAnimationListener(this);
	}

	@Override
	public void onAnimationEnd(Animation animation) {}

	/**
	 * Reduces the view by a given step, sets the width to a minimum at the end
	 */
	
	@Override
	public void onAnimationRepeat(Animation animation) {

		if(repetitionCounter == 1){
			lastWidth = MINIMUM_WIDTH;
		}
		else{
			lastWidth -=(fromWidth/REPEAT_COUNT);
		}
		layoutParams = view.getLayoutParams();
		layoutParams.width = lastWidth;
		view.setLayoutParams(layoutParams);
		repetitionCounter--;
	}
	
	/**
	 * Called when the animation is started
	 */

	@Override
	public void onAnimationStart(Animation animation) {
		lastWidth = fromWidth;
		repetitionCounter = REPEAT_COUNT;
	}

}

/**ESIPE - IR 2012/2013 - EscapeIR project for Android**/

package fr.umlv.escapeirandroid.editor.menu;

import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;

/**
 * The Class ExpandAnimation provides an animation to expand a view on its width
 *
 * @author THALAMOT Arnaud
 * @author VASSEUR Simon
 * @version $Revision: 1.0 $
 */

public class ExpandAnimation extends CustomAnimation{
	
	public ExpandAnimation(View v, int fromWidth, int toWidth, int duration) {
		this.view = v;
		this.toWidth = toWidth;
		setDuration(ANIMATION_DURATION);
		setRepeatCount(REPEAT_COUNT);
		setFillAfter(false);
		setInterpolator(new AccelerateInterpolator());
		setAnimationListener(this);
	}

	@Override
	public void onAnimationEnd(Animation animation) {}

	/**
	 * Expands the view by a given step, same step as CollapseAnimation so it don't expand to a width higher than it's origin width
	 */
	
	@Override
	public void onAnimationRepeat(Animation animation) {
		lastWidth +=toWidth/REPEAT_COUNT;
		layoutParams = view.getLayoutParams();
		layoutParams.width = lastWidth;
		view.setLayoutParams(layoutParams);
	}
	
	/**
	 * Called when the animation is started
	 */
	
	@Override
	public void onAnimationStart(Animation animation) {
		lastWidth = MINIMUM_WIDTH;
	}
}

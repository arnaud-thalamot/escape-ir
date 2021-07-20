/**ESIPE - IR 2012/2013 - EscapeIR project for Android**/

package fr.umlv.escapeirandroid.editor.menu;

import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;

/**
 * The Abstract Class CustomAnimation extends Animation and provides values and a structure to handle collapse and expand animations
 *
 * @author THALAMOT Arnaud
 * @author VASSEUR Simon
 * @version $Revision: 1.0 $
 */

public abstract class CustomAnimation extends Animation implements Animation.AnimationListener{

	protected View view;
	protected static final int ANIMATION_DURATION = 1;
	protected static final int REPEAT_COUNT = 5;
	protected static final int MINIMUM_WIDTH = 4;
	protected int lastWidth;
	protected int fromWidth;
	protected int toWidth;
	protected LayoutParams layoutParams;
	
	public abstract void onAnimationEnd(Animation animation);
	public abstract void onAnimationRepeat(Animation animation);
	public abstract void onAnimationStart(Animation animation);
	
}

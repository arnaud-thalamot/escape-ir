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
package fr.umlv.escapeirandroid.utils;

/**
 * This class provides different animation
 */
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;

public class AnimationManager {

	private final static int ANIMATED_BUTTON_DURATION = 200;
	private final static int ANIMATED_BUTTON_COUNT = 5;

	/** Provides a blink animation */
	public static Animation getBlinkAnimation(){
		Animation animation = new AlphaAnimation(1, 0);
		animation.setDuration(ANIMATED_BUTTON_DURATION);
		animation.setInterpolator(new LinearInterpolator());
		animation.setRepeatCount(ANIMATED_BUTTON_COUNT);
		animation.setRepeatMode(Animation.REVERSE);
		return animation;
	}
}
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
 * The Class Scaler provides function for scaling layout
 */
import android.app.Activity;
import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ProgressBar;
import android.widget.TextView;
import fr.umlv.escapeirandroid.R;
import fr.umlv.escapeirandroid.game.EscapeIR;

public class Scaler {

	private static final int HEROSHIP_START_HP = 30;
	private static final int DEFAULT_SCORE_WIDTH = 125;
	private static final int DEFAULT_CHRONOMETER_WIDTH = 90;
	private static final int DEFAULT_BUTTON_WIDTH = 54;
	private static final int DEFAULT_BUTTON_HEIGHT = 54;
	private static final int DEFAULT_PROGRESS_BAR_WIDTH = 250;
	private static final int DEFAULT_PROGRESS_BAR_HEIGHT = 35;
	private static final int DEFAULT_MENUBAR_TEXT_SIZE = 20;
	private final static int DEFAULT_PLAY_BUTTON_WIDTH = 165;
	private final static int DEFAULT_PLAY_BUTTON_HEIGHT = 25;
	private final static int DEFAULT_QUIT_BUTTON_WIDTH = 154;
	private final static int DEFAULT_QUIT_BUTTON_HEIGHT = 25;
	private final static int DEFAULT_LEVEL_BUTTON_WIDTH = 470;
	private final static int DEFAULT_LEVEL_BUTTON_HEIGHT = 24;

	/** Scales the gameView
	 * 
	 * @param a the parent activity
	 * @param r the resources
	 */
	@SuppressWarnings("deprecation")
	public static void scaleGameView(Activity a,Resources r){

		float ratioWidth = EscapeIR.RATIO_WIDTH;
		float ratioHeight = EscapeIR.RATIO_HEIGHT;
		View v = a.findViewById(R.id.menubar);

		v.setBackgroundDrawable(new BitmapDrawable(r,ImageManager.getInstance().loadImage(R.drawable.menubackground)));

		Button triforceButton = (Button)v.findViewById(R.id.triforce_button);
		triforceButton.setBackgroundResource(R.drawable.triforce_button);
		triforceButton.getLayoutParams().height = (int) (DEFAULT_BUTTON_HEIGHT * ratioHeight);
		triforceButton.getLayoutParams().width = (int) (DEFAULT_BUTTON_WIDTH* ratioWidth);

		Button shibooleetButton = (Button) v.findViewById(R.id.shiboleet_button);
		shibooleetButton.setBackgroundResource(R.drawable.shiboleet_button);
		shibooleetButton.getLayoutParams().height = (int) (DEFAULT_BUTTON_HEIGHT * ratioHeight);
		shibooleetButton.getLayoutParams().width = (int) (DEFAULT_BUTTON_WIDTH* ratioWidth);

		Button fireballButton = (Button) v.findViewById(R.id.fireball_button);
		fireballButton.setBackgroundResource(R.drawable.fireball_button);
		fireballButton.getLayoutParams().height = (int) (DEFAULT_BUTTON_HEIGHT * ratioHeight);
		fireballButton.getLayoutParams().width = (int) (DEFAULT_BUTTON_WIDTH* ratioWidth);

		Button missileButton = (Button) v.findViewById(R.id.missile_button);
		missileButton.setBackgroundResource(R.drawable.missille_button);
		missileButton.getLayoutParams().height = (int) (DEFAULT_BUTTON_HEIGHT *ratioHeight);
		missileButton.getLayoutParams().width = (int) (DEFAULT_BUTTON_WIDTH*ratioWidth);

		ProgressBar bar = (ProgressBar) v.findViewById(R.id.life);
		bar.getLayoutParams().width = (int)( DEFAULT_PROGRESS_BAR_WIDTH * ratioWidth);
		bar.getLayoutParams().height = (int)( DEFAULT_PROGRESS_BAR_HEIGHT * ratioHeight);
		bar.setMax(HEROSHIP_START_HP);
		bar.setProgress(HEROSHIP_START_HP);
		bar.invalidate();

		TextView s = (TextView) v.findViewById(R.id.score);
		s.setTextSize(TypedValue.COMPLEX_UNIT_SP,DEFAULT_MENUBAR_TEXT_SIZE*ratioHeight);
		s.getLayoutParams().width = (int)(DEFAULT_SCORE_WIDTH*ratioWidth);

		Chronometer c = (Chronometer) v.findViewById(R.id.chronometer);
		c.setTextSize(TypedValue.COMPLEX_UNIT_SP,DEFAULT_MENUBAR_TEXT_SIZE*ratioHeight);
		c.getLayoutParams().width = (int)(DEFAULT_CHRONOMETER_WIDTH*ratioWidth);
	}

	/** Scales the MainView
	 * 
	 * @param a the parent activity
	 * @param r the resources
	 */
	public static void scaleEscapeView(Activity a,Resources r){

		float ratioWidth = EscapeIR.RATIO_WIDTH;
		float ratioHeight = EscapeIR.RATIO_HEIGHT;

		Button playButton = (Button) a.findViewById(R.id.play);
		playButton.setBackgroundResource(R.drawable.play_button);
		playButton.getLayoutParams().height = (int) (DEFAULT_PLAY_BUTTON_HEIGHT * ratioHeight);
		playButton.getLayoutParams().width = (int) (DEFAULT_PLAY_BUTTON_WIDTH* ratioWidth);

		Button editorButton = (Button) a.findViewById(R.id.editor);
		editorButton.setBackgroundResource(R.drawable.level_editor_button);
		editorButton.getLayoutParams().height = (int) (DEFAULT_LEVEL_BUTTON_HEIGHT * ratioHeight);
		editorButton.getLayoutParams().width = (int) (DEFAULT_LEVEL_BUTTON_WIDTH* ratioWidth);

		Button quitButton = (Button) a.findViewById(R.id.quit);
		quitButton.setBackgroundResource(R.drawable.quit_button);
		quitButton .getLayoutParams().height = (int) (DEFAULT_QUIT_BUTTON_HEIGHT * ratioHeight);
		quitButton .getLayoutParams().width = (int) (DEFAULT_QUIT_BUTTON_WIDTH* ratioWidth);
	}
}

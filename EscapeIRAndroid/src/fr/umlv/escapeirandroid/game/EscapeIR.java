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

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ArrayAdapter;
import fr.umlv.escapeirandroid.R;
import fr.umlv.escapeirandroid.editor.EditorActivity;
import fr.umlv.escapeirandroid.utils.AnimationManager;
import fr.umlv.escapeirandroid.utils.FileManager;
import fr.umlv.escapeirandroid.utils.Scaler;

/**
 * This is the main class. Here you can start playing game, edit levels or quit
 *
 * @author THALAMOT Arnaud
 * @author VASSEUR Simon
 * @version $Revision: 1.0 $
 */
public class EscapeIR extends Activity implements OnClickListener{

	private static final String TAG = FileManager.class.getSimpleName();

	public static int DEFAULT_WIDTH = 768;
	public static int DEFAULT_HEIGHT = 976;
	public static int CURRENT_WIDTH;
	public static int CURRENT_HEIGHT;
	public static float RATIO_WIDTH;
	public static float RATIO_HEIGHT;
	public static int BACKGROUND_STEP;
	public static Context CONTEXT;
	public static Resources RESOURCES;
	public static final String LEVEL = "level";

	/** Levels available in the application files directory */
	private ArrayList<String> levels = new ArrayList<String>();
	private ArrayAdapter<String> adapter;
	private AlertDialog.Builder levelSelection;

	/** This method displays the menu
	 * 
	 * @param savedInstanceState the Bundle
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		DisplayMetrics display =  getResources().getDisplayMetrics();
		CURRENT_WIDTH = display.widthPixels;
		CURRENT_HEIGHT = display.heightPixels;
		RATIO_WIDTH = (float)CURRENT_WIDTH / (float)DEFAULT_WIDTH;
		RATIO_HEIGHT = (float)CURRENT_HEIGHT / (float)DEFAULT_HEIGHT;
		BACKGROUND_STEP = CURRENT_HEIGHT/10;
		CONTEXT = getApplicationContext();
		RESOURCES = getResources();

		/** If default levels had been deleted by the user, they are reinstalled */
		FileManager.getInstance().copyLevelAssetToInternalDir(getApplicationContext(), getAssets());
		setContentView(R.layout.escapeir);

		/** Scale the layout */
		Scaler.scaleEscapeView(this,EscapeIR.RESOURCES);

		this.adapter = new ArrayAdapter<String>(EscapeIR.this,android.R.layout.select_dialog_item);
		this.levelSelection = new AlertDialog.Builder(this);

		Log.d(TAG, "EscapeIR activity started");
	}

	/** Manage the event of user click
	 * 
	 * @param v the view
	 */
	@Override
	public void onClick(final View v) {

		final Intent intent;

		switch(v.getId()){

		/** Button to launch the game */
		case R.id.play:

			this.levels = FileManager.getInstance().loadAvailableLevel();
			int len = levels.size();
			this.adapter.clear();

			for(int i=0;i<len;i++){
				this.adapter.add(levels.get(i));
			}

			levelSelection.setIcon(R.drawable.icon);
			levelSelection.setTitle("Choose your level");
			levelSelection.setNegativeButton(R.string.cancel_action,new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});

			levelSelection.setAdapter(this.adapter,new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {

					String levelName = adapter.getItem(which);
					Intent intent = new Intent(getApplicationContext(),GameActivity.class);
					intent.putExtra(LEVEL,levelName);
					startActivity(intent);
					overridePendingTransition(R.anim.fadein,R.anim.fadeout);
				}
			});
			AlertDialog alert = levelSelection.create();
			alert.show();

			break;

			/** Button to launch the level editor */
		case R.id.editor:
			intent = new Intent(this, EditorActivity.class);
			findViewById(v.getId()).startAnimation(AnimationManager.getBlinkAnimation());
			this.startActivity(intent);
			overridePendingTransition(R.anim.fadein,R.anim.fadeout);
			break;

			/** Button to quit */	
		case R.id.quit:
			finish();
			break;
		}
	}
}

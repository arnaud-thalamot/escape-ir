/**ESIPE - IR 2012/2013 - EscapeIR project for Android**/

package fr.umlv.escapeirandroid.editor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.PriorityBlockingQueue;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import fr.umlv.escapeirandroid.R;
import fr.umlv.escapeirandroid.behaviour.BehaviourType;
import fr.umlv.escapeirandroid.editor.EditLevelDialogFragment.EditLevelListener;
import fr.umlv.escapeirandroid.editor.NewLevelDialogFragment.NewLevelListener;
import fr.umlv.escapeirandroid.editor.RetrieveLevelFromLinkDialogFragment.NewLevelFromLinkListener;
import fr.umlv.escapeirandroid.entity.BodyDescription;
import fr.umlv.escapeirandroid.entity.EnemyType;
import fr.umlv.escapeirandroid.game.EscapeIR;
import fr.umlv.escapeirandroid.game.GameBoard;
import fr.umlv.escapeirandroid.utils.PositionConverter;

/**
 * The Class WelcomeDialogFragment provides a dialog fragment to load various methods to create a level
 *
 * @author THALAMOT Arnaud
 * @author VASSEUR Simon
 * @version $Revision: 1.0 $
 */

public class WelcomeDialogFragment extends DialogFragment{

	/**
	 * 
	 * This interface provides listeners to transport data somewhere else
	 *
	 */
	
	public interface WelcomeListener {
		/**
		 * Called when a new level from a template level is ready to be processed
		 * 
		 * @param name name of the created level
		 * @param background background of the created
		 * @param duration duration in seconds of the level
		 * @param behaviourTypes HasMap of the known ennemy behaviours in template level used
		 * @param enemyTypes HashMap of the known ennemy types in template level used
		 * @param enemies ArrayList of ennemies from the template level converted into dragable ennemies
		 */
		public void onNewLevelFromTemplate(String name, Bitmap background,
				float duration, HashMap<String, BehaviourType> behaviourTypes,
				HashMap<String, EnemyType> enemyTypes,ArrayList<DragableEnemy> enemies);
		public void onNewLevelFromScratch(String name, Bitmap background,float duration);
		public void onEditLevel(String name, Bitmap background,
				float duration, HashMap<String, BehaviourType> behaviourTypes,
				HashMap<String, EnemyType> enemyTypes,ArrayList<DragableEnemy> enemies);
	}

	/**
	 * The listener used to transport data somewhere else
	 */
	
	private WelcomeListener customWelcomeListener;
	
	/**
	 * Buttons in the layout
	 */
	
	private Button newButton;
	private Button editButton;
	private Button deleteButton;
	private Button downloadButton;
	
	/**
	 * Used in the case the level has been load from an HTTP link
	 */
	
	private GameBoard gameboard;

	/**
	 * Inflates the layout
	 * Retrieves the buttons
	 * Sets attributes of the dialog
	 * Add a listener on the Cancel button, if clicked quits the activity
	 * @return the resulting dialog
	 */
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

		final View layout = View.inflate(getActivity(), R.layout.dialog_welcome, null);

		newButton = (Button)layout.findViewById(R.id.welcome_dialog_new_level_button);
		editButton = (Button)layout.findViewById(R.id.welcome_dialog_edit_level_button);
		downloadButton = (Button)layout.findViewById(R.id.welcome_dialog_download_level_button);
		deleteButton = (Button)layout.findViewById(R.id.welcome_dialog_delete_level_button);

		builder.setIcon(0);
		builder.setTitle(getString(R.string.welcome_dialog_title));
		builder.setNegativeButton(getString(R.string.cancel_action),
				new Dialog.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				quitActivity();
			}
		}
				);

		builder.setView(layout);
		return builder.create();

	}

	/**
	 * Sets impossible to quit the dialog by clicking outside of it
	 * Sets a listener on Back Button Pressed, on click quits activity
	 * Sets onclick listeners on buttons to load adequate fragments
	 * Sets listeners on fragments to retrieves resulting datas from them
	 * When data has been transfered the dialog dismiss itself
	 */
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

		getDialog().setCanceledOnTouchOutside(false);
		getDialog().setOnKeyListener(new OnKeyListener() {
			
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {

				if (keyCode == KeyEvent.KEYCODE_BACK) {
					quitActivity();
                    return true;
                }
				
				return false;
			}
		});

		editButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				EditLevelDialogFragment dialog = new EditLevelDialogFragment();
				dialog.show(getFragmentManager(),"EditLevelDialogFragment");
				dialog.setOnEditLevelListener(new EditLevelListener() {

					@Override
					public void onEditLevelCreated(String name, Bitmap background,
							float levelDuration, HashMap<String, BehaviourType> behaviourTypes,
							HashMap<String, EnemyType> enemyTypes,
							ArrayList<DragableEnemy> enemies) {
						customWelcomeListener.onEditLevel(name, background, levelDuration, behaviourTypes, enemyTypes, enemies);
						dismiss();
					}
				});
				getDialog().hide();
			}
		});

		newButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				NewLevelDialogFragment dialog = new NewLevelDialogFragment();
				dialog.setOnNewLevelListener(new NewLevelListener() {

					@Override
					public void onNewLevelFromTemplateCreated(String name, Bitmap background,
							float levelDuration, HashMap<String, BehaviourType> behaviourTypes,
							HashMap<String, EnemyType> enemyTypes,ArrayList<DragableEnemy> enemies) {
						customWelcomeListener.onNewLevelFromTemplate(name,background,
								levelDuration,behaviourTypes,
								enemyTypes,enemies);
						dismiss();
					}

					@Override
					public void onNewLevelFromScratchCreated(String name, Bitmap background,
							float duration) {
						customWelcomeListener.onNewLevelFromScratch(name,background,duration);
						dismiss();
					}
				});
				dialog.show(getFragmentManager(),"NewLevelDialogFragment");
				getDialog().hide();
			}
		});

		downloadButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				RetrieveLevelFromLinkDialogFragment dialog = new RetrieveLevelFromLinkDialogFragment();
				dialog.setOnNewLevelFromLinkListener(new NewLevelFromLinkListener() {
					
					@Override
					public void onNewLevelFromLinkCreated(GameBoard gameboardFromLink) {
						gameboard = gameboardFromLink;
						
						float levelDuration = gameboard.getLevelDuration();
						PriorityBlockingQueue<BodyDescription> bodyDescriptions = gameboard.getEnemies();

						HashMap<String, EnemyType> ennemyTypes = gameboard.getEnemyType();
						ArrayList<DragableEnemy> dragableEnemies = new ArrayList<DragableEnemy>();

						int len = bodyDescriptions.size();
						for (int i = 0; i < len; i++) {
							BodyDescription currentBodyDescription = bodyDescriptions.poll();
							Bitmap bitmap = (ennemyTypes.get(currentBodyDescription.getEnemyTypeName())).getPicture();
							float x = PositionConverter.worldToScreenX(currentBodyDescription.getX());
							float y = ((EscapeIR.CURRENT_HEIGHT/10)*(levelDuration/60))-((currentBodyDescription.getTime()/60)*(EscapeIR.CURRENT_HEIGHT/10));
							String ennemyType = currentBodyDescription.getEnemyTypeName();
							dragableEnemies.add(new DragableEnemy(bitmap, x, y, ennemyType));
						}
						
						customWelcomeListener.onEditLevel(gameboard.getName(), gameboard.getBackground(), levelDuration, gameboard.getBehaviourType(), gameboard.getEnemyType(),dragableEnemies);
						dismiss();
					}
				});
				dialog.show(getFragmentManager(),"RetrieveLevelFromLinkDialogFragment");
				getDialog().hide();
				
			}
		});
		
		deleteButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				DeleteLevelDialogFragment dialog = new DeleteLevelDialogFragment();
				dialog.show(getFragmentManager(),"DeleteLevelDialogFragment");
				getDialog().hide();
			}
		});

		return super.onCreateView(inflater, container, savedInstanceState);
	}

	/**
	 * Called on click Cancel or back button to quit the activity
	 */
	
	private void quitActivity(){
		Intent intent = new Intent(getActivity(), EscapeIR.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}
	
	/**
	 * Method to set a listener on this fragment
	 * @param welcomeListener
	 */
	
	public void setOnWelcomeListener(WelcomeListener welcomeListener){
		customWelcomeListener = welcomeListener;
	}
}

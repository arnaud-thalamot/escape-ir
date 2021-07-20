/**ESIPE - IR 2012/2013 - EscapeIR project for Android**/
package fr.umlv.escapeirandroid.editor;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.PriorityBlockingQueue;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;
import fr.umlv.escapeirandroid.R;
import fr.umlv.escapeirandroid.behaviour.BehaviourType;
import fr.umlv.escapeirandroid.entity.BodyDescription;
import fr.umlv.escapeirandroid.entity.EnemyType;
import fr.umlv.escapeirandroid.game.EscapeIR;
import fr.umlv.escapeirandroid.game.GameBoard;
import fr.umlv.escapeirandroid.utils.FileManager;
import fr.umlv.escapeirandroid.utils.PositionConverter;

/**
 * The Class NewLevelDialogFragment provides a dialog fragment with a form to create a new level
 *
 * @author THALAMOT Arnaud
 * @author VASSEUR Simon
 * @version $Revision: 1.0 $
 */

public class NewLevelDialogFragment extends DialogFragment{

	public interface NewLevelListener {
		public void onNewLevelFromScratchCreated(String name,Bitmap background,float duration);
		public void onNewLevelFromTemplateCreated(String name,Bitmap background,
				float levelDuration,HashMap<String,BehaviourType> behaviourTypes,
				HashMap<String, EnemyType> enemyTypes,ArrayList<DragableEnemy> enemies);
	}

	private NewLevelListener customNewLevelListener;
	private static final int MAX_DURATION = 3600;
	private static final int MIN_DURATION = 30;
	private static int RESULT_LOAD_IMAGE = 1;
	private View layout;
	private RadioGroup radiogroup;
	private Bitmap background;
	private ImageView preview;
	private EditText inputLevelName;
	private EditText inputLevelDuration;
	private Spinner spinner;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

		layout = View.inflate(getActivity(), R.layout.dialog_new_level, null);

		spinner = (Spinner)layout.findViewById(R.id.dialog_new_spinner);
		inputLevelName = (EditText)layout.findViewById(R.id.dialog_new_level_level_name);
		inputLevelDuration = (EditText)layout.findViewById(R.id.dialog_new_level_level_duration);

		ArrayList<String> levels = FileManager.getInstance().loadAvailableLevel();
		levels.add(0, "Start from scratch");

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),R.layout.spinner_item,levels);
		adapter.setDropDownViewResource(R.layout.spinner_item);
		spinner.setAdapter(adapter);

		radiogroup = (RadioGroup)layout.findViewById(R.id.dialog_new_level_radiogroup);

		RadioButton fromDisk = (RadioButton)layout.findViewById(R.id.dialog_new_level_radiobutton_fromdisk);

		fromDisk.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
					startActivityForResult(i, RESULT_LOAD_IMAGE);
				}}
		});

		builder.setIcon(0);
		builder.setCancelable(false);
		builder.setTitle(getString(R.string.new_level_dialog_title));
		builder.setNegativeButton(getString(R.string.cancel_action),
				new Dialog.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dismiss();
				DialogFragment parent = (DialogFragment)getFragmentManager().findFragmentByTag("WelcomeDialogFragment");
				parent.getDialog().show();
			}
		}
				);
		builder.setPositiveButton(getString(R.string.ok_action_new_level),
				new Dialog.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				//Blank listener because we override it in OnCreateView to prevent it from always dismissing the dialog when the button is clicked 
			}
		}
				);

		builder.setView(layout);
		return builder.create();

	}

	/**
	 * When the positive button is clicked we computes all the data retrieved and we gives it back to the activity through a NewLevelListener
	 */

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		getDialog().setCanceledOnTouchOutside(false);

		AlertDialog dialog = (AlertDialog)getDialog();
		getDialog().setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {

				if (keyCode == KeyEvent.KEYCODE_BACK) {
					return true;
				}

				return false;
			}
		});
		dialog.show();
		Button positiveButton = (Button) dialog.getButton(Dialog.BUTTON_POSITIVE);
		positiveButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{

				String name = inputLevelName.getText().toString();

				if(name.equals("")){
					inputLevelName.setError(getString(R.string.error_missing_name));
					return;
				}

				String durationString = inputLevelDuration.getText().toString();

				if(durationString.equals("")){
					inputLevelDuration.setError(getString(R.string.error_missing_duration));
					return;
				}

				float duration = Float.valueOf(durationString);

				if((int)duration >=MAX_DURATION){
					Toast.makeText(getActivity(), R.string.error_missing_duration_too_high,Toast.LENGTH_LONG ).show();
					return;
				}

				if((int)duration <=MIN_DURATION){
					Toast.makeText(getActivity(), R.string.error_missing_duration_too_low,Toast.LENGTH_LONG ).show();
					return;
				}

				String templateLevelName = (String)spinner.getSelectedItem();
				if(templateLevelName.equals("Start from scratch")){

					if(background == null){
						Toast.makeText(getActivity(),R.string.error_missing_background, Toast.LENGTH_LONG).show();
						return;
					}

					customNewLevelListener.onNewLevelFromScratchCreated(name, background, duration);
					dismiss();
				}
				else{

					GameBoard template = null;
					try {
						template = FileManager.getInstance().loadGameBoard(templateLevelName);
					} catch (FileNotFoundException e) {
						Toast.makeText(getActivity(), R.string.error_file_not_found,Toast.LENGTH_LONG ).show();
					} catch (IOException e) {
						Toast.makeText(getActivity(), R.string.error_loading_file,Toast.LENGTH_LONG ).show();
					} catch (ParserConfigurationException e) {
						Toast.makeText(getActivity(), R.string.error_parser,Toast.LENGTH_LONG ).show();
						e.printStackTrace();
					} catch (SAXException e) {
						Toast.makeText(getActivity(), R.string.error_parser,Toast.LENGTH_LONG ).show();
					}

					if(background == null){
						background = template.getBackground();
					}

					PriorityBlockingQueue<BodyDescription> bodyDescriptions = template.getEnemies();
					HashMap<String, EnemyType> ennemyTypes = template.getEnemyType();
					ArrayList<DragableEnemy> dragableEnemies = new ArrayList<DragableEnemy>();

					int len = bodyDescriptions.size();
					for (int i = 0; i < len; i++) {
						BodyDescription currentBodyDescription = bodyDescriptions.poll();
						Bitmap bitmap = (ennemyTypes.get(currentBodyDescription.getEnemyTypeName())).getPicture();
						float x = PositionConverter.worldToScreenX(currentBodyDescription.getX());
						float y = (EscapeIR.BACKGROUND_STEP*(duration/60))-((currentBodyDescription.getTime()/60)*EscapeIR.BACKGROUND_STEP);
						String ennemyType = currentBodyDescription.getEnemyTypeName();
						dragableEnemies.add(new DragableEnemy(bitmap, x, y, ennemyType));
					}

					customNewLevelListener.onNewLevelFromTemplateCreated(template.getName(),background,duration/60, template.getBehaviourType(), template.getEnemyType(),dragableEnemies);
					dismiss();
					return;
				}
			}
		});

		preview = (ImageView)layout.findViewById(R.id.dialog_new_level_imagepreview_fromdisk);
		RadioButton fromLink = (RadioButton)layout.findViewById(R.id.dialog_new_level_radiobutton_fromlink);
		fromLink.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				
				if(isChecked){
				
				RetrieveImageFromLinkDialogFragment dialog = new RetrieveImageFromLinkDialogFragment();
				dialog.show(getFragmentManager(),"RetrieveImageFromLinkDialogFragment");
				getDialog().hide();
				}
			}
		});

		return super.onCreateView(inflater, container, savedInstanceState);
	}

	/**
	 * Called when the intern gallery dialog is dismissed and retrieves the bitmap selected
	 */

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == RESULT_LOAD_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
			Uri selectedImage = data.getData();
			String[] filePathColumn = { MediaStore.Images.Media.DATA };

			Cursor cursor = getActivity().getContentResolver().query(selectedImage, filePathColumn, null, null, null);
			cursor.moveToFirst();

			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			String picturePath = cursor.getString(columnIndex);
			cursor.close();

			Bitmap image = BitmapFactory.decodeFile(picturePath);
			refreshBackgroundPreview(image);
		}
		if(requestCode == RESULT_LOAD_IMAGE){

				((RadioButton)(radiogroup.findViewById(R.id.dialog_new_level_radiobutton_fromdisk))).setChecked(false);
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * When a new image is chosen this method puts it the preview view
	 * @param image the new chosen image
	 */


	void refreshBackgroundPreview(Bitmap image){
		background = image;
		preview.setImageBitmap(image);
		LayoutParams params = preview.getLayoutParams();
		params.height = radiogroup.getHeight();
		preview.setLayoutParams(params);
	}

	/**
	 * Set a listener on this fragment to retrieve a new level properties when ready
	 * @param newLevelListener
	 */

	public void setOnNewLevelListener(NewLevelListener newLevelListener){
		customNewLevelListener = newLevelListener;
	}

}

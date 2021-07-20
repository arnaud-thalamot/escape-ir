/**ESIPE - IR 2012/2013 - EscapeIR project for Android**/

package fr.umlv.escapeirandroid.editor;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.Toast;
import fr.umlv.escapeirandroid.R;
import fr.umlv.escapeirandroid.behaviour.BehaviourType;
import fr.umlv.escapeirandroid.editor.EditorActivity.onLoadAddEnemyTypesListener;
import fr.umlv.escapeirandroid.entity.EnemyType;
import fr.umlv.escapeirandroid.game.EscapeIR;
import fr.umlv.escapeirandroid.utils.ImageManager;

/**
 * The Class AddEnemyTypeFragment provides a fragment to load a form to add a new enemyType, when saved gives it back through an EnemyTypeListener
 *
 * @author THALAMOT Arnaud
 * @author VASSEUR Simon
 * @version $Revision: 1.0 $
 */

public class AddEnemyTypeFragment extends Fragment{

	public interface EnemyTypeListener {
		public void onEnemyTypeAdded(EnemyType newEnemyType);
	}

	private EnemyTypeListener customEnemyTypeListener;
	private TableLayout layout;
	private static int RESULT_LOAD_IMAGE = 1;
	private ImageView preview;
	private EditText inputLevelName;
	private EditText inputHP;
	private EditText inputRhythm;
	private EditText inputMissile;
	private EditText inputFireball;
	private EditText inputShiboleet;
	private EditText inputTriforce;
	private Spinner behaviours;
	private Bitmap background;
	private HashMap<String, BehaviourType> behaviourTypes;
	private HashMap<String, EnemyType> enemyTypes;
	private RadioGroup radiogroup;
	private ArrayAdapter<String> adapter;
	private ArrayList<String> levels;
	private final onLoadAddEnemyTypesListener onLoadAddEnemyTypesListener = new onLoadAddEnemyTypesListener() {

		@Override
		public void onAddEnemyTypesLoading(HashMap<String, EnemyType> newEnemyTypes,HashMap<String, BehaviourType> newBehaviourTypes) {
			enemyTypes = newEnemyTypes;
			behaviourTypes = newBehaviourTypes;

			levels = new ArrayList<String>(behaviourTypes.keySet());

			adapter = new ArrayAdapter<String>(getActivity(),R.layout.spinner_item,levels);
			adapter.setDropDownViewResource(R.layout.spinner_item);
			behaviours.setAdapter(adapter);
		}
	};

	/**
	 * Set the listener to retrieve the current enemy type list
	 */

	@Override
	public void onAttach(Activity activity) {

		((EditorActivity)getActivity()).setOnAddLoadEnemyListener(onLoadAddEnemyTypesListener);
		super.onAttach(activity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		layout = (TableLayout)inflater.inflate(R.layout.add_enemy_type, container, false);

		inputLevelName = (EditText)layout.findViewById(R.id.dialog_new_level_level_name);
		radiogroup = (RadioGroup)layout.findViewById(R.id.dialog_new_level_radiogroup);
		preview = (ImageView)layout.findViewById(R.id.dialog_new_level_imagepreview_fromdisk);
		inputHP = (EditText)layout.findViewById(R.id.add_ennemy_type_hp);
		inputRhythm = (EditText)layout.findViewById(R.id.add_ennemy_type_rhythm);
		inputMissile = (EditText)layout.findViewById(R.id.add_ennemy_type_missile);
		inputFireball = (EditText)layout.findViewById(R.id.add_ennemy_type_fireball);
		inputShiboleet = (EditText)layout.findViewById(R.id.add_ennemy_type_shiboleet);
		inputTriforce = (EditText)layout.findViewById(R.id.add_ennemy_type_triforce);
		behaviours = (Spinner) layout.findViewById(R.id.add_ennemy_type_behaviour);

		RadioButton fromDisk = (RadioButton)layout.findViewById(R.id.dialog_new_level_radiobutton_fromdisk);

		fromDisk.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
					startActivityForResult(i, RESULT_LOAD_IMAGE);
				}
			}
		});

		RadioButton fromLink = (RadioButton)layout.findViewById(R.id.dialog_new_level_radiobutton_fromlink);
		fromLink.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					RetrieveImageFromLinkDialogFragment dialog = new RetrieveImageFromLinkDialogFragment();
					dialog.show(getFragmentManager(),"RetrieveImageFromLinkDialogFragment");
				}
			}
		});

		return layout;
	}

	/**
	 * Computes all the data retrieved from fields and tries to create a new enemyType with it
	 */

	public void saveEnemyTypes(){

		String name = inputLevelName.getText().toString();

		if(name.equals("")){
			inputLevelName.setError(getString(R.string.error_missing_name));
			return;
		}

		String hpString = inputHP.getText().toString();

		if(hpString.equals("")){
			inputHP.setError(getString(R.string.error_missing_name));
			return;
		}

		String rhythmString = inputRhythm.getText().toString();

		if(rhythmString.equals("")){
			inputRhythm.setError(getString(R.string.error_missing_rythm));
			return;
		}

		String missileString = inputMissile.getText().toString();

		if(missileString.equals("")){
			inputMissile.setError(getString(R.string.error_missing_missile));
			return;
		}

		String fireballString = inputFireball.getText().toString();

		if(fireballString.equals("")){
			inputFireball.setError(getString(R.string.error_missing_fireballs));
			return;
		}

		String shiboleetString = inputShiboleet.getText().toString();

		if(shiboleetString.equals("")){
			inputShiboleet.setError(getString(R.string.error_missing_shiboleet));
			return;
		}

		String triforceString = inputTriforce.getText().toString();

		if(triforceString.equals("")){
			inputTriforce.setError(getString(R.string.error_missing_triforce));
			return;
		}

		int hpValue = checkInputValueInt(hpString);
		int rhythmValue = checkInputValueInt(rhythmString);
		int missilesValue = checkInputValueInt(missileString);
		int fireballValue = checkInputValueInt(fireballString);
		int shiboleetValue = checkInputValueInt(shiboleetString);
		int triforceValue = checkInputValueInt(triforceString);

		String behaviour = (String)behaviours.getSelectedItem();

		if(background == null){
			Toast.makeText(getActivity(),R.string.error_missing_background, Toast.LENGTH_LONG).show();
			return;
		}

		EnemyType newEnemyType = new EnemyType(name, background, hpValue, rhythmValue, behaviour, missilesValue, fireballValue, shiboleetValue, triforceValue);
		enemyTypes.put(name, newEnemyType);
		customEnemyTypeListener.onEnemyTypeAdded(newEnemyType);
		Toast.makeText(getActivity(), "EnemyType "+name+" saved !", Toast.LENGTH_LONG).show();
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

			Bitmap image = ImageManager.getInstance().loadImageLevelEditor(picturePath);
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
		params.height = EscapeIR.CURRENT_HEIGHT/10;
		preview.setLayoutParams(params);
	}

	private int checkInputValueInt(String string){
		if(!(string.equals(""))){
			return Integer.parseInt(string);
		}
		return 0;
	}

	/**
	 * Set a listener on this fragment to retrieve a new enemyType when ready
	 * @param enemyTypeListener
	 */

	public void setOnEnemyTypeListener(EnemyTypeListener enemyTypeListener){
		customEnemyTypeListener = enemyTypeListener;
	}

}

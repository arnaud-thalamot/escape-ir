/**ESIPE - IR 2012/2013 - EscapeIR project for Android**/

package fr.umlv.escapeirandroid.editor;

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
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableLayout;
import fr.umlv.escapeirandroid.R;
import fr.umlv.escapeirandroid.game.EscapeIR;
import fr.umlv.escapeirandroid.utils.ImageManager;

/**
 * The Class LevelPropertiesFragment provides a fragment with a form to edit the level properties, e.g the name, the duration and the background
 *
 * @author THALAMOT Arnaud
 * @author VASSEUR Simon
 * @version $Revision: 1.0 $
 */

public class LevelPropertiesFragment extends Fragment{

	public interface LevelPropertiesListener {
		public void onPropertiesChanged(String name,Bitmap background,float duration);
	}

	private LevelPropertiesListener customLevelPropertiesListener;
	private TableLayout layout;
	private static int RESULT_LOAD_IMAGE = 1;
	private ImageView preview;
	private EditText inputLevelName;
	private Bitmap background;
	private EditText inputLevelDuration;
	private RadioGroup radiogroup;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		layout = (TableLayout)inflater.inflate(R.layout.level_properties, container, false);

		inputLevelName = (EditText)layout.findViewById(R.id.dialog_new_level_level_name);
		inputLevelDuration = (EditText)layout.findViewById(R.id.dialog_new_level_level_duration);

		radiogroup = (RadioGroup)layout.findViewById(R.id.dialog_new_level_radiogroup);

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

		preview = (ImageView)layout.findViewById(R.id.dialog_new_level_imagepreview_fromdisk);
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
	 * Computes the data and gives them through its LevelPropertiesListener
	 */

	public void saveProperties(){

		String levelName = inputLevelName.getText().toString();
		String durationString = inputLevelDuration.getText().toString();
		float duration = 0;
		if(!(durationString.equals(""))){
			duration = Float.valueOf(durationString);
		}

		customLevelPropertiesListener.onPropertiesChanged(levelName, background,duration);
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

			Bitmap image = ImageManager.getInstance().loadImageFromPath(picturePath);
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
		params.height = EscapeIR.CURRENT_HEIGHT/2;
		preview.setLayoutParams(params);
	}

	/**
	 * Set a listener on this fragment to retrieve a new level properties (name,duration,background) when ready
	 * @param enemyTypeListener
	 */

	public void setOnLevelPropertiesListener(LevelPropertiesListener levelPropertiesListener){
		customLevelPropertiesListener = levelPropertiesListener;
	}

}

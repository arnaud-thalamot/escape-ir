/**ESIPE - IR 2012/2013 - EscapeIR project for Android**/

package fr.umlv.escapeirandroid.editor;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import fr.umlv.escapeirandroid.R;
import fr.umlv.escapeirandroid.utils.FileManager;

/**
 * The Class DeleteLevelDialogFragment provides a dialog fragment to delete an existing level
 *
 * @author THALAMOT Arnaud
 * @author VASSEUR Simon
 * @version $Revision: 1.0 $
 */

public class DeleteLevelDialogFragment extends DialogFragment{

	Spinner spinner;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

		final View layout = View.inflate(getActivity(), R.layout.dialog_delete_level, null);
		spinner = (Spinner)layout.findViewById(R.id.dialog_delete_spinner);

		ArrayList<String> levels = FileManager.getInstance().loadAvailableLevel();

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),R.layout.spinner_item,levels);
		adapter.setDropDownViewResource(R.layout.spinner_item);
		spinner.setAdapter(adapter);

		builder.setIcon(0);
		builder.setCancelable(false);
		builder.setTitle(getString(R.string.delete_level_dialog_title));

		builder.setNegativeButton(getString(R.string.cancel_action),new Dialog.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				dismissProperly();
			}
		});

		builder.setPositiveButton(getString(R.string.ok_action_delete_level),new Dialog.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				String levelName = (String)spinner.getSelectedItem();
				FileManager.getInstance().deleteLevel(levelName);
				Toast toast = Toast.makeText(getActivity().getApplicationContext(),"Level "+levelName+" deleted successfully", Toast.LENGTH_SHORT);
				toast.show();
				dismissProperly();
			}
		});

		builder.setView(layout);
		return builder.create();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		Dialog dialog = getDialog();
		dialog.setCanceledOnTouchOutside(false);
		dialog.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {

				//To prevent the dialog from dismissing on Back Button clicked
				if (keyCode == KeyEvent.KEYCODE_BACK) {
                    return true;
                }

				return false;
			}
		});
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	private void dismissProperly(){
		dismiss();
		DialogFragment parent = (DialogFragment)getFragmentManager().findFragmentByTag("WelcomeDialogFragment");
		parent.getDialog().show();
	}
}

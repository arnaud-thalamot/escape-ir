/**ESIPE - IR 2012/2013 - EscapeIR project for Android**/

package fr.umlv.escapeirandroid.editor;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import fr.umlv.escapeirandroid.R;

/**
 * The Class RenameBehaviourDialogFragment provides a dialog to rename an item of a list, gives back the newname through a RenameItemListener
 *
 * @author THALAMOT Arnaud
 * @author VASSEUR Simon
 * @version $Revision: 1.0 $
 */

public class RenameBehaviourDialogFragment extends DialogFragment{

	public interface RenameItemListener {
		public void onRenamedItem(String newName);
	}
	
	private EditText mInput;
	private RenameItemListener renameItemListener;
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

		final View layout = View.inflate(getActivity(), R.layout.dialog_rename_item, null);
		mInput = (EditText) layout.findViewById(R.id.name);
		((TextView) layout.findViewById(R.id.label)).setText(R.string.item_rename_label);

		builder.setIcon(0);
		builder.setTitle(getString(R.string.item_rename_title));
		builder.setCancelable(true);
		builder.setOnCancelListener(new Dialog.OnCancelListener() {
			public void onCancel(DialogInterface dialog) {
				RenameBehaviourDialogFragment.this.dismiss();
			}
		});
		builder.setNegativeButton(getString(R.string.cancel_action),
				new Dialog.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				RenameBehaviourDialogFragment.this.dismiss();
			}
		}
				);
		builder.setPositiveButton(getString(R.string.rename_action),
				new Dialog.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				String newName = mInput.getText().toString();
				renameItemListener.onRenamedItem(newName);
			}
		}
				);
		builder.setView(layout);
		return builder.create();
	}
	
	public void setOnRenameItemListener (RenameItemListener itemListener){
		renameItemListener = itemListener;
	}
}

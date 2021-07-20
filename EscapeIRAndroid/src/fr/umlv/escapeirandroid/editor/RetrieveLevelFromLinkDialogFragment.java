/**ESIPE - IR 2012/2013 - EscapeIR project for Android**/

package fr.umlv.escapeirandroid.editor;

import java.net.MalformedURLException;
import java.net.URL;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import fr.umlv.escapeirandroid.R;
import fr.umlv.escapeirandroid.game.GameBoard;
import fr.umlv.escapeirandroid.utils.FileManager;

/**
 * The Class RetrieveLevelFromLinkDialogFragment provides a dialog fragment to load a level from an HTTP link
 *
 * @author THALAMOT Arnaud
 * @author VASSEUR Simon
 * @version $Revision: 1.0 $
 */

public class RetrieveLevelFromLinkDialogFragment extends DialogFragment{

	public interface NewLevelFromLinkListener{
		public void onNewLevelFromLinkCreated(GameBoard gameboard);
	}

	private NewLevelFromLinkListener customNewLevelFromLinkListener;
	private View layout;
	private ASyncDownload aSyncDownload;
	private URL link;
	private EditText inputLink;
	private Button load;
	private boolean isQuitAsked = false;
	private GameBoard gameboard;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		layout = View.inflate(getActivity(), R.layout.dialog_retrieve_level_fromlink, null);
		inputLink = (EditText)layout.findViewById(R.id.dialog_retrieve_level_fromlink_link);
		load = (Button)layout.findViewById(R.id.dialog_retrieve_level_fromlink_load_button);

		load.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				isQuitAsked = true;
				loadInputURL();
			}
		});

		builder.setIcon(0);
		builder.setCancelable(false);
		builder.setTitle(getString(R.string.retrieve_level_fromlink_dialog_title));
		builder.setNegativeButton(getString(R.string.cancel_action),
				new Dialog.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dismiss();
				DialogFragment parent = (DialogFragment)getFragmentManager().findFragmentByTag("WelcomeDialogFragment");
				if(parent!=null){
					parent.getDialog().show();
				}
			}
		}
				);
		builder.setPositiveButton(getString(R.string.ok_action_choose_level),new Dialog.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				//Blank listener because we override it in OnCreateView to prevent it from always dismissing the dialog when the button is clicked
			}
		}
				);

		builder.setView(layout);
		return builder.create();

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		
		AlertDialog dialog = (AlertDialog)getDialog();
		dialog.setCanceledOnTouchOutside(false);
		dialog.setCancelable(false);
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
		dialog.show();
		Button positiveButton = (Button) dialog.getButton(Dialog.BUTTON_POSITIVE);
		positiveButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{

				if(gameboard == null){
					Toast.makeText(getActivity(),getString(R.string.retrieve_level_fromlink_dialog_not_loaded), Toast.LENGTH_LONG).show();
					return;
				}

				quitSuccesDialog();
			}
		});

		return super.onCreateView(inflater, container, savedInstanceState);	
	}

	/**
	 * The download of this level must be done in an asyntask because we don't know how much time it will take and we don't want to block the UI
	 * Connect to the HTTP link, and decode the stream to a gameboard
	 * Shows also a progress  dialog so the user can know is file is downloading
	 */

	static class ASyncDownload extends AsyncTask<URL, Void, GameBoard> {

		private ProgressDialog dialog;
		private boolean isErrorOccured;
		private volatile GameBoard gameboard;
		private final RetrieveLevelFromLinkDialogFragment dialogfragment;
		private URL link;

		public ASyncDownload(RetrieveLevelFromLinkDialogFragment dialogfragment,URL link) {
			this.link = link;
			this.dialogfragment = dialogfragment;
			dialog = new ProgressDialog(dialogfragment.getActivity());
		}
		@Override
		protected void onPreExecute()
		{
			dialog.setCanceledOnTouchOutside(false);
			dialog.setCancelable(false);
			dialog.setMessage("Loading level from "+link);
			dialog.show();
		}

		@Override
		protected GameBoard doInBackground(URL... urls) {
			link = urls[0];
			return FileManager.getInstance().loadLevelOverHttp(link);
		}

		@Override
		protected void onPostExecute(GameBoard result) {
			dialog.dismiss();
			if(isErrorOccured){
				dialogfragment.isQuitAsked = false;
			}
			dialogfragment.gameboard = gameboard;
			if(dialogfragment.isQuitAsked){
				dialogfragment.quitSuccesDialog();
			}
		}
	}

	/**
	 * Called to resolve an URL with a given String
	 */

	private void loadInputURL(){
		try {
			link = new URL(inputLink.getText().toString());
			aSyncDownload = new ASyncDownload(RetrieveLevelFromLinkDialogFragment.this,link);
			aSyncDownload.execute(link);
		} catch (MalformedURLException e) {
			inputLink.setError(getString(R.string.error_missing_name));
			return;
		}
	}

	/**
	 * Called when a gameboard has been loaded successfully and that we need we can leave the dialog, gives back the gameboard object trough the NewLevelFromLinkListener
	 */

	private void quitSuccesDialog(){
		customNewLevelFromLinkListener.onNewLevelFromLinkCreated(gameboard);
		dismiss();
		DialogFragment parent = (DialogFragment)getFragmentManager().findFragmentByTag("WelcomeDialogFragment");
		if(parent!=null){
			parent.getDialog().show();
		}
	}

	public void setOnNewLevelFromLinkListener(NewLevelFromLinkListener newLevelFromLinkListener){
		customNewLevelFromLinkListener = newLevelFromLinkListener;
	}
}
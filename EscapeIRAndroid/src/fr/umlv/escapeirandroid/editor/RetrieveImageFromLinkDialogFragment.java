/**ESIPE - IR 2012/2013 - EscapeIR project for Android**/

package fr.umlv.escapeirandroid.editor;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.ImageView;
import fr.umlv.escapeirandroid.R;

/**
 * The Class RetrieveImageFromLinkDialogFragment provides a dialog fragment to load background image from an HTTP link
 *
 * @author THALAMOT Arnaud
 * @author VASSEUR Simon
 * @version $Revision: 1.0 $
 */

public class RetrieveImageFromLinkDialogFragment extends DialogFragment{

	private View layout;
	private ASyncDownload aSyncDownload;
	private URL link;
	private EditText inputLink;
	private ImageView preview;
	private Bitmap image;
	private boolean isQuitAsked = false;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		layout = View.inflate(getActivity(), R.layout.dialog_retrieve_image_fromlink, null);
		Button loadButton = (Button)layout.findViewById(R.id.dialog_retrieve_image_fromlink_load_button);
		inputLink = (EditText)layout.findViewById(R.id.dialog_retrieve_image_fromlink_link);
		preview = (ImageView)layout.findViewById(R.id.dialog_retrieve_image_fromlink_preview);

		loadButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				loadInputURL();
			}
		});

		builder.setIcon(0);
		builder.setCancelable(false);
		builder.setTitle(getString(R.string.new_level_dialog_title));
		builder.setNegativeButton(getString(R.string.cancel_action),
				new Dialog.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dismiss();
				DialogFragment parent = (DialogFragment)getFragmentManager().findFragmentByTag("NewLevelDialogFragment");
				if(parent!=null){
					parent.getDialog().show();
				}
			}
		}
				);
		builder.setPositiveButton(getString(R.string.ok_action_choose_image),new Dialog.OnClickListener() {
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
		getDialog().setCanceledOnTouchOutside(false);

		AlertDialog dialog = (AlertDialog)getDialog();
		dialog.show();
		getDialog().setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {

				//To prevent the dialog from dismissing on Back Button clicked
				if (keyCode == KeyEvent.KEYCODE_BACK) {
                    return true;
                }

				return false;
			}
		});
		Button positiveButton = (Button) dialog.getButton(Dialog.BUTTON_POSITIVE);
		positiveButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				URL currentLink = null;

				try{
					currentLink = new URL(inputLink.getText().toString());
				}
				catch(MalformedURLException e){
					inputLink.setError(getString(R.string.error_missing_name));
				}

				if(currentLink.equals("")&&(image==null)){
					inputLink.setError(getString(R.string.error_missing_name));
					return;
				}

				if((((currentLink.equals(""))&&(image!=null))||(currentLink.equals(link))&&(image!=null))){
					quitSuccesDialog();
				}
				else{
					isQuitAsked = true;
					loadInputURL();
				}
			}
		});

		return super.onCreateView(inflater, container, savedInstanceState);	
	}

	/**
	 * The download of this image must be done in an asyntask because we don't know how much time it will take and we don't want to block the UI
	 * Connect to the HTTP link, and decode the stream to a Bitmap
	 * Shows also a progress  dialog so the user can know is file is downloading
	 */

	static class ASyncDownload extends AsyncTask<ImageView, Void, Bitmap> {

		volatile ImageView imageView = null;
		private ProgressDialog dialog;
		private RetrieveImageFromLinkDialogFragment dialogfragment;
		private boolean isErrorOccured;

		public ASyncDownload(RetrieveImageFromLinkDialogFragment dialogfragment) {
			imageView = dialogfragment.preview;
			this.dialogfragment = dialogfragment;
			dialog = new ProgressDialog(dialogfragment.getActivity());
		}
		@Override
		protected void onPreExecute()
		{
			dialogfragment.image = null;
			dialog.setMessage("Loading image from "+imageView.getTag().toString());
			dialog.show();
			dialog.setOnKeyListener(new OnKeyListener() {
				
				@Override
				public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
					if (keyCode == KeyEvent.KEYCODE_BACK) {
						return true;
					}
					return false;
				}
			});
		}

		@Override
		protected Bitmap doInBackground(ImageView... imageViews) {
			imageView = imageViews[0];
			return loadImageOverHttp((URL)imageView.getTag());
		}

		private Bitmap loadImageOverHttp(URL url){
			Bitmap bitmap =null;
			try {
				HttpURLConnection connexion = (HttpURLConnection)url.openConnection();
				InputStream is = connexion.getInputStream();

				bitmap = BitmapFactory.decodeStream(is);
				if (bitmap != null){
					return bitmap;
				}
			} catch (IOException e) {
				//we can't do the job to update the imageview here, because it's runned on an other thread and it is not allowed to update the UI 
				isErrorOccured = true;
				return null;
			}
			return bitmap;
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			dialog.dismiss();
			if(isErrorOccured){
				imageView.setImageResource(android.R.drawable.ic_dialog_alert);
				dialogfragment.isQuitAsked = false;
			}
			imageView.setImageBitmap(result);
			dialogfragment.image = result;
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
			aSyncDownload = new ASyncDownload(RetrieveImageFromLinkDialogFragment.this);
			link = new URL(inputLink.getText().toString());
			preview.setTag(link);
			aSyncDownload.execute(preview);
		} catch (MalformedURLException e) {
			inputLink.setError(getString(R.string.error_missing_name));
			return;
		}
	}

	/**
	 * Called when an image has been loaded sucessfully and that we need we can leave the dialog, puts the image on it's parent
	 */

	private void quitSuccesDialog(){
		NewLevelDialogFragment parent = (NewLevelDialogFragment)getFragmentManager().findFragmentByTag("NewLevelDialogFragment");
		parent.refreshBackgroundPreview(image);
		dismiss();
		parent.getDialog().show();
	}

}

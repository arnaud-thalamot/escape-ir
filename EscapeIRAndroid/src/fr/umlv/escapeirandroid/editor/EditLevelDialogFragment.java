/**ESIPE - IR 2012/2013 - EscapeIR project for Android**/

package fr.umlv.escapeirandroid.editor;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.PriorityBlockingQueue;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import fr.umlv.escapeirandroid.R;
import fr.umlv.escapeirandroid.behaviour.BehaviourType;
import fr.umlv.escapeirandroid.entity.BodyDescription;
import fr.umlv.escapeirandroid.entity.EnemyType;
import fr.umlv.escapeirandroid.game.EscapeIR;
import fr.umlv.escapeirandroid.game.GameBoard;
import fr.umlv.escapeirandroid.utils.FileManager;
import fr.umlv.escapeirandroid.utils.PositionConverter;

/**
 * The Class EditLevelDialogFragment a list of existing level to edit
 *
 * @author THALAMOT Arnaud
 * @author VASSEUR Simon
 * @version $Revision: 1.0 $
 */

public class EditLevelDialogFragment extends DialogFragment{

	public interface EditLevelListener {
		public void onEditLevelCreated(String name,Bitmap background,
				float levelDuration,HashMap<String,BehaviourType> behaviourTypes,
				HashMap<String, EnemyType> enemyTypes,ArrayList<DragableEnemy> enemies);
	}

	private EditLevelListener customEditLevelListener;
	Spinner spinner;

	/**
	 * When the positive button is clicked, it takes the selected name in the list and loads the corresponding gameboard, then pass its properties through a EditLevelListener
	 */
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

		final View layout = View.inflate(getActivity(), R.layout.dialog_edit_level, null);
		spinner = (Spinner)layout.findViewById(R.id.dialog_edit_spinner);

		ArrayList<String> levels = FileManager.getInstance().loadAvailableLevel();

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),R.layout.spinner_item,levels);
		adapter.setDropDownViewResource(R.layout.spinner_item);
		spinner.setAdapter(adapter);

		builder.setIcon(0);
		builder.setCancelable(false);
		builder.setTitle(getString(R.string.edit_level_dialog_title));
		builder.setNegativeButton(getString(R.string.cancel_action),new Dialog.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				dismiss();
				DialogFragment parent = (DialogFragment)getFragmentManager().findFragmentByTag("WelcomeDialogFragment");
				parent.getDialog().show();
			}
		}
				);
		builder.setPositiveButton(getString(R.string.ok_action),
				new Dialog.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				String templateLevelName = (String)spinner.getSelectedItem();

				GameBoard template = null;
				try {
					template = FileManager.getInstance().loadGameBoard(templateLevelName);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-genersated catch block
					e.printStackTrace();
				} catch (ParserConfigurationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SAXException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				float levelDuration = template.getLevelDuration();

				PriorityBlockingQueue<BodyDescription> bodyDescriptions = template.getEnemies();

				HashMap<String, EnemyType> ennemyTypes = template.getEnemyType();
				/*ArrayList<EnemyType> ennemyTypesList = new ArrayList<EnemyType>(ennemyTypes.values());*/
				ArrayList<DragableEnemy> dragableEnemies = new ArrayList<DragableEnemy>();

				int len = bodyDescriptions.size();
				for (int i = 0; i < len; i++) {
					BodyDescription currentBodyDescription = bodyDescriptions.poll();
					Bitmap bitmap = (ennemyTypes.get(currentBodyDescription.getEnemyTypeName())).getPicture();
					float x = PositionConverter.worldToScreenX(currentBodyDescription.getX());
					/*System.out.println("X : "+x);*/
					float y = ((EscapeIR.CURRENT_HEIGHT/10)*(levelDuration/60))-((currentBodyDescription.getTime()/60)*(EscapeIR.CURRENT_HEIGHT/10));
					/*System.out.println("Time : "+currentBodyDescription.getTime()/60);*/
					String ennemyType = currentBodyDescription.getEnemyTypeName();
					dragableEnemies.add(new DragableEnemy(bitmap, x, y, ennemyType));
				}

				customEditLevelListener.onEditLevelCreated(templateLevelName, template.getBackground(),
						levelDuration/60, template.getBehaviourType(), 
						template.getEnemyType(),dragableEnemies);
				dismiss();

			}
		}
				);

		builder.setView(layout);
		return builder.create();

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		getDialog().setCanceledOnTouchOutside(false);
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	public void setOnEditLevelListener(EditLevelListener editLevelListener){
		customEditLevelListener = editLevelListener;
	}
}

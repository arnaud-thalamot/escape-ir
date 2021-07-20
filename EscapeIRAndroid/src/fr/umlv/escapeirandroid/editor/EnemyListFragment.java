/**ESIPE - IR 2012/2013 - EscapeIR project for Android**/

package fr.umlv.escapeirandroid.editor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import fr.umlv.escapeirandroid.R;
import fr.umlv.escapeirandroid.editor.EditorActivity.onLoadEnemyTypesListener;
import fr.umlv.escapeirandroid.editor.RenameBehaviourDialogFragment.RenameItemListener;
import fr.umlv.escapeirandroid.entity.EnemyType;

/**
 * The Class EnemyListFragment a list of the know enemyType, giving the possibility to delete and rename them
 *
 * @author THALAMOT Arnaud
 * @author VASSEUR Simon
 * @version $Revision: 1.0 $
 */

public class EnemyListFragment extends ListFragment{

	public interface EnemyListener {
		public void onAddedEnemyType(EnemyType enemyType);
		public void onDeletedEnemy(String enemyTypeName);
		public void onRenamedEnemy(String oldName,String newName);
	}
	
	
	/**
	 * Menu ids for the context menu
	 */

	private static final int MENU_ID_RENAME = 1;
	private static final int MENU_ID_REMOVE = 2;

	private EnemyTypeAdapter enemyAdapter;
	private LinearLayout linearLayout;
	private EnemyListener customListener;
	private TextView mEmpty;

	private Dialog renameDialog;
	private static EnemyType currentRenameEnemyType;
	private onLoadEnemyTypesListener enemyTypesListener = new onLoadEnemyTypesListener() {

		@Override
		public void onEnemyTypesLoading(HashMap<String, EnemyType> newEnemyTypes) {
			enemyAdapter.clear();
			if(newEnemyTypes!=null){
				ArrayList<EnemyType> enemyTypes = new ArrayList<EnemyType>(newEnemyTypes.values());
				for (int i = 0; i < enemyTypes.size(); i++) {
					addEnemyType(enemyTypes.get(i));
				}
			}
		}
	};

	/**
	 * To sort the enemyTypes with their names
	 */
	
	private final Comparator<EnemyType> enemyTypeSorter = new Comparator<EnemyType>() {

		@Override
		public int compare(EnemyType enemyType, EnemyType otherEnemyType) {
			return enemyType.getName().compareTo(otherEnemyType.getName());
		}
	};
	
	/**
	 * EnemyTypeAdapter extends displays the name of the enemyTypes and their sprite
	 *
	 */

	private class EnemyTypeAdapter extends ArrayAdapter<EnemyType>{

		private final LayoutInflater inflater;
		private final HashMap<String, Drawable> iconHashMap = new HashMap<String, Drawable>();

		public EnemyTypeAdapter(Context context) {
			super(context, 0);
			inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public void clear() {
			iconHashMap.clear();
			super.clear();
		}

		@Override
		public void add(EnemyType enemyType) {
			iconHashMap.put(enemyType.getName(),new BitmapDrawable(getResources(),enemyType.getPicture()));
			super.add(enemyType);
		}

		@Override
		public void remove(EnemyType enemyType) {
			iconHashMap.remove(enemyType.getName());
			super.remove(enemyType);
		}
		
		public void rename(EnemyType enemyType, String newName){
			iconHashMap.remove(enemyType.getName());
			enemyType.setName(newName);
			iconHashMap.put(newName,new BitmapDrawable(getResources(),enemyType.getPicture()));
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.behaviour_item, parent, false);
			}

			final EnemyType enemyType = getItem(position);
			final TextView label = (TextView) convertView;

			label.setTag(enemyType);
			label.setText(enemyType.getName());
			label.setCompoundDrawablesWithIntrinsicBounds(iconHashMap.get(enemyType.getName()),null, null, null);

			return convertView;
		}
	}

	/**
	 * Sets the listener to get the new data
	 */
	
	@Override
	public void onAttach(Activity activity) {

		((EditorActivity)getActivity()).setOnLoadEnemyListener(enemyTypesListener);
				super.onAttach(activity);
	}

	/**
	 * Sets the adapter with the datas
	 */
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		enemyAdapter = new EnemyTypeAdapter(getActivity());
		setListAdapter(enemyAdapter);
		linearLayout = (LinearLayout)inflater.inflate(R.layout.enemies_list, container, false);
		mEmpty = (TextView)linearLayout.findViewById(android.R.id.empty);
		return linearLayout;
	}

	/**
	 * Called when the given enemyType needs to be added to the list of enemyTypes
	 * @param enemyType
	 */
	
	private void addEnemyType(EnemyType enemyType){

		enemyAdapter.add(enemyType);
		enemyAdapter.sort(enemyTypeSorter);
		enemyAdapter.notifyDataSetChanged();
		checkForEmpty();
	}

	/**
	 * onDestroy of the fragment, dismiss rename dialog
 	 */
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		cleanupRenameDialog();
	}

	/**
	 * Registers a context menu on the list
	 */
	@Override
	public void onResume() {
		registerForContextMenu(getListView());
		super.onResume();
	}

	
	/**
	 * Context menu initialization with two labels
	 */
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenu.ContextMenuInfo menuInfo) {

		super.onCreateContextMenu(menu, v, menuInfo);

		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
		menu.setHeaderTitle(((TextView) info.targetView).getText());

		menu.add(0, MENU_ID_RENAME, 0, R.string.enemy_rename);
		menu.add(0, MENU_ID_REMOVE, 0, R.string.enemy_delete);
	}

	/**
	 * Context menu handling
	 */
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {

		final AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
		final EnemyType enemyType = (EnemyType) menuInfo.targetView.getTag();

		switch (item.getItemId()) {
		case MENU_ID_RENAME:
			renameEnemyType(enemyType);
			return true;
		case MENU_ID_REMOVE:
			deleteEnemyType(enemyType);
			return true;
		}

		return super.onContextItemSelected(item);
	}

	/**
	 * Called when the given enemyType needs to be renamed
	 * @param enemyType enemyType to rename
	 */
	
	private void renameEnemyType(EnemyType enemyType){
		currentRenameEnemyType = enemyType;
		showRenameGestureDialogFragment();
	}
	
	/**
	 * Called when the given enemyType needs to be deleted
	 * @param behaviourType enemyType to delete
	 */

	private void deleteEnemyType(EnemyType enemyType) {

		final EnemyTypeAdapter adapter = enemyAdapter;
		adapter.setNotifyOnChange(false);
		adapter.remove(enemyType);
		adapter.sort(enemyTypeSorter);
		checkForEmpty();
		customListener.onDeletedEnemy(enemyType.getName());
		adapter.notifyDataSetChanged();
		Toast.makeText(getActivity(), R.string.enemy_delete_success, Toast.LENGTH_SHORT).show(); //TODO change string
	}

	/**
	 * Called when the fragment is destroyed, dismisses the rename dialog
	 */
	
	private void cleanupRenameDialog() {
		if (renameDialog != null) {
			renameDialog.dismiss();
			renameDialog = null;
		}
		currentRenameEnemyType = null;
	}

	/**
	 * Called when the current Rename EnemyType has to be renamed with the given name
	 * @param newName the new name of the enemyType
	 */
	
	public void changeEnemyTypeName(String newName) {

		final int count = enemyAdapter.getCount();

		for (int i = 0; i < count; i++) {

			final EnemyType currentEnemyType = enemyAdapter.getItem(i);

			if (currentRenameEnemyType.getName().equals(currentEnemyType.getName())) {
				customListener.onRenamedEnemy(currentEnemyType.getName(),newName);
				enemyAdapter.rename(currentRenameEnemyType, newName);
				break;
			}
		}
		enemyAdapter.notifyDataSetChanged();
		currentRenameEnemyType = null;
	}

	/**
	 * Pops the rename dialog
	 */
	
	public void showRenameGestureDialogFragment() {
		RenameBehaviourDialogFragment dialog = new RenameBehaviourDialogFragment();
		dialog.setTargetFragment(this, 0);
		dialog.setOnRenameItemListener(new RenameItemListener() {

			@Override
			public void onRenamedItem(String newName) {
				changeEnemyTypeName(newName);
			}
		});
		dialog.show(getFragmentManager(), "RenameGestureDialogFragment");
	}

	/**
	 * Displays something if there is no enemyType available
	 */
	
	private void checkForEmpty() {
		if (enemyAdapter.getCount() == 0) {
			mEmpty.setText(R.string.enemy_empty);
		}
	}

	/**
	 * To set a listener on this fragment to retrieve data on add and on rename events
	 * @param behaviourListener
	 */
	public void setOnEnemyListener(EnemyListener enemyListener){
		customListener = enemyListener;
	}
}

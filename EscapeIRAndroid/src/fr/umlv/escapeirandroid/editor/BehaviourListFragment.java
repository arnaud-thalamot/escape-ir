/**ESIPE - IR 2012/2013 - EscapeIR project for Android**/
package fr.umlv.escapeirandroid.editor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.gesture.Gesture;
import android.gesture.GestureStroke;
import android.graphics.Bitmap;
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
import fr.umlv.escapeirandroid.behaviour.BehaviourType;
import fr.umlv.escapeirandroid.editor.EditorActivity.onLoadBehaviourListener;
import fr.umlv.escapeirandroid.editor.RenameBehaviourDialogFragment.RenameItemListener;

/**
 * The Class BehaviourListFragment displays a list of the know behaviours, giving the possibility to delete and rename them
 *
 * @author THALAMOT Arnaud
 * @author VASSEUR Simon
 * @version $Revision: 1.0 $
 */


public class BehaviourListFragment extends ListFragment{

	/**
	 * Listener to transport data to the activity when a behaviour is deleted or renamed
	 *
	 */

	public interface BehaviourListener {
		public void onDeletedBehaviour(String name);
		public void onRenamedBehaviour(String oldName,String newName);
	}

	/**
	 * Behaviour Adapter displays the name of the behaviours and their bitmap representation
	 *
	 */

	private class BehaviourTypeAdapter extends ArrayAdapter<BehaviourType> {

		final Resources resources = getResources();
		int drawColor = resources.getColor(R.color.behaviour_color);
		int iconDimension = (int) resources.getDimension(R.dimen.behaviour_icon);
		int icon = (int) resources.getDimension(R.dimen.behaviour_icon_size);

		private final LayoutInflater inflater;
		private final HashMap<String, Drawable> iconHashMap = new HashMap<String, Drawable>();

		public BehaviourTypeAdapter(Context context) {
			super(context, 0);
			inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public void clear() {
			iconHashMap.clear();
			super.clear();
		}

		private Drawable computePointsToDrawable(BehaviourType behaviourType){
			Gesture gesture = new Gesture();
			gesture.addStroke(new GestureStroke(behaviourType.getPointsOnScreen()));
			Bitmap bitmap = gesture.toBitmap(icon, icon,iconDimension, drawColor);
			return new BitmapDrawable(getResources(),bitmap);
		}

		@Override
		public void add(BehaviourType behaviourType) {

			iconHashMap.put(behaviourType.getName(),computePointsToDrawable(behaviourType));
			super.add(behaviourType);
		}

		@Override
		public void remove(BehaviourType behaviourType) {
			iconHashMap.remove(behaviourType.getName());
			super.remove(behaviourType);
		}

		public void rename(BehaviourType behaviourType, String newName){
			Drawable drawable = computePointsToDrawable(behaviourType);
			iconHashMap.remove(behaviourType.getName());
			behaviourType.name = newName;
			iconHashMap.put(newName,drawable);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.behaviour_item, parent, false);
			}

			final BehaviourType behaviourType = getItem(position);
			final TextView label = (TextView) convertView;
			String behaviourTypeName = behaviourType.getName();

			label.setTag(behaviourType);
			label.setText(behaviourTypeName);
			label.setCompoundDrawablesWithIntrinsicBounds(iconHashMap.get(behaviourTypeName),null, null, null);

			return convertView;
		}
	}

	/**
	 * Menu ids for the context menu
	 */

	private static final int MENU_ID_RENAME = 1;
	private static final int MENU_ID_REMOVE = 2;

	private Dialog renameDialog;
	private static BehaviourType currentRenameBehaviourType;

	private BehaviourTypeAdapter behaviourTypeAdapter;
	private TextView mEmpty;
	private LinearLayout linearLayout;

	private BehaviourListener customListener;
	private onLoadBehaviourListener behaviourListener = new onLoadBehaviourListener() {

		@Override
		public void onBehaviourLoading(HashMap<String, BehaviourType> newBehaviourType) {
			behaviourTypeAdapter.clear();
			if(newBehaviourType != null){
				ArrayList<BehaviourType> enemyTypes = new ArrayList<BehaviourType>(newBehaviourType.values());
				int i;
				for (i = 0; i < enemyTypes.size(); i++) {
					addBehaviour(enemyTypes.get(i));
				}
			}
		}
	};

	/**
	 * To sort the behaviours with their names
	 */

	private final Comparator<BehaviourType> behaviourTypeSorter = new Comparator<BehaviourType>() {
		public int compare(BehaviourType object1, BehaviourType object2) {
			return object1.getName().compareTo(object2.getName());
		}
	};



	/**
	 * Sets the listener to get the new data
	 */

	@Override
	public void onAttach(Activity activity) {

		((EditorActivity)getActivity()).setOnLoadBehaviourListener(behaviourListener);
		super.onAttach(activity);
	}

	/**
	 * Sets the adapter with the datas
	 */

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

		behaviourTypeAdapter = new BehaviourTypeAdapter(getActivity());
		setListAdapter(behaviourTypeAdapter);
		linearLayout = (LinearLayout)inflater.inflate(R.layout.behaviour_list, container, false);
		mEmpty = (TextView)linearLayout.findViewById(android.R.id.empty);

		return linearLayout;
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

		menu.add(0, MENU_ID_RENAME, 0, R.string.item_rename);
		menu.add(0, MENU_ID_REMOVE, 0, R.string.item_delete);
	}

	/**
	 * Context menu handling
	 */

	@Override
	public boolean onContextItemSelected(MenuItem item) {

		final AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
		final BehaviourType gesture = (BehaviourType) menuInfo.targetView.getTag();

		switch (item.getItemId()) {
		case MENU_ID_RENAME:
			renameBehaviourType(gesture);
			return true;
		case MENU_ID_REMOVE:
			deleteBehaviourType(gesture);
			return true;
		}

		return super.onContextItemSelected(item);
	}

	/**
	 * Called when the given behaviour needs to be renamed
	 * @param behaviourType behaviour to rename
	 */

	private void renameBehaviourType(BehaviourType behaviourType){
		currentRenameBehaviourType = behaviourType;
		showRenameGestureDialogFragment();
	}

	/**
	 * Called when the given behaviour needs to be deleted
	 * @param behaviourType behaviour to delete
	 */


	private void deleteBehaviourType(BehaviourType behaviourType) {

		final BehaviourTypeAdapter adapter = behaviourTypeAdapter;
		adapter.setNotifyOnChange(false);
		adapter.remove(behaviourType);
		adapter.sort(behaviourTypeSorter);
		checkForEmpty();
		customListener.onDeletedBehaviour(behaviourType.name);
		adapter.notifyDataSetChanged();
		Toast.makeText(getActivity(), R.string.item_delete_success, Toast.LENGTH_SHORT).show();
	}

	/**
	 * Called when the fragment is destroyed, dismisses the rename dialog
	 */


	private void cleanupRenameDialog() {
		if (renameDialog != null) {
			renameDialog.dismiss();
			renameDialog = null;
		}
		currentRenameBehaviourType = null;
	}

	/**
	 * Displays something if there is no behaviour available
	 */

	private void checkForEmpty() {
		if (behaviourTypeAdapter.getCount() == 0) {
			mEmpty.setText(R.string.behaviour_empty);
		}
	}

	/**
	 * Called when the current Rename Behaviour has to be renamed with the given name
	 * @param newName the new name of the behaviour
	 */

	public void changeBehaviourTypeName(String newName) {

		final BehaviourType renameBehaviourType = currentRenameBehaviourType;
		final BehaviourTypeAdapter adapter = behaviourTypeAdapter;
		final int count = adapter.getCount();

		int i;

		for (i=0; i < count; i++) {
			final BehaviourType currentBehaviourType = adapter.getItem(i);

			if (currentRenameBehaviourType.name.equals(currentBehaviourType.name)) {
				customListener.onRenamedBehaviour(renameBehaviourType.name,newName);
				adapter.rename(renameBehaviourType, newName);
				break;
			}
		}
		adapter.notifyDataSetChanged();
		currentRenameBehaviourType = null;
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
				changeBehaviourTypeName(newName);
			}
		});
		dialog.show(getFragmentManager(), "RenameGestureDialogFragment");
	}

	/**
	 * Called when the given behaviourType needs to be added to the list of behaviours
	 * @param behaviourType
	 */

	private void addBehaviour(BehaviourType behaviourType){

		behaviourTypeAdapter.add(behaviourType);
		behaviourTypeAdapter.sort(behaviourTypeSorter);
		behaviourTypeAdapter.notifyDataSetChanged();
		checkForEmpty();
	}

	/**
	 * To set a listener on this fragment to retrieve data on add and on rename events
	 * @param behaviourListener
	 */

	public void setOnBehaviourListener(BehaviourListener behaviourListener){
		customListener = behaviourListener;
	}
}

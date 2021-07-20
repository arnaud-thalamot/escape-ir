/**ESIPE - IR 2012/2013 - EscapeIR project for Android**/
package fr.umlv.escapeirandroid.editor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.concurrent.PriorityBlockingQueue;

import fr.umlv.escapeirandroid.R;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.animation.Animation;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import fr.umlv.escapeirandroid.behaviour.BehaviourType;
import fr.umlv.escapeirandroid.editor.AddEnemyTypeFragment.EnemyTypeListener;
import fr.umlv.escapeirandroid.editor.BehaviourListFragment.BehaviourListener;
import fr.umlv.escapeirandroid.editor.CreateBehaviourFragment.CreateBehaviourListener;
import fr.umlv.escapeirandroid.editor.EnemyListFragment.EnemyListener;
import fr.umlv.escapeirandroid.editor.LevelPropertiesFragment.LevelPropertiesListener;
import fr.umlv.escapeirandroid.editor.MapEditorView.MapEditorListener;
import fr.umlv.escapeirandroid.editor.WelcomeDialogFragment.WelcomeListener;
import fr.umlv.escapeirandroid.editor.menu.CollapseAnimation;
import fr.umlv.escapeirandroid.editor.menu.CustomExpandableListAdapter;
import fr.umlv.escapeirandroid.editor.menu.ExpandAnimation;
import fr.umlv.escapeirandroid.editor.menu.ExpandableSection;
import fr.umlv.escapeirandroid.entity.BodyDescription;
import fr.umlv.escapeirandroid.entity.EnemyType;
import fr.umlv.escapeirandroid.game.EscapeIR;
import fr.umlv.escapeirandroid.game.GameBoard;
import fr.umlv.escapeirandroid.utils.FileManager;
import fr.umlv.escapeirandroid.utils.PositionConverter;

/**
 * The Class EditorActivity is the heart of the editor, it provides several listeners to push data to fragments, also sets several listeners to get data from fragments
 * It is in charge of loading the fragments, managing the menu, collecting data and keep them accurate, and also save a level
 *
 * @author THALAMOT Arnaud
 * @author VASSEUR Simon
 * @version $Revision: 1.0 $
 */

public class EditorActivity extends FragmentActivity{

	/**
	 * Interfaces to transfer data to fragments
	 *
	 */

	public interface onLevelChangedListener {
		public void onLevelLoaded(ArrayList<DragableEnemy> ennemies,float duration,Bitmap background);
	}

	public interface onLoadBehaviourListener {
		public void onBehaviourLoading(HashMap<String, BehaviourType> behaviourTypes);
	}

	public interface onLoadEnemyTypesListener {
		public void onEnemyTypesLoading(HashMap<String, EnemyType> enemyTypes);
	}

	public interface onLoadAddEnemyTypesListener {
		public void onAddEnemyTypesLoading(HashMap<String, EnemyType> enemyTypes,HashMap<String, BehaviourType> behaviourTypes);
	}

	/**
	 * TAGs of the fragments
	 */

	private static final String LEVEL_PROPERTIES_FRAGMENT_TAG = "level_properties";
	private static final String GESTURE_LIST_FRAGMENT_TAG = "gesture_list";
	private static final String GESTURE_CREATE_FRAGMENT_TAG = "gesture_create";
	private static final String ENEMY_LIST_FRAGMENT_TAG = "enemy_list";
	private static final String ENEMY_ADD_FRAGMENT_TAG = "enemy_add";
	private static final String MAP_EDITOR_FRAGMENT_TAG = "map_editor";

	/**
	 * Threshold values to recognize a swipe
	 */

	private static final int SWIPE_THRESHOLD = 100;
	private static final int SWIPE_VELOCITY_THRESHOLD = 100;

	/**
	 * Ratio used on screen width to create menu animations
	 */

	private static final float RATIO_MENU_WIDTH = 0.9f;

	/**
	 * Listeners
	 */

	private onLevelChangedListener customLevelChangedListener;
	private onLoadBehaviourListener customLoadBehaviourListener;
	private onLoadEnemyTypesListener customLoadEnemyListener;
	private onLoadAddEnemyTypesListener customLoadAddEnemyTypesListener;

	/**
	 * Game data
	 */
	private String levelName;
	private float levelDuration;
	private Bitmap levelBackground;
	private HashMap<String, EnemyType> enemyTypes;
	private HashMap<String, BehaviourType> behaviourTypes;
	private ArrayList<DragableEnemy> enemies;

	/**
	 * Dragging state records
	 */

	private ImageView dragging;
	private String draggingEnemyType;

	/**
	 * Relative Layout to draw on when an enemy is dragged
	 */

	private RelativeLayout relativeLayoutEditor;
	private RelativeLayout.LayoutParams params;

	/**
	 * Expandable List view and source
	 */

	private ExpandableListView expandableList;
	private ArrayList<ExpandableSection> sections;

	/**
	 * FrameLayout where fragments are displayed
	 */

	private FrameLayout frameLayout;
	private FragmentManager fragmentManager;

	/**
	 * Animation Menu
	 */

	private boolean isExpanded = true;
	private Animation expandAnim;
	private Animation collapseAnim;

	/**
	 * Swipe detector
	 */

	private GestureDetector swipeDetector;

	/**
	 * Fragments
	 */

	private MapEditorView mapEditorView;
	private BehaviourListFragment behaviourListFragment;
	private AddEnemyTypeFragment enemyAddFragment;
	private EnemyListFragment enemyListFragment;
	private CreateBehaviourFragment createBehaviourFragment;
	private MapEditorFragment mapEditorFragment;
	private LevelPropertiesFragment levelPropertiesFragment;

	/**
	 * Used to retrieve data changes on the MapEditor
	 */

	private MapEditorListener mapEditorListener = new MapEditorListener() {

		@Override
		public void onDeletedEnemy(DragableEnemy enemy) {
			enemies.remove(enemy);
		}

		@Override
		public void onAddedEnnemy(DragableEnemy enemy) {
			enemies.add(enemy);
		}
	};

	/**
	 * Used to retrieve data changes saved on enemyList
	 */

	private EnemyListener enemyListener = new EnemyListener() {

		@Override
		public void onRenamedEnemy(String oldName, String newName) {
			EnemyType enemyType = enemyTypes.get(oldName);
			EnemyType renamedEnemyType = new EnemyType(enemyType.getName(),enemyType.getPicture(),
					enemyType.getHp(),enemyType.getRythm(),newName,enemyType.getNbMissile(),
					enemyType.getNbFireball(),enemyType.getNbShiboleet(),enemyType.getNbTriforce());
			enemyTypes.remove(oldName);
			enemyTypes.put(newName, renamedEnemyType);

			int i = 0;
			while(i < enemies.size()) {
				DragableEnemy dEnemyType = enemies.get(i);
				String enemyTyp = enemies.get(i).ennemyType;
				if(enemyTyp.equals(oldName)){
					DragableEnemy renamedDragableEnemy = new DragableEnemy(dEnemyType.sprite,dEnemyType.rect.left,dEnemyType.rect.top,newName);
					enemies.remove(dEnemyType);
					enemies.add(renamedDragableEnemy);
				}
				else{
					i++;
				}
			}
			refreshMenuDragandDrop();
		}

		@Override
		public void onDeletedEnemy(String enemyTypeName) {
			enemyTypes.remove(enemyTypeName);
			int i = 0;
			while(i < enemies.size()) {
				DragableEnemy currentEnemyType = enemies.get(i);
				String enemyTyp = currentEnemyType.ennemyType;
				if(enemyTyp.equals(enemyTypeName)){
					enemies.remove(currentEnemyType);
				}
				else{
					i++;
				}
			}

			refreshMenuDragandDrop();
		}

		@Override
		public void onAddedEnemyType(EnemyType enemyType) {
			enemyTypes.put(enemyType.getName(), enemyType);
			refreshMenuDragandDrop();
		}
	};

	/**
	 * Used to retrieve data after an enemyType add saved on addEnemyTypeFragment
	 */

	private EnemyTypeListener enemyTypeListener = new EnemyTypeListener() {

		@Override
		public void onEnemyTypeAdded(EnemyType newEnemyType) {
			enemyTypes.put(newEnemyType.getName(),newEnemyType);
			refreshMenuDragandDrop();
		}
	};

	/**
	 * Used to retrieve data changes saved on BehaviourList
	 */

	private BehaviourListener behaviourListener = new BehaviourListener() {

		@Override
		public void onDeletedBehaviour(String name) {
			behaviourTypes.remove(name);

			ArrayList<String> enemyTypesToDelete = new ArrayList<String>();
			
			Collection<EnemyType> vals = enemyTypes.values();
			ArrayList<EnemyType> values = new ArrayList<EnemyType>(vals);
			for (int i = 0; i < values.size(); i++) {
				String behaviourType = values.get(i).getBehaviour();
				if(name.equals(behaviourType)){
					enemyTypes.remove(values.get(i).getName());
				}
			}
			
			for (int j = 0; j < enemyTypesToDelete.size(); j++) {
				
				int i = 0;
				String enemyTyp = enemyTypesToDelete.get(j);
				while(i < enemies.size()) {
					DragableEnemy dEnemyType = enemies.get(i);
					if(enemyTyp.equals(name)){
						enemies.remove(dEnemyType);
					}
					else{
						i++;
					}
				}
				
			}
			refreshMenuDragandDrop();
			
		}

		@Override
		public void onRenamedBehaviour(String oldName, String newName) {
			behaviourTypes.get(oldName).setName(newName);
			BehaviourType behaviourTyp = behaviourTypes.get(oldName);
			BehaviourType renamedBehaviourType = new BehaviourType(newName,behaviourTyp.getPointsOrigin());
			behaviourTypes.remove(oldName);
			behaviourTypes.put(newName, renamedBehaviourType);

			Collection<EnemyType> vals = enemyTypes.values();
			ArrayList<EnemyType> values = new ArrayList<EnemyType>(vals);
			int i = 0;
			while(i < values.size()){
				EnemyType enemyType = values.get(i);
				String behaviourType = enemyType.getBehaviour();
				if(oldName.equals(behaviourType)){
					EnemyType renamedEnemyType = new EnemyType(enemyType.getName(),enemyType.getPicture(),enemyType.getHp(),enemyType.getRythm(),newName,enemyType.getNbMissile(),enemyType.getNbFireball(),enemyType.getNbShiboleet(),enemyType.getNbTriforce());
					enemyTypes.remove(oldName);
					enemyTypes.put(oldName, renamedEnemyType);
				}
				i++;

			}

			i = 0;
			while(i < enemies.size()) {
				DragableEnemy dEnemyType = enemies.get(i);
				String enemyTyp = enemies.get(i).ennemyType;
				if(enemyTyp.equals(oldName)){
					DragableEnemy renamedDragableEnemy = new DragableEnemy(dEnemyType.sprite,dEnemyType.rect.left,dEnemyType.rect.top,newName);
					enemies.remove(dEnemyType);
					enemies.add(renamedDragableEnemy);
				}
				else{
					i++;
				}
			}
			refreshMenuDragandDrop();
		}
	};

	private CreateBehaviourListener createBehaviourListener = new CreateBehaviourListener() {

		@Override
		public void onAddedBehaviour(BehaviourType behaviourType) {
			behaviourTypes.put(behaviourType.getName(),behaviourType);
		}
	};

	/**
	 * Used to retrieve data after changes saved in LevelProperties
	 */

	private LevelPropertiesListener levelPropertiesListener = new LevelPropertiesListener() {

		@Override
		public void onPropertiesChanged(String name, Bitmap background,
				float duration) {
			if(!name.equals("")){
				levelName = name;
			}
			if(background != null){
				levelBackground = background;
			}
			if(duration > 0){
				levelDuration = duration;
			}

		}
	};

	/**
	 * Listener on the expandable list click on item
	 */

	private final OnChildClickListener childListener = new OnChildClickListener() {

		@Override
		public boolean onChildClick(ExpandableListView parent, View v,int groupPosition, int childPosition, long id) {

			//cannot switch on Strings in java 1.6

			switch (groupPosition) {

			case 0:
				switch (childPosition) {
				case 0:
					android.support.v4.app.Fragment levelPropertiesFragment = fragmentManager.findFragmentByTag(LEVEL_PROPERTIES_FRAGMENT_TAG);
					if(levelPropertiesFragment != null){
						((LevelPropertiesFragment)levelPropertiesFragment).saveProperties();
					}
					break;
				}
				break;
			case 2:
				switch (childPosition) {
				case 0:
					if(enemyListFragment == null){
						enemyListFragment = new EnemyListFragment();

						enemyListFragment.setOnEnemyListener(enemyListener);
					}
					loadFragment(enemyListFragment, ENEMY_LIST_FRAGMENT_TAG);
					customLoadEnemyListener.onEnemyTypesLoading(enemyTypes);
					break;
				case 1:
					if(enemyAddFragment == null){
						enemyAddFragment = new AddEnemyTypeFragment();
						enemyAddFragment.setOnEnemyTypeListener(enemyTypeListener);
					}
					loadFragment(enemyAddFragment, ENEMY_ADD_FRAGMENT_TAG);
					customLoadAddEnemyTypesListener.onAddEnemyTypesLoading(enemyTypes,behaviourTypes);
					break;

				case 2 :
					android.support.v4.app.Fragment addEnnemyTypes = fragmentManager.findFragmentByTag(ENEMY_ADD_FRAGMENT_TAG);
					if(addEnnemyTypes != null){
						((AddEnemyTypeFragment)addEnnemyTypes).saveEnemyTypes();
						refreshMenuDragandDrop();
					}
					break;
				}
				break;
			case 3:
				switch (childPosition) {
				case 0:
					if(behaviourListFragment == null){
						behaviourListFragment=new BehaviourListFragment();
						behaviourListFragment.setOnBehaviourListener(behaviourListener);
					}
					loadFragment(behaviourListFragment, GESTURE_LIST_FRAGMENT_TAG);

					customLoadBehaviourListener.onBehaviourLoading(behaviourTypes);
					break;
				case 1:
					if(createBehaviourFragment==null){
						createBehaviourFragment=new CreateBehaviourFragment();
						createBehaviourFragment.setOnCreateBehaviourListener(createBehaviourListener);
					}
					loadFragment(createBehaviourFragment, GESTURE_CREATE_FRAGMENT_TAG);
					break;
				case 2:
					android.support.v4.app.Fragment gestureCreateFragment = fragmentManager.findFragmentByTag(GESTURE_CREATE_FRAGMENT_TAG);
					if(gestureCreateFragment != null){
						((CreateBehaviourFragment)gestureCreateFragment).addBehaviour(frameLayout);
					}
					break;
				}
				break;
			}
			return true;
		}
	};

	/**
	 * Listener on expandable list view, where a section is expanded
	 */

	private final OnGroupExpandListener groupExpandListener = new OnGroupExpandListener() {

		@Override
		public void onGroupExpand(int groupPosition) {

			int sectionCount = ((CustomExpandableListAdapter) expandableList.getExpandableListAdapter()).getGroupCount();

			for (int i = 0; i < sectionCount; i++) {
				if(i != groupPosition){
					expandableList.collapseGroup(i);
				}
			}

			if(groupPosition == 0){
				if(levelPropertiesFragment==null){
					levelPropertiesFragment=new LevelPropertiesFragment();
					levelPropertiesFragment.setOnLevelPropertiesListener(levelPropertiesListener);
				}
				loadFragment(levelPropertiesFragment, LEVEL_PROPERTIES_FRAGMENT_TAG);
			}

			if(groupPosition == 1){
				if(mapEditorFragment==null){
					mapEditorFragment=new MapEditorFragment();
				}

				loadFragment(mapEditorFragment, MAP_EDITOR_FRAGMENT_TAG);
				mapEditorView = ((MapEditorView)findViewById(R.id.map_editor_view));
				mapEditorView.setMapEditorListener(mapEditorListener);
				customLevelChangedListener.onLevelLoaded(enemies, levelDuration, levelBackground);

			}
			if(groupPosition == 4){
				saveGameBoard();
			}
		}
	};

	/**
	 * Used to detect a swipe of the menu and execute it
	 */

	private final GestureDetector.SimpleOnGestureListener swipeListener = new GestureDetector.SimpleOnGestureListener(){
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2,float velocityX, float velocityY) {
			float dY = e2.getY() - e1.getY();
			float dX = e2.getX() - e1.getX();
			if (Math.abs(dX) > Math.abs(dY)) {
				if (Math.abs(dX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
					if (dX > 0) {
						if(isExpanded){
							expandableList.startAnimation(collapseAnim);
							isExpanded = false;
							return true;
						}
					} else {
						if(!isExpanded){
							expandableList.startAnimation(expandAnim);
							isExpanded=true;
							return true;
						}
					}
				}
			}
			return false;
		}
	};

	/**
	 * Used to retrieve data after Welcome dialog dismissed
	 */

	private final WelcomeListener welcomeListener = new WelcomeListener() {

		@Override
		public void onNewLevelFromTemplate(String name, Bitmap background,
				float duration, HashMap<String, BehaviourType> behavioursTypes,
				HashMap<String, EnemyType> enemiesTypes,ArrayList<DragableEnemy> dragableEnemies) {

			loadContainers(name, background, duration, behavioursTypes, enemiesTypes, dragableEnemies);
		}

		@Override
		public void onNewLevelFromScratch(String name, Bitmap background,float duration) {

			loadContainers(name, background, duration, null, null, null);
		}

		@Override
		public void onEditLevel(String name, Bitmap background, float duration,
				HashMap<String, BehaviourType> behavioursTypes,
				HashMap<String, EnemyType> enemiesTypes,
				ArrayList<DragableEnemy> dragableEnemies) {

			loadContainers(name, background, duration, behavioursTypes, enemiesTypes, dragableEnemies);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.level_editor);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		relativeLayoutEditor = ((RelativeLayout)findViewById(R.id.editor_relative));
		frameLayout = (FrameLayout)findViewById(R.id.editor_frame_layout);
		params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

		expandableList = (ExpandableListView)findViewById(R.id.expandable_list);
		populateExpandableList();
		expandableList.setAdapter(new CustomExpandableListAdapter(this,sections));
		expandableList.setOnChildClickListener(childListener);
		expandableList.setOnGroupExpandListener(groupExpandListener);

		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		int screenWidth = metrics.widthPixels;
		expandAnim = new ExpandAnimation(expandableList,0,(int)(screenWidth*RATIO_MENU_WIDTH),20);
		collapseAnim = new CollapseAnimation(expandableList,(int)(screenWidth*RATIO_MENU_WIDTH),0,20);

		swipeDetector = new GestureDetector(this,swipeListener);
		fragmentManager = getSupportFragmentManager();

		if(savedInstanceState == null){
			WelcomeDialogFragment dialog = new WelcomeDialogFragment();
			dialog.setOnWelcomeListener(welcomeListener);
			dialog.show(fragmentManager, "WelcomeDialogFragment");
		}
	}


	/**
	 * Used to drag and drop enemy
	 */

	//We need to override dispatch because we want to capture events before any other view treats them 

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {

		this.swipeDetector.onTouchEvent(event);

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			if((dragging == null)&&(mapEditorView!=null)){

				int flatPos = expandableList.pointToPosition((int)event.getX()-frameLayout.getWidth(),(int)event.getY());
				long rowId = expandableList.getExpandableListPosition(flatPos);
				int groupPosition = ExpandableListView.getPackedPositionGroup(rowId);
				int childPosition = ExpandableListView.getPackedPositionChild(rowId);

				if((groupPosition == 1)&&(childPosition != -1)){

					String enemyTypeFromMenu = (String) expandableList.getExpandableListAdapter().getChild(groupPosition, childPosition);

					Drawable drawable = null;

					EnemyType enemyType = enemyTypes.get(enemyTypeFromMenu);

					if(enemyType != null){
						drawable = new BitmapDrawable(getResources(),enemyType.getPicture());
					}

					if(drawable!=null){
						dragging = new ImageView(this);
						draggingEnemyType = enemyTypeFromMenu;
						dragging.setImageDrawable(drawable);

						params.leftMargin = (int)event.getX();
						params.topMargin = (int)event.getY();
						dragging.setLayoutParams(params);
						relativeLayoutEditor.addView(dragging);
					}
				}
			}
			break;
		case MotionEvent.ACTION_MOVE:
			if(dragging != null){
				params.leftMargin = (int)event.getX();
				params.topMargin = (int)event.getY();
				dragging.setLayoutParams(params);
			}
			break;
		case MotionEvent.ACTION_UP:
			if(dragging != null){

				Rect outRect = new Rect();

				if(frameLayout != null){

					frameLayout.getDrawingRect(outRect);
					boolean droppable = outRect.contains((int)event.getX()+dragging.getWidth(), (int)event.getY());
					if(droppable){

						Bitmap bmap = ((BitmapDrawable)dragging.getDrawable()).getBitmap();
						//Must have scrollY to know where to drop on the appropriate Y position in the mapView
						mapEditorView.onDropped(bmap,(int)event.getX(),((VerticalScrollView)findViewById(R.id.map_scroll)).getScrollY()+(int)event.getY(),draggingEnemyType);
					}
				}
				relativeLayoutEditor.removeViewInLayout(dragging);
				relativeLayoutEditor.invalidate();
				dragging = null;
				draggingEnemyType = null;

			}
			break;
		}

		return super.dispatchTouchEvent(event);
	}

	/**
	 * Initialize the expandable list with the menu items labels
	 */

	private void populateExpandableList(){

		sections = new ArrayList<ExpandableSection>();

		populateSection(sections, "Properties", "Save Properties");
		populateSection(sections, "Map", "Type 1","Type 2","Type 3");
		populateSection(sections, "Ennemies","List","Add Ennemy Type","Save Ennemy Type");
		populateSection(sections, "Behaviours", "List","Add Behaviour","Save Behaviour");
		populateSection(sections, "Save Level");
	}

	/**
	 * Initialize a section of the expandable list with the child items labels
	 */

	private void populateSection(ArrayList<ExpandableSection> sections,String title,String... children){
		ExpandableSection parent = new ExpandableSection();
		parent.setTitle(title);
		parent.setArrayChildren(new ArrayList<String>(Arrays.asList(children)));
		sections.add(parent);
	}

	/**
	 * Replace the current fragment by the given fragment with the given tag in the framelayout
	 * @param fragment fragment to replace with
	 * @param tag tag of the given fragment
	 */

	private void loadFragment(Fragment fragment,String tag){
		android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		fragmentTransaction.replace(R.id.editor_frame_layout,fragment,tag).commit();
		fragmentManager.executePendingTransactions();
	}

	/**
	 * refresh menu items of the map section with new item, useful on event on enemyTypes
	 */

	private void refreshMenuDragandDrop(){
		sections.get(1).setArrayChildren(new ArrayList<String>(enemyTypes.keySet()));
	}

	/**
	 * Saves a gameboard with the current data, this gameboard will be used after in game
	 */

	private void saveGameBoard() {

		if((!levelName.equals(""))&&(levelBackground!=null)&&(levelDuration>=0)&&(behaviourTypes.size()!=0)&&(enemyTypes.size()!=0)&&(enemies.size()!=0)){
		
		PriorityBlockingQueue<BodyDescription> newEnnemiesQueue = new PriorityBlockingQueue<BodyDescription>();

		for (int i = 0; i < enemies.size(); i++) {
			float x = PositionConverter.screenToWorldX(enemies.get(i).rect.left);
			float y = 100;
			float time = (((EscapeIR.BACKGROUND_STEP)*levelDuration)-enemies.get(i).rect.top)/(EscapeIR.BACKGROUND_STEP);
			String type = enemies.get(i).ennemyType;
			newEnnemiesQueue.add(new BodyDescription(x, y, time, type));
		}

		GameBoard gameboard = new GameBoard(levelName, levelBackground, levelDuration, behaviourTypes, enemyTypes, newEnnemiesQueue);
		FileManager.getInstance().saveGameBoard(gameboard);
		(Toast.makeText(getApplicationContext(),R.string.succes_save_level, Toast.LENGTH_LONG)).show();
		return;
		}
		(Toast.makeText(getApplicationContext(),R.string.failure_save_level, Toast.LENGTH_LONG)).show();
	}

	/**
	 * Load the given data in the appropriate fields, useful when we load a level
	 * @param name
	 * @param background
	 * @param duration
	 * @param behavioursTypes
	 * @param enemiesTypes
	 * @param dragableEnemies
	 */

	private void loadContainers(String name, Bitmap background, float duration,
			HashMap<String, BehaviourType> behavioursTypes,
			HashMap<String, EnemyType> enemiesTypes,
			ArrayList<DragableEnemy> dragableEnemies){

		levelName = name;
		levelBackground = background;
		levelDuration = duration;

		if(behavioursTypes == null){
			behaviourTypes = new HashMap<String, BehaviourType>();
		}
		else{
			behaviourTypes = behavioursTypes;
		}

		if(enemiesTypes == null){
			enemyTypes = new HashMap<String, EnemyType>();
		}
		else{
			enemyTypes = enemiesTypes;
		}

		if(dragableEnemies == null){
			enemies = new ArrayList<DragableEnemy>();
		}
		else{
			enemies = dragableEnemies;
		}
		refreshMenuDragandDrop();
	}

	/**
	 * Listener used to transfer needed values of a level to the mapEditorView
	 * @param onLevelChangedListener
	 */

	public void setOnLevelChangedListener(onLevelChangedListener onLevelChangedListener){
		customLevelChangedListener = onLevelChangedListener;
	}

	/**
	 * Listener used to transfer needed values of a level to the behaviourListFragment
	 * @param loadBehaviourListener
	 */

	public void setOnLoadBehaviourListener(onLoadBehaviourListener loadBehaviourListener){
		customLoadBehaviourListener = loadBehaviourListener;
	}

	/**
	 * 
	 * Listener used to transfer needed values of a level to the enemyListFragment
	 */

	public void setOnLoadEnemyListener(onLoadEnemyTypesListener loadEnemyListener){
		customLoadEnemyListener = loadEnemyListener;
	}

	/**
	 * Listener used to transfer the enemyTypes to the addEnemyTypeFragment
	 * @param onLoadAddEnemyTypesListener
	 */

	public void setOnAddLoadEnemyListener(onLoadAddEnemyTypesListener onLoadAddEnemyTypesListener){
		customLoadAddEnemyTypesListener = onLoadAddEnemyTypesListener;
	}

	/**
	 * Called to save the state of the activity
	 */

	@Override
	protected void onSaveInstanceState(Bundle outState) {

		outState.putString("levelName", levelName);

		super.onSaveInstanceState(outState);
	}

	/**
	 * Called to restore the state of the activity
	 */

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		levelName = savedInstanceState.getString("levelName");
		super.onRestoreInstanceState(savedInstanceState);
	}
}

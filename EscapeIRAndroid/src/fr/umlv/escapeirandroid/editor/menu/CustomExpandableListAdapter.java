/**ESIPE - IR 2012/2013 - EscapeIR project for Android**/

package fr.umlv.escapeirandroid.editor.menu;

import java.util.ArrayList;

import android.content.Context;
import android.content.res.Resources;
import android.database.DataSetObserver;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;
import fr.umlv.escapeirandroid.R;

/**
 * The Class CustomExpandableListAdapter provides an adapter to handle data displayed in an ExpandableListView
 *
 * @author THALAMOT Arnaud
 * @author VASSEUR Simon
 * @version $Revision: 1.0 $
 */

public class CustomExpandableListAdapter extends BaseExpandableListAdapter {

	private ArrayList<ExpandableSection> sections;	
	private LayoutInflater inflater;

	public CustomExpandableListAdapter(Context context, ArrayList<ExpandableSection> parent){
		sections = parent;
		inflater = LayoutInflater.from(context);
	}
	
	/**
	 * Used to known how much children must be displayed
	 * @return number of children
	 */
	
	@Override
	public int getGroupCount() {
		return sections.size();
	}

	/**
	 * Gives the number of children of a given section
	 * @param sectionPostion position of the section in the expandablelistview
	 * @return number of children
	 */
	
	@Override
	public int getChildrenCount(int sectionPosition) {
		return sections.get(sectionPosition).getArrayChildren().size();
	}
	
	/**
	 * Returns the children of a given section
	 * @param sectionPostion position of the section in the expandablelistview
	 * @return object representing children
	 */

	@Override
	public Object getGroup(int sectionPosition) {
		return sections.get(sectionPosition).getTitle();
	}

	/**
	 * Returns a children in a given section by it's position at a given position in the section
	 * @param sectionPosition position of the section in the expandable list view
	 * @param childPosition postion of the child in the given section
	 * @return object representing the child
	 */
	
	@Override
	public Object getChild(int sectionPosition, int childPosition) {
		return sections.get(sectionPosition).getArrayChildren().get(childPosition);
	}

	/**
	 * Returns the id of a group, here it's the position in the expandable list list
	 * @return position in the expandable list
	 */
	
	@Override
	public long getGroupId(int sectionPosition) {
		return sectionPosition;
	}
	
	/**
	 * Returns the id of a child, here it's the position in the section
	 * @return position in its section
	 */

	@Override
	public long getChildId(int sectionPosition, int childPosition) {
		return childPosition;
	}

	/**
	 * Defines if items have stable ids
	 */
	
	@Override
	public boolean hasStableIds() {
		return true;
	}
	
	/**
	 * @see android.widget.ExpandableListAdapter#getGroupView(int, boolean, android.view.View, android.view.ViewGroup)
	 */
	
	@Override
	public View getGroupView(int sectionPosition, boolean isExpanded, View view, ViewGroup viewGroup) {

		if (view == null) {
			view = inflater.inflate(R.layout.expandablelistview_group, viewGroup,false);
		}
		
		Resources ressources = view.getResources();
		
		//Changes the font of the text and its color
		
		Typeface typeFace=Typeface.createFromAsset(view.getContext().getAssets(),ressources.getString(R.string.expandable_list_view_font));
		TextView textView = (TextView) view.findViewById(R.id.list_item_text_view);
		textView.setTypeface(typeFace);
		textView.setTextColor(ressources.getColor(R.color.expandable_list_view_section_color));
		textView.setText(getGroup(sectionPosition).toString());
		
		return view;
	}

	/**
	 * @see android.widget.ExpandableListAdapter#getGroupView(int, boolean, android.view.View, android.view.ViewGroup)
	 */
	
	@Override
	public View getChildView(int sectionPosition, int childPosition, boolean isExpanded, View view, ViewGroup viewGroup) {
		
		if (view == null) {
			view = inflater.inflate(R.layout.expandablelistview_child, viewGroup,false);
		}
		
		//Changes the font of the text and its color
		
		Typeface typeFace=Typeface.createFromAsset(view.getContext().getAssets(),view.getResources().getString(R.string.expandable_list_view_font));
		TextView textView = (TextView) view.findViewById(R.id.list_item_text_child);
		textView.setTypeface(typeFace);
		textView.setText(sections.get(sectionPosition).getArrayChildren().get(childPosition));
		return view;
	}

	/**
	 * Asks if a child is selectable, here all children are selectable
	 */
	
	@Override
	public boolean isChildSelectable(int sectionPosition, int childPosition) {
		return true;
	}

	/**
	 * @see android.widget.BaseExpandableListAdapter#registerDataSetObserver(android.database.DataSetObserver)
	 */
	
	@Override
	public void registerDataSetObserver(DataSetObserver observer) {
		super.registerDataSetObserver(observer);
	}
}
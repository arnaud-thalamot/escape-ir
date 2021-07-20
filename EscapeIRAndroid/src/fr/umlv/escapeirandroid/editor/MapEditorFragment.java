/**ESIPE - IR 2012/2013 - EscapeIR project for Android**/

package fr.umlv.escapeirandroid.editor;

import fr.umlv.escapeirandroid.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

/**
 * The Class MapEditorFragment contains a MapEditorView to create and edit a game map of enemy
 *
 * @author THALAMOT Arnaud
 * @author VASSEUR Simon
 * @version $Revision: 1.0 $
 */


public class MapEditorFragment extends Fragment{
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		return (ScrollView)inflater.inflate(R.layout.map_editor, container, false);
	}
}

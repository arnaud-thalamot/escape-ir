/**ESIPE - IR 2012/2013 - EscapeIR project for Android**/

package fr.umlv.escapeirandroid.editor;

import java.util.ArrayList;

import android.gesture.Gesture;
import android.gesture.GestureOverlayView;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import fr.umlv.escapeirandroid.R;
import fr.umlv.escapeirandroid.behaviour.BehaviourType;
import fr.umlv.escapeirandroid.utils.PositionConverter;

/**
 * The Class CreateBehaviourFragment provides a fragment to draw a behaviour, name it and gives it back trough a CreateBehaviourListener
 * The behaviour is drawn on a GestureOverlayView
 *
 * @author THALAMOT Arnaud
 * @author VASSEUR Simon
 * @version $Revision: 1.0 $
 */

public class CreateBehaviourFragment extends Fragment{

	public interface CreateBehaviourListener {
		public void onAddedBehaviour(BehaviourType behaviourType);
	}
	
	private static final float GESTURE_LENGTH_THRESHOLD = 120.0f;
	
	private Gesture behaviour;
	private GestureOverlayView overlay;
	private CreateBehaviourListener customListener;
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

		LinearLayout linearLayout = (LinearLayout)inflater.inflate(R.layout.create_behaviour, container, false);
		overlay = (GestureOverlayView) linearLayout.findViewById(R.id.gestures_overlay);
		overlay.addOnGestureListener(new GesturesProcessor());
		return linearLayout;
	}

	public void addBehaviour(View v) {
		if (behaviour != null) {
			final TextView input = (TextView) getView().findViewById(R.id.behaviour_name);
			final String name = input.getText().toString();
			if (name.length() == 0) {
				input.setError(getString(R.string.error_missing_name));
				return;
			}
			
			//Points need to be converted to Jbox points so the xml always contains points independant from a device
			
			float[] tmp = behaviour.getStrokes().get(0).points;
			ArrayList<PointF> points = new ArrayList<PointF>();
			for (int i = 0; i < tmp.length; i+=2) {
				points.add(new PointF(PositionConverter.screenToWorldX(tmp[i]),PositionConverter.screenToWorldY(tmp[i+1])));
			}
			BehaviourType behaviourType = new BehaviourType(name,points);
			
			customListener.onAddedBehaviour(behaviourType);
			Toast.makeText(getActivity(), "Behaviour "+name+" saved !", Toast.LENGTH_LONG).show();
			input.setText("");
			overlay.clear(false);
			return;
		}
		(Toast.makeText(getActivity(),R.string.no_behaviour_drawn , Toast.LENGTH_LONG)).show();
	}

	private class GesturesProcessor implements GestureOverlayView.OnGestureListener {
		public void onGestureStarted(GestureOverlayView overlay, MotionEvent event) {
			behaviour = null;
		}

		public void onGestureEnded(GestureOverlayView overlay, MotionEvent event) {
			behaviour = overlay.getGesture();
			if (behaviour.getLength() < GESTURE_LENGTH_THRESHOLD) {
				overlay.clear(false);
			}
		}

		public void onGestureCancelled(GestureOverlayView overlay, MotionEvent event) {
		}

		@Override
		public void onGesture(GestureOverlayView overlay, MotionEvent event) {
		}
	}

	public void setOnCreateBehaviourListener(CreateBehaviourListener createBehaviourListener){
		customListener = createBehaviourListener;
	}
}

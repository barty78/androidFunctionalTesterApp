package analytica.pericoach.android;

import hydrix.pfmat.generic.DisplaySample;
import hydrix.pfmat.generic.Result;
import hydrix.pfmat.generic.TEST;

import java.util.ArrayList;

import com.pietrantuono.pericoach.newtestapp.R;
import com.pietrantuono.pericoach.newtestapp.R.drawable;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ResultArrayAdapter extends ArrayAdapter<DisplaySample> {

	private ArrayList<DisplaySample> mResults;
	
	public ResultArrayAdapter(Context context, int resource,
			ArrayList<DisplaySample> results) {
		
		super(context, resource, results);
		// TODO Auto-generated constructor stub
		this.mResults = results;
	}

	
	public View getView(int position, View convertView, ViewGroup parent) {
		
		View v = convertView;
		
		// first check to see if the view is null. if so, we have to inflate it.
		// to inflate it basically means to render, or show, the view.
		if (v == null) {
			LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = inflater.inflate(R.layout.test_row, null);
		}
		
		/*
		 * Recall that the variable position is sent in as an argument to this method.
		 * The variable simply refers to the position of the current object in the list. (The ArrayAdapter
		 * iterates through the list we sent it)
		 * 
		 * Therefore, i refers to the current Item object.
		 */
		DisplaySample c = mResults.get(position);

		if (c != null) {
			
			TextView testtxt = (TextView) v.findViewById(R.id.TextView0);
			
			TextView sensor0txt = (TextView) v.findViewById(R.id.TextView1_1);
			TextView sensor1txt = (TextView) v.findViewById(R.id.TextView2_1);
			TextView sensor2txt = (TextView) v.findViewById(R.id.TextView3_1);
			
			TextView sensor0mintxt = (TextView) v.findViewById(R.id.TextView1_2_VAL);
			TextView sensor1mintxt = (TextView) v.findViewById(R.id.TextView2_2_VAL);
			TextView sensor2mintxt = (TextView) v.findViewById(R.id.TextView3_2_VAL);
			
			TextView sensor0maxtxt = (TextView) v.findViewById(R.id.TextView1_3_VAL);
			TextView sensor1maxtxt = (TextView) v.findViewById(R.id.TextView2_3_VAL);
			TextView sensor2maxtxt = (TextView) v.findViewById(R.id.TextView3_3_VAL);
			
			ImageView mImage0 = (ImageView) v.findViewById(R.id.ResultIcon1);
			ImageView mImage1 = (ImageView) v.findViewById(R.id.ResultIcon2);
			ImageView mImage2 = (ImageView) v.findViewById(R.id.ResultIcon3);
			
		
			testtxt.setText(c.getTestDesc());
			sensor0txt.setText(Integer.toString(c.getAvgForce().getLiteralSensor0()));
			sensor1txt.setText(Integer.toString(c.getAvgForce().getLiteralSensor1()));
			sensor2txt.setText(Integer.toString(c.getAvgForce().getLiteralSensor2()));
			
			sensor0mintxt.setText(Integer.toString(c.getMinForce().getLiteralSensor0()));
			sensor1mintxt.setText(Integer.toString(c.getMinForce().getLiteralSensor1()));
			sensor2mintxt.setText(Integer.toString(c.getMinForce().getLiteralSensor2()));
			
			sensor0maxtxt.setText(Integer.toString(c.getMaxForce().getLiteralSensor0()));
			sensor1maxtxt.setText(Integer.toString(c.getMaxForce().getLiteralSensor1()));
			sensor2maxtxt.setText(Integer.toString(c.getMaxForce().getLiteralSensor2()));
						
			Result[] entity = new Result[3];
			entity = c.getResults();
			for (int i = 0; i < entity.length; i++) {
				switch (i) {
				case 0:
					if (entity[i] != null) {
						if (entity[i].toString() == "CLEAR") {
							mImage0.setImageResource(0);
							sensor0txt.setTextColor(Color.BLACK);
						}
						
						if (entity[i].toString() == "PASS") {
							mImage0.setImageResource(drawable.tick_icon);
							sensor0txt.setTextColor(Color.GREEN);
							
						} else if (entity[i].toString() == "FAIL") {
							mImage0.setImageResource(drawable.cross_icon);
							sensor0txt.setTextColor(Color.RED);
						}					
					}

					break;
				case 1:
					if (entity[i] != null) {

						if (entity[i].toString() == "CLEAR") {
							mImage1.setImageResource(0);
							sensor1txt.setTextColor(Color.BLACK);
						}
						
						if (entity[i].toString() == "PASS") {
							mImage1.setImageResource(drawable.tick_icon);
							sensor1txt.setTextColor(Color.GREEN);
							
						} else if (entity[i].toString() == "FAIL") {
							mImage1.setImageResource(drawable.cross_icon);
							sensor1txt.setTextColor(Color.RED);
						}
					}
					break;
				case 2:
					if (entity[i] != null) {

						if (entity[i].toString() == "CLEAR") {
							mImage2.setImageResource(0);
							sensor2txt.setTextColor(Color.BLACK);
						}
						
						if (entity[i].toString() == "PASS") {
							mImage2.setImageResource(drawable.tick_icon);
							sensor2txt.setTextColor(Color.GREEN);
							
						} else if (entity[i].toString() == "FAIL") {
							mImage2.setImageResource(drawable.cross_icon);
							sensor2txt.setTextColor(Color.RED);
						}
					}
					break;
				}
			}
		}
		
		return v;
	}
}

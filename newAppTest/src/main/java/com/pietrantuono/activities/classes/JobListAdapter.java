package com.pietrantuono.activities.classes;

import java.util.ArrayList;

import com.pietrantuono.pericoach.newtestapp.R;

import server.pojos.Job;
import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class JobListAdapter extends BaseAdapter {
	private ArrayList<Job> list;
	private Activity activity;

	

	public JobListAdapter(ArrayList<Job> list, Activity activity) {
		this.list = list;
		this.activity = activity;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v=activity.getLayoutInflater().inflate(R.layout.job_row, null);
		TextView jobnumber=(TextView)v.findViewById(R.id.jobnumber);
		TextView quantity=(TextView)v.findViewById(R.id.quantity);
		TextView description =(TextView)v.findViewById(R.id.description);
		if(list.get(position).getDescription()!=null)description.setText(list.get(position).getDescription());
		else description.setText("");
		ImageView imageView=(ImageView)v.findViewById(R.id.image);
		if(list.get(position).getIslogging()==0)imageView.setImageResource(R.drawable.ic_save_white_24dp);//(R.drawable.ic_action_test);
		else imageView.setVisibility(View.INVISIBLE);//setImageResource(R.drawable.ic_action_prod);
		list.get(position).getId();
		String jobno=list.get(position).getJobno();
		if(jobno!=null){
			jobnumber.setText(jobno);
		}
		else {jobnumber.setText("EMPTY");}
		
		String q=""+list.get(position).getQuantity();
		if(q!=null){
			quantity.setText(q);
		}
		
		return v;
	}

}


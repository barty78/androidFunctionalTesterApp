package com.pietrantuono.activities.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.activeandroid.Model;
import com.activeandroid.query.Select;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pietrantuono.pericoach.newtestapp.R;

import java.util.ArrayList;
import java.util.List;

import server.pojos.Device;

public class DevicesListFragment extends Fragment {
    private ListView listView;
    private Context context;

    public DevicesListFragment() {
    }
    public static DevicesListFragment newInstance() {
        DevicesListFragment fragment = new DevicesListFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public void onAttach(Context context) {
       super.onAttach(context);
        this.context=context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.context=null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.devices_list_fragment, container, false);
        listView= (ListView) v.findViewById(R.id.list);
        v.findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                populateList();
            }
        });
        populateList();
        return v;
    }

    private void populateList() {
        List<Device> temp = new Select().from(Device.class).execute();
        ArrayList<Device> devices=new ArrayList<>();
        devices.addAll(temp);
        listView.setAdapter(new DevicesListAdapter(context,devices));
    }

    private class DevicesListAdapter extends BaseAdapter{
        private ArrayList<server.pojos.Device> devices;
        private Context context;
        private Gson gson;
        private DevicesListAdapter(Context context,ArrayList<Device> devices) {
            this.devices = devices;
            this.context = context;
            gson= new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        }

        @Override
        public int getCount() {
            return devices.size();
        }

        @Override
        public Object getItem(int position) {
            return devices.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if(convertView!=null){
                viewHolder= (ViewHolder) convertView.getTag();
            }
            else {
                LayoutInflater inflater = LayoutInflater.from(context);
                convertView=inflater.inflate(R.layout.devices_row, parent, false);
                viewHolder=new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            }
            String text=gson.toJson(devices.get(position),server.pojos.Device.class);
            if(text!=null)viewHolder.setText(text);
            else viewHolder.setText("");
            return convertView;
        }

        private class ViewHolder {
            private TextView textView;

            public ViewHolder(View v) {
                textView= (TextView) v.findViewById(R.id.text);
            }

            public void setText(String text){
                textView.setText(text);
            }
        }
    }

}

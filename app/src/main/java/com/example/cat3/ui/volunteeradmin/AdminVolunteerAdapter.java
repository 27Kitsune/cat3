package com.example.cat3.ui.volunteeradmin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cat3.R;
import com.example.cat3.ui.volunteer.VolunteerData;

import java.util.List;

public class AdminVolunteerAdapter extends BaseAdapter {
    private List<VolunteerData> volunteerList;
    private Context context;
    private VolunteeradminFragment volunteeradminFragment;

    public AdminVolunteerAdapter(Context context, List<VolunteerData> volunteerList, VolunteeradminFragment fragment) {
        this.context = context;
        this.volunteerList = volunteerList;
        this.volunteeradminFragment = fragment;
    }

    @Override
    public int getCount() {
        return volunteerList.size();
    }

    @Override
    public Object getItem(int position) {
        return volunteerList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.admin_list_item_volunteer, parent, false);
        }

        VolunteerData volunteerData = volunteerList.get(position);

        TextView textName = convertView.findViewById(R.id.textName2);
        TextView textEvent2 = convertView.findViewById(R.id.textEvent2);

        textName.setText("Name: " + volunteerData.getName2());
        textEvent2.setText("Event: " + volunteerData.getevent1());


        // Set click listener
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle item click
                volunteeradminFragment.showVolunteerDetailsPopup(volunteerData);
            }
        });

        return convertView;
    }

    // Custom method to remove a volunteer form from the adapter
    public void removeVolunteer(VolunteerData volunteerData) {
        volunteerList.remove(volunteerData);
        notifyDataSetChanged();
    }

    // Custom methods for data management
    public void add2(VolunteerData volunteerData) {
        volunteerList.add(volunteerData);
        notifyDataSetChanged();
    }

    public void clear2() {
        volunteerList.clear();
        notifyDataSetChanged();
    }
}

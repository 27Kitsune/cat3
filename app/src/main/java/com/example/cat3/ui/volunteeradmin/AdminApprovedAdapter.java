package com.example.cat3.ui.volunteeradmin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.cat3.R;
import com.example.cat3.ui.volunteer.VolunteerData;

import java.util.List;

public class AdminApprovedAdapter extends BaseAdapter {
    private List<VolunteerData> approvedList;
    private Context context;
    private VolunteeradminFragment volunteeradminFragment;

    public AdminApprovedAdapter(Context context, List<VolunteerData> approvedList, VolunteeradminFragment fragment) {
        this.context = context;
        this.approvedList = approvedList;
        this.volunteeradminFragment = fragment;
    }

    @Override
    public int getCount() {
        return approvedList.size();
    }

    @Override
    public Object getItem(int position) {
        return approvedList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.admin_list_item_event, parent, false);
        }

        VolunteerData volunteerData = approvedList.get(position);

        TextView textEvent = convertView.findViewById(R.id.textEvent);
        TextView textLocation = convertView.findViewById(R.id.textLocation);

        textEvent.setText("Event: " + volunteerData.getevent1());
        if (volunteerData.getlocation1() != null) {
            textLocation.setText("Location: " + volunteerData.getlocation1());
        } else {
            textLocation.setVisibility(View.GONE);
        }

        // Set click listener
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle item click
                volunteeradminFragment.showApprovedDetailsPopup(volunteerData);
            }
        });

        return convertView;
    }

    // Custom method to remove an event from the adapter
    public void removeApproved(VolunteerData volunteerData) {
        approvedList.remove(volunteerData);
        notifyDataSetChanged();
    }

    // Custom methods for data management
    public void add3(VolunteerData volunteerData) {
        approvedList.add(volunteerData);
        notifyDataSetChanged();
    }

    public void clear3() {
        approvedList.clear();
        notifyDataSetChanged();
    }
}


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

public class AdminEventAdapter extends BaseAdapter {
    private List<VolunteerData> eventList;
    private Context context;
    private VolunteeradminFragment volunteeradminFragment;

    public AdminEventAdapter(Context context, List<VolunteerData> eventList, VolunteeradminFragment fragment) {
        this.context = context;
        this.eventList = eventList;
        this.volunteeradminFragment = fragment;
    }

    @Override
    public int getCount() {
        return eventList.size();
    }

    @Override
    public Object getItem(int position) {
        return eventList.get(position);
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

        VolunteerData volunteerData = eventList.get(position);

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
                volunteeradminFragment.showEventDetailsPopup(volunteerData);
            }
        });

        return convertView;
    }

    // Custom method to remove an event from the adapter
    public void removeEvent(VolunteerData volunteerData) {
        eventList.remove(volunteerData);
        notifyDataSetChanged();
    }

    // Custom methods for data management
    public void add1(VolunteerData volunteerData) {
        eventList.add(volunteerData);
        notifyDataSetChanged();
    }

    public void clear1() {
        eventList.clear();
        notifyDataSetChanged();
    }
}

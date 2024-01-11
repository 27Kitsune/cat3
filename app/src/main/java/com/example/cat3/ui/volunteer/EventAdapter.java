package com.example.cat3.ui.volunteer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.cat3.R;

import java.util.List;

public class EventAdapter extends BaseAdapter {
    private List<VolunteerData> eventList;
    private Context context;

    public EventAdapter(Context context, List<VolunteerData> eventList) {
        this.context = context;
        this.eventList = eventList;
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
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_event, parent, false);
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

        return convertView;
    }

    // Custom methods for data management
    public void add(VolunteerData volunteerData) {
        eventList.add(volunteerData);
        notifyDataSetChanged();
    }

    public void clear() {
        eventList.clear();
        notifyDataSetChanged();
    }
}

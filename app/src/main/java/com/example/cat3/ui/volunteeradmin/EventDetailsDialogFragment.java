package com.example.cat3.ui.volunteeradmin;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Parcelable;

import androidx.fragment.app.DialogFragment;

import com.example.cat3.R;
import com.example.cat3.ui.volunteer.VolunteerData;

public class EventDetailsDialogFragment extends DialogFragment {
    private static final String ARG_VOLUNTEER_DATA = "volunteerData";

    public static EventDetailsDialogFragment newInstance(VolunteerData volunteerData) {
        EventDetailsDialogFragment fragment = new EventDetailsDialogFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_VOLUNTEER_DATA, (Parcelable) volunteerData);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle args = getArguments();
        if (args != null) {
            VolunteerData volunteerData = args.getParcelable(ARG_VOLUNTEER_DATA);

            // Customize the dialog content based on volunteerData
            String eventDetails = "Event: " + volunteerData.getevent1() + "\n"
                    + "Location: " + volunteerData.getlocation1() + "\n";

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Event Details")
                    .setMessage(eventDetails)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // Do nothing on click
                        }
                    });
            return builder.create();
        }

        // Return an empty dialog if arguments are not available
        return new Dialog(getActivity());
    }
}

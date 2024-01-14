package com.example.cat3.ui.volunteeradmin;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.cat3.R;
import com.example.cat3.databinding.FragmentVolunteeradminBinding;
import com.example.cat3.ui.volunteer.VolunteerData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class VolunteeradminFragment extends Fragment {
    private AdminEventAdapter eventAdapter;
    private AdminVolunteerAdapter volunteerAdapter;
    private AdminApprovedAdapter approvedAdapter;
    private DatabaseReference eventsRef;
    private DatabaseReference volunteerRef;
    private DatabaseReference approvedRef;
    private FragmentVolunteeradminBinding binding;
    private Context context;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentVolunteeradminBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        context = requireContext();

        final ListView eventListView = binding.eventListView;
        final ListView volunteerListView = binding.volunteerListView;
        final ListView approvedListView = binding.approvedListView;

        // Initialize the EventAdapter with an empty list
        eventAdapter = new AdminEventAdapter(requireContext(), new ArrayList<>(),this);
        eventListView.setAdapter(eventAdapter);

        // Initialize the VolunteerAdapter with an empty list
        volunteerAdapter = new AdminVolunteerAdapter (requireContext(), new ArrayList<>(),this);
        volunteerListView.setAdapter(volunteerAdapter);

        // Initialize the ApprovedAdapter with an empty list
        approvedAdapter = new AdminApprovedAdapter (requireContext(), new ArrayList<>(),this);
        approvedListView.setAdapter(approvedAdapter);

        // Retrieve event data from Firebase and add it to the adapter
        eventsRef = FirebaseDatabase.getInstance().getReference("Pending_Events");
        eventsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                updateEventList(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle the error
            }
        });

        // Retrieve volunteer form data from Firebase and add it to the adapter
        volunteerRef = FirebaseDatabase.getInstance().getReference("Pending_Volunteer_Forms");
        volunteerRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                updateVolunteerList(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle the error
            }
        });

        // Retrieve approved event data from Firebase and add it to the adapter
        approvedRef = FirebaseDatabase.getInstance().getReference("Approved_Events");
        approvedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                updateApprovedList(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle the error
            }
        });

        return root;
    }

    // Custom method to show a popup with event details
    public void showEventDetailsPopup(VolunteerData volunteerData) {
        // Inflate the custom layout
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_event_details, null);

        // Get references to the TextViews, ImageView, and Buttons in the custom layout
        TextView textTitle = dialogView.findViewById(R.id.textTitle);
        TextView textEvent = dialogView.findViewById(R.id.textEvent);
        ImageView imageEvent = dialogView.findViewById(R.id.imageEvent);
        Button btnApprove = dialogView.findViewById(R.id.btnApprove);
        Button btnDeny = dialogView.findViewById(R.id.btnDeny);

        // Set text and image in the TextViews and ImageView
        textTitle.setText("Event Details");
        textEvent.setText("Event: " + volunteerData.getevent1() + "\n"
                + "Location: " + volunteerData.getlocation1() + "\n"
                + "Date: " + volunteerData.getDate1() + "\n"
                + "Name: " + volunteerData.getName1() + "\n"
                + "Number: " + volunteerData.getNumber1() + "\n"
                + "Email: " + volunteerData.getEmail1());

        // Load the image from the URL using Picasso library
        Picasso.get().load(volunteerData.getImageUrl1()).into(imageEvent);

        // Build and show an AlertDialog with the custom layout
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogView);

        // Create the AlertDialog
        AlertDialog dialog = builder.create();

        // Handle Approve button click
        btnApprove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                approveEvent(volunteerData);

                //dismiss the dialog
                dialog.dismiss();
            }
        });

        // Handle Deny button click
        btnDeny.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Remove the event locally from the adapter
                eventAdapter.removeEvent(volunteerData);

                // Remove the event from Firebase database
                removeEventFromDatabase(volunteerData);

                // For now, you can simply dismiss the dialog
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    // Custom method to show a popup with volunteer details
    public void showVolunteerDetailsPopup(VolunteerData volunteerData) {
        // Inflate the custom layout
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_volunteer_details, null);

        // Get references to the TextViews, ImageView, and Buttons in the custom layout
        TextView textTitle2 = dialogView.findViewById(R.id.textTitle2);
        TextView textEvent2 = dialogView.findViewById(R.id.textEvent2);
        TextView textName2 = dialogView.findViewById(R.id.textname2);
        TextView textIc2 = dialogView.findViewById(R.id.textic2);
        TextView textNumber2 = dialogView.findViewById(R.id.textnumber2);
        Button btnApprove2 = dialogView.findViewById(R.id.btnApprove2);
        Button btnDeny2 = dialogView.findViewById(R.id.btnDeny2);

        // Set text and image in the TextViews and ImageView
        textTitle2.setText("Volunteer Details");
        textEvent2.setText("Event: " + volunteerData.getevent1() + "\n"
                + "Name: " + volunteerData.getName2() + "\n"
                + "IC: " + volunteerData.getIc() + "\n"
                + "Number: " + volunteerData.getNumber2() + "\n"
                + "Email: " + volunteerData.getEmail2());


        // Build and show an AlertDialog with the custom layout
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogView);

        // Create the AlertDialog
        AlertDialog dialog = builder.create();

        // Handle Approve button click
        btnApprove2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                approveVolunteer(volunteerData);
                //dismiss the dialog
                dialog.dismiss();
            }
        });

        // Handle Deny button click
        btnDeny2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Remove the volunteer locally from the adapter
                volunteerAdapter.removeVolunteer(volunteerData);

                // Remove the volunteer form from Firebase database
                removeVolunteerFromDatabase(volunteerData);

                // For now, you can simply dismiss the dialog
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public void showApprovedDetailsPopup(VolunteerData volunteerData) {
        // Inflate the custom layout
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_approved_details, null);

        // Get references to the TextViews, ImageView, and Buttons in the custom layout
        TextView textTitle3 = dialogView.findViewById(R.id.textTitle3);
        TextView textEvent3 = dialogView.findViewById(R.id.textEvent3);
        ImageView imageEvent3 = dialogView.findViewById(R.id.imageEvent3);
        Button btnDeny3 = dialogView.findViewById(R.id.btnDeny3);

        // Set text and image in the TextViews and ImageView
        textTitle3.setText("Event Details");
        textEvent3.setText("Event: " + volunteerData.getevent1() + "\n"
                + "Location: " + volunteerData.getlocation1() + "\n"
                + "Date: " + volunteerData.getDate1() + "\n"
                + "Name: " + volunteerData.getName1() + "\n"
                + "Number: " + volunteerData.getNumber1() + "\n"
                + "Email: " + volunteerData.getEmail1());

        // Load the image from the URL using Picasso library
        Picasso.get().load(volunteerData.getImageUrl1()).into(imageEvent3);

        // Build and show an AlertDialog with the custom layout
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogView);

        // Create the AlertDialog
        AlertDialog dialog = builder.create();

        // Handle Deny button click
        btnDeny3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Remove the event locally from the adapter
                approvedAdapter.removeApproved(volunteerData);

                // Remove the event from Firebase database
                removeApprovedFromDatabase(volunteerData);

                // For now, you can simply dismiss the dialog
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public interface ApprovalCallback {
        void onApprovalComplete();
    }

    // Custom method to add an event to the adapter
    private void approveEvent(VolunteerData volunteerData) {
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference pendingEventsRef = rootRef.child("Pending_Events");
        DatabaseReference approvedEventsRef = rootRef.child("Approved_Events");

        // Find the event with matching eventId in Pending_Events
        Query query = pendingEventsRef.orderByChild("eventId").equalTo(volunteerData.getEventId());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Event found in Pending_Events, remove it
                    for (DataSnapshot eventSnapshot : dataSnapshot.getChildren()) {
                        eventSnapshot.getRef().removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    // Successfully removed from Pending_Events, now add to Approved_Events
                                    DatabaseReference newApprovedEventRef = approvedEventsRef.push();
                                    newApprovedEventRef.setValue(volunteerData);

                                    // Update the local list only after successful removal and addition
                                    eventAdapter.removeEvent(volunteerData);

                                    // Show a success message
                                    Toast.makeText(context, "Event Approved", Toast.LENGTH_SHORT).show();
                                } else {
                                    // Failed to remove event from Pending_Events
                                    Toast.makeText(context, "Failed to approve event", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                } else {
                    // Event not found in Pending_Events
                    Toast.makeText(context, "Event not found in Pending_Events", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle the error
                Log.e("Firebase", "Query canceled.", databaseError.toException());
            }
        });
    }

    // Custom method to add volunteer to the adapter
    private void approveVolunteer(VolunteerData volunteerData) {
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference pendingVolunteersRef = rootRef.child("Pending_Volunteer_Forms");
        DatabaseReference approvedVolunteersRef = rootRef.child("Approved_Volunteer_Forms");

        // Find the event with matching eventId in Pending_Events
        Query query = pendingVolunteersRef.orderByChild("ic").equalTo(volunteerData.getIc());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Event found in Pending_Events, remove it
                    for (DataSnapshot eventSnapshot : dataSnapshot.getChildren()) {
                        eventSnapshot.getRef().removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    // Successfully removed from Pending_Events, now add to Approved_Events
                                    DatabaseReference newApprovedVolunteerRef = approvedVolunteersRef.push();
                                    newApprovedVolunteerRef.setValue(volunteerData);

                                    // Update the local list only after successful removal and addition
                                    volunteerAdapter.removeVolunteer(volunteerData);

                                    // Show a success message
                                    Toast.makeText(context, "Volunteer Approved", Toast.LENGTH_SHORT).show();
                                } else {
                                    // Failed to remove event from Pending_Events
                                    Toast.makeText(context, "Failed to approve volunteer", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                } else {
                    // Event not found in Pending_Events
                    Toast.makeText(context, "Volunteer not found in Pending Volunteer Form", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle the error
                Log.e("Firebase", "Query canceled.", databaseError.toException());
            }
        });
    }

    // Custom method to remove an event from the adapter
    private void removeEventFromDatabase(VolunteerData volunteerData) {
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference eventsRef = rootRef.child("Pending_Events");

        eventsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot uniqueKeySnapshot : dataSnapshot.getChildren()) {
                    DataSnapshot eventIdSnapshot = uniqueKeySnapshot.child("eventId");
                    String eventId = eventIdSnapshot.getValue(String.class);

                    if (eventId != null && eventId.equals(volunteerData.getEventId())) {
                        // Found the matching event, remove it
                        uniqueKeySnapshot.getRef().removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    // Event removed from the database successfully
                                    Toast.makeText(context, "Event Denied", Toast.LENGTH_SHORT).show();
                                } else {
                                    // Failed to remove event from the database
                                    Toast.makeText(context, "Failed to Deny Event", Toast.LENGTH_SHORT).show();
                                    Log.e("Firebase", "Failed to remove event from database.", task.getException());
                                }
                            }
                        });

                        // No need to continue iterating once the event is found and removed
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle the error
                Log.e("Firebase", "Query canceled.", databaseError.toException());
            }
        });
    }

    // Custom method to remove an volunteer from the adapter
    private void removeVolunteerFromDatabase(VolunteerData volunteerData) {
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference volunteersRef = rootRef.child("Pending_Volunteer_Forms");

        volunteersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot uniqueKeySnapshot : dataSnapshot.getChildren()) {
                    DataSnapshot icSnapshot = uniqueKeySnapshot.child("ic");
                    String ic = icSnapshot.getValue(String.class);

                    if (ic != null && ic.equals(volunteerData.getIc())) {
                        // Found the matching, remove it
                        uniqueKeySnapshot.getRef().removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    // Event removed from the database successfully
                                    Toast.makeText(context, "Application Denied", Toast.LENGTH_SHORT).show();
                                } else {
                                    // Failed to remove event from the database
                                    Toast.makeText(context, "Failed to Deny Event", Toast.LENGTH_SHORT).show();
                                    Log.e("Firebase", "Failed to remove event from database.", task.getException());
                                }
                            }
                        });

                        // No need to continue iterating once the event is found and removed
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle the error
                Log.e("Firebase", "Query canceled.", databaseError.toException());
            }
        });
    }

    private void removeApprovedFromDatabase(VolunteerData volunteerData) {
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference approvedsRef = rootRef.child("Approved_Events");

        approvedsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot uniqueKeySnapshot : dataSnapshot.getChildren()) {
                    DataSnapshot eventIdSnapshot = uniqueKeySnapshot.child("eventId");
                    String eventId = eventIdSnapshot.getValue(String.class);

                    if (eventId != null && eventId.equals(volunteerData.getEventId())) {
                        // Found the matching, remove it
                        uniqueKeySnapshot.getRef().removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    // ApprovedEvent removed from the database successfully
                                    Toast.makeText(context, "Application Denied", Toast.LENGTH_SHORT).show();
                                } else {
                                    // Failed to remove approved event from the database
                                    Toast.makeText(context, "Failed to Deny Event", Toast.LENGTH_SHORT).show();
                                    Log.e("Firebase", "Failed to remove event from database.", task.getException());
                                }
                            }
                        });

                        // No need to continue iterating once the event is found and removed
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle the error
                Log.e("Firebase", "Query canceled.", databaseError.toException());
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    // Helper method to update the EventAdapter with data from Firebase
    private void updateEventList(DataSnapshot dataSnapshot) {
        eventAdapter.clear1();

        // Create a list to hold the events in reverse order
        List<VolunteerData> reversedEvents = new ArrayList<>();

        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
            VolunteerData volunteerData = snapshot.getValue(VolunteerData.class);

            if (volunteerData != null && volunteerData.getevent1() != null) {
                reversedEvents.add(0, volunteerData); // Add at the beginning of the list
            }
        }

        // Add the reversed list to the adapter
        for (VolunteerData event : reversedEvents) {
            eventAdapter.add1(event);
        }
    }

    // Helper method to update the Volunteer Adapter with data from Firebase
    private void updateVolunteerList(DataSnapshot dataSnapshot) {
        volunteerAdapter.clear2();

        // Create a list to hold the events in reverse order
        List<VolunteerData> reversedVolunteer = new ArrayList<>();

        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
            VolunteerData volunteerData = snapshot.getValue(VolunteerData.class);

            if (volunteerData != null && volunteerData.getevent1() != null) {
                reversedVolunteer.add(0, volunteerData); // Add at the beginning of the list
            }
        }

        // Add the reversed list to the adapter
        for (VolunteerData volunteer : reversedVolunteer) {
            volunteerAdapter.add2(volunteer);
        }
    }

    // Helper method to update the EventAdapter with data from Firebase
    private void updateApprovedList(DataSnapshot dataSnapshot) {
        approvedAdapter.clear3();

        // Create a list to hold the events in reverse order
        List<VolunteerData> reversedEvents = new ArrayList<>();

        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
            VolunteerData volunteerData = snapshot.getValue(VolunteerData.class);

            if (volunteerData != null && volunteerData.getevent1() != null) {
                reversedEvents.add(0, volunteerData); // Add at the beginning of the list
            }
        }

        // Add the reversed list to the adapter
        for (VolunteerData approved : reversedEvents) {
            approvedAdapter.add3(approved);
        }
    }



}


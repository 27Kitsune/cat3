package com.example.cat3.ui.volunteeradmin;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import android.widget.ArrayAdapter;

import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class VolunteeradminFragment extends Fragment {
    private AdminEventAdapter eventAdapter;
    private AdminVolunteerAdapter volunteerAdapter;
    private AdminApprovedAdapter approvedAdapter;
    private DatabaseReference eventsRef;
    private DatabaseReference volunteerRef;
    private DatabaseReference approvedRef;
    private DatabaseReference usersRef;
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

        usersRef = FirebaseDatabase.getInstance().getReference("users");

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

                // Send notification
                sendNotification(volunteerData, "Congratulation!!! Your application for volunteer have been approved");

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
        Button btnVolunteer3 = dialogView.findViewById(R.id.btnVolunteer3);
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

        // Handle custom action button click
        btnVolunteer3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                approvedvolunteerlist(volunteerData);
                // Call a method to show text input dialog
                dialog.dismiss();
            }
        });

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




    private void approvedvolunteerlist(VolunteerData volunteerData) {
        // Inflate the custom layout
        View dialogView = LayoutInflater.from(context).inflate(R.layout.approved_volunteer_list, null);

        // Get references to the TextViews, ImageView, and Buttons in the custom layout
        TextView textTitle3 = dialogView.findViewById(R.id.textTitle);
        ListView listViewApprovedVolunteers = dialogView.findViewById(R.id.listViewApprovedVolunteers);
        Button btnDismiss = dialogView.findViewById(R.id.btnDismiss);

        // Set text and image in the TextViews and ImageView
        textTitle3.setText("Volunteer List");

        // Retrieve additional data from the Firebase database
        DatabaseReference approvedVolunteersRef2 = FirebaseDatabase.getInstance().getReference("Approved_Volunteer_Forms");

        // Query to get approved volunteers with the same IC as the selected volunteer
        Query query = approvedVolunteersRef2.orderByChild("event1").equalTo(volunteerData.getevent1());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> volunteerDetailsList = new ArrayList<>();

                // Iterate through the matching volunteers
                for (DataSnapshot volunteerSnapshot : dataSnapshot.getChildren()) {
                    // Assuming there are 'name' and 'number' fields in your data
                    String name = volunteerSnapshot.child("name2").getValue(String.class);
                    String number = volunteerSnapshot.child("number2").getValue(String.class);

                    // Add details to the list
                    volunteerDetailsList.add("Name: " + name + "\nNumber: " + number);
                }

                // Create an ArrayAdapter to display the details in the ListView
                ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, volunteerDetailsList);

                // Set the adapter for the ListView
                listViewApprovedVolunteers.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle the error
                Log.e("Firebase", "Query canceled.", databaseError.toException());
            }
        });

        // Build and show an AlertDialog with the custom layout
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogView);

        // Create the AlertDialog
        AlertDialog dialog = builder.create();

        btnDismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
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
        Log.d("Firebase", "approveVolunteer method called");
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference pendingVolunteersRef = rootRef.child("Pending_Volunteer_Forms");
        DatabaseReference approvedVolunteersRef = rootRef.child("Approved_Volunteer_Forms");

        // Find the volunteer with matching IC in Pending_Volunteer_Forms
        Query query = pendingVolunteersRef.orderByChild("ic").equalTo(volunteerData.getIc());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Volunteer found in Pending_Volunteer_Forms, remove it
                    for (DataSnapshot volunteerSnapshot : dataSnapshot.getChildren()) {
                        volunteerSnapshot.getRef().removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    // Successfully removed from Pending_Volunteer_Forms, now add to Approved_Volunteer_Forms
                                    DatabaseReference newApprovedVolunteerRef = approvedVolunteersRef.push();

                                    // Get FCM token from the "users" path
                                    DatabaseReference userRef = rootRef.child("users").child(volunteerData.getUserUid());
                                    userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                                            if (userSnapshot.exists()) {
                                                String fcmToken = userSnapshot.child("fcmToken").getValue(String.class);
                                                // Log the user UID for debugging
                                                Log.d("Firebase", "User UID in pending: " + volunteerData.getUserUid());

                                                // Set FCM token for the approved volunteer
                                                volunteerData.setFcmToken(fcmToken);

                                                // Store the approved volunteer in Approved_Volunteer_Forms
                                                newApprovedVolunteerRef.setValue(volunteerData);

                                                // Update the local list only after successful removal and addition
                                                volunteerAdapter.removeVolunteer(volunteerData);

                                                // Show a success message
                                                Toast.makeText(context, "Volunteer Approved", Toast.LENGTH_SHORT).show();
                                            } else {
                                                // User not found in "users" path
                                                Toast.makeText(context, "User not found in the database", Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            // Handle the error
                                            Log.e("Firebase", "Query canceled.", error.toException());
                                        }
                                    });
                                } else {
                                    // Failed to remove volunteer from Pending_Volunteer_Forms
                                    Toast.makeText(context, "Failed to approve volunteer", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                } else {
                    // Volunteer not found in Pending_Volunteer_Forms
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
                                    Toast.makeText(context, "Application Removed", Toast.LENGTH_SHORT).show();
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




    public void sendNotification(VolunteerData volunteerData, String message) {
        Log.d("Firebase", "sendNotification method called");

        // Get the event name
        String eventName = volunteerData.getevent1();

        // Create a query to find the phone number for the given event name in the approved_event path
        DatabaseReference eventRef = FirebaseDatabase.getInstance().getReference().child("Approved_Events");
        Query eventQuery = eventRef.orderByChild("event1").equalTo(volunteerData.getevent1());

        eventQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot eventSnapshot : dataSnapshot.getChildren()) {
                        // Found the corresponding event in the approved_event path
                        String phoneNumber = eventSnapshot.child("number1").getValue(String.class);

                        if (phoneNumber != null) {
                            // Now, you have the phoneNumber, proceed with the notification

                            DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("users").child(volunteerData.getUserUid());

                            usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        String fcmToken = dataSnapshot.child("fcmToken").getValue(String.class);
                                        if (fcmToken != null) {
                                            try {
                                                JSONObject jsonObject = new JSONObject();

                                                JSONObject notificationObj = new JSONObject();
                                                notificationObj.put("title", "Future Impact");

                                                // Customize the message with a WhatsApp link
                                                String whatsappLink = "https://wa.me/" + phoneNumber;
                                                String customMessage = message + "\n\nFor further details, contact Volunteer Leader on WhatsApp: " + phoneNumber;

                                                notificationObj.put("body", customMessage);

                                                JSONObject dataObj = new JSONObject();
                                                dataObj.put("userId", volunteerData.getUserUid()); // or any other relevant data

                                                jsonObject.put("notification", notificationObj);
                                                jsonObject.put("data", dataObj);
                                                jsonObject.put("to", fcmToken);

                                                callApi(jsonObject);

                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        } else {
                                            // Handle the case where FCM token is not available
                                            Toast.makeText(context, "FCM token not found for the user", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        // Handle the case where user data is not found
                                        Toast.makeText(context, "User data not found in the database", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    // Handle the error
                                    Log.e("Firebase", "Query canceled.", databaseError.toException());
                                }
                            });
                        } else {
                            // Handle the case where phone number is not available
                            Toast.makeText(context, "Phone number not found for the event", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    // Handle the case where event data is not found
                    Toast.makeText(context, "Event data not found in the database", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle the error
                Log.e("Firebase", "Query canceled.", databaseError.toException());
            }
        });
    }


    void callApi(JSONObject jsonObject) {
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        String url = "https://fcm.googleapis.com/fcm/send";
        RequestBody body = RequestBody.create(JSON, jsonObject.toString());
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .header("Authorization", "Bearer AAAAU46GfHs:APA91bFcsD3HnpxWBPIrZbDSE-n6RishsBzPVcaV2YF6fpgKQQD9VhC8Af2xZLJDC0qSMuBMDr_I3ogcLusE_Swu837Wu38b4Xgl52oP4jmx9HJD8EsPXxBgb2hRF7IE7cwpHJJqr5gO") // Replace with your FCM server key
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                // Handle failure
                Log.e("Notification", "Failed to send notification", e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                // Handle response
            }
        });
    }
}


package com.example.affinity_projectfinal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ConnectionsActivity extends AppCompatActivity {

    // Firebase
    private DatabaseReference databaseReference;

    // UI elements
    private EditText searchBar;
    private ListView listFriendRequests, listFriendList;

    // ArrayLists to store data
    private ArrayList<String> friendRequestsList;
    private ArrayList<String> friendList;

    // ArrayAdapters for ListView
    private ArrayAdapter<String> friendRequestsAdapter;
    private ArrayAdapter<String> friendListAdapter;

    private Button acceptButton;
    private Button denyButton;

    // Current user's ID
    private String currentUserId; // Add this line

   @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connections);
        currentUserId = getCurrentUserId();
        // Firebase database reference
        databaseReference = FirebaseDatabase.getInstance().getReference();

        // Initialize UI elements
        searchBar = findViewById(R.id.searchBar);

        listFriendRequests = findViewById(R.id.listFriendRequests);
        listFriendList = findViewById(R.id.listFriendList);


        // Initialize ArrayLists
        friendRequestsList = new ArrayList<>();
        friendList = new ArrayList<>();

        // Initialize ArrayAdapters
        friendRequestsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, friendRequestsList);
        friendListAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, friendList);

        // Set adapters to ListViews
        listFriendRequests.setAdapter(friendRequestsAdapter);
        //listFriendList.setAdapter(friendListAdapter);

       ListView friendsListView = findViewById(R.id.friendsListView);
       friendsListView.setAdapter(friendListAdapter);

        // Load data from Firebase
        loadFriendRequests(); // Call the function here
       loadFriendsList();
       // Attach click listener to the search button
       Button searchButton = findViewById(R.id.searchButton);
       searchButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               onSearchButtonClick(v);
           }
       });
       Button acceptButton = findViewById(R.id.acceptButton);
       Button denyButton = findViewById(R.id.denyButton);

       acceptButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               String requestId = ((TextView) findViewById(R.id.requestIdTextView)).getText().toString();
               acceptFriendRequest(requestId);
           }
       });

       denyButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               String requestId = ((TextView) findViewById(R.id.requestIdTextView)).getText().toString();
               denyFriendRequest(requestId);
           }
       });

       // Setup your navigation as already done
       BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
       bottomNavigationView.setSelectedItemId(R.id.bottom_settings);

       bottomNavigationView.setOnItemSelectedListener(item -> {
           int itemId = item.getItemId();
           if (itemId == R.id.bottom_home) {
               Intent intent = new Intent(getApplicationContext(), CourseHomeActivity.class);
               intent.putExtra("username", currentUserId); // Pass the username to ConnectionsActivity
               startActivity(intent);
               overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);
               finish();
               return true;
           } else if (itemId == R.id.bottom_search) {
               startActivity(new Intent(getApplicationContext(), ChatActivity.class));
               overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);
               finish();
               return true;
           } else if (itemId == R.id.bottom_settings) {
               return true;
           }
           else if (itemId == R.id.bottom_profile) {
               Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
               intent.putExtra("username", currentUserId); // Pass the username to ConnectionsActivity
               startActivity(intent);
               overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);
               finish();
               return true;
           }
           return false;
       });
   }



    private void loadFriendsList() {
        Log.d("ConnectionsActivity", "Loading friends list...");
        DatabaseReference friendsRef = databaseReference.child("friendRequests");
        friendsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Clear the friends list before adding new data
                friendList.clear();
                Log.d("ConnectionsActivity", "Cleared the friends list for new data.");

                for (DataSnapshot requestSnapshot : dataSnapshot.getChildren()) {
                    String user1 = requestSnapshot.child("user1").getValue(String.class);
                    String user2 = requestSnapshot.child("user2").getValue(String.class);
                    String status = requestSnapshot.child("status").getValue(String.class);
                    Log.d("ConnectionsActivity", "Checking request between " + user1 + " and " + user2 + " with status " + status);

                    // Check if the current user is involved in the friend request and the status is accepted
                    if ((user1.equals(currentUserId) || user2.equals(currentUserId)) && "accepted".equals(status)) {
                        // Get the friend's username
                        String friendUsername = user1.equals(currentUserId) ? user2 : user1;
                        Log.d("ConnectionsActivity", "Accepted friend found: " + friendUsername);

                        // Retrieve the friend's attachmentStyle and advancedAttachmentStyle from the database
                        DatabaseReference userRef = databaseReference.child("users").child(friendUsername);
                        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    // Get the attachmentStyle and advancedAttachmentStyle
                                    String attachmentStyle = dataSnapshot.child("attachmentStyle").getValue(String.class);
                                    String advancedAttachmentStyle = dataSnapshot.child("advancedAttachmentStyle").getValue(String.class);
                                    Log.d("ConnectionsActivity", "Fetched styles for " + friendUsername + ": " + attachmentStyle + ", " + advancedAttachmentStyle);

                                    // Add the friend's username and styles to the list
                                    String friendInfo = "Username: " + friendUsername;
                                    if (attachmentStyle != null) {
                                        friendInfo += "\nAttachment Style: " + attachmentStyle;
                                    }
                                    if (advancedAttachmentStyle != null) {
                                        friendInfo += "\nAdvanced Attachment Style: " + advancedAttachmentStyle;
                                    }
                                    friendList.add(friendInfo);
                                    Log.d("ConnectionsActivity", "Added friend info to list: " + friendInfo);

                                    // Update the adapter
                                    friendListAdapter.notifyDataSetChanged();
                                    Log.d("ConnectionsActivity", "Adapter notified of data change.");
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Log.e("ConnectionsActivity", "Error fetching user details: " + databaseError.getMessage());
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("ConnectionsActivity", "Failed to load friend requests: " + databaseError.getMessage());
            }
        });
    }
    public void onSearchButtonClick(View view) {
        Log.d("ConnectionsActivity", "Search button clicked");
        String searchText = searchBar.getText().toString().trim();
        if (!searchText.isEmpty()) {
            searchUser(searchText);
        } else {
            Toast.makeText(this, "Please enter a username", Toast.LENGTH_SHORT).show();
        }
    }

    private void searchUser(String username) {
        Log.d("ConnectionsActivity", "Searching for user: " + username);
        databaseReference.child("users").child(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("ConnectionsActivity", "onDataChange triggered");
                if (dataSnapshot.exists()) {
                    // User exists, perform action (e.g., send friend request)
                    String searchedUserId = dataSnapshot.getKey(); // Get the searched user's ID
                    Log.d("ConnectionsActivity", "User found. ID: " + searchedUserId);

                    // Store the friend request in the database under the searched user's node
                    DatabaseReference friendRequestsRef = databaseReference.child("friendRequests").push(); // Generate a unique request ID
                    String requestId = friendRequestsRef.getKey(); // Get the generated request ID
                    Log.d("ConnectionsActivity", "Generated request ID: " + requestId);

                    // Create a HashMap to store the users and their status
                    Map<String, Object> requestInfo = new HashMap<>();
                    requestInfo.put("user1", currentUserId); // Current user sending the request
                    requestInfo.put("user2", searchedUserId); // User being sent the request
                    requestInfo.put("status", "pending"); // Initial status is pending
                    Log.d("ConnectionsActivity", "Friend request info: " + requestInfo);

                    // Set the request info in the database under the request ID
                    friendRequestsRef.setValue(requestInfo);

                    Toast.makeText(ConnectionsActivity.this, "Friend request sent to: " + username, Toast.LENGTH_SHORT).show();
                } else {
                    // User does not exist
                    Toast.makeText(ConnectionsActivity.this, "User not found: " + username, Toast.LENGTH_SHORT).show();
                    Log.d("ConnectionsActivity", "User not found: " + username);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Error handling
                Log.d("ConnectionsActivity", "onCancelled triggered");
                Toast.makeText(ConnectionsActivity.this, "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Method to get the current user's ID (you need to implement this)
    private String getCurrentUserId() {
        // Retrieve the username passed from the previous activity
        return getIntent().getStringExtra("username");
    }

    private void loadFriendRequests() {
        DatabaseReference friendRequestsRef = databaseReference.child("friendRequests");
        friendRequestsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Check if there are any friend requests
                if (dataSnapshot.exists()) {
                    DataSnapshot latestRequest = null; // Track the latest request if only showing one

                    for (DataSnapshot requestSnapshot : dataSnapshot.getChildren()) {
                        String user1 = requestSnapshot.child("user1").getValue(String.class);
                        String user2 = requestSnapshot.child("user2").getValue(String.class);
                        String status = requestSnapshot.child("status").getValue(String.class);

                        // Log the details
                        Log.d("ConnectionsActivity", "Friend request: user1=" + user1 + ", user2=" + user2 + ", status=" + status);

                        if (user2 != null && user2.equals(currentUserId) && "pending".equals(status)) {
                            latestRequest = requestSnapshot; // Store the latest pending request
                        }
                    }

                    // If there is at least one pending request, display it
                    if (latestRequest != null) {
                        String user1 = latestRequest.child("user1").getValue(String.class);
                        String requestId = latestRequest.getKey(); // This is the requestId

                        // Show the layout for individual friend request item
                        findViewById(R.id.friendRequestItemLayout).setVisibility(View.VISIBLE);

                        // Update the TextViews
                        TextView requestFromTextView = findViewById(R.id.requestFromTextView);
                        requestFromTextView.setText("From: " + user1);

                        TextView requestIdTextView = findViewById(R.id.requestIdTextView);
                        requestIdTextView.setText(requestId); // Set the hidden requestId

                        // Set visibility of no requests message
                        findViewById(R.id.noFriendRequestsMessage).setVisibility(View.GONE);
                    } else {
                        // No pending requests, hide the request item layout
                        findViewById(R.id.friendRequestItemLayout).setVisibility(View.GONE);
                        findViewById(R.id.noFriendRequestsMessage).setVisibility(View.VISIBLE);
                    }
                } else {
                    // No data at all, display the no requests message
                    findViewById(R.id.noFriendRequestsMessage).setVisibility(View.VISIBLE);
                    findViewById(R.id.friendRequestItemLayout).setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Error handling
                Log.d("ConnectionsActivity", "onCancelled triggered");
                Toast.makeText(ConnectionsActivity.this, "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void acceptFriendRequest(String requestId) {
        DatabaseReference requestRef = databaseReference.child("friendRequests").child(requestId);
        requestRef.updateChildren(Collections.singletonMap("status", "accepted"))
                .addOnSuccessListener(aVoid -> Toast.makeText(ConnectionsActivity.this, "Friend request accepted", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(ConnectionsActivity.this, "Failed to accept friend request", Toast.LENGTH_SHORT).show());
    }

    private void denyFriendRequest(String requestId) {
        DatabaseReference requestRef = databaseReference.child("friendRequests").child(requestId);
        requestRef.removeValue()
                .addOnSuccessListener(aVoid -> Toast.makeText(ConnectionsActivity.this, "Friend request denied", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(ConnectionsActivity.this, "Failed to deny friend request", Toast.LENGTH_SHORT).show());
    }
}
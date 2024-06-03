package com.example.affinity_projectfinal;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class CourseHomeActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private TextView coursesTextView; // Declare the TextView at the class level

    String username;
    Button backButton; // Add this line

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_home);
        // Retrieve the username passed from ProfileActivity
        username = getIntent().getStringExtra("username");

        db = FirebaseFirestore.getInstance();  // Initialize Firestore
        coursesTextView = findViewById(R.id.courseTextView);

        backButton = findViewById(R.id.backButton); // Initialize back button

        loadAttachmentStyle();  // Load the attachment style from Realtime Database

        // Setup your navigation as already done
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setSelectedItemId(R.id.bottom_home);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.bottom_home) {
                return true;
            } else if (itemId == R.id.bottom_search) {
                startActivity(new Intent(getApplicationContext(), ChatActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);
                finish();
                return true;
            } else if (itemId == R.id.bottom_settings) {
                Intent intent = new Intent(getApplicationContext(), ConnectionsActivity.class);
                intent.putExtra("username", username); // Pass the username to ConnectionsActivity
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);
                finish();
                return true;
            }
            else if (itemId == R.id.bottom_profile) {
                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);
                finish();
                return true;
            }
            return false;
        });
    }


    private void loadAttachmentStyle() {
        if (username != null && !username.isEmpty()) {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users").child(username);
            databaseReference.child("attachmentStyle").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String attachmentStyle = dataSnapshot.getValue(String.class);
                        Log.d("AttachmentStyle", "Attachment Style for " + username + " is: " + attachmentStyle);

                        // Load courses that match this attachment style
                        loadCourses(attachmentStyle);

                        // Show the back button
                        backButton.setVisibility(View.VISIBLE);
                    } else {
                        Log.d("AttachmentStyle", "No attachment style found for " + username);

                        // Show all courses if no attachment style found
                        loadCourses(null);

                        // Show the back button
                        backButton.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.d("Database Error", "Failed to read attachment style: " + databaseError.getMessage());
                }
            });
        } else {
            Log.d("AttachmentStyle", "Username is null or empty");
        }
    }

    private void loadCourses(String attachmentStyle) {
        // Check if attachmentStyle is null to determine whether to load all courses or just the user's attachment style course
        if (attachmentStyle != null) {
            db.collection("courses")
                    .whereEqualTo("title", attachmentStyle)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            StringBuilder coursesInfo = new StringBuilder();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String courseName = document.getId();
                                String title = document.getString("title");

                                // Append each course detail to the StringBuilder
                                coursesInfo.append("Course Name: ").append(courseName)
                                        .append("\nTitle: ").append(title).append("\n\n");

                                // Assume you want to display chapters as well
                                for (int i = 1; i <= 6; i++) {
                                    String chapter = document.getString("chapter" + i);
                                    String description = document.getString("description" + i);

                                    coursesInfo.append("Chapter ").append(i).append(": ").append(chapter)
                                            .append("\nDescription: ").append(description).append("\n\n");
                                }
                            }
                            // Set the text to TextView
                            coursesTextView.setText(coursesInfo.toString());

                            // Show the ScrollView containing course content
                            findViewById(R.id.scrollView).setVisibility(View.VISIBLE);
                        } else {
                            Log.d("Firestore Error", "Error getting documents: ", task.getException());
                        }
                    });
        } else {
            // Load all courses if no attachment style found
            db.collection("courses")
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            StringBuilder coursesInfo = new StringBuilder();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String courseName = document.getId();
                                String title = document.getString("title");

                                // Append each course detail to the StringBuilder
                                coursesInfo.append("Course Name: ").append(courseName)
                                        .append("\nTitle: ").append(title).append("\n\n");

                                // Assume you want to display chapters as well
                                for (int i = 1; i <= 6; i++) {
                                    String chapter = document.getString("chapter" + i);
                                    String description = document.getString("description" + i);

                                    coursesInfo.append("Chapter ").append(i).append(": ").append(chapter)
                                            .append("\nDescription: ").append(description).append("\n\n");
                                }
                            }
                            // Set the text to TextView
                            coursesTextView.setText(coursesInfo.toString());

                            // Show the ScrollView containing course content
                            findViewById(R.id.scrollView).setVisibility(View.VISIBLE);
                        } else {
                            Log.d("Firestore Error", "Error getting documents: ", task.getException());
                        }
                    });
        }
    }
}

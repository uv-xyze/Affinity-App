package com.example.affinity_projectfinal;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.appcompat.app.AppCompatActivity;

public class AttachmentAnalysisTest extends AppCompatActivity implements View.OnClickListener{


    TextView totalQuestionsTextView;
    TextView questionTextView;
    Button ansA, ansB, ansC, ansD, ansE;
    Button submitBtn;

    int[] scores = new int[10]; // Array to store scores for each question
    int currentQuestionIndex = 0;
    String[] userAnswers = new String[10]; // Array to store user's answers

    String[] attachmentStyles = {"Secure Attachment", "Anxious Attachment", "Avoidant Attachment", "Disorganized Attachment"};

    String[][] questions = {
            {"I feel comfortable being close to others and expressing my feelings."},
            {"I worry that my partner will not love me or will leave me."},
            {"I prefer to be independent and self-sufficient."},
            {"I sometimes feel confused about my feelings towards my partner."},
            {"I tend to be critical of myself and my relationships."},
            {"I am comfortable expressing both positive and negative emotions to my partner."},
            {"I enjoy spending time with my partner, but I also need my own space."},
            {"I am sometimes jealous or suspicious of my partner's relationships with others."},
            {"I am open to intimacy and commitment in relationships."},
            {"I sometimes feel hesitant to trust others."}
    };

    String[][] choices = {
            {"Strongly Agree", "Agree", "Neutral", "Disagree", "Strongly Disagree"},
            {"Strongly Agree", "Agree", "Neutral", "Disagree", "Strongly Disagree"},
            {"Strongly Agree", "Agree", "Neutral", "Disagree", "Strongly Disagree"},
            {"Strongly Agree", "Agree", "Neutral", "Disagree", "Strongly Disagree"},
            {"Strongly Agree", "Agree", "Neutral", "Disagree", "Strongly Disagree"},
            {"Strongly Agree", "Agree", "Neutral", "Disagree", "Strongly Disagree"},
            {"Strongly Agree", "Agree", "Neutral", "Disagree", "Strongly Disagree"},
            {"Strongly Agree", "Agree", "Neutral", "Disagree", "Strongly Disagree"},
            {"Strongly Agree", "Agree", "Neutral", "Disagree", "Strongly Disagree"},
            {"Strongly Agree", "Agree", "Neutral", "Disagree", "Strongly Disagree"}
    };

    int[][] scoresMapping = {
            {5, 4, 3, 2, 1},
            {5, 4, 3, 2, 1},
            {5, 4, 3, 2, 1},
            {5, 4, 3, 2, 1},
            {5, 4, 3, 2, 1},
            {5, 4, 3, 2, 1},
            {5, 4, 3, 2, 1},
            {5, 4, 3, 2, 1},
            {5, 4, 3, 2, 1},
            {5, 4, 3, 2, 1}
    };
    String username;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attachment_analysis_test);
        // Retrieve the username passed from ProfileActivity
        username = getIntent().getStringExtra("username");

        totalQuestionsTextView = findViewById(R.id.total_question);
        questionTextView = findViewById(R.id.question);
        ansA = findViewById(R.id.ans_A);
        ansB = findViewById(R.id.ans_B);
        ansC = findViewById(R.id.ans_C);
        ansD = findViewById(R.id.ans_D);
        ansE = findViewById(R.id.ans_E);
        submitBtn = findViewById(R.id.submit_btn);

        ansA.setOnClickListener(this);
        ansB.setOnClickListener(this);
        ansC.setOnClickListener(this);
        ansD.setOnClickListener(this);
        ansE.setOnClickListener(this);
        submitBtn.setOnClickListener(this);

        loadNewQuestion();

    }

    @Override
    public void onClick(View view) {
        Button clickedButton = (Button) view;
        if (clickedButton.getId() == R.id.submit_btn) {
            if (!userAnswers[currentQuestionIndex].isEmpty()) {
                scores[currentQuestionIndex] = scoresMapping[currentQuestionIndex][getIndexForAnswer(userAnswers[currentQuestionIndex])];
            }
            currentQuestionIndex++;
            if (currentQuestionIndex < questions.length) {
                loadNewQuestion();
            } else {
                finishQuiz();
            }
        } else {
            userAnswers[currentQuestionIndex] = clickedButton.getText().toString();
            // Highlight selected answer
            ansA.setBackgroundColor(clickedButton.getId() == R.id.ans_A ? Color.MAGENTA : Color.WHITE);
            ansB.setBackgroundColor(clickedButton.getId() == R.id.ans_B ? Color.MAGENTA : Color.WHITE);
            ansC.setBackgroundColor(clickedButton.getId() == R.id.ans_C ? Color.MAGENTA : Color.WHITE);
            ansD.setBackgroundColor(clickedButton.getId() == R.id.ans_D ? Color.MAGENTA : Color.WHITE);
            ansE.setBackgroundColor(clickedButton.getId() == R.id.ans_E ? Color.MAGENTA : Color.WHITE);
        }
    }

    void loadNewQuestion() {
        totalQuestionsTextView.setText("Question " + (currentQuestionIndex + 1) + " out of " + questions.length);
        questionTextView.setText(questions[currentQuestionIndex][0]);
        ansA.setText(choices[currentQuestionIndex][0]);
        ansB.setText(choices[currentQuestionIndex][1]);
        ansC.setText(choices[currentQuestionIndex][2]);
        ansD.setText(choices[currentQuestionIndex][3]);
        ansE.setText(choices[currentQuestionIndex][4]);

        // Reset answer highlight
        ansA.setBackgroundColor(Color.WHITE);
        ansB.setBackgroundColor(Color.WHITE);
        ansC.setBackgroundColor(Color.WHITE);
        ansD.setBackgroundColor(Color.WHITE);
        ansE.setBackgroundColor(Color.WHITE);
    }
    public void storeAttachmentStyle(String attachmentStyle) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");

        // Check if username is not null
        if (username != null && !username.isEmpty()) {
            // Updating the attachmentStyle for the user
            reference.child(username).child("attachmentStyle").setValue(attachmentStyle)
                    .addOnSuccessListener(aVoid -> {
                        // Handle success
                        Log.d("Database", "Attachment style updated successfully for user: " + username);
                    })
                    .addOnFailureListener(e -> {
                        // Handle failure
                        Log.d("Database", "Failed to update attachment style: ", e);
                    });
        } else {
            Log.d("Database", "Username is null or empty.");
        }
    }
    void finishQuiz() {
        int secureAttachmentScore = scores[0] + scores[2] + scores[5] + scores[6] + scores[8];
        int anxiousAttachmentScore = scores[1] + scores[3] + scores[4] + scores[7];
        int avoidantAttachmentScore = scores[6] + scores[9];
        int disorganizedAttachmentScore = scores[1] + scores[3] + scores[4] + scores[7];

        int[] attachmentScores = {secureAttachmentScore, anxiousAttachmentScore, avoidantAttachmentScore, disorganizedAttachmentScore};
        int maxScoreIndex = 0;
        int maxScore = attachmentScores[0];
        for (int i = 1; i < attachmentScores.length; i++) {
            if (attachmentScores[i] > maxScore) {
                maxScore = attachmentScores[i];
                maxScoreIndex = i;
            }
        }

        AlertDialog.Builder attachmentStyleDialog = new AlertDialog.Builder(this);
        attachmentStyleDialog.setTitle("Attachment Style");
        storeAttachmentStyle(attachmentStyles[maxScoreIndex]);
        attachmentStyleDialog.setMessage("Your dominant attachment style is: " + attachmentStyles[maxScoreIndex]);
        attachmentStyleDialog.setPositiveButton("Take the extended AA quiz", (dialogInterface, i) -> startCoursePageActivity());
        attachmentStyleDialog.setNeutralButton("Home", (dialogInterface, i) -> {
            String username = getIntent().getStringExtra("username");
            Intent intent = new Intent(AttachmentAnalysisTest.this, CourseHomeActivity.class);
            intent.putExtra("username", username);
            startActivity(intent);
            finish();
        });
        attachmentStyleDialog.setCancelable(false);
        attachmentStyleDialog.show();
    }


    void startCoursePageActivity() {
        String username = getIntent().getStringExtra("username");
        Intent intent = new Intent(AttachmentAnalysisTest.this, RelationshipAAQ2.class);
        intent.putExtra("username", username);

        startActivity(intent);
    }



    // Helper method to get index for answer
    private int getIndexForAnswer(String answer) {
        switch (answer) {
            case "Strongly Agree":
                return 0;
            case "Agree":
                return 1;
            case "Neutral":
                return 2;
            case "Disagree":
                return 3;
            case "Strongly Disagree":
                return 4;
            default:
                return -1;
        }
    }
}

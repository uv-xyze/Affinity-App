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


public class RelationshipAAQ2 extends AppCompatActivity implements View.OnClickListener {

    TextView totalQuestionsTextView;
    TextView questionTextView;
    Button ansA, ansB, ansC, ansD, ansE;
    Button submitBtn;

    int[] scores = new int[20]; // Array to store scores for each question
    int currentQuestionIndex = 0;
    String[] userAnswers = new String[20]; // Array to store user's answers

    String[] attachmentStyles = {"Secure Attachment", "Anxious Attachment", "Avoidant Attachment", "Fearful-Avoidant Attachment"};

    String[][] questions = {
            // Secure Attachment Style
            {"I find it easy to get close to others and am comfortable depending on them."},
            {"I am comfortable expressing my feelings and needs in my relationships."},
            {"I believe that most people are trustworthy and have good intentions."},
            {"I am confident that my partner will be there for me when I need them."},
            {"I feel comfortable being alone, but also enjoy spending time with others."},

            // Anxious-Preoccupied Attachment Style
            {"I worry that my partner doesn't really love me or won't stay with me."},
            {"I need a lot of reassurance and affection from my partner to feel secure in the relationship."},
            {"I often feel jealous or possessive of my partner."},
            {"I tend to overthink and obsess about my relationships."},
            {"I am afraid of being alone and often feel lonely, even when in a relationship."},

            // Dismissive-Avoidant Attachment Style
            {"I prefer to keep my distance in my relationships and value my independence."},
            {"I am uncomfortable expressing my feelings and needs in my relationships."},
            {"I tend to downplay the importance of romantic relationships in my life."},
            {"I find it difficult to trust others or depend on them for support."},
            {"I often feel that my partner is too needy or clingy."},

            // Fearful-Avoidant Attachment Style
            {"I am often torn between the desire to be close to others and the fear of getting hurt or rejected."},
            {"I have had some traumatic experiences in my past relationships that make it hard for me to trust others."},
            {"I tend to push people away or sabotage my relationships when I start to feel too vulnerable or close."},
            {"I struggle with feelings of inadequacy and often worry that I am not worthy of love."},
            {"I have a hard time feeling safe or secure in my relationships, even when my partner is loving and supportive."}
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
            {"Strongly Agree", "Agree", "Neutral", "Disagree", "Strongly Disagree"},

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
            {7, 6, 5, 4, 3},
            {7, 6, 5, 4, 3},
            {7, 6, 5, 4, 3},
            {7, 6, 5, 4, 3},
            {7, 6, 5, 4, 3},

            {7, 6, 5, 4, 3},
            {7, 6, 5, 4, 3},
            {7, 6, 5, 4, 3},
            {7, 6, 5, 4, 3},
            {7, 6, 5, 4, 3},

            {7, 6, 5, 4, 3},
            {7, 6, 5, 4, 3},
            {7, 6, 5, 4, 3},
            {7, 6, 5, 4, 3},
            {7, 6, 5, 4, 3},

            {7, 6, 5, 4, 3},
            {7, 6, 5, 4, 3},
            {7, 6, 5, 4, 3},
            {7, 6, 5, 4, 3},
            {7, 6, 5, 4, 3}
    };
    String username;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_relationship_aaq2);

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
    public void storeAdvancedAttachmentStyle(String advancedAttachmentStyle) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");

        // Check if username is not null
        if (username != null && !username.isEmpty()) {
            // Updating the attachmentStyle for the user
            reference.child(username).child("advancedAttachmentStyle").setValue(advancedAttachmentStyle)
                    .addOnSuccessListener(aVoid -> {
                        // Handle success
                        Log.d("Database", "Advanced Attachment style updated successfully for user: " + username);
                    })
                    .addOnFailureListener(e -> {
                        // Handle failure
                        Log.d("Database", "Failed to update attachment style: ", e);
                    });
        } else {
            Log.d("Database", "Username is null or empty.");
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

    void finishQuiz() {
        int secureAttachmentScore = scores[0] + scores[1] + scores[2] + scores[3] + scores[4];
        int anxiousAttachmentScore = scores[5] + scores[6] + scores[7] + scores[8] + scores[9];
        int avoidantAttachmentScore = scores[10] + scores[11] + scores[12] + scores[13] + scores[14];
        int disorganizedAttachmentScore = scores[15] + scores[16] + scores[17] + scores[18] + scores[19];

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
        storeAdvancedAttachmentStyle(attachmentStyles[maxScoreIndex]);
        attachmentStyleDialog.setMessage("Your dominant attachment style is: " + attachmentStyles[maxScoreIndex]);
        attachmentStyleDialog.setPositiveButton("Courses", (dialogInterface, i) -> startCoursePageActivity());
        attachmentStyleDialog.setNegativeButton("Restart Quiz", (dialogInterface, i) -> restartQuiz());
        attachmentStyleDialog.setCancelable(false);
        attachmentStyleDialog.show();
    }

    void startCoursePageActivity() {
        Intent intent = new Intent(RelationshipAAQ2.this, CourseHomeActivity.class);
        startActivity(intent);
    }


    void restartQuiz() {
        for (int i = 0; i < scores.length; i++) {
            scores[i] = 0;
            userAnswers[i] = "";
        }
        currentQuestionIndex = 0;
        loadNewQuestion();
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

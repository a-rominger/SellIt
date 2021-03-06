package com.sellit.testdrawer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

//
public class GoalsActivity extends AppCompatActivity {

    Button continueBtn;

    EditText goalName;
    EditText description;
    EditText amount;

    String UID;

    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goals);

        continueBtn = (Button) findViewById(R.id.continueBtn);

        goalName = (EditText) findViewById(R.id.goalName);
        description = (EditText) findViewById(R.id.description);
        amount = (EditText) findViewById(R.id.amount);

        Bundle b = getIntent().getExtras();

        UID = b.getString("UID");

        mDatabase = FirebaseDatabase.getInstance().getReference();

        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GoalInfo info = new GoalInfo(UID, goalName.getText().toString(), amount.getText().toString(),
                        description.getText().toString());

                mDatabase.child("studentInfo").child(UID).child("Goal").setValue(info);
                toStudent();
            }
        });
    }

    private void toStudent() {
        startActivity(new Intent(GoalsActivity.this, StudentHomeActivity.class));
    }
}
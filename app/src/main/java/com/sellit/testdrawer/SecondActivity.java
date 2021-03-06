package com.sellit.testdrawer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;


public class SecondActivity extends AppCompatActivity implements View.OnClickListener {

    Button saveButton;
    Spinner stateSpinner;
    EditText firstName;
    EditText lastName;
    EditText email;
    EditText city;
    TextView Username;

    String TAG = SecondActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.second_layout);

        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();

        String[] states = getResources().getStringArray(R.array.states);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, states);
        stateSpinner = (Spinner) findViewById(R.id.spinnerSettings);
        stateSpinner.setAdapter(adapter);

        firstName = (EditText) findViewById(R.id.firstNameInput);
        lastName = (EditText) findViewById(R.id.lastNameInput);
        email = (EditText) findViewById(R.id.emailInput);
        city = (EditText) findViewById(R.id.cityInput);
        saveButton = (Button) findViewById(R.id.saveButton);
        Username = (TextView) findViewById(R.id.userNameInputSettings);

        saveButton.setOnClickListener(this);

        FirebaseUser FBUser = FirebaseAuth.getInstance().getCurrentUser();
        email.setText(FBUser.getEmail());
        city.setText(mRef.child("userInfo").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("city").toString());
        stateSpinner.setPrompt(mRef.child("userInfo").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("state").toString());

        String UUID = FBUser.getUid();
        ValueEventListener listener = new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                UserInfo u = dataSnapshot.getValue(UserInfo.class);
                firstName.setText(u.fullName);
                city.setText(u.city);
                Username.setText(u.userName);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mRef.child("userInfo").child(UUID).addListenerForSingleValueEvent(listener);
    }

    @Override
    public void onClick(View v)
    {
        FirebaseAuth.getInstance().getCurrentUser().updateEmail(email.getText().toString());
        UserInfo newData = new UserInfo();
        newData.city = city.getText().toString();
        newData.fullName = firstName.getText().toString();
        newData.uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        newData.state = stateSpinner.getSelectedItem().toString();
        newData.email = email.getText().toString();
        newData.TAG = "UserInfo";
        newData.userName = Username.getText().toString();

        DatabaseReference dRef = FirebaseDatabase.getInstance().getReference();

        Map<String, Object> childUpdates = new HashMap<>();
        Map<String, Object> newDataMap = newData.toMap();
        childUpdates.put("/userInfo/"+newData.uid, newDataMap);
        dRef.updateChildren(childUpdates);
        Toast.makeText(this, "Saved Data", Toast.LENGTH_LONG).show();
    }
}

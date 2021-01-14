package com.example.instagram.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.instagram.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Create_Acc extends AppCompatActivity {

    private EditText firstName;
    private EditText lastName;
    private EditText email;
    private EditText password;
    private Button createAccount;
    private DatabaseReference mDatabaseReference;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creat__acc);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference().child("MUsers");

        mFirebaseAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);

        firstName = findViewById(R.id.firstName);
        lastName = findViewById(R.id.lastName);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        createAccount = findViewById(R.id.createAcc);

        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewAcc();
            }
        });
    }

    private void createNewAcc() {
        final String name = firstName.getText().toString().trim();
        final String lname = lastName.getText().toString().trim();
        String pwd = password.getText().toString().trim();
        String em = email.getText().toString().trim();

        if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(lname)
                && !TextUtils.isEmpty(pwd) && !TextUtils.isEmpty(em)) {
            progressDialog.setMessage("Creating Account.....");
            progressDialog.show();

            mFirebaseAuth.createUserWithEmailAndPassword(em, pwd).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    if (authResult != null) {
                        String userId = mFirebaseAuth.getCurrentUser().getUid();
                        DatabaseReference currentUserDb = mDatabaseReference.child(userId);

                        currentUserDb.child("firstname").setValue(name);
                        currentUserDb.child("lastname").setValue(lname);
                        currentUserDb.child("image").setValue("none");

                        progressDialog.dismiss();

                        Intent intent = new Intent(Create_Acc.this, HomeActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                }
            });
        }
    }
}
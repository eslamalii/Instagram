package com.example.instagram;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddPostActivity extends AppCompatActivity {

    EditText mPostTitle;
    EditText mPostDes;
    ImageView mPostImage;
    Button mSubmit;
    DatabaseReference databaseReference;
    FirebaseAuth auth;
    FirebaseUser user;
    private ProgressDialog mProgressDialog;
    private static final int GALLARY_CODE = 1;
    private Uri mImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        mProgressDialog = new ProgressDialog(this);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Posts");

        mPostTitle = findViewById(R.id.titlePost);
        mPostDes = findViewById(R.id.desPost);
        mPostImage = findViewById(R.id.addImagePost);
        mSubmit = findViewById(R.id.submitPost);

        mPostImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLARY_CODE);
            }
        });

        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startPosting();

            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLARY_CODE && resultCode == RESULT_OK){
            mImageUri = data.getData();
            mPostImage.setImageURI(mImageUri);
        }
    }

    private void startPosting() {

        mProgressDialog.setMessage("Posting to blog.....");
        mProgressDialog.show();

        String titleVel = mPostTitle.getText().toString().trim();
        String desVel = mPostDes.getText().toString().trim();

        if (!TextUtils.isEmpty(titleVel) & !TextUtils.isEmpty(desVel)) {

            Blog blog = new Blog("Title", "DESC", "Null", "Null", "Null");

            databaseReference.setValue(blog).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(AddPostActivity.this, "Added", Toast.LENGTH_SHORT).show();
                    mProgressDialog.dismiss();
                }
            });
        }
    }
}
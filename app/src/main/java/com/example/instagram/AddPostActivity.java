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

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class AddPostActivity extends AppCompatActivity {

    EditText mPostTitle;
    EditText mPostDes;
    ImageView mPostImage;
    Button mSubmit;
    DatabaseReference databaseReference;
    FirebaseAuth auth;
    FirebaseUser user;
    StorageReference storageReference;
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
        storageReference = FirebaseStorage.getInstance().getReference();


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
        if (requestCode == GALLARY_CODE && resultCode == RESULT_OK) {
            mImageUri = data.getData();
            mPostImage.setImageURI(mImageUri);
        }
    }

    private void startPosting() {

        mProgressDialog.setMessage("Posting to blog.....");
        mProgressDialog.show();

        final String titleVel = mPostTitle.getText().toString().trim();
        final String desVel = mPostDes.getText().toString().trim();

        if (!TextUtils.isEmpty(titleVel) & !TextUtils.isEmpty(desVel)
                && mImageUri != null) {

            StorageReference ref = storageReference.child(String.valueOf(System.currentTimeMillis()));

            ref.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> downloadUrl = taskSnapshot.getStorage().getDownloadUrl();

                    DatabaseReference dbRef = databaseReference.push();

                    Map<String, String> dataToSave = new HashMap<>();
                    dataToSave.put("title", titleVel);
                    dataToSave.put("desc", desVel);
                    dataToSave.put("image", downloadUrl.toString());
                    dataToSave.put("timestamp", String.valueOf(System.currentTimeMillis()));
                    dataToSave.put("userid", user.getUid());

                    dbRef.setValue(dataToSave);

                    mProgressDialog.dismiss();

                }
            });

        }
    }
}
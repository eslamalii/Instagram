package com.example.instagram.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.instagram.R;
import com.example.instagram.databinding.ActivityCreatAccBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class Create_Acc extends AppCompatActivity {

    private DatabaseReference mDatabaseReference;
    private FirebaseAuth mFirebaseAuth;
    private StorageReference mFirebaseStorage;
    private FirebaseDatabase mFirebaseDatabase;
    private ProgressDialog progressDialog;
    private Uri resultUri = null;
    private final static int GALLERY_CODE = 1;

    private ActivityCreatAccBinding creatAccBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creat__acc);
        creatAccBinding = DataBindingUtil.setContentView(this, R.layout.activity_creat__acc);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference().child("MUsers");

        mFirebaseAuth = FirebaseAuth.getInstance();

        mFirebaseStorage = FirebaseStorage.getInstance().getReference().child("MBlog_profile_Pics");

        progressDialog = new ProgressDialog(this);

        creatAccBinding.profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_CODE);
            }
        });
        creatAccBinding.createAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewAcc();
            }
        });
    }

    private void createNewAcc() {
        final String name = creatAccBinding.firstName.getText().toString().trim();
        final String lname = creatAccBinding.lastName.getText().toString().trim();
        String pwd = creatAccBinding.password.getText().toString().trim();
        String em = creatAccBinding.email.getText().toString().trim();

        if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(lname)
                && !TextUtils.isEmpty(pwd) && !TextUtils.isEmpty(em)) {
            progressDialog.setMessage("Creating Account.....");
            progressDialog.show();

            mFirebaseAuth.createUserWithEmailAndPassword(em, pwd).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    if (authResult != null) {

                        StorageReference imagePath = mFirebaseStorage.child("MBlog_profile_Pics")
                                .child(resultUri.getLastPathSegment());

                        imagePath.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                String userId = mFirebaseAuth.getCurrentUser().getUid();
                                DatabaseReference currentUserDb = mDatabaseReference.child(userId);

                                currentUserDb.child("firstname").setValue(name);
                                currentUserDb.child("lastname   ").setValue(lname);
                                currentUserDb.child("image").setValue(resultUri.toString());

                                progressDialog.dismiss();

                                Intent intent = new Intent(Create_Acc.this, HomeActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            }
                        });
                    }
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_CODE && resultCode == RESULT_OK) {
            Uri mImageUri = data.getData();

            CropImage.activity(mImageUri)
                    .setAspectRatio(1, 1)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                resultUri = result.getUri();
                creatAccBinding.profilePic.setImageURI(resultUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}
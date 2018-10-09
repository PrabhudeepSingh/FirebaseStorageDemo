package com.psb13.dineshadmin.ui;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.psb13.dineshadmin.R;
import com.psb13.dineshadmin.model.ButtonModel;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddButtonActivity extends AppCompatActivity {

    private static final int RC_TAKE_PICTURE = 101;

    TextView editTextButtonName;
    ImageView circleImageViewIcon;
    TextView editTextOnClickUrl;
    Button buttonUploadButton;

    private Uri downloadUrl = null;
    private Uri fileUri = null;

    private ProgressDialog progressDialog;

    String buttonName;
    String buttonUrlToIcon;
    String buttonOnClickUrl;

    private StorageReference storageRef;
    private CollectionReference buttonsCollection;
    private DocumentReference docRef;

    public static int count = 0;

    void init() {

        storageRef = FirebaseStorage.getInstance().getReference();
        buttonsCollection = FirebaseFirestore.getInstance().collection("buttons");
        docRef = buttonsCollection.document();

        editTextButtonName = findViewById(R.id.editTextButtonName);
        circleImageViewIcon = findViewById(R.id.circleImageViewIcon);
        editTextOnClickUrl = findViewById(R.id.editTextOnClickUrl);
        buttonUploadButton = findViewById(R.id.buttonUploadButton);

        circleImageViewIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchCamera();
            }
        });

        buttonUploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonName = editTextButtonName.getText().toString().trim();
                buttonOnClickUrl = editTextOnClickUrl.getText().toString().trim();

                if (checkData()) {
                    uploadFromUri(fileUri);
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_button);

        init();
    }

    void launchCamera() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, RC_TAKE_PICTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_TAKE_PICTURE) {
            if (resultCode == RESULT_OK) {
                fileUri = data.getData();
                circleImageViewIcon.setImageURI(fileUri);
            }
        }
    }

    private void uploadFromUri(Uri fileUri) {
        this.fileUri = fileUri;
        downloadUrl = null;
        showProgressDialog("Uploading Icon...");

        final StorageReference photoRef = storageRef.child("photos").child(docRef.getId());
        photoRef.putFile(fileUri)
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        long completedUnits = taskSnapshot.getBytesTransferred();
                        long totalUnits = taskSnapshot.getTotalByteCount();

                        int percentComplete = 0;
                        if (totalUnits > 0) {
                            percentComplete = (int) (100 * completedUnits / totalUnits);
                        }

                        showProgressDialog("Uploading Icon..." + percentComplete + "%");
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        downloadUrl = taskSnapshot.getMetadata().getDownloadUrl();
                        buttonUrlToIcon = downloadUrl.toString();
                        uploadButton();
                        hideProgressDialog();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddButtonActivity.this, "Error in uploading Icon. Please try again later.", Toast.LENGTH_SHORT).show();
                        hideProgressDialog();
                    }
                });

        showProgressDialog(getString(R.string.progress_uploading));
    }

    private void showProgressDialog(String caption) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setIndeterminate(true);
        }

        progressDialog.setMessage(caption);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    Boolean checkData() {
        Boolean flag = true;

        if (fileUri == null) {
            Toast.makeText(this, "Please Select an Icon", Toast.LENGTH_SHORT).show();
            flag = false;
        } else if (buttonName.isEmpty()) {
            editTextButtonName.setError("Please Enter the Name.");
            requestFocus(editTextButtonName);
            flag = false;
        } else if (buttonOnClickUrl.isEmpty()) {
            editTextOnClickUrl.setError("Please Enter the URL.");
            requestFocus(editTextOnClickUrl);
            flag = false;
        }

        return flag;
    }

    void uploadButton() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Uploading Button");
        progressDialog.show();
        ButtonModel model = new ButtonModel(buttonName, buttonUrlToIcon, buttonOnClickUrl, true);
        docRef.set(model)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(AddButtonActivity.this, "Button Added Successfully.", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddButtonActivity.this, "Error in Adding Button. Please try again", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                });
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }
}
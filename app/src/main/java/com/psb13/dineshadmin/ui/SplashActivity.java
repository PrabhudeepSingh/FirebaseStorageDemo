package com.psb13.dineshadmin.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.psb13.dineshadmin.R;

public class SplashActivity extends AppCompatActivity {

    FirebaseFirestore firestore;
    FirebaseAuth auth;

    void init() {
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        checkPayment();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        init();
    }

    void checkPayment() {
        if (isConnected()) {
            firestore.collection("settings").document("jnWJNRrgPE0KkwpFvBSr")
                    .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                            if ((Boolean) documentSnapshot.get("isEnabled")) {
                                if (auth.getCurrentUser() != null) {
                                    startActivity(new Intent(SplashActivity.this, ViewButtonsActivity.class));
                                    finish();
                                } else {
                                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                                    finish();
                                }
                            } else {
                                Toast.makeText(SplashActivity.this, "Please pay the developers fee to Continue!", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }
                    });
        }
        else showDisconnectedDialog();

    }

    boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        return (info != null && info.isConnected());
    }

    void showDisconnectedDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("No Internet Connectivity.Please Enable and Retry")
                .setCancelable(false)
                .setNegativeButton("EXIT", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setPositiveButton("Retry",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {
                                // Restart the Activity
                                Intent intent = getIntent();
                                finish();

                                startActivity(intent);

                                dialog.cancel();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }
}

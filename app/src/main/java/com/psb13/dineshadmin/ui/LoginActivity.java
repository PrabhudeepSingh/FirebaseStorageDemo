package com.psb13.dineshadmin.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.psb13.dineshadmin.R;
import com.psb13.dineshadmin.model.User;

public class LoginActivity extends AppCompatActivity {

    EditText editTextEmail, editTextPassword;
    Button buttonSignIn;
    TextView textViewForgotPassword;

    User user;
    FirebaseAuth auth;
    ProgressDialog progressDialog;


    void init(){
        editTextEmail = findViewById(R.id.editTextEmailSignIn);
        editTextPassword = findViewById(R.id.editTextPasswordSignIn);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Logging in.");
        progressDialog.setCancelable(false);

        user = new User();

        auth = FirebaseAuth.getInstance();

        buttonSignIn = findViewById(R.id.buttonSignIn);
        buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                user.email = editTextEmail.getText().toString().trim();
                user.password = editTextPassword.getText().toString().trim();

                String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
                if(!user.email.matches(emailPattern)){
                    editTextEmail.setError("Please enter a valid Email");
                    requestFocus(editTextEmail);
                }
                else if(user.password == null || user.password.length()<6){
                    editTextPassword.setError("Password length should be greater than 6.");
                    requestFocus(editTextPassword);
                }
                else
                    loginUser();
            }
        });

        textViewForgotPassword = findViewById(R.id.textViewForgotPassword);
        textViewForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = editTextEmail.getText().toString().trim();

                String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
                if(!email.matches(emailPattern)){
                    editTextEmail.setError("Please enter a valid Email");
                    requestFocus(editTextEmail);
                }
                else {
                    auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(LoginActivity.this, "Password Reset Email sent to "+email, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    void loginUser(){

        progressDialog.show();

        auth.signInWithEmailAndPassword(user.email, user.password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Intent intent =  new Intent(LoginActivity.this, ViewButtonsActivity.class);
                            startActivity(intent);
                            progressDialog.dismiss();
                            finish();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(LoginActivity.this, "Login Error", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                        e.printStackTrace();
                    }
                });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        init();
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }
}

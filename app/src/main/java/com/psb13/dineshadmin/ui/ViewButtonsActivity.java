package com.psb13.dineshadmin.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.psb13.dineshadmin.R;
import com.psb13.dineshadmin.adapter.ButtonAdapter;
import com.psb13.dineshadmin.model.ButtonModel;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import static com.psb13.dineshadmin.R.color.colorAccent;


public class ViewButtonsActivity extends AppCompatActivity {

    RecyclerView recyclerViewButtons;
    LinearLayoutManager linearLayoutManager;
    GridLayoutManager gridLayoutManager;

    TextView textViewBottomSheet;

    ImageView upArrow, downArrow;
    Button saveBtn;

    BottomSheetBehavior bottomSheetBehavior;
    LinearLayout linearLayout;

    ButtonAdapter buttonAdapterTrue, buttonAdapterFalse;
    FirebaseFirestore firestore;
    Query queryTrue, queryFalse;
    FirestoreRecyclerOptions<ButtonModel> optionsTrue, optionsFalse;

    Boolean view;
    FirebaseAuth auth;

    ProgressDialog progressDialog;

    void init() {

        textViewBottomSheet = findViewById(R.id.textViewBottomSheet);
        upArrow = findViewById(R.id.upArrow);
        downArrow = findViewById(R.id.downArrow);

        upArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    retrieveData();

                }
            }
        });

        downArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_COLLAPSED) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

                }
            }
        });

        linearLayout = findViewById(R.id.bottom_sheet);

        bottomSheetBehavior = BottomSheetBehavior.from(linearLayout);

        /**
         * bottom sheet state change listener
         * we are changing button text when sheet changed state
         * */
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED: {


                        upArrow.setVisibility(View.GONE);
                        downArrow.setVisibility(View.VISIBLE);

                    }
                    break;
                    case BottomSheetBehavior.STATE_COLLAPSED: {
                        upArrow.setVisibility(View.VISIBLE);
                        downArrow.setVisibility(View.GONE);
                    }
                    break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        saveBtn = findViewById(R.id.saveBtn);

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveBtn();
            }
        });

        recyclerViewButtons = findViewById(R.id.recycerViewButtons);
        firestore = FirebaseFirestore.getInstance();

        queryTrue = firestore.collection("buttons").whereEqualTo("isEnabled", true);
        optionsTrue = new FirestoreRecyclerOptions.Builder<ButtonModel>().setQuery(queryTrue, ButtonModel.class).build();
        buttonAdapterTrue = new ButtonAdapter(optionsTrue, this);
        buttonAdapterTrue.startListening();

        queryFalse = firestore.collection("buttons").whereEqualTo("isEnabled", false);
        optionsFalse = new FirestoreRecyclerOptions.Builder<ButtonModel>().setQuery(queryFalse, ButtonModel.class).build();
        buttonAdapterFalse = new ButtonAdapter(optionsFalse, this);
        buttonAdapterFalse.startListening();

        linearLayoutManager = new LinearLayoutManager(this);
        gridLayoutManager = new GridLayoutManager(this, 2);
        recyclerViewButtons.setLayoutManager(gridLayoutManager);

        view = true;

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Please Wait!!!");

        auth = FirebaseAuth.getInstance();

        showEnabledButtons();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_buttons);
        getSupportActionBar().setTitle(Html.fromHtml("<font color='#000'>Enabled Buttons</font>"));
        init();
    }


    void showEnabledButtons() {
        recyclerViewButtons.setAdapter(buttonAdapterTrue);
    }

    void showDisabledButtons() {
        recyclerViewButtons.setAdapter(buttonAdapterFalse);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(1, 1, 1, "").setIcon(R.drawable.add).setShowAsAction(1);
        menu.add(1, 2, 1, "").setIcon(R.drawable.disbaled_button).setShowAsAction(1);
        menu.add(1, 5, 1, "").setIcon(R.drawable.logout).setShowAsAction(1);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 1:
                Intent intent = new Intent(this, AddButtonActivity.class);
                startActivity(intent);
                break;
            case 2:
                if (view) {
                    showDisabledButtons();
                    getSupportActionBar().setTitle(Html.fromHtml("<font color='#000'>Disbaled Buttons</font>"));
                    item.setIcon(R.drawable.enabled_buttons).setShowAsAction(1);

                    view = false;
                } else {
                    showEnabledButtons();
                    getSupportActionBar().setTitle(Html.fromHtml("<font color='#000'>Enabled Buttons</font>"));
                    item.setIcon(R.drawable.disbaled_button).setShowAsAction(1);
                    view = true;
                }
                break;

            case 5:
                showLogoutAlert();
                break;
        }
        return super.onOptionsItemSelected(item);
    }




    void retrieveData() {
        firestore.collection("settings").document("aJhzbE24kD5KZhcnqWR4").get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Boolean isBorder = (Boolean) documentSnapshot.get("border");
                        long spanCount = (Long) documentSnapshot.get("spanCount");


                        final RadioGroup radioGroupSpanCount = findViewById(R.id.radioGroupBorder);

                        if (isBorder)
                            radioGroupSpanCount.check(R.id.radioButtonYes);
                        else
                            radioGroupSpanCount.check(R.id.radioButtonNo);


                        final RadioGroup radioGroupSpanCount1 = findViewById(R.id.radioGroupSpanCount);

                        switch ((int) spanCount) {
                            case 1:
                                radioGroupSpanCount1.check(R.id.radioButton1);
                                break;
                            case 2:
                                radioGroupSpanCount1.check(R.id.radioButton2);
                                break;
                            case 3:
                                radioGroupSpanCount1.check(R.id.radioButton3);
                                break;
                        }


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ViewButtonsActivity.this, "Border Error. Please try again later.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    void saveBtn() {

        if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_COLLAPSED) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            textViewBottomSheet.setText("Saving . . .");
        }

        Map<String, Object> map = new HashMap();
        Boolean border = true;
        final RadioGroup radioGroupSpanCount = findViewById(R.id.radioGroupBorder);
        switch (radioGroupSpanCount.getCheckedRadioButtonId()) {
            case R.id.radioButtonYes:
                border = true;
                break;
            case R.id.radioButtonNo:
                border = false;
                break;
        }
        map.put("border", border);

        int spCount = 0;
        final RadioGroup radioGroupSpanCount1 = findViewById(R.id.radioGroupSpanCount);

        switch (radioGroupSpanCount1.getCheckedRadioButtonId()) {
            case R.id.radioButton1:
                spCount = 1;
                break;
            case R.id.radioButton2:
                spCount = 2;
                break;
            case R.id.radioButton3:
                spCount = 3;
                break;
        }

        map.put("spanCount",spCount);

        firestore.collection("settings").document("aJhzbE24kD5KZhcnqWR4")
                .update(map)
        .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                textViewBottomSheet.setText("Saved");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        textViewBottomSheet.setText("Settings ");

                    }
                },2000);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                textViewBottomSheet.setText("Error");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        textViewBottomSheet.setText("Settings ");

                    }
                },2000);
            }
        });
    }

    void showLogoutAlert(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("Do you really want to logout? ")
                .setCancelable(false)
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setPositiveButton("YES",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {
                                // Restart the Activity
                                auth.signOut();
                                startActivity(new Intent(ViewButtonsActivity.this, LoginActivity.class));
                                dialog.cancel();
                                finish();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }
}

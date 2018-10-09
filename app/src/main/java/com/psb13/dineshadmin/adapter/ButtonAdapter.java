package com.psb13.dineshadmin.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.psb13.dineshadmin.holder.ButtonHolder;
import com.psb13.dineshadmin.model.ButtonModel;

import com.psb13.dineshadmin.R;
import com.psb13.dineshadmin.ui.AddButtonActivity;
import com.psb13.dineshadmin.ui.EditButtonActivity;
import com.squareup.picasso.Picasso;

public class ButtonAdapter extends FirestoreRecyclerAdapter<ButtonModel, ButtonHolder> {
    Context context;
    CollectionReference buttonsCollection;

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See
     * {@link FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public ButtonAdapter(FirestoreRecyclerOptions<ButtonModel> options, Context context) {
        super(options);
        this.context = context;
        buttonsCollection = FirebaseFirestore.getInstance().collection("buttons");
    }

    @Override
    protected void onBindViewHolder(final ButtonHolder holder, int position, final ButtonModel model) {

        final ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Please Wait!!!");

        holder.textViewButtonName.setText(model.name);

        Picasso.get().load(model.urlToIcon).into(holder.circleImageViewIcon);

        holder.imageViewOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(context, holder.imageViewOptions);
                Menu menu = popupMenu.getMenu();

                if (model.isEnabled) {
                    menu.add(1, 1, 1, "Edit Button Details");
                    menu.add(1, 2, 1, "Disable Button");
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case 1:
                                    Intent intent = new Intent(context, EditButtonActivity.class);
                                    intent.putExtra("keyId", getSnapshots().getSnapshot(holder.getAdapterPosition()).getId());
                                    context.startActivity(intent);
                                    break;
                                case 2:
                                    buttonsCollection.document(getSnapshots().getSnapshot(holder.getAdapterPosition()).getId()).update("isEnabled", false)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    progressDialog.dismiss();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(context, "Can't disable the Button at the moment. Please try again later.", Toast.LENGTH_SHORT).show();
                                                    progressDialog.dismiss();
                                                }
                                            });
                                    break;
                            }
                            return false;
                        }
                    });
                } else {
                    menu.add(1, 1, 1, "Enable Button");
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case 1:
                                    buttonsCollection.document(getSnapshots().getSnapshot(holder.getAdapterPosition()).getId()).update("isEnabled", true)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    progressDialog.dismiss();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(context, "Can't Enable the Button at the moment. Please try again later.", Toast.LENGTH_SHORT).show();
                                                    progressDialog.dismiss();
                                                }
                                            });

                                    break;
                            }
                            return false;
                        }
                    });
                }
                popupMenu.show();
            }
        });
    }

    @Override
    public ButtonHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view_button, parent, false);
        ButtonHolder buttonHolder = new ButtonHolder(view);
        return buttonHolder;
    }
}

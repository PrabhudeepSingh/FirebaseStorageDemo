package com.psb13.dineshadmin.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.psb13.dineshadmin.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class ButtonHolder extends RecyclerView.ViewHolder{

    public TextView textViewButtonName;
    public ImageView imageViewOptions;
    public ImageView circleImageViewIcon;


    public ButtonHolder(View itemView) {
        super(itemView);
        textViewButtonName = itemView.findViewById(R.id.textViewButtonName);
        imageViewOptions = itemView.findViewById(R.id.imageViewOptions);
        circleImageViewIcon = itemView.findViewById(R.id.circleImageViewIconItem);
    }
}

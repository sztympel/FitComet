package com.example.fitcometv3;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class ViewHolder extends RecyclerView.ViewHolder {
    public LinearLayout root;
    public TextView txtTitle;
    public ImageView Image;
    public TextView txtDesc;

    public ViewHolder(View itemView) {
        super(itemView);
        root = itemView.findViewById(R.id.list_root);
        txtTitle = itemView.findViewById(R.id.list_title);
        Image = itemView.findViewById(R.id.list_image);
        txtDesc = itemView.findViewById(R.id.list_desc);
    }

    public void setTxtTitle(String string) {
        txtTitle.setText(string);
    }

    public void setImage(String url) {

        Picasso.get().load(url).into(Image);
    }

    public void setTxtDesc(String string) {
        txtDesc.setText(string);
    }
}
package com.example.mdpproject.recycler;

import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mdpproject.R;

public class MyViewHolder extends RecyclerView.ViewHolder {

    ImageView image;
    TextView title;
    TextView content;

    public MyViewHolder(@NonNull View itemView) {
        super(itemView);
        image = itemView.findViewById(R.id.Image);
        title = itemView.findViewById(R.id.title);
        content = itemView.findViewById(R.id.content);
        content.setMovementMethod(new ScrollingMovementMethod());
    }
}
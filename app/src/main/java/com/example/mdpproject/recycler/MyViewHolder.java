package com.example.mdpproject.recycler;

import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mdpproject.R;

public class MyViewHolder extends RecyclerView.ViewHolder{
    ImageView Image;
    TextView Title;
    TextView Content;
    public MyViewHolder(@NonNull View itemView) {
        super(itemView);
        Image = itemView.findViewById(R.id.Image);
        Title = itemView.findViewById(R.id.title);
        Content = itemView.findViewById(R.id.content);
        Content.setMovementMethod(new ScrollingMovementMethod());
    }
}
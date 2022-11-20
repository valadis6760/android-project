package com.example.mdpproject.recycler;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mdpproject.R;
import com.example.mdpproject.http.Feed;
import com.example.mdpproject.http.PicassoClient;

import java.util.List;

public class Adapter extends RecyclerView.Adapter<MyViewHolder> {
    Context context;
    private List<Feed> items;

    public Adapter(Context context, List<Feed> items) {
        super();
        this.items = items;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        String imageSrc = items.get(position).getImageSrc();
        holder.title.setText(items.get(position).getTitle());
        holder.content.setText(items.get(position).getContent());
        PicassoClient.downloadImage(context, imageSrc, holder.image);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = items.get(holder.getAdapterPosition()).getLink();
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

}
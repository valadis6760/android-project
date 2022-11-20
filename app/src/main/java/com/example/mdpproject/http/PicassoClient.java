package com.example.mdpproject.http;

import android.content.Context;
import android.widget.ImageView;

import com.example.mdpproject.R;
import com.squareup.picasso.Picasso;

public class PicassoClient {
    public static void downloadImage(Context context, String imageUrl, ImageView image) {
        if (imageUrl != null && imageUrl.length() > 0) {
            Picasso.with(context).load(imageUrl).placeholder(R.drawable.image_placeholder).into(image);
        } else {
            Picasso.with(context).load(R.drawable.image_placeholder).into(image);
        }
    }
}

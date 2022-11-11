package com.example.mdpproject.utils;

import android.content.Context;
import android.widget.ImageView;

import com.example.mdpproject.R;
import com.squareup.picasso.Picasso;

public class PicassoClient {
    public static void downloadImage(Context context, String Imageurl, ImageView image)
    {
        if (Imageurl != null && Imageurl.length()>0)
        {
            Picasso.with(context).load(Imageurl).placeholder(R.drawable.image_placeholder).into(image);
        }else
        {
            Picasso.with(context).load(R.drawable.image_placeholder).into(image);
        }
    }
}

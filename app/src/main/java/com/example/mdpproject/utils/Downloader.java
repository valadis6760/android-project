package com.example.mdpproject.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

public class Downloader extends AsyncTask<Void, Void, Object> {
    Context context;
    String urlAddress;
    RecyclerView recyclerView;
    ProgressDialog progressDialog;

    public Downloader(Context context, String urlAddress, RecyclerView recyclerView) {
        this.context = context;
        this.urlAddress = urlAddress;
        this.recyclerView = recyclerView;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Fetching");
        progressDialog.setMessage("Fetching... Please Wait");
        progressDialog.show();
    }

    @Override
    protected Object doInBackground(Void... params) {
        return downloadData();
    }

    @Override
    protected void onPostExecute(Object data) {
        super.onPostExecute(data);
        progressDialog.dismiss();
        if (data.toString().startsWith("Error")) {
            Toast.makeText(context, data.toString(), Toast.LENGTH_SHORT).show();
        } else {
            // parsing
            new Parser(context, (InputStream) data, recyclerView).execute();
        }
    }

    private Object downloadData() {
        Object connection = Connector.connector(urlAddress);
        if (connection.toString().startsWith("Error")) {
            return connection.toString();
        }
        try {
            HttpURLConnection httpURLConnection = (HttpURLConnection) connection;
            int ResponseCode = httpURLConnection.getResponseCode();
            Log.i("Downloader", httpURLConnection.getResponseMessage());
            if (ResponseCode == HttpURLConnection.HTTP_OK) {
                return new BufferedInputStream(httpURLConnection.getInputStream());
            }
            return ErrorTracker.ResponseError + httpURLConnection.getResponseMessage();
        } catch (IOException e) {
            e.printStackTrace();
            return ErrorTracker.IOError;
        }
    }
}

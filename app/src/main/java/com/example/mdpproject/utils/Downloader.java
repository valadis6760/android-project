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

public class Downloader extends AsyncTask<Void,Void,Object> {
    Context context;
    String urladdress;
    RecyclerView rv;
    ProgressDialog pd;

    public Downloader(Context context, String urladdress, RecyclerView rv){
        this.context= context;
        this.urladdress= urladdress;
        this.rv = rv;
    }


    @Override
    protected void onPreExecute(){
        super.onPreExecute();
        pd = new ProgressDialog(context);
        pd.setTitle("Fetching");
        pd.setMessage("Fetching... Please Wait");
        pd.show();
    }

    @Override
    protected Object doInBackground(Void... params) {
        return downloaddata();
    }


    @Override
    protected void onPostExecute(Object data){
        super.onPostExecute(data);
        pd.dismiss();
        if (data.toString().startsWith("Error")){
            Toast.makeText(context,data.toString(),Toast.LENGTH_SHORT).show();
        }
        else {
            // parsing
            new Parser(context, (InputStream) data,rv).execute();
        }
    }

    private Object downloaddata(){
        Object connection = Connecter.connector(urladdress);
        if (connection.toString().startsWith("Error")){
            return connection.toString();
        }
        try{
            HttpURLConnection con = (HttpURLConnection) connection;
            int ResponseCode = con.getResponseCode();
            Log.i("Downloader", con.getResponseMessage());
            if (ResponseCode == con.HTTP_OK)
            {
                InputStream is= new BufferedInputStream(con.getInputStream());
                return is;
            }
            return ErrorTracker.ResponseError+con.getResponseMessage();
        } catch (IOException e) {
            e.printStackTrace();
            return ErrorTracker.IOError;
        }
    }
}

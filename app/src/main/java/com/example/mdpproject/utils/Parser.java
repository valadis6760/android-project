package com.example.mdpproject.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.text.Html;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.mdpproject.model.Feed;
import com.example.mdpproject.recycler.Adapter;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class Parser extends AsyncTask<Void,Void,Boolean> {
    Context context;
    InputStream is;
    RecyclerView rv;
    ProgressDialog pd;
    ArrayList<Feed> items = new ArrayList<>();

    public Parser(Context context, InputStream is, RecyclerView rv){
        this.context= context;
        this.is= is;
        this.rv = rv;
    }



    @Override
    protected void onPreExecute(){
        super.onPreExecute();
        pd = new ProgressDialog(context);
        pd.setTitle("Parsing");
        pd.setMessage("Parsing... Please Wait");
        pd.show();
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        return this.ParseRSS();
    }

    @Override
    protected void onPostExecute(Boolean isParsed) {
        super.onPostExecute(isParsed);
        pd.dismiss();
        if (isParsed){
            rv.setAdapter(new Adapter(context,items));

        }
        else {
            Toast.makeText(context,"Unable to parse",Toast.LENGTH_SHORT).show();
        }

    }

    //@NonNull
    private Boolean ParseRSS(){
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();

            parser.setInput(is,null);
            int event = parser.getEventType();

            String TagValue = null;
            Boolean sitemeta = true;

            items.clear();
            Feed item = new Feed();

            do {
                String TagName = parser.getName();

                switch (event){
                    case XmlPullParser.START_TAG:
                        if (TagName.equalsIgnoreCase("item")){
                            item = new Feed() ;
                            sitemeta = false;
                        }
                        break;

                    case XmlPullParser.TEXT:
                        TagValue = parser.getText();
                        break;

                    case XmlPullParser.END_TAG:
                        if (!sitemeta){
                            if (TagName.equalsIgnoreCase("title")){
                                item.setTitle(TagValue);
                            } else if (TagName.equalsIgnoreCase("link")){
                                item.setLink(TagValue);

                            } else if (TagName.equalsIgnoreCase("description")){
                                String all = TagValue;
                                String imageurl = all.substring(all.indexOf("src")+5,all.indexOf("jpg")+3);
                                String content = "";
                                String[] splitted = (all.split("</div><div>"));
                                content =  splitted[1].replace("</div>"," ");
                                item.setImageSrc(imageurl);
                                item.setContent(content.substring(0,88)+ Html.fromHtml("<b>... See More</b>"));

                            }
                        }

                        if (TagName.equalsIgnoreCase("item")){
                            items.add(item);
                            sitemeta = true;
                        }
                        break;
                }

                event = parser.next();
            }while (event != XmlPullParser.END_DOCUMENT);

            return true;

        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;

    }
}
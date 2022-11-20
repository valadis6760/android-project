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

public class Parser extends AsyncTask<Void, Void, Boolean> {
    Context context;
    InputStream inputStream;
    RecyclerView recyclerView;
    ProgressDialog progressDialog;
    ArrayList<Feed> items = new ArrayList<>();

    public Parser(Context context, InputStream inputStream, RecyclerView recyclerView) {
        this.context = context;
        this.inputStream = inputStream;
        this.recyclerView = recyclerView;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Parsing");
        progressDialog.setMessage("Parsing... Please Wait");
        progressDialog.show();
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        return this.parseRSS();
    }

    @Override
    protected void onPostExecute(Boolean isParsed) {
        super.onPostExecute(isParsed);
        progressDialog.dismiss();
        if (isParsed) {
            recyclerView.setAdapter(new Adapter(context, items));
        } else {
            Toast.makeText(context, "Unable to parse", Toast.LENGTH_SHORT).show();
        }
    }

    private Boolean parseRSS() {
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();

            parser.setInput(inputStream, null);
            int event = parser.getEventType();

            String tagValue = null;
            Boolean sitemeta = true;

            items.clear();
            Feed item = new Feed();

            do {
                String TagName = parser.getName();

                switch (event) {
                    case XmlPullParser.START_TAG:
                        if (TagName.equalsIgnoreCase("item")) {
                            item = new Feed();
                            sitemeta = false;
                        }
                        break;

                    case XmlPullParser.TEXT:
                        tagValue = parser.getText();
                        break;

                    case XmlPullParser.END_TAG:
                        if (!sitemeta) {
                            if (TagName.equalsIgnoreCase("title")) {
                                item.setTitle(tagValue);
                            } else if (TagName.equalsIgnoreCase("link")) {
                                item.setLink(tagValue);
                            } else if (TagName.equalsIgnoreCase("description")) {
                                String all = tagValue;
                                String imageUrl = all.substring(all.indexOf("src") + 5, all.indexOf("jpg") + 3);
                                String content = "";
                                String[] split = (all.split("</div><div>"));
                                content = split[1].replace("</div>", " ");
                                item.setImageSrc(imageUrl);
                                item.setContent(content.substring(0, 88) + Html.fromHtml("<b>... See More</b>"));
                            }
                        }

                        if (TagName.equalsIgnoreCase("item")) {
                            items.add(item);
                            sitemeta = true;
                        }
                        break;
                }
                event = parser.next();
            } while (event != XmlPullParser.END_DOCUMENT);

            return true;
        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
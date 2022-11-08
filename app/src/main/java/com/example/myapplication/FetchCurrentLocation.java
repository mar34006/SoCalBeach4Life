package com.example.myapplication;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

public class FetchCurrentLocation extends AsyncTask<String, LatLng, LatLng> {

    private static final String API_KEY = "AIzaSyC0bF1q80VY5W1vD73VCb45NMEU4mUvHsg";
    DisplayBeachesActivity ctxt;

    FetchCurrentLocation(DisplayBeachesActivity context)
    {
        this.ctxt = context;
    }
    @Override
    protected LatLng doInBackground(String ... address) {
        String url = getPathUrl(address[0]);
        LatLng location = null;
        String data = "";
        InputStream is = null;
        HttpURLConnection conn = null;
        try {
            URL uri = new URL(url);
            conn = (HttpURLConnection) uri.openConnection();
            conn.connect();
            is = conn.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            StringBuffer sb = new StringBuffer();
            String line = "";
            while((line = br.readLine()) != null)
                sb.append(line);
            data = sb.toString();
            Log.i("Map", String.format("Data is %s", data));
            br.close();
        } catch (Exception e)
        {
            Log.e("Map", "Exception while reading route data!");
        } finally {
            try {
                is.close();
            } catch (Exception e)
            {
            }
            conn.disconnect();
        }
        try
        {
            JSONObject obj = new JSONObject(data);
            JSONArray results = obj.getJSONArray("results");
            JSONObject firstResult = (JSONObject)results.get(0);
            JSONObject geometry = firstResult.getJSONObject("geometry");
            JSONObject loc = geometry.getJSONObject("location");
            location = new LatLng(loc.getDouble("lat"), loc.getDouble("lng"));

        } catch(Exception e)
        {

        }

        return location;
    }

    @Override
    protected void onPostExecute(LatLng res)
    {
        super.onPostExecute(res);
        // send data to map parser
        ctxt.receivedLocation(res);
    }

    public String getPathUrl(String address)
    {
        String res = String.format("https://maps.google.com/maps/api/geocode/json?address=%s&key=%s", Uri.encode(address), API_KEY);
        return res;
    }
}

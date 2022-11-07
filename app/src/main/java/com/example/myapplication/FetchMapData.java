package com.example.myapplication;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class FetchMapData extends AsyncTask<LatLng, String, String> {
    /*String url;*/
    private static final String API_KEY = "AIzaSyC0bF1q80VY5W1vD73VCb45NMEU4mUvHsg";
    MapsActivity ctxt;
    LatLng destination;
    MapsActivity.TransportationMode mMode;

    FetchMapData(MapsActivity context, MapsActivity.TransportationMode mode)
    {
        ctxt = context;
        destination = null;
        mMode = mode;
    }

    @Override
    protected String doInBackground(LatLng ... latLngs) {
        String url = getPathUrl(latLngs[0], latLngs[1]);
        destination = latLngs[1];
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
        return data;
    }

    @Override
    protected void onPostExecute(String res)
    {
        super.onPostExecute(res);
        // send data to map parser
        new ParseMapData(ctxt, destination).execute(res);
    }

    public String getPathUrl(LatLng a, LatLng b)
    {
        String method = "";
        if(mMode == MapsActivity.TransportationMode.DRIVING)
            method = "driving";
        else
            method = "walking";
        String res = String.format("https://maps.googleapis.com/maps/api/directions/json?origin=%f,%f&destination=%f,%f&sensor=false&mode=%s&key=%s", a.latitude, a.longitude, b.latitude, b.longitude, method, API_KEY);
        Log.i("URL", res);
        return res;
    }
}

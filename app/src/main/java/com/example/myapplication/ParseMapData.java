package com.example.myapplication;

import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParseMapData extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>>
{
    MapsActivity ctxt;
    String duration;
    LatLng dest;

    ParseMapData(MapsActivity context, LatLng destination)
    {
        ctxt = context;
        duration = "";
        dest = destination;
    }

    @Override
    protected void onPreExecute(){
        super.onPreExecute();
    }

    @Override
    protected List<List<HashMap<String, String>>> doInBackground(String... data)
    {
        JSONObject j;
        List<List<HashMap<String, String>>> routes = null;
        try {
            j = new JSONObject(data[0]);
            //Log.i("Background", j.toString());
            routes = parseRoutes(j);
            //Log.i("Routes", routes.toString());
        }
        catch (JSONException e)
        {
            Log.e("JSONParser", "Couldn't parse data.");
            e.printStackTrace();
        }
        catch (Exception e)
        {
            Log.e("Parser", "Couldn't parse data.");
            e.printStackTrace();
        }
        return routes;
    }

    private List<List<HashMap<String, String>>> parseRoutes(JSONObject json)
    {
        List<List<HashMap<String, String>>> mroutes = new ArrayList<>();
        JSONArray jRoutes;
        JSONArray jLegs;
        JSONArray jSteps;
        try {
            jRoutes = json.getJSONArray("routes");
            for (int i = 0; i < jRoutes.length(); i++) {
                jLegs = ((JSONObject) jRoutes.get(i)).getJSONArray("legs");
                List path = new ArrayList<>();
                for (int j = 0; j < jLegs.length(); j++) {
                    jSteps = ((JSONObject) jLegs.get(j)).getJSONArray("steps");
                    JSONObject jDuration = ((JSONObject) jLegs.get(j)).getJSONObject("duration");
                    duration = jDuration.getString("text");
                    for (int k = 0; k < jSteps.length(); k++) {
                        String polyline = "";
                        polyline = (String) ((JSONObject) ((JSONObject) jSteps.get(k)).get("polyline")).get("points");
                        List<LatLng> list = PolyUtil.decode(polyline);
                        for (int l = 0; l < list.size(); l++) {
                            HashMap<String, String> hm = new HashMap<>();
                            hm.put("lat", Double.toString((list.get(l)).latitude));
                            hm.put("lng", Double.toString((list.get(l)).longitude));
                            path.add(hm);
                        }
                    }
                    mroutes.add(path);
                }
            }
        } catch (Exception e) {
            Log.e("Parse", "Failed to read JSON data.");
            e.printStackTrace();
        }
        Log.i("Parse", String.format("duration is %s", duration));
        return mroutes;
    }

    public void parse()
    {
        JSONObject j;
        try {
            //j = new JSONObject(data);
            //routes = parseRoutes(j);
        } catch (Exception e)
        {
            Log.e("Parser", "Couldn't parse data.");
            e.printStackTrace();
        }
    }

    @Override
    protected void onPostExecute(List<List<HashMap<String, String>>> routes)
    {
        ArrayList<LatLng> points;
        PolylineOptions lineOptions = null;
        List<List<HashMap<String, String>>> result = routes;
        // Traversing through all the routes
        for (int i = 0; i < result.size(); i++) {
            points = new ArrayList<>();
            lineOptions = new PolylineOptions();
            // Fetching i-th route
            List<HashMap<String, String>> path = result.get(i);
            // Fetching all the points in i-th route
            for (int j = 0; j < path.size(); j++) {
                HashMap<String, String> point = path.get(j);
                double lat = Double.parseDouble(point.get("lat"));
                double lng = Double.parseDouble(point.get("lng"));
                LatLng position = new LatLng(lat, lng);
                points.add(position);
            }
            // Adding all the points in the route to LineOptions
            lineOptions.addAll(points);
            lineOptions.width(20);
            lineOptions.color(Color.BLUE);
        }
        if(lineOptions != null)
            ctxt.setPolyLine(lineOptions);
        MarkerOptions options = null;
        if(duration != null)
            options = new MarkerOptions().position(dest).title(String.format("Destination (%s)", duration));
        else
            options = new MarkerOptions().position(dest).title("Destination");
        ctxt.addDestinationInformation(options, duration);
    }
}

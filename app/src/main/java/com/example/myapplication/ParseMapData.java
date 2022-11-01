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
    protected void onPreExecute()
    {
        super.onPreExecute();
    }

    @Override
    protected List<List<HashMap<String, String>>> doInBackground(String... data)
    {
        JSONObject j;
        List<List<HashMap<String, String>>> routes = null;
        try {
            j = new JSONObject(data[0]);
            routes = parseRoutes(j); // grab routes from json data
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
        JSONArray routes, legs, steps;
        try
        {
            routes = json.getJSONArray("routes");
            int routes_len = routes.length();
            for (int r = 0; r < routes_len; r++)
            {
                legs = ((JSONObject)routes.get(r)).getJSONArray("legs");
                int legs_len = legs.length();
                List path = new ArrayList<>();
                for (int l = 0; l < legs_len; l++)
                {
                    steps = ((JSONObject)legs.get(l)).getJSONArray("steps");
                    int steps_len = steps.length();
                    duration = ((JSONObject)legs.get(l)).getJSONObject("duration").getString("text");
                    for (int s = 0; s < steps_len; s++)
                    {
                        String polyline = (String) ((JSONObject) ((JSONObject) steps.get(s)).get("polyline")).get("points");
                        List<LatLng> list = PolyUtil.decode(polyline);
                        int list_len = list.size();
                        for (int i = 0; i < list_len; i++)
                        {
                            HashMap<String, String> hm = new HashMap<>();
                            hm.put("lat", Double.toString((list.get(i)).latitude));
                            hm.put("lng", Double.toString((list.get(i)).longitude));
                            path.add(hm);
                        }
                    }
                    mroutes.add(path);
                }
            }
        } catch (Exception e)
        {
            Log.e("Parse", "Failed to read JSON data.");
            e.printStackTrace();
        }
        return mroutes;
    }


    @Override
    protected void onPostExecute(List<List<HashMap<String, String>>> routes)
    {
        ArrayList<LatLng> pts;
        PolylineOptions l = null;
        List<List<HashMap<String, String>>> result = routes;
        for (int i = 0; i < result.size(); i++)
        {
            pts = new ArrayList<>();
            l = new PolylineOptions();
            List<HashMap<String, String>> path = result.get(i);
            int path_size = path.size();
            for (int j = 0; j < path_size; j++)
            {
                HashMap<String, String> pt = path.get(j);
                double lat = Double.parseDouble(pt.get("lat"));
                double lng = Double.parseDouble(pt.get("lng"));
                LatLng pos = new LatLng(lat, lng);
                pts.add(pos);
            }
            for(int p = 0; p < pts.size(); p++)
            {
                l.add(pts.get(p));
            }
            l.width(15);
            l.color(Color.rgb(180, 0, 110));
        }
        if(l != null)
            ctxt.setPolyLine(l);
        ctxt.addDestinationInformation(duration);
    }
}

//package com.lak.tunebreaddriver.Util;
////package com.example.userapp.Util;
//import android.content.Context;
//import android.graphics.Color;
//import android.os.AsyncTask;
//import android.util.Log;
//
//import com.google.android.gms.maps.model.LatLng;
//import com.google.android.gms.maps.model.PolylineOptions;
//
//import org.json.JSONObject;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//
//public class PointsParser extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>>{
//
//    com.lak.tunebreaddriver.Util.TaskLoadedCallback taskCallback;
////    com.example.userapp.Util.TaskLoadedCallback taskCallback;
//    String directionMode = "driving";
//    String latS = "";
//    String lngS = "";
//    AppConfig appConfig;
//    public PointsParser(){
//    }
//    public PointsParser(Context mContext, String directionMode) {
//        appConfig = new AppConfig(mContext);
//        this.taskCallback = (com.lak.tunebreaddriver.Util.TaskLoadedCallback) mContext;
////        this.taskCallback = (com.example.userapp.Util.TaskLoadedCallback) mContext;
//        this.directionMode = directionMode;
//    }
//    // Parsing the data in non-ui thread
//    @Override
//    protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {
//
//        JSONObject jObject;
//        List<List<HashMap<String, String>>> routes = null;
//
//        try {
//            jObject = new JSONObject(jsonData[0]);
//            Log.d("mylog", jsonData[0].toString());
//            DataParser parser = new DataParser();
//            Log.d("mylog", parser.toString());
//
//            // Starts parsing data
//            routes = parser.parse(jObject);
//            Log.d("mylog", "Executing routes");
//            Log.d("mylog", routes.toString());
//
//        } catch (Exception e) {
//            Log.d("mylog", e.toString());
//            e.printStackTrace();
//        }
//        return routes;
//    }
//
//    // Executes in UI thread, after the parsing process
//    @Override
//    protected void onPostExecute(List<List<HashMap<String, String>>> result) {
//        //super.onPostExecute(lists);
//        ArrayList<LatLng> points;
//        PolylineOptions lineOptions = null;
//      //  appConfig = new AppConfig();
//        // Traversing through all the routes
//        for (int i = 0; i < result.size(); i++) {
//            points = new ArrayList<>();
//            lineOptions = new PolylineOptions();
//            // Fetching i-th route
//            List<HashMap<String, String>> path = result.get(i);
//            // Fetching all the points in i-th route
//            for (int j = 0; j < path.size(); j++) {
//                HashMap<String, String> point = path.get(j);
//                double lat = Double.parseDouble(point.get("lat"));
//                double lng = Double.parseDouble(point.get("lng"));
//                LatLng position = new LatLng(lat, lng);
//                latS = latS + lat+",";
//                lngS = lngS + lng+",";
//                points.add(position);
//            }
//            appConfig.setLng(this.lngS);
//            appConfig.setLat(this.latS);
//            Log.d("pointss", latS+"," +lngS );
//            // Adding all the points in the route to LineOptions
//            lineOptions.addAll(points);
//            Log.d("mylog", "points" +points );
//            if (directionMode.equalsIgnoreCase("walking")) {
//                lineOptions.width(10);
//                lineOptions.color(Color.MAGENTA);
//            } else {
//                lineOptions.width(20);
//                lineOptions.color(Color.BLUE);
//            }
//            Log.d("mylog", "onPostExecute lineoptions decoded");
//        }
//        this.appConfig.setLng(lngS);
//        // Drawing polyline in the Google Map for the i-th route
//        if (lineOptions != null) {
//            //mMap.addPolyline(lineOptions);
//            taskCallback.onTaskDone(lineOptions);
//            Log.d("poly", "Option :"+ lineOptions);
//        } else {
//            Log.d("mylog", "without Polylines drawn");
//        }
//    }
//}

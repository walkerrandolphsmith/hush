package com.hush.json;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.hush.Keys;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;

 
public class AutoCompletePlaces extends ContentProvider {
 
    private static String mKey = Keys.placesKey;
    public static final String AUTHORITY = "com.hush.PlaceProvider"; 
    public static final Uri SEARCH_URI = Uri.parse("content://"+AUTHORITY+"/search"); 
    public static final Uri DETAILS_URI = Uri.parse("content://"+AUTHORITY+"/details");
 
    private static final int SEARCH = 1;
    private static final int SUGGESTIONS = 2;
    private static final int DETAILS = 3;
  
    private static final UriMatcher mUriMatcher = buildUriMatcher();
    private static UriMatcher buildUriMatcher() {
 
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH); 
        // URI for "Go" button
        uriMatcher.addURI(AUTHORITY, "search", SEARCH ); 
        // URI for suggestions in Search Dialog
        uriMatcher.addURI(AUTHORITY, SearchManager.SUGGEST_URI_PATH_QUERY,SUGGESTIONS); 
        // URI for Details
        uriMatcher.addURI(AUTHORITY, "details",DETAILS);
 
        return uriMatcher;
    }
 
    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
        String[] selectionArgs, String sortOrder) {
        Cursor c = null;
 
        AutoCompletePlaceJSONParser parser = new AutoCompletePlaceJSONParser();
        AutoCompletePlaceDetailsJSONParser detailsParser = new AutoCompletePlaceDetailsJSONParser();
 
        String jsonString = "";
        String jsonPlaceDetails = "";
 
        List<HashMap<String, String>> list = null;
        List<HashMap<String, String>> detailsList = null;

        MatrixCursor mCursor = null;
 
        switch(mUriMatcher.match(uri)){
        case SEARCH:
            mCursor = new MatrixCursor(new String[] { "_id", "description","lat","lng" });
             parser = new AutoCompletePlaceJSONParser();
             detailsParser = new AutoCompletePlaceDetailsJSONParser();
  
            jsonString = getPlaces(selectionArgs);
            try {

                list = parser.parse(new JSONObject(jsonString));
                for(int i=0;i<list.size();i++){
                    HashMap<String, String> hMap = (HashMap<String, String>) list.get(i);
                    
                    detailsParser =new AutoCompletePlaceDetailsJSONParser();
                    jsonPlaceDetails = getPlaceDetails(hMap.get("reference"));
 
                    detailsList = detailsParser.parse(new JSONObject(jsonPlaceDetails));
                    for(int j=0;j<detailsList.size();j++){
                        HashMap<String, String> hMapDetails = detailsList.get(j);
                        mCursor.addRow(new String[]{ Integer.toString(i), hMap.get("description") , hMapDetails.get("lat") , hMapDetails.get("lng") });
                    }
 
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            c = mCursor;
            break;
 
        case SUGGESTIONS :
             mCursor = new MatrixCursor(new String[] { "_id", SearchManager.SUGGEST_COLUMN_TEXT_1, SearchManager.SUGGEST_COLUMN_INTENT_EXTRA_DATA } );
 
            parser = new AutoCompletePlaceJSONParser(); 
            jsonString = getPlaces(selectionArgs);
 
            try {
                list = parser.parse(new JSONObject(jsonString));
                
                for(int i=0;i<list.size();i++){
                    HashMap<String, String> hMap = (HashMap<String, String>) list.get(i);
                    
                    mCursor.addRow(new String[] { Integer.toString(i), hMap.get("description"), hMap.get("reference") });
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            c = mCursor;
            break;
 
        case DETAILS :
            mCursor = new MatrixCursor(new String[] { "_id", "description","lat","lng" });
 
            detailsParser = new AutoCompletePlaceDetailsJSONParser();
            jsonPlaceDetails = getPlaceDetails(selectionArgs[0]);
            try {
                detailsList = detailsParser.parse(new JSONObject(jsonPlaceDetails));
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
 
            for(int j=0;j<detailsList.size();j++){
                HashMap<String, String> hMapDetails = detailsList.get(j);
                mCursor.addRow(new String[]{ Integer.toString(j), hMapDetails.get("formatted_address") , hMapDetails.get("lat") , hMapDetails.get("lng") });
            }
            c = mCursor;
            break;
        }
        return c;
    }
 
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // TODO Auto-generated method stub
        return 0;
    }
 
    @Override
    public String getType(Uri uri) {
        // TODO Auto-generated method stub
        return null;
    }
 
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // TODO Auto-generated method stub
        return null;
    }
 
    @Override
    public boolean onCreate() {
        // TODO Auto-generated method stub
        return false;
    }
 
    @Override
    public int update(Uri uri, ContentValues values, String selection,
        String[] selectionArgs) {
        // TODO Auto-generated method stub
        return 0;
    }
 
    private String downloadUrl(String strUrl) throws IOException{
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);
 
            urlConnection = (HttpURLConnection) url.openConnection(); 
            urlConnection.connect();
 
            iStream = urlConnection.getInputStream(); 
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream)); 
            StringBuffer sb = new StringBuffer();
 
            String line = "";
            while( ( line = br.readLine()) != null){
                sb.append(line);
            }
 
            data = sb.toString(); 
            br.close();
 
        }catch(Exception e){
        }finally{
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }
 
    private String getPlaceDetailsUrl(String ref){
 
        String reference = "reference="+ref;
        String sensor = "sensor=false";
        String key = "key="+mKey;
        String parameters = reference+"&"+sensor+"&"+key;
        String output = "json";
        String url = "https://maps.googleapis.com/maps/api/place/details/"+output+"?"+parameters;
 
        return url;
    }
 
    private String getPlacesUrl(String qry){
 
        try {
            qry = "input=" + URLEncoder.encode(qry, "utf-8");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        String sensor = "sensor=false";
        String types = "types=geocode";
        String key = "key="+mKey;
        String parameters = qry+"&"+types+"&"+sensor+"&"+key;
        String output = "json";

        String url = "https://maps.googleapis.com/maps/api/place/autocomplete/"+output+"?"+parameters;
        return url;
    }
 
    private String getPlaces(String[] params){
        String data = "";
        String url = getPlacesUrl(params[0]);
        try{
            data = downloadUrl(url);
        }catch(Exception e){
        }
        return data;
    }
 
    public String getPlaceDetails(String reference){
        String data = "";
        String url = getPlaceDetailsUrl(reference);
        try {
            data = downloadUrl(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }
}
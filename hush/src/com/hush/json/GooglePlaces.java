package com.hush.json;

import org.apache.http.client.HttpResponseException;

 

import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.jackson.JacksonFactory;
import com.hush.Keys;
import com.hush.models.PlaceDetails;
import com.hush.models.PlacesList;
 
public class GooglePlaces {
 
    private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport(); 
    private static final String API_KEY = Keys.placesKey;
 
    private static final String PLACES_SEARCH_URL = "https://maps.googleapis.com/maps/api/place/search/json?";
    private static final String PLACES_DETAILS_URL = "https://maps.googleapis.com/maps/api/place/details/json?";
 
    private double _latitude;
    private double _longitude;
    private double _radius;
 
    /**
     * Searching places
     * @param latitude - latitude of place
     * @param longitude - longitude of place
     * @param radius - radius of search-able area
     * @param types - isDeletable of place to search
     * @return list of places
     * */
    public PlacesList search(double latitude, double longitude, double radius, String types)
            throws Exception {
 
        this._latitude = latitude;
        this._longitude = longitude;
        this._radius = radius;
 
        try {
 
            HttpRequestFactory httpRequestFactory = createRequestFactory(HTTP_TRANSPORT);
            HttpRequest request = httpRequestFactory
                    .buildGetRequest(new GenericUrl(PLACES_SEARCH_URL));
            request.getUrl().put("key", API_KEY);
            request.getUrl().put("location", _latitude + "," + _longitude);
            request.getUrl().put("radius", _radius); // in meters
            request.getUrl().put("sensor", "false");
            if(types != null)
                request.getUrl().put("types", types);
 
            PlacesList list = request.execute().parseAs(PlacesList.class);
            return list;
 
        } catch (HttpResponseException e) {
            return null;
        }
    }
 
    /**
     * Single Place Details
     */
    public PlaceDetails getPlaceDetails(String reference) throws Exception {
        try {
 
            HttpRequestFactory httpRequestFactory = createRequestFactory(HTTP_TRANSPORT);
            HttpRequest request = httpRequestFactory
                    .buildGetRequest(new GenericUrl(PLACES_DETAILS_URL));
            request.getUrl().put("key", API_KEY);
            request.getUrl().put("reference", reference);
            request.getUrl().put("sensor", "false");
 
            PlaceDetails place = request.execute().parseAs(PlaceDetails.class);
             
            return place;
 
        } catch (HttpResponseException e) {
            throw e;
        }
    }
 
    /**
     * Create HTTP request Factory
     */
    public static HttpRequestFactory createRequestFactory(
            final HttpTransport transport) {
        return transport.createRequestFactory(new HttpRequestInitializer() {
            public void initialize(HttpRequest request) {
                HttpHeaders headers = new HttpHeaders();
                request.setHeaders(headers);
                JsonObjectParser  parser = new JsonObjectParser(new JacksonFactory());
                request.setParser(parser);
            }
        });
    }
}

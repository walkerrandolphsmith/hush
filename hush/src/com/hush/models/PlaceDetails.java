package com.hush.models;

 
public class PlaceDetails {

    public String status;
     
    public Place result;
 
    @Override
    public String toString() {
        if (result!=null) {
            return result.toString();
        }
        return super.toString();
    }
}
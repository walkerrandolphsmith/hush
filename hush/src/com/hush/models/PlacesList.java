package com.hush.models;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.api.client.util.Key;
 
public class PlacesList implements Parcelable {

	@Key
    public String status;
 
    @Key
    public List<Place> results;

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeString(status);
		dest.writeList(results);
		
	}
	
	public PlacesList(){
		results =  new ArrayList<Place>();
	}

	private PlacesList (Parcel in){
		status = in.readString(); 
		results =  new ArrayList<Place>();
		in.readList(results, Place.class.getClassLoader());
	}
	
	public static final Parcelable.Creator<PlacesList> CREATOR = new Parcelable.Creator<PlacesList>(){
		public PlacesList createFromParcel(Parcel in){
			return new PlacesList(in);
		}
		
		public PlacesList[] newArray(int size){
			return new PlacesList[size];
		}
	};
}
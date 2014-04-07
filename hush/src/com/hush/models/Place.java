package com.hush.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.api.client.util.Key;

public class Place implements Parcelable {

	@Key
	public String id;

	@Key
	public String name;

	@Key
	public String reference;

	@Key
	public String icon;

	@Key
	public String vicinity;

	@Key
	public Geometry geometry;

	@Key
	public String formatted_address;

	@Key
	public String formatted_phone_number;

	public int isArchived;
	
	public int radius;

	@Override
	public String toString() {
		return name + " - " + id + " - " + reference;
	}

	public static class Geometry implements Parcelable {
		@Key
		public Location location;

		@Override
		public int describeContents() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			// TODO Auto-generated method stub
			dest.writeParcelable(location, flags);
		}

		public Geometry() {

		}

		private Geometry(Parcel in) {
			location = in.readParcelable(com.hush.models.Place.Location.class
					.getClassLoader());

		}

		public static final Parcelable.Creator<Geometry> CREATOR = new Parcelable.Creator<Geometry>() {
			@Override
			public Geometry createFromParcel(Parcel in) {
				return new Geometry(in);
			}

			public Geometry[] newArray(int size) {
				return new Geometry[size];
			}

		};
	}

	public static class Location implements Parcelable {

		@Key
		public double lat;

		@Key
		public double lng;

		@Override
		public int describeContents() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			// TODO Auto-generated method stub
			dest.writeDouble(lat);
			dest.writeDouble(lng);
		}

		public Location() {

		}

		private Location(Parcel in) {
			lat = in.readDouble();
			lng = in.readDouble();

		}

		public static final Parcelable.Creator<Location> CREATOR = new Parcelable.Creator<Location>() {
			@Override
			public Location createFromParcel(Parcel in) {
				return new Location(in);
			}

			public Location[] newArray(int size) {
				return new Location[size];
			}

		};

	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeString(id);
		dest.writeString(name);
		dest.writeString(reference);
		dest.writeString(icon);
		dest.writeString(vicinity);
		dest.writeParcelable(geometry, flags);
		dest.writeString(formatted_address);
		dest.writeString(formatted_phone_number);
		dest.writeInt(radius);
	}

	public Place() {
		this.geometry = new Geometry();
		this.geometry.location = new com.hush.models.Place.Location();

		id = "";
		name = "Place";
		reference = "";
		icon = "";
		vicinity = "Address";
		formatted_address = "";
		formatted_phone_number = "";
		isArchived = -1;		
		radius = 128;
	}

	private Place(Parcel in) {
		id = in.readString();
		name = in.readString();
		reference = in.readString();
		icon = in.readString();
		vicinity = in.readString();
		geometry = in.readParcelable(Geometry.class.getClassLoader());
		formatted_address = in.readString();
		formatted_phone_number = in.readString();
		radius = in.readInt();
	}

	public static final Parcelable.Creator<Place> CREATOR = new Parcelable.Creator<Place>() {
		@Override
		public Place createFromParcel(Parcel in) {
			return new Place(in);
		}

		public Place[] newArray(int size) {
			return new Place[size];
		}

	};
}
package com.hush.sqlite;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.hush.models.Place;
import com.hush.models.Place.Geometry;
 
public class PlacesDatabaseHandler extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "placesManager";

	// Places table name
	private static final String TABLE_PLACES = "places";

	// Places Table Columns names
	public final String KEY_ID = "_id";
	public final String KEY_NAME = "name";
	public final String KEY_ADDRESS = "address";
	public final String KEY_PHONE = "phone";
	public final String KEY_LONGITUDE = "longitude";
	public final String KEY_LATITUDE = "latitude";
	public final String KEY_ISARCHIVED = "archived";
	public final String KEY_RADIUS = "radius";
 
    public PlacesDatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
 
    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_PLACES_TABLE = "CREATE TABLE " + TABLE_PLACES + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," 
        		+ KEY_NAME + " TEXT,"
                + KEY_ADDRESS + " TEXT," 
                + KEY_PHONE + " TEXT,"
                + KEY_LONGITUDE + " INTEGER," 
        		+ KEY_LATITUDE + " INTEGER," 
                + KEY_ISARCHIVED + " REAL,"
                + KEY_RADIUS + " INTEGER"
        		+ ")";
        db.execSQL(CREATE_PLACES_TABLE);
       // updateKeys(db);
    }
 
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older table if existed
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_PLACES);
		// Create tables again
		onCreate(db);
	}
 
    /**
     * All CRUD Operations
     */
 
	// Create
	public void addPlace(Place place) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_NAME, place.name);
		values.put(KEY_ADDRESS, place.vicinity); // Place Address
		values.put(KEY_PHONE, place.formatted_phone_number);
		values.put(KEY_LONGITUDE, place.geometry.location.lng);
		values.put(KEY_LATITUDE, place.geometry.location.lat);
		values.put(KEY_ISARCHIVED, place.isArchived); // Place Archive Status
		values.put(KEY_RADIUS, place.radius);

		// Inserting Row
		db.insert(TABLE_PLACES, null, values);
		db.close();
	}
 
	// Read
	Place getPlace(int id) {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_PLACES, new String[] { KEY_ID, KEY_NAME,
				KEY_ADDRESS, KEY_LONGITUDE, KEY_LATITUDE, KEY_ISARCHIVED, KEY_RADIUS },
				KEY_ID + "=?", new String[] { String.valueOf(id) }, null, null,
				null, null);
		if (cursor != null)
			cursor.moveToFirst();

		Place place = new Place();
		place.id = cursor.getString(0);
		place.name = cursor.getString(1);
		place.vicinity = cursor.getString(2);
		place.formatted_phone_number = cursor.getString(3);
		place.geometry.location.lng = Double.parseDouble(cursor.getString(4));
		place.geometry.location.lat = Double.parseDouble(cursor.getString(5));
		place.isArchived = Integer.parseInt((cursor.getString(6)));
		place.radius = Integer.parseInt(cursor.getString(7));

		return place;
	}
     
    // Read All
    public List<Place> getAllPlaces() {
        List<Place> placeList = new ArrayList<Place>();
        String selectQuery = "SELECT  * FROM " + TABLE_PLACES;
 
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
 
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Place place = new Place();
                place.geometry = new Geometry();
    			place.geometry.location = new com.hush.models.Place.Location();
    			
                place.id = cursor.getString(0);
                place.name = cursor.getString(1);
                place.vicinity = cursor.getString(2);
                place.formatted_phone_number = cursor.getString(3);
                place.geometry.location.lng = Double.parseDouble(cursor.getString(4));
                place.geometry.location.lat = Double.parseDouble(cursor.getString(5));
                place.isArchived = Integer.parseInt(cursor.getString(6));
                place.radius = Integer.parseInt(cursor.getString(7));
                
                placeList.add(place);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return placeList;
    }
    
	// Read All
	public Cursor fetchAllPlaces() {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery("select rowid _id,* from " + TABLE_PLACES,
				null);
		return cursor;
	}
    
	// Update
	public int updatePlace(Place place) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_NAME, place.name);
		values.put(KEY_ADDRESS, place.vicinity);
		values.put(KEY_LONGITUDE, place.geometry.location.lng);
		values.put(KEY_LATITUDE, place.geometry.location.lat);
		values.put(KEY_ISARCHIVED, place.isArchived);
		values.put(KEY_RADIUS, place.radius);

		// updating row
		return db.update(TABLE_PLACES, values, KEY_ID + " = ?",
				new String[] { String.valueOf(place.id) });
	}
 
	// Delete
	public void deletePlace(int primaryKey) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_PLACES, KEY_ID + " = ?",
				new String[] { String.valueOf(primaryKey) });
		db.close();
	}
 
	// Count
	public int getPlacesCount() {
		String countQuery = "SELECT  * FROM " + TABLE_PLACES;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		cursor.close();

		// return place
		return cursor.getCount();
	}
	
	public void updateKeys(SQLiteDatabase db){
		String updateKeysQuery = "UPDATE sqlite_sequence SET seq = 0 WHERE name = " + TABLE_PLACES;
        db.execSQL(updateKeysQuery);
	
	}
    
	public void printContentOfPlacesDb(){
	     List<Place> places = getAllPlaces();       
	         
	     for (Place p : places) {
	        String log = "Id: " 
	           			+ p.id
	           			+ " ,Name: " 
	           			+ p.name 
	           			+ " ,Address: " 
	           			+ p.vicinity
	           			+ " ,Phone: " 
	           			+ p.formatted_phone_number
						+ " ,Longitude: " 
						+ p.geometry.location.lng
						+ " ,Latitude: " 
						+ p.geometry.location.lat
					    + " ,Archived: " 
					    + p.isArchived
					    + " ,Radius: "
					    + p. radius;
	   }
	} 
}
package com.hush;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.AudioManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.SearchView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationChangeListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.hush.json.AutoCompletePlaces;
import com.hush.json.GooglePlaces;
import com.hush.models.Place;
import com.hush.models.PlacesList;
import com.hush.sqlite.PlacesDatabaseHandler;

public class MainActivity extends SherlockFragmentActivity implements
		LoaderCallbacks<Cursor>, OnInfoWindowClickListener,
		OnMarkerDragListener, OnMyLocationChangeListener {

	/*********************************
	 * Constants and Class members
	 **********************************/
	private String queryString = "";

	private PlacesDatabaseHandler db;

	private GoogleMap mGoogleMap = null;
	private CanvasView mCanvas;
	private double lat = 32.51925;
	private double lng = -84.883080;

	private ActionBar mAction;
	private ActionMode mActionMode;
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;

	private AudioManager audioManager;

	private String[] mNavItemValue = null;
	private String[] mNavItemLabel = null;

	private ProgressDialog progressDialog;
	private TextView assistanceTextView;

	private boolean isTouchable;
	private boolean isListDeletable;
	public PlacesList places = new PlacesList();
	private HashMap<Marker, Place> place_markers;
	private HashMap<Marker, Circle> proximity_rings;

	/*********************************
	 * OnCreate
	 **********************************/

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		isTouchable = false;
		isListDeletable = false;

		db = new PlacesDatabaseHandler(this);
		audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

		/* Begin Construction of Navigation List */
		mNavItemValue = getResources().getStringArray(R.array.place_type);
		mNavItemLabel = getResources().getStringArray(R.array.place_type_name);
		ArrayAdapter<String> navigation_adapter = new CustomNavigationAdapter(
				this, R.layout.each_nav_item, mNavItemLabel,
				CustomNavigationAdapter.mNavItemIcon);
		/* End Construction of Navigation List */

		mAction = getSupportActionBar();
		mAction.setHomeButtonEnabled(true);
		mAction.setDisplayHomeAsUpEnabled(true);
		
		mActionMode = null;

		assistanceTextView = (TextView) findViewById(R.id.assistance_text);
		assistanceTextView.setGravity(Gravity.CENTER_VERTICAL
				| Gravity.CENTER_HORIZONTAL);

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.drawer_list);

		mDrawerList.setAdapter(navigation_adapter);

		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				R.drawable.ic_drawer, R.string.drawer_open,
				R.string.drawer_close) {

			public void onDrawerClosed(View view) {
				super.onDrawerClosed(view);
			}

			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);

			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);
		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

		int status = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(getBaseContext());

		if (status != ConnectionResult.SUCCESS) {
			int requestCode = 10;
			Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this,
					requestCode);
			dialog.show();
		} else {
			SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager()
					.findFragmentById(R.id.map);
			mGoogleMap = fm.getMap();
			LatLng latLng = new LatLng(lat, lng);
			mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
			mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(12));
			mGoogleMap.setMyLocationEnabled(true);
			mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
			mGoogleMap.setOnCameraChangeListener(new MapPanListener());
			mGoogleMap.setOnMyLocationChangeListener(this);

			mCanvas = (CanvasView) findViewById(R.id.canvas);
			mCanvas.setBackgroundColor(Color.TRANSPARENT);

			Button current_location_button = (Button) findViewById(R.id.current);
			Button draw_button = (Button) findViewById(R.id.draw);

			current_location_button.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					v.setSelected(true);
					Location pos = mGoogleMap.getMyLocation();
					if(pos != null){
						LatLng p = new LatLng(pos.getLatitude(), pos.getLongitude());
						mGoogleMap.animateCamera(
								CameraUpdateFactory.newLatLngZoom(p, 15), 500,
								new GoogleMap.CancelableCallback() {
	
									@Override
									public void onCancel() {
									}
	
									@Override
									public void onFinish() {
									}
								});
					}
				}
			});

			draw_button.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					assistanceTextView
							.setText("Draw a circle by 'pinching out'. Add by clicking Draw.");
					mActionMode = startActionMode(mActionModeCallback);
					mCanvas.setTouchability(mCanvas.MODE_DRAWING);
				}
			});
		}
		handleIntent(getIntent());
	}

	/*********************************
	 * MainActivity Behavior
	 **********************************/

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
		getCurrentLocation();
		if (isListDeletable) {
			populateMyPlaceMarkers();
		}

	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		handleIntent(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.main, menu);
		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		SearchView searchView = (SearchView) menu.findItem(R.id.action_search)
				.getActionView();
		searchView.setSearchableInfo(searchManager
				.getSearchableInfo(getComponentName()));
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_search:
			onSearchRequested();
			break;
		case R.id.action_list:
			Intent in = new Intent(getApplicationContext(), CustomListActivity.class);
			in.putExtra("isDeletable", isListDeletable);
			in.putExtra("places", places);
			startActivity(in);
		}
		return super.onMenuItemSelected(featureId, item);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if (item.getItemId() == android.R.id.home) {

			if (mDrawerLayout.isDrawerOpen(mDrawerList)) {
				mDrawerLayout.closeDrawer(mDrawerList);
			} else {
				mDrawerLayout.openDrawer(mDrawerList);
			}
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	/*********************************
	 * Fragment Manger
	 **********************************/

	@Override
	public void onBackPressed() {
		finish();
	}

	/*********************************
	 * Progress Dialogs
	 **********************************/

	public ProgressDialog initiateProgressDialog() {
		ProgressDialog progress = new ProgressDialog(this,
				R.style.CutomAlertDialogLoadMarkers);
		progress.show();
		return progress;
	}

	public void dismissProgressDialog(ProgressDialog progressDialog) {
		progressDialog.dismiss();
	}

	/*********************************
	 * Navigation Bar
	 **********************************/

	private class DrawerItemClickListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			selectItem(position);
		}
	}

	private void selectItem(int position) {

		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

		if (position >= 3) {
			retreivePlacesResultGiven(position, mNavItemValue[position]);
			populateMarkers(position, places);
		} else {
			switch (position) {
			case 0: // Settings
				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=nWFq_oRIXKk")));
				break;
			case 1: // My Places
				populateMyPlaceMarkers();
				break;
			case 2: // Current Location
				isListDeletable = false;
				getCurrentLocation();
				clearPlacesList();
				Place current_location = new Place();

				current_location.geometry.location.lng = lng;
				current_location.geometry.location.lat = lat;

				reverseGeocodePlace(current_location);

				places.results.add(current_location);
				populateMarkers(position, places);
				assistanceTextView.setText("Current Location");
				break;
			}
		}
		ft.commit();
		mDrawerList.setItemChecked(position, true);
		mDrawerLayout.closeDrawer(mDrawerList);
	}

	/*********************************
	 * CAB -- Action Mode -- Drawing
	 **********************************/

	private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			MenuInflater inflater = mode.getMenuInflater();
			inflater.inflate(R.menu.action_mode, menu);
			return true;
		}

		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			return false;
		}

		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			switch (item.getItemId()) {
			case R.id.action_draw:
				Point p = new Point(mCanvas.mCircleDrawable.getBounds()
						.centerX(), mCanvas.mCircleDrawable.getBounds()
						.centerY());

				LatLng area = mGoogleMap.getProjection().fromScreenLocation(p);
				int radius = mCanvas.mCircleDrawable.getBounds().right;

				mGoogleMap.addCircle(createProximityRing(area, radius));

				Place place_from_proximity_ring = new Place();

				place_from_proximity_ring.geometry.location.lat = area.latitude;
				place_from_proximity_ring.geometry.location.lng = area.longitude;
				place_from_proximity_ring.radius = radius;

				reverseGeocodePlace(place_from_proximity_ring);
				db.addPlace(place_from_proximity_ring);

				return true;
			default:
				return false;
			}
		}

		@Override
		public void onDestroyActionMode(ActionMode mode) {
			mActionMode = null;
			mCanvas.clear();
			mCanvas.setTouchability(mCanvas.MODE_NOT_DRAWING);
			populateMyPlaceMarkers();
		}
	};

	/*********************************
	 * Google Map and Markers
	 **********************************/

	public boolean isMapAvailable() {
		boolean result = true;
		if (mGoogleMap == null) {
			result = false;
		}
		return result;
	}

	public void clearPlacesList() {
		places = new PlacesList();
	}

	public void populateMyPlaceMarkers() {
		isListDeletable = true;
		places.results = db.getAllPlaces();
		populateMarkers(1, places);
		assistanceTextView.setText(places.results.size()
				+ " Results\t My Places");
	}

	public void populateMarkers(int resource, PlacesList places) {

		if (isMapAvailable()) {
			mGoogleMap.clear();

			place_markers = new HashMap<Marker, Place>();
			proximity_rings = new HashMap<Marker, Circle>();
			Marker m = null;
			for (int i = 0; i < places.results.size(); i++) {
				m = placeMarker(resource, places.results.get(i));
				place_markers.put(m, places.results.get(i));
				// Only display proximity rings for my places
				if (isListDeletable) {
					Circle ring = mGoogleMap.addCircle(createProximityRing(
							m.getPosition(), places.results.get(i).radius));
					proximity_rings.put(m, ring);
				}
			}
			if (m != null) {
				CameraUpdate cameraPosition = CameraUpdateFactory.newLatLng(m
						.getPosition());
				mGoogleMap.animateCamera(cameraPosition);
			}
			mGoogleMap.setOnInfoWindowClickListener(this);
			mGoogleMap.setOnMarkerDragListener(this);
		}
	}

	public Marker placeMarker(int resource, Place p) {
		MarkerOptions markerOptions = new MarkerOptions()
				.position(
						new LatLng(p.geometry.location.lat,
								p.geometry.location.lng)).title(p.name)
				.draggable(isListDeletable);
		if (resource == -1) {
			markerOptions.icon(BitmapDescriptorFactory
					.fromResource(R.drawable.nav_search));
		} else {
			markerOptions
					.icon(BitmapDescriptorFactory
							.fromResource(CustomNavigationAdapter.mNavItemIcon[resource]));
		}
		Marker m = mGoogleMap.addMarker(markerOptions);
		return m;
	}

	private CircleOptions createProximityRing(LatLng point, int radius) {
		CircleOptions circleOptions = new CircleOptions();
		circleOptions.center(point);
		circleOptions.radius(radius);
		circleOptions.strokeColor(0x0);
		circleOptions.fillColor(0x110000FF);
		circleOptions.strokeWidth(2);
		return circleOptions;
	}

	@Override
	public void onMarkerDrag(Marker marker) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onMarkerDragEnd(Marker marker) {
		// TODO Auto-generated method stub
		LatLng position = marker.getPosition();
		Place p = place_markers.get(marker);
		Circle ring = proximity_rings.get(marker);

		p.geometry.location.lat = position.latitude;
		p.geometry.location.lng = position.longitude;
		
		reverseGeocodePlace(p);

		ring.setCenter(position);
		db.updatePlace(p);
	}

	@Override
	public void onMarkerDragStart(Marker marker) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onInfoWindowClick(Marker marker) {
		Place p = place_markers.get(marker);
		if (isListDeletable) {
			Circle ring = proximity_rings.get(marker);
			marker.remove();
			ring.remove();

			places.results.remove(p);
			db.deletePlace(Integer.parseInt(p.id));
			assistanceTextView.setText(places.results.size()
					+ " Results\t My Places");
		} else {
			db.addPlace(p);
		}
	}

	public class MapPanListener implements GoogleMap.OnCameraChangeListener {
		@Override
		public void onCameraChange(CameraPosition position) {
		}
	}

	/*********************************
	 * Retrieve Address
	 **********************************/

	public void reverseGeocodePlace(Place p) {
		StringBuilder address_as_string = new StringBuilder("");
		try {
			Geocoder geocoder = new Geocoder(getBaseContext(),
					Locale.getDefault());
			List<Address> addresses = geocoder.getFromLocation(
					p.geometry.location.lat, p.geometry.location.lng, 1);
			if (addresses.size() > 0) {
				address_as_string = new StringBuilder();
				Address address = addresses.get(0);
				int maxIndex = address.getMaxAddressLineIndex();
				for (int x = 0; x <= maxIndex; x++) {
					if (x == 0) {
						p.name = address.getAddressLine(x);
					} else {
						address_as_string.append(address.getAddressLine(x));
						address_as_string.append(",");
					}
				}
				p.vicinity = address_as_string.toString();
				if (p.name.isEmpty()) {
					p.name = address.getLocality();
				}
				if (p.vicinity.isEmpty()) {
					p.vicinity = address.getLocality();
				}
			}
		} catch (IOException e) {
			System.out.println("Address was not parsed correctly");
		}
	}

	/*********************************
	 * Menu Search
	 **********************************/

	private void handleIntent(Intent intent) {
		if (intent.getAction() != null) {
			if (intent.getAction().equals(Intent.ACTION_SEARCH)) {
				doSearch(intent.getStringExtra(SearchManager.QUERY));
			} else if (intent.getAction().equals(Intent.ACTION_VIEW)) {
				getPlace(intent.getStringExtra(SearchManager.EXTRA_DATA_KEY));
			}
		}// else splash screen intent
	}

	private void getPlace(String query) {
		Bundle data = new Bundle();
		data.putString("query", query);
		getSupportLoaderManager().restartLoader(1, data, this);
	}

	private void doSearch(String query) {
		Bundle data = new Bundle();
		data.putString("query", query);
		getSupportLoaderManager().restartLoader(0, data, this);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int uriType, Bundle query) {
		
		CursorLoader cLoader = null;
		if (uriType == 0)
			cLoader = new CursorLoader(getBaseContext(),
					AutoCompletePlaces.SEARCH_URI, null, null,
					new String[] { query.getString("query") }, null);
		else if (uriType == 1)
			cLoader = new CursorLoader(getBaseContext(),
					AutoCompletePlaces.DETAILS_URI, null, null,
					new String[] { query.getString("query") }, null);
		return cLoader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor c) {
		int count = c.getCount();
		isListDeletable = false;
		clearPlacesList();
		
		if (count > 0) {
			while (c.moveToNext()) {
				Place menu_searched_place = new Place();

				menu_searched_place.id = "-1";
				menu_searched_place.name = c.getString(1);
				menu_searched_place.vicinity = "";
				menu_searched_place.geometry.location.lng = Double
						.parseDouble(c.getString(3));
				menu_searched_place.geometry.location.lat = Double
						.parseDouble(c.getString(2));
				places.results.add(menu_searched_place);
			}
			populateMarkers(-1, places);
			
			int size = places.results.size();			
			if(size > 0)
				assistanceTextView.setText(size + " Results\t " + places.results.get(0).name);
			else
				assistanceTextView.setText(size + " Results");
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		// TODO Auto-generated method stub
	}
	
	/*********************************
	 * Format String
	 **********************************/

	public String formatString(String s) {
		s = s.replace('_', ' ');
		String[] words = s.split(" ");
		s = "";
		for (String word : words) {
			word = Character.toUpperCase(word.charAt(0)) + word.substring(1);
			s += word + " ";
		}
		return s;
	}

	/*********************************
	 * Google Places Search by Type
	 **********************************/

	public void retreivePlacesResultGiven(int position, String type) {
		queryString = type;
		new LoadPlaces(this, position).execute();
		progressDialog = initiateProgressDialog();
	}

	public class LoadPlaces extends AsyncTask<String, String, String> {
		public MainActivity mainActivity;
		public int position;
		PlacesList nearPlaces;

		public LoadPlaces(MainActivity mainActivity, int position) {
			this.mainActivity = mainActivity;
			this.position = position;
		}

		protected String doInBackground(String... args) {
			GooglePlaces googlePlaces = new GooglePlaces();
			try {
				String types = queryString;
				double radius = 10000; // meters

				nearPlaces = googlePlaces.search(lat, lng, radius, types);

			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		protected void onPostExecute(String file_url) {
			String formatted_type = formatString(queryString);
			isListDeletable = false;
			clearPlacesList();
			
			String status = "";			
			if (nearPlaces != null) {
				status = nearPlaces.status;
			}
			// Check for all possible status
			if (status.equals("OK")) {
				if (nearPlaces.results != null) {					
					places = nearPlaces;
					assistanceTextView.setText(places.results.size()
							+ " Results\t " + formatted_type);
				}
			} else if (status.equals("ZERO_RESULTS")) {
				assistanceTextView.setText("0 Results\t " + formatted_type);
			} else if (status.equals("UNKNOWN_ERROR")) {
				assistanceTextView.setText("0 Results\t " + formatted_type);
			} else if (status.equals("OVER_QUERY_LIMIT")) {
				assistanceTextView.setText("Over search limit. Try again later.");
			} else if (status.equals("REQUEST_DENIED")) {
				assistanceTextView.setText("0 Results\t " + formatted_type);
			} else if (status.equals("INVALID_REQUEST")) {
				assistanceTextView.setText("0 Results\t " + formatted_type);
			} else {				
				assistanceTextView.setText("0 Results\t " + formatted_type);
			}
			dismissProgressDialog(progressDialog);
			populateMarkers(position, places);
		}
	}

	/*********************************
	 * Retrieve Location
	 **********************************/

	protected boolean getCurrentLocation() {
		boolean result = false;
		Location location = null;
		if (isMapAvailable())
			location = mGoogleMap.getMyLocation();
		if (location != null) {
			result = true;
			lng = location.getLongitude();
			lat = location.getLatitude();
		}
		return result;
	}

	@Override
	public void onMyLocationChange(Location current_location) {
		// TODO Auto-generated method stub
		ArrayList<Place> places = (ArrayList<Place>) db.getAllPlaces();
		boolean isInProxTo = false;

		for (Place place : places) {
			Location l = new Location("PLACE");
			l.setLatitude(place.geometry.location.lat);
			l.setLongitude(place.geometry.location.lng);

			float distance = current_location.distanceTo(l);
			if (distance < place.radius) {
				isInProxTo = true;
			}
		}
		if (isInProxTo) {
			audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
		} else {
			audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
		}
	}
}

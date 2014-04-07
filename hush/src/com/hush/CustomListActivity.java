package com.hush;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.hush.models.Place;
import com.hush.models.PlacesList;
import com.hush.sqlite.PlacesDatabaseHandler;

public class CustomListActivity extends SherlockListActivity {

	// ArrayList that will hold the original Data
	LayoutInflater inflater;
	ActionBar mAction;
	boolean isDeletable;
	boolean isEmpty = false;

	public PlacesList places;
	public Place[] _places;

	PlacesDatabaseHandler db;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list);

		Intent in = getIntent();
		isDeletable = in.getExtras().getBoolean("isDeletable");
		places = (PlacesList) in.getParcelableExtra("places");

		inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		db = new PlacesDatabaseHandler(this);

		mAction = getSupportActionBar();
		mAction.setHomeButtonEnabled(true);
		mAction.setDisplayHomeAsUpEnabled(true);

		/*
		 * Integer[] photos={R.drawable., R.drawable.torres,R.drawable.,
		 * R.drawable.drogba,R.drawable., R.drawable.rooney,R.drawable.};
		 */

		TextView chkAllLabel = (TextView) findViewById(R.id.selectAllCheckBoxLabel);
		CheckBox chkAll = (CheckBox) findViewById(R.id.selectAllCheckBox);

		if (!(places.results.size() > 0)) {
			isEmpty = true;
			chkAllLabel.setVisibility(View.GONE);
			chkAll.setVisibility(View.GONE);
		}

		ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();

		for (Place p : places.results) {
			HashMap<String, Object> map = new HashMap<String, Object>();

			map.put("id", p.id);
			map.put("name", p.name);
			map.put("vicinity", p.vicinity);
			map.put("lng", p.geometry.location.lng);
			map.put("lat", p.geometry.location.lat);

			list.add(map);

		}

		final CustomListAdapter adapter = new CustomListAdapter(this,
				R.layout.each_place, list, chkAll);
		setListAdapter(adapter);
		// registerForContextMenu(getListView());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (!isEmpty) {
			getSupportMenuInflater().inflate(R.menu.list, menu);
			MenuItem item = menu.findItem(R.id.action);

			if (isDeletable)
				item.setTitle("Delete");
			else
				item.setTitle("Add");
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if (item.getItemId() == android.R.id.home) {
			finish();
		}

		if (item.getItemId() == R.id.action) {
			CustomListAdapter la = (CustomListAdapter) getListAdapter();
			boolean[] checkBoxState = la.checkBoxState;
			int k = 0;// Number of items removed
			for (int i = 0; i < checkBoxState.length; i++) {
				if (checkBoxState[i]) {
					if (isDeletable) {
						db.deletePlace(Integer.parseInt(places.results.get(i
								- k).id));

						la.remove(la.getItem(i - k));
						la.notifyDataSetChanged();
						places.results.remove(i - k);
						k++;
					} else {
						db.addPlace(places.results.get(i));
					}
				}
			}
			la.checkBoxState = new boolean[la.getCount()];
		}
		return super.onOptionsItemSelected(item);
	}

	/*
	 * @Override public void onCreateContextMenu(ContextMenu menu, View v,
	 * ContextMenuInfo menuInfo) { AdapterView.AdapterContextMenuInfo info =
	 * (AdapterView.AdapterContextMenuInfo) menuInfo; long id =
	 * getListAdapter().getItemId(info.position); menu.add(0, 0, 0,
	 * R.string.action_select_all); }
	 */
}
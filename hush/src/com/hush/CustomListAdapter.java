package com.hush;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

public class CustomListAdapter extends ArrayAdapter<HashMap<String, Object>> {

	boolean[] checkBoxState;

	boolean checkAll_flag = false;
	boolean checkItem_flag = false;

	ViewHolder viewHolder;
	Activity activity;
	ArrayList<HashMap<String, Object>> places;

	public CustomListAdapter(Activity activity, int textViewResourceId,
			ArrayList<HashMap<String, Object>> places, CheckBox selectAll) {

		super(activity, textViewResourceId, places);
		this.activity = activity;
		this.places = places;
		// initial state as false
		checkBoxState = new boolean[places.size()];
		selectAll.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton selectAll, boolean isChecked) {
				allSelectedChange(isChecked);
				notifyDataSetChanged();
			}
		});
	}

	private class ViewHolder {
		// ImageView photo;
		TextView place_id, name, address, lng, lat;
		CheckBox checkBox;
	}

	private void allSelectedChange(boolean isChecked) {
		for (int i = 0; i < checkBoxState.length; i++) {
			checkBoxState[i] = isChecked;
			viewHolder.checkBox.setChecked(checkBoxState[i]);
		}
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parnet) {
		if (convertView == null) {
			convertView = activity.getLayoutInflater().inflate(R.layout.each_place,
					null);
			viewHolder = new ViewHolder();
			// viewHolder.photo=(ImageView)
			// convertView.findViewById(R.id.photo);
			viewHolder.place_id = (TextView) convertView.findViewById(R.id.place_id);
			viewHolder.name = (TextView) convertView.findViewById(R.id.name);
			viewHolder.address = (TextView) convertView
					.findViewById(R.id.address);
			viewHolder.lng = (TextView) convertView
					.findViewById(R.id.longitude);
			viewHolder.lat = (TextView) convertView.findViewById(R.id.latitude);
			viewHolder.checkBox = (CheckBox) convertView
					.findViewById(R.id.checkBox);

			convertView.setTag(viewHolder);
		} else
			viewHolder = (ViewHolder) convertView.getTag();
		// int photoId=(Integer) places.get(position).get("photo");
		// viewHolder.photo.setImageDrawable(getResources().getDrawable(photoId));
		viewHolder.place_id.setText(places.get(position).get("id").toString());
		viewHolder.name.setText(places.get(position).get("name").toString());
		viewHolder.address.setText(places.get(position).get("vicinity").toString());
		viewHolder.lng.setText(places.get(position).get("lng").toString());
		viewHolder.lat.setText(places.get(position).get("lat").toString());

		viewHolder.checkBox.setChecked(checkBoxState[position]);
		viewHolder.checkBox.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				if (((CheckBox) v).isChecked())
					checkBoxState[position] = true;
				else
					checkBoxState[position] = false;
			}
		});	
		return convertView;
	}

}

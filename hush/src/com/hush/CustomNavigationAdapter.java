package com.hush;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomNavigationAdapter extends ArrayAdapter<String> {
	
	
	static Integer [] mNavItemIcon = new Integer[]{
	   	   R.drawable.nav_demo,
		   R.drawable.nav_star,
		   R.drawable.nav_mylocation,
		   R.drawable.mt_accounting, //accounting
		   R.drawable.mt_airport, //airport
		   R.drawable.mt_amuesment_park,//Amusement park
		   R.drawable.mt_aquarium,//Aquarium				
		   R.drawable.mt_art_gallery, //art gallery
		   R.drawable.mt_atm, //atm
		   R.drawable.mt_bakery, //bakery
		   R.drawable.mt_bank, //bank 
		   R.drawable.mt_bar, //bar
		   R.drawable.mt_hair_care, //beatuy salon
		   R.drawable.mt_bicycle_shop,//bicycle store
		   R.drawable.mt_book_store, //book store
		   R.drawable.mt_bowling_alley, //bowling alley
		   R.drawable.mt_bus_station, //bus station
		   R.drawable.mt_cafe, //cafe										
		   R.drawable.mt_campgrounds, //campground
		   R.drawable.mt_car_dealer,//car dealer
		   R.drawable.mt_car_rental, //car rental
		   R.drawable.mt_car_repair, //car repair
		   R.drawable.mt_car_wash, //car wash
		   R.drawable.mt_casino, //casino
		   R.drawable.mt_cemetery, //cemetery
		   R.drawable.mt_church, //church
		   R.drawable.mt_city_hall, //city hall
		   R.drawable.mt_clothing_store, //clothing store
		   R.drawable.mt_store,  //convience store
		   R.drawable.mt_courthouse, //court house
		   R.drawable.mt_dentist, //dentist
		   R.drawable.mt_department_store, //department store
		   R.drawable.mt_doctor, //doctor
		   R.drawable.mt_electrician, //electrician
		   R.drawable.mt_electronics_store, //electronics store
		   R.drawable.mt_embassy, //embassy
		   R.drawable.mt_establishment, //establishment
		   R.drawable.mt_finance, //finance
		   R.drawable.mt_fire_station, //fire station
		   R.drawable.mt_flourist, //flourist
		   R.drawable.mt_food, //food
		   R.drawable.mt_funeral_home, //funeral home
		   R.drawable.mt_furniture, //furniture store
		   R.drawable.mt_gas_station, //gas station
		   R.drawable.mt_general_contractor, //general contractor
		   R.drawable.mt_grocery_or_supermarket, //grocery
		   R.drawable.mt_gym, //gym
		   R.drawable.mt_hair_care, //hair care
		   R.drawable.mt_hardware_store, //hardware store
		   R.drawable.mt_health, //health
		   R.drawable.mt_hindu_temple, //hindu temple
		   R.drawable.mt_home_goods_store, //home goods store
		   R.drawable.mt_hospital, //Hospital
		   R.drawable.mt_insurance_agency, //insurance agency
		   R.drawable.mt_jewelry_store, //Jewelry store
		   R.drawable.mt_laundry, //laundry
		   R.drawable.mt_lawyer, //lawyer
		   R.drawable.mt_library, //library
		   R.drawable.mt_liquor_store, //liquor store
		   R.drawable.mt_local_government_office, //local government office
		   R.drawable.mt_locksmith, //lock smith
		   R.drawable.mt_lodging, //lodging
		   R.drawable.mt_meal_delivery, //meal delivery
		   R.drawable.mt_meal_take_away, //meal take away
		   R.drawable.mt_mosque, //Mosque
		   R.drawable.mt_movie_rental, //movie rental
		   R.drawable.mt_movie_theater, //movie theater
		   R.drawable.mt_moving_company, //moving company
		   R.drawable.mt_museum, //museum
		   R.drawable.mt_night_club, //night club
		   R.drawable.mt_painter, //painter
		   R.drawable.mt_park, //park
		   R.drawable.mt_parking, //parking
		   R.drawable.mt_pet_store, //pet store
		   R.drawable.mt_pharmacy, //pharmacy
		   R.drawable.mt_physiotherapist, //physiotherapist
		   R.drawable.mt_place_of_worship, //place of worship
		   R.drawable.mt_plumber, //plumber
		   R.drawable.mt_police, //police
		   R.drawable.mt_post_office, //post office
		   R.drawable.mt_real_estate_agent, //real estate agent
		   R.drawable.mt_resturant, //resturant
		   R.drawable.mt_roofing_company, //roofing contractor
		   R.drawable.mt_rv_park, //rv park
		   R.drawable.mt_school, //school
		   R.drawable.mt_shoe_store, //shoe store
		   R.drawable.mt_shopping_mall, //shopping mall
		   R.drawable.mt_spa, //spa
		   R.drawable.mt_stadium, //stadium
		   R.drawable.mt_storage, //storage
		   R.drawable.mt_store, //store
		   R.drawable.mt_subway_station, //subway station
		   R.drawable.mt_synagogue, //synagogue
		   R.drawable.mt_taxi, //taxi stand
		   R.drawable.mt_train_station, //train_station
		   R.drawable.mt_travel_agency, //travel agency
		   R.drawable.mt_school, //university
		   R.drawable.mt_veterinary_care, //veterinary care
		   R.drawable.mt_zoo, //zoo
		   };				
	private Activity activity;
	
	String [] mNavItems;
	Integer[] mNavIcons;
	
	public CustomNavigationAdapter(Activity a, int resource, String[] labels, Integer[] icons){
		super(a, resource, labels);
		mNavItems = labels;	
		mNavIcons = icons;
		activity = a;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		LayoutInflater inflater = activity.getLayoutInflater();
		View row = inflater.inflate(R.layout.each_nav_item, parent, false);
		
		TextView item = (TextView)row.findViewById(R.id.txtListText);
		item.setText(mNavItems[position]);
		
		ImageView icon = (ImageView)row.findViewById(R.id.imgListIcon);
		icon.setImageResource(mNavIcons[position]);
		
		return row;
	}
}

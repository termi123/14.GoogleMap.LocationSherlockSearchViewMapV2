package in.wptrafficanalyzer.locationsherlocksearchviewmapv2;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.widget.SearchView;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends SherlockFragmentActivity implements LoaderCallbacks<Cursor>{
	
	GoogleMap mGoogleMap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		SupportMapFragment fragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mGoogleMap = fragment.getMap();

        handleIntent(getIntent());

		
	}
	
	private void handleIntent(Intent intent){
        if(intent.getAction().equals(Intent.ACTION_SEARCH)){
                doSearch(intent.getStringExtra(SearchManager.QUERY));
        }else if(intent.getAction().equals(Intent.ACTION_VIEW)){
                getPlace(intent.getStringExtra(SearchManager.EXTRA_DATA_KEY));
        }
	}

	@Override
	protected void onNewIntent(Intent intent) {
	        super.onNewIntent(intent);
	        setIntent(intent);
	        handleIntent(intent);
	}
	
	private void doSearch(String query){
	        Bundle data = new Bundle();
	        data.putString("query", query);
	        getSupportLoaderManager().restartLoader(0, data, this);
	}
	
	private void getPlace(String query){
	        Bundle data = new Bundle();
	        data.putString("query", query);
	        getSupportLoaderManager().restartLoader(1, data, this);
	}	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getSupportMenuInflater().inflate(R.menu.main, menu);
		
		// Get the SearchView and set the searchable configuration
	    SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
	    SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
	    // Assumes current activity is the searchable activity
	    searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
	    // searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default

		
		return true;
	}

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle query) {
		 CursorLoader cLoader = null;
         if(arg0==0)
                 cLoader = new CursorLoader(getBaseContext(), PlaceProvider.SEARCH_URI, null, null, new String[]{ query.getString("query") }, null);
         else if(arg0==1)
                 cLoader = new CursorLoader(getBaseContext(), PlaceProvider.DETAILS_URI, null, null, new String[]{ query.getString("query") }, null);
         return cLoader;

	}

	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor c) {
		showLocations(c);
		
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		// TODO Auto-generated method stub		
	}
	
	private void showLocations(Cursor c){
        MarkerOptions markerOptions = null;
        LatLng position = null;
        mGoogleMap.clear();
        while(c.moveToNext()){
                markerOptions = new MarkerOptions();
                position = new LatLng(Double.parseDouble(c.getString(1)),Double.parseDouble(c.getString(2)));
                markerOptions.position(position);
                markerOptions.title(c.getString(0));
                mGoogleMap.addMarker(markerOptions);
        }
        if(position!=null){
                CameraUpdate cameraPosition = CameraUpdateFactory.newLatLng(position);
                mGoogleMap.animateCamera(cameraPosition);
        }
	}
}
package eu.michalu.nestedmapfragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MyMapFragment extends SupportMapFragment {
	private LatLng mPosition;

	public MyMapFragment() {
		super();
	}

	public static MyMapFragment newInstance(LatLng position) {
		MyMapFragment frag = new MyMapFragment();
		frag.mPosition = position;
		return frag;
	}

	@Override
	public View onCreateView(LayoutInflater arg0, ViewGroup arg1, Bundle arg2) {
		View v = super.onCreateView(arg0, arg1, arg2);
		initMap();
		return v;
	}

	private void initMap() {
		getMap().moveCamera(CameraUpdateFactory.newLatLngZoom(mPosition, 15));
		getMap().addMarker(new MarkerOptions().position(mPosition));
		getMap().getUiSettings().setAllGesturesEnabled(true);
		getMap().getUiSettings().setCompassEnabled(true);
	}
}

package eu.michalu.nestedmapfragment;

import com.google.android.gms.maps.model.LatLng;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import eu.michalu.nestedmapfragment.model.Address;

public class ActivityWithMap extends FragmentActivity {

	Address address = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity);

		address = new Address("Marsza³kowska Street", "Warsaw", new LatLng(52.228083, 21.012967));

		FragmentManager fm = getSupportFragmentManager();
		AddressDetailsFragment addressFragment = (AddressDetailsFragment) fm.findFragmentByTag("address_fragment");

		if (addressFragment == null) {
			FragmentTransaction ft = fm.beginTransaction();
			ft.replace(R.id.fragment_container_for_map, AddressDetailsFragment.newInstance(address), "address_fragment");
			ft.commit();
			fm.executePendingTransactions();
		}

	}
}

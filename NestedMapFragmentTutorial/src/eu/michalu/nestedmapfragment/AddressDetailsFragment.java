package eu.michalu.nestedmapfragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import eu.michalu.nestedmapfragment.model.Address;

public class AddressDetailsFragment extends Fragment {
	private TextView mStreet;
	private TextView mTown;

	public static AddressDetailsFragment newInstance(Address venue) {
		AddressDetailsFragment fragment = new AddressDetailsFragment();
		fragment.setRetainInstance(true);
		Bundle b = new Bundle();
		b.putSerializable("address", venue);
		fragment.setArguments(b);
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View v = inflater.inflate(R.layout.details_view, container, false);

		Bundle args = getArguments();
		Address address = null;
		if (args.containsKey("address")) {
			address = (Address) args.getSerializable("address");
		}

		int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity());
		// Showing status
		if (status == ConnectionResult.SUCCESS) {
			{
				FragmentManager fm = getChildFragmentManager();
				MyMapFragment mMapFragment = MyMapFragment.newInstance(address.getCoordinates());
				FragmentTransaction fragmentTransaction = fm.beginTransaction();
				fragmentTransaction.add(R.id.my_map_fragment, mMapFragment);
				fragmentTransaction.commit();
				fm.executePendingTransactions();
			}
		}
		mStreet = (TextView) v.findViewById(R.id.address_details_street);
		mTown = (TextView) v.findViewById(R.id.address_details_town);
		mStreet.setText(address.getStreet());
		mTown.setText(address.getTown());

		return v;
	}
}

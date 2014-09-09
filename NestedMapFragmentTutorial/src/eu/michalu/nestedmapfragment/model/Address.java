package eu.michalu.nestedmapfragment.model;

import java.io.Serializable;

import com.google.android.gms.maps.model.LatLng;

public class Address implements Serializable {
	private String street;
	private String town;
	private LatLng coordinates;

	public Address(String street, String town, LatLng coordinates) {
		super();
		this.street = street;
		this.town = town;
		this.coordinates = coordinates;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getTown() {
		return town;
	}

	public void setTown(String town) {
		this.town = town;
	}

	public LatLng getCoordinates() {
		return coordinates;
	}

	public void setCoordinates(LatLng coordinates) {
		this.coordinates = coordinates;
	}
}

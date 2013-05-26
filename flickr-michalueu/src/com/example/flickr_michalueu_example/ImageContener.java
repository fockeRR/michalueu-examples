package com.example.flickr_michalueu_example;

import com.example.flickr_michalueu_example.FlickrManager.GetThumbnailsThread;
import com.example.flickr_michalueu_example.MainActivity.UIHandler;

import android.graphics.Bitmap;

public class ImageContener implements IThumb {
	String id;
	int position;
	String thumbURL;
	Bitmap thumb;
	Bitmap photo;
	String largeURL;
	String owner;// ;: "69155168@N07",
	String secret;// : "f546671ea1",
	String server;// : "8089",
	String farm;// : 9,

	public String getThumbURL() {
		return thumbURL;
	}

	public void setThumbURL(String thumbURL) {
		this.thumbURL = thumbURL;
		onSaveThumbURL(this.position, FlickrManager.uihandler, this);
		// thumb = FlickrManager.getThumbnail(this);
	}

	public String getLargeURL() {
		return largeURL;
	}

	public void setLargeURL(String largeURL) {
		this.largeURL = largeURL;
		// photo = FlickrManager.getImage(this);
	}

	@Override
	public String toString() {
		return "ImageContener [id=" + id + ", thumbURL=" + thumbURL
				+ ", largeURL=" + largeURL + ", owner=" + owner + ", secret="
				+ secret + ", server=" + server + ", farm=" + farm + "]";
	}

	public ImageContener(String id, String thumbURL, String largeURL,
			String owner, String secret, String server, String farm) {
		super();
		this.id = id;
		this.owner = owner;
		this.secret = secret;
		this.server = server;
		this.farm = farm;
	}

	public ImageContener(String id, String owner, String secret, String server,
			String farm) {
		super();
		this.id = id;
		this.owner = owner;
		this.secret = secret;
		this.server = server;
		this.farm = farm;
		setThumbURL(createPhotoURL(FlickrManager.PHOTO_THUMB, this));
		setLargeURL(createPhotoURL(FlickrManager.PHOTO_LARGE, this));
	}

	private String createPhotoURL(int photoType, ImageContener imgCon) {
		String tmp = null;
		tmp = "http://farm" + imgCon.farm + ".staticflickr.com/"
				+ imgCon.server + "/" + imgCon.id + "_" + imgCon.secret;// +".jpg";
		switch (photoType) {
		case FlickrManager.PHOTO_THUMB:
			tmp += "_t";
			break;
		case FlickrManager.PHOTO_LARGE:
			tmp += "_z";
			break;

		}
		tmp += ".jpg";
		return tmp;
	}

	@Override
	public void onSaveThumbURL(int position, UIHandler uih, ImageContener ic) {
		// TODO Auto-generated method stub
		new GetThumbnailsThread(position, uih, ic).start();
	}
}

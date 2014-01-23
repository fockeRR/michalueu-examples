package com.example.flickr_michalueu_example;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Message;
import android.util.Log;

import com.example.flickr_michalueu_example.MainActivity.UIHandler;

public class FlickrManager {

	// String to create Flickr API urls
	private static final String FLICKR_BASE_URL = "http://api.flickr.com/services/rest/?method=";
	private static final String FLICKR_PHOTOS_SEARCH_STRING = "flickr.photos.search";
	private static final String FLICKR_GET_SIZES_STRING = "flickr.photos.getSizes";
	private static final int FLICKR_PHOTOS_SEARCH_ID = 1;
	private static final int FLICKR_GET_SIZES_ID = 2;
	private static final int NUMBER_OF_PHOTOS = 20;
	
	//You can set here your API_KEY
	private static final String APIKEY_SEARCH_STRING = "&api_key=64c0f179f8aec0444033c8b2c57a7db0";
	
	private static final String TAGS_STRING = "&tags=";
	private static final String PHOTO_ID_STRING = "&photo_id=";
	private static final String FORMAT_STRING = "&format=json";
	public static final int PHOTO_THUMB = 111;
	public static final int PHOTO_LARGE = 222;

	public static UIHandler uihandler;

	private static String createURL(int methodId, String parameter) {
		String method_type = "";
		String url = null;
		switch (methodId) {
		case FLICKR_PHOTOS_SEARCH_ID:
			method_type = FLICKR_PHOTOS_SEARCH_STRING;
			url = FLICKR_BASE_URL + method_type + APIKEY_SEARCH_STRING + TAGS_STRING + parameter + FORMAT_STRING + "&per_page="+NUMBER_OF_PHOTOS+"&media=photos";
			break;
		case FLICKR_GET_SIZES_ID:
			method_type = FLICKR_GET_SIZES_STRING;
			url = FLICKR_BASE_URL + method_type + PHOTO_ID_STRING + parameter + APIKEY_SEARCH_STRING + FORMAT_STRING;
			break;
		}
		return url;
	}

	// http://farm{farm-id}.staticflickr.com/{server-id}/{id}_{secret}.jpg
	public static void getImageURLS(ImageContener imgCon) {
		String url = createURL(FLICKR_GET_SIZES_ID, imgCon.id);
		ByteArrayOutputStream baos = URLConnector.readBytes(url);
		String json = baos.toString();
		try {
			JSONObject root = new JSONObject(json.replace("jsonFlickrApi(", "").replace(")", ""));
			JSONObject sizes = root.getJSONObject("sizes");
			JSONArray size = sizes.getJSONArray("size");
			for (int i = 0; i < size.length(); i++) {
				JSONObject image = size.getJSONObject(i);
				if (image.getString("label").equals("Square")) {
					imgCon.setThumbURL(image.getString("source"));
				} else if (image.getString("label").equals("Medium")) {
					imgCon.setLargeURL(image.getString("source"));
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public static Bitmap getImage(ImageContener imgCon) {
		Bitmap bm = null;
		try {
			URL aURL = new URL(imgCon.largeURL);
			URLConnection conn = aURL.openConnection();
			conn.connect();
			InputStream is = conn.getInputStream();
			BufferedInputStream bis = new BufferedInputStream(is);
			bm = BitmapFactory.decodeStream(bis);
			bis.close();
			is.close();
		} catch (Exception e) {
			Log.e("FlickrManager", e.getMessage());
		}
		return bm;
	}

	public static void getThumbnails(ArrayList<ImageContener> imgCon, UIHandler uih) {
		for (int i = 0; i < imgCon.size(); i++)
			new GetThumbnailsThread(uih, imgCon.get(i)).start();
	}

	public static Bitmap getThumbnail(ImageContener imgCon) {
		Bitmap bm = null;
		try {
			URL aURL = new URL(imgCon.thumbURL);
			URLConnection conn = aURL.openConnection();
			conn.connect();
			InputStream is = conn.getInputStream();
			BufferedInputStream bis = new BufferedInputStream(is);
			bm = BitmapFactory.decodeStream(bis);
			bis.close();
			is.close();
		} catch (Exception e) {
			Log.e("FlickrManager", e.getMessage());
		}
		return bm;
	}

	public static class GetThumbnailsThread extends Thread {
		UIHandler uih;
		ImageContener imgContener;

		public GetThumbnailsThread(UIHandler uih, ImageContener imgCon) {
			this.uih = uih;
			this.imgContener = imgCon;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			imgContener.thumb = getThumbnail(imgContener);
			if (imgContener.thumb != null) {
				Message msg = Message.obtain(uih, UIHandler.ID_UPDATE_ADAPTER);
				uih.sendMessage(msg);

			}
		}

	}

	public static ArrayList<ImageContener> searchImagesByTag(UIHandler uih, Context ctx, String tag) {
		uihandler = uih;
		String url = createURL(FLICKR_PHOTOS_SEARCH_ID, tag);
		ArrayList<ImageContener> tmp = new ArrayList<ImageContener>();
		String jsonString = null;
		try {
			if (URLConnector.isOnline(ctx)) {
				ByteArrayOutputStream baos = URLConnector.readBytes(url);
				jsonString = baos.toString();
			}
			try {
				JSONObject root = new JSONObject(jsonString.replace("jsonFlickrApi(", "").replace(")", ""));
				JSONObject photos = root.getJSONObject("photos");
				JSONArray imageJSONArray = photos.getJSONArray("photo");
				for (int i = 0; i < imageJSONArray.length(); i++) {
					JSONObject item = imageJSONArray.getJSONObject(i);
					ImageContener imgCon = new ImageContener(item.getString("id"), item.getString("owner"), item.getString("secret"), item.getString("server"),
							item.getString("farm"));
					imgCon.position = i;
					tmp.add(imgCon);
				}
				Message msg = Message.obtain(uih, UIHandler.ID_METADATA_DOWNLOADED);
				msg.obj = tmp;
				uih.sendMessage(msg);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (NullPointerException nue) {
			nue.printStackTrace();
		}

		return tmp;
	}

}

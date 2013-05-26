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

public class FlickrManager{
	public static String FLICKR_BASE_URL = "http://api.flickr.com/services/rest/?method=";
	public static String FLICKR_PHOTOS_SEARCH_STRING = "flickr.photos.search";
	public static String FLICKR_GET_SIZES_STRING = "flickr.photos.getSizes";
	public final static int FLICKR_PHOTOS_SEARCH_ID = 1;
	public final static int FLICKR_GET_SIZES_ID = 2;
	public static String APIKEY_SEARCH_STRING = "&api_key=64c0f179f8aec0444033c8b2c57a7db0";
	public static String TAGS_STRING = "&tags=";
	public static String PHOTO_ID_STRING = "&photo_id=";
	public static String FORMAT_STRING = "&format=json";
	
	private static final int ID_0 = 0;
	
	public static final int PHOTO_THUMB = 111;
	public static final int PHOTO_LARGE = 222;
		
	public static UIHandler uihandler;

	private static String createURL(int methodId, String parameter) {
		String method_type = "";
		String url = null;
		switch (methodId) {
		case FLICKR_PHOTOS_SEARCH_ID:
			method_type = FLICKR_PHOTOS_SEARCH_STRING;
			url = FLICKR_BASE_URL + method_type + APIKEY_SEARCH_STRING
					+ TAGS_STRING + parameter + FORMAT_STRING +"&per_page=20&media=photos";
			break;
		case FLICKR_GET_SIZES_ID:
			method_type = FLICKR_GET_SIZES_STRING;
			url = FLICKR_BASE_URL + method_type + PHOTO_ID_STRING + parameter
					+ APIKEY_SEARCH_STRING + FORMAT_STRING;
			break;
		}
		return url;
	}

	// private ImageContener
	// http://farm{farm-id}.staticflickr.com/{server-id}/{id}_{secret}.jpg
	private static void getImageURLS(ImageContener imgCon) {
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
//					onSaveThumbURL(i, imgCon);
					 
				} else if (image.getString("label").equals("Medium")) {
					imgCon.setLargeURL(image.getString("source"));
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	public static Bitmap getImage(ImageContener imgCon)
	{
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
            Log.e("[Bitmap]", "Blad pobierania photo: " + imgCon.getLargeURL());
        }
        return bm;
	}
	
	public static void getThumbnails(ArrayList<ImageContener> imgCon, UIHandler uih)
	{
		for (int i = 0; i < imgCon.size(); i++)
		{
			new GetThumbnailsThread(i, uih, imgCon.get(i)).start();
		}
	}
	
	public static Bitmap getThumbnail(ImageContener imgCon)
	{
		 Bitmap bm = null;
         try {
             URL aURL = new URL(imgCon.thumbURL);
             Log.i("URL_GET_THUMB",imgCon.thumbURL);
             URLConnection conn = aURL.openConnection();
             conn.connect();
             InputStream is = conn.getInputStream();
             BufferedInputStream bis = new BufferedInputStream(is);
             bm = BitmapFactory.decodeStream(bis);
             bis.close();
             is.close();
        } catch (Exception e) {
            Log.e("[Bitmap]", "Blad pobierania thumb: " + imgCon.getThumbURL());
        }
        return bm;
	}
	
	public static class GetThumbnailsThread extends Thread {
		int position;
		UIHandler uih;
		ImageContener imgContener;

		public GetThumbnailsThread(int position, UIHandler uih, ImageContener imgCon) {
			this.position = position;
			this.uih = uih;
			this.imgContener = imgCon;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			imgContener.thumb = getThumbnail(imgContener);
			if (imgContener.thumb != null) {

				Message msg = Message.obtain(uih, UIHandler.ID_UPDATE_ADAPTER);
				imgContener.position = position;
				msg.obj = imgContener;
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
					ImageContener imgCon = new ImageContener(
							item.getString("id"),
							item.getString("owner"), item.getString("secret"),
							item.getString("server"), item.getString("farm"));
					imgCon.position = i;
					tmp.add(imgCon);
				}
				Message msg = Message.obtain(uih, FlickrManager.ID_0);
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

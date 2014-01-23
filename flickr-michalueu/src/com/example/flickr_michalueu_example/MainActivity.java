package com.example.flickr_michalueu_example;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageView;

import com.example.flickr_michalueu_example.FlickrManager.GetThumbnailsThread;

public class MainActivity extends Activity {

	public final String LAST_IMAGE = "lastImage";
	public UIHandler uihandler;
	public ImageAdapter imgAdapter;
	private ArrayList<ImageContener> imageList;

	// UI
	private Button downloadPhotos;
	private Gallery gallery;
	private ImageView imgView;
	private EditText editText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Init UI Handler
		uihandler = new UIHandler();

		downloadPhotos = (Button) findViewById(R.id.button1);
		editText = (EditText) findViewById(R.id.editText1);
		gallery = (Gallery) findViewById(R.id.gallery1);
		imgView = (ImageView) findViewById(R.id.imageView1);

		// Click on thumbnail
		gallery.setOnItemClickListener(onThumbClickListener);
		// Click on search
		downloadPhotos.setOnClickListener(onSearchButtonListener);

		// Get prevoiusly downloaded list after orientation change
		imageList = (ArrayList<ImageContener>) getLastNonConfigurationInstance();
		if (imageList != null) {
			imgAdapter = new ImageAdapter(getApplicationContext(), imageList);
			ArrayList<ImageContener> ic = imgAdapter.getImageContener();
			gallery.setAdapter(imgAdapter);
			imgAdapter.notifyDataSetChanged();
			int lastImage = -1;
			if (savedInstanceState.containsKey(LAST_IMAGE)) {
				lastImage = savedInstanceState.getInt(LAST_IMAGE);
			}
			if (lastImage >= 0 && ic.size() >= lastImage) {
				gallery.setSelection(lastImage);
				Bitmap photo = ic.get(lastImage).getPhoto();
				if (photo == null)
					new GetLargePhotoThread(ic.get(lastImage), uihandler).start();
				else
					imgView.setImageBitmap(ic.get(lastImage).photo);
			}
		}

	}

	/**
	 * Saving information about images
	 */
	@Override
	public Object onRetainNonConfigurationInstance() {
		if (imgAdapter != null)
			return this.imgAdapter.getImageContener();
		else
			return null;
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// Saving index of selected item in Gallery
		outState.putInt(LAST_IMAGE, gallery.getSelectedItemPosition());
		super.onSaveInstanceState(outState);

	}

	/**
	 * 
	 * @author michalu
	 * 
	 *         Downloading a larger photo using Thread
	 */
	public class GetLargePhotoThread extends Thread {
		ImageContener ic;
		UIHandler uih;

		public GetLargePhotoThread(ImageContener ic, UIHandler uih) {
			this.ic = ic;
			this.uih = uih;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			if (ic.getPhoto() == null) {
				ic.setPhoto(FlickrManager.getImage(ic));
			}
			Bitmap bmp = ic.getPhoto();
			if (ic.getPhoto() != null) {
				Message msg = Message.obtain(uih, UIHandler.ID_SHOW_IMAGE);
				msg.obj = bmp;
				uih.sendMessage(msg);
			}
		}
	}

	/**
	 * Runnable to get metadata from Flickr API
	 */
	Runnable getMetadata = new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			String tag = editText.getText().toString().trim();
			if (tag != null && tag.length() >= 3)
				FlickrManager.searchImagesByTag(uihandler, getApplicationContext(), tag);
		}
	};

	public class ImageAdapter extends BaseAdapter {
		private Context mContext;
		private int defaultItemBackground;
		private ArrayList<ImageContener> imageContener;

		public ArrayList<ImageContener> getImageContener() {
			return imageContener;
		}

		public void setImageContener(ArrayList<ImageContener> imageContener) {
			this.imageContener = imageContener;
		}

		public ImageAdapter(Context c, ArrayList<ImageContener> imageContener) {
			mContext = c;
			this.imageContener = imageContener;
			TypedArray styleAttrs = c.obtainStyledAttributes(R.styleable.PicGallery);
			styleAttrs.getResourceId(R.styleable.PicGallery_android_galleryItemBackground, 0);
			defaultItemBackground = styleAttrs.getResourceId(R.styleable.PicGallery_android_galleryItemBackground, 0);
			styleAttrs.recycle();
		}

		public int getCount() {
			return imageContener.size();
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView i = new ImageView(mContext);
			if (imageContener.get(position).thumb != null) {
				i.setImageBitmap(imageContener.get(position).thumb);
				i.setLayoutParams(new Gallery.LayoutParams(75, 75));
				i.setBackgroundResource(defaultItemBackground);
			} else
				i.setImageDrawable(getResources().getDrawable(android.R.color.black));
			return i;
		}

	}

	/**
	 * 
	 * @author michalu
	 * 
	 *         UI Handler to handle messages from threads
	 */
	class UIHandler extends Handler {
		public static final int ID_METADATA_DOWNLOADED = 0;
		public static final int ID_SHOW_IMAGE = 1;
		public static final int ID_UPDATE_ADAPTER = 2;

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case ID_METADATA_DOWNLOADED:
				// Set of information required to download thumbnails is
				// available now
				if (msg.obj != null) {
					imageList = (ArrayList<ImageContener>) msg.obj;
					imgAdapter = new ImageAdapter(getApplicationContext(), imageList);
					gallery.setAdapter(imgAdapter);
					for (int i = 0; i < imgAdapter.getCount(); i++) {
						new GetThumbnailsThread(uihandler, imgAdapter.getImageContener().get(i)).start();
					}
				}
				break;
			case ID_SHOW_IMAGE:
				// Display large image
				if (msg.obj != null) {
					imgView.setImageBitmap((Bitmap) msg.obj);
					imgView.setVisibility(View.VISIBLE);
				}
				break;
			case ID_UPDATE_ADAPTER:
				// Update adapter with thumnails
				imgAdapter.notifyDataSetChanged();
				break;
			}
			super.handleMessage(msg);
		}
	}

	OnItemClickListener onThumbClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
			// Get large image of selected thumnail
			new GetLargePhotoThread(imageList.get(position), uihandler).start();
		}
	};

	/**
	 * to get metadata from Flickr API
	 */
	OnClickListener onSearchButtonListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if (gallery.getAdapter() != null) {
				imgAdapter.imageContener = new ArrayList<ImageContener>();
				gallery.setAdapter(imgAdapter);
				imgView.setVisibility(View.INVISIBLE);
			}
			new Thread(getMetadata).start();
		}
	};

}

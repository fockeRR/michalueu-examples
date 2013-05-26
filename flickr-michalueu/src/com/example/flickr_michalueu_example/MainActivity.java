package com.example.flickr_michalueu_example;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
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

public class MainActivity extends Activity{

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		outState.putInt("lastImage", gallery.getSelectedItemPosition());
		super.onSaveInstanceState(outState);
		
	}

	public UIHandler uihandler;
	public ImageAdapter imgAdapter;
	Button downloadPhotos;
	ArrayList<ImageContener> imageList;
	Gallery gallery;
	ImageView imgView;
	EditText editText;

	@Override
	public Object onRetainNonConfigurationInstance() {
		if(imgAdapter != null)
			return this.imgAdapter.imageContener;
		else
			return null;
	}

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		uihandler = new UIHandler();

		downloadPhotos = (Button) findViewById(R.id.button1);
		editText = (EditText) findViewById(R.id.editText1);
		editText.setSingleLine();
		editText.setText("egicf13");
		gallery = (Gallery) findViewById(R.id.gallery1);
		imgView = (ImageView) findViewById(R.id.imageView1);
		gallery.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				// TODO Auto-generated method stub
				
				new GetPhotoThread(position, uihandler).start();
			}
		});
		downloadPhotos.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(gallery.getAdapter() != null)
				{
					imgAdapter.imageContener = new ArrayList<ImageContener>();
					gallery.setAdapter(imgAdapter);
					imgView.setVisibility(View.INVISIBLE);
				}
				new Thread(getPhotosThread).start();

			}
		});
		imageList = (ArrayList<ImageContener>) getLastNonConfigurationInstance();
		if (imageList != null) {
			imgAdapter = new ImageAdapter(getApplicationContext(), imageList);
			gallery.setAdapter(imgAdapter);
			int lastImage = savedInstanceState.getInt("lastImage");
			if(savedInstanceState.containsKey("lastImage") && lastImage  >= 0 && imgAdapter.imageContener.size() >= lastImage)
			{
				gallery.setSelection(lastImage);
				
				imgView.setImageBitmap(imgAdapter.imageContener.get(lastImage).photo);
			}
		}

	}

	public class GetPhotoThread extends Thread {
		int position;
		UIHandler uih;

		public GetPhotoThread(int position, UIHandler uih) {
			this.position = position;
			this.uih = uih;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			if(gallery.getAdapter() != null && ((ImageAdapter) (gallery.getAdapter())).imageContener != null ) {
			ImageContener imgCon = ((ImageContener) (((ImageAdapter) gallery
					.getAdapter()).imageContener.get(position)));
			if (imgCon.photo == null) {
				imgCon.photo = FlickrManager.getImage(imgCon);
			}
			Bitmap bm = imgCon.photo;
			if (bm != null) {

				Message msg = Message.obtain(uih, UIHandler.ID_SHOW_IMAGE);
				msg.obj = bm;
				uih.sendMessage(msg);

			}
		}

	}
	}

	Runnable getPhotosThread = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			String tag = editText.getText().toString().trim();
			if (tag != null && tag.length() >=3 )
				FlickrManager
						.searchImagesByTag(uihandler, getApplicationContext(), tag);
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public class ImageAdapter extends BaseAdapter {
		private Context mContext;
		int defaultItemBackground;

		public ArrayList<ImageContener> imageContener;

		public ImageAdapter(Context c, ArrayList<ImageContener> imageContener) {
			mContext = c;
			this.imageContener = imageContener;
			TypedArray styleAttrs = c
					.obtainStyledAttributes(R.styleable.PicGallery);
			styleAttrs.getResourceId(
					R.styleable.PicGallery_android_galleryItemBackground, 0);
			defaultItemBackground = styleAttrs.getResourceId(
					R.styleable.PicGallery_android_galleryItemBackground, 0);
			// recycle attributes
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
			// i.set(imageContener.get(position));
			i.setLayoutParams(new Gallery.LayoutParams(75, 75));
			// i.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
			i.setBackgroundResource(defaultItemBackground);
			} else
			{
				i.setImageDrawable(getResources().getDrawable(android.R.color.black));
			}

			return i;
		}

	}

	class UIHandler extends Handler {

		public static final int ID_0 = 0;
		public static final int ID_SHOW_IMAGE = 1;
		public static final int ID_UPDATE_ADAPTER = 2;

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case ID_0:
				// a message is received; update UI text view
				if (msg.obj != null) {
					imgAdapter = new ImageAdapter(getApplicationContext(),
							((ArrayList<ImageContener>) msg.obj));
					gallery.setAdapter(imgAdapter);
					for (int i = 0; i < imgAdapter.getCount(); i++) {
						new GetThumbnailsThread(i,uihandler,imgAdapter.imageContener.get(i)).start() ;
					}
				}

				break;
			case ID_SHOW_IMAGE:
				// a message is received; update UI text view
				if (msg.obj != null) {
					imgView.setImageBitmap((Bitmap) msg.obj);
					imgView.setVisibility(View.VISIBLE);
				}

				break;
			case ID_UPDATE_ADAPTER:
				// a message is received; update UI text view
				if (msg.obj != null) {
					ImageContener tmp = ((ImageContener)msg.obj);
					if (imgAdapter != null) {
						if (tmp.position >= imgAdapter.imageContener.size())
						{
							imgAdapter.imageContener.add(tmp);
						}
						else
						{
							imgAdapter.imageContener.get(tmp.position).thumb = tmp.thumb;
						}
						
						gallery.setAdapter(imgAdapter);
					}
//					imgView.setImageBitmap(((ImageContener) msg.obj).thumb);
				}

				break;
			}
			super.handleMessage(msg);
		}
	}

}

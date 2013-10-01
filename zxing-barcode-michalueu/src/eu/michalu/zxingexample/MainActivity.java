package eu.michalu.zxingexample;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

public class MainActivity extends Activity {

	public static final int SCANNER_RESULT = 111;
	public static final boolean QRCODE_OR_BARCODE_ENCODE = false; // true for
																	// barcode
																	// encoding
	public ImageView img;
	public TextView tv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		img = (ImageView) findViewById(R.id.img);
		tv = (TextView) findViewById(R.id.tv);
		Intent intent = new Intent("eu.michalu.SCAN");
		intent.putExtra("SCAN_MODE", "ONE_D_MODE");
		startActivityForResult(intent, SCANNER_RESULT);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		/**
		 * Back from scanner view
		 */
		if (requestCode == SCANNER_RESULT) {
			if (resultCode == RESULT_OK) {
				String contents = intent.getStringExtra("SCAN_RESULT");
				if (QRCODE_OR_BARCODE_ENCODE) {
					Intent encodeIntent = new Intent("eu.michalu.ENCODE");
					encodeIntent.addCategory(Intent.CATEGORY_DEFAULT);
					encodeIntent.putExtra("ENCODE_FORMAT", "CODE_128");
					encodeIntent.putExtra("ENCODE_DATA", contents);
					startActivity(encodeIntent);
				} else {
					QRCodeWriter writer = new QRCodeWriter();
					BitMatrix bitMatrix = null;
					try {
						bitMatrix = writer.encode(contents, BarcodeFormat.QR_CODE, 300, 300);
						int width = bitMatrix.getWidth();
						int height = bitMatrix.getHeight();
						int h = 300;
						int w = 300;
						Config conf = Bitmap.Config.RGB_565;
						Bitmap bmp = Bitmap.createBitmap(w, h, conf);
						try {
							for (int x = 0; x < width; x++) {
								for (int y = 0; y < height; y++) {
									bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
								}
							}
							img.setImageBitmap(bmp);
							img.setAdjustViewBounds(true);        
							img.setScaleType(ImageView.ScaleType.FIT_CENTER);
							img.setVisibility(View.VISIBLE);
							tv.setVisibility(View.GONE); 

						} catch (Exception e) {
							System.out.println(e.getMessage());
						}
					} catch (WriterException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}

			} else if (resultCode == RESULT_CANCELED) {
				// Handle cancel
				Log.i(MainActivity.class.getSimpleName(), "Handle cancel");
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}

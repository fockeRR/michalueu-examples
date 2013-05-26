package eu.michalu.zxingexample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

public class MainActivity extends Activity {

	public static final int SCANNER_RESULT = 111;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
				Intent encodeIntent = new Intent("eu.michalu.ENCODE");  
				encodeIntent.addCategory(Intent.CATEGORY_DEFAULT); 
				encodeIntent.putExtra("ENCODE_FORMAT", "CODE_128");  
				encodeIntent.putExtra("ENCODE_DATA", contents);  
			    startActivity(encodeIntent); 

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

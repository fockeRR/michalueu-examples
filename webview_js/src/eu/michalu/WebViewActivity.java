package eu.michalu;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebViewActivity extends Activity {
	WebView mWebView;
	/*
	 * array of div's ids to disable/hide
	 */
	String[] idsToHide = { "section1", "section3", "section5" };

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		mWebView = (WebView) findViewById(R.id.activity_webview);
		//enable javascript engine
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageFinished(WebView view, String url) {
				// TODO Auto-generated method stub
				super.onPageFinished(view, url);
				
				//run 'disableSection' for all divs to hide/disable
				for (String s : idsToHide) {
					String surveyId = s;
					view.loadUrl("javascript:disableSection('" + surveyId + "');");
				}
			}
		});
		//load webpage from assets
		mWebView.loadUrl("file:///android_asset/list.html");
	}

}

package eu.michalu;

import android.app.Activity;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebViewActivity extends Activity {
	WebView mWebView;
	String[] idsToHide = { "section1", "section3", "section5" };

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		mWebView = (WebView) findViewById(R.id.activity_webview);
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.setWebViewClient(new WebViewClient() {

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				// TODO Auto-generated method stub
				super.onPageStarted(view, url, favicon);

			}

			@Override
			public void onPageFinished(WebView view, String url) {
				// TODO Auto-generated method stub
				super.onPageFinished(view, url);
				for (String s : idsToHide) {
					String surveyId = s;
					view.loadUrl("javascript:disableSection('" + surveyId + "');");
				}

			}

			@Override
			public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
				handler.proceed();
			}

		});

		mWebView.loadUrl("file:///android_asset/list.html");
	}

}

package net.louislam.hkepc;

import java.io.IOException;
import java.util.Map;
import java.util.Stack;

import net.louislam.hkepc.page.*;
import net.louislam.hkepc.urlhandler.*;


import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.google.gson.Gson;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;

import android.util.Log;
import android.view.KeyEvent;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * HKEPC Activity Base Class
 * @author Louis Lam
 *
 */
public abstract class HKEPC extends Activity {
	
	/** Page Handler */
	private static final Page[] pageHandlers = { 
		new Index(), 
		new ForumDisplay(), 
		new ViewThread(), 
		
		new Last()
	};
	
	/** Url Handler */
	private static final UrlHandler[] urlHandlers = {
		new Logging()
	};
	
	/** Default base url */
	public final static String URL = "http://www.hkepc.com/forum/";
	
	/** WebView */
	protected WebView webView;
	
	/** Page Load Task Object */
	protected PageLoadTask task;
	
	protected AppLayout layout = null;
	
	private Stack<String> urlStack;
	
	private String currentUrl;
	
	/** Loading Dialog */
	private ProgressDialog loadingDialog;
	
	/**
	 * 
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		urlStack = new Stack<String>();

		webView = (WebView) findViewById(R.id.webView1);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setAllowFileAccess(true);

		webView.setWebViewClient(new WebViewClient() {
			    @Override 
			    public boolean shouldOverrideUrlLoading(WebView view, String url) { 
				   loadNewUrl(url);
				   return true;
			    } 
			    
			    @Override
			    public void onPageFinished(WebView view, String url) {
				    super.onPageFinished(view, url);
				    loadingDialog.hide();
			    }
		});
		
		layout = new AppLayout(this);

		// Create Loading Dialog
		loadingDialog = new ProgressDialog(this);
		loadingDialog.setMessage("Loading");
		loadingDialog.setIndeterminate(true);
		loadingDialog.setCanceledOnTouchOutside(false);
	}
	
	public void loadNewUrl(String url) {
		
		// Not hkepc forum
		if ( ! url.contains("hkepc.com/forum")) {
			Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
			startActivity(browserIntent);
			return;
		}
		
		if (currentUrl != null) {
			urlStack.add(currentUrl);
		}		
		
		currentUrl = url;
		loadData(url);
	}
	
	/**
	 * Go back to the previous page
	 */
	public void goBack() {
		loadingDialog.show();
		if (canGoBack()) {
			currentUrl = urlStack.pop();
			this.loadData(currentUrl);
		}
	}
	
	/**
	 * Check whether can go back
	 * @return
	 */
	public boolean canGoBack() {
		return (urlStack.size() != 0);
	}
	
	/**
	 * Refresh
	 */
	public void refresh() {
		loadData(currentUrl);
	}
	
	/**
	 * Call a background task to load hkepc page data
	 * @param url
	 */
	private void loadData(String url) {
		loadingDialog.show();
		
		task = new PageLoadTask();
		task.execute(url);
	}
	
	/**
	 * Render html to the Webview
	 * @param html
	 */
	private void loadPage(String html) {
		//(new WebViewTask()).execute(webView, html);
		webView.loadDataWithBaseURL(URL, html, "text/html", "utf-8", "");
	}
	
	/**
	 * Render html to the Webview
	 * With default layout
	 */
	public void loadPage() {
		this.loadPage(layout.html());
	}
	
	/**
	 * Set Content
	 * @param content
	 */
	public void setContent(String content) {
		layout.content(content);
	}
	
	/**
	 * Page Load Done
	 * @param doc
	 */
	public void pageLoadDone(Document doc) {
		if (doc == null) {
			loadingDialog.hide();
			return;
		}
		
		// Get Body id
		String id = doc.select("body").attr("id");
		boolean valid = false;
		String content = null;
		
		
		// Handle page
		for (Page p : pageHandlers) {
			if (p.getId().equals(id) || p.getId().equals("*")) {
				content = p.getContent(doc);
				valid = true;
				break;
			}
		}
		
		if (valid) {
			
			// Check login 
			checkLogin(doc);
			
			this.setContent(content);
			this.loadPage();
		}
	}
	
	/**
	 * Check Login
	 * @param doc
	 */
	public abstract void checkLogin(Document doc);
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK) && canGoBack()) {
			goBack();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	public void showLoading() {
		loadingDialog.show();
	}
	
	public void hideLoading() {
		loadingDialog.hide();
	}
	
	
	
	/**
	 * PageLoadTask
	 * @author Louis Lam
	 */
	class PageLoadTask extends AsyncTask<Object, Integer, Document> {
		@Override
		protected Document doInBackground(Object... obj) {
			
			if (obj[0] == null)
				return null;
			
			String url = (String) obj[0];
			
			Connection conn = null;
			Document doc = null;
			
			for (UrlHandler h : urlHandlers) {	
				if (url.contains(h.getUrlMatch())) {
					return h.handle(HKEPC.this, url);
				}
			}
			
			try {
				
				if (HKEPC.getCookies(HKEPC.this) != null) {
					conn = Jsoup.connect(url).cookies(HKEPC.getCookies(HKEPC.this));
				} else {
					conn = Jsoup.connect(url);
				}
				doc = conn.get();
			} catch (IOException e) {

			}

			return doc;
		}

		// This is called each time you call publishProgress()
		protected void onProgressUpdate(Integer... progress) {

		}

		// This is called when doInBackground() is finished
		@Override
		protected void onPostExecute(Document doc) {
			pageLoadDone(doc);
		}
	}
	
	class WebViewTask extends AsyncTask<Object, Void, Void> {

		@Override
		protected Void doInBackground(Object... obj) {
			WebView webView = (WebView) obj[0];
			String html = (String) obj[1];
			webView.loadDataWithBaseURL(URL, html, "text/html", "utf-8", "");
			return null;
		}
		
		protected void onProgressUpdate(Void... v) {

		}

		@Override
		protected void onPostExecute(Void v) {
			
		}
	}

	/**
	 * @return the cookies
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, String> getCookies(Context c) {
		  SharedPreferences appSharedPrefs = PreferenceManager
		  .getDefaultSharedPreferences(c);
		  Gson gson = new Gson();
		  String json = appSharedPrefs.getString("Cookies", "");
		  return gson.fromJson(json, Map.class);
		
		//return cookies;
	}

	/**
	 * @param cookies the cookies to set
	 */
	public static void setCookies(Map<String, String> cookies, Context c) {
		
		SharedPreferences appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(c);
		Editor prefsEditor = appSharedPrefs.edit();
		
		Gson gson = new Gson();
		
		String json = gson.toJson(cookies);
		prefsEditor.putString("Cookies", json);
		prefsEditor.commit();
		
		//HKEPC.cookies = cookies;
	}
	
	
	
}

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

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;

import android.util.Log;
import android.view.KeyEvent;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

/**
 * HKEPC Activity Base Class
 * @author Louis Lam
 *
 */
public abstract class HKEPC extends Activity {
	
	/** Page Handler */
	protected static final Page[] pageHandlers = { 
		new Index(), 
		new ForumDisplay(), 
		new ViewThread()
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
		webView.getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
		//webView.getSettings().setBlockNetworkImage(true);
		//webView.getSettings().setEnableSmoothTransition(true);
		//webView.getSettings().setAppCacheEnabled(true);

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
	
	/**
	 * Load New Url
	 * @param url
	 */
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
		
		loadData(url);
	}
	
	@Override
	protected void onResume() {
		this.refresh();
		super.onResume();
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
		
		// Set the current url
		currentUrl = url;
		
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
	 * When the process reach here, it assumes that all pages are hkepc pages.
	 * @param doc
	 */
	public void pageLoadDone(Document doc, String url) {
		
		// If the document is nothing.
		if (doc == null) {
			loadingDialog.hide();
			Toast.makeText(getApplicationContext(), "無法連接到 HKEPC。", Toast.LENGTH_SHORT).show();
			return;
		}
		
		// Get Body id
		String id = doc.select("body").attr("id");
		boolean valid = false;
		String content = null;
		
		// Handle page
		for (Page p : pageHandlers) {
			if (p.getId().equals(id)) {
				content = p.getContent(doc);
				valid = true;
				break;
			}
		}
		
		if (valid) {
		
		// Get Simple Content
		} else {
			content = (new Last()).getContent(doc);
		}
		
		this.setContent(content);
		this.loadPage();
		//loadingDialog.hide();
	}
	
	/**
	 * Check Login
	 * @param doc
	 */
	public abstract void checkLogin(Document doc, String url);
	
	public abstract void controlUI(Document doc, String url);
	
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
	class PageLoadTask extends AsyncTask<String, Integer, Object[]> {
		@Override
		protected Object[] doInBackground(String... obj) {
			Connection conn = null;
			
			String url = (String) obj[0];
			
			Object[] returnObjs = new Object[2];
			returnObjs[1] = url;
			
			if (obj[0] == null)
				return returnObjs;
			
			for (UrlHandler h : urlHandlers) {	
				if (url.contains(h.getUrlMatch())) {
					returnObjs[0] = h.handle(HKEPC.this, url);
					return returnObjs;
				}
			}
			
			try {
				if (HKEPC.getCookies(HKEPC.this) != null) {
					conn = Jsoup.connect(url).cookies(HKEPC.getCookies(HKEPC.this));
				} else {
					conn = Jsoup.connect(url);
				}
				returnObjs[0] = conn.get();
			} catch (IOException e) {
				//returnObjs[0] = Jsoup.parse("<div>Cannot connect to HKEPC</div>");
			}
			
			return returnObjs;
		}

		// This is called each time you call publishProgress()
		protected void onProgressUpdate(Integer... progress) {

		}

		// This is called when doInBackground() is finished
		@Override
		protected void onPostExecute(Object[] obj) {
			Document doc = (Document) obj[0];
			String url = (String) obj[1];
			pageLoadDone(doc, url);
			
			checkLogin(doc, url);
			controlUI(doc, url);
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

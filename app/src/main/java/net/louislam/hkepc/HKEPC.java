package net.louislam.hkepc;

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
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import net.louislam.android.L;
import net.louislam.hkepc.page.*;
import net.louislam.hkepc.urlhandler.UrlHandler;
import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.Map;
import java.util.Stack;
import java.util.Timer;

/**
 * HKEPC Activity Base Class
 * @author Louis Lam
 *
 */
public abstract class HKEPC extends AppCompatActivity {
	
	/** Page Handler (討論區) */
	protected static final Page[] pageHandlers = { 
		new Index(), 
		new ForumDisplay(), 
		new ViewThread(),
		new PM(),
		new Space(),
		new Search(),
		new Post()
	};

	/** Page Handler (首頁文章) */
	protected static final Page[] mainPageHandlers = {
		new EPCHome(),
		new EPCHomeCoverStory(),
		new EPCHomeNewProduct(),

	};

	/** Url Handler */
	protected static final UrlHandler[] urlHandlers = {
		//new Logging()
	};
	
	/** File Format */
	public static final String[] fileFormats = {
		"png", "jpg", "jpeg", "gif", "bmp", "tiff", "tif"
	};
	
	/** Default base url */
	public final static String URL = "https://www.hkepc.com/forum/";
	
	/** WebView */
	protected WebView webView;
	
	/** Page Load Task Object */
	protected PageLoadTask task;
	
	protected AppLayout layout = null;
	
	private Stack<Content> contentStack;
	
	protected Content currentContent;
	
	private String replyUrl;
	private String replyFormHash;
	
	private ProgressBar loadingIcon;


	Timer timer = new Timer(true);
	
	/**
	 * @return the webView
	 */
	public WebView getWebView() {
		return webView;
	}

	/**
	 * @param webView the webView to set
	 */
	public void setWebView(WebView webView) {
		this.webView = webView;
	}

	/**
	 * @return the replyFormHash
	 */
	public String getReplyFormHash() {
		return replyFormHash;
	}

	/**
	 * @param replyFormHash the replyFormHash to set
	 */
	public void setReplyFormHash(String replyFormHash) {
		this.replyFormHash = replyFormHash;
	}

	/**
	 * @return the replyUrl
	 */
	public String getReplyUrl() {
		return replyUrl;
	}

	/**
	 * @param replyUrl the replyUrl to set
	 */
	public void setReplyUrl(String replyUrl) {
		this.replyUrl = replyUrl;
	}

	/** Loading Dialog */
	private ProgressDialog loadingDialog;
	
	/**
	 * 
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && BuildConfig.DEBUG) {
			WebView.setWebContentsDebuggingEnabled(true);
		}

		contentStack = new Stack<Content>();
		
		webView = (WebView) findViewById(R.id.webView1);
		webView.getSettings().setJavaScriptEnabled(true);

		if (Build.VERSION.SDK_INT >= 21) {
			webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
		}

		//webView.getSettings().setBlockNetworkImage(true);
		webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
		
		webView.setWebViewClient(new WebViewClient() {
			    @Override 
			    public boolean shouldOverrideUrlLoading(WebView view, String url) { 
				   loadNewUrl(url);
				   return true;
			    } 
			    
			    @Override
			    public void onPageFinished(WebView view, String url) {
				    super.onPageFinished(view, url);
				    webViewPageLoadDone();
				    //loadingDialog.hide();

					if (currentContent != null) {
						scrollTo(currentContent.getScrollPosition());

					}

			    }
			    
			    @Override
			    public void onLoadResource(WebView view, String url) {
				    super.onLoadResource(view, url);
			    }
		});
		
		layout = new AppLayout(this);

		// Create Loading Dialog
		loadingDialog = new ProgressDialog(this);
		loadingDialog.setMessage("Loading");
		loadingDialog.setIndeterminate(true);
		loadingDialog.setCanceledOnTouchOutside(true);

	}

	private void scrollTo(final int yLocation) {
		webView.postDelayed(new Runnable () {
			@Override
			public void run() {
				webView.scrollTo(0, yLocation);
				L.log("Scroll To: " + currentContent.getScrollPosition());
			}
		}, 50);
	}
	
	public abstract void webViewPageLoadDone();
	
	/**
	 * Load New Url
	 * @param url
	 */
	public void loadNewUrl(String url) {
		
		boolean isImage = false;
		
		for (String format : fileFormats) {
			if (url.endsWith(format)) {
				isImage = true;
				break;
			}
		}
		
		// Not hkepc forum or image
		if ( isImage || ! url.contains("hkepc.com")) {
			Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
			startActivity(browserIntent);
			return;
		}
	
		if (currentContent != null) {
			currentContent.setScrollPosition(webView.getScrollY());
			contentStack.add(currentContent);
		}

		loadData(url);
	}


	/**
	 * Go back to the previous page
	 */
	public void goBack() {
		if (canGoBack()) {
			currentContent = contentStack.pop();
			this.setContent(currentContent.getContent());
			this.loadPage();
		}
	}
	
	/**
	 * Check whether can go back
	 * @return
	 */
	public boolean canGoBack() {
		return (contentStack.size() != 0);
	}
	
	/**
	 * Refresh
	 */
	public void refresh() {
		loadData(currentContent.getUrl());
	}
	
	private void loadData(String url) {
		loadingDialog.show();
		
		// Set the current url
		currentContent = new Content();
		currentContent.setUrl(url);
	
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
	
	public String getContent() {
		return layout.content();
	}
	
	/**
	 * Page Load Done
	 * When the process reach here, it assumes that all pages are hkepc pages.
	 * @param doc
	 */
	public void pageLoadDone(Document doc, String url) {

		L.log("Url: " + url);

		// If the document is nothing.
		if (doc == null) {
			loadingDialog.hide();
			Toast.makeText(getApplicationContext(), "無法連接到 HKEPC。網路不穩定，或者 EPC 又死機。 :(", Toast.LENGTH_LONG).show();
			return;
		}
		
		// Get Body id
		String id = doc.select("body").attr("id");
		boolean valid = false;
		String content = null;
		
		// Handle page (for forum)
		for (Page p : pageHandlers) {
			if (p.getId().equals(id)) {
				content = p.getContent(doc);
				valid = true;
				break;
			}
		}

		// Handle page (for epc home)
		for (Page p : mainPageHandlers) {
			if (url.contains(p.getId())) {
				content = p.getContent(doc);
				valid = true;
				break;
			}
		}

		if ( ! valid) {

			// 最後睇下係咪首頁嘅 Article, 唔係就直接display content
			if (doc.select("#article").size() > 0) {
				content = (new EPCHomeArticle()).getContent(doc);
				valid = true;
			} else {
				content = (new Last()).getContent(doc);
			}
		}
		
		// if no content, do not load it into webview
		if (content == null) {
			currentContent = contentStack.pop();
			return;
		}

		currentContent.setContent(content);
		this.setContent(content);
		this.loadPage();
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
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		// Login
		if (requestCode == 1234 && resultCode == RESULT_OK) {

			// 如果睇緊 forum, 就REFRESH,  否則 login 完去返討論區首頁.
			if (this.getCurrentUrl().contains("forum")) {
				this.refresh();
			} else {
				this.loadNewUrl(HKEPC.URL);
			}

		}
		
		if (requestCode == R.layout.post && resultCode == RESULT_OK) {
			this.refresh();
		}
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
			
			// URL Handlers
			for (UrlHandler h : urlHandlers) {	
				if (url.contains(h.getUrlMatch())) {
					returnObjs[0] = h.handle(HKEPC.this, url);
					return returnObjs;
				}
			}

			try {
				if (HKEPC.getCookies(HKEPC.this) != null) {
					conn = Jsoup.connect(url).cookies(HKEPC.getCookies(HKEPC.this)).timeout(20000);
				} else {
					conn = Jsoup.connect(url).timeout(20000);
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
			HKEPC.this.hideLoading();
			Document doc = (Document) obj[0];
			String url = (String) obj[1];
			
			pageLoadDone(doc, url);
			checkLogin(doc, url);
			controlUI(doc, url);
			
			
		}
	}
	
	class ReplyTask extends AsyncTask<String, Void, Connection.Response> {
		@Override
		protected Connection.Response doInBackground(String... strs) {
			String page = strs[0];
			String formHash = strs[1];
			String msg = strs[2];
			Connection.Response res = null;
			
			try {
				res = Jsoup.connect(HKEPC.URL + page)
						.cookies(HKEPC.getCookies(HKEPC.this))
						.data("message", msg, "formhash", formHash)
						.method(Method.POST)
						.ignoreHttpErrors(true)
						.execute();		
				//Helper.log(res.parse().toString());
			} catch (Exception e) {
				Log.d("Error", e.toString());
			}
						
			return res;
		}

		protected void onProgressUpdate(Void... progress) {}

		@Override
		protected void onPostExecute(Connection.Response b) {

			if (b != null && b.statusCode() != 200) {
				L.alert(HKEPC.this, "無法送出，請重試。Code: " + b.statusCode() );
				replyFail();
			} else {

				HKEPC.this.refresh();
				replyDone();
			}
		}
	}	
	
	public abstract void replyDone();
	public abstract void replyFail();


	/**
	 * Show Load Dialog
	 */
	public void showLoading() {
		loadingDialog.show();
	}

	/**
	 * Hide Loading Dialog
	 */
	public void hideLoading() {
		loadingDialog.hide();
	}	
	
	/**
	 * @return the cookies
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, String> getCookies(Context c) {
		  SharedPreferences appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(c);
		  Gson gson = new Gson();
		  String json = appSharedPrefs.getString("Cookies", "");
		  return gson.fromJson(json, Map.class);
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
	}

	/**
	 * Get current url
	 * @return
	 */
	public String getCurrentUrl() {
		return this.currentContent.getUrl();
	}
	
}

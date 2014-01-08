package net.louislam.hkepc;

import android.app.ActionBar;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;
import net.louislam.hkepc.action.*;
import net.louislam.hkepc.page.Page;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.Set;
import java.util.TimerTask;
import java.util.TreeSet;

/**
 * MainActivity
 * @author Louis Lam
 */
public class MainActivity extends HKEPC implements OnClickListener {

	/** Menu Object */
	private Menu menu;

	/** */
	private LinearLayout panel;

	/** */
	private Button replyButton;

	/** */
	private EditText replyEditText;

	/** */
	private String mySpaceUrl;

	/** */
	private boolean hasLoggedIn;


	private Button closeAdButton;

	private AdView adView;
	private RelativeLayout adLayout;

	/**
	 * Actions
	 */
	private final Action[] actions = {
		new Login(),
		new Logout(),
		new Refresh(),
		new MySpace(),
		new PM(),
		new About(),
		new Home(),
		new DesktopWebsite(),
		new SavingMode()
	};

	/**
	 *
	 * @param savedInstanceState
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {

		if (Build.VERSION.SDK_INT >= 11) {
			
		} else {
			requestWindowFeature(Window.FEATURE_NO_TITLE);
		}
		
		super.onCreate(savedInstanceState);
		
		if (Build.VERSION.SDK_INT >= 11) {
			styleActionBar();
		} else {
			LinearLayout fakeBar = (LinearLayout) findViewById(R.id.fakeBar);
			fakeBar.setVisibility(LinearLayout.VISIBLE);
		}
		
		replyButton = (Button) findViewById(R.id.replyButton);
		replyButton.setOnClickListener(this);
		replyEditText = (EditText) findViewById(R.id.replyEditText);
		panel = (LinearLayout) findViewById(R.id.panel);
		panel.setVisibility(LinearLayout.GONE);		
		
		for(Page p : pageHandlers) {
			p.setMainActivity(this);
		}

		Uri data = getIntent().getData();
		
		if (data == null)
			this.loadNewUrl(HKEPC.URL);
		else {
			this.loadNewUrl(data.toString());
		}


		// 建立 adView
		adView = new AdView(this, AdSize.BANNER, "a152cd6f14893c0");
		//adView

		// 查詢 LinearLayout (假設您已經提供)
		// 屬性是 android:id="@+id/mainLayout"
		final RelativeLayout layout = (RelativeLayout) findViewById(R.id.adLayout);
		adLayout = layout;

		// 在其中加入 adView
		layout.addView(adView);

		Set<String> keywords = new TreeSet<String>();
		keywords.add("電腦");
		keywords.add("Computer");
		keywords.add("硬件");
		keywords.add("hardware");
		keywords.add("手機");
		keywords.add("mobile");
		keywords.add("相機");
		keywords.add("顯卡");
		keywords.add("3D printer");
		keywords.add("game");

		AdRequest adRequest = new AdRequest();
		adRequest.setKeywords(keywords);

		// 啟用泛用請求，並隨廣告一起載入
		adView.loadAd(adRequest);

		closeAdButton = (Button) findViewById(R.id.button_close_ad);
		closeAdButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				layout.setVisibility(RelativeLayout.GONE);
			}
		});


		timer.schedule(new timerTask(), 120000);

	}

	public class timerTask extends TimerTask {
		public void run() {
			adLayout.setVisibility(RelativeLayout.VISIBLE);
		}
	};

	/**
	 * @return the mySpaceUrl
	 */
	public String getMySpaceUrl() {
		return mySpaceUrl;
	}

	/**
	 * @param mySpaceUrl the mySpaceUrl to set
	 */
	public void setMySpaceUrl(String mySpaceUrl) {
		this.mySpaceUrl = mySpaceUrl;
	}
	
	@Override
	public void pageLoadDone(Document doc, String url) {
		super.pageLoadDone(doc, url);
	}
	
	@Override
	public void webViewPageLoadDone() {
		if (! (hasLoggedIn && currentContent.getUrl().contains("viewthread.php"))) {
			this.hidePanel();
		}		
	}
	
	@Override
	public void onAttachedToWindow() {
	    super.onAttachedToWindow();
	    
	    if (Build.VERSION.SDK_INT < 11) {
		    this.openOptionsMenu();
		    this.closeOptionsMenu();
	    }
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//Log.v("menu", menu.toString());
		MenuInflater inflater = this.getMenuInflater();
		inflater.inflate(R.layout.menu, menu);
		this.menu = menu;

		if (AppSettings.get(this, "SavingMode").equals("true")) {
			menu.findItem(R.id.savingMode).setChecked(true);
		}
		
		return super.onCreateOptionsMenu(menu);
	}
	

	/**
	 * Menu bar actions
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);

		for (Action a : actions) {
			if (a.getId() == item.getItemId()) {
				a.action(this);
			}
		}	
		return true;
	}
	
	/**
	 * @return the menu
	 */
	public Menu getMenu() {
		return menu;
	}

	/**
	 * @param menu the menu to set
	 */
	public void setMenu(Menu menu) {
		this.menu = menu;
	}

	/**
	 *
	 * @param doc
	 * @return
	 */
	public boolean hasLoggedIn(Document doc) {
		Element e = null;
		
		if (doc != null)
			e = doc.select("#umenu a").first();
		
		hasLoggedIn = ! (doc == null || e == null || e.text().equals("註冊"));
		
		return hasLoggedIn;
	}

	/**
	 * Check Login
	 */
	@Override
	public void checkLogin(Document doc, String url) {
		Element e = null;
		
		if (doc != null)
			e = doc.select("#umenu a").first();
		
		// Have not login
		if ( ! this.hasLoggedIn(doc)) {
			menu.findItem(R.id.username).setVisible(false);
			menu.findItem(R.id.login).setVisible(true);
			menu.findItem(R.id.logout).setVisible(false);
			menu.findItem(R.id.msg).setVisible(false);
			
		// Logged in
		} else {
			menu.findItem(R.id.username).setVisible(true);
			menu.findItem(R.id.username).setTitle(e.text());
			this.setMySpaceUrl(HKEPC.URL + e.attr("href"));
			
			menu.findItem(R.id.login).setVisible(false);
			menu.findItem(R.id.logout).setVisible(true);
			menu.findItem(R.id.msg).setVisible(true);
		}
		
	}
	
	@Override
	public void controlUI(Document doc, String url) {

	}
	
	public void showPanel() {
		if (panel.getVisibility() == LinearLayout.GONE)
			panel.setVisibility(LinearLayout.VISIBLE);
	}
	
	public void hidePanel() {
		if (panel.getVisibility() == LinearLayout.VISIBLE)
			panel.setVisibility(LinearLayout.GONE);
	}


	/**
	 * For Android 4.0 only
	 */
	public void styleActionBar() {
		ActionBar bar = this.getActionBar();

		ColorDrawable cd = new ColorDrawable();
		cd.setColor(Color.rgb(142, 195, 31));
		bar.setBackgroundDrawable(cd);
		bar.setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.replyButton) {

			if (replyEditText.getText().toString().equals("")) {
				return;
			}
			
			replyButton.setEnabled(false);
			
			(new ReplyTask()).execute(this.getReplyUrl(), this.getReplyFormHash(), replyEditText.getText().toString());
		}
	}


	@Override
	public void replyDone() {
		replyEditText.setText("");
		replyButton.setEnabled(true);
	}
	
	
}

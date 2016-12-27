package net.louislam.hkepc;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.ServiceConnection;
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
import com.android.vending.billing.IInAppBillingService;
import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;
import com.google.ads.mediation.admob.AdMobAdapterExtras;
import com.google.analytics.tracking.android.EasyTracker;
import net.louislam.android.L;
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

	private IInAppBillingService mService;
	private ServiceConnection mServiceConn;

	private GooglePlay googlePlay;

	private AlertDialog.Builder closeAdsDialog;


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
		new SavingMode(),
		new RemoveAds()
	};

	public static final String UPDATE_TAG = "update1";

	public void firstRun() {
		if (L.getString(this, UPDATE_TAG) == null) {
			L.alert(this, "2014/1/25\n更新功能: \n" +
				"EPC 首頁文章閱讀功能\n(新聞中心, 新品快遞, 專題報導)");
		}

		L.storeString(this, UPDATE_TAG, "1");
	}


	/**
	 *
	 * @param savedInstanceState
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {

		firstRun();

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

		adLayout = (RelativeLayout) findViewById(R.id.adLayout);
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

		googlePlay = new GooglePlay(this);
		googlePlay.checkAds();

		EasyTracker.getInstance(this).activityStart(this);

	}

	public void ads(boolean display) {

		if (!display) {
			adLayout.setVisibility(RelativeLayout.GONE);
			menu.findItem(R.id.remove_ads).setVisible(false);
			timer.cancel();
			return;
		}

		menu.findItem(R.id.remove_ads).setVisible(true);

		if (this.closeAdsDialog == null) {
			AlertDialog.Builder alert = new AlertDialog.Builder(this);
			alert.setTitle("作者的話");
			alert.setMessage("親愛的 EPC 巴打您好\n\n" +
				"感謝您地一直支持 EPC APP。" +
				"作者可是花了很多時間製作這個程式給一眾巴打使用。" +
				"如覺得好用，不妨用一支汽水嘅價錢 (HKD 9.9) 支持下小弟！支持小弟繼續開展下去！" +
				"\n\nLouisLam 上");

			alert.setNegativeButton("永久移除廣告", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					googlePlay.buyNoAds();
				}
			});

			alert.setPositiveButton("暫時隱藏 (諗下先)", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					adLayout.setVisibility(RelativeLayout.GONE);
				}
			});

			closeAdsDialog = alert;
		}



		// 建立 adView
		adView = new AdView(this, AdSize.BANNER, "a152cd6f14893c0");
		//adView

		// 查詢 LinearLayout (假設您已經提供)
		// 屬性是 android:id="@+id/mainLayout"
		final RelativeLayout layout = adLayout;

		// 在其中加入 adView
		layout.addView(adView);

		Set<String> keywords = new TreeSet<String>();
		keywords.add("電腦");
		keywords.add("Computer");
		keywords.add("硬件");
		keywords.add("hardware");
		keywords.add("mobile");
		keywords.add("android");
		keywords.add("apple");
		keywords.add("samsung");
		keywords.add("手機");
		keywords.add("mobile");
		keywords.add("相機");
		keywords.add("顯卡");
		keywords.add("game");

		AdRequest adRequest = new AdRequest();
		adRequest.setKeywords(keywords);


		// AdMob network
		AdMobAdapterExtras adMob = new AdMobAdapterExtras();
		adRequest.setNetworkExtras(adMob);

		// 啟用泛用請求，並隨廣告一起載入
		adView.loadAd(adRequest);


		closeAdButton = (Button) findViewById(R.id.button_close_ad);
		closeAdButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				closeAdsDialog.show();
			}
		});

		adLayout.setVisibility(RelativeLayout.VISIBLE);
		timer.schedule(new timerTask(), 900000);

	}


	@Override
	public void onDestroy() {
		super.onDestroy();

	}


	public class timerTask extends TimerTask {
		public void run() {
			runOnUiThread(new Runnable() {
				public void run() {
					adLayout.setVisibility(RelativeLayout.VISIBLE);
					timer.schedule(new timerTask(), 900000);
				}
			});
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


	public void buy() {
		googlePlay.buyNoAds();
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

		// 如果唔係 Forum, 唔洗 check
		if ( ! url.contains("forum")) {
			return;
		}
		
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

	@Override
	public void replyFail() {
		replyButton.setEnabled(true);
	}

	@Override
	public void onStop() {
		super.onStop();
		EasyTracker.getInstance(this).activityStop(this);
	}

	
}

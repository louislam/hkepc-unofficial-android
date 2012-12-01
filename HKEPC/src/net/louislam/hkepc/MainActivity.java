package net.louislam.hkepc;

import net.louislam.hkepc.action.*;
import net.louislam.hkepc.page.Page;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

/**
 * MainActivity
 * @author Louis Lam
 */
public class MainActivity extends HKEPC implements OnClickListener {

	private Menu menu;
	private LinearLayout panel;
	private Button replyButton;
	private EditText replyEditText;
	
	private String mySpaceUrl;
	
	private boolean hasLoggedIn;
	
	private final Action[] actions = {
		new Login(),
		new Logout(),
		new Refresh(),
		new MySpace(),
		new About(),
		new Home(),
		new DesktopWebsite()
	};

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
		

		
		//openOptionsMenu();
		
		Uri data = getIntent().getData();
		
		if (data == null)
			this.loadNewUrl(HKEPC.URL);
		else {
			this.loadNewUrl(data.toString());
		}
	}
	
	
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
		Log.v("menu", menu.toString());
		MenuInflater inflater = this.getMenuInflater();
		inflater.inflate(R.layout.menu, menu);
		this.menu = menu;

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
	
	public boolean hasLoggedIn(Document doc) {
		Element e = null;
		
		if (doc != null)
			e = doc.select("#umenu a").first();
		
		hasLoggedIn = ! (doc == null || e == null || e.text().equals("µù¥U"));
		
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
	@TargetApi(11)
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
		// TODO Auto-generated method stub
		replyEditText.setText("");
		replyButton.setEnabled(true);
	}
	
	
}

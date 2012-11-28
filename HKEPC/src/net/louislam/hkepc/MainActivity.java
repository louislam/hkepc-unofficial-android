package net.louislam.hkepc;

import net.louislam.hkepc.action.*;
import net.louislam.hkepc.page.Page;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.LinearLayout;

/**
 * MainActivity
 * @author Louis Lam
 */
public class MainActivity extends HKEPC {

	private Menu menu;
	private LinearLayout panel;
	
	private final Action[] actions = {
		new Login(),
		new Logout(),
		new Refresh()
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		for(Page p : pageHandlers) {
			p.setMainActivity(this);
		}
		
		panel = (LinearLayout) findViewById(R.id.panel);
		panel.setVisibility(LinearLayout.GONE);
		
		if (Build.VERSION.SDK_INT >= 11) {
			styleActionBar();
		}
		//openOptionsMenu();
		this.loadNewUrl(HKEPC.URL);
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
		
		return ! (doc == null || e == null || e.text().equals("µù¥U"));
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
			
			for (int i = 0; i <menu.size(); i++) {
				Log.v("Item", menu.getItem(i).getTitle().toString());
			}
			
			menu.findItem(R.id.username).setVisible(false);
			menu.findItem(R.id.login).setVisible(true);
			menu.findItem(R.id.logout).setVisible(false);
			menu.findItem(R.id.msg).setVisible(false);
			
		// Logged in
		} else {
			menu.findItem(R.id.username).setVisible(true);
			menu.findItem(R.id.username).setTitle(e.text());
			menu.findItem(R.id.login).setVisible(false);
			menu.findItem(R.id.logout).setVisible(true);
			menu.findItem(R.id.msg).setVisible(true);
		}
		
		menu.findItem(R.id.post).setVisible(false);
		menu.findItem(R.id.reply).setVisible(false);
	}
	
	@Override
	public void controlUI(Document doc, String url) {

	}
	
	public void showPanel() {
		panel.setVisibility(LinearLayout.VISIBLE);
	}
	
	public void hidePanel() {
		panel.setVisibility(LinearLayout.GONE);
	}
	
	public void enable(int id) {
		menu.findItem(R.id.post).setVisible(false);
		menu.findItem(R.id.reply).setVisible(false);
		
		menu.findItem(id).setVisible(true);
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
	}
}

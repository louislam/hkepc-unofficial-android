package net.louislam.hkepc;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

/**
 * 
 * @author Louis Lam
 *
 */
public class MainActivity extends HKEPC {

	private Menu menu;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.loadNewUrl(HKEPC.URL);
		
		if (Build.VERSION.SDK_INT >= 11) {
			styleActionBar();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
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

		// Login Button
		if (item.getItemId() == R.id.login) {
			Intent intent = new Intent(this, LoginActivity.class);
			startActivity(intent);
			//this.loadNewUrl(HKEPC.URL + "logging.php?action=login");
	                
		} else if (item.getItemId() == R.id.refresh) {
			this.refresh();
		}
		
		return true;
	}
	
	/**
	 * Check Login
	 */
	@Override
	public void checkLogin(Document doc) {
		Element e = doc.select("#umenu a").first();
		
		if (e != null && e.text().equals("µù¥U")) {
			menu.findItem(R.id.username).setVisible(false);
			menu.findItem(R.id.login).setVisible(true);
			menu.findItem(R.id.logout).setVisible(false);
		} else {
			menu.findItem(R.id.username).setVisible(true);
			menu.findItem(R.id.username).setTitle(e.text());
			menu.findItem(R.id.login).setVisible(false);
			menu.findItem(R.id.logout).setVisible(true);
		}
		
		menu.findItem(R.id.post).setVisible(false);
		menu.findItem(R.id.reply).setVisible(false);
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

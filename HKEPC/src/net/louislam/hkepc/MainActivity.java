package net.louislam.hkepc;

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
			this.loadNewUrl(HKEPC.URL + "logging.php?action=login");
	                
		} else if (item.getItemId() == R.id.refresh) {
			this.refresh();
		}
		
		return true;
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

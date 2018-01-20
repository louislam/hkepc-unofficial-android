package net.louislam.hkepc;

import net.louislam.android.L;
import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;


/**
*
 */
public class AboutActivity extends Activity {

	TextView textViewVersion;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.about);

		textViewVersion = (TextView) findViewById(R.id.textViewVersion);
		textViewVersion.setText("版本: " + L.getAppVersion(this));
	}

}

package net.louislam.hkepc.action;

import android.content.Intent;
import android.net.Uri;
import net.louislam.hkepc.HKEPC;
import net.louislam.hkepc.MainActivity;
import net.louislam.hkepc.R;

public class DesktopWebsite implements Action {

	@Override
	public int getId() {
		return R.id.viewInBrowser;
	}

	@Override
	public void action(MainActivity a) {
		Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(a.getCurrentUrl()));
		a.startActivity(browserIntent);
	}

}

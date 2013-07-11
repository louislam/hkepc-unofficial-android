package net.louislam.hkepc.action;

import net.louislam.hkepc.AboutActivity;
import net.louislam.hkepc.MainActivity;
import net.louislam.hkepc.R;
import android.content.Intent;

public class About implements Action {

	@Override
	public int getId() {
		return R.id.About;
	}

	@Override
	public void action(MainActivity a) {
		Intent intent = new Intent(a, AboutActivity.class);
		a.startActivity(intent);
	}

}

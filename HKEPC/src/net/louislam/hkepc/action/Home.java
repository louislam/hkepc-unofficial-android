package net.louislam.hkepc.action;

import net.louislam.hkepc.HKEPC;
import net.louislam.hkepc.MainActivity;
import net.louislam.hkepc.R;

public class Home implements Action {

	@Override
	public int getId() {
		return android.R.id.home;
	}

	@Override
	public void action(MainActivity a) {
		a.loadNewUrl(HKEPC.URL);
	}

}

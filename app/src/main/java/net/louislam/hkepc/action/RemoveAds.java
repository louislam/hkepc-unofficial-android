package net.louislam.hkepc.action;

import net.louislam.hkepc.MainActivity;
import net.louislam.hkepc.R;

public class RemoveAds implements Action {

	@Override
	public int getId() {
		return R.id.remove_ads;
	}

	@Override
	public void action(MainActivity a) {
		a.buy();
	}

}

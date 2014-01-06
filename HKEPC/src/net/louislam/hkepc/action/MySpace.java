package net.louislam.hkepc.action;

import net.louislam.hkepc.MainActivity;
import net.louislam.hkepc.R;

public class MySpace implements Action {

	@Override
	public int getId() {
		return R.id.username;
	}

	@Override
	public void action(MainActivity a) {
		a.loadNewUrl(a.getMySpaceUrl());
	}

}

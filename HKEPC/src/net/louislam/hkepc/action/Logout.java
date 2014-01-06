package net.louislam.hkepc.action;

import net.louislam.hkepc.HKEPC;
import net.louislam.hkepc.MainActivity;
import net.louislam.hkepc.R;

public class Logout implements Action {

	@Override
	public int getId() {
		return R.id.logout;
	}

	@Override
	public void action(MainActivity a) {
		HKEPC.setCookies(null, a);
		a.refresh();
	}

}

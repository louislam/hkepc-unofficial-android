package net.louislam.hkepc.action;

import net.louislam.hkepc.MainActivity;
import net.louislam.hkepc.R;

public class Refresh implements Action {

	@Override
	public int getId() {
		return R.id.refresh;
	}

	@Override
	public void action(MainActivity a) {
		a.refresh();
	}

}

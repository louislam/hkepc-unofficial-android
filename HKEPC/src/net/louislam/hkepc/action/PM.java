package net.louislam.hkepc.action;

import net.louislam.hkepc.HKEPC;
import net.louislam.hkepc.MainActivity;
import net.louislam.hkepc.R;

public class PM implements Action {

	@Override
	public int getId() {
		return R.id.msg;
	}

	@Override
	public void action(MainActivity a) {
		a.loadNewUrl(HKEPC.URL + "pm.php");
	}

}

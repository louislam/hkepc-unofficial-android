package net.louislam.hkepc.action;

import net.louislam.hkepc.HKEPC;
import net.louislam.hkepc.MainActivity;
import net.louislam.hkepc.R;

public class My implements Action {

	@Override
	public int getId() {
		return 0;
	}

	@Override
	public void action(MainActivity a) {
		a.loadNewUrl(HKEPC.URL + "my.php?item=threads");
	}

}

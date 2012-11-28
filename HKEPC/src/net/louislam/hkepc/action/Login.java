package net.louislam.hkepc.action;

import net.louislam.hkepc.LoginActivity;
import net.louislam.hkepc.MainActivity;
import net.louislam.hkepc.R;
import android.content.Intent;

public class Login implements Action {

	@Override
	public int getId() {
		return R.id.login;
	}

	@Override
	public void action(MainActivity a) {
		Intent intent = new Intent(a, LoginActivity.class);
		a.startActivity(intent);
	}

}

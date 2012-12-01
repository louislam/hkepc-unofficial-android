package net.louislam.hkepc.action;

import android.view.MenuItem;
import net.louislam.hkepc.AppSettings;
import net.louislam.hkepc.HKEPC;
import net.louislam.hkepc.MainActivity;
import net.louislam.hkepc.R;

public class SavingMode implements Action {

	@Override
	public int getId() {
		return R.id.savingMode;
	}

	@Override
	public void action(MainActivity a) {
		MenuItem i =  a.getMenu().findItem(R.id.savingMode);
		i.setChecked( (! i.isChecked()) );

		
		AppSettings.set(a, "SavingMode", i.isChecked() + "");
		//a.refresh();
	}

}

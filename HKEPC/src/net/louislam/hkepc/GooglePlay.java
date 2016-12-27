package net.louislam.hkepc;

import android.app.ProgressDialog;
import mc.inappbilling.Inventory;
import mc.inappbilling.OnCompleteListener;
import mc.inappbilling.Purchase;
import mc.inappbilling.v3.InAppBillingV3Impl;
import net.louislam.android.L;

/**
 * Created by Louis Lam on 1/11/14.
 */
public class GooglePlay {

	private static final String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA5KLEFHLMzqKOk05y2h40ephyw289YxXOoH3FB+lf7EPhj8t0kHJGHe1ZNyKDLj7YZ99088r1HOhMRnmAMjIKwJ6CNU24bN/0IZO9qcL8/iIoIfTA7zoW/u7tuww6gJim1y2jPrX2Zu1D4z7oNw8XfSANxTZ6lfkLlLS51l8YWqQRQeSgv+eFR6zUBMDkW7sZFIK6lPkCTaNUQPfKqkE56PnpbByyzEkb6IlK44g3Q6cw/4X60STZOEdyfuNnBAD01up0DWCoj9VWc0RJHcqPXuy2dMIDOUh+Gvvfm1WdwmmKWFsvZPqAOVZ3dVO8WBvi3TMrMiFFTCcYNwjgeuNJuQIDAQAB";
	public static final String ERROR_MSG = "Error: 不能連接到 Google Play 服務, 請重試.";
	private static final String SKU = "noads";
	//private static final String SKU = "android.test.purchased";

	private MainActivity context;

	private ProgressDialog loading;

	public GooglePlay(MainActivity context) {
		this.context = context;
	}


	/**
	 * In app purchase
	 * @deprecated
	 */
	public void buyNoAds() {

	}

	/**
	 * @deprecated
	 */
	public void checkAds() {

	}

}

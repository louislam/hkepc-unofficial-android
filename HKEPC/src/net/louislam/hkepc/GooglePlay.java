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
	//private static final String SKU = "noads";
	private static final String SKU = "android.test.purchased";

	private MainActivity context;

	private ProgressDialog loading;

	public GooglePlay(MainActivity context) {
		this.context = context;

	}


	/**
	 * In app purcharse
	 */
	public void buyNoAds() {
		loading = L.progressDialog(context, "連接到 Google Play 中");

		InAppBillingV3Impl.createNewInAppBillingV3Impl(
			context, base64EncodedPublicKey, new OnCompleteListener<InAppBillingV3Impl>() {

			@Override
			public void complete(final InAppBillingV3Impl inAppBillingV3) {
				inAppBillingV3.buyProduct(SKU, new OnCompleteListener<Purchase>() {

					@Override
					public void complete(Purchase purchase) {
						L.alert(GooglePlay.this.context, "謝謝! 購買成功");
						loading.hide();
						context.ads(false);

						/*inAppBillingV3.consumePurchase(purchase, new OnCompleteListener<Boolean>() {
							@Override
							public void complete(Boolean aBoolean) {

							}

							@Override
							public void error(int i) {

							}
						});*/
					}

					@Override
					public void error(int i) {
						L.alert(GooglePlay.this.context, "付款不成功，請再試。");
						loading.hide();
					}
				});
			}

			@Override
			public void error(int i) {
				//L.alert(GooglePlay.this.context, ERROR_MSG);
			}
		});
	}

	public void checkAds() {
		InAppBillingV3Impl.createNewInAppBillingV3Impl(
			context, base64EncodedPublicKey, new OnCompleteListener<InAppBillingV3Impl>() {

			@Override
			public void complete(final InAppBillingV3Impl inAppBillingV3) {
				inAppBillingV3.getPurchases(new OnCompleteListener<Inventory>() {
					@Override
					public void complete(Inventory inventory) {
						context.ads( ! inventory.hasPurchase(SKU));
					}

					@Override
					public void error(int i) {
						//L.alert(GooglePlay.this.context, ERROR_MSG);
						context.ads(true);
					}
				});
			}

			@Override
			public void error(int i) {
				//L.alert(GooglePlay.this.context, ERROR_MSG);
				context.ads(true);
			}
		});

	}

}

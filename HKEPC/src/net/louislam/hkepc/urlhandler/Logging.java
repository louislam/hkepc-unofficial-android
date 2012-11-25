package net.louislam.hkepc.urlhandler;

import net.louislam.hkepc.AppLayout;
import net.louislam.hkepc.Helper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import android.content.Context;

public class Logging implements UrlHandler {

	@Override
	public String getUrlMatch() {
		return "logging.php?action=login";
	}

	@Override
	public Document handle(Context c, String url) {
		AppLayout al = new AppLayout(c);
		String html = Helper.loadHtmlFile(c, "www/login.html");
		
		al.content(html);
		
		return Jsoup.parse(al.html());
	}
}

package net.louislam.hkepc.urlhandler;

import org.jsoup.nodes.Document;

import android.content.Context;

public interface UrlHandler {

	public String getUrlMatch();
	public Document handle(Context c, String url);
	
}

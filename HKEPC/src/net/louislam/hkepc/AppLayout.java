package net.louislam.hkepc;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

public class AppLayout {
	
	private String contentTag = "{Content}";
	
	/** Layout HTML */
	private String html;
	
	private String content = "";
	
	public AppLayout(Context c) {
		AssetManager am = c.getAssets();
		
		InputStream inStream = null;
		try {
			inStream = am.open("www/index.html");
		} catch (IOException e) {
			e.printStackTrace();
		}
		Scanner scanner = new Scanner(inStream);
		
		html = "";
		
		while(scanner.hasNextLine()) {
			html += scanner.nextLine();
		}
			
	}
	
	public String content() {
		return content;
	}
	
	public void content(String c) {
		this.content = c;
	}
	
	public Document document() {
		return Jsoup.parse(this.html());
	}
	
	public String html() {
		String newHtml = html.replace(contentTag, content);
		return newHtml;
	}
}

package net.louislam.hkepc;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

/**
 * 
 * @author Louis Lam
 *
 */
public class Helper {

	/** 
	 * Load html file 
	 * 
	 * */
	public static String loadHtmlFile(Context c, String path) {
		StringBuilder sb = new StringBuilder();
		
		AssetManager am = c.getAssets();
		
		InputStream inStream = null;
		try {
			inStream = am.open(path);
		} catch (IOException e) {
		}
		Scanner scanner = new Scanner(inStream);
		
		while(scanner.hasNextLine()) {
			sb.append(scanner.nextLine());
		}
		
		return sb.toString();
	}
	
	public static String listViewDivider(String name) {
		return "<li class=\"divider\">" + name + "</li>";
	}
	
	public static String listViewItem(String name, String href) {
		return "<li><a href=\"" + href + "\">" + name + "</a></li>";
	}
	
	public static String listViewStartTag() {
		return "<ul>";
	}
	
	public static String li(String str) {
		return "<li>" + str + "</li>";
	}
	
	public static String li(String str, String bgColorCode, String textColorCode) {
		StringBuilder sb = new StringBuilder();
		sb.append("<li style=\"background-color: ");
		sb.append(bgColorCode);
		sb.append("; color:");
		sb.append(textColorCode);
		sb.append(";\">");
		sb.append(str);
		sb.append("</li>");
		return sb.toString();
	}
	
	public static void appendNav(StringBuilder sb, Document doc) {
		
		sb.append(Helper.listViewDivider("¾ÉÄý"));
		
		// Nav
		Elements navs = doc.select("#nav a");
		//sb.append("<div id=\"nav\")>");
		for (Element nav : navs) {
			sb.append("<li>" + nav.toString() + "</li>");
		}
		//sb.append("<div style=\"clear:both\"></div></div>");

	}
	
	public static void appendPaging(StringBuilder sb, Document doc) {
		Elements pagings = doc.select(".pages");
		
		if (pagings != null && pagings.size() > 0) {
			Element paging = pagings.first();
			
			Elements prevs = paging.select(".prev");
					
			if (prevs != null && prevs.size() > 0) {
				prevs.first().html("<");
			}
			
			sb.append(Helper.li(paging.toString()));
		}
	}
	
	public static String clear() {
		return "<div style=\"clear:both\"></div>";
	}
	
	public static void log(String veryLongString) {
		int maxLogSize = 1000;
		    for(int i = 0; i <= veryLongString.length() / maxLogSize; i++) {
		        int start = i * maxLogSize;
		        int end = (i+1) * maxLogSize;
		        end = end > veryLongString.length() ? veryLongString.length() : end;
		        Log.v("HTML", veryLongString.substring(start, end));
		    }
	}
}

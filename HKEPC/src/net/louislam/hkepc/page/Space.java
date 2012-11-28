package net.louislam.hkepc.page;

import net.louislam.hkepc.HKEPC;
import net.louislam.hkepc.Helper;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class Space extends Page {

	public String getId() {
		return "profile";
	}

	public String getContent(Document doc) {
		StringBuilder sb = new StringBuilder();
		
		Element myPost = doc.select("#profile_act .searchpost a").first().text("§Úªº¤å³¹");
		
		sb.append("<ul><li>");
		sb.append(myPost.toString());
		sb.append("</li></ul>");
		
		return sb.toString();
	}


}

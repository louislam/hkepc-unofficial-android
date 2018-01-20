package net.louislam.hkepc.page;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;


public class Space extends Page {

	public String getId() {
		return "profile";
	}

	public String getContent(Document doc) {
		StringBuilder sb = new StringBuilder();
		
		Element myPost = doc.select("#profile_act .searchpost a").first().text("我的文章");
		
		sb.append("<ul><li>");
		sb.append(myPost.toString());
		sb.append("</li></ul>");
		
		return sb.toString();
	}


}

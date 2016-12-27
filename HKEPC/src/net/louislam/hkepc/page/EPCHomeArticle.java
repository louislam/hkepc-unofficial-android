package net.louislam.hkepc.page;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class EPCHomeArticle extends Page {

	public String getId() {
		return "/";
	}

	public String getContent(Document doc) {
		StringBuilder sb = new StringBuilder();


		sb.append("<div class=\"epc-home\"><div class=\"article\">");
		
		Elements items = doc.select("#article");

		sb.append(items.html());

		sb.append("</div></div>");

		return sb.toString();
	}

}

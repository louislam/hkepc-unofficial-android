package net.louislam.hkepc.page;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class EPCHome extends Page {

	public String getId() {
		return "/moreNews";
	}

	public String getContent(Document doc) {
		StringBuilder sb = new StringBuilder();


		sb.append("<div class=\"epc-home\">");
		
		Elements items = doc.select("#items");

		sb.append(items.html());

		sb.append("<div class=\"pages\">");
		sb.append(doc.select(".paginate").first());
		sb.append("</div>");

		sb.append("</div>");

		return sb.toString();
	}

}

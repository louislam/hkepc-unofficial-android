package net.louislam.hkepc.page;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;


public class PM extends Page {

	public String getId() {
		return "pm";
	}

	public String getContent(Document doc) {
		StringBuilder sb = new StringBuilder();
		doc.select(".avatar").remove();
		doc.select(".action").remove();
		Elements pmList = doc.select(".pm_list");

		sb.append(pmList);
		
		return sb.toString();
	}


}

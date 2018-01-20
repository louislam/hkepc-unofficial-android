package net.louislam.hkepc.page;

import org.jsoup.nodes.Document;


public class Last extends Page {

	public String getId() {
		return "*";
	}

	public String getContent(Document doc) {
		return doc.html();
	}


}

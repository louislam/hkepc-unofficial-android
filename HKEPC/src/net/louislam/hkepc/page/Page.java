package net.louislam.hkepc.page;

import org.jsoup.nodes.Document;

public interface Page {

	public String getId();
	public String getContent(Document doc);
	
}

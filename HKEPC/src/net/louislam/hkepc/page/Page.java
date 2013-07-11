package net.louislam.hkepc.page;

import net.louislam.hkepc.MainActivity;

import org.jsoup.nodes.Document;

public abstract class Page {

	protected MainActivity a;
	
	public abstract String getId();
	public abstract String getContent(Document doc);
	
	public void setMainActivity(MainActivity a) {
		this.a = a;
	}
	
}

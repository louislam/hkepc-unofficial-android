package net.louislam.hkepc;

import org.jsoup.nodes.Document;

public class DocumentCache {
	
	private String url;
	private Document document;
	
	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}
	
	/**
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * @return the document
	 */
	public Document getDocument() {
		return document;
	}

	/**
	 * @param document2 the document to set
	 */
	public void setDocument(Document document2) {
		this.document = document2;
	}
	
	
	
}

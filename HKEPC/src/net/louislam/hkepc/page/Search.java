package net.louislam.hkepc.page;

import net.louislam.hkepc.Helper;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class Search extends Page {

	public String getId() {
		return "search";
	}

	public String getContent(Document doc) {
		StringBuilder sb = new StringBuilder();
		Element item;
		Element author;
		Element date;
		
		sb.append("<ul>");

		// Post List
		Elements groups = doc.select(".datatable tbody");

		for (Element g : groups) {
			item = g.select(".subject a").first();
			
			if (item != null) {
				author = g.select(".author a").first();
				date = g.select(".author em").first();
				//num = g.select(".nums").first();
				item.html(item.html() + "<br /><span style=\"color: #AAA; text-decoration: none; font-size: 0.8em\">" + author.html() + " - " +date + "</span>");
				
				sb.append( "<li>" + item+ "</li>");
			} else
				sb.append(Helper.listViewDivider(""));
		}
		
		// Paging
		Helper.appendPaging(sb, doc);
		sb.append(Helper.clear());
		
		sb.append("</ul>");
			
		return sb.toString();
	}


}

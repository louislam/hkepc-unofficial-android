package net.louislam.hkepc.page;

import net.louislam.hkepc.Helper;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ForumDisplay implements Page{

	public String getId() {
		return "forumdisplay";
	}

	public String getContent(Document doc) {
		StringBuilder sb = new StringBuilder();
		Element item;
		Element author;
		sb.append("<ul>");
		
		// Nav
		Helper.appendNav(sb, doc);
		
		// Sub Forum
		Elements subForums = doc.select("table[summary=subform] h2 a");
		
		if (subForums.size() >= 1) {
			sb.append(Helper.listViewDivider("¤lª©¶ô"));
		}
		
		for (Element sub : subForums) {
			sb.append("<li>" + sub + "</li>");
		}
		
		// Post List
		Elements groups = doc.select(".datatable tbody");

		for (Element g : groups) {
			item = g.select(".subject span a").first();
			
			if (item != null) {
				author = g.select(".author a").first();
				item.html(item.html() + "<br /><span style=\"color: #AAA; text-decoration: none\">" + author.html() + "</span>");
				
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

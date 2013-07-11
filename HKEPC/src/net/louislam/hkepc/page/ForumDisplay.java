package net.louislam.hkepc.page;

import net.louislam.hkepc.Helper;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ForumDisplay extends Page {

	public String getId() {
		return "forumdisplay";
	}

	public String getContent(Document doc) {
		
		boolean first = true;
		
		StringBuilder sb = new StringBuilder();
		Element item;
		Element author;
		Element date;
		Element num;
		sb.append("<div class=\"padding\"><ul>");
		
		// Nav
		Helper.appendNav(sb, doc);
		
		// Function
		Element postBtn = doc.select("#newspecial").first();
		
		if (postBtn != null) {
			sb.append(Helper.listViewDivider("功能"));
			sb.append(Helper.li(postBtn.html()));
		}
		
		// Sub Forum
		Elements subForums = doc.select("table[summary=subform] h2 a");
		if (subForums.size() >= 1) {
			sb.append(Helper.listViewDivider("子版塊"));
		}
		
		for (Element sub : subForums) {
			sb.append("<li>" + sub + "</li>");
		}
		
		sb.append("</ul></div>");
		sb.append("<ul>");
		
		// Post List
		Elements groups = doc.select(".datatable tbody");
		
		for (Element g : groups) {
			item = g.select(".subject span a").first();
			
			if (item != null) {
				author = g.select(".author a").first();
				date = g.select(".author em").first();
				num = g.select(".nums").first();
				sb.append("<li>");
				
				sb.append(item.html(item.html() + 
						"<br /><span class=\"box\">" + author.html() + "</span> " +
						"<span class=\"box\">" + date + "</span> " +
						"<span class=\"box\">" + num + "</span> "));
				
				sb.append("</li>");
			} else {	
				if (first) {
					sb.append(Helper.listViewDivider("置頂文章"));
					first = false;
				} else {
					sb.append(Helper.listViewDivider("文章"));
				}	
			}
		}
		
		// Paging
		Helper.appendPaging(sb, doc);
		sb.append(Helper.clear());
		
		// Print Error
		sb.append(Helper.li(doc.select(".alert_error").html()));
		
		sb.append("</ul>");
			
		return sb.toString();
	}


}

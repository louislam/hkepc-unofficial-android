package net.louislam.hkepc.page;

import net.louislam.hkepc.HKEPC;
import net.louislam.hkepc.Helper;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class ViewThread extends Page {

	public String getId() {
		return "viewthread";
	}

	public String getContent(Document doc) {
		
		// Show Reply panel
		if (doc.select(".replybtn").size() > 0) {
			a.showPanel();
		}
		
		// Get Reply Form post url
		a.setReplyUrl(doc.select("#fastpostform").attr("action"));
		a.setReplyFormHash(doc.select("input[name=formhash]").attr("value"));
		
		StringBuilder sb = new StringBuilder();
		sb.append("<ul>");
		String item;
		String authorName;
		String time;
		String msg;
		Elements imgs;
		
		// Nav
		Helper.appendNav(sb, doc);
		
		Elements posts = doc.select("#postlist > div");
		
		// Remove User Reaction function
		posts.select(".useraction").remove();
		posts.select(".imgtitle").remove();
		
		for (Element g : posts) {
			
			// Author Details
			authorName = g.select(".postauthor .postinfo a").first().text();
			time = g.select(".authorinfo em").first().html().toString().replace("µoªí©ó ", "");
			sb.append("<li class=\"divider\">" + authorName + " (" + time + ")</li>");
			
			// Remove ads
			g.select(".adv").remove();
			
			// Image handling
			imgs = g.select(".postmessage img");
			
			for (Element img : imgs) {
				if ( img.hasAttr("file")) {
					img.attr("src", img.attr("file"));
				} else {
					img.attr("src", img.attr("src"));
				}

				if ( ! img.attr("src").startsWith("http")) {
					img.attr("src", HKEPC.URL + img.attr("src"));
				}
			}
			
			msg = g.select(".postmessage").html();
			
			// Post Content
			item = "<li class=\"post\">" +  msg + "</li>";
			sb.append(item);
		}
		
		// Paging
		Helper.appendPaging(sb, doc);
		sb.append(Helper.clear());
		
		sb.append("</ul>");
		return sb.toString();
	}


}

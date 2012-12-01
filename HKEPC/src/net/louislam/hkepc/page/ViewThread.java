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

		Elements replybtns = doc.select(".replybtn");
		
		// Show Reply panel
		if (replybtns.size() > 0) {
			a.showPanel();
		}
		
		// Get Reply Form post url
		a.setReplyUrl(doc.select("#fastpostform").attr("action"));
		a.setReplyFormHash(doc.select("input[name=formhash]").attr("value"));
		
		StringBuilder sb = new StringBuilder();
		
		String item;
		String authorName;
		String time;
		String msg;
		Elements imgs;
		//Element profilePic;
		
		// Nav
		sb.append("<div class=\"padding\"><ul>");
		Helper.appendNav(sb, doc);
		
		
		if (replybtns.size() > 0) {
			sb.append(Helper.listViewDivider("功能"));
			sb.append(Helper.li(replybtns.first().html()));
		}
		
		sb.append("</ul></div>");
		
		Elements posts = doc.select("#postlist > div");
		
		// Remove User Reaction function
		posts.select(".useraction").remove();
		posts.select(".imgtitle").remove();
		
		sb.append("<ul>");
		
		// For each post
		for (Element g : posts) {
			
			// Author Details
			authorName = g.select(".postauthor .postinfo a").first().text();
			time = g.select(".authorinfo em").first().html().toString().replace("發表於 ", "");
			//profilePic = g.select(".popavatar img").first();
			
			sb.append("<li class=\"divider\">" + authorName + " (" + time + ")<br />");
			sb.append(g.select(".postact em"));
			sb.append("</li>");
			
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
		
		// Print Error
		sb.append(Helper.li(doc.select(".alert_error").html()));
		
		sb.append("</ul>");
		return sb.toString();
	}


}

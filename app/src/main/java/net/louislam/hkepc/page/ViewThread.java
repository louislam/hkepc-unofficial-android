package net.louislam.hkepc.page;

import net.louislam.hkepc.AppSettings;
import net.louislam.hkepc.HKEPC;
import net.louislam.hkepc.Helper;
import net.louislam.hkepc.util.Util;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ViewThread extends Page {
    static Pattern threadLinkPattern = Pattern.compile("viewthread\\.php\\?tid=(\\d+)");

	public String getId() {
		return "viewthread";
	}

	public String getContent(Document doc) {
        initDbHelper();
        setRead(doc);

		Elements replybtns = doc.select(".replybtn");
		
		// Show Reply panel
		if (replybtns.size() > 0) {
            mainActivity.showPanel();
		}
		
		// Get Reply Form post url
        mainActivity.setReplyUrl(doc.select("#fastpostform").attr("action"));
        mainActivity.setReplyFormHash(doc.select("input[name=formhash]").attr("value"));
		
		StringBuilder sb = new StringBuilder();
		
		String item;
		String authorName;
		String time;
		String msg;
		String floor;
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

			// Time
			time = g.select(".authorinfo em").first().html().toString().replace("發表於 ", "");

			// 樓層
			floor = g.select(".postinfo em").first().text();

			// Profile pic ?
			//profilePic = g.select(".popavatar img").first();
			
			sb.append("<li class=\"divider\">" + authorName + " (" + time + ") <div style=\"float: right\">#" + floor + "</div><br />");

			// Reply
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

				// handle protocol-relative path
				if (img.attr("src").startsWith("//")) {
					img.attr("src", "https:" + img.attr("src"));
				}

				if ( ! img.attr("src").startsWith("http")) {
					img.attr("src", HKEPC.URL + img.attr("src"));
				}
			}
				
			if (AppSettings.get(this.mainActivity, "SavingMode").equals("true")) {
				String link;
				
				for (Element img : imgs) {
					
					// Icon not hide
					if (img.hasAttr("smilieid")) {
						continue;
					}
					
					link = img.attr("src");
					
					img.after("<a target=\"_blank\" href=\"" + link + "\">[圖片]</a>");
					img.remove();
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

    private int getThreadId(Document doc){
        String s = doc.select(".authorinfo a").first().attr("href");
        int ret = -1;
        Matcher matcher = threadLinkPattern.matcher(s);
        if(matcher.find()){
            ret = Util.convertInt(matcher.group(1));
        }
        return ret;
    }

    private int getPageNo(Document doc){
        Element page = doc.select(".pages strong").first();
        int pageNo = 1;
        if(page != null){
            String s = page.html();
            pageNo = Util.convertInt(s);
        }
        return pageNo;
    }

    private void setRead(Document doc){
        int pageNo = getPageNo(doc);
        int threadId = getThreadId(doc);
        if(threadId > -1 && pageNo > 0){
            dbHelper.setReadThread(threadId, pageNo);
        }
    }
}

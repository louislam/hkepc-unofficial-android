package net.louislam.hkepc.page;

import android.util.Log;
import net.louislam.hkepc.Helper;

import net.louislam.hkepc.db.DatabaseHelper;
import net.louislam.hkepc.util.Util;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ForumDisplay extends Page {
    static Pattern threadLinkPattern = Pattern.compile("viewthread\\.php\\?tid=(\\d+)&extra=page%3D1");

	public String getId() {
		return "forumdisplay";
	}

	public String getContent(Document doc) {
		int threadId;
        String link;
		boolean first = true;

        initDbHelper();

        StringBuilder sb = new StringBuilder();
		Element item;
		Element author;
		Element date;
		Element num;
        Element lastReplyDate;
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
                link = item.attr("href");
                threadId = getThreadId(link);
				author = g.select(".author a").first();
				date = g.select(".author em").first();
				num = g.select(".nums strong").first();
                lastReplyDate = g.select(".lastpost em span").first();
				sb.append("<li>");

                //update the thread link to last read page
                item.attr("href", link + getPageSuffix(threadId));

                Log.d("FD", "Update link: " + item.attr("href"));

				sb.append(item.html(item.html() + 
						"<br /><span class=\"box\">" + author.html() + "</span> " +
//						"<span class=\"box\">" + date + "</span> |" +
						"<span class=\"box\">| 回覆: " + num + "</span> " +
                        "<span class=\"box\">| " + (lastReplyDate==null?"-":lastReplyDate) + "</span> "));

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

    private int getThreadId(String link){
        int id = -1;

        Matcher matcher = threadLinkPattern.matcher(link);
        if(matcher.find()){
            id = Util.convertInt(matcher.group(1));
        }

        return id;
    }

    private String getPageSuffix(int threadId){
        int pageNo = dbHelper.getRead(threadId);
        String append;

        if(pageNo > -1){
            append = "&page=" + pageNo;
        }
        else{
            append = "";
        }

        return append;
    }
}

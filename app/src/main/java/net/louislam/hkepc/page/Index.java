package net.louislam.hkepc.page;

import net.louislam.hkepc.Helper;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Index extends Page {

	public String getId() {
		return "index";
	}

	public String getContent(Document doc) {
		StringBuilder sb = new StringBuilder();
		String href;
		String name;
		Element a;
		Element img;

		sb.append("<ul class=\"index\">");
		
		Elements groups = doc.select(".group");

		for (Element g : groups) {
			sb.append(Helper.listViewDivider(g.select("a").text()));
			
			Element current = g.nextElementSibling();

			while (current !=null && current.hasClass("forum")) {
				a = current.select(".forumInfo p a").first();
				href = a.attr("href");
				name = "<span>" + a.text() + "</span>";

				img = current.select(".icon img").first();
				img.attr("src", img.attr("src"));
				img.addClass("icon");
				
				sb.append("<li>" + "<a href=\"" + href + "\">" + img + name + "</a></li>");
				current = current.nextElementSibling();
			}
		}
		sb.append("</ul>");
		
		return sb.toString();
	}

}

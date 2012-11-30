package net.louislam.hkepc.page;

import java.util.HashMap;
import java.util.Map;

import net.louislam.hkepc.Helper;
import net.louislam.hkepc.LoginActivity;
import net.louislam.hkepc.PostActivity;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.content.Intent;
import android.util.Log;

public class Post extends Page implements NonHistoryPage {

	public String getId() {
		return "post";
	}

	public String getContent(Document doc) {
		HashMap<String,String> data = new HashMap<String, String>();
		
		Elements inputs = doc.select("#postform input");
		
		for (Element input : inputs) {
			data.put(input.attr("name"), input.attr("value"));
		}
		
		String textarea = doc.select("#e_textarea").text();
		
		Intent intent = new Intent(a, PostActivity.class);
		intent.putExtra("data", data);
		intent.putExtra("textarea", textarea);
		a.startActivity(intent);
		
		return null;
	}

}

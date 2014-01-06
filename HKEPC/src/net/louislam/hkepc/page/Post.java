package net.louislam.hkepc.page;

import java.util.HashMap;

import net.louislam.hkepc.PostActivity;
import net.louislam.hkepc.R;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.content.Intent;
import android.util.Log;

public class Post extends Page {

	public String getId() {
		return "post";
	}

	public String getContent(Document doc) {
		HashMap<String,String> data = new HashMap<String, String>();
		
		Elements inputs = doc.select("#postform input");
		
		// Form Action link
		String postLink = doc.select("#postform").attr("action");
		Log.d("POSTLINK", postLink + " 123");
		data.put("postLink", postLink);
		
		for (Element input : inputs) {
			
			// if checkbox not checked, don't add it.
			if (input.attr("type").equals("checkbox") && ! input.attr("checked").equals("checked")) {
				continue;
			}
			
			if ( ! input.attr("name").equals("")) {
				//Log.d("KEY", input.attr("name") +": "+ input.attr("value"));
				data.put(input.attr("name"), input.attr("value"));
			}
		}
		
		String textarea = doc.select("#e_textarea").text();
		
		// For Reply Title
		/*
		Elements replyTitles = doc.select("#subjecthide");
		
		if (replyTitles.size() > 0) {
			Element replyTitle = replyTitles.first();
			replyTitle.select("a").removea;
			data.put("subject", replyTitle.text());
		}*/
		
		Intent intent = new Intent(mainActivity, PostActivity.class);
		intent.putExtra("data", data);
		intent.putExtra("textarea", textarea);

        mainActivity.startActivityForResult(intent, R.layout.post);
		
		return null;
	}

}

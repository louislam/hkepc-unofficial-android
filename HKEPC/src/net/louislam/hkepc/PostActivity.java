package net.louislam.hkepc;

import java.util.Map;

import org.jsoup.Connection;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.Connection.Method;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

public class PostActivity extends Activity implements OnClickListener {

	public static final String MSG = "\n\n[size=2][color=#c0c0c0](由 HKEPC Android 手機版 發出)[/color][/size]";
	public static final String[][] ICONS = {{"157", ":band:","icon_band.gif","20","6","50"},{"158", ":happybday:","icon_happybday.gif","20","7","50"},{"159", ":goodjob:","icon_goodjob.gif","20","14","50"},{"160", ":titter:","titter.gif","20","20","20"},{"161", ":sleep:","icon_sleep.gif","20","13","38"},{"163", ":baby:","icon_baby.gif","19","20","20"},{"164", ":crutch:","icon_crutch.gif","20","7","50"},{"165", ":nono:","icon_nono.gif","19","20","23"},{"166", ":discuss:","icon_discuss.gif","20","11","50"},{"167", ":chair:","icon_chair.gif","20","18","45"},{"12", ":cry:","icon_cry.gif","20","9","40"},{"80", ":fight:","icon_fight.gif","20","14","40"},{"81", ":angry:","icon_angry2.gif","20","18","28"},{"82", ":kicking:","icon_kicking.gif","20","17","28"},{"84", ":crybye:","icon_crybye.gif","20","14","26"},{"86", ":cheers:","icon_cheers2.gif","20","14","50"},{"87", ":noway:","noway.gif","20","20","20"},{"88", ":redface:","icon22.gif","19","19","19"},{"89", ":xd:","icon77.gif","19","19","19"},{"90", ":shifty:","VsX4_shifty_P31Twc0M1TeT.gif","20","20","20"},{"91", ":loveliness:","loveliness.gif","20","20","20"},{"94", ":faint:","smile_27.gif","20","20","20"},{"93", ":ar:","smile_38.gif","20","15","30"},{"95", ":silent:","smile_44.gif","20","20","24"},{"137", ":dizzy:","dizzy.gif","20","20","20"},{"138", ":shutup:","shutup.gif","20","20","20"},{"79", ":photo:","icon_photo.gif","20","16","27"},{"78", ":gun:","icon_gun2.gif","20","8","50"},{"77", ":giveup:","icon_giveup.gif","20","15","28"},{"11", ":mad:","icon_mad.gif","18","18","18"},{"10", ":help:","icon_help.gif","20","15","35"},{"9", ":?:","icon_confused.gif","15","20","18"},{"8", ":eek:","icon_eek.gif","18","18","18"},{"7", ":p:","icon_tongue.gif","20","20","20"},{"6", ":dev:","icon_dev.gif","20","19","25"},{"5", ":good:","icon_good.gif","20","14","29"},{"3", ":d:","icon_biggrin.gif","18","18","18"},{"2", ":adore:","icon_adore.gif","20","16","33"},{"1", ":haha:","icon_haha.gif","18","18","18"},{"18", ":wahaha:","icon_clap.gif","19","20","28"},{"17", ":drool:","icon_drool.gif","15","20","18"},{"16", ":kiss:","icon_kiss.gif","18","18","18"},{"14", ":smoke:","icon_smoke.gif","20","9","46"},{"74", ":agree:","icon_agree.gif","20","19","33"},{"75", ":hitwall:","icon_hitwall.gif","20","16","30"},{"76", ":naug:","icon_naughty.gif","20","20","20"}};
	
	private EditText titleEditText;
	private EditText contentEditText;
	private Button postButton;
	private CheckBox isMobileCheckBox;
	
	private Map<String, String> data;
	
	@SuppressWarnings({ "unchecked"})
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.post);
		
		titleEditText = (EditText) findViewById(R.id.postTitle);
		contentEditText = (EditText) findViewById(R.id.postContent);
		postButton = (Button) findViewById(R.id.postButton);
		postButton.setOnClickListener(this);
		
		isMobileCheckBox = (CheckBox) findViewById(R.id.checkBoxIsMobile);
		
		if (AppSettings.get(this, "isMobileCheckBox").equals("false")) {
			isMobileCheckBox.setChecked(false);
		}
		
		Intent i = this.getIntent();
		contentEditText.setText( i.getExtras().getString("textarea").replace(MSG, ""));
		data = (Map<String, String>) i.getSerializableExtra("data");
		titleEditText.setText(data.get("subject"));
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onClick(View v) {
		postButton.setEnabled(false);
		postButton.setText("傳送中...");
		
		data.put("subject", titleEditText.getText().toString());
		String content = contentEditText.getText().toString();
		
		if (isMobileCheckBox.isChecked()) {
			content += MSG;
		}
		
		AppSettings.set(this, "isMobileCheckBox", isMobileCheckBox.isChecked() + "");
		
		data.put("message", content);
		(new Task()).execute(data);
	}
	
	public void done(Response res) {

		//Helper.log(res.parse().html());

		setResult(RESULT_OK);
		this.finish();
	}

	/**
	 * 
	 * @author Louis Lam
	 */
	class Task extends AsyncTask<Map<String, String>, Void, Connection.Response> {
		@Override
		protected Connection.Response doInBackground(Map<String, String>... data) {

			Connection.Response res = null;
			try {
				res = Jsoup.connect(HKEPC.URL + data[0].get("postLink"))
						    .data(data[0]).cookies(HKEPC.getCookies(PostActivity.this))
						    .method(Method.POST)
						    .execute();
			} catch (Exception e) {
				e.printStackTrace();
				Log.d("Error", e.toString());
			}
			
			return res;
		}

		// This is called each time you call publishProgress()
		protected void onProgressUpdate(Void... progress) {

		}

		// This is called when doInBackground() is finished
		@Override
		protected void onPostExecute(Connection.Response b) {
			done(b);
		}
	}		
	
}

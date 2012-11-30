package net.louislam.hkepc;

import java.io.IOException;
import java.util.Map;

import net.louislam.hkepc.LoginActivity.LoginTask;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.Connection.Method;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class PostActivity extends Activity implements OnClickListener {

	private EditText titleEditText;
	private EditText contentEditText;
	private Button postButton;
	
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
		
		Intent i = this.getIntent();
		contentEditText.setText( i.getExtras().getString("textarea"));
		data = (Map<String, String>) i.getSerializableExtra("data");
		titleEditText.setText(data.get("subject"));
	}

	@Override
	public void onClick(View v) {
		(new Task()).execute(username.getText().toString(), password.getText().toString());
	}

	/**
	 * 
	 * @author Louis Lam
	 */
	class Task extends AsyncTask<String, Void, Connection.Response> {
		@Override
		protected Connection.Response doInBackground(String... strs) {
			String page = "logging.php?action=login&loginsubmit=yes&floatlogin=yes";
			
			Connection.Response res = null;
			try {
				res = Jsoup.connect(HKEPC.URL + page)
						    .data("username", strs[0], "password", strs[1])
						    .method(Method.POST)
						    .execute();
			} catch (IOException e) {
				e.printStackTrace();
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

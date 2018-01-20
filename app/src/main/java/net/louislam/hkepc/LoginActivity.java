package net.louislam.hkepc;

import java.io.IOException;

import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

/**
 * 
 * @author Louis Lam
 */
public class LoginActivity extends Activity implements OnClickListener {
	
	private EditText username, password;
	private Button button;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.login);
		
		username = (EditText) findViewById(R.id.username);
		password = (EditText) findViewById(R.id.password);
		button = (Button) findViewById(R.id.loginButton);
		button.setOnClickListener(this);
	}

	public void onClick(View view) {
		button.setEnabled(false);
		button.setText("登入中... 請稍後");
		(new LoginTask()).execute(username.getText().toString(), password.getText().toString());
	}
	
	public void done(Connection.Response res) {
		
		boolean isOk = false;
		String msg = "Error!";
		
		if (res != null) {
			Document doc = null;
			try {
				doc = res.parse();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Element alert = doc.select(".alert_info").first();
			
			if (alert != null && alert.text().contains("歡迎您回來")) {
				HKEPC.setCookies(res.cookies(), this);
				isOk = true;
			}
			
			msg = alert.text();
		}
		
		if (isOk) {
			setResult(RESULT_OK);
			this.finish();
		} else {
			AlertDialog.Builder dialog = new AlertDialog.Builder(this);
			dialog.setMessage(msg);
			dialog.show();
		}
	}
	
	/**
	 * 
	 * @author Louis Lam
	 */
	class LoginTask extends AsyncTask<String, Void, Connection.Response> {
		@Override
		protected Connection.Response doInBackground(String... strs) {
			String page = "logging.php?action=login&loginsubmit=yes&floatlogin=yes";
			
			Connection.Response res = null;
			try {
				res = Jsoup.connect(HKEPC.URL + page)
						    .data("username", strs[0], "password", strs[1])
						    .method(Method.POST)
							.timeout(0)
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

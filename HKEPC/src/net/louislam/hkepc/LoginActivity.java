package net.louislam.hkepc;

import java.io.IOException;

import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Jsoup;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
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
		(new LoginTask()).execute(username.getText().toString(), password.getText().toString());
	}
	
	/**
	 * 
	 * @author Louis Lam
	 */
	class LoginTask extends AsyncTask<String, Void, Void> {
		@Override
		protected Void doInBackground(String... strs) {
			String page = "logging.php?action=login&loginsubmit=yes&floatlogin=yes";
			
			Connection.Response res = null;
			try {
				res = Jsoup.connect(HKEPC.URL + page)
						    .data("username", strs[0], "password", strs[1])
						    .method(Method.POST)
						    .execute();
				HKEPC.setCookies(res.cookies(), LoginActivity.this);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			return null;
		}

		// This is called each time you call publishProgress()
		protected void onProgressUpdate(Void... progress) {

		}

		// This is called when doInBackground() is finished
		@Override
		protected void onPostExecute(Void v) {
		}
	}	

}

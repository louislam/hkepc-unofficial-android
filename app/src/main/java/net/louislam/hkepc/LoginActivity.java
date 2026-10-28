package net.louislam.hkepc;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import android.app.AlertDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

/**
 * 
 * @author Louis Lam
 */
public class LoginActivity extends AppCompatActivity implements OnClickListener {
	
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
				e.printStackTrace();
			}

			if (doc != null) {
				Element alert = doc.select(".alert_info").first();
				if (alert == null) {
					alert = doc.select(".alert_error").first();
				}

				if (alert != null) {
					if (alert.text().contains("歡迎您回來")) {
						HKEPC.setCookies(res.cookies(), this);
						isOk = true;
					}

					msg = alert.text();
				} else {
					msg = "登入失敗，請重試。";
				}
			} else {
				msg = "登入失敗，無法讀取伺服器回應。";
			}
		}
		
		if (isOk) {
			setResult(RESULT_OK);
			this.finish();
		} else {
			button.setEnabled(true);
			button.setText("登入");
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
				// Discuz requires the formhash/loginsid of the login form and its session cookies
				Connection.Response loginPage = Jsoup.connect(HKEPC.URL + "logging.php?action=login")
						.userAgent(HKEPC.USER_AGENT)
						.method(Method.GET)
						.timeout(20000)
						.execute();
				
				Document doc = loginPage.parse();
				Element form = doc.select("#loginform").first();
				String formhash = doc.select("input[name=formhash]").first().val();
				String loginsid = form.select("input[name=loginsid]").first().val();
				
				res = Jsoup.connect(HKEPC.URL + page)
						    .userAgent(HKEPC.USER_AGENT)
						    .cookies(loginPage.cookies())
						    .data("formhash", formhash, "loginsid", loginsid)
						    .data("referer", "", "loginfield", "username")
						    .data("username", strs[0], "password", strs[1])
						    .data("questionid", "0", "answer", "")
						    .method(Method.POST)
							.timeout(0)
						    .execute();
				
				// Keep the pre-login cookies (e.g. auth) as well
				Map<String, String> cookies = new HashMap<String, String>(loginPage.cookies());
				cookies.putAll(res.cookies());
				res.cookies().clear();
				res.cookies().putAll(cookies);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (RuntimeException e) {
				// The login form is missing (site changed or blocked)
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

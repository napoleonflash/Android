package napoleon.translate.pro;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity {

	private EditText edtInput;
	private TextView tvOutput;
	private Button btnTranslate;
	private static final String TAG = "TranslateActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		edtInput = (EditText) findViewById(R.id.edtInput);
		tvOutput = (TextView) findViewById(R.id.tvOutput);
		btnTranslate = (Button) findViewById(R.id.btnTranslate);

		btnTranslate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				AsyncTask<Void, Void, Void> translateTask = new AsyncTask<Void, Void, Void>() {

					@Override
					protected Void doInBackground(Void... arg0) {

						try {

							Log.d(TAG, "MainActivity start translate ...");

							HttpParams httpParameters = new BasicHttpParams();
							DefaultHttpClient client = new DefaultHttpClient(
									httpParameters);
							String url = "https://www.googleapis.com/language/translate/v2?key=AIzaSyC-CSkRiS3TOsBvtIKlsP81yUHKK-dQMK0&source=en&target=vi&q="
									+ edtInput.getText().toString();
							HttpUriRequest request = new HttpGet(url);
							HttpResponse httpResponse = client.execute(request);

							Log.d(TAG,
									"MainActivity httpResponse status code : "
											+ httpResponse.getStatusLine()
													.getStatusCode());
							BufferedReader r = new BufferedReader(
									new InputStreamReader(httpResponse
											.getEntity().getContent()));
							StringBuilder total = new StringBuilder();
							String line;
							while ((line = r.readLine()) != null) {
								total.append(line);
							}

							Log.d(TAG, "MainActivity response : " + total);

							final String output = translate(total.toString());

							runOnUiThread(new Runnable() {
								public void run() {
									tvOutput.setText("Result : " + output);
								}
							});
						} catch (Exception e) {
							e.printStackTrace();
						}
						return null;
					}
				};
				translateTask.execute();
			}
		});

	}

	public String translate(String jsonInput) {
		String result = "";

		JSONObject jsonObj;
		try {
			jsonObj = new JSONObject(jsonInput);
			JSONObject tralJsonObj = jsonObj.getJSONObject("data");
			// .getJSONObject("translations");
			JSONArray jsonArr = tralJsonObj.getJSONArray("translations");
			result = jsonArr.getJSONObject(0).getString("translatedText");
			Log.d(TAG, "result : " + result);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return result;

	}

}

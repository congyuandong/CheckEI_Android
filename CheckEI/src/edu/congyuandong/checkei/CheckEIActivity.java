package edu.congyuandong.checkei;

import org.json.JSONException;
import org.json.JSONObject;

import edu.congyuandong.checkei.httprequest.DoPost;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class CheckEIActivity extends Activity implements OnClickListener {

	private Button btn_Search;
	private EditText searchWord1;
	private TextView acNumber, title, authors, docType, conName;
	private String str_acNumber, str_title, str_authors, str_docType,
			str_conName;
	Handler handler = new Handler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.check_ei);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.title_bar);

		viewBind();

	}

	private void viewBind() {
		btn_Search = (Button) findViewById(R.id.btn_search);
		searchWord1 = (EditText) findViewById(R.id.SearchWord1);
		acNumber = (TextView) findViewById(R.id.acNumber);
		title = (TextView) findViewById(R.id.title);
		authors = (TextView) findViewById(R.id.authors);
		docType = (TextView) findViewById(R.id.docType);
		conName = (TextView) findViewById(R.id.conName);
		btn_Search.setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.check_ei, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_search:
			Thread getData_thread = new Thread(getData);
			getData_thread.start();
			break;
		}
	};

	private Runnable getData = new Runnable() {
		@Override
		public void run() {
			DoPost post = new DoPost();
			String search = searchWord1.getText().toString();
			if (search.equals("")) {
				// System.out.println("输入为空");
				showMsg("不要调戏我了，什么都没有嘛～");
			} else {
				String result = post.RequestPost(search);
				if (!result.equals("SORRY")) {
					try {
						JSONObject jsonObject = new JSONObject(result);
						setContext(jsonObject);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					showMsg("似乎还没有您要找的信息");
				}
			}

		}
	};

	private void setContext(JSONObject jobj) {
		try {
			str_acNumber = jobj.getString("Accession number:");
			str_title = jobj.getString("Title:");
			str_authors = jobj.getString("Authors:");
			str_docType = jobj.getString("Document type:");
			if(str_docType.indexOf("Conference")!=-1)
				str_conName = jobj.getString("Conerence name:");
			handler.post(setContextThread);

		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	private Runnable setContextThread = new Runnable() {

		@Override
		public void run() {
			acNumber.setText(str_acNumber);
			title.setText(str_title);
			authors.setText(str_authors);
			docType.setText(str_docType);
			conName.setText(str_conName);
			acNumber.refreshDrawableState();
			title.refreshDrawableState();
			authors.refreshDrawableState();
			docType.refreshDrawableState();
			conName.refreshDrawableState();

		}
	};

	private void showMsg(final String msg) {
		handler.post(new Runnable() {
			@Override
			public void run() {
				myToast(msg);
			}
		});
	}

	private void myToast(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}

}

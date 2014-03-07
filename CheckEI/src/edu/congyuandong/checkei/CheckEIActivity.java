package edu.congyuandong.checkei;

import org.json.JSONException;
import org.json.JSONObject;

import edu.congyuandong.checkei.ProgressDialog.CustomProgressDialog;
import edu.congyuandong.checkei.httprequest.DoPost;
import edu.congyuandong.checkei.util.SystemSettings;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Context;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class CheckEIActivity extends Activity implements OnClickListener {

	private Button btn_Search;
	private AutoCompleteTextView searchWord1;
	private TextView acNumber, title, authors, docType, conName;
	private String str_acNumber, str_title, str_authors, str_docType,
			str_conName;
	Handler handler = new Handler();
	private LinearLayout dataLayout;
	private ScrollView scrollView;
	private CustomProgressDialog progressDialog = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.check_ei);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.title_bar);
		viewBind();
		dataLayout.setVisibility(View.GONE);
		scrollView.setVisibility(View.GONE);
		fillAutoTextView();
	}

	private void fillAutoTextView() {
		String[] titles = new String[] {
				SystemSettings.getSettingMessage(this, "SHARE_sw1", ""),
				SystemSettings.getSettingMessage(this, "SHARE_sw2", ""),
				SystemSettings.getSettingMessage(this, "SHARE_sw3", "") };
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_dropdown_item_1line, titles);
		searchWord1.setAdapter(adapter);
	}

	private void saveSearchWord(String searchWord) {
		if ((!searchWord.equals(SystemSettings.getSettingMessage(this,
				"SHARE_sw3", "")))
				&& (!searchWord.equals(SystemSettings.getSettingMessage(this,
						"SHARE_sw2", "")))) {
			SystemSettings.setSettingMessage(this, "SHARE_sw1",
					SystemSettings.getSettingMessage(this, "SHARE_sw2", ""));
			SystemSettings.setSettingMessage(this, "SHARE_sw2",
					SystemSettings.getSettingMessage(this, "SHARE_sw3", ""));
			SystemSettings.setSettingMessage(this, "SHARE_sw3", searchWord);

		}

	}

	private void viewBind() {
		btn_Search = (Button) findViewById(R.id.btn_search);
		searchWord1 = (AutoCompleteTextView) findViewById(R.id.SearchWord1);
		acNumber = (TextView) findViewById(R.id.acNumber);
		title = (TextView) findViewById(R.id.title);
		authors = (TextView) findViewById(R.id.authors);
		docType = (TextView) findViewById(R.id.docType);
		conName = (TextView) findViewById(R.id.conName);
		btn_Search.setOnClickListener(this);
		dataLayout = (LinearLayout) findViewById(R.id.dataLayout);
		scrollView = (ScrollView) findViewById(R.id.scrollView);
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
			// 隐藏键盘
			hideInput();
			// 加载进度条
			startProcessDialog();
			// 保存历史查询数据
			String search = searchWord1.getText().toString();
			saveSearchWord(search);
			fillAutoTextView();
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
			// saveSearchWord(search);
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
					stopProcessDialog();
					showMsg("似乎还没有您要找的信息");
				}
			}

		}
	};

	private void setContext(JSONObject jobj) {
		try {
			if (jobj.has("Accession number:"))
				str_acNumber = jobj.getString("Accession number:");
			if (jobj.has("Title:"))
				str_title = jobj.getString("Title:");
			if (jobj.has("Authors:"))
				str_authors = jobj.getString("Authors:");
			if (jobj.has("Document type:"))
				str_docType = jobj.getString("Document type:");
			if (jobj.has("Conference name:"))
				str_conName = jobj.getString("Conference name:");
			stopProcessDialog();
			handler.post(setContextThread);

		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	private Runnable setContextThread = new Runnable() {

		@Override
		public void run() {
			dataLayout.setVisibility(View.VISIBLE);
			scrollView.setVisibility(View.VISIBLE);
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

	/**
	 * 隐藏输入键盘
	 */
	private void hideInput() {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		if (imm.isActive())
			imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT,
					InputMethodManager.HIDE_NOT_ALWAYS);
	}

	/**
	 * 打开自定义加载
	 */
	private void startProcessDialog() {
		if (progressDialog == null) {
			progressDialog = CustomProgressDialog.createDialog(this);
			progressDialog.show();
		}
	}

	/**
	 * 关闭自定义加载
	 */
	private void stopProcessDialog() {
		if (progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}
	}
}

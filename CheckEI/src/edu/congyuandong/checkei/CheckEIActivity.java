package edu.congyuandong.checkei;

import org.json.JSONException;
import org.json.JSONObject;

import com.umeng.analytics.MobclickAgent;
import com.umeng.fb.FeedbackAgent;

import edu.congyuandong.checkei.ProgressDialog.CustomProgressDialog;
import edu.congyuandong.checkei.httprequest.DoPost;
import edu.congyuandong.checkei.offerWall.OfferWall;
import edu.congyuandong.checkei.util.SystemSettings;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Context;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
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
	private Context mContext;
	private OfferWall wall;
	private int SCORE = 50;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.check_ei);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.title_bar);
		viewBind();

		mContext = this;
		MobclickAgent.updateOnlineConfig(mContext);
		wall = new OfferWall(mContext);

		// 将积分数据存储在本地
		// wall.checkWall(mContext);

		// 设置查询次数
		if (SystemSettings.getSettingMessage(mContext, "EISEARCH_TIMES", -1) == -1)
			SystemSettings.setSettingMessage(mContext, "EISEARCH_TIMES", 0);
		dataLayout.setVisibility(View.GONE);
		scrollView.setVisibility(View.GONE);

		// 填充自动完成输入框
		fillAutoTextView();

		// System.out.println(gs.getIMEI(mContext));
		// SystemSettings.setSettingMessage(mContext, "SEARCHEI_USERID",
		// gs.getIMEI(mContext));
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
		// 这里获取在线数据，从而决定是否启用积分模式
		String isOnline = MobclickAgent.getConfigParams(mContext, "isOnline");
		if (isOnline.equals("off")) {
			getMenuInflater().inflate(R.menu.check_ei_off, menu);
		} else {
			getMenuInflater().inflate(R.menu.check_ei_on, menu);
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_search:
			// OfferWall wall1 = new OfferWall(mContext);
			wall.checkWall(mContext);
			break;
		case R.id.menu_getscore:
			// OfferWall wall = new OfferWall(mContext);
			wall.wallGet();
			break;
		case R.id.menu_feedback:
			System.out.println("feedback");
			FeedbackAgent agent = new FeedbackAgent(mContext);
			agent.startFeedbackActivity();
			break;
		}
		return true;
	};

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_search:
			// 隐藏键盘
			hideInput();

			// 保存历史查询数据
			String search = searchWord1.getText().toString();
			saveSearchWord(search);
			fillAutoTextView();

			// 这里获取在线数据，从而决定是否启用积分模式
			String isOnline = MobclickAgent.getConfigParams(mContext,
					"isOnline");
			int Search_time = SystemSettings.getSettingMessage(mContext,
					"EISEARCH_TIMES", 0);
			if (isOnline.equals("on")) {
				int lastScore = SystemSettings.getSettingMessage(mContext,
						"EISEARCH_SCORE", 0);
				System.out.println("lastScore:" + lastScore);
				if (lastScore < SCORE && Search_time >= 2) {
					showMsg("查询积分不足啦，请点击菜单键查询积分");
				} else {
					Search_time++;
					SystemSettings.setSettingMessage(mContext,
							"EISEARCH_TIMES", Search_time);
					// 加载进度条
					startProcessDialog();
					Thread getData_thread = new Thread(getData);
					getData_thread.start();
				}
				break;

			} else {

				// 加载进度条
				startProcessDialog();
				Thread getData_thread = new Thread(getData);
				getData_thread.start();
				break;
			}

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

					// 查询成功，扣除积分
					String isOnline = MobclickAgent.getConfigParams(mContext,
							"isOnline");
					int Search_time = SystemSettings.getSettingMessage(
							mContext, "EISEARCH_TIMES", 0);
					if(isOnline.equals(""))
					showMsg("查询成功,消耗积分" + SCORE);
					wall.wallUse(SCORE, mContext);

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
		Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
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

	private long exitTime = 0;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			if ((System.currentTimeMillis() - exitTime) > 2000) {
				Toast.makeText(getApplicationContext(), "再按一次退出程序",
						Toast.LENGTH_SHORT).show();
				exitTime = System.currentTimeMillis();
			} else {
				finish();
				System.exit(0);
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	// 增加Umeng统计组建
	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
}

package edu.congyuandong.checkei;

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
import android.widget.Toast;

public class CheckEIActivity extends Activity implements OnClickListener {

	private Button btn_Search;
	private EditText searchWord1;
	Handler handler = new Handler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);  
		setContentView(R.layout.check_ei);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title_bar);  

		viewBind();

	}

	private void viewBind() {
		btn_Search = (Button) findViewById(R.id.btn_search);
		searchWord1 = (EditText) findViewById(R.id.SearchWord1);
		btn_Search.setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.check_ei, menu);
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
				//System.out.println(post.RequestPost(search));
			}

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
	
	private void myToast(String msg){
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}

}

package edu.congyuandong.checkei;

import edu.congyuandong.checkei.httprequest.DoPost;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class CheckEIActivity extends Activity implements OnClickListener {
	
	private Button btn_Search;
	private EditText searchWord1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.check_ei);
        
        viewBind();
        
    }


    private void viewBind() {
    	btn_Search = (Button)findViewById(R.id.btn_search);
    	searchWord1 = (EditText)findViewById(R.id.SearchWord1);
    	btn_Search.setOnClickListener(this);
	}


	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.check_ei, menu);
        return true;
    }


	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.btn_search:
			Thread getData_thread = new Thread(getData);
			getData_thread.start();
			break;
		}
	};
	
	private Runnable getData = new Runnable(){
		@Override
		public void run() {
			DoPost post = new DoPost();
			String search = searchWord1.getText().toString();
			post.RequestPost(search);
			
		}		
	};
	
	
}


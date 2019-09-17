package com.tools.payhelper;

import com.tools.payhelper.utils.AbSharedUtil;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

/**
 * 

* @ClassName: SettingActivity

* @Description: TODO(这里用一句话描述这个类的作用)

* @author xinyu126

* @date 2018年6月23日 下午1:26:51

*
 */
public class SettingActivity extends Activity implements OnClickListener{
	
	private EditText et_mchid,et_notifyurl,et_signkey,et_device;
	private Button bt_save,bt_back;
	private RelativeLayout rl_back;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); 
		setContentView(R.layout.activity_setting);
		et_notifyurl=(EditText) findViewById(R.id.notifyurl);
		et_mchid=(EditText) findViewById(R.id.mchid);

		if(!TextUtils.isEmpty(AbSharedUtil.getString(getApplicationContext(), "notifyurl"))){
			et_notifyurl.setText(AbSharedUtil.getString(getApplicationContext(), "notifyurl"));
		}

		if(!TextUtils.isEmpty(AbSharedUtil.getString(getApplicationContext(), "mchid"))){
			et_mchid.setText(AbSharedUtil.getString(getApplicationContext(), "mchid"));
		}


		bt_save=(Button) findViewById(R.id.save);
		bt_back=(Button) findViewById(R.id.back);
		rl_back=(RelativeLayout) findViewById(R.id.rl_back);
		bt_back.setOnClickListener(this);
		bt_save.setOnClickListener(this);
		rl_back.setOnClickListener(this);
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	@Override
	protected void onResume() {
		super.onResume();
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.save:
			String notifyurl=et_notifyurl.getText().toString();
			if(TextUtils.isEmpty(notifyurl)){
				Toast.makeText(getApplicationContext(), "异步通知地址不能为空！", Toast.LENGTH_LONG).show();
				return;
			}else{
				AbSharedUtil.putString(getApplicationContext(), "notifyurl", notifyurl);
				MainActivity.notifyurl = notifyurl;
			}


			String mchid=et_mchid.getText().toString();
			if(TextUtils.isEmpty(mchid)){
				Toast.makeText(getApplicationContext(), "mchid不能为空！", Toast.LENGTH_LONG).show();
				return;
			}else{
				AbSharedUtil.putString(getApplicationContext(), "mchid", mchid);
				MainActivity.mchid = mchid;
			}

			Toast.makeText(getApplicationContext(), "保存成功", Toast.LENGTH_LONG).show();
			break;
		case R.id.back:
			finish();
			break;
		case R.id.rl_back:
			finish();
			break;
		default:
			break;
		}
	}
}

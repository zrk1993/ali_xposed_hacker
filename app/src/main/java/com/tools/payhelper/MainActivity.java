package com.tools.payhelper;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.tools.payhelper.utils.AbSharedUtil;
import com.tools.payhelper.utils.DBManager;
import com.tools.payhelper.utils.MD5;
import com.tools.payhelper.utils.OrderBean;
import com.tools.payhelper.utils.PayHelperUtils;
import com.tools.payhelper.utils.QrCodeBean;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ScrollView;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

/**
 * 

* @ClassName: MainActivity

* @Description: TODO(这里用一句话描述这个类的作用)

* @author xinyu126

* @date 2018年6月23日 下午1:26:32

*
 */
public class MainActivity extends Activity {

	public static String notifyurl;
	public static String signkey;
	public static String mchid;
	public static String device;

	public static TextView console;
	public static ScrollView scrollView;
	public static Activity context;
	public AlarmReceiver alarmReceiver;
	public BillReceived billReceived;
	public static String NOTIFY_ACTION = "com.tools.payhelper.notify";
	public static String MSGRECEIVED_ACTION = "com.tools.payhelper.ali.msgreceived";
	public static String BILLRECEIVED_ACTION = "com.tools.payhelper.ali.billreceived";

	public static String LOG_TAG = "alipay";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.activity_main);
		console = (TextView) findViewById(R.id.console);
		scrollView = (ScrollView) findViewById(R.id.scrollview);
		context = this;
		this.findViewById(R.id.start_alipay).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View arg0) {
						Intent broadCastIntent = new Intent();
						broadCastIntent.setAction(PayHelperUtils.ALIPAYSTART_ACTION);
						String time = System.currentTimeMillis() / 10000L + "";
						broadCastIntent.putExtra("mark", "test" + time);
						broadCastIntent.putExtra("money", "0.01");
						sendBroadcast(broadCastIntent);
					}
				});

		this.findViewById(R.id.setting).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View arg0) {
						Intent intent = new Intent(MainActivity.this, SettingActivity.class);
						startActivity(intent);
					}
				});
		//注册广播
		billReceived=new BillReceived();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(MSGRECEIVED_ACTION);
		intentFilter.addAction(BILLRECEIVED_ACTION);
		registerReceiver(billReceived, intentFilter);

		alarmReceiver = new AlarmReceiver();
		IntentFilter alarmIntentFilter = new IntentFilter();
		alarmIntentFilter.addAction(NOTIFY_ACTION);
		registerReceiver(alarmReceiver, alarmIntentFilter);
		startService(new Intent(this, DaemonService.class));

//		notifyurl = AbSharedUtil.getString(getApplicationContext(), "notifyurl");
//		signkey = AbSharedUtil.getString(getApplicationContext(), "signkey");
//		mchid = AbSharedUtil.getString(getApplicationContext(), "mchid");
//		device = AbSharedUtil.getString(getApplicationContext(), "device");
//
//		sendmsg("notifyurl: " + notifyurl);
//		sendmsg("signkey: " + signkey);
//		sendmsg("mchid: " + mchid);
//		sendmsg("device: " + device);
	}

	@SuppressLint("HandlerLeak")
	public static Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			String txt = msg.getData().getString("log");
			if (console != null) {
				if (console.getText() != null) {
					if (console.getText().toString().length() > 5000) {
						console.setText("日志定时清理完成..." + "\n\n" + txt);
					} else {
						console.setText(console.getText().toString() + "\n\n" + txt);
					}
				} else {
					console.setText(txt);
				}
				scrollView.post(new Runnable() {
					public void run() {
						scrollView.fullScroll(View.FOCUS_DOWN);
					}
				});
			}

			super.handleMessage(msg);
		}

	};

	@Override
	protected void onDestroy() {
		unregisterReceiver(alarmReceiver);
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}


	public static void sendmsg(String txt) {
		Message msg = new Message();
		msg.what = 1;
		Bundle data = new Bundle();
		long l = System.currentTimeMillis();
		Date date = new Date(l);
		SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
		String d = dateFormat.format(date);
		data.putString("log", d + ":" + "  结果:" + txt);
		msg.setData(data);
		try {
			handler.sendMessage(msg);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onBackPressed() {
		moveTaskToBack(true);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// 过滤按键动作
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			moveTaskToBack(true);
		}
		return super.onKeyDown(keyCode, event);
	}

	//自定义接受订单通知广播
	class BillReceived extends BroadcastReceiver {
		@Override
		public void onReceive(final Context context, Intent intent) {
			try {
				// msg
				if (intent.getAction().contentEquals(MSGRECEIVED_ACTION)) {
					String msg = intent.getStringExtra("msg");
					sendmsg(msg);
				}
				// notifyurl
				if (intent.getAction().contentEquals(BILLRECEIVED_ACTION)) {
					String amount = intent.getStringExtra("amount");
					String conbiz_opp_uid = intent.getStringExtra("conbiz_opp_uid");
					String bizType = intent.getStringExtra("bizType");
					String bizInNo = intent.getStringExtra("bizInNo");
					notifyurl(amount, conbiz_opp_uid, bizType, bizInNo);
				}
			} catch (Exception e) {
				PayHelperUtils.sendmsg(context, "BillReceived异常" + e.getMessage());
			}
		}
	}

	public static void notifyurl(final String amount, String conbiz_opp_uid,  String bizType,  String bizInNo) {
		try {

			String mchid = MainActivity.mchid;
			String signkey = "12345679";

			PayHelperUtils.sendmsg(context, "发送异步通知：amount：" + amount + "，" + "mchid：" + mchid);

			String signStr = amount + "|" + mchid + "|" + "success" + signkey;
			String sign = MD5.md5(signStr).toUpperCase();
			RequestParams params = new RequestParams();
			params.addBodyParameter("bill_no", bizInNo);
			params.addBodyParameter("userid", conbiz_opp_uid);
			params.addBodyParameter("amount", amount);
			params.addBodyParameter("paystatus", "success");
			params.addBodyParameter("mchid", mchid);
			params.addBodyParameter("sign", sign);

			final String mNotifyurl = MainActivity.notifyurl + "/pay_aliqrcode/notifyurl";

			HttpUtils httpUtils = new HttpUtils(10000);
			httpUtils.configResponseTextCharset("GBK");

			httpUtils.send(HttpRequest.HttpMethod.POST, mNotifyurl, params, new RequestCallBack<String>() {

				@Override
				public void onFailure(HttpException arg0, String arg1) {
					Log.e(LOG_TAG, "发送异步通知异常，服务器异常 " + arg1);
					PayHelperUtils.sendmsg(context,"发送异步通知异常，服务器异常 " + arg1);
				}

				@Override
				public void onSuccess(ResponseInfo<String> arg0) {
					String result = arg0.result;

					if (result.contains("success")) {
						PayHelperUtils.sendmsg(context,"发送异步通知成功, result: " + result);
						Log.i(LOG_TAG, "发送异步通知成功，服务器返回 " + result);
					} else {
						PayHelperUtils.sendmsg(context,"发送异步通知失败，服务器返回 " + result);
						Log.e(LOG_TAG, "发送异步通知失败，服务器返回 " + result);
					}
				}
			});
		} catch (Exception e) {
			PayHelperUtils.sendmsg(context,"发送异步通知失败 " + e.getMessage() + e.toString());
			Log.e(LOG_TAG, "发送异步通知失败 " + e.getMessage() + e.toString());
		}
	}
}
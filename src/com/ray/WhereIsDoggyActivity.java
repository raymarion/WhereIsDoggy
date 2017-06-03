package com.ray;

import java.util.ArrayList;


import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class WhereIsDoggyActivity extends Activity {
    /** Called when the activity is first created. */
	
	
	Button button;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        WhereIsDoggyService.registerIntent(this);
        button = (Button) findViewById(R.id.button1);
        if(PollingUtils.isPendingIntent(this, WhereIsDoggyService.class, WhereIsDoggyService.ACTION)) {
        	button.setText("Stop Service");
		} else {
			button.setText("Start Service");
		}
    }
    
    public void startService(View view){
    	
    	Log.v("MainActivity", "OnClickService");
    	if(!PollingUtils.isPendingIntent(this, WhereIsDoggyService.class, WhereIsDoggyService.ACTION)) { // 判断服务是否已经开启
			
			//Start polling service
	        System.out.println("Start polling service...");
	        PollingUtils.startPollingService(this, 240, WhereIsDoggyService.class, WhereIsDoggyService.ACTION);
	        //startService(new Intent(WhereIsDoggyActivity.this, WhereIsDoggyService.class));
			button.setText("Stop Service");
		} else {
			
			// 下面关闭获取信息服务
			
			//Stop polling service
	        System.out.println("Stop polling service...");
	        PollingUtils.stopPollingService(this, WhereIsDoggyService.class, WhereIsDoggyService.ACTION);
	        stopService(new Intent(WhereIsDoggyActivity.this, WhereIsDoggyService.class));
			
			button.setText("Start Service");

			
		}
    }

	/** 判断 Service是否处于运行状态 */
	public boolean isWorked(Context context) {
		
		
		
		
		ActivityManager myManager = (ActivityManager) context
						.getSystemService(Context.ACTIVITY_SERVICE); // 获取服务
		ArrayList<RunningServiceInfo> runningService = (ArrayList<RunningServiceInfo>) myManager
			.getRunningServices(30); // 获取正在运行的服务
		for (int i = 0; i < runningService.size(); i++) {
			if (runningService.get(i).service.getClassName().toString()
					.equals("com.ray.WhereIsDoggyService")) {
				Log.v("WhereIsDoggyActivity", "ServiceIsWorked");
				System.out.println("worked...");
				return true;
			}
		}
		Log.v("MainActivity", "ServiceIsNotWorking");
		System.out.println("not worked...");
		return false;
	}
}
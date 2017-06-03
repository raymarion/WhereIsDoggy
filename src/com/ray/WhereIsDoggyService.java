package com.ray;


import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;



import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.os.PowerManager;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.TextView;

public class WhereIsDoggyService extends Service {

	
	LocationManager locMgr; // 位置信息
	Criteria criteria;
	String setProvider = LocationManager.NETWORK_PROVIDER;

	public static final String ACTION = "com.ray.service.WhereIsDoggyService";
	NotificationManager notificationManager = null;
	Notification notification;
	TextView tv;
	private static WhereIsDoggyActivity mainActivity;
	URI url = URI.create("http://goubao.sinaapp.com/api/Location/insert/");
	
	private PowerManager pm;  
    private PowerManager.WakeLock wakeLock; 
	
	static void registerIntent(Context context) {
		mainActivity = (WhereIsDoggyActivity) context;
	}
	
	
	public void setNotification(String string) {
		Log.v("getmsgService", "notification");

	
		notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		
		notification = new Notification.Builder(this).setAutoCancel(true).setSmallIcon(R.drawable.ic_launcher).setTicker("Location upload "+string).setWhen(System.currentTimeMillis())
				.setContentText("update location").setContentTitle("location")//.setLights(Color.YELLOW, 100, 100)
				.getNotification();


		notificationManager.notify(R.drawable.ic_launcher, notification);
		if (wakeLock.isHeld()) 
        { 
			wakeLock.release();
        }
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		notificationManager.cancel(R.drawable.ic_launcher);
		//contentIntent.cancel();
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
 
    @Override
    public void onCreate() {
    	super.onCreate();
    	pm = (PowerManager) getSystemService(Context.POWER_SERVICE);  
        //保持cpu一直运行，不管屏幕是否黑屏  
        wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "WhereIdDoggyService");  
    	prepareLocation();
    	
        Log.v("LocalService", "Service onCreate");
      
    }
     
    @Override
    public void onStart(Intent intent, int startId) {
    	//super.onStart(intent, startId);  
    	//创建PowerManager对象  
        
        if (wakeLock!=null) {
			wakeLock.acquire();
		}  
        
        try {
			Thread.sleep(20);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (wakeLock.isHeld()) 
        { 
			wakeLock.release();
        }
		
        //this.stopSelfResult(startId);
    }
    
    public WhereIsDoggyService getService()
    {
    	return this;
    }
    
    final LocationListener locationListener = new LocationListener() {

		@Override
		public void onLocationChanged(Location location) {
			// TODO Auto-generated method stub
			//pm = (PowerManager) getSystemService(Context.POWER_SERVICE);  
	        //保持cpu一直运行，不管屏幕是否黑屏  
	        //wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "WhereIdDoggyService");  
	        if (wakeLock!=null) {
	        	if (!wakeLock.isHeld())
	        	{
				wakeLock.acquire();
	        	}
			} 
			new PollingThread(location).start();
			/*if (wakeLock.isHeld()) 
		        { 
					wakeLock.release();
		        }*/
		
		}

		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub

		}

	};
    
    public void prepareLocation() {
		locMgr = (LocationManager) getSystemService(LOCATION_SERVICE);
	
		try {
			locMgr.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 240000, 0,
					locationListener);
		
		} catch (Exception e) {
			Log.v("localService", "Error: " + e.toString());
		}
	}
    
/*
	public String getLocation() {
		// 
		Location recentLoc;
		if (isGPSOn) {
			recentLoc = locMgr.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			//PostLocation(recentLoc);
			return showLocation(recentLoc, 1);
		} else {
			return showLocation(null, 2);
		}
	}

	// 展示位置信息
	public String showLocation(Location location, int flag) {
		String locationString = "null";
		if (location != null) {
			Log.v("LocalService", "ok");
			
			
			Date d = new Date();
			d.setTime(location.getTime());// UTC时间,转北京时间+8小时
			String GpsTime = DateFormat.format("yyyy-MM-dd kk:mm:ss", d)
					.toString();
			locationString = "latitude:" + location.getLatitude()
					+ ",longtitude:" + location.getLongitude() + ",altitude:"
					+ location.getAltitude() + ",bearing:"
					+ location.getBearing() + ",speed:" + location.getSpeed()
					+ ",gpstime:" + GpsTime;
			Log.v("Location",
					"纬度:" + location.getLatitude() + "经度:"
							+ location.getLongitude() + "海拔:"
							+ location.getAltitude() + "方向:"
							+ location.getBearing() + "速度:"
							+ location.getSpeed() + "GPS时间:" + GpsTime);
			
			
		} else {
			if (flag == 1) {
				// locationString = "null";
				Log.v("LocalService", "没有位置信息");
			} else if (flag == 2) {
				// locationString = "null";
				Log.v("LocalService", "GPS服务已关闭");
			}
		}
		return locationString;
	}
    */
	
	public void PostLocation(Location location){
		
		HttpPost post =new HttpPost(url);
		Date d = new Date();
		d.setTime(location.getTime());// UTC时间,转北京时间+8小时
		String GpsTime = DateFormat.format("yyyy-MM-dd kk:mm:ss", d)
				.toString();

		List <BasicNameValuePair> params=new ArrayList<BasicNameValuePair>();

		         params.add(new BasicNameValuePair("long",""+(location.getLongitude()*1000000)));
		         params.add(new BasicNameValuePair("lat",""+(location.getLatitude()*1000000)));
		         params.add(new BasicNameValuePair("time",GpsTime));
		  
		         try{
		          post.setEntity(new UrlEncodedFormEntity(params,HTTP.UTF_8));
		          HttpResponse response=new DefaultHttpClient().execute(post);
		          if (response.getStatusLine().getStatusCode() == 200) {  
		        	  	setNotification("ok!");     
		        	}else{
		        		setNotification(response.getStatusLine().getStatusCode()+"");
		        	}
		         } catch (Exception e) {
		        	 System.out.println(e.toString());
		        	 
		         }
		
	}
    
    
   
    class PollingThread extends Thread {
    	
    	Location location;
    	

        public PollingThread(Location location) {
			super();
			this.location = location;
		}

		@Override
        public void run() {
            System.out.println("send location");

            PostLocation(location);
           
            if (wakeLock.isHeld()) 
            { 
    			wakeLock.release();
            }
            //getService().stopSelf();
        }
		
    }
     
    
	
	@Override
    public void onDestroy() {
    	if (locationListener != null) {  
    		locMgr.removeUpdates(locationListener);  
        }
    	if (wakeLock.isHeld()) { 
    			wakeLock.release();
    	  }
          
        super.onDestroy();
        
        System.out.println("Service:onDestroy");
    }

	

}

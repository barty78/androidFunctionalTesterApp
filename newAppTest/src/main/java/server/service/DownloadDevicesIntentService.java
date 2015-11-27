package server.service;

import java.util.List;

import com.pietrantuono.application.PeriCoachTestApplication;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import server.RetrofitRestServices;
import server.pojos.Device;

public class DownloadDevicesIntentService extends IntentService{
	public DownloadDevicesIntentService() {
		super("foo");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Log.e("TAG","Downlaoding devices list");
		RetrofitRestServices.getRest(DownloadDevicesIntentService.this).getAllDevices(PeriCoachTestApplication.getDeviceid(), new Callback<List<Device>>() {
			
			@Override
			public void success(List<Device> arg0, Response arg1) {
				if(arg0!=null && arg0.size()>0)
				{
					ServiceDBHelper.addDevices(arg0);
				Log.d("TAG","Downlaoded devices list of "+arg0.size()+" items");
				}
				else Log.d("TAG","Downlaoded empty devices list");
			}
			
			@Override
			public void failure(RetrofitError arg0) {
				Log.d("TAG","Error downlaoding devices list");
			}
		});
	}

}

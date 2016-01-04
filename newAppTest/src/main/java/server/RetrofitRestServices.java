package server;

import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pietrantuono.pericoach.newtestapp.BuildConfig;
import com.pietrantuono.pericoach.newtestapp.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RestAdapter.LogLevel;
import retrofit.converter.GsonConverter;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.POST;
import server.pojos.Device;
import server.pojos.Firmware;
import server.pojos.Job;
import server.pojos.Test;
import server.pojos.records.TestRecord;
import server.pojos.records.response.Response;

public class RetrofitRestServices {
    private static REST rest;

    public interface REST {

        @GET("/jobs/active")
        void getJobListActiveJobs(@Header("DeviceId") String DeviceId, Callback<List<Job>> callback);

        @GET("/firmware")
        void getFirmware(@Header("DeviceId") String DeviceId, @Header("FirmwareId") String FirmwareId, Callback<Firmware> callback);

        @GET("/tests")
        void getSequence(@Header("DeviceId") String DeviceId, @Header("JobNo") String JobNo, Callback<List<Test>> callback);

        @POST("/record")
        void postResults(@Header("DeviceId") String DeviceId, @Header("JobNo") String JobNo, @Body TestRecord record, Callback<Response> callback);

        @GET("/devices")
        void getLastDevices(@Header("DeviceId") String DeviceId, @Header("LastId") String LastId, Callback<List<Device>> callback);

        @GET("/devices")
        void getAllDevices(@Header("DeviceId") String DeviceId, Callback<List<Device>> callback);

    }


    public synchronized static REST getRest(Context context) {
        if (rest == null) {
            String ENDPOINT = null;
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
            boolean use_default = sharedPref.getBoolean("use_default_url", false);

            Log.d("REST", String.valueOf(BuildConfig.DEBUG));
            if (BuildConfig.DEBUG) {
//                if (use_default) ENDPOINT = context.getResources().getString(R.string.default_url);
//                else
//                    ENDPOINT = sharedPref.getString("custom_url", "http://peritest.hopto.org/peridev/v1");
                ENDPOINT = "http://peritest.hopto.org/peridev/v1";
            } else {
                if (use_default) ENDPOINT = context.getResources().getString(R.string.default_url);
                else
                    ENDPOINT = sharedPref.getString("custom_url", "http://peritest.hopto.org/periprod/v1");
            }
            Gson gson = new GsonBuilder()
                    .excludeFieldsWithoutExposeAnnotation()
                    .registerTypeAdapter(Long.class, new MyLongTypeAdapter())
                    .registerTypeAdapter(Double.class, new MyDoubleTypeAdapter())
                    .registerTypeAdapter(Integer.class, new MyIntTypeAdapter())
                    .registerTypeAdapter(Float.class, new MyFloatTypeAdapter())
                    .create();

            RestAdapter restAdapter = new RestAdapter.Builder()
                    .setEndpoint(ENDPOINT).setLogLevel(LogLevel.FULL)
                    .setConverter(new GsonConverter(gson))
                    .build();

            rest = restAdapter.create(REST.class);
        }
        return rest;
    }


}

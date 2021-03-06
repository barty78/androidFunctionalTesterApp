package server;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pietrantuono.activities.SelectJobActivityHelper;
import com.pietrantuono.pericoach.newtestapp.BuildConfig;
import com.pietrantuono.pericoach.newtestapp.R;
import com.squareup.okhttp.OkHttpClient;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.preference.PreferenceManager;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RestAdapter.LogLevel;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.POST;
import server.pojos.DevicesList;
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

        @GET("/jobs/all/active")
        void getJobListAllActiveJobs(@Header("DeviceId") String DeviceId, Callback<List<Job>> callback);

        @GET("/firmware")
        void getFirmware(@Header("DeviceId") String DeviceId, @Header("FirmwareId") String FirmwareId, Callback<Firmware> callback);

        @GET("/tests")
        void getSequence(@Header("DeviceId") String DeviceId, @Header("JobNo") String JobNo, Callback<List<Test>> callback);

        @POST("/record")
        void postResults(@Header("DeviceId") String DeviceId, @Header("JobNo") String JobNo, @Body TestRecord record, Callback<Response> callback);

        @POST("/record")
        retrofit.client.Response postResultsSync(@Header("DeviceId") String DeviceId, @Header("JobNo") String JobNo, @Body TestRecord record);

        @GET("/devices")
        void getLastDevicesAsync(@Header("DeviceId") String DeviceId, @Header("LastId") String LastId, Callback<DevicesList> callback);

        @GET("/devices")
        DevicesList getLastDevicesSync(@Header("DeviceId") String DeviceId, @Header("LastId") String LastId);

        @GET("/devices")
        void getAllDevices(@Header("DeviceId") String DeviceId, Callback<DevicesList> callback);

        @POST("/testclasses")
        void postXMLTests(@Header("DeviceId") String DeviceId, @Body SelectJobActivityHelper.XMLTestsList xMLTestsList, Callback<Response> callback);

    }

    static KeyStore readKeyStore(Context context) {
        KeyStore ks = null;
        InputStream in = null;
        try {
            ks = KeyStore.getInstance("PKCS12");
            char[] password = "XXXX".toCharArray();
            in = context.getResources().openRawResource(R.raw.sql);
            ks.load(in, password);
        } catch (KeyStoreException | NoSuchAlgorithmException | IOException | CertificateException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {

                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return ks;
    }

    public static OkHttpClient getUnsafeOkHttpClient(Context context) {
        try {
            final TrustManager[] trustAllCerts = new TrustManager[] {
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

                        }

                        @Override
                        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {

                        }

                        @Override
                        public X509Certificate[] getAcceptedIssuers() {
                            return null;
                        }
                    }
            };

            KeyStore keyStore = readKeyStore(context);
            KeyManagerFactory kmf = KeyManagerFactory.getInstance("X509");
            kmf.init(keyStore, "admin".toCharArray());
            KeyManager[] keyManagers = kmf.getKeyManagers();

            final SSLContext sslContext = SSLContext.getInstance("SSL");

            sslContext.init(null, trustAllCerts, new SecureRandom());
                    final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
            OkHttpClient okHttpClient = new OkHttpClient();
            okHttpClient.setSslSocketFactory(sslSocketFactory);
            okHttpClient.setHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
            return okHttpClient;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

//    public static OkClient getOkClient () {
//        OkHttpClient client1 = new OkHttpClient();
//        client1 = getUnsafeOkHttpClient()
//    }

    public synchronized static REST getRest(Context context) {
        if (rest == null) {
            String ENDPOINT;
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
            boolean use_default = sharedPref.getBoolean("use_default_url", false);

            if (BuildConfig.DEBUG) {
//                if (use_default) ENDPOINT = context.getResources().getString(R.string.default_url);
//                else
//                    ENDPOINT = sharedPref.getString("custom_url", "http://peritest.hopto.org/peridev/v1");
                ENDPOINT = context.getResources().getString(R.string.default_dev_url);
            } else {
//                if (use_default) ENDPOINT = context.getResources().getString(R.string.default_prod_url);
                if (use_default) ENDPOINT = context.getResources().getString(R.string.default_productiontest_url);
                else
                    ENDPOINT = sharedPref.getString("custom_url", "http://peritest.hopto.org/periprod/v1");
            }

            Gson gson = new GsonBuilder()
                    .excludeFieldsWithoutExposeAnnotation()
                    .registerTypeAdapter(Long.class, new MyLongTypeAdapter())
                    .registerTypeAdapter(Double.class, new MyDoubleTypeAdapter())
                    .registerTypeAdapter(Integer.class, new MyIntTypeAdapter())
                    .registerTypeAdapter(Float.class, new MyFloatTypeAdapter())
                    //.registerTypeAdapter(Current.Units.class,new CurrentDeserializer())
                    .create();

            RestAdapter restAdapter = new RestAdapter.Builder()
                    .setEndpoint(ENDPOINT).setLogLevel(LogLevel.FULL)
//                    .setClient(okHttpClient)
                    .setConverter(new GsonConverter(gson))
                    .build();

            rest = restAdapter.create(REST.class);
        }
        return rest;
    }


}

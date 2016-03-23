package utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.cyanogenmod.updater.utils.MD5;
import com.pietrantuono.application.PeriCoachTestApplication;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.PowerManager;
import android.widget.Toast;
 public class DownloadTask extends AsyncTask<Void, Integer, String> {

    private final Activity context;
    private PowerManager.WakeLock mWakeLock;
    private final String url;
    private final String filename;
    private final ProgressDialog mProgressDialog;
    private final MyCallback callback;

    public DownloadTask(Activity context, String url, String filename) {
        this.context = context;
        this.filename=filename;
        this.url=url;
        this.callback= (MyCallback)context;
		mProgressDialog=MyDialogs.getDeterminateProgress(context, "Downloading firmware file", "Please wait");

        
    }

    @Override
    protected String doInBackground(Void... bar) {
        InputStream input = null;
        OutputStream output = null;
        HttpURLConnection connection = null;
        try {
            URL url = new URL(this.url);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            // expect HTTP 200 OK, so we don't mistakenly save error report
            // instead of the file
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return "Server returned HTTP " + connection.getResponseCode()
                        + " " + connection.getResponseMessage();
            }

            // this will be useful to display download percentage
            // might be -1: server did not report the length
            int fileLength = connection.getContentLength();

            // download the file
            input = connection.getInputStream();
            output = context.openFileOutput(filename,Context.MODE_PRIVATE );

            byte data[] = new byte[4096];
            long total = 0;
            int count;
            while ((count = input.read(data)) != -1) {
                // allow canceling with back button
                if (isCancelled()) {
                    input.close();
                    return null;
                }
                total += count;
                // publishing the progress....
                if (fileLength > 0) // only if total length is known
                    publishProgress((int) (total * 100 / fileLength));
                output.write(data, 0, count);
            }
        } catch (Exception e) {
            return e.toString();
        } finally {
            try {
                if (output != null)
                    output.close();
                if (input != null)
                    input.close();
            } catch (IOException ignored) {
            }

            if (connection != null)
                connection.disconnect();
        }
        return null;
    }
    
    
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Activity activity=(Activity)context;
		if(activity.isFinishing())return;
        mProgressDialog.show();
        // take CPU lock to prevent CPU from going off if the user 
        // presses the power button during download
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
             getClass().getName());
        mWakeLock.acquire();
        
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        super.onProgressUpdate(progress);       
        Activity activity=(Activity)context;
		if(activity.isFinishing())return;
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setMax(100);
        mProgressDialog.setProgress(progress[0]);
    }

    @Override
    protected void onPostExecute(String result) {
    	Activity activity=(Activity)context;
		if(activity.isFinishing())return;
        mWakeLock.release();
        mProgressDialog.dismiss();
        if (result != null)
        {
        	if(callback!=null)callback.onDownloadFileFailure();
            Toast.makeText(context,"Download error: "+result, Toast.LENGTH_LONG).show();
        }
        else {
        	PeriCoachTestApplication.setFirmware(filename);
        	ProgressDialog dialog=null;
            Toast.makeText(context,"File downloaded", Toast.LENGTH_SHORT).show();
            if(callback!=null){
            	dialog= new ProgressDialog((Activity)callback);
            	dialog.setTitle("Checking md5");
            	dialog.show();
            	}
            if(	MD5.checkMD5(PeriCoachTestApplication.getGetFirmware().getMd5sum(), PeriCoachTestApplication.getFirmware())){
                if(dialog!=null && dialog.isShowing())dialog.dismiss();
            	Toast.makeText(context,"OK md5 check", Toast.LENGTH_LONG).show();
            	if(callback!=null)callback.onDownloadFileSuccess();
            }
            else {
            	if(dialog!=null && dialog.isShowing())dialog.dismiss();
                Toast.makeText(context,"md5 check failed !!!", Toast.LENGTH_LONG).show();
            	if(callback!=null)callback.onDownloadFileFailure();;

            };
        }
    }
    
    public static interface MyCallback{
    	
    	public void onDownloadFileSuccess();
    	public void onDownloadFileFailure();
    	
    }
 }
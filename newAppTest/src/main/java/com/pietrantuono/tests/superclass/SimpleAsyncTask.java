package com.pietrantuono.tests.superclass;

import android.os.AsyncTask;

/**
 * Created by Maurizio Pietrantuono, maurizio.pietrantuono@gmail.com.
 */
public abstract class SimpleAsyncTask extends AsyncTask<Void,Void,Void> {

    public void executeParallel(){
        executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
}

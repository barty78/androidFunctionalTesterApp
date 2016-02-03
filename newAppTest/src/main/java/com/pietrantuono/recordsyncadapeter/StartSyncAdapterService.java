package com.pietrantuono.recordsyncadapeter;

import android.app.IntentService;
import android.content.Intent;

import com.pietrantuono.application.PeriCoachTestApplication;

/**
 * Created by mauriziopietrantuono on 15/12/15.
 */
public class StartSyncAdapterService extends IntentService {

    public StartSyncAdapterService() {
        super("StartSyncAdapterService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        PeriCoachTestApplication.forceSync();
    }
}

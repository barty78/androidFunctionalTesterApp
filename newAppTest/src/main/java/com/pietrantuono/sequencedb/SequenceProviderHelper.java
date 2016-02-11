package com.pietrantuono.sequencedb;

import android.content.Context;

/**
 * Created by Maurizio Pietrantuono, maurizio.pietrantuono@gmail.com.
 */
public class SequenceProviderHelper {
    public static long createNewRecord(Context context){
        return Integer.parseInt(context.getContentResolver().insert(SequenceProvider.RECORDS_CONTENT_URI, null).getPathSegments().get(1));
    }
}

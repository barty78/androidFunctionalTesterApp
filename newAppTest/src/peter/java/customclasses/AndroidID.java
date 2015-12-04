package customclasses;

import android.content.Context;
import android.provider.Settings;

/**
 * Created by mauriziopietrantuono on 04/12/15.
 */
public class AndroidID {
    public static String getID(Context context){
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }
}

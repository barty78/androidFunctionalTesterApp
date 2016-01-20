package customclasses;

import com.pietrantuono.pericoach.newtestapp.BuildConfig;

/**
 * Created by Maurizio Pietrantuono, maurizio.pietrantuono@gmail.com.
 */
public class DebugHelper {

    public static boolean isMaurizioDebug(){
        if(!BuildConfig.DEBUG)return false;
        return true;
    }
}


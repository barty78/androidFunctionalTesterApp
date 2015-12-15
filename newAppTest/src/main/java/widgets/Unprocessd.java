package widgets;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.preference.DialogPreference;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.activeandroid.Model;
import com.activeandroid.query.Select;
import com.pietrantuono.pericoach.newtestapp.R;

import java.util.List;

import server.pojos.records.TestRecord;
import server.utils.MyDatabaseUtils;

/**
 * Created by mauriziopietrantuono on 14/12/15.
 */
public class Unprocessd extends Preference {

    public Unprocessd(Context context) {
        super(context);
    }

    public Unprocessd(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public Unprocessd(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public Unprocessd(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
    @Override
    protected View onCreateView( ViewGroup parent )
    {
        LayoutInflater li = (LayoutInflater)getContext().getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        View v= li.inflate(R.layout.unprocessed, parent, false);
        TextView tv=(TextView)v.findViewById(R.id.text);
        List<Model> records = new Select().from(TestRecord.class).execute();
        if(records.size()<=0){tv.setText("0 records unprocessed");}
        else {tv.setText(""+records.size()+" record unprocessed");}
        return v;
    }
}

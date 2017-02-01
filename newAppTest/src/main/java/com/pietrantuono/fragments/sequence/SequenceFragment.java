package com.pietrantuono.fragments.sequence;

import android.animation.ValueAnimator;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pietrantuono.pericoach.newtestapp.R;
import com.pietrantuono.sequencedb.SequenceContracts;
import com.pietrantuono.sequencedb.SequenceProvider;


public class SequenceFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    @SuppressWarnings("unused")
    private static final String TAG = "SequenceFragment";
    private static final int SEQUENCE_LOADER_MANAGER = 1;
    private SequenceFragmentCallback mListener;
    private RecyclerView recyclerView;
    private TextView success_failure_text;
    private LinearLayout success_failure_container;
    private long recordId = -1;
    private final String RECORD_ID = "record_id";
    private SequenceCursorRecyclerAdapter mAdapter;

    public SequenceFragment() {
    }

    public static SequenceFragment newInstance() {
        return new SequenceFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (recordId >= 0) initLoader(recordId);
    }

    private void initLoader(long recordId) {
        Bundle bundle = new Bundle();
        bundle.putLong(RECORD_ID, recordId);
        getLoaderManager().initLoader(SEQUENCE_LOADER_MANAGER, bundle, SequenceFragment.this);
    }


    public void forceLoaderUpdate(long recordId){
        this.recordId=recordId;
        Bundle bundle = new Bundle();
        bundle.putLong(RECORD_ID, recordId);
        getLoaderManager().restartLoader(SEQUENCE_LOADER_MANAGER, bundle, SequenceFragment.this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.new_sequence_fragment, container, false);
        recyclerView = (RecyclerView) v.findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new SequenceCursorRecyclerAdapter(null,getActivity());
        success_failure_text = (TextView) v.findViewById(R.id.text);
        success_failure_container = (LinearLayout) v.findViewById(R.id.success_failure_container);
        recyclerView.setAdapter(mAdapter);
        setContainerVisible();
        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SequenceFragmentCallback) {
            mListener = (SequenceFragmentCallback) context;
            mListener.registerSequenceFragment(SequenceFragment.this);
        } else {
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener.unregisterSequenceFragment();
        mListener = null;
    }

    public void cleanUI() {
        mAdapter = new SequenceCursorRecyclerAdapter(null,getActivity());
        recyclerView.setAdapter(mAdapter);
    }

    public void removeOverallFailOrPass() {
        Handler handler = new Handler(getActivity().getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                success_failure_container.setVisibility(View.GONE);
            }
        });
    }


    public void setOverallFailOrPass(final boolean success, final String string) {
        final String barcode;
        if (string != null) {
            barcode = string;
        } else {
            barcode = "";
        }
        Handler handler = new Handler(getActivity().getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                success_failure_container.setVisibility(View.VISIBLE);
                success_failure_container.requestLayout();
                if (!success) {
                    success_failure_container.setBackgroundColor(Color.RED);
                    success_failure_text.setText(barcode + " FAILED!");
                } else {
                    success_failure_container.setBackgroundColor(Color.GREEN);
                    success_failure_text.setText(barcode + " PASSED");
                }
            }
        });
    }

    private void setContainerVisible() {
        if (true) return;
        success_failure_container.getLayoutParams().height = 1;
        success_failure_container.setVisibility(View.VISIBLE);
        ValueAnimator animation = ValueAnimator.ofInt(0, getActivity().getResources().getDimensionPixelOffset(R.dimen.succes_conteiner_height));
        animation.setDuration(1000);
        animation.start();
        success_failure_container.getLayoutParams().height = (int) animation.getAnimatedValue();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        long recordid = args.getLong(RECORD_ID);
        if(recordid<0)return null;
        String selection = SequenceContracts.Tests.TABLE_TESTS_FOREIGN_KEY_ID_OF_RECORD + " = " + recordid;
        String orderBy = SequenceContracts.Tests.TABLE_TESTS_TIME_INSERTED + " ASC";
        return new CursorLoader(getActivity(), SequenceProvider.TESTS_CONTENT_URI, null, selection, null, orderBy);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.changeCursor(data);
        if(mAdapter.getItemCount()>0)recyclerView.smoothScrollToPosition(mAdapter.getItemCount()-1);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.changeCursor(null);
    }

    public interface SequenceFragmentCallback {
        void registerSequenceFragment(SequenceFragment sequenceFragment);
        void unregisterSequenceFragment();
    }


}


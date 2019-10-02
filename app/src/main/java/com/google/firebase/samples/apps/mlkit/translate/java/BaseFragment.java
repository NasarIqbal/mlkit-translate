package com.google.firebase.samples.apps.mlkit.translate.java;

import android.util.Log;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public abstract class BaseFragment extends Fragment {

    private static final String TAG = "BaseFragment";

    protected void setUpRecyclerView(RecyclerView mRecyclerView) {
        try {
            mRecyclerView.setHasFixedSize(true);
            LinearLayoutManager mLayoutManager = new LinearLayoutManager(requireContext());
            mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            mRecyclerView.setLayoutManager(mLayoutManager);
            RecyclerView.Adapter mAdopter = onPrepareAdopter();
            if (mAdopter != null)
                mRecyclerView.setAdapter(mAdopter);
        } catch (Exception e) {
            Log.e(TAG, "setUpRecyclerView: ", e);
        }
    }

    protected abstract RecyclerView.Adapter onPrepareAdopter();

}

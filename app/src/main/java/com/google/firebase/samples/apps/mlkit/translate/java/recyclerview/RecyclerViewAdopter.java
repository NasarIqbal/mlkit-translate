package com.google.firebase.samples.apps.mlkit.translate.java.recyclerview;

import android.content.Context;
import android.content.Intent;
import android.util.Log;


import com.google.firebase.samples.apps.mlkit.translate.java.model.History;

import java.util.List;

public class RecyclerViewAdopter extends SingleLayoutAdopter {

    private static final String TAG = "RecyclerViewAdopter";

    private List<Object> mData;
    private Context mContext;
    //private List<Object> mDataFiltered;

    public RecyclerViewAdopter(Context mContext, int layoutId, List<Object> mData) {
        super(layoutId);
        this.mData = mData;
        this.mContext=mContext;
        //this.mDataFiltered = mData;
    }

    @Override
    protected Object getObjForPosition(int position) {
        return mData.get(position);
    }

    @Override
    protected void itemOnClick(Object view) {
//        History history = (History) view;
//        Intent sendIntent = new Intent();
//        sendIntent.setAction(Intent.ACTION_SEND);
//        sendIntent.putExtra(Intent.EXTRA_TEXT, "Source Text: "+history.getSource()+" Target Text: "+history.getTarget());
//        sendIntent.setType("text/plain");
//        Intent shareIntent = Intent.createChooser(sendIntent, null);
//        mContext.startActivity(shareIntent);
    }

    @Override
    public int getItemCount() {

        return mData.size();
    }

    public void setData(Object data) {
        if ((mData != null) && (!mData.contains(data))) {
            mData.add(data);
            //mDataFiltered.add(data);
            Log.e(TAG, "setData: call notifyDataSetChanged");
            notifyDataSetChanged();
        }

    }

    public void setListData(List<Object> data) {

        if ((mData != null)) {
            mData.clear();
            mData.addAll(data);
            Log.e(TAG, "setListData: with size " + data.size());
            notifyDataSetChanged();
        }

    }
}

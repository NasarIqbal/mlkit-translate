package com.google.firebase.samples.apps.mlkit.translate.java.recyclerview;


import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import androidx.databinding.library.baseAdapters.BR;
import androidx.recyclerview.widget.RecyclerView;


public class BaseViewHolder extends RecyclerView.ViewHolder {

    private final ViewDataBinding mBinding;

    public BaseViewHolder(@NonNull ViewDataBinding mBinding) {
        super(mBinding.getRoot());
        this.mBinding = mBinding;
    }

    public void bind(Object obj) {
        mBinding.setVariable(BR.obj, obj);
        mBinding.executePendingBindings();
    }

    public ViewDataBinding getmBinding() {
        return mBinding;
    }
}

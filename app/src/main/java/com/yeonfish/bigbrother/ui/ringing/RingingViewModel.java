package com.yeonfish.bigbrother.ui.ringing;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class RingingViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public RingingViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is ringing fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
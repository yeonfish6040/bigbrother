package com.yeonfish.bigbrother.ui.updatePos;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class UpdatePosViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public UpdatePosViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is position fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
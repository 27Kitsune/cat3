package com.example.cat3.ui.aaa;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AaaViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public AaaViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is aaa fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
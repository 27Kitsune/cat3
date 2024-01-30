package com.example.cat3.ui.Monitoring;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MonitoringViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public MonitoringViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Water Level");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
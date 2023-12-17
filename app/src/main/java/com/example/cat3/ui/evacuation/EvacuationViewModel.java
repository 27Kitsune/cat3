package com.example.cat3.ui.evacuation;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class EvacuationViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public EvacuationViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is slideshow fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
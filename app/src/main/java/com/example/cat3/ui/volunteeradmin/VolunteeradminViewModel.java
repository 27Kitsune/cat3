package com.example.cat3.ui.volunteeradmin;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class VolunteeradminViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public VolunteeradminViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is volunteer admin fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
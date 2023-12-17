package com.example.cat3.ui.volunteer;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class VolunteerViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public VolunteerViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is volunteer fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
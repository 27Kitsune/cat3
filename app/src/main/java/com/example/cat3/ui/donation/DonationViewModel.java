package com.example.cat3.ui.donation;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class DonationViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public DonationViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("User Donation Fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
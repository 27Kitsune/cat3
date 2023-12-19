package com.example.cat3.ui.UploadQR;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class UploadDonationViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public UploadDonationViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("ADMIN: This is Qr Upload fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}

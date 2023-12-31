package com.example.cat3.ui.UploadQR;

public class UploadQr {
    private String imageUrl;
    public UploadQr(){
        //Keep this constructor with no operation.
    }

    public UploadQr(String imageUrl) {

        this.imageUrl = imageUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
